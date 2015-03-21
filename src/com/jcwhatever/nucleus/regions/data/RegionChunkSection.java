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


package com.jcwhatever.nucleus.regions.data;

import com.jcwhatever.nucleus.regions.selection.IRegionSelection;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.ChunkInfo;
import com.jcwhatever.nucleus.utils.file.IBinarySerializable;
import com.jcwhatever.nucleus.utils.file.NucleusByteReader;
import com.jcwhatever.nucleus.utils.file.NucleusByteWriter;

import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;

import java.io.IOException;

/**
 * Provides variables representing a section of a region
 * contained within a chunk.
 */
public class RegionChunkSection implements IBinarySerializable {

    private int _chunkX;
    private int _chunkZ;

    private int _chunkBlockX;
    private int _chunkBlockZ;

    private int _chunkBlockEndX;
    private int _chunkBlockEndZ;

    private int _startChunkX;
    private int _endChunkX;

    private int _startChunkZ;
    private int _endChunkZ;

    private int _startBlockX;
    private int _endBlockX;

    private int _startBlockZ;
    private int _endBlockZ;

    private int _yStart;
    private int _yEnd;

    private int _regionStartX;
    private int _regionEndX;

    private int _regionStartZ;
    private int _regionEndZ;

    Location _p1;
    Location _p2;

    /**
     * Constructor.
     */
    protected RegionChunkSection () {}

    /**
     * Constructor.
     *
     * @param region  The region.
     * @param chunkX  The X coordinates of the chunk.
     * @param chunkZ  The Y coordinates of the chunk.
     */
    public RegionChunkSection (IRegionSelection region, int chunkX, int chunkZ) {
        _chunkX = chunkX;
        _chunkZ = chunkZ;
        init(region.getP1(), region.getP2());
    }

    /**
     * Constructor.
     *
     * @param region  The region.
     * @param chunk   The chunk to get chunk coordinates from.
     */
    public RegionChunkSection (IRegionSelection region, ChunkInfo chunk) {
        _chunkX = chunk.getX();
        _chunkZ = chunk.getZ();
        init(region.getP1(), region.getP2());
    }

    /**
     * Constructor.
     *
     * @param region  The region.
     * @param chunk   The chunk snapshot to get chunk coordinates from.
     */
    public RegionChunkSection (IRegionSelection region, ChunkSnapshot chunk) {
        _chunkX = chunk.getX();
        _chunkZ = chunk.getZ();
        init(region.getP1(), region.getP2());
    }

    /**
     * Get the section cuboid first point location.
     */
    public Location getP1() {
        return _p1;
    }

    /**
     * Get the section cuboid second point location.
     */
    public Location getP2() {
        return _p2;
    }

    /**
     * Get the regions StartX value.
     */
    public int getRegionStartX() {
        return _regionStartX;
    }

    /**
     * Get the regions EndX value.
     */
    public int getRegionEndX() {
        return _regionEndX;
    }

    /**
     * Get the regions StartZ value.
     */
    public int getRegionStartZ() {
        return _regionStartZ;
    }

    /**
     * Get the regions EndZ value
     */
    public int getRegionEndZ() {
        return _regionEndZ;
    }

    /**
     * Get the sections chunk X coordinates.
     */
    public int getChunkX() {
        return _chunkX;
    }

    /**
     * Get the section chunk Z coordinates.
     */
    public int getChunkZ() {
        return _chunkZ;
    }

    /**
     * Get the sections chunk X coordinate as
     * a block coordinate.
     */
    public int getChunkBlockX() {
        return _chunkBlockX;
    }

    /**
     * Get the sections chunk Z coordinate as
     * a block coordinate.
     */
    public int getChunkBlockZ() {
        return _chunkBlockZ;
    }

    /**
     * Get the sections ending chunk X coordinate
     * as a block coordinate.
     */
    public int getChunkBlockEndX() {
        return _chunkBlockEndX;
    }

    /**
     * Get the sections ending chunk Z coordinate
     * as a block coordinate.
     */
    public int getChunkBlockEndZ() {
        return _chunkBlockEndZ;
    }

    /**
     * Get the coordinates relative to the chunk
     * where the section begins on the X axis.
     */
    public int getStartChunkX() {
        return  _startChunkX;
    }

    /**
     * Get the coordinates relative to the chunk
     * where the section ends on the X axis.
     */
    public int getEndChunkX() {
        return  _endChunkX;
    }

    /**
     * Get the coordinates relative to the chunk
     * where the section starts on the Z axis.
     */
    public int getStartChunkZ() {
        return _startChunkZ;
    }

