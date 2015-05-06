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

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.ThreadSingletons;
import com.jcwhatever.nucleus.utils.ThreadSingletons.ISingletonFactory;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * 2D mutable integer coordinates.
 */
public class MutableCoords2Di extends Coords2Di {

    /**
     * Create a new {@link ThreadSingletons} instance.
     */
    public static ThreadSingletons<MutableCoords2Di> createThreadSingletons() {
        return new ThreadSingletons<>(new ISingletonFactory<MutableCoords2Di>() {
            @Override
            public MutableCoords2Di create(Thread thread) {
                return new MutableCoords2Di();
            }
        });
    }

    /**
     * Constructor.
     */
    public MutableCoords2Di() {
        super(0, 0);
    }

    /**
     * Constructor.
     *
     * @param x The x coordinates.
     * @param z The z coordinates.
     */
    public MutableCoords2Di(int x, int z) {
        super(x, z);
    }

    /**
     * Constructor.
     *
     * <p>Clones values from source coordinates.</p>
     *
     * @param source The source coordinates.
     */
    public MutableCoords2Di(ICoords2Di source) {
        super(source);
    }

    /**
     * Constructor.
     *
     * <p>Clones values from source coordinates and adds delta values.</p>
     *
     * @param source The source coordinates.
     * @param deltaX The X coordinate values to add to the source coordinates.
     * @param deltaZ The Z coordinate values to add to the source coordinates.
     */
    public MutableCoords2Di(ICoords2Di source, int deltaX, int deltaZ) {
        super(source, deltaX, deltaZ);
    }

    /**
     * Set the X coordinate.
     *
     * @param x  The X coordinate.
     */
    @Override
    public void setX(int x) {
        super.setX(x);
    }

    /**
     * Set the Z coordinate.
     *
     * @param z  The Z coordinate.
     */
    @Override
    public void setZ(int z) {
        super.setZ(z);
    }

    /**
     * Copy values from a {@link ICoords2D} instance.
     *
     * @param coords  The coordinates to copy.
     */
    public void copyFrom(ICoords2D coords) {
        PreCon.notNull(coords);

        super.setX(coords.getFloorX());
        super.setZ(coords.getFloorZ());
    }

    /**
     * Copy values from a {@link ICoords2Di} instance.
     *
     * @param coords  The coordinates to copy.
     */
    public void copyFrom(ICoords2Di coords) {
        PreCon.notNull(coords);

        super.setX(coords.getX());
        super.setZ(coords.getZ());
    }

    /**
     * Copy X and Z values from a {@link org.bukkit.Chunk} instance.
     *
     * @param chunk  The chunk to copy.
     */
    public void copyFrom(Chunk chunk) {
        PreCon.notNull(chunk);

        super.setX(chunk.getX());
        super.setZ(chunk.getZ());
    }

    /**
     * Copy X and Z values from a {@link org.bukkit.Location} instance.
     *
     * @param location  The location to copy.
     */
    public void copyFrom(Location location) {
        PreCon.notNull(location);

        super.setX(location.getBlockX());
        super.setZ(location.getBlockZ());
    }

    /**
     * Copy X and Z values from a {@link org.bukkit.util.Vector} instance.
     *
     * @param vector  The vector to copy.
     */
    public void copyFrom(Vector vector) {
        PreCon.notNull(vector);

        super.setX(vector.getBlockX());
        super.setZ(vector.getBlockZ());
    }

    @Override
    protected void seal() {
        // do nothing, prevent making the object immutable
    }
}
