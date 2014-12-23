/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

package com.jcwhatever.bukkit.generic.utils;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Array utilities.
 */
public final class ArrayUtils {

    private ArrayUtils() {}

    public static final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    public static final char[] EMPTY_CHAR_ARRAY = new char[0];
    public static final short[] EMPTY_SHORT_ARRAY = new short[0];
    public static final int[] EMPTY_INT_ARRAY = new int[0];
    public static final long[] EMPTY_LONG_ARRAY = new long[0];
    public static final float[] EMPTY_FLOAT_ARRAY = new float[0];
    public static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static final ItemStack[] EMPTY_ITEMSTACK_ARRAY = new ItemStack[0];
    public static final Entity[] EMPTY_ENTITY_ARRAY = new Entity[0];

    /**
     * Copy the source array elements to the destination array starting from the
     * beginning and ending when either the destination runs out of space or the
     * source runs out of elements.
     *
     * @param source       The source array.
     * @param destination  The destination array.
     *
     * @param <T>  The array component type.
     *
     * @return  The destination array.
     */
    public static <T> T[] copyFromStart(T[] source, T[] destination) {
        PreCon.notNull(source);
        PreCon.notNull(destination);

        int size = Math.min(source.length, destination.length);

        System.arraycopy(source, 0, destination, 0, size);

        return destination;
    }

    /**
     * Copy the source array elements to the destination array starting from the
     * beginning and ending when either the destination runs out of space or the
     * source runs out of elements.
     *
     * @param source       The source array.
     * @param destination  The destination array.
     *
     * @return  The destination array.
     */
    public static boolean[] copyFromStart(boolean[] source, boolean[] destination) {
        PreCon.notNull(source);
        PreCon.notNull(destination);

        int size = Math.min(source.length, destination.length);

        System.arraycopy(source, 0, destination, 0, size);

        return destination;
    }

    /**
     * Copy the source array elements to the destination array starting from the
     * beginning and ending when either the destination runs out of space or the
     * source runs out of elements.
     *
     * @param source       The source array.
     * @param destination  The destination array.
     *
     * @return  The destination array.
     */
    public static byte[] copyFromStart(byte[] source, byte[] destination) {
        PreCon.notNull(source);
        PreCon.notNull(destination);

        int size = Math.min(source.length, destination.length);

        System.arraycopy(source, 0, destination, 0, size);

        return destination;
    }

    /**
     * Copy the source array elements to the destination array starting from the
     * beginning and ending when either the destination runs out of space or the
     * source runs out of elements.
     *
     * @param source       The source array.
     * @param destination  The destination array.
     *
     * @return  The destination array.
     */
    public static char[] copyFromStart(char[] source, char[] destination) {
        PreCon.notNull(source);
        PreCon.notNull(destination);

        int size = Math.min(source.length, destination.length);

        System.arraycopy(source, 0, destination, 0, size);

        return destination;
    }

    /**
     * Copy the source array elements to the destination array starting from the
     * beginning and ending when either the destination runs out of space or the
     * source runs out of elements.
     *
     * @param source       The source array.
     * @param destination  The destination array.
     *
     * @return  The destination array.
     */
    public static short[] copyFromStart(short[] source, short[] destination) {
        PreCon.notNull(source);
        PreCon.notNull(destination);

        int size = Math.min(source.length, destination.length);

        System.arraycopy(source, 0, destination, 0, size);

        return destination;
    }

    /**
     * Copy the source array elements to the destination array starting from the
     * beginning and ending when either the destination runs out of space or the
     * source runs out of elements.
     *
     * @param source       The source array.
     * @param destination  The destination array.
     *
     * @return  The destination array.
     */
    public static int[] copyFromStart(int[] source, int[] destination) {
        PreCon.notNull(source);
        PreCon.notNull(destination);

        int size = Math.min(source.length, destination.length);

        System.arraycopy(source, 0, destination, 0, size);

        return destination;
    }

    /**
     * Copy the source array elements to the destination array starting from the
     * beginning and ending when either the destination runs out of space or the
     * source runs out of elements.
     *
     * @param source       The source array.
     * @param destination  The destination array.
     *
     * @return  The destination array.
     */
    public static long[] copyFromStart(long[] source, long[] destination) {
        PreCon.notNull(source);
        PreCon.notNull(destination);

        int size = Math.min(source.length, destination.length);

        System.arraycopy(source, 0, destination, 0, size);

        return destination;
    }

    /**
     * Copy the source array elements to the destination array starting from the
     * beginning and ending when either the destination runs out of space or the
     * source runs out of elements.
     *
     * @param source       The source array.
     * @param destination  The destination array.
     *
     * @return  The destination array.
     */
    public static float[] copyFromStart(float[] source, float[] destination) {
        PreCon.notNull(source);
        PreCon.notNull(destination);

        int size = Math.min(source.length, destination.length);

        System.arraycopy(source, 0, destination, 0, size);

        return destination;
    }

    /**
     * Copy the source array elements to the destination array starting from the
     * beginning and ending when either the destination runs out of space or the
     * source runs out of elements.
     *
     * @param source       The source array.
     * @param destination  The destination array.
     *
     * @return  The destination array.
     */
    public static double[] copyFromStart(double[] source, double[] destination) {
        PreCon.notNull(source);
        PreCon.notNull(destination);

        int size = Math.min(source.length, destination.length);

        System.arraycopy(source, 0, destination, 0, size);

        return destination;
    }

    /**
     * Copy the source array elements to the destination array starting from the end
     * and finishing when either the destination runs out of space or the source runs
     * out of elements.
     *
     * @param source       The source array.
     * @param destination  The destination array.
     *
     * @param <T>  The array component type.
     *
     * @return  The destination array.
     */
    public static <T> T[] copyFromEnd(T[] source, T[] destination) {
        PreCon.notNull(source);
        PreCon.notNull(destination);

        int delta = source.length - destination.length;
        int sourceAdjust = delta < 0 ?  0 : delta;
        int destAdjust = delta < 0 ? -delta : 0;

        System.arraycopy(source, sourceAdjust, destination, destAdjust, destination.length - destAdjust);

        return destination;
    }

    /**
     * Copy the source array elements to the destination array starting from the end
     * and finishing when either the destination runs out of space or the source runs
     * out of elements.
     *
     * @param source       The source array.
     * @param destination  The destination array.
     *
     * @return  The destination array.
     */
    public static boolean[] copyFromEnd(boolean[] source, boolean[] destination) {
        PreCon.notNull(source);
        PreCon.notNull(destination);

        int delta = source.length - destination.length;
        int sourceAdjust = delta < 0 ?  0 : delta;
        int destAdjust = delta < 0 ? -delta : 0;

        System.arraycopy(source, sourceAdjust, destination, destAdjust, destination.length - destAdjust);

        return destination;
    }

    /**
     * Copy the source array elements to the destination array starting from the end
     * and finishing when either the destination runs out of space or the source runs
     * out of elements.
     *
     * @param source       The source array.
     * @param destination  The destination array.
     *
     * @return  The destination array.
     */
    public static byte[] copyFromEnd(byte[] source, byte[] destination) {
        PreCon.notNull(source);
        PreCon.notNull(destination);

        int delta = source.length - destination.length;
        int sourceAdjust = delta < 0 ?  0 : delta;
        int destAdjust = delta < 0 ? -delta : 0;

        System.arraycopy(source, sourceAdjust, destination, destAdjust, destination.length - destAdjust);

        return destination;
    }

    /**
     * Copy the source array elements to the destination array starting from the end
     * and finishing when either the destination runs out of space or the source runs
     * out of elements.
     *
     * @param source       The source array.
     * @param destination  The destination array.
     *
     * @return  The destination array.
     */
    public static char[] copyFromEnd(char[] source, char[] destination) {
        PreCon.notNull(source);
        PreCon.notNull(destination);

        int delta = source.length - destination.length;
        int sourceAdjust = delta < 0 ?  0 : delta;
        int destAdjust = delta < 0 ? -delta : 0;

        System.arraycopy(source, sourceAdjust, destination, destAdjust, destination.length - destAdjust);

        return destination;
    }

