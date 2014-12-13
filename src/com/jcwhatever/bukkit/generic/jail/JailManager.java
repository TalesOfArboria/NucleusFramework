/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.jail;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.internal.Msg;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.mixins.INamedLocation;
import com.jcwhatever.bukkit.generic.mixins.implemented.NamedLocation;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.DateUtils;
import com.jcwhatever.bukkit.generic.utils.DateUtils.TimeRound;
import com.jcwhatever.bukkit.generic.utils.PlayerUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.Rand;
import com.jcwhatever.bukkit.generic.utils.Scheduler;
import com.jcwhatever.bukkit.generic.utils.Utils;
import com.jcwhatever.bukkit.generic.utils.text.TextUtils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Manages a player jail
 */
public class JailManager {

    @Localizable static final String _RELEASE_TIME = "Release in {0} minutes.";

    private Plugin _plugin;
    private String _name;
    private IDataNode _dataNode;
    private JailBounds _bounds;
    private Map<String, INamedLocation> _jailLocations = new HashMap<>(10);
    private Location _releaseLocation;
    private Map<UUID, JailSession> _sessionMap = new HashMap<UUID, JailSession>(20);
    private Map<UUID, Location> _lateReleases = new HashMap<UUID, Location>(20);
    Warden _warden = new Warden();

    /**
     * Constructor.
     *
     * @param plugin    The owning plugin.
     * @param name      The name of the jail.
     * @param dataNode  The jails data node.
     */
    public JailManager(Plugin plugin, String name, IDataNode dataNode) {
        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(dataNode);

        _plugin = plugin;
        _name = name;
        _dataNode = dataNode;
        _bounds = new JailBounds(_plugin, this, _name, _dataNode.getNode("bounds"));

        loadSettings();

        // check for prisoner release every 1 minute.
        Scheduler.runTaskRepeat(GenericsLib.getPlugin(),  20, 1200, _warden);

        BukkitEventListener _eventListener = new BukkitEventListener();
        Bukkit.getPluginManager().registerEvents(_eventListener, plugin);
    }

    /**
     * Get the owning plugin.
     */
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the name of the jail.
     */
    public String getName() {
        return _name;
    }

    /**
     * Imprison a player in the jail.
     *
     * @param p        The player to imprison.
     * @param minutes  The number of minutes to imprison the player.
     */
    @Nullable
    public JailSession imprison(Player p, int minutes) {
        PreCon.notNull(p);

        // can't teleport to jail if there is none.
        if (!_bounds.isDefined())
            return null;

        // get release time
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minutes);
        Date expires = calendar.getTime();

        // create session
        JailSession jailSession = new JailSession(this, p.getUniqueId(), expires);

        IDataNode sessions = _dataNode.getNode("sessions");
        sessions.set(p.getUniqueId().toString(), expires.getTime());
        sessions.saveAsync(null);

        // register session
        _sessionMap.put(p.getUniqueId(), jailSession);

        // teleport player to jail
        Location teleport = getRandomTeleport();
        if (teleport == null) {
            teleport = _bounds.getCenter();
        }

        p.teleport(teleport);
        p.setGameMode(GameMode.SURVIVAL);

