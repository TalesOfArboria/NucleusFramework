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

import com.jcwhatever.nucleus.regions.options.RegionPriority.RegionReason;

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

    private final List<CachedLocation> _locationPool = new ArrayList<>(100);
    private final LinkedList<CachedLocation> _cached = new LinkedList<>();
    private final PlayerLocations _removed = new PlayerLocations();

    private volatile boolean _isRemovedDiscarded = true;

    // The index location to check for an unused pooled location.
    private volatile int _poolIndex = 0;

    // the min index the _poolIndex can be reset to, values below are in use
    // by PlayerLocations (_removed) instance.
    private volatile int _minIndex = 0;

    /**
     * Determine if the cache is empty.
     */
    public boolean isEmpty() {

        boolean isEmpty;

        synchronized (_cached) {
            isEmpty = _cached.isEmpty();
        }

        if (isEmpty) {

            if (_isRemovedDiscarded)
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
        synchronized (_cached) {
            _cached.clear();
        }
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
        synchronized (_cached) {

            if (reason == RegionReason.JOIN_SERVER ||
                    reason == RegionReason.TELEPORT) {
                clear();
            }
            _cached.addLast(location);
        }
        return location;
    }

    /**
     * Determine if {@link #canRemoveAll} can be invoked.
     */
    public boolean canRemoveAll() {
        return _isRemovedDiscarded;
    }

    /**
     * Remove all cached locations and return them in a {@link PlayerLocations} instance.
     *
     * <p>Since locations are reused from a pool, only one {@link PlayerLocations} instance
     * can be used per {@link PlayerLocationCache} at one time. The current instance must be
     * discarded before {@link #removeAll} can be invoked again.</p>
     */
    public PlayerLocations removeAll() {
        if (!_isRemovedDiscarded)
            throw new IllegalStateException("The PlayerLocations instance is already in use.");

        _removed.fill();
        _minIndex = _poolIndex;
        clear();

        return _removed;
    }

    /*
     * Get an available location from the pool of locations.
     */
    private CachedLocation getPooledLocation(RegionReason reason) {

        synchronized (_locationPool) {

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
    }

    /**
     * Holds cached locations removed from the parent cache.
     *
     * <p>Can only be used for one operation at a time.</p>
     */
    class PlayerLocations {

        private LinkedList<CachedLocation> locations = new LinkedList<>();

        void fill() {
            _isRemovedDiscarded = false;
            locations.clear();
            synchronized (_cached) {
                locations.addAll(_cached);
            }
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
         * Recycle the snapshot.
         */
        public void recycle() {
            _minIndex = 0;
            _isRemovedDiscarded = true;
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
