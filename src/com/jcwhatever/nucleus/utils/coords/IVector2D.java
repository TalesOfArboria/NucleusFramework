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

import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * 2-dimensional vector.
 */
public interface IVector2D extends ICoords2D {

    /**
     * Set the vector X coordinate.
     *
     * @param x  The X coordinate.
     *
     * @return  Self for chaining.
     */
    IVector2D setX(double x);

    /**
     * Set the vector Z coordinate.
     *
     * @param z  The Z coordinate.
     *
     * @return  Self for chaining.
     */
    IVector2D setZ(double z);

    /**
     * Copy coordinate values from specified coordinates.
     *
     * @param coords  The values to copy.
     *
     * @return  Self for chaining.
     */
    IVector2D copyFrom2D(ICoords2D coords);

    /**
     * Copy coordinate values from specified coordinates.
     *
     * @param vector  The Bukkit vector.
     *
     * @return  Self for chaining.
     */
    IVector2D copyFrom2D(Vector vector);

    /**
     * Copy coordinate values from specified location.
     *
     * @param location  The Bukkit location.
     *
     * @return  Self for chaining.
     */
    IVector2D copyFrom2D(Location location);

    /**
     * Copy coordinate values to the specified vector.
     *
     * @param vector  The Bukkit vector.
     *
     * @return  Self for chaining.
     */
    IVector2D copyTo2D(Vector vector);

    /**
     * Copy coordinate values to the specified location.
     *
     * @param location  The Bukkit location.
     *
     * @return  Self for chaining.
     */
    IVector2D copyTo2D(Location location);

    /**
     * Add vector.
     *
     * @param vector  The vector to add.
     *
     * @return  Self for chaining.
     */
    IVector2D add2D(ICoords2D vector);

    /**
     * Add scalar to vector.
     *
     * @param value  The scalar value.
     *
     * @return  Self for chaining.
     */
    IVector2D add2D(double value);

    /**
     * Subtract vector.
     *
     * @param vector  The vector to subtract.
     *
     * @return  Self for chaining.
     */
    IVector2D subtract2D(ICoords2D vector);

    /**
     * Subtract scalar from vector.
     *
     * @param scalar  The scalar value.
     *
     * @return  Self for chaining.
     */
    IVector2D subtract2D(double scalar);

    /**
     * Multiply the vector by another vector.
     *
     * @param vector  The other vector.
     *
     * @return  Self for chaining.
     */
    IVector2D multiply2D(ICoords2D vector);

    /**
     * Multiply the vector by a scalar value.
     *
     * @param scalar  The scalar value.
     *
     * @return  Self for chaining.
     */
    IVector2D multiply2D(double scalar);

    /**
     * Average the vector with another vector.
     *
     * @param vector  The other vector.
     *
     * @return  Self for chaining.
     */
    IVector2D average2D(ICoords2D vector);

    /**
     * Reverse the vector.
     *
     * @return  Self for chaining.
     */
    IVector2D reverse2D();

    /**
     * Normalize vector.
     *
     * @return  Self for chaining.
     */
    IVector2D normalize();

    /**
     * Set all coordinate values to 0.
     *
     * @return  Self for chaining.
     */
    IVector2D reset();

    /**
     * Change all values to absolute values.
     *
     * @return  Self for chaining.
     */
    IVector2D abs();

    /**
     * Get the angle from this vector to another.
     *
     * @param vector  The other vector.
     *
     * @return  The angle.
     */
    float getAngle(ICoords2D vector);

    /**
     * Get the vector yaw angle difference.
     *
     * @param vector  The other vector.
     */
    float getYawDelta(ICoords2D vector);

    /**
     * Get the vector angle as a Minecraft yaw angle.
     */
    float getYaw();

    /**
     * Get Dot Product of this vector and another vector.
     *
     * @param vector  The other vector.
     *
     * @return  The Dot product.
     */
    double getDot2D(ICoords2D vector);

    /**
     * Get vector magnitude.
     */
    double getMagnitude();

    /**
     * Get vector magnitude.
     */
    double getMagnitudeSquared();

    /**
     * Get vector direction.
     */
    double getDirection();

    /**
     * Get distance to another vector.
     *
     * @param vector  The other vector.
     */
    double getDistance2D(ICoords2D vector);

    /**
     * Get distance squared to another vector.
     *
     * @param vector  The other vector.
     */
    double getDistanceSquared2D(ICoords2D vector);

    /**
     * Get coordinates as a new instance of a Bukkit vector.
     */
    Vector getBukkitVector();

    /**
     * Copy the vector coordinates to an output Bukkit vector.
     *
     * @param output  The output vector.
     *
     * @return  The output vector.
     */
    Vector getBukkitVector(Vector output);
}