    /**
     * Get the coordinates relative to the chunk
     * where the section ends on the Z axis.
     */
    public int getEndChunkZ() {
        return _endChunkZ;
    }

    /**
     * Get the block coordinates relative to the world
     * center where the section starts on the X axis.
     */
    public int getStartBlockX() {
        return  _startBlockX;
    }

    /**
     * Get the block coordinates relative to the world
     * center where the section ends on the X axis.
     */
    public int getEndBlockX() {
        return  _endBlockX;
    }

    /**
     * Get the block coordinates relative to the world
     * center where the section starts on the Z axis.
     */
    public int getStartBlockZ() {
        return _startBlockZ;
    }

    /**
     * Get the block coordinates relative to the world
     * center where the section ends on the Z axis.
     */
    public int getEndBlockZ() {
        return _endBlockZ;
    }

    /**
     * Get the lowest coordinates of the section
     * on the Y axis.
     */
    public int getStartY() {
        return _yStart;
    }

    /**
     * Get the highest coordinates of the section
     * on the Y axis.
     */
    public int getEndY() {
        return _yEnd;
    }

    /**
     * Determine if the specified block coordinates relative to the world center
     * are contained within the section.
     *
     * @param x  The X coordinates.
     * @param y  The Y coordinates.
     * @param z  The Z coordinates.
     */
    public boolean containsBlockCoords(int x, int y, int z) {
        return x >= getStartBlockX() && x <= getEndBlockX() &&
                y >= getStartY() && y <= getEndY() &&
                z >= getStartBlockZ() && z <= getEndBlockZ();
    }

    /**
     * Determine if the specified block coordinates relative to the chunk
     * are contained within the section.
     *
     * @param x  The X coordinates.
     * @param y  The Y coordinates.
     * @param z  The Z coordinates.
     */
    public boolean containsChunkCoords(int x, int y, int z) {
        return x >= getStartChunkX() && x <= getEndChunkX() &&
                y >= getStartY() && y <= getEndY() &&
                z >= getStartChunkZ() && z <= getEndChunkZ();
    }


    // initialize all variables
    private void init(Location p1, Location p2) {

        _chunkBlockX = _chunkX * 16;
        _chunkBlockZ = _chunkZ * 16;

        _chunkBlockEndX = _chunkBlockX + 15;
        _chunkBlockEndZ = _chunkBlockZ + 15;

        //noinspection ConstantConditions
        int xStart = _regionStartX = Math.min(p1.getBlockX(), p2.getBlockX());
        int xEnd = _regionEndX =  Math.max(p1.getBlockX(), p2.getBlockX());

        _yStart = Math.min(p1.getBlockY(), p2.getBlockY());
        _yEnd = Math.max(p1.getBlockY(), p2.getBlockY());

        int zStart = _regionStartZ = Math.min(p1.getBlockZ(), p2.getBlockZ());
        int zEnd = _regionEndZ = Math.max(p1.getBlockZ(), p2.getBlockZ());

        _startChunkX = (_chunkBlockX > xStart) ? 0 : xStart - _chunkBlockX;
        _endChunkX = (xEnd > _chunkBlockEndX) ? 15 : 15 - (_chunkBlockEndX - xEnd);

        _startChunkZ = (_chunkBlockZ > zStart) ? 0 : zStart - _chunkBlockZ;
        _endChunkZ = (zEnd > _chunkBlockEndZ) ? 15 : 15 - (_chunkBlockEndZ - zEnd);

        _startBlockX = _chunkBlockX + _startChunkX;
        _endBlockX = _startBlockX + _endChunkX;

        _startBlockZ = _chunkBlockZ + _startChunkZ;
        _endBlockZ = _startBlockZ + _endChunkZ;

        _p1 = new Location(p1.getWorld(), _startBlockX, _yStart, _startBlockZ);
        _p2 = new Location(p2.getWorld(), _endBlockX, _yEnd, _endBlockZ);
    }

    @Override
    public void serializeToBytes(NucleusByteWriter writer) throws IOException {
        PreCon.notNull(writer);

        writer.write(_p1);
        writer.write(_p2);

        writer.write(_chunkX);
        writer.write(_chunkZ);
    }

    @Override
    public void deserializeFromBytes(NucleusByteReader reader) throws IOException, InstantiationException {
        PreCon.notNull(reader);

        Location p1 = reader.getLocation();
        Location p2 = reader.getLocation();

        _chunkX = reader.getInteger();
        _chunkZ = reader.getInteger();

        init(p1, p2);
    }
}
