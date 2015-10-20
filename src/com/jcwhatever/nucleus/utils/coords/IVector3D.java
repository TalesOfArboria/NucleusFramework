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
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * 3-dimensional vector.
 */
public interface IVector3D extends IVector2D, ICoords3D {

    /**
     * Set the Y coordinate.
     *
     * @param y  The Y coordinate.
     *
     * @return  Self for chaining.
     */
    IVector3D setY(double y);

    /**
     * Set the vector X, Y and Z coordinates.
     *
     * @param x  The X coordinate.
     * @param y  The Y coordinate.
     * @param z  The Z coordinate.
     *
     * @return  Self for chaining.
     */
    IVector3D set3D(double x, double y, double z);

    /**
     * Copy entity velocity into vector.
     *
     * @param entity  The entity.
     *
     * @return  Self for chaining.
     */
    IVector3D copyFrom3D(Entity entity);

    /**
     * Copy coordinate values from specified coordinates.
     *
     * @param coords  The values to copy.
     *
     * @return  Self for chaining.
     */
    IVector3D copyFrom3D(ICoords2D coords);

    /**
     * Copy coordinate values from specified coordinates.
     *
     * @param coords  The values to copy.
     *
     * @return  Self for chaining.
     */
    IVector3D copyFrom3D(ICoords2Di coords);

    /**
     * Copy coordinate values from specified coordinates.
     *
     * @param vector  The Bukkit vector.
     *
     * @return  Self for chaining.
     */
    IVector3D copyFrom3D(Vector vector);

    /**
     * Copy coordinate values from specified location.
     *
     * @param location  The Bukkit location.
     *
     * @return  Self for chaining.
     */
    IVector3D copyFrom3D(Location location);

    /**
     * Copy coordinate values to the specified entity's velocity.
     *
     * @param entity  The entity.
     *
     * @return  Self for chaining.
     */
    IVector3D copyTo3D(Entity entity);

    /**
     * Copy coordinate values to the specified vector.
     *
     * @param vector  The Bukkit vector.
     *
     * @return  Self for chaining.
     */
    IVector3D copyTo3D(Vector vector);

    /**
     * Copy coordinate values to the specified location.
     *
     * @param location  The Bukkit location.
     *
     * @return  Self for chaining.
     */
    IVector3D copyTo3D(Location location);

    /**
     * Add vector.
     *
     * @param vector  The vector to add.
     *                If the vector is 2D, 0 is substituted for the Y coord.
     *
     * @return  Self for chaining.
     */
    IVector3D add3D(ICoords2D vector);

    /**
     * Add vector.
     *
     * @param vector  The vector to add.
     *                If the vector is 2D, 0 is substituted for the Y coord.
     *
     * @return  Self for chaining.
     */
    IVector3D add3D(ICoords2Di vector);

    /**
     * Add location as vector.
     *
     * @param vector  The location vector to add.
     *
     * @return  Self for chaining.
     */
    IVector3D add3D(Location vector);

    /**
     * Add scalar to vector.
     *
     * @param value  The scalar value.
     *
     * @return  Self for chaining.
     */
    IVector3D add3D(double value);

    /**
     * Add a value to the Y axis.
     *
     * @param value  The value to add.
     *
     * @return  Self for chaining.
     */
    IVector3D addY(double value);

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
    IVector3D add3DMax(double scalar, double value);

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
    IVector3D add3DMin(double scalar, double value);

    /**
     * Subtract vector.
     *
     * @param vector  The vector to subtract.
     *                If the vector is 2D, 0 is substituted for the Y coord.
     *
     * @return  Self for chaining.
     */
    IVector3D subtract3D(ICoords2D vector);

    /**
     * Subtract vector.
     *
     * @param vector  The vector to subtract.
     *                If the vector is 2D, 0 is substituted for the Y coord.
     *
     * @return  Self for chaining.
     */
    IVector3D subtract3D(ICoords2Di vector);

    /**
     * Subtract location as vector.
     *
     * @param vector  The location vector to subtract.
     *
     * @return  Self for chaining.
     */
    IVector3D subtract3D(Location vector);

    /**
     * Subtract scalar from vector.
     *
     * @param scalar  The scalar value.
     *
     * @return  Self for chaining.
     */
    IVector3D subtract3D(double scalar);

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
    IVector3D subtract3DMax(double scalar, double value);

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
    IVector3D subtract3DMin(double scalar, double value);

    /**
     * Subtract a value from to the Y axis.
     *
     * @param value  The value to add.
     *
     * @return  Self for chaining.
     */
    IVector3D subtractY(double value);

    /**
     * Multiply the vector by another vector.
     *
     * @param vector  The other vector.
     *                If the vector is 2D, 0 is substituted for the Y coord.
     *
     * @return  Self for chaining.
     */
    IVector3D multiply3D(ICoords2D vector);

    /**
     * Multiply the vector by another vector.
     *
     * @param vector  The other vector.
     *                If the vector is 2D, 0 is substituted for the Y coord.
     *
     * @return  Self for chaining.
     */
    IVector3D multiply3D(ICoords2Di vector);

    /**
     * Multiply the vector by a location as a vector.
     *
     * @param vector  The location vector.
     *
     * @return  Self for chaining.
     */
    IVector3D multiply3D(Location vector);

