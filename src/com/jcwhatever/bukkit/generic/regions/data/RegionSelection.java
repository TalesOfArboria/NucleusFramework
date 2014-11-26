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

package com.jcwhatever.bukkit.generic.regions.data;

import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nullable;

/**
 * Contains pre-calculated variables regarding a cuboid region
 * of space as defined by two region locations.
 */
public class RegionSelection implements IRegionSelection {

    protected final Object _sync = new Object();

    private Location _p1;
    private Location _p2;
    private Location _lastP1;
    private Location _lastP2;

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

    private Location _center;

    protected enum RegionPoint {
        P1,
        P2
    }

    /**
     * Empty Constructor.
     */
    protected RegionSelection() {}

    /**
     * Constructor.
     *
     * @param p1  The first point location.
     * @param p2  The second point location.
     */
    public RegionSelection(Location p1, Location p2) {
        PreCon.notNull(p1);
        PreCon.notNull(p2);

        setCoords(p1, p2);
    }

    /**
     * Determine if the regions cuboid points have been set.
     */
    @Override
    public final boolean isDefined() {
        return _p1 != null && _p2 != null;
    }

    /**
     * Get the world the region is in.
     */
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

    /**
     * Get the cuboid regions first point location.
     *
     * <p>Note: If the location is set but the world it's for is not
     * loaded, the World value of location may be null.</p>
     */
    @Override
    @Nullable
    public final Location getP1() {
        if (_p1 == null)
            return null;

        synchronized (_sync) {
            return _p1.clone();
        }
    }

    /**
     * Get the cuboid regions seconds point location.
     *
     * <p>Note: If the location is set but the world it's for is not
     * loaded, the World value of location may be null.</p>
     */
    @Override
    @Nullable
    public final Location getP2() {
        if (_p2 == null)
            return null;

        synchronized (_sync) {
            return _p2.clone();
        }
    }

    /**
     * Get the cuboid regions lower point location.
     */
    @Override
    @Nullable
    public final Location getLowerPoint() {
        return getP1();
    }

    /**
     * Get the cuboid regions upper point location.
     */
    @Override
    @Nullable
    public final Location getUpperPoint() {
        return getP2();
    }

    /**
     * Get the smallest X axis coordinates
     * of the region.
     */
    @Override
    public final int getXStart() {
        return _startX;
    }

    /**
     * Get the smallest Y axis coordinates
     * of the region.
     */
    @Override
    public final int getYStart() {
        return _startY;
    }

    /**
     * Get the smallest Z axis coordinates
     * of the region.
     */
    @Override
    public final int getZStart() {
        return _startZ;
    }

    /**
     * Get the largest X axis coordinates
     * of the region.
     */
    @Override
    public final int getXEnd() {
        return _endX;
    }

    /**
     * Get the largest Y axis coordinates
     * of the region.
     */
    @Override
    public final int getYEnd() {
        return _endY;
    }

    /**
     * Get the largest Z axis coordinates
     * of the region.
     */
    @Override
    public final int getZEnd() {
        return _endZ;
    }

    /**
     * Get the X axis width of the region.
     */
    @Override
    public final int getXWidth() {
        return _xWidth;
    }

    /**
     * Get the Z axis width of the region.
     */
    @Override
    public final int getZWidth() {
        return _zWidth;
    }

    /**
     * Get the Y axis height of the region.
     */
    @Override
    public final int getYHeight() {
        return _yHeight;
    }

    /**
     * Get the number of blocks that make up the width of the
     * region on the X axis.
     */
    @Override
    public final int getXBlockWidth() {
        return _xBlockWidth;
    }

    /**
     * Get the number of blocks that make up the width of the
     * region on the Z axis.
     */
    @Override
    public final int getZBlockWidth() {
        return _zBlockWidth;
    }

    /**
     * Get the number of blocks that make up the height of the
     * region on the Y axis.
     */
    @Override
    public final int getYBlockHeight() {
        return _yBlockHeight;
    }

    /**
     * Get the total volume of the region.
     */
    @Override
    public final long getVolume() {
        return _volume;
    }

    /**
     * Get the center location of the region.
     */
    @Override
    @Nullable
    public final Location getCenter() {
        if (_center == null)
            return null;
        return _center.clone();
    }

    /**
     * Get the smallest X axis coordinates from the chunks
     * the region intersects with.
     */
    @Override
    public final int getChunkX() {
        return _chunkX;
    }

    /**
     * Get the smallest Z axis coordinates from the chunks
     * the region intersects with.
     */
    @Override
    public final int getChunkZ() {
        return _chunkZ;
    }

    /**
     * Get the number of chunks that comprise the chunk width
     * on the X axis of the region.
     */
    @Override
    public final int getChunkXWidth() {
        return _chunkXWidth;
    }

    /**
     * Get the number of chunks that comprise the chunk width
     * on the Z axis of the region.
     */
    @Override
    public final int getChunkZWidth() {
        return _chunkZWidth;
    }


    /**
     * Determine if the region is 1 block tall.
     */
    @Override
    public final boolean isFlatHorizontal() {
        return getYBlockHeight() == 1;
    }

    /**
     * Determine if the region is 1 block wide on the
     * X or Z axis and is not 1 block tall.
     */
    @Override
    public final boolean isFlatVertical() {
        return !isFlatHorizontal() &&
                (getZBlockWidth() == 1 || getXBlockWidth() == 1);
    }

