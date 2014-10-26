package com.jcwhatever.bukkit.generic.regions;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.collections.EntryCounter;
import com.jcwhatever.bukkit.generic.collections.EntryCounter.RemovalPolicy;
import com.jcwhatever.bukkit.generic.messaging.Messenger;
import com.jcwhatever.bukkit.generic.performance.SingleCache;
import com.jcwhatever.bukkit.generic.player.collections.PlayerMap;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Global Region Manager
 */
public class RegionManager {

    // String key is chunk coordinates
    private final Map<String, Set<ReadOnlyRegion>> _listenerRegionsMap = new HashMap<>(100);
    private final Map<String, Set<ReadOnlyRegion>> _allRegionsMap = new HashMap<>(100);

    private final Object _sync = new Object();
    private EntryCounter<World> _listenerWorlds = new EntryCounter<>(RemovalPolicy.REMOVE);
    private Map<UUID, Set<ReadOnlyRegion>> _playerMap;
    private Map<UUID, LinkedList<Location>> _playerLocationCache;
    private SingleCache<UUID, LinkedList<Location>> _lastLocationCache = new SingleCache<>();
    private Set<ReadOnlyRegion> _regions = new HashSet<>(100);


    /**
     * Constructor. Used by jcGenerics to initialize RegionEventManager.
     */
    public RegionManager() {
        _playerMap = new PlayerMap<>();
        _playerLocationCache = new PlayerMap<>();
        PlayerWatcher _playerWatcher = new PlayerWatcher();
        Scheduler.runTaskRepeat(GenericsLib.getPlugin(),  7, 7, _playerWatcher);
    }

    /**
     * Get number of regions registered.
     */
    public int getRegionCount() {
        return _regions.size();
    }

    /**
     * Get a set of regions that contain the specified location.
     *
     * @param location  The location to check.
     */
    public Set<ReadOnlyRegion> getRegions(Location location) {
        PreCon.notNull(location);

        return getRegion(location, _allRegionsMap);
    }

    /**
     * Get a set of regions that the specified location
     * is inside of and are player watchers/listeners.
     *
     * @param location  The location to check.
     */
    public Set<ReadOnlyRegion> getListenerRegions(Location location) {
        return getRegion(location, _listenerRegionsMap);
    }

    /**
     * Get all regions that intersect with the specified chunk.
     *
     * @param chunk  The chunk to check.
     */
    public Set<ReadOnlyRegion> getRegionsInChunk(Chunk chunk) {
        synchronized(_sync) {

            if (getRegionCount() == 0)
                return new HashSet<>(0);

            // TODO: Add Single Cache using chunk as key

            String key = getChunkKey(chunk.getWorld(), chunk.getX(), chunk.getZ());

            Set<ReadOnlyRegion> regions = _allRegionsMap.get(key);
            if (regions == null)
                return new HashSet<>(0);

            _sync.notifyAll();
            return new HashSet<>(regions);
        }
    }

    /**
     * Get all regions that player is currently in.
     *
     * @param p  The player to check.
     */
    public List<ReadOnlyRegion> getPlayerRegions(Player p) {
        PreCon.notNull(p);

        synchronized(_sync) {
            if (_playerMap == null)
                return new ArrayList<ReadOnlyRegion>(0);

            Set<ReadOnlyRegion> regions = _playerMap.get(p.getUniqueId());
            if (regions == null)
                return new ArrayList<>(0);

            _sync.notifyAll();
            return new ArrayList<>(regions);
        }
    }

    /**
     * Add a location that the player has moved to so it can be
     * cached and processed by the player watcher the next time it
     * runs.
     *
     * @param p         The player.
     * @param location  The location to cache.
     */
    public void updatePlayerLocation(Player p, Location location) {
        PreCon.notNull(p);
        PreCon.notNull(location);

        if (!_listenerWorlds.contains(location.getWorld()))
            return;

        // ignore NPC's
        if (p.hasMetadata("NPC"))
            return;

        LinkedList<Location> locations = getPlayerLocations(p.getUniqueId());
        locations.add(location);
    }

    /**
     * Causes a region to re-fire the onPlayerEnter event
     * if the player is already in it
     * .
     * @param p       The player.
     * @param region  The region.
     */
    public void resetPlayerRegion(Player p, Region region) {
        PreCon.notNull(p);
        PreCon.notNull(region);

        synchronized(_sync) {
            if (_playerMap == null)
                return;

            Set<ReadOnlyRegion> regions = _playerMap.get(p.getUniqueId());
            if (regions == null)
                return;

            regions.remove(new ReadOnlyRegion(region));
            _sync.notifyAll();
        }
    }

