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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.ElementCounter;
import com.jcwhatever.nucleus.collections.ElementCounter.RemovalPolicy;
import com.jcwhatever.nucleus.collections.players.PlayerMap;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.internal.regions.PlayerLocationCache.CachedLocation;
import com.jcwhatever.nucleus.internal.regions.PlayerLocationCache.PlayerLocations;
import com.jcwhatever.nucleus.regions.IGlobalRegionManager;
import com.jcwhatever.nucleus.regions.IRegion;
import com.jcwhatever.nucleus.regions.IRegionEventListener;
import com.jcwhatever.nucleus.regions.ReadOnlyRegion;
import com.jcwhatever.nucleus.regions.options.EnterRegionReason;
import com.jcwhatever.nucleus.regions.options.LeaveRegionReason;
import com.jcwhatever.nucleus.regions.options.RegionEventPriority.PriorityType;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.MetaKey;
import com.jcwhatever.nucleus.utils.NpcUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Scheduler;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Global Region Manager.
 *
 * <p>Tracks instances of {@link IRegion} and provides methods to determine which regions
 * a player is in as well as track players to determine when they enter and leave
 * player watcher regions.</p>
 *
 * <p>Methods that return {@link IRegion} instances are returning
 * {@link com.jcwhatever.nucleus.regions.ReadOnlyRegion} instances. This is to prevent
 * inter-plugin conflicts caused by changes to a region that the region owning plugin is
 * unaware of.</p>
 *
 * <p>Methods that return a specific region type return the actual region instance.</p>
 */
public final class InternalRegionManager extends RegionTypeManager<IRegion> implements IGlobalRegionManager {

    public static final MetaKey<IRegion> REGION_HANDLE = new MetaKey<IRegion>(IRegion.class);

    // worlds that have regions
    private final ElementCounter<World> _listenerWorlds = new ElementCounter<>(RemovalPolicy.REMOVE);

    // cached regions the player was detected in during last player watcher cycle.
    private final Map<UUID, EventOrderedRegions<IRegion>> _playerCacheMap;

    // locations the player was detected in between player watcher cycles.
    private final Map<UUID, PlayerLocationCache> _playerLocationCache;

    // store managers for individual region types.
    private final Map<Class<? extends IRegion>, RegionTypeManager<?>> _managers = new HashMap<>(15);

    // store regions by lookup name. lookup name is: PluginName:RegionName
    private final Map<String, IRegion> _regionNameMap = new HashMap<>(35);

    private final PlayerWatcherAsync _watcherAsync = new PlayerWatcherAsync();

    private final Object _sync = new Object();

    // IDs of players that have joined within a region and have not yet moved.
    private Set<UUID> _joined = new HashSet<>(10);

    private volatile boolean _isAsyncWatcherRunning;

    /**
     * Constructor
     *
     * @param plugin  The owning plugin.
     */
    public InternalRegionManager(Plugin plugin) {
        super(IRegion.class);

        if (Nucleus.getPlugin() != plugin) {
            throw new RuntimeException("InternalRegionManager should not be instantiated.");
        }

        _playerCacheMap = new PlayerMap<>(plugin);
        _playerLocationCache = new PlayerMap<>(plugin);
        Scheduler.runTaskRepeat(plugin,  2, 2, new PlayerWatcher());
        Scheduler.runTaskRepeatAsync(plugin, 1, 1, _watcherAsync);
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

        if (player.isDead())
            return;

        // ignore NPC's
        if (player.hasMetadata("NPC"))
            return;

        if (!_listenerWorlds.contains(player.getWorld()))
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

        if (player.isDead())
            return;

        // ignore NPC's
        if (NpcUtils.isNpc(player))
            return;

        if (!_listenerWorlds.contains(player.getWorld()))
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

        // ignore NPC's
        if (NpcUtils.isNpc(player))
            return;

        synchronized (_sync) {

            EventOrderedRegions<IRegion> regions = _playerCacheMap.remove(player.getUniqueId());

            if (regions == null)
                return;

            Iterator<IRegion> iterator = regions.iterator(PriorityType.LEAVE);
            while (iterator.hasNext()) {
                IRegion region = iterator.next();

                if (region.isEventListener())
                    region.getEventListener().onPlayerLeave(player, reason);
            }

            clearPlayerLocations(player.getUniqueId());
        }
    }

    @Override
    public <T extends IRegion> boolean hasRegion(Location location, Class<T> regionClass) {
        RegionTypeManager<T> manager = getManager(regionClass, false);
        return manager != null && manager.hasRegion(location);
    }

