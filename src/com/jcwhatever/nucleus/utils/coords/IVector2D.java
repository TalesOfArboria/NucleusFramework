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
     * Set the vector X and Z coordinates.
     *
     * @param x  The X coordinate.
     * @param z  The Z coordinate.
     *
     * @return  Self for chaining.
     */
    IVector2D set2D(double x, double z);

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
     * Add scalar to vector.
     *
     * <p>Uses the larger of the specified value or the added result. Essentially performs a
     * Math.max operation using the specified value and the added result per axis.</p>
     *
     * @param scalar  The scalar value.
     * @param value   The value to use in the max comparison.
     *
     * @return  Self for chaining.
     */
    IVector2D add2DMax(double scalar, double value);

    /**
     * Add scalar to vector.
     *
     * <p>Uses the smaller of the specified value or the added result. Essentially performs a
     * Math.max operation using the specified value and the added result per axis.</p>
     *
     * @param scalar  The scalar value.
     * @param value   The value to use in the min comparison.
     *
     * @return  Self for chaining.
     */
    IVector2D add2DMin(double scalar, double value);

    /**
     * Add a value to the X axis.
     *
     * @param value  The value to add.
     *
     * @return  Self for chaining.
     */
    IVector2D addX(double value);

    /**
     * Add a value to the Z axis.
     *
     * @param value  The value to add.
     *
     * @return  Self for chaining.
     */
    IVector2D addZ(double value);

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
     * Subtract scalar from vector.
     *
     * <p>Uses the larger of the specified value or the subtracted result. Essentially performs a
     * Math.max operation using the specified value and the subtraction result per axis.</p>
     *
     * @param scalar  The scalar value.
     * @param value   The value to use in the max comparison.
     *
     * @return  Self for chaining.
     */
    IVector2D subtract2DMax(double scalar, double value);

    /**
     * Subtract scalar from vector.
     *
     * <p>Uses the smaller of the specified value or the subtracted result. Essentially performs a
     * Math.min operation using the specified value and the subtraction result per axis.</p>
     *
     * @param scalar  The scalar value.
     * @param value   The value to use in the min comparison.
     *
     * @return  Self for chaining.
     */
    IVector2D subtract2DMin(double scalar, double value);

    /**
     * Subtract a value from to the X axis.
     *
     * @param value  The value to add.
     *
     * @return  Self for chaining.
     */
    IVector2D subtractX(double value);

    /**
     * Subtract a value from the Z axis.
     *
     * @param value  The value to add.
     *
     * @return  Self for chaining.
     */
    IVector2D subtractZ(double value);

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
     * Multiply the vector by a scalar value.
     *
     * <p>Uses the larger of the specified value or the factor result. Essentially performs a
     * Math.max operation using the specified value and the factor result per axis.</p>
     *
     * @param scalar  The scalar value.
     * @param value   The value to use in the max comparison.
     *
     * @return  Self for chaining.
     */
    IVector2D multiply2DMax(double scalar, double value);

    /**
     * Multiply the vector by a scalar value.
     *
     * <p>Uses the smaller of the specified value or the factor result. Essentially performs a
     * Math.min operation using the specified value and the factor result per axis.</p>
     *
     * @param scalar  The scalar value.
     * @param value   The value to use in the min comparison.
     *
     * @return  Self for chaining.
     */
    IVector2D multiply2DMin(double scalar, double value);

    /**
     * Multiply the X axis by the specified value.
     *
     * @param value  The value.
     *
     * @return  Self for chaining.
     */
    IVector2D multiplyX(double value);

    /**
     * Multiply the Z axis by the specified value.
     *
     * @param value  The value.
     *
     * @return  Self for chaining.
     */
    IVector2D multiplyZ(double value);

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
     * Get vector magnitude from X and Z axis.
     */
    double getMagnitude2D();

    /**
     * Get vector magnitude squared from X and Z axis.
     */
    double getMagnitudeSquared2D();

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