    /**
     * Copy the source array elements to the destination array starting from the end
     * and finishing when either the destination runs out of space or the source runs
     * out of elements.
     *
     * @param source       The source array.
     * @param destination  The destination array.
     *
     * @return  The destination array.
     */
    public static short[] copyFromEnd(short[] source, short[] destination) {
        PreCon.notNull(source);
        PreCon.notNull(destination);

        int delta = source.length - destination.length;
        int sourceAdjust = delta < 0 ?  0 : delta;
        int destAdjust = delta < 0 ? -delta : 0;

        System.arraycopy(source, sourceAdjust, destination, destAdjust, destination.length - destAdjust);

        return destination;
    }

    /**
     * Copy the source array elements to the destination array starting from the end
     * and finishing when either the destination runs out of space or the source runs
     * out of elements.
     *
     * @param source       The source array.
     * @param destination  The destination array.
     *
     * @return  The destination array.
     */
    public static int[] copyFromEnd(int[] source, int[] destination) {
        PreCon.notNull(source);
        PreCon.notNull(destination);

        int delta = source.length - destination.length;
        int sourceAdjust = delta < 0 ?  0 : delta;
        int destAdjust = delta < 0 ? -delta : 0;

        System.arraycopy(source, sourceAdjust, destination, destAdjust, destination.length - destAdjust);

        return destination;
    }

    /**
     * Copy the source array elements to the destination array starting from the end
     * and finishing when either the destination runs out of space or the source runs
     * out of elements.
     *
     * @param source       The source array.
     * @param destination  The destination array.
     *
     * @return  The destination array.
     */
    public static long[] copyFromEnd(long[] source, long[] destination) {
        PreCon.notNull(source);
        PreCon.notNull(destination);

        int delta = source.length - destination.length;
        int sourceAdjust = delta < 0 ?  0 : delta;
        int destAdjust = delta < 0 ? -delta : 0;

        System.arraycopy(source, sourceAdjust, destination, destAdjust, destination.length - destAdjust);

        return destination;
    }

    /**
     * Copy the source array elements to the destination array starting from the end
     * and finishing when either the destination runs out of space or the source runs
     * out of elements.
     *
     * @param source       The source array.
     * @param destination  The destination array.
     *
     * @return  The destination array.
     */
    public static float[] copyFromEnd(float[] source, float[] destination) {
        PreCon.notNull(source);
        PreCon.notNull(destination);

        int delta = source.length - destination.length;
        int sourceAdjust = delta < 0 ?  0 : delta;
        int destAdjust = delta < 0 ? -delta : 0;

        System.arraycopy(source, sourceAdjust, destination, destAdjust, destination.length - destAdjust);

        return destination;
    }

    /**
     * Copy the source array elements to the destination array starting from the end
     * and finishing when either the destination runs out of space or the source runs
     * out of elements.
     *
     * @param source       The source array.
     * @param destination  The destination array.
     *
     * @return  The destination array.
     */
    public static double[] copyFromEnd(double[] source, double[] destination) {
        PreCon.notNull(source);
        PreCon.notNull(destination);

        int delta = source.length - destination.length;
        int sourceAdjust = delta < 0 ?  0 : delta;
        int destAdjust = delta < 0 ? -delta : 0;

        System.arraycopy(source, sourceAdjust, destination, destAdjust, destination.length - destAdjust);

        return destination;
    }

    /**
     * Reduce the size of an array by trimming from the beginning and end of the array.
     *
     * @param startAmountToRemove  The number of elements to remove from the start of the array.
     * @param array                The array to trim.
     * @param endAmountToRemove    The number of elements to remove from the end of the array.
     *
     * @param <T>  The component type.
     *
     * @return  A new trimmed array.
     */
    public static <T> T[] reduce(int startAmountToRemove, T[] array, int endAmountToRemove) {
        PreCon.positiveNumber(startAmountToRemove);
        PreCon.notNull(array);
        PreCon.positiveNumber(endAmountToRemove);
        PreCon.isValid(startAmountToRemove + endAmountToRemove <= array.length,
                "Amount to remove is larger than the array.");

        @SuppressWarnings("unchecked")
        Class<T> componentClass = (Class<T>) array.getClass().getComponentType();

        int size = array.length - (startAmountToRemove + endAmountToRemove);

        if (size == 0)
            return newArray(componentClass, 0);

        T[] newArray = newArray(componentClass, size);

        System.arraycopy(array, startAmountToRemove - 1, newArray, 0, newArray.length);

        return newArray;
    }

    /**
     * Reduce the size of an array by trimming from the beginning and end of the array.
     *
     * @param startAmountToRemove  The number of elements to remove from the start of the array.
     * @param array                The array to trim.
     * @param endAmountToRemove    The number of elements to remove from the end of the array.
     *
     * @return  A new trimmed array.
     */
    public static boolean[] reduce(int startAmountToRemove, boolean[] array, int endAmountToRemove) {
        PreCon.positiveNumber(startAmountToRemove);
        PreCon.notNull(array);
        PreCon.positiveNumber(endAmountToRemove);
        PreCon.isValid(startAmountToRemove + endAmountToRemove <= array.length,
                "Amount to remove is larger than the array.");

        int size = array.length - (startAmountToRemove + endAmountToRemove);

        if (size == 0)
            return EMPTY_BOOLEAN_ARRAY;

        boolean[] newArray = new boolean[size];

        System.arraycopy(array, startAmountToRemove - 1, newArray, 0, newArray.length);

        return newArray;
    }

    /**
     * Reduce the size of an array by trimming from the beginning and end of the array.
     *
     * @param startAmountToRemove  The number of elements to remove from the start of the array.
     * @param array                The array to trim.
     * @param endAmountToRemove    The number of elements to remove from the end of the array.
     *
     * @return  A new trimmed array.
     */
    public static byte[] reduce(int startAmountToRemove, byte[] array, int endAmountToRemove) {
        PreCon.positiveNumber(startAmountToRemove);
        PreCon.notNull(array);
        PreCon.positiveNumber(endAmountToRemove);
        PreCon.isValid(startAmountToRemove + endAmountToRemove <= array.length,
                "Amount to remove is larger than the array.");

        int size = array.length - (startAmountToRemove + endAmountToRemove);

        if (size == 0)
            return EMPTY_BYTE_ARRAY;

        byte[] newArray = new byte[size];

        System.arraycopy(array, startAmountToRemove - 1, newArray, 0, newArray.length);

        return newArray;
    }

    /**
     * Reduce the size of an array by trimming from the beginning and end of the array.
     *
     * @param startAmountToRemove  The number of elements to remove from the start of the array.
     * @param array                The array to trim.
     * @param endAmountToRemove    The number of elements to remove from the end of the array.
     *
     * @return  A new trimmed array.
     */
    public static char[] reduce(int startAmountToRemove, char[] array, int endAmountToRemove) {
        PreCon.positiveNumber(startAmountToRemove);
        PreCon.notNull(array);
        PreCon.positiveNumber(endAmountToRemove);
        PreCon.isValid(startAmountToRemove + endAmountToRemove <= array.length,
                "Amount to remove is larger than the array.");

        int size = array.length - (startAmountToRemove + endAmountToRemove);

        if (size == 0)
            return EMPTY_CHAR_ARRAY;

        char[] newArray = new char[size];

        System.arraycopy(array, startAmountToRemove - 1, newArray, 0, newArray.length);

        return newArray;
    }

    /**
     * Reduce the size of an array by trimming from the beginning and end of the array.
     *
     * @param startAmountToRemove  The number of elements to remove from the start of the array.
     * @param array                The array to trim.
     * @param endAmountToRemove    The number of elements to remove from the end of the array.
     *
     * @return  A new trimmed array.
     */
    public static short[] reduce(int startAmountToRemove, short[] array, int endAmountToRemove) {
        PreCon.positiveNumber(startAmountToRemove);
        PreCon.notNull(array);
        PreCon.positiveNumber(endAmountToRemove);
        PreCon.isValid(startAmountToRemove + endAmountToRemove <= array.length,
                "Amount to remove is larger than the array.");

        int size = array.length - (startAmountToRemove + endAmountToRemove);

        if (size == 0)
            return EMPTY_SHORT_ARRAY;

        short[] newArray = new short[size];

        System.arraycopy(array, startAmountToRemove - 1, newArray, 0, newArray.length);

        return newArray;
    }

