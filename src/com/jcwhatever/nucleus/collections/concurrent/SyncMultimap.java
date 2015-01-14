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

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.jcwhatever.nucleus.collections.ElementCounter;
import com.jcwhatever.nucleus.collections.ElementCounter.ElementCount;
import com.jcwhatever.nucleus.collections.ElementCounter.RemovalPolicy;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nonnull;

/**
 * An abstract implementation of a synchronized {@link Multimap} wrapper.
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
 * {@code onPutAll}, {@link #onRemove}, {@link @onRemoveAll}, {@link #onClear}</p>
 */
public abstract class SyncMultimap<K, V> implements Multimap<K, V> {

    protected final Object _sync;
    private final KeySetWrapper _keySet;
    private final ValuesWrapper _values;
    private final AsMapWrapper _asMap;

    /**
     * Constructor.
     */
    public SyncMultimap() {
        this(new Object());
    }

    /**
     * Constructor.
     *
     * @param sync  The synchronization object to use.
     */
    protected SyncMultimap(Object sync) {
        PreCon.notNull(sync);

        _sync = sync;

        _keySet = new KeySetWrapper();
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
    public boolean containsKey(Object o) {
        synchronized (_sync) {
            return map().containsKey(o);
        }
    }

    @Override
    public boolean containsValue(Object o) {
        synchronized (_sync) {
            return map().containsValue(o);
        }
    }

    @Override
    public boolean containsEntry(Object o, Object o1) {
        synchronized (_sync) {
            return map().containsEntry(o, o1);
        }
    }

    @Override
    public boolean put(@Nonnull K k, @Nonnull V v) {
        PreCon.notNull(k);
        PreCon.notNull(v);

        synchronized (_sync) {

            if (map().put(k, v)) {
                onPut(k, v);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean remove(@Nonnull Object o, @Nonnull Object o1) {
        PreCon.notNull(o);
        PreCon.notNull(o1);

        synchronized (_sync) {

            if (map().remove(o, o1)) {
                onRemove(o, o1);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean putAll(@Nonnull K k, @Nonnull Iterable<? extends V> iterable) {
        PreCon.notNull(k);
        PreCon.notNull(iterable);

        synchronized (_sync) {

            if (map().putAll(k, iterable)) {

                onPutAll(k, iterable);
                return true;
            }
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

        Collection<V> removed;
        ElementCounter<V> counter;

        synchronized (_sync) {
            removed = map().removeAll(k);

            counter = new ElementCounter<>(RemovalPolicy.KEEP_COUNTING, removed);

            map().putAll(k, iterable);

            counter.subtractAll(map().get(k));
        }

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

        synchronized (_sync) {
             removed = map().removeAll(o);
        }

        onRemoveAll(o, removed);

        return removed;
    }

    @Override
    public void clear() {

        Collection<Entry<K, V>> entries;

        synchronized (_sync) {

            entries = map().entries();

            map().clear();
        }

        onClear(entries);
    }

    @Override
    public Collection<V> get(K k) {
        synchronized (_sync) {
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
        return null; // TODO
    }

    @Override
    public Map<K, Collection<V>> asMap() {
        return _asMap;
    }

    private class GetWrapper extends SyncCollection<V> {

        final K key;
        final Collection<V> collection;

        GetWrapper(K key, Collection<V> collection) {
            super(SyncMultimap.this._sync);
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

    private class ValuesWrapper extends SyncCollection<V> {

        ValuesWrapper() {
            super(SyncMultimap.this._sync);
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

    private class KeySetWrapper extends SyncSet<K> {

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

    private class AsMapWrapper extends SyncMap<K, Collection<V>> {

        @Override
        protected void onPut(K key, Collection<V> value) {
            SyncMultimap.this.onPutAll(key, value);
        }

        @Override
        protected void onRemove(Object key, Collection<V> removed) {
            SyncMultimap.this.onRemoveAll(key, removed);
        }

        @Override
        protected void onClear(Collection<Entry<K, Collection<V>>> entries) {
            for (Entry<K, Collection<V>> entry : entries) {
                SyncMultimap.this.onRemoveAll(entry.getKey(), entry.getValue());
            }
        }

        @Override
        protected Map<K, Collection<V>> map() {
            return SyncMultimap.this.asMap();
        }
    }
}
