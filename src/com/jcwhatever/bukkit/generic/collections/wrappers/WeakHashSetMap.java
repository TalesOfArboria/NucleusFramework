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

package com.jcwhatever.bukkit.generic.collections.wrappers;

import com.jcwhatever.bukkit.generic.collections.SetMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * A {@code WeakHashMap} that uses hash sets to store values.
 *
 * @param <K>  Key type
 * @param <V>  Value type
 */
public class WeakHashSetMap<K, V> extends SetMap<K, V> {

    protected final Map<K, Set<V>> _map;
    protected int _setSize;

    /**
     * Constructor.
     *
     * <p>Map capacity starts at 10 elements</p>
     *
     * <p>Set capacity starts at 10 elements.</p>
     */
    public WeakHashSetMap(){
        this(10, 10);
    }

    /**
     * Constructor.
     *
     * <p>Set capacity starts at 10 elements.</p>
     *
     * @param mapSize  The initial capacity of the map.
     */
    public WeakHashSetMap(int mapSize) {
        this(mapSize, 10);
    }

    /**
     * Constructor.
     *
     * @param mapSize  The initial capacity of the map.
     * @param setSize  The initial capacity of sets.
     */
    public WeakHashSetMap(int mapSize, int setSize) {
        _map = new WeakHashMap<>(mapSize);

        _setSize = setSize;
    }

    @Override
    protected Map<K, Set<V>> getMap() {
        return _map;
    }

    @Override
    protected Set<V> createSet() {
        return createSet(_setSize);
    }

    @Override
    protected Set<V> createSet(int size) {
        return new HashSet<>(size);
    }
}
