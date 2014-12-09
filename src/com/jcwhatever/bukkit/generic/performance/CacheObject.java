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

import com.jcwhatever.bukkit.generic.collections.TimeScale;

import javax.annotation.Nullable;

/**
 * Caches an object instance.
 * <p>
 *     Useful when an operation is required to do lengthy
 *     operations and external caching of the results is not possible.
 * </p>
 * <p>
 *     Can be used to cache an object for a specified amount of time.
 * </p>
 * <p>
 *     The {@code CacheObject} instance hash code and equals match the set value.
 *     These values change when the cached object expires. It is not recommended
 *     to use the {@code CacheObject} as a hash key.
 * </p>
 *
 * @param <V>  The value type.
 */
public class CacheObject<V> extends ExpiringCache {

    private V _value;
    private boolean _hasValue = false;

    /**
     * Constructor.
     *
     * <p>Unlimited cache value lifespan.</p>
     */
    public CacheObject() {
        this(-1, TimeScale.TICKS);
    }

    /**
     * Constructor.
     *
     * @param lifespan   The cached value lifespan.
     * @param timeScale  The lifespan time scale.
     */
    public CacheObject(int lifespan, TimeScale timeScale) {
        super(lifespan, timeScale);
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
     * Set the current cached value.
     *
     * @param value  The value.
     */
    public void set(V value) {
        _value = value;
        _hasValue = true;
        resetExpires();
    }

    /**
     * Set all values to null.
     */
    public void reset() {
        _value = null;
        _hasValue = false;
        expireNow();
    }

    /**
     * Determine if the cache has a key/value pair set.
     */
    public boolean hasValue() {
        return _hasValue && !isExpired();
    }

    @Override
    public int hashCode() {
        if (_value == null)
            return -1;

        return _value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (_value == null)
            return false;

        if (obj instanceof CacheObject)
            return _value.equals(((CacheObject) obj)._value);

        return _value.equals(obj);
    }
}