    /**
     * Multiply the vector by a scalar value.
     *
     * @param scalar  The scalar value.
     *
     * @return  Self for chaining.
     */
    IVector3D multiply3D(double scalar);

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
    IVector3D multiply3DMax(double scalar, double value);

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
    IVector3D multiply3DMin(double scalar, double value);

    /**
     * Multiply the Y axis by the specified value.
     *
     * @param value  The value.
     *
     * @return  Self for chaining.
     */
    IVector2D multiplyY(double value);

    /**
     * Average the vector with another vector.
     *
     * @param vector  The other vector.
     *                If the vector is 2D, 0 is substituted for the Y coord.
     *
     * @return  Self for chaining.
     */
    IVector3D average3D(ICoords2D vector);

    /**
     * Average the vector with another vector.
     *
     * @param vector  The other vector.
     *                If the vector is 2D, 0 is substituted for the Y coord.
     *
     * @return  Self for chaining.
     */
    IVector3D average3D(ICoords2Di vector);

    /**
     * Reverse the vector.
     *
     * @return  Self for chaining.
     */
    IVector3D reverse3D();

    /**
     * Cross product of this vector and another vector.
     *
     * @param vector  The other vector.
     *
     * @return  Self for chaining.
     */
    IVector3D cross(ICoords3D vector);

    /**
     * Cross product of this vector and another vector.
     *
     * @param vector  The other vector.
     *
     * @return  Self for chaining.
     */
    IVector3D cross(ICoords3Di vector);

    /**
     * Get Dot Product of this vector and another vector.
     *
     * @param vector  The other vector.
     *
     * @return  The Dot product.
     */
    double getDot3D(ICoords3D vector);

    /**
     * Get Dot Product of this vector and another vector.
     *
     * @param vector  The other vector.
     *
     * @return  The Dot product.
     */
    double getDot3D(ICoords3Di vector);

    /**
     * Get distance to another vector.
     *
     * @param vector  The other vector.
     *                If the vector is 2D, 0 is substituted for the Y coord.
     */
    double getDistance3D(ICoords2D vector);

    /**
     * Get distance to another vector.
     *
     * @param vector  The other vector.
     *                If the vector is 2D, 0 is substituted for the Y coord.
     */
    double getDistance3D(ICoords2Di vector);

    /**
     * Get distance to a location used as a vector.
     *
     * @param vector  The location vector.
     */
    double getDistance3D(Location vector);

    /**
     * Get distance squared to another vector.
     *
     * @param vector  The other vector.
     *                If the vector is 2D, 0 is substituted for the Y coord.
     */
    double getDistanceSquared3D(ICoords2D vector);

    /**
     * Get distance squared to another vector.
     *
     * @param vector  The other vector.
     *                If the vector is 2D, 0 is substituted for the Y coord.
     */
    double getDistanceSquared3D(ICoords2Di vector);

    /**
     * Get distance squared to a location used as a vector.
     *
     * @param vector  The location vector.
     */
    double getDistanceSquared3D(Location vector);

    /**
     * Get vector magnitude from X, Y and Z axis.
     */
    double getMagnitude3D();

    /**
     * Get vector magnitude squared from X, Y and Z axis.
     */
    double getMagnitudeSquared3D();

    /**
     * Get a Bukkit Vector whose values are linked to the current vector.
     */
    Vector asBukkitVector();

    @Override
    IVector3D setX(double x);

    @Override
    IVector3D setZ(double z);

    @Override
    IVector3D set2D(double x, double z);

    @Override
    IVector3D copyFrom2D(ICoords2D coords);

    @Override
    IVector3D copyFrom2D(ICoords2Di coords);

    @Override
    IVector3D copyFrom2D(Vector vector);

    @Override
    IVector3D copyFrom2D(Location location);

    @Override
    IVector3D copyTo2D(Vector vector);

    @Override
    IVector3D copyTo2D(Location location);

    @Override
    IVector3D add2D(ICoords2D vector);

    @Override
    IVector3D add2D(ICoords2Di vector);

    @Override
    IVector3D add2D(double value);

    @Override
    IVector3D add2DMax(double scalar, double value);

    @Override
    IVector3D add2DMin(double scalar, double value);

    @Override
    IVector3D addX(double value);

    @Override
    IVector3D addZ(double value);

    @Override
    IVector3D subtract2D(ICoords2D vector);

    @Override
    IVector3D subtract2D(ICoords2Di vector);

    @Override
    IVector3D subtract2D(double scalar);

    @Override
    IVector3D subtract2DMax(double scalar, double value);

    @Override
    IVector3D subtract2DMin(double scalar, double value);

    @Override
    IVector3D subtractX(double value);

    @Override
    IVector3D subtractZ(double value);

    @Override
    IVector3D multiply2D(ICoords2D vector);

    @Override
    IVector3D multiply2D(ICoords2Di vector);

    @Override
    IVector3D multiply2D(double scalar);

    @Override
    IVector3D multiply2DMax(double scalar, double value);

    @Override
    IVector3D multiply2DMin(double scalar, double value);

    @Override
    IVector3D multiplyX(double value);

    @Override
    IVector3D multiplyZ(double value);

    @Override
    IVector3D average2D(ICoords2D vector);

    @Override
    IVector3D average2D(ICoords2Di vector);

    @Override
    IVector3D normalize();

    @Override
    IVector3D reset();

    @Override
    IVector3D abs();

    @Override
    IVector3D reverse2D();
}
