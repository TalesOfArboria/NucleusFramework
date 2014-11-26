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

import com.jcwhatever.bukkit.generic.regions.Region.PriorityType;
import com.jcwhatever.bukkit.generic.regions.Region.RegionPriority;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * A container for a region that prevents setter operations with
 * a few exceptions. The owner of the region, transient event handlers
 * as well as meta can still be modified.
 *
 * <p>Allows other plugins to retrieve region info without giving full access
 * to a region, which could cause issues with the regions owning plugin.</p>
 */
public class ReadOnlyRegion implements IRegion {

    private IRegion _region;

    /**
     * Constructor.
     *
     * @param region The region to encapsulate.
     */
    public ReadOnlyRegion(IRegion region) {
        PreCon.notNull(region);

        _region = region;
    }

    @Override
    public RegionPriority getPriority(PriorityType priorityType) {
        return _region.getPriority(priorityType);
    }

    /**
     * Get the owning plugin.
     */
    @Override
    public Plugin getPlugin () {
        return _region.getPlugin();
    }

    /**
     * Get the name of the region.
     */
    @Override
    public String getName () {
        return _region.getName();
    }

    /**
     * Get the name of the region in lower case.
     */
    @Override
    public String getSearchName () {
        return _region.getSearchName();
    }

    /**
     * Get the id of the region player owner.
     */
    @Override
    @Nullable
    public UUID getOwnerId () {
        return _region.getOwnerId();
    }

    /**
     * Determine if the region has a player owner.
     */
    @Override
    public boolean hasOwner () {
        return _region.hasOwner();
    }

    /**
     * Set the player owner of the region.
     *
     * @param ownerId  The id of the player owner.
     */
    @Override
    public boolean setOwner(@Nullable UUID ownerId) {
        return _region.setOwner(ownerId);
    }

    /**
     * Get the world the region is in.
     */
    @Override
    @Nullable
    public World getWorld () {
        return _region.getWorld();
    }

    /**
     * Get the regions first cuboid point location.
     */
    @Override
    @Nullable
    public Location getP1 () {
        return _region.getP1();
    }

    /**
     * Get the regions seconds cuboid point location.
     */
    @Override
    @Nullable
    public Location getP2 () {
        return _region.getP2();
    }

    /**
     * Get the region cuboid point location that
     * is in the lower portion of the region.
     */
    @Override
    @Nullable
    public Location getLowerPoint () {
        return _region.getLowerPoint();
    }

    /**
     * Get the region cuboid point location that
     * is in the upper portion of the region.
     */
    @Override
    @Nullable
    public Location getUpperPoint () {
        return _region.getUpperPoint();
    }

    /**
     * Get the regions X coordinates with the smallest value.
     */
    @Override
    public int getXStart () {
        return _region.getXStart();
    }

    /**
     * Get the regions Y coordinates with the smallest value.
     */
    @Override
    public int getYStart () {
        return _region.getYStart();
    }

    /**
     * Get the regions Z coordinates with the smallest value.
     */
    @Override
    public int getZStart () {
        return _region.getZStart();
    }

    /**
     * Get the regions X coordinates with the largest value.
     */
    @Override
    public int getXEnd () {
        return _region.getXEnd();
    }

    /**
     * Get the regions Y coordinates with the largest value.
     */
    @Override
    public int getYEnd () {
        return _region.getYEnd();
    }

    /**
     * Get the regions Z coordinates with the largest value.
     */
    @Override
    public int getZEnd () {
        return _region.getZEnd();
    }

    /**
     * Get the regions X axis width.
     */
    @Override
    public int getXWidth () {
        return _region.getXWidth();
    }

    /**
     * Get the regions Z axis width.
     */
    @Override
    public int getZWidth () {
        return _region.getZWidth();
    }

    /**
     * Get the regions Y axis height.
     */
    @Override
    public int getYHeight () {
        return _region.getYHeight();
    }

    /**
     * Get the number of blocks that comprise
     * the X axis width.
     */
    @Override
    public int getXBlockWidth () {
        return _region.getXBlockWidth();
    }

    /**
     * Get the number of blocks that comprise
     * the Z axis width.
     */
    @Override
    public int getZBlockWidth () {
        return _region.getZBlockWidth();
    }

    /**
     * Get the number of blocks that comprise
     * the Y axis height.
     */
    @Override
    public int getYBlockHeight () {
        return _region.getYBlockHeight();
    }

    /**
     * Get the total volume of the region.
     */
    @Override
    public long getVolume () {
        return _region.getVolume();
    }

    /**
     * Find locations in the region that are made
     * of the specified {@code Material}.
     *
     * @param material  The material to find.
     */
    @Override
    public LinkedList<Location> find (Material material) {
        return _region.find(material);
    }

