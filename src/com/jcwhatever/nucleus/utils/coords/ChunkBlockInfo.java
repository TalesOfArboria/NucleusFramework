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

package com.jcwhatever.nucleus.utils.coords;

import com.jcwhatever.nucleus.storage.DeserializeException;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.file.NucleusByteReader;
import com.jcwhatever.nucleus.utils.file.NucleusByteWriter;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.io.IOException;

/**
 * Data object to hold information about a single block
 */
public final class ChunkBlockInfo extends Coords3Di
        implements IChunkBlockInfo, Comparable<ChunkBlockInfo> {

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
        super(chunkBlockX, y, chunkBlockZ);
        _material = material;
        _data = data;
        _light = light;
        _skylight = skylight;
    }

    @Override
    public Material getMaterial() {
        return _material;
    }

    @Override
    public int getData() {
        return _data;
    }

    @Override
    public int getEmittedLight() {
        return _light;
    }

    @Override
    public int getSkylight() {
        return _skylight;
    }

    /**
     * Get the {@link org.bukkit.block.Block} represented by this coordinates in
     * the specified {@link org.bukkit.Chunk}.
     *
     * @param chunk  The chunk to get the block from.
     */
    public Block getBlock(Chunk chunk) {
        PreCon.notNull(chunk);

        return chunk.getBlock(getX(), getY(), getZ());
    }

    @Override
    public int compareTo(ChunkBlockInfo o) {
        //noinspection SuspiciousNameCombination
        return Integer.compare(this.getY(), o.getY());
    }

    @Override
    public void serialize(IDataNode dataNode) {
        super.serialize(dataNode);
        dataNode.set("material", _material);
        dataNode.set("meta", _data);
        dataNode.set("light", _light);
        dataNode.set("sky", _skylight);
    }

    @Override
    public void deserialize(IDataNode dataNode) throws DeserializeException {
        super.deserialize(dataNode);
        _material = dataNode.getEnum("material", Material.class);
        _data = dataNode.getInteger("meta");
        _light = dataNode.getInteger("light");
        _skylight = dataNode.getInteger("sky");
    }

    @Override
    public void serializeToBytes(NucleusByteWriter writer) throws IOException {
        writer.write((byte)((getX() << 4) | (getY() & 0xF)));
        writer.write((short)getY());
        writer.write(_material);
        writer.write((byte)_data);
        writer.write((byte)((_light << 4) | (_skylight & 0xF)));
    }

    @Override
    public void deserializeFromBytes(NucleusByteReader reader) throws IOException, ClassNotFoundException, InstantiationException {
        int xz = reader.getByte();
        int x = xz >> 4;
        int z = xz & 0xF;
        int y = reader.getShort();
        _material = reader.getEnum(Material.class);
        _data = reader.getByte();

        int ls = reader.getByte();

        _light = ls >> 4;
        _skylight = ls & 0xF;

        deserialize(x, y, z);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " { x:" + getX() + ", z:" + getZ() +
                ", material:" + _material.name() +
                ", data:" + _data +
                ", light:" + _light +
                ", skylight: " + _skylight + '}';
    }
}
