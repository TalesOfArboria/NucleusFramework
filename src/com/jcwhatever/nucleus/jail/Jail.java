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


package com.jcwhatever.nucleus.jail;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.mixins.INamed;
import com.jcwhatever.nucleus.mixins.INamedLocation;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.mixins.implemented.NamedLocation;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Rand;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * A player jail.
 */
public class Jail implements IPluginOwned, INamed, IDisposable {

    private final Plugin _plugin;
    private String _name;
    private IDataNode _dataNode;
    private JailBounds _bounds;
    private Map<String, INamedLocation> _jailLocations = new HashMap<>(10);
    private Location _releaseLocation;

    /**
     * Constructor.
     *
     * @param plugin    The owning plugin.
     * @param name      The name of the jail.
     * @param dataNode  The jails data node.
     */
    public Jail(Plugin plugin, String name, IDataNode dataNode) {
        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(dataNode);

        _plugin = plugin;
        _name = name;
        _dataNode = dataNode;
        _bounds = new JailBounds(_plugin, this, _name, _dataNode.getNode("bounds"));

        load();

        Nucleus.getJailManager().registerJail(this);
    }

    /**
     * Get the owning plugin.
     */
    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the name of the jail.
     */
    @Override
    public String getName() {
        return _name;
    }

    /**
     * Imprison a player in the jail.
     *
     * @param p        The player to imprison.
     * @param minutes  The number of minutes to imprison the player.
     *
     * @return Null if failed to imprison player.
     */
    @Nullable
    public JailSession imprison(Player p, int minutes) {
        PreCon.notNull(p);

        // teleport player to jail
        Location teleport = getRandomTeleport();
        if (teleport == null) {

            if (!_bounds.isDefined())
                return null;

            teleport = _bounds.getCenter();
        }

        // register session
        JailSession session = Nucleus.getJailManager().registerJailSession(
                this, p.getUniqueId(), minutes);

        if (session == null)
            return null;

        p.teleport(teleport);
        p.setGameMode(GameMode.SURVIVAL);

        return session;
    }

    /**
     * Determine if a player is a prisoner of the jail.
     *
     * @param p  The player to check.
     */
    public boolean isPrisoner(Player p) {
        JailSession session = Nucleus.getJailManager().getSession(p.getUniqueId());
        return !(session == null || session.isReleased()) &&
                session.getJail() == this;
    }

    /**
     * Get the bounding region of the jail.
     */
    public JailBounds getJailBounds() {
        return _bounds;
    }

    /**
     * Add a location the player can be teleported to when imprisoned.
     *
     * @param name      The name of the location.
     * @param teleport  The teleport location.
     *
     * @return  True if the new teleport was added. Will fail if the name is in use.
     */
    public boolean addTeleport(String name, Location teleport) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(teleport);
        PreCon.isValid(TextUtils.isValidName(name));

        // make sure the name is not already in use
        INamedLocation location = _jailLocations.get(name.toLowerCase());
        if (location != null)
            return false;

        location = new NamedLocation(name, teleport);
        _jailLocations.put(name.toLowerCase(), location);

        IDataNode teleportNode = _dataNode.getNode("teleport");
        teleportNode.set(name, teleport);
        _dataNode.saveAsync(null);

        return true;
    }

    /**
     * Remove a jail teleport location.
     *
     * @param name  The name of the location.
     *
     * @return  True if found and removed.
     */
    public boolean removeTeleport(String name) {
        PreCon.notNullOrEmpty(name);

        INamedLocation location = _jailLocations.remove(name.toLowerCase());
        if (location == null)
            return false;

        IDataNode teleportNode = _dataNode.getNode("teleport");
        teleportNode.set(name, null);
        _dataNode.saveAsync(null);

        return true;
    }

    /**
     * Get a random jail teleport location.
     */
    @Nullable
    public Location getRandomTeleport() {

        List<INamedLocation> locations = new ArrayList<>(_jailLocations.values());
        if (locations.isEmpty())
            return null;

        return Rand.get(locations).getLocation();
    }

    /**
     * Get a jail teleport location by name.
     *
     * @param name  The name of the location.
     */
    @Nullable
    public INamedLocation getTeleport(String name) {
        PreCon.notNullOrEmpty(name);

        return _jailLocations.get(name.toLowerCase());
    }

    /**
     * Get all jail teleport locations.
     */
    public List<INamedLocation> getTeleports() {
        return new ArrayList<>(_jailLocations.values());
    }

    /**
     * Get the location a player is teleported to when
     * released.
     */
    @Nullable
    public Location getReleaseLocation() {

        if (_releaseLocation == null && _bounds.getWorld() != null) {
            return _bounds.getWorld().getSpawnLocation();
        }

        return _releaseLocation != null
                ? _releaseLocation
                : null;
    }

    /**
     * Set the location a player is teleported to when
     * released.
     *
     * @param location  The release location.
     */
    public void setReleaseLocation(@Nullable Location location) {

        _releaseLocation = location;

        _dataNode.set("release-location", location);
        _dataNode.saveAsync(null);
    }

    @Override
    public boolean isDisposed() {
        return false;
    }

    @Override
    public void dispose() {
        Nucleus.getJailManager().unregisterJail(this);
    }

    // load settings from data node
    protected void load() {

        _jailLocations.clear();

        _releaseLocation = _dataNode.getLocation("release-location");

        // Load jail teleport locations
        IDataNode teleportNode = _dataNode.getNode("teleport");

        for(IDataNode node : teleportNode) {

            String teleportName = node.getName();

            Location location = teleportNode.getLocation(teleportName);
            if (location == null)
                continue;

            _jailLocations.put(teleportName.toLowerCase(), new NamedLocation(teleportName, location));
        }
    }
}