    @Override
    public <T extends IRegion> boolean hasRegion(World world, int x, int y, int z, Class<T> regionClass) {
        RegionTypeManager<T> manager = getManager(regionClass, false);
        return manager != null && manager.hasRegion(world, x, y, z);
    }

    @Nullable
    @Override
    public IRegion getRegion(Plugin plugin, String name) {
        return _regionNameMap.get(getLookupName(plugin, name));
    }

    @Override
    public <T extends IRegion> List<T> getRegions(Location location, Class<T> regionClass) {
        PreCon.notNull(location);
        PreCon.notNull(regionClass);

        RegionTypeManager<T> manager = getManager(regionClass, false);
        if (manager == null)
            return CollectionUtils.unmodifiableList();

        return manager.getRegions(location);
    }

    @Override
    public <T extends IRegion> List<T> getRegions(World world, int x, int y, int z, Class<T> regionClass) {
        PreCon.notNull(world);
        PreCon.notNull(regionClass);

        RegionTypeManager<T> manager = getManager(regionClass, false);
        if (manager == null)
            return CollectionUtils.unmodifiableList();

        return manager.getRegions(world, x, y, z);
    }

    @Override
    public <T extends IRegion> List<T> getListenerRegions(Location location, Class<T> regionClass) {
        PreCon.notNull(location);
        PreCon.notNull(regionClass);

        RegionTypeManager<T> manager = getManager(regionClass, false);
        if (manager == null)
            return CollectionUtils.unmodifiableList();

        return manager.getListenerRegions(location);
    }

    @Override
    public <T extends IRegion> List<T> getListenerRegions(World world, int x, int y, int z,
                                                          Class<T> regionClass) {
        PreCon.notNull(world);
        PreCon.notNull(regionClass);

        RegionTypeManager<T> manager = getManager(regionClass, false);
        if (manager == null)
            return CollectionUtils.unmodifiableList();

        return manager.getListenerRegions(world, x, y, z);
    }

    @Override
    public <T extends IRegion> List<T> getListenerRegions(Location location, PriorityType priorityType,
                                                          Class<T> regionClass) {
        PreCon.notNull(location);
        PreCon.notNull(priorityType);
        PreCon.notNull(regionClass);

        RegionTypeManager<T> manager = getManager(regionClass, false);
        if (manager == null)
            return CollectionUtils.unmodifiableList();

        return manager.getListenerRegions(location, priorityType);
    }

    @Override
    public <T extends IRegion> List<T> getListenerRegions(World world, int x, int y, int z,
                                                          PriorityType priorityType, Class<T> regionClass) {
        PreCon.notNull(world);
        PreCon.notNull(priorityType);
        PreCon.notNull(regionClass);

        RegionTypeManager<T> manager = getManager(regionClass, false);
        if (manager == null)
            return CollectionUtils.unmodifiableList();

        return manager.getListenerRegions(world, x, y, z, priorityType);
    }

    @Override
    public <T extends IRegion> List<T> getRegionsInChunk(Chunk chunk, Class<T> regionClass) {
        PreCon.notNull(chunk);
        PreCon.notNull(regionClass);

        RegionTypeManager<T> manager = getManager(regionClass, false);
        if (manager == null)
            return CollectionUtils.unmodifiableList();

        return manager.getRegionsInChunk(chunk);
    }

    @Override
    public <T extends IRegion> List<T> getRegionsInChunk(World world, int x, int z, Class<T> regionClass) {
        PreCon.notNull(world);
        PreCon.notNull(regionClass);

        RegionTypeManager<T> manager = getManager(regionClass, false);
        if (manager == null)
            return CollectionUtils.unmodifiableList();

        return manager.getRegionsInChunk(world, x, z);
    }

    @Override
    public List<IRegion> getPlayerRegions(Player player) {
        PreCon.notNull(player);

        if (_playerCacheMap == null)
            return new ArrayList<>(0);

        synchronized(_sync) {
            Set<IRegion> regions = _playerCacheMap.get(player.getUniqueId());
            if (regions == null)
                return new ArrayList<>(0);

            _sync.notifyAll();
            return new ArrayList<>(regions);
        }
    }

    @Override
    public void forgetPlayer(Player p, IRegion region) {
        PreCon.notNull(p);
        PreCon.notNull(region);

        if (_playerCacheMap == null)
            return;

        synchronized(_sync) {

            Set<IRegion> regions = _playerCacheMap.get(p.getUniqueId());
            if (regions == null)
                return;

            regions.remove(new ReadOnlyRegion(region));
        }
    }

