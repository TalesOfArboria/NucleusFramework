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

import com.jcwhatever.nucleus.providers.regionselect.IRegionSelection;
import com.jcwhatever.nucleus.regions.data.CuboidPoint;
import com.jcwhatever.nucleus.regions.data.RegionShape;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.ChunkCoords;
import com.jcwhatever.nucleus.utils.coords.IChunkCoords;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.utils.coords.SyncLocation;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Contains pre-calculated variables regarding a cuboid region
 * of space as defined by two region locations.
 */
public class SimpleRegionSelection implements IRegionSelection {

    private final Object _sync = new Object();

    private SyncLocation _p1;
    private SyncLocation _p2;
    private SyncLocation _lowerPoint;
    private SyncLocation _upperPoint;

    private int _startX;
    private int _startY;
    private int _startZ;

    private int _endX;
    private int _endY;
    private int _endZ;

    private int _xWidth;
    private int _zWidth;
    private int _yHeight;
    private int _xBlockWidth;
    private int _zBlockWidth;
    private int _yBlockHeight;
    private long _volume;

    private int _chunkX;
    private int _chunkZ;
    private int _chunkXWidth;
    private int _chunkZWidth;
    private List<IChunkCoords> _chunks;

    private Location _center;
    private RegionShape _flatness = RegionShape.CUBOID;

    /**
     * Empty Constructor.
     */
    protected SimpleRegionSelection() {}

    /**
     * Constructor.
     *
     * @param p1  The first point location.
     * @param p2  The second point location.
     */
    public SimpleRegionSelection(Location p1, Location p2) {
        PreCon.notNull(p1);
        PreCon.notNull(p2);

        setCoords(p1, p2);
    }

    @Override
    public final boolean isDefined() {
        return _p1 != null && _p2 != null && _p1.getWorldName() != null;
    }

    @Override
    @Nullable
    public final World getWorld() {

        synchronized (_sync) {

            if (isDefined()) {
                return _p1.getWorld();
            }

            return null;
        }
    }

    @Nullable
    @Override
    public String getWorldName() {

        if (_p1 == null)
            return null;

        return _p1.getWorldName();
    }

    @Override
    public boolean isWorldLoaded() {
        return isDefined() && _p1.getWorldName() != null && getWorld() != null;
    }

    @Override
    @Nullable
    public final Location getP1() {
        if (_p1 == null)
            return null;

        synchronized (_sync) {
            return _p1.getBukkitLocation();
        }
    }

    @Override
    @Nullable
    public Location getP1(Location location) {
        PreCon.notNull(location);

        if (_p1 == null)
            return null;

        return LocationUtils.copy(_p1, location);
    }

    @Override
    @Nullable
    public final Location getP2() {
        if (_p2 == null)
            return null;

        synchronized (_sync) {
            return _p2.getBukkitLocation();
        }
    }

    @Override
    @Nullable
    public Location getP2(Location location) {
        PreCon.notNull(location);

        if (_p2 == null)
            return null;

        return LocationUtils.copy(_p2, location);
    }

    @Override
    @Nullable
    public final Location getLowerPoint() {
        if (_lowerPoint == null)
            return null;

        return _lowerPoint.getBukkitLocation();
    }

    @Override
    @Nullable
    public Location getLowerPoint(Location location) {
        PreCon.notNull(location);

        if (_lowerPoint == null)
            return null;

        return LocationUtils.copy(_lowerPoint, location);
    }

    @Override
    @Nullable
    public final Location getUpperPoint() {
        if (_upperPoint == null)
            return null;

        return _upperPoint.getBukkitLocation();
    }

    @Override
    public Location getUpperPoint(Location location) {
        PreCon.notNull(location);

        if (_upperPoint == null)
            return null;

        return LocationUtils.copy(_upperPoint, location);
    }

    @Override
    public final int getXStart() {
        return _startX;
    }

    @Override
    public final int getYStart() {
        return _startY;
    }

    @Override
    public final int getZStart() {
        return _startZ;
    }

    @Override
    public final int getXEnd() {
        return _endX;
    }

    @Override
    public final int getYEnd() {
        return _endY;
    }

    @Override
    public final int getZEnd() {
        return _endZ;
    }

    @Override
    public final int getXWidth() {
        return _xWidth;
    }

    @Override
    public final int getZWidth() {
        return _zWidth;
    }

    @Override
    public final int getYHeight() {
        return _yHeight;
    }

    @Override
    public final int getXBlockWidth() {
        return _xBlockWidth;
    }

    @Override
    public final int getZBlockWidth() {
        return _zBlockWidth;
    }

    @Override
    public final int getYBlockHeight() {
        return _yBlockHeight;
    }

    @Override
    public final long getVolume() {
        return _volume;
    }

