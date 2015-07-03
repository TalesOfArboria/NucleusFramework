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


package com.jcwhatever.nucleus.utils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Randomizing utilities.
 */
public final class Rand {

    private Rand() {}

    static final String SAFE_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static final String UNSAFE_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "01234567890`~!@#$%^&*()_+-=";

    private static final ThreadSingletons<XORShiftRandom> RANDOMS = new ThreadSingletons<>(
            new ThreadSingletons.ISingletonFactory<XORShiftRandom>() {
                @Override
                public XORShiftRandom create(Thread thread) {
                    return new XORShiftRandom();
                }
            });

    /**
     * Get a random item from a list.
     *
     * @param items  The list to get a random item from.
     *
     * @param <T>  The list generic type.
     */
    public static <T> T get(List<T> items) {
        PreCon.notNull(items);
        PreCon.isValid(items.size() > 0);

        if (items.size() == 1)
            return items.get(0);

        return items.get(getInt(items.size()));
    }

    /**
     * Get a random item from an array of items.
     *
     * @param items  The array of items.
     *
     * @param <T>  The array item type.
     */
    public static <T> T get(T[] items) {
        PreCon.notNull(items);
        PreCon.isValid(items.length > 0);

        if (items.length == 1)
            return items[0];

        return items[getInt(items.length)];
    }

    /**
     * Remove a random item from a list.
     *
     * @param items  The list to remove a random item from.
     *
     * @param <T>  The list generic type.
     */
    public static <T> T remove(List<T> items) {
        PreCon.notNull(items);
        PreCon.isValid(items.size() > 0);

        if (items.size() == 1)
            return items.remove(0);

        return items.remove(getInt(items.size()));
    }

    /**
     * Get a random integer equal to or between the specified minimum and maximum
     * amount.
     *
     * @param min  The minimum result.
     * @param max  The maximum result.
     */
    public static int getInt(int min, int max) {
        int range = max - min + 1;
        int i = Math.abs(RANDOMS.get().nextInt()) % range;
        return  min + i;
    }

    /**
     * Get a random integer from 0 up to the specified maximum number.
     *
     * @param max  The maximum result.
     */
    public static int getInt(int max) {
        return RANDOMS.get().nextInt(max);
    }

    /**
     * Get a random integer.
     */
    public static int getInt() {
        return RANDOMS.get().nextInt();
    }

    /**
     * Get a random double equal to or between the specified minimum and maximum
     * amount.
     *
     * @param min  The minimum result.
     * @param max  The maximum result.
     */
    public static double getDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    /**
     * Get a random double from 0 up to the specified maximum number.
     *
     * @param max  The maximum result.
     */
    public static double getDouble(double max) {
        return ThreadLocalRandom.current().nextDouble(0, max);
    }

    /**
     * Get a random double.
     */
    public static double getDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }

    /**
     * Get a random gaussian double with an average of 0 and a
     * deviation of 1.
     */
    public static double getGaussian() {
        return ThreadLocalRandom.current().nextGaussian();
    }

    /**
     * Get a random gaussian double.
     *
     * @param average    The average.
     * @param deviation  The deviation from average.
     */
    public static double getGaussian(double average, double deviation) {
        return ThreadLocalRandom.current().nextGaussian() * deviation + average;
    }

    /**
     * Get a random alphabet string of the specified length.
     *
     * <p>Returned string should be safe to use in contexts such as node names in
     * config files.</p>
     *
     * @param length  The length of the returned string.
     */
    public static String getSafeString(int length) {
        PreCon.positiveNumber(length);

        return getString(length, SAFE_CHARACTERS);
    }

    /**
     * Get a random string of characters, including symbols, of the specified length.
     *
     * <p>May not be safe to use where the usage context does not allow for symbols in
     * the string.</p>
     *
     * @param length  The length of the returned string.
     */
    public static String getUnsafeString(int length) {
        PreCon.positiveNumber(length);

        return getString(length, UNSAFE_CHARACTERS);
    }

    /**
     * Get a random string of characters using the specified character pool.
     *
     * @param length         The length of the returned string.
     * @param characterPool  The pool of characters to construct a string with.
     */
    public static String getString(int length, String characterPool) {
        PreCon.positiveNumber(length);
        PreCon.notNullOrEmpty(characterPool);

        StringBuilder sb = new StringBuilder(length);

        for (int i=0; i < length; i++) {
            int index = getInt(0, characterPool.length() - 1);

            sb.append(characterPool.charAt(index));
        }

        return sb.toString();
    }

    /**
     * Get a random boolean using the specified chance.
     *
     * <p>The maximum effective chance is 100.</p>
     *
     * @param chance  The chance of getting a result of true.
     */
    public static boolean chance(int chance) {
        return chance >= 100 || chance < Rand.getInt(100);
    }

    /**
     * Get a random boolean using the specified chance.
     *
     * <p>The maximum effective chance is 1.0.</p>
     *
     * @param chance  The chance of getting a result of true.
     */
    public static boolean chance(double chance) {
        return chance >= 1.0D || chance < Rand.getDouble(1);
    }

    /**
     * Roll dice of the specified number of sides.
     *
     * <p>Maximum effective sides is 100.</p>
     *
     * @param sides  The number of sides the dice has.
     */
    public static boolean rollDice(int sides) {
        PreCon.positiveNumber(sides);
        if (sides == 0 || sides > 100)
            return false;

        if (sides == 1)
            return true;

        int chance = 100 / sides;

        return chance(chance);
    }

    private static class XORShiftRandom {
        long x = System.currentTimeMillis();

        long next() {
            x ^= (x << 21);
            x ^= (x >>> 35);
            x ^= (x << 4);
            return x;
        }

        int nextInt() {
            next();
            return Math.abs((int)((x >> 32) ^ ((int)x)));
        }

        int nextInt(int max) {
            if (max <= 1)
                return 0;

            return nextInt() % (max - 1);
        }
    }
}
