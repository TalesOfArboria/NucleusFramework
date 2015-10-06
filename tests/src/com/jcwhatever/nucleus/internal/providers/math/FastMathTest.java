package com.jcwhatever.nucleus.internal.providers.math;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test Nucleus default fast math implementation.
 */
public class FastMathTest {

    @Test
    public void testSin() {

        NucleusFastMathProvider fastMath = new NucleusFastMathProvider();

        float fastSin = fastMath.sin(0);
        float sin = (float)Math.sin(Math.toRadians(0));
        assertEquals(sin, fastSin, 0.0001D);

        fastSin = fastMath.sin(180);
        sin = (float)Math.sin(Math.toRadians(180));
        assertEquals(sin, fastSin, 0.0001D);

        fastSin = fastMath.sin(-180);
        sin = (float)Math.sin(Math.toRadians(-180));
        assertEquals(sin, fastSin, 0.0001D);

        fastSin = fastMath.sin(-360);
        sin = (float)Math.sin(Math.toRadians(-360));
        assertEquals(sin, fastSin, 0.0001D);

        fastSin = fastMath.sin(360);
        sin = (float)Math.sin(Math.toRadians(360));
        assertEquals(sin, fastSin, 0.0001D);

        fastSin = fastMath.sin(90);
        sin = (float)Math.sin(Math.toRadians(90));
        assertEquals(sin, fastSin, 0.0001D);

        fastSin = fastMath.sin(-90);
        sin = (float)Math.sin(Math.toRadians(-90));
        assertEquals(sin, fastSin, 0.0001D);
    }

    @Test
    public void testCos() {

        NucleusFastMathProvider fastMath = new NucleusFastMathProvider();

        float fastCos = fastMath.cos(0);
        float cos = (float)Math.cos(Math.toRadians(0));
        assertEquals(cos, fastCos, 0.0001D);

        fastCos = fastMath.cos(180);
        cos = (float)Math.cos(Math.toRadians(180));
        assertEquals(cos, fastCos, 0.0001D);

        fastCos = fastMath.cos(-180);
        cos = (float)Math.cos(Math.toRadians(-180));
        assertEquals(cos, fastCos, 0.0001D);

        fastCos = fastMath.cos(360);
        cos = (float)Math.cos(Math.toRadians(360));
        assertEquals(cos, fastCos, 0.0001D);

        fastCos = fastMath.cos(-360);
        cos = (float)Math.cos(Math.toRadians(-360));
        assertEquals(cos, fastCos, 0.0001D);

        fastCos = fastMath.cos(90);
        cos = (float)Math.cos(Math.toRadians(90));
        assertEquals(cos, fastCos, 0.0001D);

        fastCos = fastMath.cos(-90);
        cos = (float)Math.cos(Math.toRadians(-90));
        assertEquals(cos, fastCos, 0.0001D);
    }

    @Test
    public void testAtan2() {

        NucleusFastMathProvider fastMath = new NucleusFastMathProvider();

        float fastAtan2 = fastMath.atan2(15, 16);
        float atan2 = (float)Math.atan2(15, 16);
        assertEquals(atan2, fastAtan2, 0.0001D);

        fastAtan2 = fastMath.atan2(0, 90);
        atan2 = (float)Math.atan2(0, 90);
        assertEquals(atan2, fastAtan2, 0.0001D);

        fastAtan2 = fastMath.atan2(90, 0);
        atan2 = (float)Math.atan2(90, 0);
        assertEquals(atan2, fastAtan2, 0.0001D);

        fastAtan2 = fastMath.atan2(0, 0);
        atan2 = (float)Math.atan2(0, 0);
        assertEquals(atan2, fastAtan2, 0.0001D);

        fastAtan2 = fastMath.atan2(180, 0);
        atan2 = (float)Math.atan2(180, 0);
        assertEquals(atan2, fastAtan2, 0.0001D);

        fastAtan2 = fastMath.atan2(-180, 0);
        atan2 = (float)Math.atan2(-180, 0);
        assertEquals(atan2, fastAtan2, 0.0001D);

        fastAtan2 = fastMath.atan2(0, 180);
        atan2 = (float)Math.atan2(0, 180);
        assertEquals(atan2, fastAtan2, 0.0001D);

        fastAtan2 = fastMath.atan2(0, -180);
        atan2 = (float)Math.atan2(0, -180);
        assertEquals(atan2, fastAtan2, 0.0001D);

        fastAtan2 = fastMath.atan2(0, -360);
        atan2 = (float)Math.atan2(0, -360);
        assertEquals(atan2, fastAtan2, 0.0001D);

        fastAtan2 = fastMath.atan2(0, 360);
        atan2 = (float)Math.atan2(0, 360);
        assertEquals(atan2, fastAtan2, 0.0001D);
    }

    @Test
    public void testSqrt() {

        NucleusFastMathProvider fastMath = new NucleusFastMathProvider();

        float fastSqrt = fastMath.sqrt(5);
        float sqrt = (float)Math.sqrt(5);
        assertEquals(sqrt, fastSqrt, 0.0001D);

        fastSqrt = fastMath.sqrt(-5);
        sqrt = (float)Math.sqrt(-5);
        assertEquals(sqrt, fastSqrt, 0.0001D);
    }
}
