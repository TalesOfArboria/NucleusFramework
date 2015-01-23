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

import com.jcwhatever.nucleus.utils.PreCon;

import java.util.Map.Entry;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * An abstract implementation of an {@link Entry} wrapper. The wrapper is
 * optionally synchronized via a sync object or {@link ReadWriteLock}
 * passed into the constructor using a {@link SyncStrategy}.
 *
 * <p>The actual entry is provided to the abstract implementation by
 * overriding and returning it from the {@link #entry} method.</p>
 */
public abstract class EntryWrapper<K, V> implements Entry<K, V> {

    protected final Object _sync;
    protected final ReadWriteLock _lock;
    protected final SyncStrategy _strategy;

    /**
     * Constructor.
     *
     * <p>No synchronization.</p>
     */
    public EntryWrapper() {
        this(SyncStrategy.NONE);
    }

    /**
     * Constructor.
     *
     * @param strategy  The synchronization strategy to use.
     */
    public EntryWrapper(SyncStrategy strategy) {
        PreCon.notNull(strategy);

        _sync = strategy.getSync(this);
        _strategy = new SyncStrategy(_sync);
        _lock = _sync instanceof ReadWriteLock
                ? (ReadWriteLock)_sync
                : null;
    }

    protected abstract Entry<K, V> entry();

    @Override
    public K getKey() {
        return entry().getKey();
    }

    @Override
    public V getValue() {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return entry().getValue();
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return entry().getValue();
            }
        } else {
            return entry().getValue();
        }
    }

    @Override
    public V setValue(V value) {
        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                return entry().setValue(value);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return entry().setValue(value);
            }
        } else {
            return entry().setValue(value);
        }
    }
}
