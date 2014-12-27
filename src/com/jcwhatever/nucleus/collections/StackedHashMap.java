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

import com.jcwhatever.nucleus.collections.wrappers.AbstractIteratorWrapper;
import com.jcwhatever.nucleus.collections.wrappers.AbstractSetWrapper;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Can add multiple values per key except values are placed in a {@Code LinkedList}.
 * Removing a value removes the last item added to the linked list.
 *
 * <p>The {@code LinkedList}'s that store the values are used as stack's.</p>
 *
 * @param <K>  Key type
 * @param <V>  Value type
 */
public class StackedHashMap<K, V> implements Map<K, V> {

    private Map<K, LinkedList<V>> _map;
    private Set<V> _cachedValues;
    private Set<K> _keySet;

    /**
     * Constructor.
     */
    public StackedHashMap() {
        this(10);
    }

    /**
     * Constructor.
     *
     * @param size  The initial size.
     */
    public StackedHashMap(int size) {
        PreCon.positiveNumber(size);

        _map = new HashMap<K, LinkedList<V>>(size);
        _keySet = new StackedKeySet();
    }

    /**
     * Determine if the map is empty.
     */
    @Override
    public boolean isEmpty() {
        return _map.isEmpty();
    }

    /**
     * Clear all items
     */
    @Override
    public void clear() {
        _map.clear();
        resetCache();
    }

    /**
     * Determine if the map contains the specified key.
     *
     * @param key  The key to check.
     */
    @Override
    public boolean containsKey(Object key) {
        PreCon.notNull(key);

        return _map.containsKey(key);
    }

    /**
     * Determine if the map contains the specified value.
     *
     * @param value  The value to check.
     */
    @Override
    public boolean containsValue(Object value) {
        PreCon.notNull(value);

        //noinspection SuspiciousMethodCalls
        return valueSet().contains(value);
    }

    /**
     * Unsupported.
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    /**
     * Get a value using the specified key.
     *
     * <p>The value returned is peeked from the stack, if one is found.</p>
     *
     * @param key  The key to check.
     */
    @Override
    @Nullable
    public V get(Object key) {
        PreCon.notNull(key);

        LinkedList<V> stack = _map.get(key);
        if (stack != null && !stack.isEmpty())
            return stack.peekLast();

        return null;
    }

    /**
     * Get the maps keys.
     */
    @Override
    public Set<K> keySet() {
        return _keySet;
    }

    /**
     * Put a value into the map.
     *
     * @param key    The key to use.
     * @param value  The value to add.
     */
    @Override
    public V put(K key, V value) {
        PreCon.notNull(key);
        PreCon.notNull(value);

        LinkedList<V> stack = _map.get(key);
        if (stack == null) {
            stack = new LinkedList<>();
            _map.put(key, stack);
        }

        stack.push(value);

        resetCache();

        return value;
    }

    /**
     * Put map values.
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        PreCon.notNull(map);

        for (Entry<? extends K, ? extends V> set : map.entrySet()) {
            put(set.getKey(), set.getValue());
        }
    }

    /**
     * Remove a value by key. Pops a value from the internal stack
     * represented by the key.
     *
     * @param key  The key of the stack to pop.
     *
     * @return Returns the popped value, if any.
     */
    @Override
    @Nullable
    public V remove(Object key) {
        PreCon.notNull(key);

        //noinspection SuspiciousMethodCalls
        LinkedList<V> stack = _map.get(key);
        if (stack == null) {
            return null;
        }

        if (stack.isEmpty())
            _map.remove(key);

        resetCache();

        return stack.pop();
    }

    /**
     * Get the number of entries in the map.
     * Or in other words, the number of keys.
     */
    @Override
    public int size() {
        return _map.size();
    }

    /**
     * Get the number of items in the internal stack
     * represented by the specified key.
     *
     * @param key  The key to check.
     */
    public int keySize(Object key) {
        PreCon.notNull(key);

        //noinspection SuspiciousMethodCalls
        LinkedList<V> stack = _map.get(key);
        if (stack == null || stack.isEmpty()) {
            return 0;
        }

        return stack.size();
    }

    /**
     * Get all values in the map.
     *
     * <p>No duplicate values are in the returned collection.</p>
     */
    @Override
    public Collection<V> values() {
        return new HashSet<>(valueSet());
    }

    private Set<V> valueSet() {
        if (_cachedValues != null)
            return _cachedValues;

        Collection<LinkedList<V>> values = _map.values();
        Set<V> results = new HashSet<V>(values.size());

        for (LinkedList<V> stack : values) {
            results.addAll(stack);
        }

        _cachedValues = results;

        return results;
    }

    private void resetCache() {
        _cachedValues = null;
    }

    private final class StackedKeySet extends AbstractSetWrapper<K> {

        @Override
        public Iterator<K> iterator() {
            return new StackedKeySetIterator();
        }

        @Override
        public boolean add(K k) {
            if (_map.keySet().add(k)) {
                resetCache();
                return true;
            }
            return false;
        }

        @Override
        public boolean remove(Object o) {
            if (_map.keySet().remove(o)) {
                resetCache();
                return true;
            }
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends K> c) {
            if (_map.keySet().addAll(c)) {
                resetCache();
                return true;
            }
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            if (_map.keySet().retainAll(c)) {
                resetCache();
                return true;
            }
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            if (_map.keySet().removeAll(c)) {
                resetCache();
                return true;
            }
            return false;
        }

        @Override
        protected Collection<K> getCollection() {
            return _map.keySet();
        }

        private final class StackedKeySetIterator extends AbstractIteratorWrapper<K> {

            Iterator<K> iterator = _map.keySet().iterator();

            @Override
            public void remove() {
                iterator.remove();
                resetCache();
            }

            @Override
            protected Iterator<K> getIterator() {
                return iterator;
            }
        }
    }
}
