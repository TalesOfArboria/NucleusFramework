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


package com.jcwhatever.generic.utils.performance;

import com.jcwhatever.generic.collections.timed.TimeScale;

import javax.annotation.Nullable;

/**
 * Caches a single value for a single key.
 * <p>
 *     Useful when an operation is required to do lengthy
 *     operations and external caching of the results is not possible.
 *     Methods employing single cache can cache the previous results
 *     in case the results for the same key is needed consecutively.
 * </p>
 *
 * @param <K>  The key type.
 * @param <V>  The value type.
 */
public class SingleCache <K, V> extends ExpiringCache {

    private K _key;
    private V _value;
    private boolean _hasValue = false;

    /**
     * Constructor.
     *
     * <p>Unlimited cache value lifespan.</p>
     */
    public SingleCache() {
        this(-1, TimeScale.TICKS);
    }

    /**
     * Constructor.
     *
     * @param lifespan   The cached value lifespan.
     * @param timeScale  The lifespan time scale.
     */
    public SingleCache(int lifespan, TimeScale timeScale) {
        super(lifespan, timeScale);
    }

    /**
     * Determine if the cached key is equal to the
     * provided key.
     *
     * @param key  The key to check.
     */
    public boolean keyEquals(@Nullable Object key) {
        return _key != null && !isExpired() && _key.equals(key);
    }

    /**
     * Get the current cached key.
     */
    @Nullable
    public K getKey() {
        if (isExpired()) {
            reset();
        }
        return _key;
    }

    /**
     * Get the current cached value.
     */
    @Nullable
    public V getValue() {
        if (isExpired()) {
            reset();
        }
        return _value;
    }

    /**
     * Set the current cached key/value pair.
     *
     * @param key    The key.
     * @param value  The value.
     */
    public void set(K key, @Nullable V value) {
        _key = key;
        _value = value;
        _hasValue = true;
        resetExpires();
    }

    /**
     * Set all values to null.
     */
    public void reset() {
        _key = null;
        _value = null;
        _hasValue = false;
        expireNow();
    }

    /**
     * Determine if the cache has a key/value pair set.
     */
    public boolean hasValue() {
        return _hasValue;
    }
}
