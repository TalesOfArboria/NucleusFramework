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

package com.jcwhatever.nucleus.internal.managed.scripting.api;

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Rand;

import java.util.List;

/*
 * Provide scripts with access to randomizing utilities.
 */
public class SAPI_Rand implements IDisposable {

    private boolean _isDisposed;

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        _isDisposed = true;
    }

    /**
     * Get a random item from a list.
     *
     * @param items  The list to get a random item from.
     *
     * @param <T>    The list generic type.
     */
    public <T> T getListItem(List<T> items) {
        PreCon.notNull(items);

        return Rand.get(items);
    }

    /**
     * Get a random item from an array of items.
     *
     * @param items  The array of items.
     *
     * @param <T>    The array item type.
     */
    public <T> T getArrayItem(T[] items) {
        PreCon.notNull(items);

        return Rand.get(items);
    }

    /**
     * Get a random integer equal to or between
     * the specified minimum and maximum amount.
     *
     * @param min  The minimum result.
     * @param max  The maximum result.
     */
    public int getIntMinMax(int min, int max) {
        return Rand.getInt(min, max);
    }

    /**
     * Get a random integer from 0 up to the
     * specified maximum number.
     *
     * @param max  The maximum result.
     */
    public int getIntMax(int max) {

        return Rand.getInt(max);
    }

    /**
     * Get a random integer.
     */
    public static int getInt() {
        return Rand.getInt();
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
    public String getSafeString(int length) {
        return Rand.getSafeString(length);
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
    public String getUnsafeString(int length) {
        return Rand.getUnsafeString(length);
    }

    /**
     * Get a random string of characters using the specified
     * character pool.
     *
     * @param length         The length of the returned string.
     * @param characterPool  The pool of characters to construct a string with.
     */
    public String getString(int length, String characterPool) {
        PreCon.notNullOrEmpty(characterPool);

        return Rand.getString(length, characterPool);
    }

    /**
     * Get a random boolean using the specified chance.
     * The maximum effective chance is 100.
     *
     * @param chance  The chance of getting a result of true.
     */
    public boolean chance(int chance) {
        return Rand.chance(chance);
    }

    /**
     * Roll dice of the specified number of sides.
     * Maximum effective sides is 100.
     *
     * @param sides  The number of sides the dice has.
     */
    public boolean rollDice(int sides) {
        return Rand.rollDice(sides);
    }
}
