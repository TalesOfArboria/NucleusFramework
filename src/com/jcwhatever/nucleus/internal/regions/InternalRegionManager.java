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
import com.jcwhatever.nucleus.regions.collections.EventOrderedRegions;
import com.jcwhatever.nucleus.regions.options.EnterRegionReason;
import com.jcwhatever.nucleus.regions.options.LeaveRegionReason;
import com.jcwhatever.nucleus.regions.options.RegionPriority.PriorityType;
import com.jcwhatever.nucleus.regions.options.RegionPriority.RegionReason;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.LocationUtils;
import com.jcwhatever.nucleus.utils.MetaKey;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Scheduler;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

    private final Object _sync = new Object();

    // IDs of players that have joined within a region and have not yet moved.
    private Set<UUID> _joined = new HashSet<>(10);

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
        Scheduler.runTaskRepeat(plugin,  3, 3, new PlayerWatcher(this));
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
    public void updatePlayerLocation(Player player, RegionReason reason) {
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
    public void updatePlayerLocation(Player player, Location location, RegionReason reason) {
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
        if (player.hasMetadata("NPC"))
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
            region = region.getMeta(REGION_HANDLE);

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
     * Repeating task that determines which regions need events fired.
     */
    private static final class PlayerWatcher implements Runnable {

        private InternalRegionManager _manager;

        PlayerWatcher(InternalRegionManager manager) {
            _manager = manager;
        }

        @Override
        public void run() {

            List<World> worlds = new ArrayList<World>(_manager._listenerWorlds.getElements());

            final List<WorldPlayers> worldPlayers = new ArrayList<WorldPlayers>(worlds.size());

            // get players in worlds with regions
            for (World world : worlds) {

                if (world == null)
                    continue;

                List<Player> players = world.getPlayers();

                if (players == null || players.isEmpty())
                    continue;

                WorldPlayers wp = new WorldPlayers(_manager, world, players);
                if (wp.players.isEmpty())
                    continue;

                worldPlayers.add(wp);
            }

            // end if there are no players in region worlds
            if (worldPlayers.isEmpty())
                return;

            Scheduler.runTaskLaterAsync(Nucleus.getPlugin(), 1, new PlayerWatcherAsync(_manager, worldPlayers));
        }
    }

    /*
     * Async portion of the player watcher
     */
    private static class PlayerWatcherAsync implements Runnable {

        private final Object _sync;
        private final Collection<WorldPlayers> _worldPlayers;
        private final Map<UUID, EventOrderedRegions<IRegion>> _playerCacheMap;
        private final InternalRegionManager _manager;

        PlayerWatcherAsync(InternalRegionManager manager, Collection<WorldPlayers> worldPlayers) {
            _manager = manager;
            _sync = manager._sync;
            _worldPlayers = worldPlayers;
            _playerCacheMap = manager._playerCacheMap;
        }

        @Override
        public void run() {

            for (WorldPlayers wp : _worldPlayers) {

                synchronized (_sync) {

                    // get players in world
                    List<WorldPlayer> worldPlayers = wp.players;

                    // iterate players
                    for (WorldPlayer worldPlayer : worldPlayers) {

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

                            if (location.getReason() == RegionReason.JOIN_SERVER) {
                                // add player to _join set.
                                _manager._joined.add(playerId);

                                // do not notify regions until player has moved.
                                // allows time for player to load resource packs.
                                continue;
                            }

                            boolean isJoining = _manager._joined.contains(playerId);

                            if (isJoining && location.getReason() != RegionReason.MOVE) {
                                // ignore all other reasons until joined player moves
                                continue;
                            }

                            // see which regions a player actually is in
                            List<IRegion> inRegions = _manager.getListenerRegions(location, PriorityType.ENTER);

                            // check for entered regions
                            if (!inRegions.isEmpty()) {

                                // get enter reason
                                RegionReason reason = isJoining && _manager._joined.remove(playerId)
                                        ? RegionReason.JOIN_SERVER
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

                        // discard player locations
                        worldPlayer.locations.discard();

                    } // END for (Player

                } // END synchronized

            } // END for (WorldPlayers

        } // END run()

        /*
         * Executes doPlayerEnter method in the specified region on the main thread.
         */
        private void onPlayerEnter(final IRegion region, final Player p, RegionReason reason) {

            final EnterRegionReason enterReason = reason.getEnterReason();
            if (enterReason == null)
                throw new AssertionError();

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
        private void onPlayerLeave(final IRegion region, final Player p, RegionReason reason) {

            final LeaveRegionReason leaveReason = reason.getLeaveReason();
            if (leaveReason == null)
                throw new AssertionError();

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

    /*
     * Stores a collection of players that are in a world.
     */
    private static class WorldPlayers {

        public final World world;
        public final List<WorldPlayer> players;

        public WorldPlayers (InternalRegionManager manager, World world, List<Player> players) {
            this.world = world;

            List<WorldPlayer> worldPlayers = new ArrayList<WorldPlayer>(players.size());
            for (Player p : players) {

                if (p.hasMetadata("NPC"))
                    continue;

                PlayerLocationCache locations = manager.getPlayerLocations(p.getUniqueId());
                if (locations.isEmpty() || !locations.canRemoveAll())
                    continue;

                WorldPlayer worldPlayer = new WorldPlayer(p, locations.removeAll());
                worldPlayers.add(worldPlayer);

                locations.clear();
            }

            this.players = worldPlayers;
        }
    }

    /**
     * Represents a player and the locations they have been
     * since the last player watcher update.
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
