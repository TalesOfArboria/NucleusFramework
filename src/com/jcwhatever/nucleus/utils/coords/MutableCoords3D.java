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

import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * 3D mutable coordinates.
 */
public class MutableCoords3D extends Coords3D {

    /**
     * Create a new {@link ThreadSingletons} instance.
     */
    public static ThreadSingletons<MutableCoords3D> createThreadSingletons() {
        return new ThreadSingletons<>(new ISingletonFactory<MutableCoords3D>() {
            @Override
            public MutableCoords3D create() {
                return new MutableCoords3D();
            }
        });
    }

    /**
     * Constructor.
     */
    public MutableCoords3D() {
        super(0, 0, 0);
    }

    /**
     * Constructor.
     *
     * @param x  The x coordinates.
     * @param y  The y coordinates.
     * @param z  The z coordinates.
     */
    public MutableCoords3D(double x, double y, double z) {
        super(x, y, z);
    }

    /**
     * Constructor.
     *
     * <p>Clones values from source coordinates.</p>
     *
     * @param source  The source coordinates.
     */
    public MutableCoords3D(ICoords3D source) {
        super(source);
    }

    /**
     * Constructor.
     *
     * <p>Clones values from source coordinates and adds delta values.</p>
     *
     * @param source  The source coordinates.
     * @param deltaX  The X coordinate values to add to the source coordinates.
     * @param deltaY  The Y coordinate values to add to the source coordinates.
     * @param deltaZ  The Z coordinate values to add to the source coordinates.
     */
    public MutableCoords3D(ICoords3D source, double deltaX, double deltaY, double deltaZ) {
        super(source, deltaX, deltaY, deltaZ);
    }

    /**
     * Set the X coordinate.
     *
     * @param x  The X coordinate.
     */
    @Override
    public void setX(double x) {
        super.setX(x);
    }

    /**
     * Set the Y coordinate.
     *
     * @param y  The Y coordinate.
     */
    @Override
    public void setY(double y) {
        super.setY(y);
    }

    /**
     * Set the Z coordinate.
     *
     * @param z  The Z coordinate.
     */
    @Override
    public void setZ(double z) {
        super.setZ(z);
    }

    /**
     * Copy X and Z values from a {@link ICoords2D} instance.
     *
     * @param coords  The coordinates to copy.
     */
    public void copyFrom(ICoords2D coords) {
        PreCon.notNull(coords);

        super.setX(coords.getX());
        super.setZ(coords.getZ());
    }

    /**
     * Copy X and Z values from a {@link ICoords2Di} instance.
     *
     * @param coords  The coordinates to copy.
     */
    public void copyFrom(ICoords2Di coords) {
        PreCon.notNull(coords);

        super.setX(coords.getX());
        super.setZ(coords.getZ());
    }

    /**
     * Copy values from a {@link ICoords3D} instance.
     *
     * @param coords  The coordinates to copy.
     */
    public void copyFrom(ICoords3D coords) {
        PreCon.notNull(coords);

        super.setX(coords.getX());
        super.setY(coords.getY());
        super.setZ(coords.getZ());
    }

    /**
     * Copy values from a {@link ICoords3Di} instance.
     *
     * @param coords  The coordinates to copy.
     */
    public void copyFrom(ICoords3Di coords) {
        PreCon.notNull(coords);

        super.setX(coords.getX());
        super.setY(coords.getY());
        super.setZ(coords.getZ());
    }

    /**
     * Copy X and Z values from a {@link org.bukkit.Location} instance.
     *
     * @param location  The location to copy.
     */
    public void copyFrom(Location location) {
        PreCon.notNull(location);

        super.setX(location.getX());
        super.setY(location.getY());
        super.setZ(location.getZ());
    }

    /**
     * Copy X and Z values from a {@link org.bukkit.util.Vector} instance.
     *
     * @param vector  The vector to copy.
     */
    public void copyFrom(Vector vector) {
        PreCon.notNull(vector);

        super.setX(vector.getX());
        super.setX(vector.getY());
        super.setZ(vector.getZ());
    }

    @Override
    protected void seal() {
        // do nothing, prevent the object from being made immutable
    }
}
