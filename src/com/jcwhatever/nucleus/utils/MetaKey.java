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

import javax.annotation.Nullable;

/**
 * A meta data key that is used as a singleton instance or as a wrapper to
 * an object that represents the key.
 *
 * @see IMeta
 * @see MetaStore
 */
public class MetaKey<V> {

    private final Object _key;
    private final Class<V> _type;

    /**
     * Constructor.
     *
     * <p>The newly created instance acts as a meta key and should
     * be stored as a singleton instance.</p>
     *
     * @param type  The meta value type.
     */
    public MetaKey(Class<V> type) {
        this(type, null);
    }

    /**
     * Constructor.
     *
     * <p>The meta key instance equals the provided key.</p>
     *
     * @param type  The meta value type.
     * @param key   The key object.
     */
    public MetaKey(Class<V> type, @Nullable Object key) {
        PreCon.notNull(type);

        _type = type;
        _key = key != null ? key : this;
    }

    /**
     * Get the meta value class.
     */
    public Class<V> getValueClass() {
        return _type;
    }

    /**
     * Get the meta value represented by the key
     * from the specified meta store.
     *
     * @param meta  The meta store.
     *
     * @return  Null if the value is not present or is not the correct type.
     */
    @Nullable
    public V getValue(IMeta meta) {
        PreCon.notNull(meta);

        Object value = meta.getMetaObject(this);
        if (value == null)
            return null;

        if (_type.isAssignableFrom(value.getClass())) {
            return _type.cast(value);
        }

        return null;
    }

    @Override
    public int hashCode() {
        if (_key == this) {
            return super.hashCode();
        }
        return _key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MetaKey) {
            return ((MetaKey) obj)._key.equals(_key);
        }

        return _key.equals(obj);
    }
}
