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

import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.regions.IRegion;
import com.jcwhatever.nucleus.regions.Region.PriorityType;
import com.jcwhatever.nucleus.regions.data.OrderedRegions;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A region manager responsible for storing a specific region type.
 */
public class RegionTypeManager<R extends IRegion> {

    // Player watcher regions chunk map. String key is chunk coordinates.
    private final Map<String, OrderedRegions<R>> _listenerRegionsMap = new HashMap<>(10);

    // All regions chunk map. String key is chunk coordinates
    private final Map<String, Set<R>> _allRegionsMap = new HashMap<>(15);

    // hash set of all registered regions
    private final Set<R> _regions = new HashSet<>(10);

    // synchronization object
    private final Object _sync = new Object();

    private final Class<R> _regionClass;

    /**
     * Constructor.
     *
     * @param regionClass  The region type class.
     */
    public RegionTypeManager (Class<R> regionClass) {
        PreCon.notNull(regionClass);

        _regionClass = regionClass;
    }

    /**
     * Get the region type class.
     */
    public Class<R> getRegionClass() {
        return _regionClass;
    }

    /**
     * Get number of regions registered.
     */
    public int getRegionCount() {
        return _regions.size();
    }

    /**
     * Determine if there is a region at the specified location.
     *
     * @param location  The location to check.
     */
    public boolean hasRegion(Location location) {
        PreCon.notNull(location);

        return hasRegion(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Determine if there is a region at the specified location.
     *
     * @param world  The world to check in.
     * @param x      The X coordinates to check.
     * @param y      The Y coordinates to check.
     * @param z      The Z coordinates to check.
     */
    public boolean hasRegion(World world, int x, int y, int z) {

        synchronized(_sync) {

            if (_regions.size() == 0)
                return false;

            // calculate chunk location instead of getting it from chunk
            // to prevent asynchronous issues
            int chunkX = (int)Math.floor((double)x / 16);
            int chunkZ = (int)Math.floor((double)z / 16);

            String key = getChunkKey(world, chunkX, chunkZ);

            Set<R> regions = _allRegionsMap.get(key);
            if (regions == null)
                return false;

            for (R region : regions) {
                if (region.contains(x, y, z))
                    return true;
            }

            return false;
        }
    }

    /**
     * Get a set of regions that contain the specified location.
     *
     * @param location  The location to check.
     */
    public List<R> getRegions(Location location) {
        PreCon.notNull(location);

        return getRegion(location.getWorld(),
                location.getBlockX(), location.getBlockY(), location.getBlockZ(),
                PriorityType.ENTER, _allRegionsMap);
    }

    /**
     * Get a set of regions that contain the specified location.
     *
     * @param world  The world to check.
     * @param x      The x coordinates.
     * @param y      The y coordinates.
     * @param z      The z coordinates.
     */
    public List<R> getRegions(World world, int x, int y, int z) {
        PreCon.notNull(world);

        return getRegion(world, x, y, z, PriorityType.ENTER, _allRegionsMap);
    }

    /**
     * Get a set of regions that the specified location
     * is inside of and are player watchers/listeners.
     *
     * @param location  The location to check.
     */
    public List<R> getListenerRegions(Location location) {
        return getRegion(location.getWorld(),
                location.getBlockX(), location.getBlockY(), location.getBlockZ(),
                PriorityType.ENTER, _listenerRegionsMap);
    }

    /**
     * Get a set of regions that the specified location
     * is inside of and are player watchers/listeners.
     *
     * @param world  The world to check.
     * @param x      The x coordinates.
     * @param y      The y coordinates.
     * @param z      The z coordinates.
     */
    public List<R> getListenerRegions(World world, int x, int y, int z) {
        return getRegion(world, x, y, z, PriorityType.ENTER, _listenerRegionsMap);
    }

    /**
     * Get a set of regions that the specified location
     * is inside of and are player watchers/listeners.
     *
     * @param location      The location to check.
     * @param priorityType  The priority sorting type of the returned list.
     */
    public List<R> getListenerRegions(Location location, PriorityType priorityType) {
        return getRegion(location.getWorld(),
                location.getBlockX(), location.getBlockY(), location.getBlockZ(),
                priorityType, _listenerRegionsMap);
    }

    /**
     * Get a set of regions that the specified location
     * is inside of and are player watchers/listeners.
     *
     * @param world         The world to check.
     * @param x             The X coordinates.
     * @param y             The Y coordinates.
     * @param z             The Z coordinates.
     * @param priorityType  The priority sorting type of the returned list.
     */
    public List<R> getListenerRegions(World world, int x, int y, int z, PriorityType priorityType) {
        return getRegion(world, x, y, z, priorityType, _listenerRegionsMap);
    }

    /**
     * Get all regions that intersect with the specified chunk.
     *
     * @param chunk  The chunk to check.
     */
    public List<R> getRegionsInChunk(Chunk chunk) {
        return getRegionsInChunk(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    /**
     * Get all regions that intersect with the specified chunk.
     *
     * @param world  The world the chunk is in.
     * @param x      The chunks X coordinates.
     * @param z      The chunks Z coordinates.
     */
    public List<R> getRegionsInChunk(World world, int x, int z) {
        synchronized(_sync) {

            String key = getChunkKey(world, x, z);

            Set<R> regions = _allRegionsMap.get(key);
            if (regions == null)
                return CollectionUtils.unmodifiableList();

            return CollectionUtils.unmodifiableList(regions);
        }
    }

    /*
     * Get all regions contained in the specified location using
     * the supplied region map.
     */
    private <T extends Set<R>> List<R> getRegion(World world, int x, int y, int z,
                                                             PriorityType priorityType,
                                                             Map<String, T> map) {
        synchronized(_sync) {

            List<R> results = new ArrayList<>(10);

            if (_regions.size() == 0)
                return results;

            // calculate chunk location instead of getting it from chunk
            // to prevent asynchronous issues
            int chunkX = (int)Math.floor((double)x / 16);
            int chunkZ = (int)Math.floor((double)z / 16);

            String key = getChunkKey(world, chunkX, chunkZ);

            Set<R> regions = map.get(key);
            if (regions == null)
                return results;

            Iterator<R> iterator;

            iterator = regions instanceof OrderedRegions
                    ? ((OrderedRegions<R>) regions).iterator(priorityType)
                    : regions.iterator();

            while (iterator.hasNext()) {
                R region = iterator.next();

                if (region.contains(x, y, z))
                    results.add(region);
            }

            return results;
        }
    }

    /**
     * Register a region so it can be found in searches
     * and its events called if it is a player watcher.
     *
     * @param region  The Region to register.
     */
    public void register(R region) {
        PreCon.notNull(region);

        if (!region.isDefined() || !region.isWorldLoaded()) {
            NucMsg.debug("Failed to register region '{0}' with RegionManager because " +
                    "it's coords are undefined.", region.getName());
            return;
        }

        _regions.add(region);

        synchronized(_sync) {

            boolean isFormerListener = false;

            int xMax = region.getChunkX() + region.getChunkXWidth();
            int zMax = region.getChunkZ() + region.getChunkZWidth();

            for (int x= region.getChunkX(); x < xMax; x++) {
                for (int z= region.getChunkZ(); z < zMax; z++) {

                    //noinspection ConstantConditions
                    String key = getChunkKey(region.getWorld(), x, z);

                    if (region.isEventListener()) {

                        // add to listener regions map
                        OrderedRegions<R> regions = _listenerRegionsMap.get(key);
                        if (regions == null) {
                            regions = new OrderedRegions<R>(5);
                            _listenerRegionsMap.put(key, regions);
                        }
                        regions.add(region);
                    }
                    else {
                        isFormerListener = removeFromMap(_listenerRegionsMap, key, region);
                    }

                    // add to all regions map
                    Set<R> regions = _allRegionsMap.get(key);
                    if (regions == null) {
                        regions = new HashSet<>(5);
                        _allRegionsMap.put(key, regions);
                    }
                    regions.add(region);
                }
            }

            onRegister(region, isFormerListener);
        }
    }

    /**
     * Unregister a region and its events completely.
     *
     * <p>Called when a region is disposed.</p>
     *
     * @param region  The Region to unregister.
     */
    public void unregister(R region) {
        PreCon.notNull(region);

        if (!region.isDefined() || !region.isWorldLoaded())
            return;

        synchronized(_sync) {

            int xMax = region.getChunkX() + region.getChunkXWidth();
            int zMax = region.getChunkZ() + region.getChunkZWidth();

            for (int x= region.getChunkX(); x < xMax; x++) {
                for (int z= region.getChunkZ(); z < zMax; z++) {

                    //noinspection ConstantConditions
                    String key = getChunkKey(region.getWorld(), x, z);

                    removeFromMap(_listenerRegionsMap, key, region);
                    removeFromMap(_allRegionsMap, key, region);
                }
            }

            if (_regions.remove(region)) {
                onUnregister(region);
            }
        }
    }

    /**
     * Invoked after a region is registered.
     *
     * @param region            The region that was registered.
     * @param isFormerListener  True if the region was formerly registered as a listener
     *                          and has be re-registered as a non-listener.
     */
    protected void onRegister(R region, boolean isFormerListener) {}

    /**
     * Invoked after a region is un-registered.
     *
     * @param region  The region that was unregistered.
     */
    protected void onUnregister(R region) {}

    /*
     * Remove a region from a region map.
     */
    protected <T extends Set<R>> boolean removeFromMap(Map<String, T> map, String key, R region) {
        Set<R> regions = map.get(key);
        return regions != null && regions.remove(region);
    }

    /*
     * Get a regions chunk map key.
     */
    protected String getChunkKey(World world, int x, int z) {
        return world.getName() + '.' + String.valueOf(x) + '.' + String.valueOf(z);
    }
}
