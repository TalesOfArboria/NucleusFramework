/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.performance;

import com.jcwhatever.bukkit.generic.utils.PreCon;

import java.util.HashMap;
import java.util.Map;

public class MultiCache<K, V> {

    private Map<String, SingleCache<K, V>> _cache = new HashMap<String, SingleCache<K, V>>(10);

    public void set(String cacheName, K key, V value) {
        SingleCache<K, V> cache = getCache(cacheName, true);
        if (cache == null)
            return;

        cache.set(key,  value);
    }

    public boolean keyEquals(String cacheName, K key) {
        PreCon.notNullOrEmpty(cacheName);
        PreCon.notNull(key);

        SingleCache<K, V> cache = getCache(cacheName, false);
        return cache != null && cache.keyEquals(key);
    }

    public void reset(String cacheName) {
        SingleCache<K, V> cache = getCache(cacheName, false);
        if (cache == null)
            return;

        cache.reset();
    }

    public void clear() {
        _cache.clear();
    }

    public SingleCache<K, V> getCache(String cacheName) {
        return getCache(cacheName, false);
    }

    private SingleCache<K, V> getCache(String cacheName, boolean addIfNotExists) {

        SingleCache<K, V> cache = _cache.get(cacheName);

        if (addIfNotExists && cache == null) {
            cache = new SingleCache<K, V>();
            _cache.put(cacheName, cache);
        }

        return cache;
    }

}
