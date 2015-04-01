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
import com.jcwhatever.nucleus.utils.file.NucleusByteReader;
import com.jcwhatever.nucleus.utils.file.NucleusByteWriter;

import org.bukkit.Chunk;
import org.bukkit.World;

import java.io.IOException;
import javax.annotation.Nullable;

/**
 * Contains information about a chunk.
 */
public class ChunkInfo extends Coords2Di implements IChunkInfo {

    private WorldInfo _world;

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
        super(x, z);
        _world = world;
    }

    /**
     * Protected constructor for serialization.
     */
    protected ChunkInfo() {
        super();
    }

    @Override
    public WorldInfo getWorld() {
        return _world;
    }

    @Override
    @Nullable
    public Chunk getChunk() {
        World world = _world.getBukkitWorld();
        if (world == null)
            return null;

        return world.getChunkAt(getX(), getZ());
    }

    @Override
    public int hashCode() {
        return _world.hashCode() ^ getX() ^ getZ();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof IChunkInfo) {
            IChunkInfo other = (IChunkInfo)obj;

            return other.getWorld().getName().equals(_world.getName()) &&
                    other.getX() == getX() && other.getZ() == getZ();
        }

        return false;
    }

    @Override
    public void serialize(IDataNode dataNode) {
        dataNode.set("world", _world);
        super.serialize(dataNode);
    }

    @Override
    public void deserialize(IDataNode dataNode) throws DeserializeException {
        _world = dataNode.getSerializable("world", WorldInfo.class);
        super.deserialize(dataNode);
    }

    @Override
    public void serialize(NucleusByteWriter writer) throws IOException {
        writer.write(_world);
        super.serialize(writer);
    }

    @Override
    public void deserialize(NucleusByteReader reader)
            throws IOException, ClassNotFoundException, InstantiationException {

        _world = reader.getBinarySerializable(WorldInfo.class);
        if (_world == null)
            throw new RuntimeException("Failed to deserialize world info.");

        super.deserialize(reader);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " { world:" + _world.getName() + ", x:" + getX() + ", z:" + getZ() + '}';
    }
}

