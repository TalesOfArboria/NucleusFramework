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
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.internal.providers.InternalProviderInfo;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.providers.Provider;
import com.jcwhatever.nucleus.providers.jail.IJail;
import com.jcwhatever.nucleus.providers.jail.IJailProvider;
import com.jcwhatever.nucleus.providers.jail.IJailSession;
import com.jcwhatever.nucleus.providers.storage.DataStorage;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.DateUtils;
import com.jcwhatever.nucleus.utils.DateUtils.TimeRound;
import com.jcwhatever.nucleus.utils.MetaKey;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Rand;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Internal implementation of {@link IJailProvider}.
 */
public final class NucleusJailProvider extends Provider implements IJailProvider{

    public static final String NAME = "NucleusJails";

    @Localizable static final String _RELEASE_TIME = "Release in {0} minutes.";

    private static final MetaKey<Long> RELEASE_MESSAGE_META = new MetaKey<>(Long.class);

    private IDataNode _dataNode;
    private Map<UUID, IJailSession> _sessionMap = new HashMap<>(20);
    private Map<String, IJail> _jails = new HashMap<>(5);
    private Map<UUID, Location> _lateReleases = new HashMap<UUID, Location>(20);
    private Map<Plugin, IDataNode> _dataNodes = new WeakHashMap<>(10);
    private Warden _warden = new Warden();
    private NucleusJail _serverJail;

    public NucleusJailProvider() {
        setInfo(new InternalProviderInfo(this.getClass(),
                NAME, "Default jail provider."));
    }

    @Override
    public void onEnable() {

        _dataNode = getDataNode();

        _serverJail = new NucleusJail(this, Nucleus.getPlugin(), "Server", _dataNode.getNode("Server"));

        // check for prisoner release.
        Scheduler.runTaskRepeat(Nucleus.getPlugin(), Rand.getInt(1, 25), 25, _warden);

        Bukkit.getPluginManager().registerEvents(new BukkitEventListener(), Nucleus.getPlugin());


        // load late releases
        IDataNode lateNode = _dataNode.getNode("late-release");

        for (IDataNode dataNode : lateNode) {
            UUID playerId = TextUtils.parseUUID(dataNode.getName());
            if (playerId == null)
                continue;

            Location release = dataNode.getLocation("");
            if (release == null)
                continue;

            _lateReleases.put(playerId, release);
        }
    }

    @Override
    public void onDisable() {

        Collection<IJailSession> sessions = getSessions();

        for (IJailSession session : sessions) {
            session.release();
        }
    }

    @Override
    public NucleusJail getServerJail() {
        return _serverJail;
    }

    @Nullable
    @Override
    public NucleusJail createJail(Plugin plugin, String name) {
        PreCon.notNull(plugin);
        PreCon.validNodeName(name);

        String lookup = getJailLookup(plugin, name);

        IJail current = _jails.get(lookup);
        if (current != null)
            return null;

        IDataNode dataNode = _dataNodes.get(plugin);
        if (dataNode == null) {
            dataNode = DataStorage.get(plugin, getDataPath("jails"));
            dataNode.load();

            _dataNodes.put(plugin, dataNode);
        }

        NucleusJail jail = new NucleusJail(this, plugin, name, dataNode.getNode(name));
        _jails.put(lookup, jail);

        return jail;
    }

    @Nullable
    @Override
    public IJail getJail(Plugin plugin, String name) {
        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(name);

        return _jails.get(getJailLookup(plugin, name));
    }

    @Override
    public Collection<IJail> getJails() {
        return new ArrayList<>(_jails.values());
    }

    @Nullable
    @Override
    public IJailSession getSession(UUID playerId) {
        PreCon.notNull(playerId);

        return _sessionMap.get(playerId);
    }

    @Override
    public Collection<IJailSession> getSessions() {
        return new ArrayList<>(_sessionMap.values());
    }

    @Override
    public boolean isPrisoner(UUID playerId) {
        PreCon.notNull(playerId);

        return _sessionMap.containsKey(playerId);
    }

    @Override
    public boolean release(UUID playerId) {
        PreCon.notNull(playerId);

        IJailSession session = getSession(playerId);
        if (session != null) {
            session.dispose();
            _warden.run(true, true);
            return true;
        }

        return false;
    }

    NucleusJailSession createSession(NucleusJail jail, UUID playerId, Date expires) {
        NucleusJailSession session = new NucleusJailSession(jail, playerId, expires);
        _sessionMap.put(playerId, session);

        return session;
    }

    void removeJail(NucleusJail jail) {
        if (jail == _serverJail)
            throw new AssertionError("Server jail cannot be disposed.");

        _jails.remove(getJailLookup(jail.getPlugin(), jail.getName()));

        Collection<IJailSession> sessions = getSessions();

        for (IJailSession session : sessions) {
            if (session.equals(jail))
                session.dispose();
        }
    }

    boolean isLateRelease(UUID playerId) {
        return _lateReleases.containsKey(playerId);
    }

