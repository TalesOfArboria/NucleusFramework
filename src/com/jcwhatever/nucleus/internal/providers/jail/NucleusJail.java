/*
 * This file is part of NucleusFramework for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.jcwhatever.nucleus.internal.providers.jail;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.providers.jail.IJail;
import com.jcwhatever.nucleus.providers.jail.IJailSession;
import com.jcwhatever.nucleus.regions.Region;
import com.jcwhatever.nucleus.regions.options.LeaveRegionReason;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.coords.NamedLocation;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Rand;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.utils.TimeScale;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/*
 * 
 */
public class NucleusJail implements IJail {

    private final NucleusJailProvider _provider;
    private final Plugin _plugin;
    private final String _name;
    private final String _searchName;
    private final IDataNode _dataNode;
    private final Bounds _bounds;
    private final Map<String, NamedLocation> _jailLocations = new HashMap<>(10);

    private Location _releaseLocation;
    private boolean _isDisposed;

    NucleusJail(NucleusJailProvider provider, Plugin plugin, String name, IDataNode dataNode) {
        PreCon.notNull(provider);
        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(name);

        _provider = provider;
        _plugin = plugin;
        _name = name;
        _searchName = name.toLowerCase();
        _dataNode = dataNode;
        _bounds = new Bounds(plugin, name, dataNode.getNode("bounds"));
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getSearchName() {
        return _searchName;
    }

    @Nullable
    @Override
    public IJailSession imprison(Player player, int duration, TimeScale timeScale) {
        PreCon.greaterThanZero(duration);
        PreCon.notNull(timeScale);

        return imprison(player, new Date(System.currentTimeMillis() + (duration * timeScale.getTimeFactor())));
    }

    @Nullable
    @Override
    public IJailSession imprison(Player player, Date expires) {
        PreCon.notNull(player);

        checkDisposed();

        // teleport player to jail
        Location teleport = getRandomTeleport();
        if (teleport == null) {

            if (!_bounds.isDefined()) {
                NucMsg.debug(getPlugin(), "Cannot imprison player in jail '{0}' because its coordinates " +
                        "are undefined and no teleport location has been assigned.");
                return null;
            }

            teleport = _bounds.getCenter();
        }

        // register session
        IJailSession session = _provider.createSession(this, player.getUniqueId(), expires);

        if (session == null)
            return null;

        player.teleport(teleport);
        player.setGameMode(GameMode.SURVIVAL);

        return session;
    }

    @Override
    public boolean isPrisoner(Player player) {
        PreCon.notNull(player);

        IJailSession session = _provider.getSession(player.getUniqueId());
        return session != null && session.getJail().equals(this);
    }

    @Override
    public Bounds getRegion() {
        return _bounds;
    }

    @Override
    public boolean addTeleport(String name, Location teleport) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(teleport);
        PreCon.isValid(TextUtils.isValidName(name));

        return addTeleport(new NamedLocation(name, teleport));
    }

    @Override
    public boolean addTeleport(NamedLocation teleport) {
        PreCon.notNull(teleport);

        checkDisposed();

        // make sure the name is not already in use
        NamedLocation location = _jailLocations.get(teleport.getSearchName());
        if (location != null)
            return false;

        _jailLocations.put(teleport.getSearchName(), teleport);

        IDataNode teleportNode = _dataNode.getNode("teleport");
        teleportNode.set(teleport.getSearchName(), teleport);
        _dataNode.save();

        return true;
    }

    @Override
    public boolean removeTeleport(String name) {
        PreCon.notNullOrEmpty(name);

        checkDisposed();

        NamedLocation location = _jailLocations.remove(name.toLowerCase());
        if (location == null)
            return false;

        IDataNode teleportNode = _dataNode.getNode("teleport");
        teleportNode.set(name, null);
        _dataNode.save();

        return true;
    }

    @Nullable
    @Override
    public NamedLocation getRandomTeleport() {
        List<NamedLocation> locations = new ArrayList<>(_jailLocations.values());
        if (locations.isEmpty())
            return null;

        return Rand.get(locations);
    }

    @Nullable
    @Override
    public NamedLocation getTeleport(String name) {
        PreCon.notNullOrEmpty(name);

        return _jailLocations.get(name.toLowerCase());
    }

    @Override
    public Collection<NamedLocation> getTeleports() {
        return new ArrayList<>(_jailLocations.values());
    }

    @Nullable
    @Override
    public Location getReleaseLocation() {
        if (_releaseLocation == null && _bounds.getWorld() != null) {
            return _bounds.getWorld().getSpawnLocation();
        }

        return _releaseLocation != null
                ? _releaseLocation.clone()
                : null;
    }

    @Override
    public void setReleaseLocation(@Nullable Location location) {
        _releaseLocation = location;

        checkDisposed();

        _dataNode.set("release-location", location);
        _dataNode.save();
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {

        if (this.equals(_provider.getServerJail()))
            throw new IllegalStateException("Cannot dispose server jail.");

        _provider.removeJail(this);

        _dataNode.remove();
        _dataNode.save();

        _isDisposed = true;
    }

    NucleusJailProvider getProvider() {
        return _provider;
    }

    private void checkDisposed() {
        if (_isDisposed)
            throw new IllegalStateException("Cannot use a disposed jail.");
    }

    public class Bounds extends Region {

        /**
         * Constructor
         *
         * @param plugin
         * @param name
         * @param dataNode
         */
        public Bounds(Plugin plugin, String name, @Nullable IDataNode dataNode) {
            super(plugin, name, dataNode);
        }

        /**
         * Determine if the {@link #onPlayerLeave} method can be called.
         *
         * @param player  The player leaving the region.
         */
        @Override
        protected boolean canDoPlayerLeave(Player player, LeaveRegionReason reason) {
            PreCon.notNull(player);

            return reason != LeaveRegionReason.QUIT_SERVER && isPrisoner(player);
        }

        /**
         * Prevent imprisoned players from leaving the jail region.
         *
         * @param player  The player leaving the region.
         */
        @Override
        protected void onPlayerLeave (final Player player, LeaveRegionReason reason) {
            PreCon.notNull(player);

            Scheduler.runTaskLater(Nucleus.getPlugin(), 10, new Runnable() {
                @Override
                public void run() {

                    // prevent player from leaving jail
                    Location tpLocation = getRandomTeleport();

                    if (tpLocation == null)
                        tpLocation = getCenter();

                    player.teleport(tpLocation);
                }
            });

        }
    }
}