    /*
     * Get all regions contained in the specified location using
     * the supplied region map.
     */
    private Set<ReadOnlyRegion> getRegion(Location location, Map<String, Set<ReadOnlyRegion>> map) {
        synchronized(_sync) {

            Set<ReadOnlyRegion> results = new HashSet<>(10);

            if (getRegionCount() == 0)
                return results;

            // calculate chunk location instead of getting it from chunk
            // to prevent asynchronous issues
            int chunkX = (int)Math.floor(location.getX() / 16);
            int chunkZ = (int)Math.floor(location.getZ() / 16);

            String key = getChunkKey(location.getWorld(), chunkX, chunkZ);

            Set<ReadOnlyRegion> regions = map.get(key);
            if (regions == null)
                return results;

            for (ReadOnlyRegion region : regions) {
                if (region.contains(location))
                    results.add(region);
            }

            _sync.notifyAll();
            return results;
        }
    }

    /**
     * Register a region so it can be found in searches
     * and its events called if it is a player watcher.
     *
     * @param region  The Region to register.
     */
    void register(Region region) {
        PreCon.notNull(region);

        if (!region.isDefined()) {
            Messenger.debug(GenericsLib.getPlugin(), "Failed to register region '{0}' with RegionManager because it's coords are undefined.", region.getName());
            return;
        }

        ReadOnlyRegion readOnlyRegion = new ReadOnlyRegion(region);

        _regions.add(readOnlyRegion);

        synchronized(_sync) {

            int xMax = region.getChunkX() + region.getChunkXWidth();
            int zMax = region.getChunkZ() + region.getChunkZWidth();

            boolean hasRegion = false;

            for (int x= region.getChunkX(); x < xMax; x++) {
                for (int z= region.getChunkZ(); z < zMax; z++) {

                    String key = getChunkKey(region.getWorld(), x, z);

                    if (region.isPlayerWatcher()) {
                        addToMap(_listenerRegionsMap, key, readOnlyRegion);
                    }
                    else {
                        hasRegion = removeFromMap(_listenerRegionsMap, key, readOnlyRegion);
                    }

                    addToMap(_allRegionsMap, key, readOnlyRegion);
                }
            }

            if (region.isPlayerWatcher()) {
                _listenerWorlds.add(region.getWorld());
            }
            else if (hasRegion){
                _listenerWorlds.subtract(region.getWorld());
            }

            _sync.notifyAll();
        }
    }

    /**
     * Unregister a region and its events completely.
     *
     * <p>Called when a region is disposed.</p>
     *
     * @param region  The Region to unregister.
     */
    void unregister(Region region) {
        PreCon.notNull(region);

        if (!region.isDefined())
            return;

        ReadOnlyRegion readOnlyRegion = new ReadOnlyRegion(region);

        synchronized(_sync) {

            int xMax = region.getChunkX() + region.getChunkXWidth();
            int zMax = region.getChunkZ() + region.getChunkZWidth();

            for (int x= region.getChunkX(); x < xMax; x++) {
                for (int z= region.getChunkZ(); z < zMax; z++) {

                    String key = getChunkKey(region.getWorld(), x, z);

                    removeFromMap(_listenerRegionsMap, key, readOnlyRegion);
                    removeFromMap(_allRegionsMap, key, readOnlyRegion);
                }
            }

            if (_regions.remove(readOnlyRegion) && region.isPlayerWatcher()) {
                _listenerWorlds.subtract(region.getWorld());
            }

            _sync.notifyAll();
        }
    }

    /*
     * Get the cached movement locations of a player that have not been processed
      * by the PlayerWatcher task yet.
     */
    private LinkedList<Location> getPlayerLocations(UUID playerId) {
        if (_lastLocationCache.keyEquals(playerId))
            return _lastLocationCache.getValue();

        LinkedList<Location> locations = _playerLocationCache.get(playerId);
        if (locations == null) {
            locations = new LinkedList<Location>();
            _playerLocationCache.put(playerId, locations);
        }

        _lastLocationCache.set(playerId, locations);

        return locations;
    }

    /*
     * Clear cached movement locations of a player
     */
    private void clearPlayerLocations(UUID playerId) {
        LinkedList<Location> locations = getPlayerLocations(playerId);
        locations.clear();
    }

    /*
     * Executes doPlayerLeave method in the specified region on the main thread.
     */
    private void onPlayerLeave(final Region region, final Player p) {

        Scheduler.runTaskSync(GenericsLib.getPlugin(), new Runnable() {

            @Override
            public void run() {
                region.doPlayerLeave(p);
            }

        });
    }

    /*
     * Executes doPlayerEnter method in the specified region on the main thread.
     */
    private void onPlayerEnter(final Region region, final Player p) {

        Scheduler.runTaskSync(GenericsLib.getPlugin(), new Runnable() {

            @Override
            public void run() {
                region.doPlayerEnter(p);
            }

        });
    }

