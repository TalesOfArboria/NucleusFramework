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


package com.jcwhatever.bukkit.generic.collections;

import com.jcwhatever.bukkit.generic.utils.PreCon;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A hash map that uses hash sets to store values.
 *
 * @param <K>  Key type
 * @param <V>  Value type
 */
public class HashSetMap<K, V> extends AbstractSetMap<K, V> {

    protected Map<K, Set<V>> _map;

    /**
     * Constructor.
     */
    public HashSetMap() {
        _map = new HashMap<>(10);
    }

    /**
     * Constructor.
     *
     * @param size  The initial size.
     */
    public HashSetMap(int size) {
        PreCon.positiveNumber(size);

        _map = new HashMap<>(size);
    }

    @Override
    protected Map<K, Set<V>> getMap() {
        return _map;
    }

    @Override
    protected Set<V> createSet() {
        return createSet(10);
    }

    @Override
    protected Set<V> createSet(int size) {
        return new HashSet<>(size);
    }
}
