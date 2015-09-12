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

package com.jcwhatever.nucleus.internal.regions;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.players.PlayerMap;
import com.jcwhatever.nucleus.internal.regions.PlayerLocationCache.CachedLocation;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.providers.npc.Npcs;
import com.jcwhatever.nucleus.regions.IRegion;
import com.jcwhatever.nucleus.regions.IRegionEventListener;
import com.jcwhatever.nucleus.regions.options.LeaveRegionReason;
import com.jcwhatever.nucleus.regions.options.RegionEventPriority.PriorityType;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.utils.performance.pool.IPoolElementFactory;
import com.jcwhatever.nucleus.utils.performance.pool.SimpleCheckoutPool.CheckedOutElements;
import com.jcwhatever.nucleus.utils.performance.pool.SimplePool;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Watches and tracks players for the purpose of detecting
 * entry and exit from event listening regions.
 */
public final class InternalPlayerWatcher {

    private final InternalRegionManager _manager;

    // cached regions the player was detected in during last player watcher cycle.
    private final PlayerMap<EventOrderedRegions<IRegion>> _playerRegionCache;

    // locations the player was detected in between player watcher cycles.
    private final PlayerMap<PlayerLocationCache> _playerLocationCache;

    // async player watcher
    private final PlayerWatcherAsync _watcherAsync = new PlayerWatcherAsync();

    // sync region event caller
    private final EventCaller _eventCaller = new EventCaller();

    // IDs of players that have joined the server within a region and have not yet moved.
    private Set<UUID> _joined = new HashSet<>(10);

    // pool of player location caches
    private final SimplePool<PlayerLocationCache> _pools;

    private volatile boolean _isAsyncWatcherRunning;

    /**
     * Constructor.
     *
     * @param manager  The parent manager.
     */
    InternalPlayerWatcher(InternalRegionManager manager) {

        _manager = manager;
        _playerRegionCache = new PlayerMap<>(Nucleus.getPlugin());
        _playerLocationCache = new PlayerMap<>(Nucleus.getPlugin());
        _pools = new SimplePool<PlayerLocationCache>(100,
                new IPoolElementFactory<PlayerLocationCache>() {
                    @Override
                    public PlayerLocationCache create() {
                        return new PlayerLocationCache();
                    }
                });

        Scheduler.runTaskRepeat(Nucleus.getPlugin(), 1, 1, new QueueFiller());
        Scheduler.runTaskRepeatAsync(Nucleus.getPlugin(), 1, 1, _watcherAsync);
    }

    /**
     * Add a location that the player has moved to so it can be
     * cached and processed by the player watcher the next time it
     * runs.
     *
     * <p>The players current location is used.</p>
     *
     * @param player  The player.
     * @param reason  The reason that will be used if the player enters a region.
     */
    public void updatePlayerLocation(Player player, RegionEventReason reason) {
        PreCon.notNull(player);
        PreCon.notNull(reason);

        if (player.isDead() && reason != RegionEventReason.RESPAWN)
            return;

        // ignore NPC's
        if (Npcs.isNpc(player))
            return;

        if (!_manager.getListenerWorlds().contains(player.getWorld()))
            return;

        PlayerLocationCache locations = getPlayerLocations(player.getUniqueId());
        player.getLocation(locations.add(reason));
    }

    /**
     * Add a location that the player has moved to so it can be
     * cached and processed by the player watcher the next time it
     * runs.
     *
     * @param player    The player.
     * @param location  The location to add.
     * @param reason    The reason that will be used if the player enters a region.
     */
    public void updatePlayerLocation(Player player, Location location, RegionEventReason reason) {
        PreCon.notNull(player);
        PreCon.notNull(reason);

        if (player.isDead() && reason != RegionEventReason.RESPAWN)
            return;

        // ignore NPC's
        if (Npcs.isNpc(player))
            return;

        if (!_manager.getListenerWorlds().contains(player.getWorld()))
            return;

        PlayerLocationCache locations = getPlayerLocations(player.getUniqueId());
        LocationUtils.copy(location, locations.add(reason));
    }