    /**
     * Reduce the size of an array by trimming from the beginning and end of the array.
     *
     * @param startAmountToRemove  The number of elements to remove from the start of the array.
     * @param array                The array to trim.
     * @param endAmountToRemove    The number of elements to remove from the end of the array.
     *
     * @return  A new trimmed array.
     */
    public static int[] reduce(int startAmountToRemove, int[] array, int endAmountToRemove) {
        PreCon.positiveNumber(startAmountToRemove);
        PreCon.notNull(array);
        PreCon.positiveNumber(endAmountToRemove);
        PreCon.isValid(startAmountToRemove + endAmountToRemove <= array.length,
                "Amount to remove is larger than the array.");

        int size = array.length - (startAmountToRemove + endAmountToRemove);

        if (size == 0)
            return EMPTY_INT_ARRAY;

        int[] newArray = new int[size];

        System.arraycopy(array, startAmountToRemove - 1, newArray, 0, newArray.length);

        return newArray;
    }

    /**
     * Reduce the size of an array by trimming from the beginning and end of the array.
     *
     * @param startAmountToRemove  The number of elements to remove from the start of the array.
     * @param array                The array to trim.
     * @param endAmountToRemove    The number of elements to remove from the end of the array.
     *
     * @return  A new trimmed array.
     */
    public static long[] reduce(int startAmountToRemove, long[] array, int endAmountToRemove) {
        PreCon.positiveNumber(startAmountToRemove);
        PreCon.notNull(array);
        PreCon.positiveNumber(endAmountToRemove);
        PreCon.isValid(startAmountToRemove + endAmountToRemove <= array.length,
                "Amount to remove is larger than the array.");

        int size = array.length - (startAmountToRemove + endAmountToRemove);

        if (size == 0)
            return EMPTY_LONG_ARRAY;

        long[] newArray = new long[size];

        System.arraycopy(array, startAmountToRemove - 1, newArray, 0, newArray.length);

        return newArray;
    }

    /**
     * Reduce the size of an array by trimming from the beginning and end of the array.
     *
     * @param startAmountToRemove  The number of elements to remove from the start of the array.
     * @param array                The array to trim.
     * @param endAmountToRemove    The number of elements to remove from the end of the array.
     *
     * @return  A new trimmed array.
     */
    public static float[] reduce(int startAmountToRemove, float[] array, int endAmountToRemove) {
        PreCon.positiveNumber(startAmountToRemove);
        PreCon.notNull(array);
        PreCon.positiveNumber(endAmountToRemove);
        PreCon.isValid(startAmountToRemove + endAmountToRemove <= array.length,
                "Amount to remove is larger than the array.");

        int size = array.length - (startAmountToRemove + endAmountToRemove);

        if (size == 0)
            return EMPTY_FLOAT_ARRAY;

        float[] newArray = new float[size];

        System.arraycopy(array, startAmountToRemove - 1, newArray, 0, newArray.length);

        return newArray;
    }

    /**
     * Reduce the size of an array by trimming from the beginning and end of the array.
     *
     * @param startAmountToRemove  The number of elements to remove from the start of the array.
     * @param array                The array to trim.
     * @param endAmountToRemove    The number of elements to remove from the end of the array.
     *
     * @return  A new trimmed array.
     */
    public static double[] reduce(int startAmountToRemove, double[] array, int endAmountToRemove) {
        PreCon.positiveNumber(startAmountToRemove);
        PreCon.notNull(array);
        PreCon.positiveNumber(endAmountToRemove);
        PreCon.isValid(startAmountToRemove + endAmountToRemove <= array.length,
                "Amount to remove is larger than the array.");

        int size = array.length - (startAmountToRemove + endAmountToRemove);

        if (size == 0)
            return EMPTY_DOUBLE_ARRAY;

        double[] newArray = new double[size];

        System.arraycopy(array, startAmountToRemove - 1, newArray, 0, newArray.length);

        return newArray;
    }

    /**
     * Reduce the size of an array by trimming from the beginning and end of the array.
     *
     * @param startAmountToRemove  The number of elements to remove from the start of the array.
     * @param array                The array to trim.
     * @param endAmountToRemove    The number of elements to remove from the end of the array.
     *
     * @return  A new trimmed array.
     */
    public static String[] reduce(int startAmountToRemove, String[] array, int endAmountToRemove) {
        PreCon.positiveNumber(startAmountToRemove);
        PreCon.notNull(array);
        PreCon.positiveNumber(endAmountToRemove);
        PreCon.isValid(startAmountToRemove + endAmountToRemove <= array.length,
                "Amount to remove is larger than the array.");

        int size = array.length - (startAmountToRemove + endAmountToRemove);

        if (size == 0)
            return EMPTY_STRING_ARRAY;

        String[] newArray = new String[size];

        System.arraycopy(array, startAmountToRemove - 1, newArray, 0, newArray.length);

        return newArray;
    }

    /**
     * Reduce the size of an array by trimming from the beginning and end of the array.
     *
     * @param startAmountToRemove  The number of elements to remove from the start of the array.
     * @param array                The array to trim.
     * @param endAmountToRemove    The number of elements to remove from the end of the array.
     *
     * @return  A new trimmed array.
     */
    public static ItemStack[] reduce(int startAmountToRemove, ItemStack[] array, int endAmountToRemove) {
        PreCon.positiveNumber(startAmountToRemove);
        PreCon.notNull(array);
        PreCon.positiveNumber(endAmountToRemove);
        PreCon.isValid(startAmountToRemove + endAmountToRemove <= array.length,
                "Amount to remove is larger than the array.");

        int size = array.length - (startAmountToRemove + endAmountToRemove);

        if (size == 0)
            return EMPTY_ITEMSTACK_ARRAY;

        ItemStack[] newArray = new ItemStack[size];

        System.arraycopy(array, startAmountToRemove - 1, newArray, 0, newArray.length);

        return newArray;
    }

    /**
     * Reduce the size of an array by trimming from the beginning and end of the array.
     *
     * @param startAmountToRemove  The number of elements to remove from the start of the array.
     * @param array                The array to trim.
     * @param endAmountToRemove    The number of elements to remove from the end of the array.
     *
     * @return  A new trimmed array.
     */
    public static Entity[] reduce(int startAmountToRemove, Entity[] array, int endAmountToRemove) {
        PreCon.positiveNumber(startAmountToRemove);
        PreCon.notNull(array);
        PreCon.positiveNumber(endAmountToRemove);
        PreCon.isValid(startAmountToRemove + endAmountToRemove <= array.length,
                "Amount to remove is larger than the array.");

        int size = array.length - (startAmountToRemove + endAmountToRemove);

        if (size == 0)
            return EMPTY_ENTITY_ARRAY;

        Entity[] newArray = new Entity[size];

        System.arraycopy(array, startAmountToRemove - 1, newArray, 0, newArray.length);

        return newArray;
    }

    /**
     * Reduce the size of an array by trimming from the beginning of the array.
     *
     * @param amountToRemove  The number of elements to remove.
     * @param array           The array to trim.
     *
     * @param <T>  The component type.
     *
     * @return A new trimmed array.
     */
    public static <T> T[] reduceStart(int amountToRemove, T[] array) {
        PreCon.notNull(array);
        PreCon.isValid(amountToRemove <= array.length, "Amount to remove is larger than the array.");

        @SuppressWarnings("unchecked")
        Class<T> componentClass = (Class<T>) array.getClass().getComponentType();

        if (array.length == amountToRemove)
            return newArray(componentClass, 0);

        int size = array.length - amountToRemove;

        T[] result = newArray(componentClass, size);

        return copyFromEnd(array, result);
    }

