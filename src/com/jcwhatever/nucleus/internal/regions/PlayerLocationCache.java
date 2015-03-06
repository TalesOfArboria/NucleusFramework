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

import com.jcwhatever.nucleus.regions.Region.RegionReason;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Caches locations a player has been until they can be processed by
 * the player watcher in {@link InternalRegionManager}.
 *
 * <p>Keeps a pool of locations for reuse to prevent excessive
 * new object generation.</p>
 */
class PlayerLocationCache {

    private final List<CachedLocation> _locationPool = new ArrayList<>(20);
    private final LinkedList<CachedLocation> _cached = new LinkedList<>();

    private int _poolIndex = 0;
    private int _minIndex = 0;
    private PlayerLocations _removed;

    /**
     * Determine if the cache is empty.
     */
    public boolean isEmpty() {
        if (_cached.isEmpty()) {

            if (_removed == null)
                _minIndex = 0;

            _poolIndex = _minIndex;

            return true;
        }
        return false;
    }

    /**
     * Clear cached locations.
     */
    public void clear() {
        _poolIndex = _minIndex;
        _cached.clear();
    }

    /**
     * Add a location to cache.
     *
     * <p>Uses a location from a pool. Copy the location values into the returned
     * {@link org.bukkit.Location}.</p>
     *
     * @param reason  The reason the location is being added.
     */
    public Location add(RegionReason reason) {
        CachedLocation location = getPooledLocation(reason);
        _cached.addLast(location);
        return location;
    }

    /**
     * Remove all cached locations and return them in a {@link PlayerLocations} instance.
     *
     * <p>Since locations are reused from a pool, only one {@link PlayerLocations} instance
     * can exist per {@link PlayerLocationCache} at one time. The current instance must be
     * discarded before another one can be created.</p>
     */
    public PlayerLocations removeAll() {
        if (_removed != null)
            throw new IllegalStateException("Only one snapshot can exist per PlayerLocationCache.");

        _minIndex = _poolIndex;
        _removed = new PlayerLocations();
        clear();

        return _removed;
    }

    /*
     * Get an available location from the pool of locations.
     */
    private CachedLocation getPooledLocation(RegionReason reason) {

        if (_poolIndex >= _locationPool.size()) {

            // add a new location to the pool (expand pool)
            CachedLocation location = new CachedLocation(reason);
            _locationPool.add(location);
            return location;
        }

        // use a location from the pool
        CachedLocation result = _locationPool.get(_poolIndex);
        result.reason = reason;
        _poolIndex++;

        return result;
    }

    /**
     * Holds cached locations removed from the parent cache.
     *
     * <p>There can only be a single instance in use to prevent
     * conflicts with pool locations which are recycled.</p>
     */
    class PlayerLocations {

        private LinkedList<CachedLocation> locations;

        PlayerLocations() {
            locations = new LinkedList<>(_cached);
        }

        /**
         * Determine if the snapshot is empty.
         */
        public boolean isEmpty() {
            return locations.isEmpty();
        }

        /**
         * Remove and return the first item.
         */
        public CachedLocation remove() {
            return locations.removeFirst();
        }

        /**
         * Discard the snapshot so another can be created.
         */
        public void discard() {
            _minIndex = 0;
            _removed = null;
        }
    }

    /**
     * Represents a location a player has been as well as
     * the reason the location was added.
     */
    static class CachedLocation extends Location {

        RegionReason reason;

        CachedLocation(RegionReason reason) {
            super(null, 0, 0, 0);

            this.reason = reason;
        }

        /**
         * Get the reason the location was added.
         */
        public RegionReason getReason() {
            return reason;
        }
    }
}