        return jailSession;
    }

    /**
     * Determine if a player is a prisoner of the jail.
     *
     * @param p  The player to check.
     */
    public boolean isPrisoner(Player p) {
        JailSession session = _sessionMap.get(p.getUniqueId());
        if (session == null || session.isReleased())
            return false;

        return true;
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

    /**
     * Get a players jail session.
     *
     * @param playerId  The id of the player.
     */
    @Nullable
    public JailSession getJailSession(UUID playerId) {
        return _sessionMap.get(playerId);
    }

    // register a late release so the next time a player re-spawns or logs
    // in they will be teleported to the release location.
    private void registerLateRelease(UUID playerId, Location releaseLocation) {
        PreCon.notNull(playerId);
        PreCon.notNull(releaseLocation);

        _lateReleases.put(playerId, releaseLocation);

        IDataNode node = _dataNode.getNode("late-release");
        node.set(playerId.toString(), releaseLocation);
        node.saveAsync(null);
    }

    // load settings from data node
    private void loadSettings() {

        _releaseLocation = _dataNode.getLocation("release-location");

        // load late releases
        IDataNode lateNode = _dataNode.getNode("late-release");
        Set<String> rawIds = lateNode.getSubNodeNames();

        for (String rawId : rawIds) {
            UUID playerId = Utils.getId(rawId);
            if (playerId == null)
                continue;

            Location release = lateNode.getLocation(rawId);
            if (release == null)
                continue;

            _lateReleases.put(playerId, release);
        }

        // Load jail teleport locations
        IDataNode teleportNode = _dataNode.getNode("teleport");
        Set<String> teleportNames = teleportNode.getSubNodeNames();

        for(String teleportName : teleportNames) {

            Location location = teleportNode.getLocation(teleportName);
            if (location == null)
                continue;

            _jailLocations.put(teleportName.toLowerCase(), new NamedLocation(teleportName, location));
        }

        // Load jail sessions
        IDataNode sessions = _dataNode.getNode("sessions");
        Set<String> rawSessionIds = sessions.getSubNodeNames();

        for (String rawId : rawSessionIds) {

            UUID playerId = Utils.getId(rawId);
            if (playerId == null)
                continue;

            long expireTime = sessions.getLong(rawId, 0);

            JailSession jailSession = new JailSession(this, playerId, new Date(expireTime));

            _sessionMap.put(playerId, jailSession);
        }
    }


    /**
     * Scheduled task responsible for determining when
     * to release players from prison.
     */
    class Warden implements Runnable {

        @Override
        public void run () {
            run(false);
        }


        public void run(boolean silent) {
            if (_sessionMap.isEmpty())
                return;

            List<JailSession> jailSessions = new ArrayList<JailSession>(_sessionMap.values());

            Date now = new Date();

            for (JailSession session : jailSessions) {
                if (session.isReleased() || session.isExpired()) {
                    _sessionMap.remove(session.getPlayerId());
                    session.release(false);

                    Location releaseLoc = session.getReleaseLocation();
                    if (releaseLoc == null)
                        throw new AssertionError();

                    Player p = PlayerUtils.getPlayer(session.getPlayerId());
                    if (p != null) {
                        p.teleport(releaseLoc);
                    }
                    else {
                        // register player to be released at next login
                        registerLateRelease(session.getPlayerId(), releaseLoc);
                    }
                }
                else if (!silent) {

                    long releaseMinutes = DateUtils.getDeltaMinutes(now, session.getExpiration(), TimeRound.ROUND_UP);

                    if (releaseMinutes <= 5 || releaseMinutes % 10 == 0) {
                        Player p = PlayerUtils.getPlayer(session.getPlayerId());

                        if (p != null) {
                            Msg.tellAnon(p, Lang.get(_RELEASE_TIME, releaseMinutes));
                        }
                    }
                }
            }
        }
    }

    // event listener
    private class BukkitEventListener implements Listener {

        @EventHandler
        private void onCommandPreprocess(PlayerCommandPreprocessEvent event) {

            if (isPrisoner(event.getPlayer())) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        private void onPlayerInteract(PlayerInteractEvent event) {

            if (isPrisoner(event.getPlayer())) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        private void onPlayerJoin(PlayerJoinEvent event) {

            Location spawnLocation = _lateReleases.remove(event.getPlayer().getUniqueId());

            if (spawnLocation != null) {
                event.getPlayer().teleport(spawnLocation);
            }
        }


        @EventHandler(priority=EventPriority.LOW)
        private void onPlayerRespawn(PlayerRespawnEvent event) {

            // release prisoner
            Location spawnLocation = _lateReleases.remove(event.getPlayer().getUniqueId());
            if (spawnLocation != null) {
                event.getPlayer().teleport(spawnLocation);
            }

            // re-spawn imprisoned players in jail
            if (_bounds.isDefined()) {

                JailSession session = _sessionMap.get(event.getPlayer().getUniqueId());
                if (session != null && !session.isExpired()) {

                    Location loc = getRandomTeleport();
                    if (loc == null)
                        loc = _bounds.getCenter();

                    if (loc == null)
                        throw new AssertionError();

                    event.setRespawnLocation(loc);
                }
            }
        }

    }
}
