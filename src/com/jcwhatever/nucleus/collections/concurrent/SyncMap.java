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

package com.jcwhatever.nucleus.collections.concurrent;

import com.jcwhatever.nucleus.utils.PreCon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An abstract implementation of a synchronized {@link Map} wrapper.
 *
 * <p>The actual map is provided to the abstract implementation by
 * overriding and returning it from the {@link #map} method.</p>
 *
 * <p>If the wrapper is being used to wrap a collection that is part of the internals
 * of another type, the other types synchronization object can be used by passing
 * it into the wrappers constructor.</p>
 *
 * <p>In order to make using the wrapper as an extension of a map easier,
 * several protected methods are provided for optional override. See {@link #onPut},
 * {@link #onRemove}, {@link #onClear}</p>
 */
public abstract class SyncMap<K, V> implements Map<K, V> {

    private final ValuesWrapper _valuesWrapper = new ValuesWrapper();
    private final KeySetWrapper _keySetWrapper = new KeySetWrapper();
    private final EntrySetWrapper _entrySetWrapper = new EntrySetWrapper();
    protected final Object _sync;

    /**
     * Constructor.
     */
    public SyncMap() {
        this(new Object());
    }

    /**
     * Constructor.
     *
     * @param sync  The synchronization object to use.
     */
    protected SyncMap(Object sync) {
        PreCon.notNull(sync);

        _sync = sync;
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
    protected abstract void onPut(K key, V value);

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
    protected abstract void onRemove(Object key, V removed);

    /**
     * Invoked after the map is cleared.
     *
     * <p>Not guaranteed to be called from a synchronized block.</p>
     *
     * <p>Intended to be optionally overridden by implementation.</p>
     *
     * @param entries  The cleared entries.
     */
    protected abstract void onClear(Collection<Entry<K, V>> entries);

    /**
     * Invoked from a synchronized block to get the encapsulated {@code Map}.
     */
    protected abstract Map<K, V> map();

    @Override
    public int size() {
        synchronized (_sync) {
            return map().size();
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (_sync) {
            return map().isEmpty();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        synchronized (_sync) {
            return map().containsKey(key);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        synchronized (_sync) {
            return map().containsValue(value);
        }
    }

    @Override
    public V get(Object key) {
        synchronized (_sync) {
            return map().get(key);
        }
    }

    @Override
    public V put(K key, V value) {
        PreCon.notNull(key);
        PreCon.notNull(value);

        V previous;

        synchronized (_sync) {
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

        synchronized (_sync) {
            removed = map().remove(key);
        }

        if (removed != null)
            onRemove(key, removed);

        return removed;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

        List<Entry<? extends K, ? extends V>> entrySet;

        synchronized (_sync) {
            entrySet = new ArrayList<Entry<? extends K, ? extends V>>(m.entrySet());
        }

        for (Entry<? extends K, ? extends V> entry : entrySet) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {

        synchronized (_sync) {

            Set<Entry<K, V>> entries = map().entrySet();

            map().clear();

            onClear(entries);
        }
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

    private class ValuesWrapper extends SyncCollection<V> {

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

    private class KeySetWrapper extends SyncSet<K> {

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
            synchronized (_sync) {
                //noinspection SuspiciousMethodCalls
                removed = map().get(o);
            }
            return true;
        }

        @Override
        protected void onRemoved(Object key) {
            SyncMap.this.onRemove(key, removed);
            removed = null;
        }

        @Override
        protected void onPreClear() {
            synchronized (_sync) {
                cleared = new HashSet<Entry<K, V>>(map().entrySet());
            }
        }

        @Override
        protected void onClear(Collection<K> values) {
            SyncMap.this.onClear(cleared);
            cleared = null;
        }
    }

    private class EntrySetWrapper extends SyncSet<Entry<K, V>> {

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
            SyncMap.this.onPut(kvEntry.getKey(), kvEntry.getValue());
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

                SyncMap.this.onRemove(entry.getKey(), entry.getValue());
            }
        }

        @Override
        protected void onClear(Collection<Entry<K, V>> values) {
            SyncMap.this.onClear(values);
        }
    }
}
