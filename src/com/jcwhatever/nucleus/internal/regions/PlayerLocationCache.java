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

import com.jcwhatever.nucleus.internal.regions.PlayerLocationCache.CachedLocation;
import com.jcwhatever.nucleus.utils.performance.pool.SimpleCheckoutPool;
import com.jcwhatever.nucleus.utils.performance.pool.IPoolElementFactory;
import com.jcwhatever.nucleus.utils.performance.pool.IPoolRecycleHandler;

import org.bukkit.Location;

import java.util.UUID;

/**
 * Caches locations a player has been until they can be processed by
 * the player watcher in {@link InternalRegionManager}.
 *
 * <p>Keeps a pool of locations for reuse. The purpose of the pool is to prevent the old
 * generation memory space from filling with temporary objects due to excessive new object
 * creation.</p>
 */
class PlayerLocationCache extends SimpleCheckoutPool<CachedLocation> {

    private static final IPoolElementFactory<CachedLocation> ELEMENT_FACTORY =
            new IPoolElementFactory<CachedLocation>() {
                @Override
                public CachedLocation create() {
                    return new CachedLocation();
                }
            };

    private static final IPoolRecycleHandler<CachedLocation> RECYCLE_HANDLER =
            new IPoolRecycleHandler<CachedLocation>() {
                @Override
                public void onRecycle(CachedLocation element) {
                    element.setWorld(null);
                    element.reason = null;
                }
            };

    private UUID _playerId;

    public PlayerLocationCache() {
        super(75, ELEMENT_FACTORY, RECYCLE_HANDLER);
    }

    /**
     * Set the player owner of the cache.
     *
     * @param playerId The ID of the player.
     */
    public void setOwner(UUID playerId) {
        _playerId = playerId;
    }

    /**
     * Get the caches owning player ID.
     */
    public UUID getPlayerId() {
        return _playerId;
    }

    /**
     * Add a location to cache.
     *
     * <p>Uses a location from a pool. Copy the location values into the returned
     * {@link org.bukkit.Location}.</p>
     *
     * @param reason  The reason the location is being added.
     */
    public Location add(RegionEventReason reason) {

        synchronized (this) {

            if (reason == RegionEventReason.JOIN_SERVER ||
                    reason == RegionEventReason.TELEPORT) {
                getCheckedOut().clear();
            }

            CachedLocation location = checkout();
            assert location != null;

            location.reason = reason;

            return location;
        }
    }

    /**
     * Represents a location a player has been as well as
     * the reason the location was added.
     */
    static class CachedLocation extends Location {

        RegionEventReason reason;

        CachedLocation() {
            super(null, 0, 0, 0);
        }

        /**
         * Get the reason the location was added.
         */
        public RegionEventReason getReason() {
            return reason;
        }
    }
}