    @Override
    public void register(IRegion region) {
        PreCon.notNull(region);

        if (!region.isDefined() || !region.isWorldLoaded()) {
            NucMsg.debug("Failed to register region '{0}' with RegionManager because " +
                    "it's coords are undefined. Region Type: {1}", region.getName(), region.getClass().getName());
            return;
        }

        if (region instanceof ReadOnlyRegion) {
            region = region.getMeta().get(REGION_HANDLE);

            if (region == null) {
                throw new RuntimeException("ReadOnlyRegions handle has no meta reference to itself.");
            }
        }

        @SuppressWarnings("unchecked")
        RegionTypeManager<IRegion> manager = (RegionTypeManager<IRegion>)getManager(region.getClass(), true);
        assert manager != null;
        manager.register(region);

        ReadOnlyRegion readOnlyRegion = new ReadOnlyRegion(region);
        super.register(readOnlyRegion);

        _regionNameMap.put(getLookupName(region.getPlugin(), region), readOnlyRegion);
    }

    @Override
    public void unregister(IRegion region) {
        PreCon.notNull(region);

        if (!region.isDefined() || !region.isWorldLoaded())
            return;

        @SuppressWarnings("unchecked")
        RegionTypeManager<IRegion> manager = (RegionTypeManager<IRegion>)getManager(region.getClass(), false);
        if (manager != null) {
            manager.unregister(region);
        }

        ReadOnlyRegion readOnlyRegion = new ReadOnlyRegion(region);
        super.unregister(readOnlyRegion);

        _regionNameMap.remove(getLookupName(region.getPlugin(), region));
    }

    @Override
    protected void onRegister(IRegion region, boolean isFormerListener) {
        if (region.isEventListener()) {
            //noinspection ConstantConditions
            _listenerWorlds.add(region.getWorld());
        }
        else if (isFormerListener){
            //noinspection ConstantConditions
            _listenerWorlds.subtract(region.getWorld());
        }
    }

    @Override
    protected void onUnregister(IRegion region) {
        if (region.isEventListener()) {
            //noinspection ConstantConditions
            _listenerWorlds.subtract(region.getWorld());
        }
    }

    /*
     * Get the cached movement locations of a player that have not been processed
      * by the PlayerWatcher task yet.
     */
    private PlayerLocationCache getPlayerLocations(UUID playerId) {

        PlayerLocationCache locations = _playerLocationCache.get(playerId);
        if (locations == null) {
            locations = new PlayerLocationCache();
            _playerLocationCache.put(playerId, locations);
        }

        return locations;
    }

    /*
     * Clear cached movement locations of a player
     */
    private void clearPlayerLocations(UUID playerId) {
        PlayerLocationCache locations = getPlayerLocations(playerId);
        locations.clear();
    }

    /*
     * Remove a region from a region map.
     */
    private <T extends Set<IRegion>> boolean removeFromMap(Map<String, T> map, String key, ReadOnlyRegion region) {
        Set<IRegion> regions = map.get(key);
        return regions != null && regions.remove(region);
    }

    /*
     * Get region manager for a specific region type.
     */
    @Nullable
    private <T extends IRegion> RegionTypeManager<T> getManager(Class<T> regionClass, boolean create) {

        @SuppressWarnings("unchecked")
        RegionTypeManager<T> manager = (RegionTypeManager<T>)_managers.get(regionClass);

        if (manager == null && create) {
            manager = new RegionTypeManager<>(regionClass);
            _managers.put(regionClass, manager);
        }

        return manager;
    }

    /*
     * Get a regions lookup name.
     */
    private String getLookupName(Plugin plugin, IRegion region) {
        return plugin.getName() + ':' + region.getSearchName();
    }

    /*
     * Get a regions lookup name.
     */
    private String getLookupName(Plugin plugin, String name) {
        return plugin.getName() + ':' + name.toLowerCase();
    }

    /*
     * Repeating task that determines which regions need events fired.
     */
    private final class PlayerWatcher implements Runnable {

