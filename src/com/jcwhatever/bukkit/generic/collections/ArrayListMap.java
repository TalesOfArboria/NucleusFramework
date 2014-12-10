/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

package com.jcwhatever.bukkit.generic.collections;

import com.jcwhatever.bukkit.generic.utils.PreCon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * A hash map that uses array lists to store values.
 *
 * @param <K>  Key type
 * @param <V>  Value type
 */
public class ArrayListMap<K, V> implements Map<K, V> {

    protected Map<K, List<V>> _map;

    /**
     * Constructor.
     */
    public ArrayListMap() {
        _map = new HashMap<>(10);
    }

    /**
     * Constructor.
     *
     * @param size  The initial size.
     */
    public ArrayListMap(int size) {
        PreCon.positiveNumber(size);

        _map = new HashMap<>(size);
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

        for (List<V> list : _map.values()) {

            //noinspection SuspiciousMethodCalls
            if (list.contains(value))
                return true;
        }
        return false;
    }

    /**
     * Determine if the list represented by the specified
     * key contains the specified value.
     *
     * @param key  The key.
     */
    public boolean containsValue(Object key, V value) {
        PreCon.notNull(key);
        PreCon.notNull(value);

        //noinspection SuspiciousMethodCalls
        List<V> set = _map.get(key);
        return set != null && set.contains(value);
    }

    /**
     * Unsupported.
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    /**
     * Get a value using the specified key.
     *
     *  <p>The value returned is the last value in the list.</p>
     *
     * @param key  The key to check.
     */
    @Override
    @Nullable
    public V get(Object key) {
        PreCon.notNull(key);

        List<V> list = _map.get(key);
        if (list != null && !list.isEmpty()) {
            return list.get(list.size() - 1);
        }

        return null;
    }

    /**
     * Get all values associated with the specified key.
     *
     * @param key  The key to check.
     */
    public List<V> getAll(Object key) {
        PreCon.notNull(key);

        //noinspection SuspiciousMethodCalls
        List<V> list = _map.get(key);
        if (list == null) {
            return new ArrayList<>(0);
        }

        return list;
    }

    /**
     * Get the maps keys.
     */
    @Override
    public Set<K> keySet() {
        return _map.keySet();
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

        List<V> list = _map.get(key);
        if (list == null) {
            list = new ArrayList<>(10);
            _map.put(key, list);
        }

        list.add(value);

        return value;
    }

    /**
     * Unsupported.
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        throw new UnsupportedOperationException();
    }

    /**
     * All all values in a collection to the specified
     * key.
     *
     * @param key     The key to use.
     * @param values  The values to add.
     */
    public void putAll(K key, Collection<V> values) {
        PreCon.notNull(key);
        PreCon.notNull(values);

        if (values.isEmpty())
            return;

        List<V> list = _map.get(key);
        if (list == null) {
            list = new ArrayList<>(10);
            _map.put(key, list);
        }

        list.addAll(values);
    }

    /**
     * Remove value by key. Removes the last value from the internal list
     * represented by the key.
     *
     * @param key  The key.
     *
     * @return Returns the removed value, if any.
     */
    @Override
    @Nullable
    public V remove(Object key) {
        PreCon.notNull(key);

        //noinspection SuspiciousMethodCalls
        List<V> list = _map.get(key);
        if (list == null) {
            return null;
        }

        if (list.isEmpty()) {
            _map.remove(key);
            return null;
        }

        int index = list.size() - 1;
        V removed = list.get(index);
        list.remove(index);

        return removed;
    }

    /**
     * Remove a value from the set associated with the key.
     *
     * @param key    The key.
     * @param value  The value.
     *
     * @return  True if the collection is modified.
     */
    public boolean removeValue(Object key, V value) {
        PreCon.notNull(key);
        PreCon.notNull(value);

        //noinspection SuspiciousMethodCalls
        List<V> list = _map.get(key);
        if (list == null) {
            return false;
        }

        if (list.remove(value)) {
            if (list.isEmpty()) {
                //noinspection SuspiciousMethodCalls
                _map.remove(key);
            }
            return true;
        }

        return false;
    }

    /**
     * Remove a value from all lists.
     *
     * @param value  The value.
     *
     * @return  True if the collection is modified.
     */
    public boolean removeValue(V value) {
        PreCon.notNull(value);

        boolean isModified = false;

        List<Entry<K, List<V>>> entries = new ArrayList<>(_map.entrySet());

        for (Entry<K, List<V>> entry : entries) {
            isModified = isModified || entry.getValue().remove(value);
            if (entry.getValue().isEmpty()) {
                _map.remove(entry.getKey());
            }
        }

        return isModified;
    }

    /**
     * Remove all value of the specified key.
     *
     * @param key  The key.
     *
     * @return Returns the removed set, if any.
     */
    public List<V> removeAll(Object key) {
        PreCon.notNull(key);

        //noinspection SuspiciousMethodCalls
        List<V> list = _map.remove(key);
        if (list == null) {
            return new ArrayList<>(0);
        }

        return list;
    }

    /**
     * Get the number of entries in the map.
     * Or in other terms the number of keys, or
     * the number of stacks.
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
        List<V> list = _map.get(key);
        if (list == null || list.isEmpty()) {
            return 0;
        }

        return list.size();
    }

    /**
     * Get all values in the map.
     */
    @Override
    public Collection<V> values() {
        Collection<List<V>> values = _map.values();
        Set<V> results = new HashSet<V>(values.size());

        for (List<V> set : values) {
            results.addAll(set);
        }

        return results;
    }
}

