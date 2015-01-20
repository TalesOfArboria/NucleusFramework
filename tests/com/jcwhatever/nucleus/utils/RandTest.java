package com.jcwhatever.nucleus.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.easetech.easytest.annotation.Repeat;
import org.junit.Test;

import java.util.List;

public class RandTest {

    @Test
    @Repeat(times=1000)
    public void testGet() throws Exception {

        List<String> list = ArrayUtils.asList("a", "b", "c");

        String result = Rand.get(list);

        assertTrue("abc".contains(result));
    }

    @Test
    @Repeat(times=1000)
    public void testGet1() throws Exception {

        String[] array = new String[] {
                "a",
                "b",
                "c"
        };

        String result = Rand.get(array);

        assertTrue("abc".contains(result));
    }

    @Test
    @Repeat(times=1000)
    public void testGetInt() throws Exception {

        Rand.getInt();

    }

    @Test
    @Repeat(times=1000)
    public void testGetInt1() throws Exception {

        int result = Rand.getInt(10);

        assertTrue(result >= 0);
        assertTrue(result <= 10);
    }

    @Test
    @Repeat(times=1000)
    public void testGetInt2() throws Exception {

        int result = Rand.getInt(5, 10);

        assertTrue(result >= 5);
        assertTrue(result <= 10);
    }

    @Test
    @Repeat(times=1000)
    public void testGetDouble() throws Exception {

        double result = Rand.getDouble();

        assertTrue(result >= 0.0D);
        assertTrue(result <= 1.0D);
    }

    @Test
    @Repeat(times=1000)
    public void testGetDouble1() throws Exception {

        double result = Rand.getDouble(0.5D);

        assertTrue(result >= 0.0D);
        assertTrue(result <= 0.5D);
    }

    @Test
    @Repeat(times=1000)
    public void testGetDouble2() throws Exception {

        double result = Rand.getDouble(0.2D, 0.75D);

        try {

            assertTrue(result >= 0.2D);
            assertTrue(result <= 0.75D);

        }
        catch (AssertionError e) {
            System.out.println("result: " + result);
            throw e;
        }
    }

    @Test
    @Repeat(times=1000)
    public void testGetSafeString() throws Exception {

        String str = Rand.getSafeString(10);

        assertEquals(10, str.length());

        for (int i=0; i < str.length(); i++) {
            assertTrue(Rand.SAFE_CHARACTERS.indexOf(str.charAt(i)) != -1);
        }
    }

    @Test
    @Repeat(times=1000)
    public void testGetUnsafeString() throws Exception {

        String str = Rand.getUnsafeString(10);

        assertEquals(10, str.length());

        for (int i=0; i < str.length(); i++) {
            assertTrue(Rand.UNSAFE_CHARACTERS.indexOf(str.charAt(i)) != -1);
        }
    }

    @Test
    @Repeat(times=1000)
    public void testGetString() throws Exception {

        String str = Rand.getString(10, "abc");

        assertEquals(10, str.length());

        for (int i=0; i < str.length(); i++) {
            assertTrue("abc".indexOf(str.charAt(i)) != -1);
        }
    }

    @Test
    @Repeat(times=1000)
    public void testChance() throws Exception {

        Rand.chance(100);

    }

    @Test
    @Repeat(times=1000)
    public void testRollDice() throws Exception {
        Rand.rollDice(10);
    }
}