    /**
     * Get all chunks the region intersects with.
     */
    @Override
    public List<Chunk> getChunks () {
        return _region.getChunks();
    }

    /**
     * Refresh all chunks the region intersects with.
     */
    @Override
    public void refreshChunks () {
        _region.refreshChunks();
    }

    /**
     * Determine if the region has both its
     * cuboid points set.
     */
    @Override
    public boolean isDefined () {
        return _region.isDefined();
    }

    /**
     * Determine if the region contains a block
     * of the specified {@code Material}
     *
     * @param material  The material to find.
     */
    @Override
    public boolean contains (Material material) {
        return _region.contains(material);
    }

    /**
     * Determine if the region contains a location.
     *
     * @param loc  The location to check.
     */
    @Override
    public boolean contains (Location loc) {
        return _region.contains(loc);
    }

    /**
     * Determine if a region contains a location on
     * specific axis.
     *
     * @param loc  The location to check.
     * @param x    True to check the X axis.
     * @param y    True to check the Y axis.
     * @param z    True to check the Z axis.
     */
    @Override
    public boolean contains (Location loc, boolean x, boolean y, boolean z) {
        return _region.contains(loc, x, y, z);
    }

    /**
     * Remove all specified entity types from the region.
     *
     * @param itemTypes  The entity types to remove.
     */
    @Override
    public void removeEntities (Class<?>... itemTypes) {
        _region.removeEntities(itemTypes);
    }

    /**
     * Get the center location of the region.
     */
    @Override
    @Nullable
    public Location getCenter () {
        return _region.getCenter();
    }

    /**
     * Get the X coordinates of the chunk
     * with the smallest X value.
     */
    @Override
    public int getChunkX () {
        return _region.getChunkX();
    }

    /**
     * Get the Z coordinates of the chunk
     * with the smallest Z value.
     */
    @Override
    public int getChunkZ () {
        return _region.getChunkZ();
    }

    /**
     * Get the number of chunks that comprise
     * the chunk width on the X axis.
     */
    @Override
    public int getChunkXWidth () {
        return _region.getChunkXWidth();
    }

    /**
     * Get the number of chunks that comprise
     * the chunk width on the Z axis.
     */
    @Override
    public int getChunkZWidth () {
        return _region.getChunkZWidth();
    }

    /**
     * Determine if the region is 1 block tall.
     */
    @Override
    public boolean isFlatHorizontal () {
        return _region.isFlatHorizontal();
    }

    /**
     * Determine if the region is 1 block wide on
     * either the X axis or Z axis and is
     * not 1 block tall.
     */
    @Override
    public boolean isFlatVertical () {
        return _region.isFlatVertical();
    }

    /**
     * Get a meta value from the regions meta data store.
     *
     * @param key  The meta value key.
     *
     * @param <T>  The meta value type.
     */
    @Override
    public <T> T getMeta (Object key) {
        return _region.getMeta(key);
    }

    /**
     * Set a meta value from the regions meta data store.
     *
     * @param key    The meta value key.
     * @param value  The meta value type.
     */
    @Override
    public void setMeta (Object key, @Nullable Object value) {
        _region.setMeta(key, value);
    }

    /**
     * Determine if the region watches players to see
     * if they enter or leave.
     */
    @Override
    public boolean isPlayerWatcher () {
        return _region.isPlayerWatcher();
    }

    /**
     * Add a transient event handler to the region.
     *
     * @param handler  The handler to add.
     */
    @Override
    public boolean addEventHandler(IRegionEventHandler handler) {
        return _region.addEventHandler(handler);
    }

    /**
     * Remove a transient event handler from the region.
     *
     * @param handler  The handler to add.
     */
    @Override
    public boolean removeEventHandler(IRegionEventHandler handler) {
        return _region.removeEventHandler(handler);
    }

    /**
     * Get the regions hash code.
     */
    @Override
    public int hashCode() {
        return _region.hashCode();
    }

    /**
     * Determine if an object is the containers region or
     * another container with the same region.
     */
    @Override
    public boolean equals(Object obj) {
        IRegion region;

        if (obj instanceof ReadOnlyRegion) {
            region = ((ReadOnlyRegion) obj)._region;
        }
        else if (obj instanceof IRegion) {
            region = (IRegion)obj;
        }
        else {
            return false;
        }

        return region.equals(_region);
    }

    /**
     * Get the class of the region.
     */
    @Override
    public Class<? extends IRegion> getRegionClass() {
        return _region.getRegionClass();
    }

    /**
     * Get the region.
     *
     * <p>For internal use.</p>
     */
    Region getHandle() {
        if (!(_region instanceof Region)) {
            throw new AssertionError();
        }

        return (Region)_region;
    }

}
