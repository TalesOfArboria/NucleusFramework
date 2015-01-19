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

package com.jcwhatever.nucleus.collections.wrap;

import java.util.Map.Entry;
import java.util.concurrent.locks.ReadWriteLock;
import javax.annotation.Nullable;

/**
 * An abstract implementation of a {@link Entry} wrapper designed to convert
 * between the entries internal value type and the publicly visible value
 * type. The wrapper is optionally synchronized via a sync object or {@link ReadWriteLock}
 * passed in through the constructor.
 *
 * <p>The actual entry is provided to the abstract implementation by
 * overriding and returning it from the {@link #entry} method.</p>
 */
public abstract class ConversionEntryWrapper<K, V, I> implements Entry<K, V> {

    protected final Object _sync;
    protected final ReadWriteLock _lock;

    /**
     * Constructor.
     *
     * <p>No synchronization.</p>
     */
    public ConversionEntryWrapper() {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param sync A synchronizable object or {@code ReadWriteLock}. Null for no
     *             synchronization.
     */
    public ConversionEntryWrapper(@Nullable Object sync) {

        _sync = sync;
        _lock = sync instanceof ReadWriteLock
                ? (ReadWriteLock)sync
                : null;
    }

    /**
     * Invoked from a synchronized block (if synchronized) to get the
     * encapsulated {@code Entry}.
     */
    protected abstract Entry<K, I> entry();

    /**
     * Convert an internal element type to the external type.
     *
     * @param key       The key.
     * @param internal  The internal type to convert.
     *
     * @return  An external type instance.
     */
    protected abstract V convert(K key, I internal);

    /**
     * Unconvert an external type to an internal type.
     *
     * @param key       The key.
     * @param external  The external type to unconvert.
     *
     * @return  An internal type instance.
     *
     * @throws java.lang.ClassCastException if the external
     * type cannot be converted.
     */
    protected abstract I unconvert(K key, V external);

    @Override
    public K getKey() {
        return entry().getKey();
    }

    @Override
    public V getValue() {

        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return convert(entry().getKey(), entry().getValue());
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return convert(entry().getKey(), entry().getValue());
            }
        }
        else {
            return convert(entry().getKey(), entry().getValue());
        }
    }

    @Override
    public V setValue(V value) {

        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return setValueSource(value);
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return setValueSource(value);
            }
        } else {
            return setValueSource(value);
        }
    }

    private V setValueSource(V value) {
        I current = entry().getValue();
        entry().setValue(unconvert(entry().getKey(), value));
        return convert(entry().getKey(), current);
    }
}