    // register a late release so the next time a player re-spawns or logs
    // in they will be teleported to the release location.
    private void registerLateRelease(UUID playerId, Location releaseLocation) {
        PreCon.notNull(playerId);
        PreCon.notNull(releaseLocation);

        releaseLocation = releaseLocation.clone();

        _lateReleases.put(playerId, releaseLocation);

        IDataNode node = _dataNode.getNode("late-release");
        node.set(playerId.toString(), releaseLocation);
        node.save();
    }

    // remove a late release
    @Nullable
    private Location unregisterLateRelease(UUID playerId) {
        PreCon.notNull(playerId);

        Location result = _lateReleases.remove(playerId);

        if (result != null) {
            IDataNode node = _dataNode.getNode("late-release");
            node.remove(playerId.toString());
            node.save();
        }

        return result;
    }

    private String getJailLookup(Plugin plugin, String name) {
        return plugin.getName() + ':' + name.toLowerCase();
    }

    /**
     * Scheduled task responsible for determining when
     * to release players from prison.
     */
    class Warden implements Runnable {

        boolean hasProcessedLateReleases;

        @Override
        public void run () {
            run(false, false);
        }

        public void run(boolean silent, boolean doLateReleases) {
            if (_sessionMap.isEmpty())
                return;

            if (!hasProcessedLateReleases) {
                doLateReleases = true;
                hasProcessedLateReleases = true;
            }

            List<IJailSession> jailSessions = new ArrayList<>(_sessionMap.values());

            Date now = new Date();

            for (IJailSession session : jailSessions) {

                boolean isLateRelease = _lateReleases.containsKey(session.getPlayerId());
                if (isLateRelease && !doLateReleases)
                    continue;

                if (isLateRelease || session.isExpired()) {

                    Player p = PlayerUtils.getPlayer(session.getPlayerId());
                    if (p == null) {

                        // register player to be released at next login
                        Location releaseLocation = getReleaseLocation(session);
                        if (releaseLocation == null) {
                            NucMsg.warning("Failed to find a jail release location");
                            continue;
                        }

                        registerLateRelease(session.getPlayerId(), releaseLocation);
                        continue;
                    }

                    _sessionMap.remove(session.getPlayerId());

                    Location releaseLoc = unregisterLateRelease(session.getPlayerId());
                    if (releaseLoc == null)
                        releaseLoc = getReleaseLocation(session);

                    if (!session.isReleased())
                        session.release();

                    if (releaseLoc != null)
                        p.teleport(releaseLoc);
                }
                else if (!silent) {

                    long releaseMinutes = DateUtils
                            .getDeltaMinutes(now, session.getExpiration(), TimeRound.ROUND_UP);

                    Long lastMessageMin = session.getMeta().get(RELEASE_MESSAGE_META);
                    if (lastMessageMin == null ||
                            (lastMessageMin != releaseMinutes &&
                                    (releaseMinutes <= 5 || releaseMinutes % 10 == 0))) {

                        Player p = PlayerUtils.getPlayer(session.getPlayerId());

                        if (p != null) {
                            NucMsg.tellAnon(p, NucLang.get(_RELEASE_TIME, releaseMinutes));
                        }

                        session.getMeta().set(RELEASE_MESSAGE_META, releaseMinutes);
                    }
                }
            }
        }

        @Nullable
        private Location getReleaseLocation(IJailSession session) {

            Location releaseLoc = session.getReleaseLocation();

            if (releaseLoc == null) {
                World world = session.getJail().getRegion().getWorld();

                if (world != null)
                    releaseLoc = world.getSpawnLocation();
            }

            return releaseLoc;
        }
    }

    final class BukkitEventListener implements Listener {

        @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
        private void onCommandPreprocess(PlayerCommandPreprocessEvent event) {

            // prevent prisoners from using commands
            if (isPrisoner(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
            }
        }

        @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
        private void onPlayerInteract(PlayerInteractEvent event) {

            // prevent prisoners from interacting
            if (isPrisoner(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        private void onPlayerJoin(final PlayerJoinEvent event) {

            // make sure player that is logging in is released if no longer a prisoner
            if (isLateRelease(event.getPlayer().getUniqueId())) {
                Scheduler.runTaskLater(Nucleus.getPlugin(), 5, new Runnable() {
                    @Override
                    public void run() {
                        release(event.getPlayer().getUniqueId());
                    }
                });
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        private void onPlayerRespawn(PlayerRespawnEvent event) {

            UUID playerId = event.getPlayer().getUniqueId();

            // release prisoner
            if (isLateRelease(playerId)) {
                release(playerId);
            }
            else if (isPrisoner(playerId)) {

                // send prisoner back to jail
                IJailSession session = getSession(playerId);
                if (session != null) {
                    Location location = session.getJail().getRandomTeleport();
                    if (location != null) {
                        event.setRespawnLocation(location);
                    }
                }
            }
        }
    }
}
