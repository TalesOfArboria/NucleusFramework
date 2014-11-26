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

package com.jcwhatever.bukkit.generic.regions;

import com.jcwhatever.bukkit.generic.regions.data.IRegionMath;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Represents a region. Not intended to be implemented externally
 * unless the external implementation is not intended to be used
 * with GenericsLib's {@link RegionManager}.
 *
 * <p>For nearly all cases, the abstract class {@link Region} should be extended
 * or use one of the other abstract implementations that extend {@link Region}.</p>
 */
public interface IRegion extends IRegionMath, IRegionComparable {

    /**
     * Get the owning plugin.
     */
    Plugin getPlugin();

    /**
     * Get the name of the region.
     */
    String getName();

    /**
     * Get the name of the region in lower case.
     */
    String getSearchName();

    /**
     * Get the id of the region player owner.
     */
    @Nullable
    UUID getOwnerId();

    /**
     * Determine if the region has a player owner.
     */
    boolean hasOwner();

    /**
     * Set the player owner of the region.
     *
     * @param ownerId  The id of the player owner.
     *
     * @return True if the owner was set.
     */
    boolean setOwner(@Nullable UUID ownerId);

    /**
     * Find locations in the region that are made
     * of the specified {@code Material}.
     *
     * @param material  The material to find.
     */
    LinkedList<Location> find(Material material);

    /**
     * Get all chunks the region intersects with.
     */
    List<Chunk> getChunks();

    /**
     * Refresh all chunks the region intersects with.
     */
    void refreshChunks();

    /**
     * Determine if the region contains a block
     * of the specified {@code Material}
     *
     * @param material  The material to find.
     */
    boolean contains(Material material);

    /**
     * Determine if the region contains a location.
     *
     * @param loc  The location to check.
     */
    boolean contains(Location loc);

    /**
     * Determine if a region contains a location on
     * specific axis.
     *
     * @param loc  The location to check.
     * @param x    True to check the X axis.
     * @param y    True to check the Y axis.
     * @param z    True to check the Z axis.
     */
    boolean contains (Location loc, boolean x, boolean y, boolean z);

    /**
     * Determine if the region contains the specified
     * coordinates.
     *
     * @param x  The location X coordinates.
     * @param y  The location Y coordinates.
     * @param z  The location Z coordinates.
     */
    boolean contains(int x, int y, int z);

    /**
     * Remove all specified entity types from the region.
     *
     * @param itemTypes  The entity types to remove.
     */
    void removeEntities (Class<?>... itemTypes);

    /**
     * Get a meta value from the regions meta data store.
     *
     * @param key  The meta value key.
     * @param <T>  The meta value type.
     */
    <T> T getMeta (Object key);

    /**
     * Set a meta value from the regions meta data store.
     *
     * @param key    The meta value key.
     * @param value  The meta value type.
     */
    void setMeta (Object key, @Nullable Object value);

    /**
     * Determine if the region watches players to see
     * if they enter or leave.
     */
    boolean isPlayerWatcher ();

    /**
     * Add a transient event handler to the region.
     *
     * @param handler  The handler to add.
     */
    boolean addEventHandler(IRegionEventHandler handler);

    /**
     * Remove a transient event handler from the region.
     *
     * @param handler  The handler to add.
     */
    boolean removeEventHandler(IRegionEventHandler handler);

    /**
     * The same as calling {@code getClass} method except
     * in cases where the actual region is not represented by the
     * implementer (wrappers), in which case the encapsulated
     * regions class is returned.
     */
    Class<? extends IRegion> getRegionClass();
}