    /**
     * Reduce the size of an array by trimming from the beginning of the array.
     *
     * @param amountToRemove  The number of elements to remove.
     * @param array           The array to trim.
     *
     * @return A new trimmed array.
     */
    public static boolean[] reduceStart(int amountToRemove, boolean[] array) {
        PreCon.notNull(array);
        PreCon.isValid(amountToRemove <= array.length, "Amount to remove is larger than the array.");

        if (array.length == amountToRemove)
            return EMPTY_BOOLEAN_ARRAY;

        int size = array.length - amountToRemove;

        return copyFromEnd(array, new boolean[size]);
    }

    /**
     * Reduce the size of an array by trimming from the beginning of the array.
     *
     * @param amountToRemove  The number of elements to remove.
     * @param array           The array to trim.
     *
     * @return A new trimmed array.
     */
    public static byte[] reduceStart(int amountToRemove, byte[] array) {
        PreCon.notNull(array);
        PreCon.isValid(amountToRemove <= array.length, "Amount to remove is larger than the array.");

        if (array.length == amountToRemove)
            return EMPTY_BYTE_ARRAY;

        int size = array.length - amountToRemove;

        return copyFromEnd(array, new byte[size]);
    }

    /**
     * Reduce the size of an array by trimming from the beginning of the array.
     *
     * @param amountToRemove  The number of elements to remove.
     * @param array           The array to trim.
     *
     * @return A new trimmed array.
     */
    public static char[] reduceStart(int amountToRemove, char[] array) {
        PreCon.notNull(array);
        PreCon.isValid(amountToRemove <= array.length, "Amount to remove is larger than the array.");

        if (array.length == amountToRemove)
            return EMPTY_CHAR_ARRAY;

        int size = array.length - amountToRemove;

        return copyFromEnd(array, new char[size]);
    }

    /**
     * Reduce the size of an array by trimming from the beginning of the array.
     *
     * @param amountToRemove  The number of elements to remove.
     * @param array           The array to trim.
     *
     * @return A new trimmed array.
     */
    public static short[] reduceStart(int amountToRemove, short[] array) {
        PreCon.notNull(array);
        PreCon.isValid(amountToRemove <= array.length, "Amount to remove is larger than the array.");

        if (array.length == amountToRemove)
            return EMPTY_SHORT_ARRAY;

        int size = array.length - amountToRemove;

        return copyFromEnd(array, new short[size]);
    }

    /**
     * Reduce the size of an array by trimming from the beginning of the array.
     *
     * @param amountToRemove  The number of elements to remove.
     * @param array           The array to trim.
     *
     * @return A new trimmed array.
     */
    public static int[] reduceStart(int amountToRemove, int[] array) {
        PreCon.notNull(array);
        PreCon.isValid(amountToRemove <= array.length, "Amount to remove is larger than the array.");

        if (array.length == amountToRemove)
            return EMPTY_INT_ARRAY;

        int size = array.length - amountToRemove;

        return copyFromEnd(array, new int[size]);
    }

    /**
     * Reduce the size of an array by trimming from the beginning of the array.
     *
     * @param amountToRemove  The number of elements to remove.
     * @param array           The array to trim.
     *
     * @return A new trimmed array.
     */
    public static long[] reduceStart(int amountToRemove, long[] array) {
        PreCon.notNull(array);
        PreCon.isValid(amountToRemove <= array.length, "Amount to remove is larger than the array.");

        if (array.length == amountToRemove)
            return EMPTY_LONG_ARRAY;

        int size = array.length - amountToRemove;

        return copyFromEnd(array, new long[size]);
    }

    /**
     * Reduce the size of an array by trimming from the beginning of the array.
     *
     * @param amountToRemove  The number of elements to remove.
     * @param array           The array to trim.
     *
     * @return A new trimmed array.
     */
    public static float[] reduceStart(int amountToRemove, float[] array) {
        PreCon.notNull(array);
        PreCon.isValid(amountToRemove <= array.length, "Amount to remove is larger than the array.");

        if (array.length == amountToRemove)
            return EMPTY_FLOAT_ARRAY;

        int size = array.length - amountToRemove;

        return copyFromEnd(array, new float[size]);
    }

    /**
     * Reduce the size of an array by trimming from the beginning of the array.
     *
     * @param amountToRemove  The number of elements to remove.
     * @param array           The array to trim.
     *
     * @return A new trimmed array.
     */
    public static double[] reduceStart(int amountToRemove, double[] array) {
        PreCon.notNull(array);
        PreCon.isValid(amountToRemove <= array.length, "Amount to remove is larger than the array.");

        if (array.length == amountToRemove)
            return EMPTY_DOUBLE_ARRAY;

        int size = array.length - amountToRemove;

        return copyFromEnd(array, new double[size]);
    }

    /**
     * Reduce the size of an array by trimming from the beginning of the array.
     *
     * @param amountToRemove  The number of elements to remove.
     * @param array           The array to trim.
     *
     * @return A new trimmed array.
     */
    public static String[] reduceStart(int amountToRemove, String[] array) {
        PreCon.notNull(array);
        PreCon.isValid(amountToRemove <= array.length, "Amount to remove is larger than the array.");

        if (array.length == amountToRemove)
            return EMPTY_STRING_ARRAY;

        int size = array.length - amountToRemove;

        return copyFromEnd(array, new String[size]);
    }

    /**
     * Reduce the size of an array by trimming from the beginning of the array.
     *
     * @param amountToRemove  The number of elements to remove.
     * @param array           The array to trim.
     *
     * @return A new trimmed array.
     */
    public static ItemStack[] reduceStart(int amountToRemove, ItemStack[] array) {
        PreCon.notNull(array);
        PreCon.isValid(amountToRemove <= array.length, "Amount to remove is larger than the array.");

        if (array.length == amountToRemove)
            return EMPTY_ITEMSTACK_ARRAY;

        int size = array.length - amountToRemove;

        return copyFromEnd(array, new ItemStack[size]);
    }

    /**
     * Reduce the size of an array by trimming from the beginning of the array.
     *
     * @param amountToRemove  The number of elements to remove.
     * @param array           The array to trim.
     *
     * @return A new trimmed array.
     */
    public static Entity[] reduceStart(int amountToRemove, Entity[] array) {
        PreCon.notNull(array);
        PreCon.isValid(amountToRemove <= array.length, "Amount to remove is larger than the array.");

        if (array.length == amountToRemove)
            return EMPTY_ENTITY_ARRAY;

        int size = array.length - amountToRemove;

        return copyFromEnd(array, new Entity[size]);
    }

    /**
     * Reduce the size of an array by trimming from the end of the array.
     *
     * @param array           The array to trim.
     * @param amountToRemove  The number of elements to remove.
     *
     * @param <T>  The array component type.
     *
     * @return  A new trimmed array.
     */
    public static <T> T[] reduceEnd(T[] array, int amountToRemove) {
        PreCon.notNull(array);
        PreCon.isValid(amountToRemove <= array.length, "Amount to remove is larger than the array.");

        @SuppressWarnings("unchecked")
        Class<T> componentClass = (Class<T>) array.getClass().getComponentType();

        if (array.length == amountToRemove)
            return newArray(componentClass, 0);

        int size = array.length - amountToRemove;

        T[] result = newArray(componentClass, size);

        return copyFromStart(array, result);
    }

    /**
     * Reduce the size of an array by trimming from the end of the array.
     *
     * @param array           The array to trim.
     * @param amountToRemove  The number of elements to remove.
     *
     * @return  A new trimmed array.
     */
    public static boolean[] reduceEnd(boolean[] array, int amountToRemove) {
        PreCon.notNull(array);
        PreCon.isValid(amountToRemove <= array.length, "Amount to remove is larger than the array.");

        if (array.length == amountToRemove)
            return EMPTY_BOOLEAN_ARRAY;

        int size = array.length - amountToRemove;

        return copyFromStart(array, new boolean[size]);
    }

