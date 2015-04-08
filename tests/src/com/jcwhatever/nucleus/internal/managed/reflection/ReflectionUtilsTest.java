package com.jcwhatever.nucleus.internal.managed.reflection;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.managed.reflection.Reflection;

import org.junit.BeforeClass;
import org.junit.Test;

public class ReflectionUtilsTest {

    @BeforeClass
    public static void beforeClass() {
        NucleusTest.init();
    }

    @Test
    public void testGetArrayDimensions() throws Exception {

        boolean notAnArray = true;
        boolean[] array1 = new boolean[0];
        boolean[][] array2 = new boolean[0][0];
        boolean[][][] array3 = new boolean[0][0][0];

        assertEquals(0, Reflection.getArrayDimensions(notAnArray));
        assertEquals(1, Reflection.getArrayDimensions(array1));
        assertEquals(2, Reflection.getArrayDimensions(array2));
        assertEquals(3, Reflection.getArrayDimensions(array3));
    }

    @Test
    public void testGetArrayComponentType() {

        ReflectionUtilsTest notAnArray = this;
        boolean[] array1 = new boolean[0];
        boolean[][] array2 = new boolean[0][0];
        boolean[][][] array3 = new boolean[0][0][0];

        assertEquals(ReflectionUtilsTest.class, Reflection.getArrayComponentType(notAnArray));
        assertEquals(boolean.class, Reflection.getArrayComponentType(array1));
        assertEquals(boolean.class, Reflection.getArrayComponentType(array2));
        assertEquals(boolean.class, Reflection.getArrayComponentType(array3));
    }
}