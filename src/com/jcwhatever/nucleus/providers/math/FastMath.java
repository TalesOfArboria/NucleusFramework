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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.providers.math.NucleusFastMathProvider;

import javax.annotation.Nonnull;

/**
 * Fast math provider static utility methods.
 */
public final class FastMath {

    private FastMath() {}

    private static IFastMathProvider _standIn;

    /**
     * Get the trigonometric sine of an angle.
     *
     * @param radianAngle  The radian angle.
     */
    public static float sin(double radianAngle) {
        return provider().sin(radianAngle);
    }

    /**
     * Get the trigonometric cosine of an angle.
     *
     * @param radianAngle  The radian angle.
     */
    public static float cos(double radianAngle) {
        return provider().cos(radianAngle);
    }

    /**
     * Get the trigonometric tangent of an angle.
     *
     * @param radianAngle  The radian angle.
     */
    public static float tan(double radianAngle) {
        return provider().tan(radianAngle);
    }

    /**
     * Get the arc sine of a value.
     *
     * @param value  The value to get an arc sine from.
     */
    public static float asin(double value) {
        return provider().asin(value);
    }

    /**
     * Get the arc cosine of a value.
     *
     * @param value  The value to get an arc cosine from.
     */
    public static float acos(double value) {
        return provider().acos(value);
    }

    /**
     * Get the arc tangent of a value.
     *
     * @param value  The value to get an arc tangent from.
     */
    public static float atan(double value) {
        return provider().atan(value);
    }

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
    public static float atan2(double x, double z) {
        return provider().atan2(x, z);
    }

    /**
     * Get the square root of a value.
     *
     * @param value  The value to get the square root of.
     */
    public static float sqrt(double value) {
        return provider().sqrt(value);
    }

    /**
     * Get the cube square root of a value.
     *
     * @param value  The value to get a cube square root from.
     */
    public static float cbrt(double value) {
        return provider().cbrt(value);
    }


    /**
     * Get the hyperbolic sine of a value.
     *
     * @param value  The value to get a hyperbolic sine from.
     */
    public static float sinh(double value) {
        return provider().sinh(value);
    }

    /**
     * Get the hyperbolic cosine of a value.
     *
     * @param value  The value to get a hyperbolic sine from.
     */
    public static float cosh(double value) {
        return provider().cosh(value);
    }

    /**
     * Get the hyperbolic tangent of a value.
     *
     * @param value  The value to get a hyperbolic tangent from.
     */
    public static float tanh(double value) {
        return provider().tanh(value);
    }

    /**
     * Get a random long value.
     */
    public static long randomLong() {
        return provider().randomLong();
    }

    /**
     * Get a random double value.
     */
    public static double randomDouble() {
        return provider().randomDouble();
    }

    /**
     * Get a rotation matrix used for rotating vectors.
     *
     * <p>May return a cached matrix after rounding the specified angle.</p>
     *
     * <p>Note that Minecraft's X axis coordinates are inverted and the returned matrix
     * implementation values will be consistent with this.</p>
     *
     * @param angle  The angle of rotation.
     */
    public static IRotationMatrix getRotationMatrix(float angle) {
        return provider().getRotationMatrix(angle);
    }

    @Nonnull
    private static IFastMathProvider provider() {

        if (Nucleus.isLoaded()) {
            _standIn = null;
            return Nucleus.getProviders().getMath();
        } else {
            if (_standIn == null)
                _standIn = new NucleusFastMathProvider();
            return _standIn;
        }
    }
}