    /**
     * Reduce the size of an array by trimming from the end of the array.
     *
     * @param array           The array to trim.
     * @param amountToRemove  The number of elements to remove.
     *
     * @return  A new trimmed array.
     */
    public static byte[] reduceEnd(byte[] array, int amountToRemove) {
        PreCon.notNull(array);
        PreCon.isValid(amountToRemove <= array.length, "Amount to remove is larger than the array.");

        if (array.length == amountToRemove)
            return EMPTY_BYTE_ARRAY;

        int size = array.length - amountToRemove;

        return copyFromStart(array, new byte[size]);
    }

    /**
     * Reduce the size of an array by trimming from the end of the array.
     *
     * @param array           The array to trim.
     * @param amountToRemove  The number of elements to remove.
     *
     * @return  A new trimmed array.
     */
    public static char[] reduceEnd(char[] array, int amountToRemove) {
        PreCon.notNull(array);
        PreCon.isValid(amountToRemove <= array.length, "Amount to remove is larger than the array.");

        if (array.length == amountToRemove)
            return EMPTY_CHAR_ARRAY;

        int size = array.length - amountToRemove;

        return copyFromStart(array, new char[size]);
    }

    /**
     * Reduce the size of an array by trimming from the end of the array.
     *
     * @param array           The array to trim.
     * @param amountToRemove  The number of elements to remove.
     *
     * @return  A new trimmed array.
     */
    public static short[] reduceEnd(short[] array, int amountToRemove) {
        PreCon.notNull(array);
        PreCon.isValid(amountToRemove <= array.length, "Amount to remove is larger than the array.");

        if (array.length == amountToRemove)
            return EMPTY_SHORT_ARRAY;

        int size = array.length - amountToRemove;

        return copyFromStart(array, new short[size]);
    }

    /**
     * Reduce the size of an array by trimming from the end of the array.
     *
     * @param array           The array to trim.
     * @param amountToRemove  The number of elements to remove.
     *
     * @return  A new trimmed array.
     */
    public static int[] reduceEnd(int[] array, int amountToRemove) {
        PreCon.notNull(array);
        PreCon.isValid(amountToRemove <= array.length, "Amount to remove is larger than the array.");

        if (array.length == amountToRemove)
            return EMPTY_INT_ARRAY;

        int size = array.length - amountToRemove;

        return copyFromStart(array, new int[size]);
    }

    /**
     * Reduce the size of an array by trimming from the end of the array.
     *
     * @param array           The array to trim.
     * @param amountToRemove  The number of elements to remove.
     *
     * @return  A new trimmed array.
     */
    public static long[] reduceEnd(long[] array, int amountToRemove) {
        PreCon.notNull(array);
        PreCon.isValid(amountToRemove <= array.length, "Amount to remove is larger than the array.");

        if (array.length == amountToRemove)
            return EMPTY_LONG_ARRAY;

        int size = array.length - amountToRemove;

        return copyFromStart(array, new long[size]);
    }

    /**
     * Reduce the size of an array by trimming from the end of the array.
     *
     * @param array           The array to trim.
     * @param amountToRemove  The number of elements to remove.
     *
     * @return  A new trimmed array.
     */
    public static float[] reduceEnd(float[] array, int amountToRemove) {
        PreCon.notNull(array);
        PreCon.isValid(amountToRemove <= array.length, "Amount to remove is larger than the array.");

        if (array.length == amountToRemove)
            return EMPTY_FLOAT_ARRAY;

        int size = array.length - amountToRemove;

        return copyFromStart(array, new float[size]);
    }

    /**
     * Reduce the size of an array by trimming from the end of the array.
     *
     * @param array           The array to trim.
     * @param amountToRemove  The number of elements to remove.
     *
     * @return  A new trimmed array.
     */
    public static double[] reduceEnd(double[] array, int amountToRemove) {
        PreCon.notNull(array);
        PreCon.isValid(amountToRemove <= array.length, "Amount to remove is larger than the array.");

        if (array.length == amountToRemove)
            return EMPTY_DOUBLE_ARRAY;

        int size = array.length - amountToRemove;

        return copyFromStart(array, new double[size]);
    }

    /**
     * Reduce the size of an array by trimming from the end of the array.
     *
     * @param array           The array to trim.
     * @param amountToRemove  The number of elements to remove.
     *
     * @return  A new trimmed array.
     */
    public static String[] reduceEnd(String[] array, int amountToRemove) {
        PreCon.notNull(array);
        PreCon.isValid(amountToRemove <= array.length, "Amount to remove is larger than the array.");

        if (array.length == amountToRemove)
            return EMPTY_STRING_ARRAY;

        int size = array.length - amountToRemove;

        return copyFromStart(array, new String[size]);
    }

    /**
     * Reduce the size of an array by trimming from the end of the array.
     *
     * @param array           The array to trim.
     * @param amountToRemove  The number of elements to remove.
     *
     * @return  A new trimmed array.
     */
    public static ItemStack[] reduceEnd(ItemStack[] array, int amountToRemove) {
        PreCon.notNull(array);
        PreCon.isValid(amountToRemove <= array.length, "Amount to remove is larger than the array.");

        if (array.length == amountToRemove)
            return EMPTY_ITEMSTACK_ARRAY;

        int size = array.length - amountToRemove;

        return copyFromStart(array, new ItemStack[size]);
    }

    /**
     * Reduce the size of an array by trimming from the end of the array.
     *
     * @param array           The array to trim.
     * @param amountToRemove  The number of elements to remove.
     *
     * @return  A new trimmed array.
     */
    public static Entity[] reduceEnd(Entity[] array, int amountToRemove) {
        PreCon.notNull(array);
        PreCon.isValid(amountToRemove <= array.length, "Amount to remove is larger than the array.");

        if (array.length == amountToRemove)
            return EMPTY_ENTITY_ARRAY;

        int size = array.length - amountToRemove;

        return copyFromStart(array, new Entity[size]);
    }

    /**
     * Convert a primitive wrapper array to a primitive array.
     *
     * @param array  The array to convert.
     *
     * @return A new primitive array.
     */
    public static boolean[] toPrimitive(Boolean[] array) {
        PreCon.notNull(array);

        if (array.length == 0)
            return EMPTY_BOOLEAN_ARRAY;

        boolean[] newArray = new boolean[array.length];

        for (int i=0; i < array.length; i++) {
            Boolean element = array[i];
            newArray[i] = element == null ? false : element;
        }

        return newArray;
    }

    /**
     * Convert a primitive wrapper array to a primitive array.
     *
     * @param array  The array to convert.
     *
     * @return A new primitive array.
     */
    public static byte[] toPrimitive(Byte[] array) {
        PreCon.notNull(array);

        if (array.length == 0)
            return EMPTY_BYTE_ARRAY;

        byte[] newArray = new byte[array.length];

        for (int i=0; i < array.length; i++) {
            Byte element = array[i];
            newArray[i] = element == null ? 0 : element;
        }

        return newArray;
    }

    /**
     * Convert a primitive wrapper array to a primitive array.
     *
     * @param array  The array to convert.
     *
     * @return A new primitive array.
     */
    public static char[] toPrimitive(Character[] array) {
        PreCon.notNull(array);

        if (array.length == 0)
            return EMPTY_CHAR_ARRAY;

        char[] newArray = new char[array.length];

        for (int i=0; i < array.length; i++) {
            Character element = array[i];
            newArray[i] = element == null ? 0 : element;
        }

        return newArray;
    }

    /**
     * Convert a primitive wrapper array to a primitive array.
     *
     * @param array  The array to convert.
     *
     * @return A new primitive array.
     */
    public static short[] toPrimitive(Short[] array) {
        PreCon.notNull(array);

        if (array.length == 0)
            return EMPTY_SHORT_ARRAY;

        short[] newArray = new short[array.length];

        for (int i=0; i < array.length; i++) {
            Short element = array[i];
            newArray[i] = element == null ? 0 : element;
        }

        return newArray;
    }

    /**
     * Convert a primitive wrapper array to a primitive array.
     *
     * @param array  The array to convert.
     *
     * @return A new primitive array.
     */
    public static int[] toPrimitive(Integer[] array) {
        PreCon.notNull(array);

        if (array.length == 0)
            return EMPTY_INT_ARRAY;

        int[] newArray = new int[array.length];

        for (int i=0; i < array.length; i++) {
            Integer element = array[i];
            newArray[i] = element == null ? 0 : element;
        }

        return newArray;
    }

