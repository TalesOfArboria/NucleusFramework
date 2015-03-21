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

package com.jcwhatever.nucleus.utils;

import com.jcwhatever.nucleus.mixins.IMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

/**
 * A meta data storage container.
 */
public class MetaStore {

    private final int _size;
    private Map<Object, Object> _meta;

    /**
     * Constructor.
     *
     * <p>Initializes with an initial capacity of 3.</p>
     */
    public MetaStore() {
        this(7);
    }

    /**
     * Constructor.
     *
     * @param size  The initial capacity.
     */
    public MetaStore(int size) {
        _size = size;
    }

    /**
     * Get a meta value using a {@link MetaKey}.
     *
     * @param key  The key.
     *
     * @param <T>  The value type.
     *
     * @return  The value or null if not found.
     */
    @Nullable
    public <T> T get(MetaKey<T> key) {
        PreCon.notNull(key);

        if (_meta == null)
            return null;

        @SuppressWarnings("unchecked")
        T result = (T)_meta.get(key);

        return result;
    }

    /**
     * Get a meta value.
     *
     * @param key  The meta values lookup key.
     *
     * @return  The value or null if not found.
     */
    @Nullable
    public Object get(Object key) {
        PreCon.notNull(key);

        if (_meta == null)
            return null;

        return _meta.get(key);
    }

    /**
     * Set a meta value using a {@link MetaKey}.
     *
     * @param key    The meta key.
     * @param value  The value to set. Null to remove.
     *
     * @param <T>  The value type.
     *
     * @return  Self for chaining.
     */
    public <T> MetaStore set(MetaKey<T> key, @Nullable T value) {
        PreCon.notNull(key);

        setValue(key, value);

        return this;
    }

    /**
     * Set a meta value.
     *
     * @param key    The meta values key.
     * @param value  The value to set. Null to remove.
     *
     * @return  Self for chaining.
     */
    public MetaStore set(Object key, @Nullable Object value) {
        PreCon.notNull(key);

        setValue(key, value);

        return this;
    }

    /**
     * Copy all meta values from the specified {@link IMeta} object.
     *
     * @param meta  The {@link IMeta} object.
     *
     * @return  Self for chaining.
     */
    public MetaStore copyAll(IMeta meta) {
        PreCon.notNull(meta);

        return copyAll(meta.getMeta()._meta);
    }

    /**
     * Copy all meta key/values from the specified {@link MetaStore}.
     *
     * @param metaStore  The {@link MetaStore} to copy values from.
     *
     * @return  Self for chaining.
     */
    public MetaStore copyAll(MetaStore metaStore) {
        PreCon.notNull(metaStore);

        return copyAll(metaStore._meta);
    }

    /**
     * Copy all meta values from the specified {@link Map}.
     *
     * @param map  The {@link Map}.
     *
     * @return  Self for chaining.
     */
    public MetaStore copyAll(Map<Object, Object> map) {
        PreCon.notNull(map);

        for (Entry<Object, Object> entry : map.entrySet()) {
            setValue(entry.getKey(), entry.getValue());
        }

        return this;
    }

    protected void setValue(Object key, Object value) {
        if (_meta == null) {
            if (value == null)
                return;

            _meta = new HashMap<>(_size);
        }

        if (value == null) {
            _meta.remove(key);
        }
        else {
            _meta.put(key, value);
        }
    }
}
