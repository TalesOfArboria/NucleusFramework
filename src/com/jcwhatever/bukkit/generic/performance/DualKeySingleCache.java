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


package com.jcwhatever.bukkit.generic.performance;

import com.jcwhatever.bukkit.generic.collections.timed.TimeScale;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import javax.annotation.Nullable;

/**
 * Caches a single value represented by two keys.
 * <p>
 *     Useful when an operation is required to do lengthy
 *     operations and external caching of the results is not possible.
 *     Methods employing this cache can cache the previous results
 *     in case the results for the same key pair is needed consecutively.
 * </p>
 *
 * @param <K1>  The 1st key type.
 * @param <K2>  The 2nd key type.
 * @param <V>  The value type.
 */
public class DualKeySingleCache <K1, K2, V> extends ExpiringCache {

    private K1 _key1;
    private K2 _key2;
    private V _value;
    private boolean _hasValue = false;

    /**
     * Constructor.
     *
     */
    public DualKeySingleCache() {
        this(-1, TimeScale.TICKS);
    }

    /**
     * Constructor.
     *
     * @param lifespan  The cached value lifespan.
     * @param timeScale The lifespan time scale.
     */
    public DualKeySingleCache(int lifespan, TimeScale timeScale) {
        super(lifespan, timeScale);
    }

    /**
     * Determine if the cached key pairs are equal to the provided
     * key pairs.
     *
     * @param key1  The 1st key to check.
     * @param key2  The 2nd key to check.
     */
    public boolean keyEquals(@Nullable Object key1, @Nullable Object key2) {
        return !isExpired() && !(_key1 == null || _key2 == null) &&
                _key1.equals(key1) && _key2.equals(key2);
    }

    /**
     * Get the currently cached 1st key.
     */
    @Nullable
    public K1 getKey1() {
        if (isExpired()) {
            reset();
        }
        return _key1;
    }

    /**
     * Get the currently cached 2nd key.
     */
    @Nullable
    public K2 getKey2() {
        if (isExpired()) {
            reset();
        }
        return _key2;
    }

    /**
     * Get the currently cached value.
     */
    @Nullable
    public V getValue() {
        if (isExpired()) {
            reset();
        }
        return _value;
    }

    /**
     * Set the current cache key/values.
     *
     * @param key1   The 1st key.
     * @param key2   The 2nd key.
     * @param value  The value.
     */
    public void set(K1 key1, K2 key2, @Nullable V value) {
        PreCon.notNull(key1);
        PreCon.notNull(key2);

        _key1 = key1;
        _key2 = key2;
        _value = value;
        _hasValue = true;
        resetExpires();
    }

    /**
     * Reset the cache by removing cached
     * keys and value.
     */
    public void reset() {
        _key1 = null;
        _key2 = null;
        _value = null;
        _hasValue = false;
        expireNow();
    }

    /**
     * Determine if the cache has keys
     * and value set.
     */
    public boolean hasValue() {
        return _hasValue;
    }

}
