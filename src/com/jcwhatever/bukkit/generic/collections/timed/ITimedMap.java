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

package com.jcwhatever.bukkit.generic.collections.timed;

import java.util.Map;

/**
 * Represents a map collection whose elements have individual
 * lifespans. When the elements lifespan ends, the element is
 * removed.
 */
public interface ITimedMap<K, V> {

    /**
     * Put an item into the map using the specified lifespan.
     *
     * @param key            The item key.
     * @param value          The item to add.
     * @param lifespanTicks  The items lifespan in ticks.
     */
    boolean put(final K key, final V value, int lifespanTicks);

    /**
     * Put a map of items into the map using the specified lifespan.
     *
     * @param entries        The map to add.
     * @param lifespanTicks  The lifespan of the added items in ticks.
     */
    void putAll(Map<? extends K, ? extends V> entries, int lifespanTicks);
}
