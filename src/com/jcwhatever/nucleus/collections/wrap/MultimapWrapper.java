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

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.jcwhatever.nucleus.collections.ElementCounter;
import com.jcwhatever.nucleus.collections.ElementCounter.ElementCount;
import com.jcwhatever.nucleus.collections.ElementCounter.RemovalPolicy;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import javax.annotation.Nonnull;

/**
 * An abstract implementation of a synchronized {@link Multimap} wrapper. The wrapper is
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
 * {@code onPutAll}, {@link #onRemove}, {@link @onRemoveAll}, {@link #onClear}</p>
 */
public abstract class MultimapWrapper<K, V> implements Multimap<K, V> {

    private final KeySetWrapper _keySet;
    private final EntrySetWrapper _entrySet;
    private final ValuesWrapper _values;
    private final AsMapWrapper _asMap;

    protected final Object _sync;
    protected final ReadWriteLock _lock;
    protected final SyncStrategy _strategy;

    /**
     * Constructor.
     *
     * <p>No synchronization.</p>
     */
    public MultimapWrapper() {
        this(SyncStrategy.NONE);
    }

    /**
     * Constructor.
     *
     * @param strategy  The synchronization strategy to use.
     */
    public MultimapWrapper(SyncStrategy strategy) {
        PreCon.notNull(strategy);

        _sync = strategy.getSync(this);
        _strategy = new SyncStrategy(_sync);
        _lock = _sync instanceof ReadWriteLock
                ? (ReadWriteLock)_sync
                : null;

        _keySet = new KeySetWrapper();
        _entrySet = new EntrySetWrapper();
        _values = new ValuesWrapper();
        _asMap = new AsMapWrapper();
    }

    /**
     * Invoked after put an entry into the map.
     *
     * <p>Not guaranteed to be called from a synchronized block.</p>
     *
     * <p>Intended to be optionally overridden by implementation.</p>
     *
     * @param key    The entry key.
     * @param value  The entry value.
     */
    protected abstract void onPut(K key, V value);

    /**
     * Invoked after putting multiple values into the map. It is not
     * guaranteed that this is invoked for all batch "put" operations. In some
     * cases, {@code #onPut} is invoked for each entry added instead.
     *
     * <p>Not guaranteed to be called from a synchronized block.</p>
     *
     * <p>Intended to be optionally overridden by implementation.</p>
     *
     * @param key     The key.
     * @param values  The values added.
     */
    protected abstract void onPutAll(K key, Iterable<? extends V> values);

    /**
     * Invoked after removing a value from an entry except when
     * the map is cleared.
     *
     * <p>Not guaranteed to be called from a synchronized block.</p>
     *
     * <p>Intended to be optionally overridden by implementation.</p>
     *
     * @param key    The key.
     * @param value  The value removed from the entry.
     */
    protected abstract void onRemove(Object key, Object value);

    /**
     * Invoked after removing an entry from the map except when the
     * map is cleared. It is not guaranteed that this will be called
     * for all batch remove operations. In some cases, the {@code #onRemove}
     * method may be invoked for each value removed.
     *
     * <p>Not guaranteed to be called from a synchronized block.</p>
     *
     * <p>Intended to be optionally overridden by implementation.</p>
     *
     * @param key     The key.
     * @param values  The values associated with the removed entry.
     */
    protected abstract void onRemoveAll(Object key, Collection<V> values);

    /**
     * Invoked after the map is cleared.
     *
     * <p>Not guaranteed to be called from a synchronized block.</p>
     *
     * <p>Intended to be optionally overridden by implementation.</p>
     *
     * @param entries  The entries that were cleared from the map.
     */
    protected abstract void onClear(Collection<Entry<K, V>> entries);