    /**
     * Update player location when the player does not have a location.
     *
     * <p>Declares the player as leaving all current regions.</p>
     *
     * @param player  The player.
     * @param reason  The reason the player is leaving the regions.
     */
    public void updatePlayerLocation(Player player, LeaveRegionReason reason) {
        PreCon.notNull(player);
        PreCon.notNull(reason);

        // ignore NPC's
        if (Npcs.isNpc(player))
            return;

        UUID playerId = player.getUniqueId();


        EventOrderedRegions<IRegion> regions = forgetPlayer(playerId);
        if (regions == null)
            return;

        synchronized (this) {
            Iterator<IRegion> iterator = regions.iterator(PriorityType.LEAVE);
            while (iterator.hasNext()) {
                IRegion region = iterator.next();

                if (region.isEventListener())
                    region.getEventListener().onPlayerLeave(player, reason);
            }
        }

        if (reason == LeaveRegionReason.QUIT_SERVER) {

            synchronized (this) {
                // re-pool location cache
                PlayerLocationCache cache = _playerLocationCache.remove(player.getUniqueId());
                if (cache != null) {
                    _pools.recycle(cache);
                }
            }
        }
        else {
            clearPlayerLocations(player.getUniqueId());
        }
    }

    /**
     * Get the cached movement locations of a player that have not been processed
     * by the {@link InternalPlayerWatcher} yet.
     */
    PlayerLocationCache getPlayerLocations(UUID playerId) {

        PlayerLocationCache locations = _playerLocationCache.get(playerId);
        if (locations == null) {
            locations = _pools.retrieve();
            assert locations != null;

            locations.setOwner(playerId);
            _playerLocationCache.put(playerId, locations);
        }

        return locations;
    }

    /**
     * Get the regions a player is currently in.
     *
     * <p>The {@link InternalPlayerWatcher} should be synchronized when using the returned
     * {@link EventOrderedRegions}.</p>
     *
     * @param playerId  The ID of the player to check.
     */
    @Nullable
    EventOrderedRegions<IRegion> getCurrentRegions(UUID playerId) {
        synchronized (this) {
            return _playerRegionCache.get(playerId);
        }
    }

    /**
     * Remove the regions a player is currently in from the cache.
     *
     * @param playerId  The ID of the player to check.
     *
     * @return  The removed regions.
     */
    @Nullable
    EventOrderedRegions<IRegion> forgetPlayer(UUID playerId) {
        synchronized (this) {
            return _playerRegionCache.remove(playerId);
        }
    }

    /*
     * Clear cached movement locations of a player
     */
    private void clearPlayerLocations(UUID playerId) {
        PlayerLocationCache locations = getPlayerLocations(playerId);
        locations.getCheckedOut().recycle();
    }

    /*
     * Repeating task that pre-processes and filters player movement data before
     * adding it to the async player watchers queue.
     */
    private final class QueueFiller implements Runnable {

        @Override
        public void run() {

            // do not run while async watcher is running
            if (_isAsyncWatcherRunning)
                return;

            // call queued region events
            if (!_eventCaller.queue.isEmpty()) {
                _eventCaller.run();
            }

            // get worlds where listener regions exist
            List<World> worlds = new ArrayList<World>(_manager.getListenerWorlds().getElements());

            // get players in worlds with regions
            for (World world : worlds) {

                if (world == null)
                    continue;

                // get players currently in world
                List<Player> players = world.getPlayers();
                if (players == null || players.isEmpty())
                    continue;

                // process and add players to wp list
                for (Player player : players) {

                    // do not process NPC's
                    if (Npcs.isNpc(player))
                        continue;

                    // get locations that the player was recorded in between watcher cycles
                    PlayerLocationCache locations = getPlayerLocations(player.getUniqueId());

                    synchronized (InternalPlayerWatcher.this) {
                        // skip if there are no locations recorded
                        if (locations.getCheckedOut().size() == 0)
                            continue;

                        WorldPlayer worldPlayer = new WorldPlayer(player, locations.getCheckedOut());
                        _watcherAsync.queue.add(worldPlayer);
                    }
                }
            }

            // end if there are no players to process
            if (_watcherAsync.queue.isEmpty())
                return;

            // let the async watcher run
            _isAsyncWatcherRunning = true;
        }
    }

    /*
     * Async portion of the player watcher
     */
    private class PlayerWatcherAsync implements Runnable {

        final Queue<WorldPlayer> queue = new ArrayDeque<>(Bukkit.getMaxPlayers());
        // Temporary queue to hold regions a player has possibly entered.
        // Because "enter" is processed before "leave", the new "enter" event
        // will not be processed unless regions in question are processed after
        // the "leave" event has removed the region from the players region cache.
        // The enter queue is used to hold regions that need to be checked again
        // after "leave" events have been processed.
        final Queue<IRegion> enter = new ArrayDeque<>(20);

