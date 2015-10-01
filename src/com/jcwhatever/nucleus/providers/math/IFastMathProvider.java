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

package com.jcwhatever.nucleus.providers.math;

import com.jcwhatever.nucleus.providers.IProvider;

/**
 * Math provider that provides math speed equal to or greater than Java's {@link Math} utility
 * while potentially sacrificing precision in the results.
 *
 * <p>If greater precision is more important than speed, use Java's {@link Math} utility.</p>
 */
public interface IFastMathProvider extends IProvider {

    /**
     * Get the trigonometric sine of an angle.
     *
     * @param radianAngle  The radian angle.
     */
    float sin(double radianAngle);

    /**
     * Get the trigonometric cosine of an angle.
     *
     * @param radianAngle  The radian angle.
     */
    float cos(double radianAngle);

    /**
     * Get the trigonometric tangent of an angle.
     *
     * @param radianAngle  The radian angle.
     */
    float tan(double radianAngle);

    /**
     * Get the arc sine of a value.
     *
     * @param value  The value to get an arc sine from.
     */
    float asin(double value);

    /**
     * Get the arc cosine of a value.
     *
     * @param value  The value to get an arc cosine from.
     */
    float acos(double value);

    /**
     * Get the arc tangent of a value.
     *
     * @param value  The value to get an arc tangent from.
     */
    float atan(double value);

    /**
     * Returns the angle theta from the conversion of rectangular coordinates
     * (x, z) to polar coordinates (r, theta).
     *
     * <p>Field names x and z are used instead of y and x to correlate with Minecraft's
     * coordinate system.</p>
     *
     * @param x  The x coordinate.
     * @param z  The z coordinate.
     */
    float atan2(double x, double z);

    /**
     * Get the square root of a value.
     *
     * @param value  The value to get the square root of.
     */
    float sqrt(double value);

    /**
     * Get the cube square root of a value.
     *
     * @param value  The value to get a cube square root from.
     */
    float cbrt(double value);

    /**
     * Get the hyperbolic sine of a value.
     *
     * @param value  The value to get a hyperbolic sine from.
     */
    float sinh(double value);

    /**
     * Get the hyperbolic cosine of a value.
     *
     * @param value  The value to get a hyperbolic sine from.
     */
    float cosh(double value);

    /**
     * Get the hyperbolic tangent of a value.
     *
     * @param value  The value to get a hyperbolic tangent from.
     */
    float tanh(double value);

    /**
     * Get a random long value.
     */
    long randomLong();

    /**
     * Get a random double value.
     */
    double randomDouble();

    /**
     * Get a rotation matrix used for rotating vectors.
     *
     * <p>May return a cached matrix after rounding the specified angle.</p>
     *
     * @param angle  The angle of rotation.
     */
    IRotationMatrix getRotationMatrix(float angle);

    /**
     * Get a rotation matrix used for rotating vectors.
     *
     * <p>The matrix returned always uses the angle specified.</p>
     *
     * @param angle  The angle of rotation.
     */
    IRotationMatrix getStrictRotationMatrix(float angle);
}
