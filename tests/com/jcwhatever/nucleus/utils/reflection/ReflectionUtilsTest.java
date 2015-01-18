package com.jcwhatever.nucleus.utils.reflection;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ReflectionUtilsTest {

    @Test
    public void testGetArrayDimensions() throws Exception {

        boolean notAnArray = true;
        boolean[] array1 = new boolean[0];
        boolean[][] array2 = new boolean[0][0];
        boolean[][][] array3 = new boolean[0][0][0];

        assertEquals(0, ReflectionUtils.getArrayDimensions(notAnArray));
        assertEquals(1, ReflectionUtils.getArrayDimensions(array1));
        assertEquals(2, ReflectionUtils.getArrayDimensions(array2));
        assertEquals(3, ReflectionUtils.getArrayDimensions(array3));
    }
}