        @Override
        public void run() {

            if (!_isAsyncWatcherRunning)
                return;

            // iterate players
            while (!queue.isEmpty()) {

                WorldPlayer worldPlayer = queue.remove();

                UUID playerId = worldPlayer.player.getUniqueId();

                // get regions the player is in (cached from previous check)
                EventOrderedRegions<IRegion> cachedRegions;

                synchronized (InternalPlayerWatcher.this) {
                    cachedRegions = _playerRegionCache.get(playerId);
                }

                if (cachedRegions == null) {
                    cachedRegions = new EventOrderedRegions<>(7);

                    synchronized (InternalPlayerWatcher.this) {
                        _playerRegionCache.put(playerId, cachedRegions);
                    }
                }

                // iterate locations the player has been since the last check
                for (CachedLocation location : worldPlayer.locations) {

                    if (location.getReason() == RegionEventReason.JOIN_SERVER) {
                        // do not notify regions until player has moved.
                        // Allows time for player to load resource packs.
                        _joined.add(playerId);
                        continue;
                    }

                    boolean isJoining = _joined.contains(playerId);

                    if (isJoining && location.getReason() != RegionEventReason.MOVE) {
                        // ignore all other reasons until joined player moves
                        continue;
                    }

                    // get regions the player location is in
                    List<IRegion> locationRegions = _manager.getListenerRegions(location, PriorityType.ENTER);

                    RegionEventReason reason = null;

                    // check ENTER regions
                    if (!locationRegions.isEmpty()) {

                        reason = isJoining && _joined.remove(playerId)
                                ? RegionEventReason.JOIN_SERVER
                                : location.getReason();

                        for (IRegion region : locationRegions) {
                            synchronized (InternalPlayerWatcher.this) {
                                // check if player was not previously in region
                                if (cachedRegions.contains(region)) {
                                    // add to "enter" queue to verify later
                                    enter.offer(region);
                                } else {
                                    cachedRegions.add(region);
                                    _eventCaller.queue.addLast(new EventInfo(
                                            region, worldPlayer.player, reason, true
                                    ));
                                }
                            }
                        }
                    }

                    // check LEAVE regions
                    synchronized (InternalPlayerWatcher.this) {

                        if (!cachedRegions.isEmpty()) {

                            // get iterator for regions the player is currently in.
                            Iterator<IRegion> iterator = cachedRegions.iterator(PriorityType.LEAVE);

                            while (iterator.hasNext()) {
                                IRegion region = iterator.next();

                                // check if player was previously in region
                                if (!locationRegions.contains(region) || reason == RegionEventReason.DEAD) {
                                    //remove from players cached regions
                                    iterator.remove();
                                    _eventCaller.queue.addLast(new EventInfo(
                                            region, worldPlayer.player, location.getReason(), false
                                    ));
                                }
                            }
                        }
                    }

                    // verify "enter" queue
                    while (reason != null && !enter.isEmpty()) {
                        IRegion region = enter.poll();

                        synchronized (InternalPlayerWatcher.this) {
                            // check if player was not previously in region
                            if (!cachedRegions.contains(region)) {
                                cachedRegions.add(region);
                                _eventCaller.queue.addLast(new EventInfo(
                                        region, worldPlayer.player, reason, true
                                ));
                            }
                        }
                    }
                }

                // recycle player locations so they can be reused
                worldPlayer.locations.recycle();

            } // END while(queue.isEmpty)

            _isAsyncWatcherRunning = false;

        } // END run()
    }

    /**
     * Represents a player and the locations they have been
     * since the last player watcher cycle.
     */
    private static class WorldPlayer {

        final Player player;
        final CheckedOutElements<CachedLocation> locations;

        public WorldPlayer(Player player, CheckedOutElements<CachedLocation> locations) {
            this.player = player;
            this.locations = locations;
        }
    }

    /**
     * Contains info for a region event that needs to be called
     */
    private static class EventInfo {

        final IRegion region;
        final Player player;
        final RegionEventReason reason;
        final boolean isEntering;

        EventInfo(IRegion region, Player player, RegionEventReason reason, boolean isEntering) {
            this.region = region;
            this.player = player;
            this.reason = reason;
            this.isEntering = isEntering;
        }
    }

    /**
     * Calls region events in queue on the main thread.
     */
    private static class EventCaller implements Runnable {

        final Deque<EventInfo> queue = new ArrayDeque<EventInfo>(Bukkit.getMaxPlayers());

        @Override
        public void run() {

            while (!queue.isEmpty()) {

                EventInfo info = queue.removeFirst();
                if (info.region.isDisposed())
                    continue;

                IRegionEventListener listener;

                try {
                    listener = info.region.getEventListener();
                    if (listener == null) {
                        // print null pointer exception message to console.
                        throw new NullPointerException("Region event listener cannot be null.");
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    continue;
                }

                try {
                    if (info.isEntering)
                        listener.onPlayerEnter(info.player, info.reason.getEnterReason());
                    else
                        listener.onPlayerLeave(info.player, info.reason.getLeaveReason());
                }
                catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
