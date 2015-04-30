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


package com.jcwhatever.nucleus.regions;

import com.jcwhatever.nucleus.mixins.IReadOnly;
import com.jcwhatever.nucleus.regions.data.CuboidPoint;
import com.jcwhatever.nucleus.regions.data.RegionShape;
import com.jcwhatever.nucleus.regions.options.RegionEventPriority;
import com.jcwhatever.nucleus.regions.options.RegionEventPriority.PriorityType;
import com.jcwhatever.nucleus.utils.MetaStore;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.IChunkCoords;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
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
public class ReadOnlyRegion implements IRegion, IReadOnly {

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
    public int getPriority() {
        return _region.getPriority();
    }

    @Override
    public RegionEventPriority getEventPriority(PriorityType priorityType) {
        return _region.getEventPriority(priorityType);
    }

    @Override
    public Plugin getPlugin () {
        return _region.getPlugin();
    }

    @Override
    public String getName () {
        return _region.getName();
    }

    @Override
    public String getSearchName () {
        return _region.getSearchName();
    }

    @Override
    @Nullable
    public UUID getOwnerId () {
        return _region.getOwnerId();
    }

    @Override
    public boolean hasOwner () {
        return _region.hasOwner();
    }

    @Override
    public boolean setOwner(@Nullable UUID ownerId) {
        return _region.setOwner(ownerId);
    }

    @Override
    public MetaStore getMeta() {
        return _region.getMeta();
    }

    @Override
    @Nullable
    public World getWorld () {
        return _region.getWorld();
    }

    @Nullable
    @Override
    public String getWorldName() {
        return _region.getWorldName();
    }

    @Override
    public boolean isWorldLoaded() {
        return _region.isWorldLoaded();
    }

    @Override
    @Nullable
    public Location getP1 () {
        return _region.getP1();
    }

    @Nullable
    @Override
    public Location getP1(Location location) {
        return _region.getP1(location);
    }

    @Override
    @Nullable
    public Location getP2 () {
        return _region.getP2();
    }

    @Nullable
    @Override
    public Location getP2(Location location) {
        return _region.getP2(location);
    }

    @Override
    @Nullable
    public Location getLowerPoint () {
        return _region.getLowerPoint();
    }

    @Nullable
    @Override
    public Location getLowerPoint(Location location) {
        return _region.getLowerPoint(location);
    }

    @Override
    @Nullable
    public Location getUpperPoint () {
        return _region.getUpperPoint();
    }

    @Nullable
    @Override
    public Location getUpperPoint(Location location) {
        return _region.getUpperPoint(location);
    }

    @Override
    public int getXStart () {
        return _region.getXStart();
    }

    @Override
    public int getYStart () {
        return _region.getYStart();
    }

    @Override
    public int getZStart () {
        return _region.getZStart();
    }

    @Override
    public int getXEnd () {
        return _region.getXEnd();
    }

    @Override
    public int getYEnd () {
        return _region.getYEnd();
    }

    @Override
    public int getZEnd () {
        return _region.getZEnd();
    }

    @Override
    public int getXWidth () {
        return _region.getXWidth();
    }

    @Override
    public int getZWidth () {
        return _region.getZWidth();
    }

    @Override
    public int getYHeight () {
        return _region.getYHeight();
    }

    @Override
    public int getXBlockWidth () {
        return _region.getXBlockWidth();
    }

    @Override
    public int getZBlockWidth () {
        return _region.getZBlockWidth();
    }

    @Override
    public int getYBlockHeight () {
        return _region.getYBlockHeight();
    }

    @Override
    public long getVolume () {
        return _region.getVolume();
    }

    @Override
    public Collection<Location> find (Material material) {
        return _region.find(material);
    }

    @Override
    public Collection<IChunkCoords> getChunkCoords() {
        return _region.getChunkCoords();
    }

    @Override
    public void refreshChunks () {
        _region.refreshChunks();
    }

    @Override
    public boolean isDefined () {
        return _region.isDefined();
    }

    @Override
    public boolean contains (Material material) {
        return _region.contains(material);
    }

    @Override
    public boolean contains (Location loc) {
        return _region.contains(loc);
    }

    @Override
    public boolean contains (Location loc, boolean x, boolean y, boolean z) {
        return _region.contains(loc, x, y, z);
    }

    @Override
    public boolean contains(int x, int y, int z) {
        return _region.contains(x, y, z);
    }

    @Override
    public boolean intersects(Chunk chunk) {
        return _region.intersects(chunk);
    }

    @Override
    public boolean intersects(int chunkX, int chunkZ) {
        return _region.intersects(chunkX, chunkZ);
    }

    @Override
    public Location getPoint(CuboidPoint point) {
        return _region.getPoint(point);
    }

    @Nullable
    @Override
    public CuboidPoint getPoint(Location location) {
        return _region.getPoint(location);
    }

    @Override
    public void removeEntities (Class<?>... itemTypes) {
        _region.removeEntities(itemTypes);
    }

    @Override
    @Nullable
    public Location getCenter () {
        return _region.getCenter();
    }

    @Nullable
    @Override
    public Location getCenter(Location location) {
        return _region.getCenter(location);
    }

    @Override
    public int getChunkX () {
        return _region.getChunkX();
    }

    @Override
    public int getChunkZ () {
        return _region.getChunkZ();
    }

    @Override
    public int getChunkXWidth () {
        return _region.getChunkXWidth();
    }

    @Override
    public int getChunkZWidth () {
        return _region.getChunkZWidth();
    }

    @Override
    public RegionShape getShape() {
        return _region.getShape();
    }

    @Override
    public boolean isEventListener() {
        return _region.isEventListener();
    }

    @Override
    public IRegionEventListener getEventListener() {
        return _region.getEventListener();
    }

    @Override
    public boolean addEventHandler(IRegionEventHandler handler) {
        return _region.addEventHandler(handler);
    }

    @Override
    public boolean removeEventHandler(IRegionEventHandler handler) {
        return _region.removeEventHandler(handler);
    }

    @Override
    public int hashCode() {
        return _region.hashCode();
    }

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

    @Override
    public boolean isDisposed() {
        return false;
    }

    @Override
    public void dispose() {
        throw new RuntimeException("Cannot dispose a read only region.");
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }
}