    /**
     * Convert a primitive wrapper array to a primitive array.
     *
     * @param array  The array to convert.
     *
     * @return A new primitive array.
     */
    public static long[] toPrimitive(Long[] array) {
        PreCon.notNull(array);

        if (array.length == 0)
            return EMPTY_LONG_ARRAY;

        long[] newArray = new long[array.length];

        for (int i=0; i < array.length; i++) {
            Long element = array[i];
            newArray[i] = element == null ? 0L : element;
        }

        return newArray;
    }

    /**
     * Convert a primitive wrapper array to a primitive array.
     *
     * @param array  The array to convert.
     *
     * @return A new primitive array.
     */
    public static float[] toPrimitive(Float[] array) {
        PreCon.notNull(array);

        if (array.length == 0)
            return EMPTY_FLOAT_ARRAY;

        float[] newArray = new float[array.length];

        for (int i=0; i < array.length; i++) {
            Float element = array[i];
            newArray[i] = element == null ? 0F : element;
        }

        return newArray;
    }

    /**
     * Convert a primitive wrapper array to a primitive array.
     *
     * @param array  The array to convert.
     *
     * @return A new primitive array.
     */
    public static double[] toPrimitive(Double[] array) {
        PreCon.notNull(array);

        if (array.length == 0)
            return EMPTY_DOUBLE_ARRAY;

        double[] newArray = new double[array.length];

        for (int i=0; i < array.length; i++) {
            Double element = array[i];
            newArray[i] = element == null ? 0 : element;
        }

        return newArray;
    }

    /**
     * Convert a primitive array to a primitive wrapper array.
     *
     * @param array  The array to convert.
     *
     * @return A new primitive wrapper array.
     */
    public static Boolean[] toWrapper(boolean[] array) {
        PreCon.notNull(array);

        Boolean[] newArray = new Boolean[array.length];

        for (int i=0; i < array.length; i++) {
            newArray[i] = array[i];
        }

        return newArray;
    }

    /**
     * Convert a primitive array to a primitive wrapper array.
     *
     * @param array  The array to convert.
     *
     * @return A new primitive wrapper array.
     */
    public static Byte[] toWrapper(byte[] array) {
        PreCon.notNull(array);

        Byte[] newArray = new Byte[array.length];

        for (int i=0; i < array.length; i++) {
            newArray[i] = array[i];
        }

        return newArray;
    }

    /**
     * Convert a primitive array to a primitive wrapper array.
     *
     * @param array  The array to convert.
     *
     * @return A new primitive wrapper array.
     */
    public static Character[] toWrapper(char[] array) {
        PreCon.notNull(array);

        Character[] newArray = new Character[array.length];

        for (int i=0; i < array.length; i++) {
            newArray[i] = array[i];
        }

        return newArray;
    }

    /**
     * Convert a primitive array to a primitive wrapper array.
     *
     * @param array  The array to convert.
     *
     * @return A new primitive wrapper array.
     */
    public static Short[] toWrapper(short[] array) {
        PreCon.notNull(array);

        Short[] newArray = new Short[array.length];

        for (int i=0; i < array.length; i++) {
            newArray[i] = array[i];
        }

        return newArray;
    }

    /**
     * Convert a primitive array to a primitive wrapper array.
     *
     * @param array  The array to convert.
     *
     * @return A new primitive wrapper array.
     */
    public static Integer[] toWrapper(int[] array) {
        PreCon.notNull(array);

        Integer[] newArray = new Integer[array.length];

        for (int i=0; i < array.length; i++) {
            newArray[i] = array[i];
        }

        return newArray;
    }

    /**
     * Convert a primitive array to a primitive wrapper array.
     *
     * @param array  The array to convert.
     *
     * @return A new primitive wrapper array.
     */
    public static Long[] toWrapper(long[] array) {
        PreCon.notNull(array);

        Long[] newArray = new Long[array.length];

        for (int i=0; i < array.length; i++) {
            newArray[i] = array[i];
        }

        return newArray;
    }

    /**
     * Convert a primitive array to a primitive wrapper array.
     *
     * @param array  The array to convert.
     *
     * @return A new primitive wrapper array.
     */
    public static Float[] toWrapper(float[] array) {
        PreCon.notNull(array);

        Float[] newArray = new Float[array.length];

        for (int i=0; i < array.length; i++) {
            newArray[i] = array[i];
        }

        return newArray;
    }

    /**
     * Convert a primitive array to a primitive wrapper array.
     *
     * @param array  The array to convert.
     *
     * @return A new primitive wrapper array.
     */
    public static Double[] toWrapper(double[] array) {
        PreCon.notNull(array);

        Double[] newArray = new Double[array.length];

        for (int i=0; i < array.length; i++) {
            newArray[i] = array[i];
        }

        return newArray;
    }

    /**
     * Get the element at the specified index of the array
     * or return the default value if the array is smaller
     * than the specified index.
     *
     * @param array         The array to get an element from.
     * @param index         The index of the element to get.
     * @param defaultValue  The default value if the array isn't large enough.
     *
     * @param <T>  The array component type.
     *
     * @return  The element at the specified index or the default value.
     */
    public static <T> T get(T[] array, int index, T defaultValue) {
        PreCon.notNull(array);

        return (index > array.length - 1)
                ? defaultValue
                : array[index];
    }

    /**
     * Get the element at the specified index of the array
     * or return the default value if the array is smaller
     * than the specified index.
     *
     * @param array         The array to get an element from.
     * @param index         The index of the element to get.
     * @param defaultValue  The default value if the array isn't large enough.
     *
     * @return  The element at the specified index or the default value.
     */
    public static boolean get(boolean[] array, int index, boolean defaultValue) {
        PreCon.notNull(array);

        return (index > array.length - 1)
                ? defaultValue
                : array[index];
    }

    /**
     * Get the element at the specified index of the array
     * or return the default value if the array is smaller
     * than the specified index.
     *
     * @param array         The array to get an element from.
     * @param index         The index of the element to get.
     * @param defaultValue  The default value if the array isn't large enough.
     *
     * @return  The element at the specified index or the default value.
     */
    public static byte get(byte[] array, int index, byte defaultValue) {
        PreCon.notNull(array);

        return (index > array.length - 1)
                ? defaultValue
                : array[index];
    }

    /**
     * Get the element at the specified index of the array
     * or return the default value if the array is smaller
     * than the specified index.
     *
     * @param array         The array to get an element from.
     * @param index         The index of the element to get.
     * @param defaultValue  The default value if the array isn't large enough.
     *
     * @return  The element at the specified index or the default value.
     */
    public static short get(short[] array, int index, short defaultValue) {
        PreCon.notNull(array);

        return (index > array.length - 1)
                ? defaultValue
                : array[index];
    }

    /**
     * Get the element at the specified index of the array
     * or return the default value if the array is smaller
     * than the specified index.
     *
     * @param array         The array to get an element from.
     * @param index         The index of the element to get.
     * @param defaultValue  The default value if the array isn't large enough.
     *
     * @return  The element at the specified index or the default value.
     */
    public static char get(char[] array, int index, char defaultValue) {
        PreCon.notNull(array);

        return (index > array.length - 1)
                ? defaultValue
                : array[index];
    }

    /**
     * Get the element at the specified index of the array
     * or return the default value if the array is smaller
     * than the specified index.
     *
     * @param array         The array to get an element from.
     * @param index         The index of the element to get.
     * @param defaultValue  The default value if the array isn't large enough.
     *
     * @return  The element at the specified index or the default value.
     */
    public static int get(int[] array, int index, int defaultValue) {
        PreCon.notNull(array);

        return (index > array.length - 1)
                ? defaultValue
                : array[index];
    }

