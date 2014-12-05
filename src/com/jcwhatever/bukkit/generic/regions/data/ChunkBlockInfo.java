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

import com.jcwhatever.bukkit.generic.file.GenericsByteReader;
import com.jcwhatever.bukkit.generic.file.GenericsByteWriter;
import com.jcwhatever.bukkit.generic.file.IBinarySerializable;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.IDataNodeSerializable;
import com.jcwhatever.bukkit.generic.storage.UnableToDeserializeException;

import org.bukkit.Material;

import java.io.IOException;

/**
 * Data object to hold information about a single block
 */
public final class ChunkBlockInfo implements IChunkBlockInfo, Comparable<ChunkBlockInfo>,
        IDataNodeSerializable, IBinarySerializable {

    private int _x;
    private int _y;
    private int _z;

    private Material _material;
    private int _data;
    private int _light;
    private int _skylight;

    /**
     * Constructor.
     *
     * @param material     The block material.
     * @param data         The block meta data.
     * @param chunkBlockX  The blocks X coordinates relative to its chunk.
     * @param y            The blocks Y coordinates.
     * @param chunkBlockZ  The blocks Z coordinates relative to its chunk,
     */
    public ChunkBlockInfo(int chunkBlockX, int y, int chunkBlockZ, Material material,
                          int data, int light, int skylight) {
        _x = chunkBlockX;
        _y = y;
        _z = chunkBlockZ;
        _material = material;
        _data = data;
        _light = light;
        _skylight = skylight;
    }

    /**
     * Get the block material.
     */
    @Override
    public Material getMaterial() {
        return _material;
    }

    /**
     * Get the blocks meta data.
     */
    @Override
    public int getData() {
        return _data;
    }

    /**
     * Get the blocks emitted light.
     */
    @Override
    public int getEmittedLight() {
        return _light;
    }

    /**
     * Get the amount of skylight on the block.
     */
    @Override
    public int getSkylight() {
        return _skylight;
    }

    /**
     * Get the blocks X coordinates relative to its chunk.
     */
    @Override
    public int getChunkBlockX() {
        return _x;
    }

    /**
     * Get the blocks Y coordinates.
     */
    @Override
    public int getY() {
        return _y;
    }

    /**
     * Get the Blocks Z coordinates relative to its chunk.
     */
    @Override
    public int getChunkBlockZ() {
        return _z;
    }

    @Override
    public int hashCode() {
        return _x ^ _y ^ _z;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof ChunkBlockInfo) {
            ChunkBlockInfo other = (ChunkBlockInfo)obj;

            return other._x == _x &&
                    other._y == _y &&
                    other._z == _z;
        }

        return false;
    }

    /**
     * Sort by Y coordinates. Lowest to Highest
     */
    @Override
    public int compareTo(ChunkBlockInfo o) {
        //noinspection SuspiciousNameCombination
        return Integer.compare(this._y, o._y);
    }

    @Override
    public void serializeToDataNode(IDataNode dataNode) {
        dataNode.set("x", _x);
        dataNode.set("y", _y);
        dataNode.set("z", _z);
        dataNode.set("material", _material);
        dataNode.set("meta", _data);
        dataNode.set("light", _light);
        dataNode.set("sky", _skylight);
    }

    @Override
    public void deserializeFromDataNode(IDataNode dataNode) throws UnableToDeserializeException {
        _x = dataNode.getInteger("x");
        _y = dataNode.getInteger("y");
        _z = dataNode.getInteger("z");
        _material = dataNode.getEnum("material", Material.class);
        _data = dataNode.getInteger("meta");
        _light = dataNode.getInteger("light");
        _skylight = dataNode.getInteger("sky");
    }

    @Override
    public void serializeToBytes(GenericsByteWriter writer) throws IOException {
        writer.write((byte)((_x << 4) | (_y & 0xF)));
        writer.write((short)_y);
        writer.write(_material);
        writer.write((byte)_data);
        writer.write((byte)((_light << 4) | (_skylight & 0xF)));
    }

    @Override
    public void deserializeFromBytes(GenericsByteReader reader) throws IOException, ClassNotFoundException, InstantiationException {
        int xz = reader.getByte();
        _x = xz >> 4;
        _z = xz & 0xF;

        _y = reader.getShort();
        _material = reader.getEnum(Material.class);
        _data = reader.getByte();

        int ls = reader.getByte();

        _light = ls >> 4;
        _skylight = ls & 0xF;
    }
}
