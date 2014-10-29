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
import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Caches multiple {@code SingleCache} objects.
 * <p>
 *     A {@code SingleCache} is useful when an operation is required to do lengthy
 *     operations and external caching of the results is not possible.
 *     Methods employing single cache can cache the previous results
 *     in case the results for the same key is needed consecutively.
 * </p>
 * <p>
 *     The {@code MultiCache} is useful where there are multiple contexts
 *     for caching {@code SingleCache} objects of the same generics types.
 * </p>
 *
 * @param <K>  The {@code SingleCache} key type.
 * @param <V>  The {@code SingleCache} value type.
 */
public class MultiCache<K, V> {

    private Map<String, SingleCache<K, V>> _cache = new HashMap<String, SingleCache<K, V>>(10);

    /**
     * Set the current cached key/value pair for the given context.
     *
     * @param contextName  The name of the context.
     * @param key          The key.
     * @param value        The value.
     */
    public void set(String contextName, K key, V value) {
        SingleCache<K, V> cache = getCache(contextName, true);
        if (cache == null)
            return;

        cache.set(key,  value);
    }

    /**
     * Determine if the cached key is equal to the provided
     * key in the given context.
     *
     * @param contextName  The name of the context.
     * @param key          The key to check.
     */
    public boolean keyEquals(String contextName, K key) {
        PreCon.notNullOrEmpty(contextName);
        PreCon.notNull(key);

        SingleCache<K, V> cache = getCache(contextName, false);
        return cache != null && cache.keyEquals(key);
    }

    /**
     * Reset the given context by clearing
     * its cached key/value pair.
     *
     * @param contextName  The name of the context.
     */
    public void reset(String contextName) {
        SingleCache<K, V> cache = getCache(contextName, false);
        if (cache == null)
            return;

        cache.reset();
    }

    /**
     * Clear the cache.
     */
    public void clear() {
        _cache.clear();
    }

    /**
     * Get the {@code SingleCache} object associated with
     * the given context.
     *
     * @param contextName  The name of the context.
     *
     * @return  Null if the context is not found.
     */
    @Nullable
    public SingleCache<K, V> getCache(String contextName) {
        return getCache(contextName, false);
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
