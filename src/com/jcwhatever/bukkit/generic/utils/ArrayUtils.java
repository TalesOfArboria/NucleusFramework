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

/**
 * Array utilities.
 */
public class ArrayUtils {

    private ArrayUtils() {}

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
        int sourceAdjust = delta < 0 ?  0 : -delta;
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
        int sourceAdjust = delta < 0 ?  0 : -delta;
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
        int sourceAdjust = delta < 0 ?  0 : -delta;
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
        int sourceAdjust = delta < 0 ?  0 : -delta;
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
        int sourceAdjust = delta < 0 ?  0 : -delta;
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
        int sourceAdjust = delta < 0 ?  0 : -delta;
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
        int sourceAdjust = delta < 0 ?  0 : -delta;
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
        int sourceAdjust = delta < 0 ?  0 : -delta;
        int destAdjust = delta < 0 ? -delta : 0;

        System.arraycopy(source, sourceAdjust, destination, destAdjust, destination.length - destAdjust);

        return destination;
    }

    /**
     * Reduce the size of an array by trimming from the beginning of the array.
     *
     * @param array           The array to trim.
     * @param amountToRemove  The number of elements to remove.
     *
     * @param <T>  The component type.
     *
     * @return A new trimmed array.
     */
    public static <T> T[] reduceStart(T[] array, int amountToRemove) {
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
     * @param array           The array to trim.
     * @param amountToRemove  The number of elements to remove.
     *
     * @return A new trimmed array.
     */
    public static byte[] reduceStart(byte[] array, int amountToRemove) {
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
     * @param array           The array to trim.
     * @param amountToRemove  The number of elements to remove.
     *
     * @return A new trimmed array.
     */
    public static char[] reduceStart(char[] array, int amountToRemove) {
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
     * @param array           The array to trim.
     * @param amountToRemove  The number of elements to remove.
     *
     * @return A new trimmed array.
     */
    public static short[] reduceStart(short[] array, int amountToRemove) {
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
     * @param array           The array to trim.
     * @param amountToRemove  The number of elements to remove.
     *
     * @return A new trimmed array.
     */
    public static int[] reduceStart(int[] array, int amountToRemove) {
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
     * @param array           The array to trim.
     * @param amountToRemove  The number of elements to remove.
     *
     * @return A new trimmed array.
     */
    public static long[] reduceStart(long[] array, int amountToRemove) {
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
     * @param array           The array to trim.
     * @param amountToRemove  The number of elements to remove.
     *
     * @return A new trimmed array.
     */
    public static float[] reduceStart(float[] array, int amountToRemove) {
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
     * @param array           The array to trim.
     * @param amountToRemove  The number of elements to remove.
     *
     * @return A new trimmed array.
     */
    public static double[] reduceStart(double[] array, int amountToRemove) {
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
     * @param array           The array to trim.
     * @param amountToRemove  The number of elements to remove.
     *
     * @return A new trimmed array.
     */
    public static String[] reduceStart(String[] array, int amountToRemove) {
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
     * @param array           The array to trim.
     * @param amountToRemove  The number of elements to remove.
     *
     * @return A new trimmed array.
     */
    public static ItemStack[] reduceStart(ItemStack[] array, int amountToRemove) {
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
     * @param array           The array to trim.
     * @param amountToRemove  The number of elements to remove.
     *
     * @return A new trimmed array.
     */
    public static Entity[] reduceStart(Entity[] array, int amountToRemove) {
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

    private static <T> T[] newArray(Class<T> arrayClass, int size) {

        @SuppressWarnings("unchecked")
        T[] newArray = (T[])Array.newInstance(arrayClass, size);

        return newArray;
    }
}
