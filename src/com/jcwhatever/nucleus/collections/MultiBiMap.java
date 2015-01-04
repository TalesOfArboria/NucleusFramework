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


package com.jcwhatever.nucleus.collections;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;
import com.jcwhatever.nucleus.collections.wrappers.AbstractCollectionWrapper;
import com.jcwhatever.nucleus.collections.wrappers.AbstractIteratorWrapper;
import com.jcwhatever.nucleus.collections.wrappers.AbstractMapWrapper;
import com.jcwhatever.nucleus.collections.wrappers.AbstractSetWrapper;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Allows adding multiple values per key.
 *
 * <p>Also allows getting keys based on value.</p>
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class MultiBiMap<K, V> implements SetMultimap<K, V> {

    // keyed to key type
    private SetMultimap<K, V> _keyToValue;

    // keyed to value type
    private SetMultimap<V, K> _valueToKey;

    private final MapWrapper _mapWrapper = new MapWrapper();
    private final EntrySetWrapper _entrySet = new EntrySetWrapper();
    private final KeySetWrapper _keySet = new KeySetWrapper();

    /**
     * Constructor.
     */
    public MultiBiMap() {
        this(10, 10);
    }

    /**
     * Constructor.
     *
     * @param size  The initial capacity of the map.
     */
    public MultiBiMap(int size) {
        this(size, 10);
    }

    /**
     * Constructor.
     *
     * @param size       The initial capacity of the map.
     * @param entrySize  The initial capacity of the internal collections for each key.
     */
    public MultiBiMap(int size, int entrySize) {
        PreCon.positiveNumber(size);
        PreCon.positiveNumber(entrySize);

        _keyToValue = MultimapBuilder.hashKeys(size).linkedHashSetValues(entrySize).build();
        _valueToKey = HashMultimap.create(size, entrySize);
    }

    /**
     * Get the number of keys in the map.
     */
    public int keySize() {
        return _keyToValue.size();
    }

    /**
     * Get the number of unique values in the map.
     */
    public int valueSize() {
        return _valueToKey.size();
    }

    /**
     * Put a value into the map.
     *
     * @param key    The key associated with the value.
     * @param value  The value
     *
     * @return Self for chaining
     */
    public MultiBiMap<K, V> add(K key, V value) {
        PreCon.notNull(key);
        PreCon.notNull(value);

        put(key, value);

        return this;
    }

    /**
     * Gets a key associated with the value.
     *
     * <p>Order of keys is not guaranteed.</p>
     *
     * @param value  The value to check.
     */
    @Nullable
    public K getKey(V value) {
        PreCon.notNull(value);

        Set<K> set = _valueToKey.get(value);
        if (set == null)
            return null;

        if (set.isEmpty())
            return null;

        return new ArrayList<K>(set).get(0);
    }

    /**
     * Get a list of keys associated with the specified value.
     *
     * @param value  The value to check.
     */
    @Nullable
    public Set<K> getKeys(V value) {
        PreCon.notNull(value);

        return _valueToKey.get(value);
    }

    /**
     * Return the first value associated with a key in the map.
     *
     * @param key  The key to check.
     */
    @Nullable
    public V getValue(K key) {
        PreCon.notNull(key);

        Set<V> values = _keyToValue.get(key);
        if (values.isEmpty())
            return null;

        return new ArrayList<V>(values).get(0);
    }

    /**
     * Remove a value from a key value collection.
     *
     * @param key    The key to check.
     * @param value  The value to remove.
     *
     * @return  True if value was found and removed.
     */
    public boolean removeValue(K key, V value) {
        PreCon.notNull(key);
        PreCon.notNull(value);

        Set<V> values = _keyToValue.get(key);
        return values != null && values.remove(value);
    }

    /**
     * Remove a value from all keys in the map.
     *
     * @param value  The value to remove.
     *
     * @return Number of values removed.
     */
    public int removeValues(V value) {
        PreCon.notNull(value);

        Set<K> keys = _valueToKey.get(value);
        if (keys == null)
            return 0;

        int removeCount = 0;

        for (K key : keys) {
            Set<V> values = _keyToValue.get(key);
            if (values == null)
                continue;

            values.remove(value);
            removeCount++;

            if (values.size() == 0)
                removeAll(key);
        }

        return removeCount;
    }


    @Override
    public void clear() {
        _keyToValue.clear();
        _valueToKey.clear();
    }

    @Override
    public int size() {
        return _keyToValue.size();
    }

    @Override
    public boolean isEmpty() {
        return _keyToValue.isEmpty();
    }

    @Override
    public boolean containsKey(@Nonnull Object key) {
        PreCon.notNull(key);

        return _keyToValue.containsKey(key);
    }

    @Override
    public boolean containsValue(@Nonnull Object value) {
        PreCon.notNull(value);

        return _valueToKey.containsKey(value);
    }

    @Override
    public boolean containsEntry(@Nonnull Object key, @Nonnull Object value) {
        PreCon.notNull(key);
        PreCon.notNull(value);

        return _keyToValue.containsEntry(key, value);
    }

    @Override
    public Set<K> keySet() {
        return _keySet;
    }

    @Override
    public Multiset<K> keys() {
        return _keyToValue.keys(); // TODO: Wrap
    }

    @Override
    public boolean put(@Nonnull K key, @Nonnull V value) {
        PreCon.notNull(key);
        PreCon.notNull(value);

        if (_keyToValue.put(key, value)) {
            _valueToKey.put(value, key);
            return true;
        }

        return false;
    }

    @Override
    public boolean remove(@Nonnull Object key, @Nonnull Object value) {
        PreCon.notNull(key);
        PreCon.notNull(value);

        if (_keyToValue.remove(key, value)) {
            _valueToKey.remove(value, key);
            return true;
        }
        return false;
    }

    @Override
    public boolean putAll(@Nonnull K k, Iterable<? extends V> iterable) {
        PreCon.notNull(k);
        PreCon.notNull(iterable);

        boolean isChanged = false;

        for (V value : iterable) {
            isChanged = put(k, value) || isChanged;
        }

        return isChanged;
    }

    @Override
    public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
        PreCon.notNull(multimap);

        boolean isChanged = false;

        for (Entry<? extends K, ? extends V> entry : multimap.entries()) {
            isChanged = put(entry.getKey(), entry.getValue()) || isChanged;
        }

        return isChanged;
    }

    @Override
    public Collection<V> values() {
        return _valueToKey.keySet();
    }

    @Override
    public Set<V> get(@Nonnull K key) {
        PreCon.notNull(key);

        return _keyToValue.get(key);
    }

    @Override
    public Set<V> removeAll(@Nonnull Object key) {
        PreCon.notNull(key);

        Set<V> removed = _keyToValue.removeAll(key);

        for (V value : removed) {
            Set<K> keys = _valueToKey.get(value);
            //noinspection SuspiciousMethodCalls
            keys.remove(key);
        }

        _valueToKey.removeAll(removed);
        return removed;
    }

    @Override
    public Set<V> replaceValues(K k, Iterable<? extends V> iterable) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public Set<Entry<K, V>> entries() {
        return _entrySet;
    }

    @Override
    public Map<K, Collection<V>> asMap() {
        return _mapWrapper;
    }

    private class KeySetWrapper extends AbstractSetWrapper<K> {

        @Override
        public Iterator<K> iterator() {
            return new KeySetIteratorWrapper();
        }

        @Override
        public boolean add(K key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object key) {
            return !MultiBiMap.this.removeAll(key).isEmpty();
        }

        @Override
        public boolean addAll(Collection<? extends K> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean isChanged = false;
            for (Object obj : c) {
                isChanged = remove(obj) || isChanged;
            }
            return isChanged;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            Set<K> removed = new HashSet<>(_keyToValue.keySet());

            for (Object obj : c) {
                //noinspection SuspiciousMethodCalls
                removed.remove(obj);
            }

            for (K key : removed) {
                remove(key);
            }

            return removed.size() != _keyToValue.keySet().size();
        }

        @Override
        protected Set<K> getSet() {
            return _keyToValue.keySet();
        }
    }

    private class KeySetIteratorWrapper extends AbstractIteratorWrapper<K> {

        Iterator<K> iterator = _keyToValue.keySet().iterator();

        @Override
        public void remove() {
            MultiBiMap.this.removeAll(_current);
        }

        @Override
        protected Iterator<K> getIterator() {
            return iterator;
        }
    }

    private class EntrySetWrapper extends AbstractSetWrapper<Entry<K, V>> {

        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new EntrySetIteratorWrapper();
        }

        @Override
        public boolean add(Entry<K, V> e) {
            return MultiBiMap.this.put(e.getKey(), e.getValue());
        }

        @Override
        public boolean remove(Object o) {
            if (o instanceof Entry) {
                Entry<?, ?> entry = (Entry<?, ?>)o;
                return MultiBiMap.this.remove(entry.getKey(), entry.getValue());
            }
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends Entry<K, V>> c) {
            boolean isChanged = false;
            for (Entry<K, V> entry : c) {
                isChanged = MultiBiMap.this.put(entry.getKey(), entry.getValue()) || isChanged;
            }
            return isChanged;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean isChanged = false;
            for (Object obj : c) {
                isChanged = remove(obj) || isChanged;
            }
            return isChanged;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            Set<Entry<K, V>> entries = new HashSet<>(_keyToValue.entries());

            for (Object obj : c) {
                //noinspection SuspiciousMethodCalls
                entries.remove(obj);
            }

            for (Entry<K, V> entry : entries) {
                MultiBiMap.this.remove(entry.getKey(), entry.getValue());
            }

            return entries.size() != _keyToValue.entries().size();
        }

        @Override
        public void clear() {
            MultiBiMap.this.clear();
        }

        @Override
        protected Set<Entry<K, V>> getSet() {
            return _keyToValue.entries();
        }
    }

    private class EntrySetIteratorWrapper extends AbstractIteratorWrapper<Entry<K, V>> {

        Iterator<Entry<K, V>> iterator = _keyToValue.entries().iterator();

        @Override
        public void remove() {
            MultiBiMap.this.remove(_current.getKey(), _current.getValue());
        }

        @Override
        protected Iterator<Entry<K, V>> getIterator() {
            return iterator;
        }
    }

    private class MapWrapper extends AbstractMapWrapper<K, Collection<V>> {

        final MapKeySetWrapper keySet = new MapKeySetWrapper();
        final MapValuesWrapper values = new MapValuesWrapper();
        final MapEntrySetWrapper entrySet = new MapEntrySetWrapper();

        @Override
        public Collection<V> put(K key, Collection<V> values) {

            List<V> result = new ArrayList<V>(values.size());
            for (V value : values) {
                Set<K> keys = MultiBiMap.this._valueToKey.get(value);
                if (keys.contains(key))
                    result.add(value);

                MultiBiMap.this.put(key, value);
            }

            return result;
        }

        @Override
        public Collection<V> remove(Object key) {
            return MultiBiMap.this.removeAll(key);
        }

        @Override
        public void putAll(Map<? extends K, ? extends Collection<V>> m) {
            for (Entry<? extends K, ? extends Collection<V>> entry : m.entrySet()) {
                MultiBiMap.this.putAll(entry.getKey(), entry.getValue());
            }
        }

        @Override
        public void clear() {
            MultiBiMap.this.clear();
        }

        @Override
        public Set<K> keySet() {
            return keySet;
        }

        @Override
        public Collection<Collection<V>> values() {
            return values;
        }

        @Override
        public Set<Entry<K, Collection<V>>> entrySet() {
            return entrySet;
        }

        @Override
        protected Map<K, Collection<V>> getMap() {
            return _keyToValue.asMap();
        }
    }

    private class MapKeySetWrapper extends AbstractSetWrapper<K> {

        @Override
        public Iterator<K> iterator() {
            return new MapKeySetIteratorWrapper();
        }

        @Override
        public boolean add(K key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object key) {
            return !MultiBiMap.this.removeAll(key).isEmpty();
        }

        @Override
        public boolean addAll(Collection<? extends K> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean isChanged = false;
            for (Object key : c) {
                isChanged = !MultiBiMap.this.removeAll(key).isEmpty() || isChanged;
            }
            return isChanged;
        }

        @Override
        public boolean retainAll(Collection<?> c) {

            Set<K> removed = new HashSet<>(_keyToValue.keySet());

            for (Object key : c) {
                //noinspection SuspiciousMethodCalls
                removed.remove(key);
            }

            for (K key : removed) {
                MultiBiMap.this.removeAll(key);
            }

            return removed.size() != _keyToValue.keySet().size();
        }

        @Override
        public void clear() {
            MultiBiMap.this.clear();
        }

        @Override
        protected Set<K> getSet() {
            return _keyToValue.asMap().keySet();
        }
    }

    private class MapKeySetIteratorWrapper extends AbstractIteratorWrapper<K> {

        Iterator<K> iterator = _keyToValue.keySet().iterator();

        @Override
        public void remove() {
            MultiBiMap.this.removeAll(_current);
        }

        @Override
        protected Iterator<K> getIterator() {
            return iterator;
        }
    }

    private class MapValuesWrapper extends AbstractCollectionWrapper<Collection<V>> {

        @Override
        public Iterator<Collection<V>> iterator() {
            return new MapValuesIteratorWrapper();
        }

        @Override
        public boolean add(Collection<V> e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends Collection<V>> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            MultiBiMap.this.clear();
        }

        @Override
        protected Collection<Collection<V>> getCollection() {
            return _keyToValue.asMap().values();
        }
    }

    private class MapValuesIteratorWrapper extends AbstractIteratorWrapper<Collection<V>> {

        Iterator<Collection<V>> iterator = _keyToValue.asMap().values().iterator();

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected Iterator<Collection<V>> getIterator() {
            return iterator;
        }
    }

    private class MapEntrySetWrapper extends AbstractSetWrapper<Entry<K, Collection<V>>> {

        @Override
        public Iterator<Entry<K, Collection<V>>> iterator() {
            return getCollection().iterator();
        }

        @Override
        public boolean add(Entry<K, Collection<V>> e) {
            return MultiBiMap.this.putAll(e.getKey(), e.getValue());
        }

        @Override
        public boolean remove(Object o) {
            Set<Entry<K, Collection<V>>> entrySet = _keyToValue.asMap().entrySet();
            //noinspection SuspiciousMethodCalls
            if (!entrySet.contains(o))
                return false;

            for (Entry<K, Collection<V>> entry : entrySet) {
                if (entry.equals(o)) {
                    return !MultiBiMap.this.removeAll(entry.getKey()).isEmpty();
                }
            }

            return false;
        }

        @Override
        public boolean addAll(Collection<? extends Entry<K, Collection<V>>> c) {
            boolean isChanged = false;
            for (Entry<K, Collection<V>> entry : c) {
                isChanged = MultiBiMap.this.putAll(entry.getKey(), entry.getValue()) || isChanged;
            }
            return isChanged;
        }

        @Override
        public boolean removeAll(Collection<?> c) {

            Set<Entry<K, Collection<V>>> entrySet = _keyToValue.asMap().entrySet();
            //noinspection SuspiciousMethodCalls

            boolean isChanged = false;

            for (Object obj : c) {
                //noinspection SuspiciousMethodCalls
                if (!entrySet.contains(obj))
                    continue;

                Iterator<Entry<K, Collection<V>>> iterator = _keyToValue.asMap().entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry<K, Collection<V>> entry = iterator.next();

                    if (entry.equals(obj)) {
                        isChanged = !MultiBiMap.this.removeAll(entry.getKey()).isEmpty() || isChanged;
                        break;
                    }

                }
            }
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {

            Set<Entry<K, Collection<V>>> removed = new HashSet<>(_keyToValue.asMap().entrySet());

            for (Object obj : c) {
                //noinspection SuspiciousMethodCalls
                removed.remove(obj);
            }

            removeAll(removed);

            return removed.size() != _keyToValue.asMap().entrySet().size();
        }

        @Override
        public void clear() {
            MultiBiMap.this.clear();
        }

        @Override
        protected Set<Entry<K, Collection<V>>> getSet() {
            return _keyToValue.asMap().entrySet();
        }
    }

}
