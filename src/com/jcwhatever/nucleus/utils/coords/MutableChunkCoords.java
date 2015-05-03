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

import com.jcwhatever.nucleus.utils.ThreadSingletons;
import com.jcwhatever.nucleus.utils.ThreadSingletons.ISingletonFactory;

import org.bukkit.World;

/**
 * Mutable chunk coordinates.
 */
public class MutableChunkCoords extends ChunkCoords {

    /**
     * Create a new {@link ThreadSingletons} instance.
     */
    public static ThreadSingletons<MutableChunkCoords> createThreadSingletons() {
        return new ThreadSingletons<>(new ISingletonFactory<MutableChunkCoords>() {
            @Override
            public MutableChunkCoords create() {
                return new MutableChunkCoords();
            }
        });
    }

    /**
     * Constructor.
     */
    public MutableChunkCoords() {
        super((String)null, 0, 0);
    }

    /**
     * Constructor.
     *
     * @param worldName  The name of the world.
     * @param x          The chunk X coordinates.
     * @param z          The chunk Z coordinates.
     */
    public MutableChunkCoords(String worldName, int x, int z) {
        super(worldName, x, z);
    }

    /**
     * Constructor.
     *
     * @param world  The world.
     * @param x      The chunk X coordinates.
     * @param z      The chunk Z coordinates.
     */
    public MutableChunkCoords(World world, int x, int z) {
        super(world, x, z);
    }

    /**
     * Set the world name via a {@link World} object.
     *
     * @param world  The world.
     */
    public void setWorld(World world) {
        super.setWorldName(world != null ? world.getName() : null);
    }

    @Override
    public void setWorldName(String name) {
        super.setWorldName(name);
    }

    @Override
    public void setX(int x) {
        super.setX(x);
    }

    @Override
    public void setZ(int z) {
        super.setZ(z);
    }

    @Override
    protected void seal() {
        // do nothing, prevent making the object immutable
    }
}