    /**
     * Get the element at the specified index of the array
     * or return the default value if the array is smaller
     * than the specified index.
     *
     * @param array         The array to get an element from.
     * @param index         The index of the element to get.
     * @param defaultValue  The default value if the array isn't large enough.
     *
     * @return  The element at the specified index or the default value.
     */
    public static long get(long[] array, int index, long defaultValue) {
        PreCon.notNull(array);

        return (index > array.length - 1)
                ? defaultValue
                : array[index];
    }

    /**
     * Get the element at the specified index of the array
     * or return the default value if the array is smaller
     * than the specified index.
     *
     * @param array         The array to get an element from.
     * @param index         The index of the element to get.
     * @param defaultValue  The default value if the array isn't large enough.
     *
     * @return  The element at the specified index or the default value.
     */
    public static float get(float[] array, int index, float defaultValue) {
        PreCon.notNull(array);

        return (index > array.length - 1)
                ? defaultValue
                : array[index];
    }

    /**
     * Get the element at the specified index of the array
     * or return the default value if the array is smaller
     * than the specified index.
     *
     * @param array         The array to get an element from.
     * @param index         The index of the element to get.
     * @param defaultValue  The default value if the array isn't large enough.
     *
     * @return  The element at the specified index or the default value.
     */
    public static double get(double[] array, int index, double defaultValue) {
        PreCon.notNull(array);

        return (index > array.length - 1)
                ? defaultValue
                : array[index];
    }

    /**
     * Get the last element in an array.
     *
     * @param array  The array.
     *
     * @param <T>  The array component type.
     */
    public static <T> T last(T[] array) {
        PreCon.notNull(array);
        PreCon.isValid(array.length > 0, "Array has no elements.");

        return array[array.length - 1];
    }

    /**
     * Get the last element in an array.
     *
     * @param array  The array.
     *
     * @param <T>  The array component type.
     */
    public static <T> T last(T[] array, T empty) {
        PreCon.notNull(array);

        if (array.length == 0)
            return empty;

        return array[array.length - 1];
    }

    /**
     * Get the last element in an array.
     *
     * @param array  The array.
     */
    public static boolean last(boolean[] array) {
        PreCon.notNull(array);
        PreCon.isValid(array.length > 0, "Array has no elements.");

        return array[array.length - 1];
    }

    /**
     * Get the last element in an array.
     *
     * @param array  The array.
     * @param empty  The value to return if the array is empty.
     */
    public static boolean last(boolean[] array, boolean empty) {
        PreCon.notNull(array);

        if (array.length == 0)
            return empty;

        return array[array.length - 1];
    }

    /**
     * Get the last element in an array.
     *
     * @param array  The array.
     */
    public static byte last(byte[] array) {
        PreCon.notNull(array);
        PreCon.isValid(array.length > 0, "Array has no elements.");

        return array[array.length - 1];
    }

    /**
     * Get the last element in an array.
     *
     * @param array  The array.
     * @param empty  The value to return if the array is empty.
     */
    public static byte last(byte[] array, byte empty) {
        PreCon.notNull(array);

        if (array.length == 0)
            return empty;

        return array[array.length - 1];
    }

    /**
     * Get the last element in an array.
     *
     * @param array  The array.
     */
    public static char last(char[] array) {
        PreCon.notNull(array);
        PreCon.isValid(array.length > 0, "Array has no elements.");

        return array[array.length - 1];
    }

    /**
     * Get the last element in an array.
     *
     * @param array  The array.
     * @param empty  The value to return if the array is empty.
     */
    public static char last(char[] array, char empty) {
        PreCon.notNull(array);

        if (array.length == 0)
            return empty;

        return array[array.length - 1];
    }

    /**
     * Get the last element in an array.
     *
     * @param array  The array.
     */
    public static short last(short[] array) {
        PreCon.notNull(array);
        PreCon.isValid(array.length > 0, "Array has no elements.");

        return array[array.length - 1];
    }

    /**
     * Get the last element in an array.
     *
     * @param array  The array.
     * @param empty  The value to return if the array is empty.
     */
    public static short last(short[] array, short empty) {
        PreCon.notNull(array);

        if (array.length == 0)
            return empty;

        return array[array.length - 1];
    }

    /**
     * Get the last element in an array.
     *
     * @param array  The array.
     */
    public static int last(int[] array) {
        PreCon.notNull(array);
        PreCon.isValid(array.length > 0, "Array has no elements.");

        return array[array.length - 1];
    }

    /**
     * Get the last element in an array.
     *
     * @param array  The array.
     * @param empty  The value to return if the array is empty.
     */
    public static int last(int[] array, int empty) {
        PreCon.notNull(array);

        if (array.length == 0)
            return empty;

        return array[array.length - 1];
    }

    /**
     * Get the last element in an array.
     *
     * @param array  The array.
     */
    public static long last(long[] array) {
        PreCon.notNull(array);
        PreCon.isValid(array.length > 0, "Array has no elements.");

        return array[array.length - 1];
    }

    /**
     * Get the last element in an array.
     *
     * @param array  The array.
     * @param empty  The value to return if the array is empty.
     */
    public static long last(long[] array, long empty) {
        PreCon.notNull(array);

        if (array.length == 0)
            return empty;

        return array[array.length - 1];
    }

    /**
     * Get the last element in an array.
     *
     * @param array  The array.
     */
    public static float last(float[] array) {
        PreCon.notNull(array);
        PreCon.isValid(array.length > 0, "Array has no elements.");

        return array[array.length - 1];
    }

    /**
     * Get the last element in an array.
     *
     * @param array  The array.
     * @param empty  The value to return if the array is empty.
     */
    public static float last(float[] array, float empty) {
        PreCon.notNull(array);

        if (array.length == 0)
            return empty;

        return array[array.length - 1];
    }

    /**
     * Get the last element in an array.
     *
     * @param array  The array.
     */
    public static double last(double[] array) {
        PreCon.notNull(array);
        PreCon.isValid(array.length > 0, "Array has no elements.");

        return array[array.length - 1];
    }

    /**
     * Get the last element in an array.
     *
     * @param array  The array.
     * @param empty  The value to return if the array is empty.
     */
    public static double last(double[] array, double empty) {
        PreCon.notNull(array);

        if (array.length == 0)
            return empty;

        return array[array.length - 1];
    }

    /**
     * Convert an array to an array list.
     *
     * @param array  The array to convert.
     *
     * @param <T>  The array component type.
     */
    public static <T> ArrayList<T> asList(T[] array) {
        PreCon.notNull(array);

        ArrayList<T> result = new ArrayList<T>(array.length);

        Collections.addAll(result, array);

        return result;
    }

    /**
     * Convert an array to an array list.
     *
     * @param array  The array to convert.
     */
    public static ArrayList<Boolean> asList(boolean[] array) {
        PreCon.notNull(array);

        ArrayList<Boolean> result = new ArrayList<>(array.length);

        for (boolean b : array) {
            result.add(b);
        }

        return result;
    }

    /**
     * Convert an array to an array list.
     *
     * @param array  The array to convert.
     */
    public static ArrayList<Byte> asList(byte[] array) {
        PreCon.notNull(array);

        ArrayList<Byte> result = new ArrayList<>(array.length);

        for (byte b : array) {
            result.add(b);
        }

        return result;
    }

    /**
     * Convert an array to an array list.
     *
     * @param array  The array to convert.
     */
    public static ArrayList<Character> asList(char[] array) {
        PreCon.notNull(array);

        ArrayList<Character> result = new ArrayList<>(array.length);

        for (char b : array) {
            result.add(b);
        }

        return result;
    }

    /**
     * Convert an array to an array list.
     *
     * @param array  The array to convert.
     */
    public static ArrayList<Short> asList(short[] array) {
        PreCon.notNull(array);

        ArrayList<Short> result = new ArrayList<>(array.length);

        for (short b : array) {
            result.add(b);
        }

        return result;
    }

    /**
     * Convert an array to an array list.
     *
     * @param array  The array to convert.
     */
    public static ArrayList<Integer> asList(int[] array) {
        PreCon.notNull(array);

        ArrayList<Integer> result = new ArrayList<>(array.length);

        for (int b : array) {
            result.add(b);
        }

        return result;
    }