    @Override
    @Nullable
    public final Location getCenter() {
        if (_center == null)
            return null;

        return LocationUtils.copy(_center);
    }

    @Override
    @Nullable
    public Location getCenter(Location location) {
        PreCon.notNull(location);

        if (_center == null)
            return null;

        return LocationUtils.copy(_center, location);
    }

    @Override
    public final int getChunkX() {
        return _chunkX;
    }

    @Override
    public final int getChunkZ() {
        return _chunkZ;
    }

    @Override
    public final int getChunkXWidth() {
        return _chunkXWidth;
    }

    @Override
    public final int getChunkZWidth() {
        return _chunkZWidth;
    }

    @Override
    public final Collection<IChunkCoords> getChunkCoords() {

        if (_chunks != null)
            return new ArrayList<>(_chunks);

        return getChunkCoords(new ArrayList<IChunkCoords>(1));
    }

    @Override
    public <T extends Collection<IChunkCoords>> T getChunkCoords(T output) {
        PreCon.notNull(output);

        if (getWorld() == null || !isDefined())
            return output;

        if (_chunks != null) {
            output.addAll(_chunks);
            return output;
        }

        synchronized (_sync) {

            int startX = _chunkX;
            int endX = _chunkX + _chunkXWidth - 1;

            int startZ = _chunkZ;
            int endZ = _chunkZ + _chunkZWidth - 1;

            List<IChunkCoords> result = new ArrayList<IChunkCoords>((endX - startX) * (endZ - startZ) + 1);

            for (int x = startX; x <= endX; x++) {
                for (int z = startZ; z <= endZ; z++) {
                    result.add(new ChunkCoords(_p1.getWorldName(), x, z));
                }
            }

            output.addAll(_chunks = result);
            return output;
        }
    }

    @Override
    public final RegionShape getShape() {
        return _flatness;
    }

    @Override
    public final boolean contains(Location loc) {

        if (!isDefined())
            return false;

        if (loc.getWorld() == null)
            return false;

        if (!loc.getWorld().equals(getWorld()))
            return false;

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        return contains(x, y, z);
    }

    @Override
    public final boolean contains(int x, int y, int z) {
        synchronized (_sync) {

            _sync.notifyAll();

            return x >= getXStart() && x <= getXEnd() &&
                    y >= getYStart() && y <= getYEnd() &&
                    z >= getZStart() && z <= getZEnd();
        }
    }

    @Override
    public final boolean contains(Location loc, boolean cx, boolean cy, boolean cz) {

        if (!isDefined())
            return false;

        synchronized (_sync) {

            if (!loc.getWorld().equals(getWorld()))
                return false;

            if (cx) {
                int x = loc.getBlockX();
                if (x < getXStart() || x > getXEnd())
                    return false;
            }

            if (cy) {
                int y = loc.getBlockY();
                if (y < getYStart() || y > getYEnd())
                    return false;
            }

            if (cz) {
                int z = loc.getBlockZ();
                if (z < getZStart() || z > getZEnd())
                    return false;
            }

            _sync.notifyAll();

            return true;
        }
    }

    @Override
    public final boolean intersects(Chunk chunk) {
        PreCon.notNull(chunk);

        return isDefined() &&
                chunk.getWorld().equals(getWorld()) &&
                intersects(chunk.getX(), chunk.getZ());
    }

    @Override
    public final boolean intersects(int chunkX, int chunkZ) {

        return getChunkX() <= chunkX && (getChunkX() + getChunkXWidth() - 1) >= chunkX &&
                getChunkZ() <= chunkZ && (getChunkZ() + getChunkZWidth() - 1) >= chunkZ;
    }

    @Override
    public Location getPoint(CuboidPoint point) {
        PreCon.notNull(point);

        return point.getLocation(this);
    }

    @Nullable
    @Override
    public CuboidPoint getPoint(Location location) {
        PreCon.notNull(location);

        return CuboidPoint.getCuboidPoint(location, this);
    }

    /**
     * Get the synchronization object.
     */
    protected final Object getSync() {
        return _sync;
    }

    /**
     * Get a reference to the underlying P1 {@link SyncLocation}
     * coordinates.
     */
    protected SyncLocation getSyncP1() {
        return _p1;
    }

    /**
     * Get a reference to the underlying P2 {@link SyncLocation}
     * coordinates.
     */
    protected SyncLocation getSyncP2() {
        return _p2;
    }