    /**
     * Determine if the region contains the specified location.
     *
     * @param loc  The location to check.
     */
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

    /**
     * Determine if the region contains the specified
     * coordinates.
     *
     * @param x  The location X coordinates.
     * @param y  The location Y coordinates.
     * @param z  The location Z coordinates.
     */
    @Override
    public final boolean contains(int x, int y, int z) {
        synchronized (_sync) {

            _sync.notifyAll();

            return x >= getXStart() && x <= getXEnd() &&
                    y >= getYStart() && y <= getYEnd() &&
                    z >= getZStart() && z <= getZEnd();
        }
    }

    /**
     * Determine if the region contains the the specified location
     * on the specified axis.
     *
     * @param loc  The location to check.
     * @param cx   True to check if the point is inside the region on the X axis.
     * @param cy   True to check if the point is inside the region on the Y axis.
     * @param cz   True to check if the point is inside the region on the Z axis.
     */
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

    /**
     * Determine if the region intersects with the chunk specified.
     *
     * @param chunk  The chunk.
     */
    @Override
    public final boolean intersects(Chunk chunk) {
        PreCon.notNull(chunk);

        return isDefined() &&
                chunk.getWorld().equals(getWorld()) &&
                intersects(chunk.getX(), chunk.getZ());
    }

    /**
     * Determine if the region intersects with the chunk specified.
     *
     * @param chunkX  The chunk X coordinates.
     * @param chunkZ  The chunk Z coordinates.
     */
    @Override
    public final boolean intersects(int chunkX, int chunkZ) {

        return getChunkX() <= chunkX && (getChunkX() + getChunkXWidth() - 1) >= chunkX &&
                getChunkZ() <= chunkZ && (getChunkZ() + getChunkZWidth() - 1) >= chunkZ;
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

        setPoint(RegionPoint.P1, p1);
        setPoint(RegionPoint.P2, p2);

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

            _xWidth = _p1 == null || _p2 == null ? 0 : (int)Math.abs(_p1.getX() - _p2.getX());
            _zWidth = _p1 == null || _p2 == null ? 0 : (int)Math.abs(_p1.getZ() - _p2.getZ());
            _yHeight = _p1 == null || _p2 == null ? 0 : (int)Math.abs(_p1.getY() - _p2.getY());

            _xBlockWidth = _p1 == null || _p2 == null ? 0 : Math.abs(_p1.getBlockX() - _p2.getBlockX()) + 1;
            _zBlockWidth = _p1 == null || _p2 == null ? 0 : Math.abs(_p1.getBlockZ() - _p2.getBlockZ()) + 1;
            _yBlockHeight = _p1 == null || _p2 == null ? 0 : Math.abs(_p1.getBlockY() - _p2.getBlockY()) + 1;

            _volume = _xWidth * _zWidth * _yHeight;

            if (getWorld() != null) {
                double xCenter = _startX + (_xBlockWidth / 2);
                double yCenter = _startY + (_yBlockHeight / 2);
                double zCenter = _startZ + (_zBlockWidth / 2);

                _center = new Location(getWorld(), xCenter, yCenter, zCenter);
            }

            _chunkX = Math.min(_p1.getBlockX(), _p2.getBlockX()) == _p1.getBlockX() ? _p1.getChunk().getX() : _p2.getChunk().getX();
            _chunkZ = Math.min(_p1.getBlockZ(), _p2.getBlockZ()) == _p1.getBlockZ() ? _p1.getChunk().getZ() : _p2.getChunk().getZ();

            int chunkEndX = Math.max(_p1.getBlockX(), _p2.getBlockX()) == _p1.getBlockX() ? _p1.getChunk().getX() : _p2.getChunk().getX();
            int chunkEndZ = Math.max(_p1.getBlockZ(), _p2.getBlockZ()) == _p1.getBlockZ() ? _p1.getChunk().getZ() : _p2.getChunk().getZ();

            _chunkXWidth = chunkEndX - _chunkX + 1;
            _chunkZWidth = chunkEndZ - _chunkZ + 1;

            _sync.notifyAll();
        }
    }


    /*
    * Set one of the region points.
    */
    protected void setPoint(RegionPoint point, @Nullable Location l) {
        Location lower;
        Location upper;

        switch (point) {
            case P1: {
                if (l == null) {
                    _p1 = null;
                    return;
                }

                _lastP1 = l.clone();
                lower = _lastP1.clone();
                upper = _lastP2 != null
                        ? _lastP2.clone()
                        : _p2;
                break;
            }
            case P2: {
                if (l == null) {
                    _p2 = null;
                    return;
                }

                _lastP2 = l.clone();
                lower = _lastP1 != null
                        ? _lastP1.clone()
                        : _p1;
                upper = _lastP2.clone();
                break;
            }
            default: {
                upper = null;
                lower = null;
            }
        }
        if (lower != null && upper != null) {
            double tmp;
            if (lower.getX() > upper.getX()) {
                tmp = lower.getX();
                lower.setX(upper.getX());
                upper.setX(tmp);
            }
            if (lower.getY() > upper.getY()) {
                tmp = lower.getY();
                lower.setY(upper.getY());
                upper.setY(tmp);
            }
            if (lower.getZ() > upper.getZ()) {
                tmp = lower.getZ();
                lower.setZ(upper.getZ());
                upper.setZ(tmp);
            }
        }

        if (lower != null) {
            _p1 = lower;
        }
        if (upper != null) {
            _p2 = upper;
        }

    }
}
