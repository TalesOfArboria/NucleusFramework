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

import java.util.List;
import java.util.Random;

/**
 * Randomizing utilities
 */
public class Rand {

    private Rand() {}

    private static final Random RANDOM = new Random();
    private static final String SAFE_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String UNSAFE_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "01234567890`~!@#$%^&*()_+-=";

    /**
     * Get a random item from a list.
     *
     * @param items  The list to get a random item from.
     *
     * @param <T>  The list generics type.
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
     * Get a random integer equal to or between
     * the specified minimum and maximum amount.
     *
     * @param min  The minimum result.
     * @param max  The maximum result.
     */
    public static int getInt(int min, int max) {
        Random random = new Random(RANDOM.nextInt());
        int range = max - min + 1;
        int i = random.nextInt() % range;
        return  min + i;
    }

    /**
     * Get a random integer from 0 up to the
     * specified maximum number.
     *
     * @param max  The maximum result.
     */
    public static int getInt(int max) {
        Random random = new Random(RANDOM.nextInt());
        return random.nextInt(max);
    }

    /**
     * Get a random integer.
     */
    public static int getInt() {
        Random random = new Random(RANDOM.nextInt());
        return random.nextInt();
    }

    /**
     * Get a random alphabet string of the specified length.
     *
     * <p>
     *     Returned string should be safe to use in contexts
     *     such as node names in config files.
     * </p>
     *
     * @param length  The length of the returned string.
     */
    public static String getSafeString(int length) {
        PreCon.positiveNumber(length);

        return getString(length, SAFE_CHARACTERS);
    }

    /**
     * Get a random string of characters, including symbols,
     * of the specified length.
     *
     * <p>
     *     May not be safe to use where the usage context
     *     does not allow for symbols in the string.
     * </p>
     *
     * @param length  The length of the returned string.
     */
    public static String getUnsafeString(int length) {
        PreCon.positiveNumber(length);

        return getString(length, UNSAFE_CHARACTERS);
    }


    /**
     * Get a random string of characters using the specified
     * character pool.
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
     * The maximum effective chance is 100.
     *
     * @param chance  The chance of getting a result of true.
     */
    public static boolean chance(int chance) {
        return chance >= 100 || chance > Rand.getInt(100);
    }

    /**
     * Roll dice of the specified number of sides.
     * Maximum effective sides is 100.
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

}