    /*
     * Add a region to a region map.
     */
    private void addToMap(Map<String, Set<ReadOnlyRegion>> map, String key, ReadOnlyRegion region) {
        Set<ReadOnlyRegion> regions = map.get(key);
        if (regions == null) {
            regions = new HashSet<>(10);
            map.put(key, regions);
        }
        regions.add(region);
    }

    /*
     * Remove a region from a region map.
     */
    private boolean removeFromMap(Map<String, Set<ReadOnlyRegion>> map, String key, ReadOnlyRegion region) {
        Set<ReadOnlyRegion> regions = map.get(key);
        return regions != null && regions.remove(region);
    }

    /*
     * Get a regions chunk map key.
     */
    private String getChunkKey(World world, int x, int z) {
        return world.getName() + '.' + String.valueOf(x) + '.' + String.valueOf(z);
    }

    /*
     * Repeating task that determines which regions need events fired.
     */
    private final class PlayerWatcher implements Runnable {

        private boolean _isRunning = true;

        public void cancel() {
            _isRunning = false;
        }

        @Override
        public void run() {

            if (!_isRunning)
                return;

            List<World> worlds;

            synchronized (_sync) {
                worlds = new ArrayList<World>(_listenerWorlds.getTypesCounted());
            }

            final List<WorldPlayers> worldPlayers = new ArrayList<WorldPlayers>(worlds.size());

            for (World world : worlds) {
                if (world == null)
                    continue;

                List<Player> players = world.getPlayers();

                if (players == null || players.isEmpty())
                    continue;

                worldPlayers.add(new WorldPlayers(world, players));
            }

            if (!worldPlayers.isEmpty()) {

                Bukkit.getScheduler().runTaskAsynchronously(GenericsLib.getPlugin(), new Runnable() {

                    @Override
                    public void run() {

                        for (WorldPlayers wp : worldPlayers) {

                            synchronized (_sync) {

                                // get players in world
                                List<WorldPlayer> worldPlayers = wp.players;

                                // iterate players
                                for (WorldPlayer worldPlayer : worldPlayers) {

                                    UUID playerId = worldPlayer.player.getUniqueId();

                                    // get regions the player is in (cached from previous check)
                                    Set<ReadOnlyRegion> cachedRegions = _playerMap.get(playerId);

                                    if (cachedRegions == null) {
                                        cachedRegions = new HashSet<>(10);
                                        _playerMap.put(playerId, cachedRegions);
                                    }

                                    // iterate cached locations
                                    while (!worldPlayer.locations.isEmpty()) {
                                        Location location = worldPlayer.locations.removeFirst();

                                        // see which regions a player actually is in
                                        Set<ReadOnlyRegion> inRegions = getListenerRegions(location); // causing async issues


                                        // check for entered regions
                                        if (inRegions != null && !inRegions.isEmpty()) {
                                            for (ReadOnlyRegion region : inRegions) {
                                                if (!cachedRegions.contains(region)) {
                                                    onPlayerEnter(region.getHandle(), worldPlayer.player);
                                                    cachedRegions.add(region);
                                                }
                                            }
                                        }

                                        // check for regions player has left
                                        if (!cachedRegions.isEmpty()) {
                                            Iterator<ReadOnlyRegion> iterator = cachedRegions.iterator();
                                            while(iterator.hasNext()) {
                                                ReadOnlyRegion region = iterator.next();

                                                if (inRegions == null || !inRegions.contains(region)) {
                                                    onPlayerLeave(region.getHandle(), worldPlayer.player);
                                                    iterator.remove();
                                                }
                                            }
                                        }
                                    }


                                } // END for (Player

                                _sync.notifyAll();

                            } // END synchronized

                        } // END for (WorldPlayers

                    } // END run()

                });
            }


            // schedule next check
            //if (jcGenerics.getInstance().isEnabled())
            //	Bukkit.getScheduler().runTaskLater(jcGenerics.getInstance(), _instance, 7);
        }
    }


    private class WorldPlayers {
        public final World world;
        public final List<WorldPlayer> players;

        public WorldPlayers (World world, List<Player> players) {
            this.world = world;

            List<WorldPlayer> worldPlayers = new ArrayList<WorldPlayer>(players.size());
            for (Player p : players) {

                LinkedList<Location> locations = getPlayerLocations(p.getUniqueId());
                if (locations.isEmpty())
                    continue;

                WorldPlayer worldPlayer = new WorldPlayer(p, new LinkedList<Location>(locations));
                worldPlayers.add(worldPlayer);

                clearPlayerLocations(p.getUniqueId());
            }

            this.players = worldPlayers;
        }
    }

    private static class WorldPlayer {
        public final Player player;
        public final LinkedList<Location> locations;

        public WorldPlayer(Player p, LinkedList<Location> locations) {
            this.player = p;
            this.locations = locations;
        }
    }

}