        @Override
        public void run() {

            // do not run while async watcher is running
            if (_isAsyncWatcherRunning)
                return;

            // get worlds where listener regions exist
            List<World> worlds = new ArrayList<World>(_listenerWorlds.getElements());

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
                    if (NpcUtils.isNpc(player))
                        continue;

                    // get locations that the player was recorded in between watcher cycles
                    PlayerLocationCache locations = getPlayerLocations(player.getUniqueId());

                    // skip if there are no locations recorded
                    if (locations.isEmpty() || !locations.canRemoveAll())
                        continue;

                    WorldPlayer worldPlayer = new WorldPlayer(player, locations.removeAll());
                    _watcherAsync.queue.add(worldPlayer);

                    // clear recorded player locations from cache
                    locations.clear();
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

        final LinkedList<WorldPlayer> queue = new LinkedList<>();

        @Override
        public void run() {

            if (!_isAsyncWatcherRunning)
                return;

            // iterate players
            while (!queue.isEmpty()) {

                WorldPlayer worldPlayer = queue.remove();

                synchronized (_sync) {

                    UUID playerId = worldPlayer.player.getUniqueId();

                    // get regions the player is in (cached from previous check)
                    EventOrderedRegions<IRegion> cachedRegions = _playerCacheMap.get(playerId);

                    if (cachedRegions == null) {
                        cachedRegions = new EventOrderedRegions<>(7);
                        _playerCacheMap.put(playerId, cachedRegions);
                    }

                    // iterate cached locations
                    while (!worldPlayer.locations.isEmpty()) {
                        CachedLocation location = worldPlayer.locations.remove();

                        if (location.getReason() == RegionEventReason.JOIN_SERVER) {
                            // add player to _join set.
                            _joined.add(playerId);

                            // do not notify regions until player has moved.
                            // allows time for player to load resource packs.
                            continue;
                        }

                        boolean isJoining = _joined.contains(playerId);

                        if (isJoining && location.getReason() != RegionEventReason.MOVE) {
                            // ignore all other reasons until joined player moves
                            continue;
                        }

                        // see which regions a player is actually in
                        List<IRegion> inRegions = getListenerRegions(location, PriorityType.ENTER);

                        // check for entered regions
                        if (!inRegions.isEmpty()) {

                            // get enter reason
                            RegionEventReason reason = isJoining && _joined.remove(playerId)
                                    ? RegionEventReason.JOIN_SERVER
                                    : location.getReason();

                            for (IRegion region : inRegions) {

                                // check if player was not previously in region
                                if (!cachedRegions.contains(region)) {

                                    cachedRegions.add(region);
                                    onPlayerEnter(region, worldPlayer.player, reason);
                                }
                            }
                        }

                        // check for regions player has left
                        if (!cachedRegions.isEmpty()) {
                            Iterator<IRegion> iterator = cachedRegions.iterator(PriorityType.LEAVE);
                            while(iterator.hasNext()) {
                                IRegion region = iterator.next();

                                // check if player was previously in region
                                if (!inRegions.contains(region)) {

                                    iterator.remove();
                                    onPlayerLeave(region, worldPlayer.player,
                                            location.getReason());
                                }
                            }
                        }
                    }

                    // recycle player locations so they can be reused
                    worldPlayer.locations.recycle();

                } // END synchronized

            } // END while(queue.isEmpty)

            _isAsyncWatcherRunning = false;

        } // END run()

        /*
         * Executes doPlayerEnter method in the specified region on the main thread.
         */
        private void onPlayerEnter(final IRegion region, final Player p, RegionEventReason reason) {

            final EnterRegionReason enterReason = reason.getEnterReason();
            if (enterReason == null)
                throw new AssertionError();

            // run task on main thread
            Scheduler.runTaskSync(Nucleus.getPlugin(), new Runnable() {

                @Override
                public void run() {
                    IRegionEventListener listener = region.getEventListener();
                    if (listener == null)
                        throw new NullPointerException("Region event listener cannot be null.");

                    listener.onPlayerEnter(p, enterReason);
                }
            });
        }

        /*
         * Executes doPlayerLeave method in the specified region on the main thread.
         */
        private void onPlayerLeave(final IRegion region, final Player p, RegionEventReason reason) {

            final LeaveRegionReason leaveReason = reason.getLeaveReason();
            if (leaveReason == null)
                throw new AssertionError();

            // run task on main thread
            Scheduler.runTaskSync(Nucleus.getPlugin(), new Runnable() {

                @Override
                public void run() {

                    IRegionEventListener listener = region.getEventListener();
                    if (listener == null)
                        throw new NullPointerException("Region event listener cannot be null.");

                    listener.onPlayerLeave(p, leaveReason);
                }
            });
        }
    }

    /**
     * Represents a player and the locations they have been
     * since the last player watcher cycle.
     */
    private static class WorldPlayer {
        public final Player player;
        public final PlayerLocations locations;

        public WorldPlayer(Player p, PlayerLocations locations) {
            this.player = p;
            this.locations = locations;
        }
    }
}
