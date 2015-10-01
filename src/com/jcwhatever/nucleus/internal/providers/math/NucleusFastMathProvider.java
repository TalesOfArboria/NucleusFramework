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

package com.jcwhatever.nucleus.internal.providers.math;

import com.jcwhatever.nucleus.internal.providers.InternalProviderInfo;
import com.jcwhatever.nucleus.providers.Provider;
import com.jcwhatever.nucleus.providers.math.IFastMathProvider;
import com.jcwhatever.nucleus.providers.math.IRotationMatrix;

/*
 * 
 */
public class NucleusFastMathProvider extends Provider implements IFastMathProvider {

    public static final String NAME = "NucleusFastMath";
    static final XORShiftRandom XOR_RANDOM = new XORShiftRandom();

    public NucleusFastMathProvider() {
        setInfo(new InternalProviderInfo(this.getClass(),
                NAME, "Default fast math provider."));
    }

    @Override
    public float sin(double radianAngle) {
        return (float)Math.sin(radianAngle);
    }

    @Override
    public float cos(double radianAngle) {
        return (float)Math.cos(radianAngle);
    }

    @Override
    public float tan(double radianAngle) {
        return (float)Math.tan(radianAngle);
    }

    @Override
    public float asin(double value) {
        return (float)Math.asin(value);
    }

    @Override
    public float acos(double value) {
        return (float)Math.acos(value);
    }

    @Override
    public float atan(double value) {
        return (float)Math.atan(value);
    }

    @Override
    public float atan2(double x, double z) {
        //noinspection SuspiciousNameCombination
        return (float)Math.atan2(x, z);
    }

    @Override
    public float sqrt(double value) {
        return (float)Math.sqrt(value);
    }

    @Override
    public float cbrt(double value) {
        return (float)Math.cbrt(value);
    }

    @Override
    public float sinh(double value) {
        return (float)Math.sinh(value);
    }

    @Override
    public float cosh(double value) {
        return (float)Math.cosh(value);
    }

    @Override
    public float tanh(double value) {
        return (float)Math.tanh(value);
    }

    @Override
    public long randomLong() {
        return XOR_RANDOM.next();
    }

    @Override
    public double randomDouble() {
        return Math.random();
    }

    @Override
    public IRotationMatrix getRotationMatrix(float angle) {
        return NucleusRotationMatrix.get(angle);
    }

    @Override
    public IRotationMatrix getStrictRotationMatrix(float angle) {
        return new NucleusRotationMatrix(angle);
    }

    private static class XORShiftRandom {
        volatile long x = System.currentTimeMillis();

        long next() {
            long w = x;
            w ^= (w << 21);
            w ^= (w >>> 35);
            w ^= (w << 4);
            return x = w;
        }
    }
}