    /**
     * Set the regions cuboid point coordinates.
     *
     * @param p1  The first point location.
     * @param p2  The second point location.
     */
    protected void setCoords(Location p1, Location p2) {
        PreCon.notNull(p1);
        PreCon.notNull(p2);

        World p1World = p1.getWorld();
        World p2World = p2.getWorld();

        if (p1World != null && !p1World.equals(p2World) ||
                p2World != null && !p2World.equals(p1World)) {
            throw new IllegalArgumentException("Both region points must be from the same world.");
        }

        double lowerX = Math.min(p1.getX(), p2.getX());
        double lowerY = Math.min(p1.getY(), p2.getY());
        double lowerZ = Math.min(p1.getZ(), p2.getZ());

        double upperX = Math.max(p1.getX(), p2.getX());
        double upperY = Math.max(p1.getY(), p2.getY());
        double upperZ = Math.max(p1.getZ(), p2.getZ());

        String worldName = null;

        if (p1 instanceof SyncLocation) {
            worldName = ((SyncLocation) p1).getWorldName();
        }
        else if (p1.getWorld() != null) {
            worldName = p1.getWorld().getName();
        }

        _p1 = new SyncLocation(worldName, p1.getX(), p1.getY(), p1.getZ(), 0F, 0F);
        _p2 = new SyncLocation(worldName, p2.getX(), p2.getY(), p2.getZ(), 0F, 0F);
        _lowerPoint = new SyncLocation(worldName, lowerX, lowerY, lowerZ, 0F, 0F);
        _upperPoint = new SyncLocation(worldName, upperX, upperY, upperZ, 0F, 0F);
        updateMath();
    }

    /*
     * Update region math variables
     */
    protected void updateMath() {

        if (_p1 == null || _p2 == null)
            return;

        synchronized (_sync) {

            _startX = Math.min(_p1.getBlockX(), _p2.getBlockX());
            _startY = Math.min(_p1.getBlockY(), _p2.getBlockY());
            _startZ = Math.min(_p1.getBlockZ(), _p2.getBlockZ());

            _endX = Math.max(_p1.getBlockX(), _p2.getBlockX());
            _endY = Math.max(_p1.getBlockY(), _p2.getBlockY());
            _endZ = Math.max(_p1.getBlockZ(), _p2.getBlockZ());

            _xWidth = (int)Math.abs(_p1.getX() - _p2.getX());
            _zWidth = (int)Math.abs(_p1.getZ() - _p2.getZ());
            _yHeight = (int)Math.abs(_p1.getY() - _p2.getY());

            _xBlockWidth = _xWidth + 1;
            _zBlockWidth = _zWidth + 1;
            _yBlockHeight = _yHeight + 1;

            _volume = _xWidth * _zWidth * _yHeight;

            if (getWorld() != null) {
                double xCenter = (double) _startX + (_xBlockWidth / 2.0D);
                double yCenter = (double) _startY + (_yBlockHeight / 2.0D);
                double zCenter = (double) _startZ + (_zBlockWidth / 2.0D);

                _center = new Location(getWorld(), xCenter, yCenter, zCenter);
            }

            _chunkX = Math.min(_p1.getBlockX(), _p2.getBlockX()) == _p1.getBlockX()
                    ? (int)Math.floor(_p1.getBlockX() / 16.0D)
                    : (int)Math.floor(_p2.getBlockX() / 16.0D);

            _chunkZ = Math.min(_p1.getBlockZ(), _p2.getBlockZ()) == _p1.getBlockZ()
                    ? (int)Math.floor(_p1.getBlockZ() / 16.0D)
                    : (int)Math.floor(_p2.getBlockZ() / 16.0D);

            int chunkEndX = Math.max(_p1.getBlockX(), _p2.getBlockX()) == _p1.getBlockX()
                    ? (int)Math.floor(_p1.getBlockX() / 16.0D)
                    : (int)Math.floor(_p2.getBlockX() / 16.0D);

            int chunkEndZ = Math.max(_p1.getBlockZ(), _p2.getBlockZ()) == _p1.getBlockZ()
                    ? (int)Math.floor(_p1.getBlockZ() / 16.0D)
                    : (int)Math.floor(_p2.getBlockZ() / 16.0D);

            _chunkXWidth = chunkEndX - _chunkX + 1;
            _chunkZWidth = chunkEndZ - _chunkZ + 1;

            _chunks = null;

            _flatness = RegionShape.CUBOID;

            if (getXBlockWidth() == 1) { // west/east

                if (getZBlockWidth() == 1) { // north/south

                    _flatness = getYBlockHeight() == 1
                            ? RegionShape.POINT
                            : RegionShape.VERTICAL_LINE;
                }
                else {
                    _flatness = getYBlockHeight() == 1
                            ? RegionShape.NORTH_SOUTH_LINE
                            : RegionShape.FLAT_WEST_EAST;
                }
            }
            else if (getZBlockWidth() == 1) { // north/south
                _flatness = getYBlockHeight() == 1
                        ? RegionShape.WEST_EAST_LINE
                        : RegionShape.FLAT_NORTH_SOUTH;
            }
            else if (getYBlockHeight() == 1) {
                _flatness = RegionShape.FLAT_HORIZONTAL;
            }
        }
    }
}
