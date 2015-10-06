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
     * @param angleDegrees  The angle in degrees.
     */
    float sin(double angleDegrees);

    /**
     * Get the trigonometric cosine of an angle.
     *
     * @param angleDegrees  The angle in degrees.
     */
    float cos(double angleDegrees);

    /**
     * Get the trigonometric tangent of an angle.
     *
     * @param angleDegrees  The angle in degrees.
     */
    float tan(double angleDegrees);

    /**
     * Get the arc sine of a value.
     *
     * @param angleDegrees  The value to get an arc sine from in degrees.
     */
    float asin(double angleDegrees);

    /**
     * Get the arc cosine of a value.
     *
     * @param angleDegrees  The value to get an arc cosine from in degrees.
     */
    float acos(double angleDegrees);

    /**
     * Get the arc tangent of a value.
     *
     * @param angleDegrees  The value to get an arc tangent from in degrees.
     */
    float atan(double angleDegrees);

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
     * @param angleDegrees  The value to get a hyperbolic sine from in degrees.
     */
    float sinh(double angleDegrees);

    /**
     * Get the hyperbolic cosine of a value.
     *
     * @param angleDegrees  The value to get a hyperbolic sine from in degrees.
     */
    float cosh(double angleDegrees);

    /**
     * Get the hyperbolic tangent of a value.
     *
     * @param angleDegrees  The value to get a hyperbolic tangent from in degrees.
     */
    float tanh(double angleDegrees);

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
     * <p>May return a cached matrix after rounding the specified angle to an implementation
     * specific precision.</p>
     *
     * <p>Note that Minecraft's X axis coordinates are inverted relative to Yaw 0 and the returned
     * matrix implementation values will be consistent with this. When rotating the Y axis, it will
     * rotate in the opposite direction requested to compensate.</p>
     *
     * @param angleDegrees  The angle of rotation in degrees. Values are in the range of -180 to 180.
     *                      Values outside range are clamped.
     */
    IRotationMatrix getRotationMatrix(float angleDegrees);
}
