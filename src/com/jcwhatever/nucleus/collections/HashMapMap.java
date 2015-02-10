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

package com.jcwhatever.nucleus.collections;

import com.jcwhatever.nucleus.utils.PreCon;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * A {@code HashMap} with a map value.
 */
public class HashMapMap<K1, K2, V> extends HashMap<K1, HashMap<K2, V>> {

    /**
     * Constructor.
     */
    public HashMapMap() {
        super();
    }

    /**
     * Constructor.
     *
     * @param size  The initial capacity.
     */
    public HashMapMap(int size) {
        super(size);
    }

    /**
     * Put a value into a map.
     *
     * <p>If the map specified by key1 does not exist,
     * it is automatically added.</p>
     *
     * @param key1   The map key.
     * @param key2   The value key.
     * @param value  The value to set.
     *
     * @return  The previous value, if any.
     */
    @Nullable
    public V put (K1 key1, K2 key2, V value) {
        PreCon.notNull(key1);
        PreCon.notNull(key2);

        HashMap<K2, V> map = super.get(key1);

        if (map == null) {
            map = new HashMap<>(10);
            super.put(key1, map);
        }

        return map.put(key2, value);
    }

    /**
     * Remove a value from a map.
     *
     * @param key1  The map key.
     * @param key2  The value key.
     *
     * @return  The removed value, if any.
     */
    @Nullable
    public V removeValue(K1 key1, K2 key2) {
        PreCon.notNull(key1);
        PreCon.notNull(key2);

        HashMap<K2, V> map = super.get(key1);
        if (map == null)
            return null;

        return map.remove(key2);
    }

    /**
     * Get a value from a map.
     *
     * @param key1  The map key.
     * @param key2  The value key.
     *
     * @return  The value, if any.
     */
    @Nullable
    public V get(K1 key1, K2 key2) {
        PreCon.notNull(key1);
        PreCon.notNull(key2);

        HashMap<K2, V> map = super.get(key1);
        if (map == null)
            return null;

        return map.get(key2);
    }

    /**
     * Get all values.
     */
    public Set<V> valueSet() {

        HashSet<V> values = new HashSet<>(size() * 5);

        for (Entry<K1, HashMap<K2, V>> entry : entrySet()) {
            values.addAll(entry.getValue().values());
        }

        return values;
    }
}
