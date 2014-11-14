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

import org.bukkit.Material;

/**
 * Data object to hold information about a single block
 */
public final class ChunkBlockInfo implements Comparable<ChunkBlockInfo> {
    private final int _data;
    private final int _x;
    private final int _y;
    private final int _z;
    private final Material _material;

    /**
     * Constructor.
     *
     * @param material     The block material.
     * @param data         The block meta data.
     * @param chunkBlockX  The blocks X coordinates relative to its chunk.
     * @param y            The blocks Y coordinates.
     * @param chunkBlockZ  The blocks Z coordinates relative to its chunk,
     */
    public ChunkBlockInfo(Material material, int data, int chunkBlockX, int y, int chunkBlockZ) {
        _material = material;
        _data = data;
        _x = chunkBlockX;
        _y = y;
        _z = chunkBlockZ;
    }

    /**
     * Get the block material.
     */
    public Material getMaterial() {
        return _material;
    }

    /**
     * Get the blocks meta data.
     */
    public int getData() {
        return _data;
    }

    /**
     * Get the blocks X coordinates relative to its chunk.
     */
    public int getChunkBlockX() {
        return _x;
    }

    /**
     * Get the blocks Y coordinates.
     */
    public int getY() {
        return _y;
    }

    /**
     * Get the Blocks Z coordinates relative to its chunk.
     */
    public int getChunkBlockZ() {
        return _z;
    }

    /**
     * Sort by Y coordinates. Lowest to Highest
     */
    @Override
    public int compareTo(ChunkBlockInfo o) {
        //noinspection SuspiciousNameCombination
        return Integer.compare(this._y, o._y);
    }
}
