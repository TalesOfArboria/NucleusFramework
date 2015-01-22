package com.jcwhatever.nucleus.utils;

import org.junit.Assert;
import org.junit.Test;

public class ArrayUtilsTest {

    @Test
    public void testCopyFromStart() throws Exception {

        String[] source = new String[] { "1", "2", "3", "4", "5"};
        String[] destination = new String[3];

        ArrayUtils.copyFromStart(source, destination);

        Assert.assertEquals("1", destination[0]);
        Assert.assertEquals("2", destination[1]);
        Assert.assertEquals("3", destination[2]);
    }

    @Test
    public void testCopyFromStart1() throws Exception {
        int[] source = new int[] { 1, 2, 3, 4, 5};
        int[] destination = new int[3];

        ArrayUtils.copyFromStart(source, destination);

        Assert.assertEquals(1, destination[0]);
        Assert.assertEquals(2, destination[1]);
        Assert.assertEquals(3, destination[2]);
    }

    @Test
    public void testCopyFromEnd() throws Exception {
        String[] source = new String[] { "1", "2", "3", "4", "5"};
        String[] destination = new String[3];

        ArrayUtils.copyFromEnd(source, destination);

        Assert.assertEquals("3", destination[0]);
        Assert.assertEquals("4", destination[1]);
        Assert.assertEquals("5", destination[2]);
    }

    @Test
    public void testCopyFromEnd1() throws Exception {
        int[] source = new int[] { 1, 2, 3, 4, 5};
        int[] destination = new int[3];

        ArrayUtils.copyFromEnd(source, destination);

        Assert.assertEquals(3, destination[0]);
        Assert.assertEquals(4, destination[1]);
        Assert.assertEquals(5, destination[2]);
    }

    @Test
    public void testReduce() throws Exception {

        String[] source = new String[] { "1", "2", "3", "4", "5"};

        String[] destination = ArrayUtils.reduce(1, source, 1);

        Assert.assertEquals("2", destination[0]);
        Assert.assertEquals("3", destination[1]);
        Assert.assertEquals("4", destination[2]);

    }

    @Test
    public void testReduce1() throws Exception {
        int[] source = new int[] { 1, 2, 3, 4, 5};

        int[] destination = ArrayUtils.reduce(2, source, 2);

        Assert.assertEquals(3, destination[0]);
    }

    @Test
    public void testReduceStart() throws Exception {
        String[] source = new String[] { "1", "2", "3", "4", "5"};

        String[] destination = ArrayUtils.reduceStart(2, source);

        Assert.assertEquals("3", destination[0]);
        Assert.assertEquals("4", destination[1]);
        Assert.assertEquals("5", destination[2]);
    }

    @Test
    public void testReduceStart1() throws Exception {
        int[] source = new int[] { 1, 2, 3, 4, 5};

        int[] destination = ArrayUtils.reduceStart(2, source);

        Assert.assertEquals(3, destination[0]);
        Assert.assertEquals(4, destination[1]);
        Assert.assertEquals(5, destination[2]);
    }

    @Test
    public void testReduceEnd() throws Exception {
        String[] source = new String[] { "1", "2", "3", "4", "5"};

        String[] destination = ArrayUtils.reduceEnd(source, 2);

        Assert.assertEquals("1", destination[0]);
        Assert.assertEquals("2", destination[1]);
        Assert.assertEquals("3", destination[2]);
    }

    @Test
    public void testReduceEnd1() throws Exception {
        int[] source = new int[] { 1, 2, 3, 4, 5};

        int[] destination = ArrayUtils.reduceEnd(source, 2);

        Assert.assertEquals(1, destination[0]);
        Assert.assertEquals(2, destination[1]);
        Assert.assertEquals(3, destination[2]);
    }

    @Test
    public void testToPrimitive() throws Exception {

        Boolean[] source = new Boolean[] { null, true, false };

        boolean[] destination = ArrayUtils.toPrimitive(source);

        Assert.assertEquals(false, destination[0]);
        Assert.assertEquals(true, destination[1]);
        Assert.assertEquals(false, destination[2]);

    }

    @Test
    public void testToPrimitive1() throws Exception {
        Integer[] source = new Integer[] { null, 1, 2 };

        int[] destination = ArrayUtils.toPrimitive(source);

        Assert.assertEquals(0, destination[0]);
        Assert.assertEquals(1, destination[1]);
        Assert.assertEquals(2, destination[2]);
    }

    @Test
    public void testToWrapper() throws Exception {
        boolean[] source = new boolean[] { false, true, false };

        Boolean[] destination = ArrayUtils.toWrapper(source);

        Assert.assertEquals(false, destination[0]);
        Assert.assertEquals(true, destination[1]);
        Assert.assertEquals(false, destination[2]);
    }

    @Test
    public void testToWrapper1() throws Exception {
        int[] source = new int[] { 0, 1, 2 };

        Integer[] destination = ArrayUtils.toWrapper(source);

        Assert.assertEquals((Integer)0, destination[0]);
        Assert.assertEquals((Integer)1, destination[1]);
        Assert.assertEquals((Integer)2, destination[2]);
    }

    @Test
    public void testGet() throws Exception {
        int[] source = new int[] { 0, 1, 2 };

        int result = ArrayUtils.get(source, 0, 10);

        Assert.assertEquals(0, result);


        result = ArrayUtils.get(source, 100, 10);

        Assert.assertEquals(10, result);
    }

    @Test
    public void testLast() throws Exception {
        int[] source = new int[] { 0, 1, 2 };

        Assert.assertEquals(2, ArrayUtils.last(source));

        source = new int[0];

        Assert.assertEquals(100, ArrayUtils.last(source, 100));
    }

    @Test
    public void testRemoveNull() throws Exception {

        String[] source = new String[] { null, "1", "2", null, null };

        String[] destination = ArrayUtils.removeNull(source);

        Assert.assertEquals(2, destination.length);
        Assert.assertEquals("1", destination[0]);
        Assert.assertEquals("2", destination[1]);
    }
}