    /**
     * Invoked from a synchronized block to get
     * the encapsulated {@code Multimap}.
     */
    protected abstract Multimap<K, V> map();

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
    public boolean containsKey(Object o) {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return map().containsKey(o);
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return map().containsKey(o);
            }
        } else {
            return map().containsKey(o);
        }
    }

    @Override
    public boolean containsValue(Object o) {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return map().containsValue(o);
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return map().containsValue(o);
            }
        } else {
            return map().containsValue(o);
        }
    }

    @Override
    public boolean containsEntry(Object o, Object o1) {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return map().containsEntry(o, o1);
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return map().containsEntry(o, o1);
            }
        } else {
            return map().containsEntry(o, o1);
        }
    }

    @Override
    public boolean put(@Nonnull K k, @Nonnull V v) {
        PreCon.notNull(k);
        PreCon.notNull(v);

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                return putSource(k, v);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return putSource(k, v);
            }
        } else {
            return putSource(k, v);
        }
    }

    private boolean putSource(K k, V v) {
        if (map().put(k, v)) {
            onPut(k, v);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(@Nonnull Object key, @Nonnull Object value) {
        PreCon.notNull(key);
        PreCon.notNull(value);

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                return removeSource(key, value);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return removeSource(key, value);
            }
        } else {
            return removeSource(key, value);
        }
    }

    private boolean removeSource(Object o, Object o1) {
        if (map().remove(o, o1)) {
            onRemove(o, o1);
            return true;
        }
        return false;
    }

    @Override
    public boolean putAll(@Nonnull K k, @Nonnull Iterable<? extends V> iterable) {
        PreCon.notNull(k);
        PreCon.notNull(iterable);

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                return putAllSource(k, iterable);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return putAllSource(k, iterable);
            }
        } else {
            return putAllSource(k, iterable);
        }
    }

    private boolean putAllSource(K k, Iterable<? extends V> iterable) {
        if (map().putAll(k, iterable)) {
            onPutAll(k, iterable);
            return true;
        }
        return false;
    }

    @Override
    public boolean putAll(@Nonnull Multimap<? extends K, ? extends V> multimap) {
        PreCon.notNull(multimap);

        boolean isChanged = false;

        for (Entry<? extends K, ? extends V> entry : multimap.entries()) {

            isChanged = put(entry.getKey(), entry.getValue()) || isChanged;
        }

        return isChanged;
    }

    @Override
    public Collection<V> replaceValues(@Nonnull K k, @Nonnull Iterable<? extends V> iterable) {
        PreCon.notNull(k);
        PreCon.notNull(iterable);

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                return replaceValuesSource(k, iterable);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return replaceValuesSource(k, iterable);
            }
        } else {
            return replaceValuesSource(k, iterable);
        }
    }

    private Collection<V> replaceValuesSource(K k, Iterable<? extends V> iterable) {
        Collection<V> removed = map().removeAll(k);
        ElementCounter<V> counter;

        counter = new ElementCounter<>(RemovalPolicy.KEEP_COUNTING, removed);

        map().putAll(k, iterable);

        counter.subtractAll(map().get(k));

        for (ElementCount<V> elmCount : counter) {

            V agent = elmCount.getElement();

            for (int i=0; i < Math.abs(elmCount.getCount()); i++) {
                if (elmCount.getCount() > 0) {
                    onRemove(k, agent);
                }
                else {
                    onPut(k, agent);
                }
            }
        }

        return removed;
    }

    @Override
    public Collection<V> removeAll(Object o) {

        Collection<V> removed;

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                removed = map().removeAll(o);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                removed = map().removeAll(o);
            }
        } else {
            removed = map().removeAll(o);
        }

        onRemoveAll(o, removed);

        return removed;
    }

    @Override
    public void clear() {

        Collection<Entry<K, V>> entries;

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                entries = new HashSet<>(map().entries());
                map().clear();
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                entries = new HashSet<>(map().entries());
                map().clear();
            }
        } else {
            entries = new HashSet<>(map().entries());
            map().clear();
        }

        onClear(entries);
    }

    @Override
    public Collection<V> get(K k) {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return new GetWrapper(k, map().get(k));
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return new GetWrapper(k, map().get(k));
            }
        } else {
            return new GetWrapper(k, map().get(k));
        }
    }

    @Override
    public Set<K> keySet() {
        return _keySet;
    }

    @Override
    public Multiset<K> keys() {
        return map().keys(); // TODO: Wrap
    }

    @Override
    public Collection<V> values() {
        return _values;
    }

    @Override
    public Collection<Entry<K, V>> entries() {
        return _entrySet;
    }

    @Override
    public Map<K, Collection<V>> asMap() {
        return _asMap;
    }

    private class GetWrapper extends CollectionWrapper<V> {

        final K key;
        final Collection<V> collection;

        GetWrapper(K key, Collection<V> collection) {
            super(MultimapWrapper.this._strategy);
            this.key = key;
            this.collection = collection;
        }

        @Override
        protected void onAdded(V v) {
            onPut(key, v);
        }

        @Override
        protected void onRemoved(Object o) {
            onRemove(key, o);
        }

        @Override
        protected Collection<V> collection() {
            return collection;
        }
    }

    private class ValuesWrapper extends CollectionWrapper<V> {

        ValuesWrapper() {
            super(MultimapWrapper.this._strategy);
        }

        @Override
        protected void onAdded(V v) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void onRemoved(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected Collection<V> collection() {
            return map().values();
        }
    }

    private class KeySetWrapper extends SetWrapper<K> {

        KeySetWrapper() {
            super(MultimapWrapper.this._strategy);
        }

        Collection<V> removed;

        @Override
        protected Set<K> set() {
            return map().keySet();
        }

        @Override
        protected void onAdded(K k) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected boolean onPreRemove(Object o) {

            @SuppressWarnings("unchecked")
            K k = (K)o;

            removed = map().get(k);

            return true;
        }

        @Override
        protected void onRemoved(Object o) {
            onRemoveAll(o, removed);
            removed = null;
        }
    }

    private class AsMapWrapper extends MapWrapper<K, Collection<V>> {

        AsMapWrapper() {
            super(MultimapWrapper.this._strategy);
        }

        @Override
        protected void onPut(K key, Collection<V> value) {
            MultimapWrapper.this.onPutAll(key, value);
        }

        @Override
        protected void onRemove(Object key, Collection<V> removed) {
            MultimapWrapper.this.onRemoveAll(key, removed);
        }

        @Override
        protected void onClear(Collection<Entry<K, Collection<V>>> entries) {
            for (Entry<K, Collection<V>> entry : entries) {
                MultimapWrapper.this.onRemoveAll(entry.getKey(), entry.getValue());
            }
        }

        @Override
        protected Map<K, Collection<V>> map() {
            return MultimapWrapper.this.asMap();
        }
    }

    private class EntrySetWrapper extends CollectionWrapper<Entry<K, V>> {

        EntrySetWrapper() {
            super(MultimapWrapper.this._strategy);
        }

        @Override
        protected boolean onPreAdd(Entry<K, V> kvEntry) {
            return true;
        }

        @Override
        protected void onAdded(Entry<K, V> kvEntry) {
            MultimapWrapper.this.onPut(kvEntry.getKey(), kvEntry.getValue());
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

                MultimapWrapper.this.onRemove(entry.getKey(), entry.getValue());
            }
        }

        @Override
        protected void onClear(Collection<Entry<K, V>> values) {
            MultimapWrapper.this.onClear(values);
        }

        @Override
        protected Collection<Entry<K, V>> collection() {
            return map().entries();
        }
    }
}
