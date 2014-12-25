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

package com.jcwhatever.generic.regions.data;

import com.jcwhatever.generic.utils.file.GenericsByteReader;
import com.jcwhatever.generic.utils.file.GenericsByteWriter;
import com.jcwhatever.generic.utils.file.IBinarySerializable;
import com.jcwhatever.generic.storage.IDataNode;
import com.jcwhatever.generic.storage.IDataNodeSerializable;
import com.jcwhatever.generic.storage.UnableToDeserializeException;

import org.bukkit.Chunk;
import org.bukkit.World;

import java.io.IOException;
import javax.annotation.Nullable;

/**
 * Contains information about a chunk.
 */
public class ChunkInfo implements IChunkInfo, IDataNodeSerializable, IBinarySerializable {

    private WorldInfo _world;
    private int _x;
    private int _z;
    private int _hash;

    /**
     * Constructor.
     *
     * @param chunk  The chunk to get info from.
     */
    public ChunkInfo(Chunk chunk) {
        this(new WorldInfo(chunk.getWorld()),
                chunk.getX(), chunk.getZ());
    }

    /**
     * Constructor.
     *
     * @param world  The world the chunk is in.
     * @param x      The chunk X coordinates.
     * @param z      The chunk Z coordinates.
     */
    public ChunkInfo(World world, int x, int z) {
        this(new WorldInfo(world), x, z);
    }

    /**
     * Constructor.
     *
     * @param world  The world the chunk is in.
     * @param x      The chunk X coordinates.
     * @param z      The chunk Z coordinates.
     */
    public ChunkInfo(WorldInfo world, int x, int z) {
        _world = world;
        _x = x;
        _z = z;
        _hash = _world.hashCode() ^ _x ^ _z;
    }

    /**
     * Get the world the chunk is in.
     */
    @Override
    public WorldInfo getWorld() {
        return _world;
    }

    /**
     * Get the chunk X coordinates.
     */
    @Override
    public int getX() {
        return _x;
    }

    /**
     * Get the chunk Z coordinates.
     */
    @Override
    public int getZ() {
        return _z;
    }

    /**
     * Get the chunk.
     */
    @Override
    @Nullable
    public Chunk getChunk() {
        World world = _world.getBukkitWorld();
        if (world == null)
            return null;

        return world.getChunkAt(_x, _z);
    }

    @Override
    public int hashCode() {
        return _hash;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof ChunkInfo) {
            ChunkInfo other = (ChunkInfo)obj;

            return other._world.getName().equals(_world.getName()) &&
                    other._x == _x && other._z == _z;
        }

        return false;
    }

    @Override
    public void serializeToDataNode(IDataNode dataNode) {
        dataNode.set("world", _world);
        dataNode.set("x", _x);
        dataNode.set("z", _z);
    }

    @Override
    public void deserializeFromDataNode(IDataNode dataNode) throws UnableToDeserializeException {
        _world = dataNode.getSerializable("world", WorldInfo.class);
        _x = dataNode.getInteger("x");
        _z = dataNode.getInteger("z");
        _hash = _world.hashCode() ^ _x ^ _z;
    }

    @Override
    public void serializeToBytes(GenericsByteWriter writer) throws IOException {
        writer.write(_world);
        writer.write(_x);
        writer.write(_z);
    }

    @Override
    public void deserializeFromBytes(GenericsByteReader reader) throws IOException, ClassNotFoundException, InstantiationException {
        _world = reader.getGenerics(WorldInfo.class);
        if (_world == null)
            throw new RuntimeException("Failed to deserialize world info.");

        _x = reader.getInteger();
        _z = reader.getInteger();
        _hash = _world.hashCode() ^ _x ^ _z;
    }
}

