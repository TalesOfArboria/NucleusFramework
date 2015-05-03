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

import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.serialize.DeserializeException;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.file.IByteReader;
import com.jcwhatever.nucleus.utils.file.IByteWriter;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.io.IOException;
import javax.annotation.Nullable;

/**
 * Contains information about a chunk.
 */
public class ChunkCoords extends Coords2Di implements IChunkCoords {

    private String _worldName;

    /**
     * Constructor.
     *
     * @param chunk  The chunk to get info from.
     */
    public ChunkCoords(Chunk chunk) {
        this(chunk.getWorld().getName(),
                chunk.getX(), chunk.getZ());
    }

    /**
     * Constructor.
     *
     * @param world  The world the chunk is in.
     * @param x      The chunk X coordinates.
     * @param z      The chunk Z coordinates.
     */
    public ChunkCoords(World world, int x, int z) {
        this(world.getName(), x, z);
    }

    /**
     * Constructor.
     *
     * @param world   The world the chunk is in.
     * @param coords  The chunk coordinates.
     */
    public ChunkCoords(World world, Coords2Di coords) {
        this(world.getName(), coords.getX(), coords.getZ());
    }

    /**
     * Constructor.
     *
     * @param worldName   The name of the world the chunk is in.
     * @param coords      The chunk coordinates.
     */
    public ChunkCoords(String worldName, Coords2Di coords) {
        this(worldName, coords.getX(), coords.getZ());
    }

    /**
     * Constructor.
     *
     * @param worldName  The world the chunk is in.
     * @param x          The chunk X coordinates.
     * @param z          The chunk Z coordinates.
     */
    public ChunkCoords(String worldName, int x, int z) {
        super(x, z);

        PreCon.notNull(worldName);

        _worldName = worldName;
    }

    /**
     * Protected constructor for serialization.
     */
    protected ChunkCoords() {
        super();
    }

    @Override
    public String getWorldName() {
        return _worldName;
    }

    @Override
    @Nullable
    public Chunk getChunk() {
        World world = Bukkit.getWorld(_worldName);
        if (world == null)
            return null;

        return world.getChunkAt(getX(), getZ());
    }

    @Override
    public int hashCode() {
        return _worldName.hashCode() ^ getX() ^ getZ();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof IChunkCoords) {
            IChunkCoords other = (IChunkCoords)obj;

            return other.getWorldName().equals(_worldName) &&
                    other.getX() == getX() && other.getZ() == getZ();
        }

        return false;
    }

    @Override
    public void serialize(IDataNode dataNode) {
        dataNode.set("world", _worldName);
        super.serialize(dataNode);
    }

    @Override
    public void deserialize(IDataNode dataNode) throws DeserializeException {
        _worldName = dataNode.getString("world");
        super.deserialize(dataNode);
    }

    @Override
    public void serialize(IByteWriter writer) throws IOException {
        writer.write(_worldName);
        super.serialize(writer);
    }

    @Override
    public void deserialize(IByteReader reader)
            throws IOException, ClassNotFoundException, InstantiationException {

        _worldName = reader.getString();
        if (_worldName == null)
            throw new RuntimeException("Failed to deserialize world info.");

        super.deserialize(reader);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " { world:" + _worldName + ", x:" + getX() + ", z:" + getZ() + '}';
    }

    /**
     * Set the world name.
     *
     * @param worldName  The name of the world.
     *
     * @throws java.lang.IllegalStateException if the object is immutable.
     */
    protected void setWorldName(String worldName) {
        if (isImmutable())
            throw new IllegalStateException("Coordinate is immutable.");

        _worldName = worldName;
    }
}

