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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * An abstract implementation of a synchronized {@link Map} wrapper. The wrapper is
 * optionally synchronized via a sync object or {@link ReadWriteLock} passed into the
 * constructor using a {@link SyncStrategy}.
 *
 * <p>If the map is synchronized, the sync object must be externally locked while
 * any associated iterator is in use. Otherwise, a {@link java.lang.IllegalStateException} will
 * be thrown.</p>
 *
 * <p>The actual map is provided to the abstract implementation by
 * overriding and returning it from the {@link #map} method.</p>
 *
 * <p>In order to make using the wrapper as an extension of a map easier,
 * several protected methods are provided for optional override. See {@link #onPut},
 * {@link #onRemove}, {@link #onClear}.</p>
 */
public abstract class MapWrapper<K, V> implements Map<K, V> {

    private final ValuesWrapper _valuesWrapper;
    private final KeySetWrapper _keySetWrapper;
    private final EntrySetWrapper _entrySetWrapper;

    protected final Object _sync;
    protected final ReadWriteLock _lock;
    protected final SyncStrategy _strategy;

    /**
     * Constructor.
     *
     * <p>No synchronization.</p>
     */
    public MapWrapper() {
        this(SyncStrategy.NONE);
    }

    /**
     * Constructor.
     *
     * @param strategy  The synchronization strategy to use.
     */
    public MapWrapper(SyncStrategy strategy) {
        PreCon.notNull(strategy);

        _sync = strategy.getSync(this);
        _strategy = new SyncStrategy(_sync);
        _lock = _sync instanceof ReadWriteLock
                ? (ReadWriteLock)_sync
                : null;

        _valuesWrapper = new ValuesWrapper();
        _keySetWrapper = new KeySetWrapper();
        _entrySetWrapper = new EntrySetWrapper();
    }

    /**
     * Invoked after an entry is put into the map.
     *
     * <p>Not guaranteed to be called from a synchronized block.</p>
     *
     * <p>Intended to be optionally overridden by implementation.</p>
     *
     * @param key    The key.
     * @param value  The value.
     */
    protected void onPut(K key, V value) {}

    /**
     * Invoked after an entry is removed from the map except
     * when the map is cleared.
     *
     * <p>Not guaranteed to be called from a synchronized block.</p>
     *
     * <p>Intended to be optionally overridden by implementation.</p>
     *
     * @param key      The key.
     * @param removed  The value.
     */
    protected void onRemove(Object key, V removed) {}

    /**
     * Invoked after the map is cleared.
     *
     * <p>Not guaranteed to be called from a synchronized block.</p>
     *
     * <p>Intended to be optionally overridden by implementation.</p>
     *
     * @param entries  The cleared entries.
     */
    protected void onClear(Collection<Entry<K, V>> entries) {}

    /**
     * Invoked from a synchronized block to get the encapsulated {@link Map}.
     */
    protected abstract Map<K, V> map();

    @Override
    public int size() {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return map().size();
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return map().size();
            }
        } else {
            return map().size();
        }
    }

    @Override
    public boolean isEmpty() {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return map().isEmpty();
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return map().isEmpty();
            }
        } else {
            return map().isEmpty();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return map().containsKey(key);
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return map().containsKey(key);
            }
        } else {
            return map().containsKey(key);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return map().containsValue(value);
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return map().containsValue(value);
            }
        } else {
            return map().containsValue(value);
        }
    }

    @Override
    public V get(Object key) {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return map().get(key);
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return map().get(key);
            }
        } else {
            return map().get(key);
        }
    }

    @Override
    public V put(K key, V value) {
        PreCon.notNull(key);

        V previous;

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                previous = map().put(key, value);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                previous = map().put(key, value);
            }
        } else {
            previous = map().put(key, value);
        }

        if (previous == null || !previous.equals(value)) {
            onPut(key, value);
        }

        return previous;
    }

    @Override
    public V remove(Object key) {

        V removed;

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                removed = map().remove(key);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                removed = map().remove(key);
            }
        } else {
            removed = map().remove(key);
        }

        if (removed != null)
            onRemove(key, removed);

        return removed;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

        List<Entry<? extends K, ? extends V>> entrySet;

        if (_lock != null) {
            _lock.readLock().lock();
            try {
                entrySet = new ArrayList<Entry<? extends K, ? extends V>>(m.entrySet());
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                entrySet = new ArrayList<Entry<? extends K, ? extends V>>(m.entrySet());
            }
        } else {
            entrySet = new ArrayList<Entry<? extends K, ? extends V>>(m.entrySet());
        }

        for (Entry<? extends K, ? extends V> entry : entrySet) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                clearSource();
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                clearSource();
            }
        } else {
            clearSource();
        }
    }

    private void clearSource() {
        Set<Entry<K, V>> entries = new HashSet<>(map().entrySet());

        map().clear();

        onClear(entries);
    }

    @Override
    public Set<K> keySet() {
        return _keySetWrapper;
    }

    @Override
    public Collection<V> values() {
        return _valuesWrapper;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return _entrySetWrapper;
    }

    private class ValuesWrapper extends CollectionWrapper<V> {

        ValuesWrapper() {
            super(MapWrapper.this._strategy);
        }

        @Override
        protected boolean onPreAdd(V v) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected boolean onPreRemove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void onPreClear() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected Collection<V> collection() {
            return map().values();
        }
    }

    private class KeySetWrapper extends SetWrapper<K> {

        KeySetWrapper() {
            super(MapWrapper.this._strategy);
        }

        V removed;
        Set<Entry<K, V>> cleared;

        @Override
        protected Set<K> set() {
            return map().keySet();
        }

        @Override
        protected boolean onPreAdd(K k) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected boolean onPreRemove(Object o) {
            if (_lock != null) {
                _lock.readLock().lock();
                try {
                    //noinspection SuspiciousMethodCalls
                    removed = map().get(o);
                }
                finally {
                    _lock.readLock().unlock();
                }
            }
            else if (_sync != null) {
                synchronized (_sync) {
                    //noinspection SuspiciousMethodCalls
                    removed = map().get(o);
                }
            } else {
                //noinspection SuspiciousMethodCalls
                removed = map().get(o);
            }
            return true;
        }

        @Override
        protected void onRemoved(Object key) {
            MapWrapper.this.onRemove(key, removed);
            removed = null;
        }

        @Override
        protected void onPreClear() {

            if (_lock != null) {
                _lock.readLock().lock();
                try {
                    cleared = new HashSet<Entry<K, V>>(map().entrySet());
                }
                finally {
                    _lock.readLock().unlock();
                }
            }
            else if (_sync != null) {
                synchronized (_sync) {
                    cleared = new HashSet<Entry<K, V>>(map().entrySet());
                }
            } else {
                cleared = new HashSet<Entry<K, V>>(map().entrySet());
            }
        }

        @Override
        protected void onClear(Collection<K> values) {
            MapWrapper.this.onClear(cleared);
            cleared = null;
        }
    }

    private class EntrySetWrapper extends SetWrapper<Entry<K, V>> {

        EntrySetWrapper() {
            super(MapWrapper.this._strategy);
        }

        @Override
        protected Set<Entry<K, V>> set() {
            return map().entrySet();
        }

        @Override
        protected boolean onPreAdd(Entry<K, V> kvEntry) {
            return true;
        }

        @Override
        protected void onAdded(Entry<K, V> kvEntry) {
            MapWrapper.this.onPut(kvEntry.getKey(), kvEntry.getValue());
        }

        @Override
        protected boolean onPreRemove(Object o) {
            return o instanceof Entry;
        }

        @Override
        protected void onRemoved(Object o) {
            if (o instanceof Entry) {

                @SuppressWarnings("unchecked")
                Entry<K, V> entry = (Entry<K, V>)o;

                MapWrapper.this.onRemove(entry.getKey(), entry.getValue());
            }
        }

        @Override
        protected void onClear(Collection<Entry<K, V>> values) {
            MapWrapper.this.onClear(values);
        }
    }
}