    /**
     * Convert an array to an array list.
     *
     * @param array  The array to convert.
     */
    public static ArrayList<Long> asList(long[] array) {
        PreCon.notNull(array);

        ArrayList<Long> result = new ArrayList<>(array.length);

        for (long b : array) {
            result.add(b);
        }

        return result;
    }

    /**
     * Convert an array to an array list.
     *
     * @param array  The array to convert.
     */
    public static ArrayList<Float> asList(float[] array) {
        PreCon.notNull(array);

        ArrayList<Float> result = new ArrayList<>(array.length);

        for (float b : array) {
            result.add(b);
        }

        return result;
    }

    /**
     * Convert an array to an array list.
     *
     * @param array  The array to convert.
     */
    public static ArrayList<Double> asList(double[] array) {
        PreCon.notNull(array);

        ArrayList<Double> result = new ArrayList<>(array.length);

        for (double b : array) {
            result.add(b);
        }

        return result;
    }

    /**
     * Convert an array to a {@code LinkedList}.
     *
     * @param array  The array to convert.
     *
     * @param <T>  The array component type.
     */
    public static <T> LinkedList<T> asLinkedList(T[] array) {
        PreCon.notNull(array);

        LinkedList<T> result = new LinkedList<>();

        Collections.addAll(result, array);

        return result;
    }

    /**
     * Convert an array to a {@code LinkedList}.
     *
     * @param array  The array to convert.
     */
    public static LinkedList<Boolean> asLinkedList(boolean[] array) {
        PreCon.notNull(array);

        LinkedList<Boolean> result = new LinkedList<>();

        for (boolean b : array) {
            result.add(b);
        }

        return result;
    }

    /**
     * Convert an array to a {@code LinkedList}.
     *
     * @param array  The array to convert.
     */
    public static LinkedList<Byte> asLinkedList(byte[] array) {
        PreCon.notNull(array);

        LinkedList<Byte> result = new LinkedList<>();

        for (byte b : array) {
            result.add(b);
        }

        return result;
    }

    /**
     * Convert an array to a {@code LinkedList}.
     *
     * @param array  The array to convert.
     */
    public static LinkedList<Character> asLinkedList(char[] array) {
        PreCon.notNull(array);

        LinkedList<Character> result = new LinkedList<>();

        for (char b : array) {
            result.add(b);
        }

        return result;
    }

    /**
     * Convert an array to a {@code LinkedList}.
     *
     * @param array  The array to convert.
     */
    public static LinkedList<Short> asLinkedList(short[] array) {
        PreCon.notNull(array);

        LinkedList<Short> result = new LinkedList<>();

        for (short b : array) {
            result.add(b);
        }

        return result;
    }

    /**
     * Convert an array to a {@code LinkedList}.
     *
     * @param array  The array to convert.
     */
    public static LinkedList<Integer> asLinkedList(int[] array) {
        PreCon.notNull(array);

        LinkedList<Integer> result = new LinkedList<>();

        for (int b : array) {
            result.add(b);
        }

        return result;
    }

    /**
     * Convert an array to a {@code LinkedList}.
     *
     * @param array  The array to convert.
     */
    public static LinkedList<Long> asLinkedList(long[] array) {
        PreCon.notNull(array);

        LinkedList<Long> result = new LinkedList<>();

        for (long b : array) {
            result.add(b);
        }

        return result;
    }

    /**
     * Convert an array to a {@code LinkedList}.
     *
     * @param array  The array to convert.
     */
    public static LinkedList<Float> asLinkedList(float[] array) {
        PreCon.notNull(array);

        LinkedList<Float> result = new LinkedList<>();

        for (float b : array) {
            result.add(b);
        }

        return result;
    }

    /**
     * Convert an array to a {@code LinkedList}.
     *
     * @param array  The array to convert.
     */
    public static LinkedList<Double> asLinkedList(double[] array) {
        PreCon.notNull(array);

        LinkedList<Double> result = new LinkedList<>();

        for (double b : array) {
            result.add(b);
        }

        return result;
    }

    /**
     * Convert an array to a {@code HashSet}.
     *
     * @param array  The array to convert.
     *
     * @param <T>  The array component type.
     */
    public static <T> HashSet<T> asSet(T[] array) {
        PreCon.notNull(array);

        HashSet<T> result = new HashSet<>(array.length);

        Collections.addAll(result, array);

        return result;
    }

    /**
     * Convert an array to a {@code HashSet}.
     *
     * @param array  The array to convert.
     */
    public static HashSet<Boolean> asSet(boolean[] array) {
        PreCon.notNull(array);

        HashSet<Boolean> result = new HashSet<>(2);

        for (boolean b : array) {
            result.add(b);
            if (result.size() == 2)
                break;
        }

        return result;
    }

    /**
     * Convert an array to a {@code HashSet}.
     *
     * @param array  The array to convert.
     */
    public static HashSet<Byte> asSet(byte[] array) {
        PreCon.notNull(array);

        HashSet<Byte> result = new HashSet<>(array.length);

        for (byte b : array) {
            result.add(b);
        }

        return result;
    }

    /**
     * Convert an array to a {@code HashSet}.
     *
     * @param array  The array to convert.
     */
    public static HashSet<Character> asSet(char[] array) {
        PreCon.notNull(array);

        HashSet<Character> result = new HashSet<>(array.length);

        for (char b : array) {
            result.add(b);
        }

        return result;
    }

    /**
     * Convert an array to a {@code HashSet}.
     *
     * @param array  The array to convert.
     */
    public static HashSet<Short> asSet(short[] array) {
        PreCon.notNull(array);

        HashSet<Short> result = new HashSet<>(array.length);

        for (short b : array) {
            result.add(b);
        }

        return result;
    }

    /**
     * Convert an array to a {@code HashSet}.
     *
     * @param array  The array to convert.
     */
    public static HashSet<Integer> asSet(int[] array) {
        PreCon.notNull(array);

        HashSet<Integer> result = new HashSet<>(array.length);

        for (int b : array) {
            result.add(b);
        }

        return result;
    }

    /**
     * Convert an array to a {@code HashSet}.
     *
     * @param array  The array to convert.
     */
    public static HashSet<Long> asSet(long[] array) {
        PreCon.notNull(array);

        HashSet<Long> result = new HashSet<>(array.length);

        for (long b : array) {
            result.add(b);
        }

        return result;
    }

    /**
     * Convert an array to a {@code HashSet}.
     *
     * @param array  The array to convert.
     */
    public static HashSet<Float> asSet(float[] array) {
        PreCon.notNull(array);

        HashSet<Float> result = new HashSet<>(array.length);

        for (float b : array) {
            result.add(b);
        }

        return result;
    }

    /**
     * Convert an array to a {@code HashSet}.
     *
     * @param array  The array to convert.
     */
    public static HashSet<Double> asSet(double[] array) {
        PreCon.notNull(array);

        HashSet<Double> result = new HashSet<>(array.length);

        for (double b : array) {
            result.add(b);
        }

        return result;
    }

    /**
     * Create a new array that contains elements from
     * the provided array but without the null elements.
     *
     * <p>The number of null elements can be determined by
     * the difference in size of the new array.</p>
     *
     * @param array  The source array.
     *
     * @param <T>  The array component type.
     *
     * @return A new, possibly smaller array without null elements.
     */
    public static <T> T[] removeNull(T[] array) {
        PreCon.notNull(array);

        List<T> list = new ArrayList<>(array.length);
        for (T element : array) {
            if (element != null) {
                list.add(element);
            }
        }

        @SuppressWarnings("unchecked")
        Class<T> componentClass = (Class<T>) array.getClass().getComponentType();

        T[] newArray = newArray(componentClass, list.size());

        for (int i=0; i < newArray.length; i++) {
            newArray[i] = list.get(i);
        }

        return newArray;
    }

    private static <T> T[] newArray(Class<T> arrayClass, int size) {

        @SuppressWarnings("unchecked")
        T[] newArray = (T[])Array.newInstance(arrayClass, size);

        return newArray;
    }
}
