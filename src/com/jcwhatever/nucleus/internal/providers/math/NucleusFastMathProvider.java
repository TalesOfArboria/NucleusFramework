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

public class NucleusFastMathProvider extends Provider implements IFastMathProvider {

    public static final String NAME = "NucleusFastMath";
    static final XORShiftRandom XOR_RANDOM = new XORShiftRandom();

    private static final int SIN_BITS = 12;
    private static final int SIN_MASK = ~(-1 << SIN_BITS);
    private static final float DEG_TO_INDEX;
    private static final float[] SIN_TABLE, COS_TABLE;

    private static final int ATAN2_BITS = 12;
    private static final int ATAN2_BITS2 = ATAN2_BITS << 1;
    private static final int ATAN2_MASK = ~(-1 << ATAN2_BITS2);
    private static final int ATAN2_COUNT = ATAN2_MASK + 1;
    private static final int ATAN2_DIM = (int) Math.sqrt(ATAN2_COUNT);
    private static final float ATAN2_DIM_MINUS_1 = (ATAN2_DIM - 1);
    private static final float[] ATAN2_TABLE = new float[ATAN2_COUNT];

    static {

        // cos/sin code from http://riven8192.blogspot.com/2009/08/fastmath-sincos-lookup-tables.html
        // licensed: http://creativecommons.org/licenses/by/3.0/
        int sinCount = SIN_MASK + 1;
        float degFull = (float) (360.0);
        float radFull = (float) (Math.PI * 2.0);
        DEG_TO_INDEX = sinCount / degFull;
        SIN_TABLE = new float[sinCount];
        COS_TABLE = new float[sinCount];

        for (int i = 0; i < sinCount; i++) {
            SIN_TABLE[i] = (float) Math.sin((i + 0.5f) / sinCount * radFull);
            COS_TABLE[i] = (float) Math.cos((i + 0.5f) / sinCount * radFull);
        }
        for (int i = 0; i < 360; i += 90) {
            SIN_TABLE[(int)(i * DEG_TO_INDEX) & SIN_MASK] = (float)Math.sin(i * Math.PI / 180.0);
            COS_TABLE[(int)(i * DEG_TO_INDEX) & SIN_MASK] = (float)Math.cos(i * Math.PI / 180.0);
        }

        // atan2 code from http://riven8192.blogspot.com/2009/08/fastmath-atan2-lookup-table.html
        // licensed: http://creativecommons.org/licenses/by/3.0/
        for (int i = 0; i < ATAN2_DIM; i++) {
            for (int j = 0; j < ATAN2_DIM; j++) {

                float x = (float) i / ATAN2_DIM;
                float y = (float) j / ATAN2_DIM;

                ATAN2_TABLE[j * ATAN2_DIM + i] = (float) Math.atan2(y, x);
            }
        }
    }

    public NucleusFastMathProvider() {
        setInfo(new InternalProviderInfo(this.getClass(),
                NAME, "Default fast math provider."));
    }

    @Override
    public float sin(double angleDegrees) {
        return SIN_TABLE[(int) (angleDegrees * DEG_TO_INDEX) & SIN_MASK];
    }

    @Override
    public float cos(double angleDegrees) {
        return COS_TABLE[(int) (angleDegrees * DEG_TO_INDEX) & SIN_MASK];
    }

    @Override
    public float tan(double angleDegrees) {
        return (float)Math.tan(Math.toRadians(angleDegrees));
    }

    @Override
    public float asin(double angleDegrees) {
        return (float)Math.toDegrees(Math.asin(Math.toRadians(angleDegrees)));
    }

    @Override
    public float acos(double angleDegrees) {
        return (float)Math.toDegrees(Math.acos(Math.toRadians(angleDegrees)));
    }

    @Override
    public float atan(double angleDegrees) {
        return (float)Math.toDegrees(Math.atan(Math.toRadians(angleDegrees)));
    }

    @Override
    public float atan2(double y, double x) {

        float add, mul;

        if (x < 0.0f) {

            if (y < 0.0f) {
                x = -x;
                y = -y;
                mul = 1.0f;
            } else {
                x = -x;
                mul = -1.0f;
            }
            add = -3.141592653f;

        } else {

            if (y < 0.0f) {
                y = -y;
                mul = -1.0f;
            }
            else {
                mul = 1.0f;
            }
            add = 0.0f;
        }

        float invDiv = ATAN2_DIM_MINUS_1 / (float)((x < y) ? y : x);
        int xi = Math.min((int) (x * invDiv), (int)ATAN2_DIM_MINUS_1);
        int yi = Math.min((int) (y * invDiv), (int)ATAN2_DIM_MINUS_1);

        return (ATAN2_TABLE[yi * ATAN2_DIM + xi] + add) * mul;
    }

    @Override
    public float sqrt(double value) {
        return (float)Math.sqrt(value); // this is already very fast
    }

    @Override
    public float cbrt(double value) {
        return (float)Math.cbrt(value);
    }

    @Override
    public float sinh(double angleDegrees) {
        return (float)Math.sinh(Math.toRadians(angleDegrees));
    }

    @Override
    public float cosh(double angleDegrees) {
        return (float)Math.cosh(Math.toRadians(angleDegrees));
    }

    @Override
    public float tanh(double angleDegrees) {
        return (float)Math.tanh(Math.toRadians(angleDegrees));
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
    public IRotationMatrix getRotationMatrix(float angleDegrees) {
        return NucleusRotationMatrix.get(angleDegrees);
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
