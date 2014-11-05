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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A hash map that uses timed hash sets to store values.
 * Each item has its own lifespan. When an items lifespan ends,
 * it is removed from the hash set. If all items in a set are removed,
 * the map entry is removed.
 *
 * <p>If a duplicate item is added to a set, the items lifespan is reset, in addition to normal
 * hash set operations.</p>
 *
 * @param <K>  Key type
 * @param <V>  Value type
 */
public class TimedSetMap<K, V> extends SetMap<K, V> {

    private int _defaultLifespan = 20;
    private List<CollectionEmptyAction<TimedSetMap<K, V>>> _onEmpty = new ArrayList<>(5);

    /**
     * Constructor. Default lifespan is 1 second.
     */
    public TimedSetMap() {
        super();
    }

    /**
     * Constructor. Default lifespan is 1 second.
     *
     * @param size  The initial size.
     */
    public TimedSetMap(int size) {
        super(size);
    }

    /**
     * Constructor.
     *
     * @param size             The initial size.
     * @param defaultLifespan  The default lifespan of a value in ticks.
     */
    public TimedSetMap(int size, int defaultLifespan) {
        super(size);
        PreCon.positiveNumber(defaultLifespan);

        _defaultLifespan = defaultLifespan;
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

        Set<V> set = _map.get(key);
        if (set == null) {
            set = newTimedSet(key);
            _map.put(key, set);
        }

        set.add(value);

        return value;
    }

    /**
     * Put a value into the map.
     *
     * @param key       The key to use.
     * @param value     The value to add.
     * @param lifespan  The lifespan of the value in ticks.
     */
    public V put(K key, V value, int lifespan) {
        PreCon.notNull(key);
        PreCon.notNull(value);
        PreCon.notNull(lifespan);

        Set<V> set = _map.get(key);
        if (set == null) {
            set = newTimedSet(key);
            _map.put(key, set);
        }

        ((TimedSet<V>)set).add(value, lifespan);

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
     * Remove value by key. Removes a value from the internal set
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

        Set<V> set = _map.get(key);
        if (set == null) {
            return null;
        }

        if (set.isEmpty()) {
            _map.remove(key);
            onEmpty();
            return null;
        }

        V removed = set.iterator().next();
        set.remove(removed);

        return removed;
    }

    /**
     * Remove a value from the set associated with the key.
     *
     * @param key  The key.
     */
    @Override
    public boolean removeValue(Object key, V value) {
        PreCon.notNull(key);
        PreCon.notNull(value);

        Set<V> set = _map.get(key);
        if (set == null) {
            return false;
        }

        if (set.isEmpty()) {
            _map.remove(key);
            onEmpty();
            return false;
        }

        return set.remove(value);
    }

    /**
     * Remove all value of the specified key.
     *
     * @param key  The key.
     *
     * @return Returns the removed set, if any.
     */
    @Override
    @Nullable
    public Set<V> removeAll(Object key) {
        PreCon.notNull(key);

        Set<V> set = _map.remove(key);
        if (set == null) {
            return null;
        }

        onEmpty();
        return set;
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
    @Override
    public int keySize(Object key) {
        PreCon.notNull(key);

        Set<V> set = _map.get(key);
        if (set == null || set.isEmpty()) {
            return 0;
        }

        return set.size();
    }

    /**
     * Get all values in the map.
     */
    @Override
    public Collection<V> values() {
        Collection<Set<V>> values = _map.values();
        Set<V> results = new HashSet<V>(values.size() * 10);

        for (Set<V> set : values) {
            results.addAll(set);
        }

        return results;
    }

    /**
     * Add a handler to be called whenever the collection becomes empty.
     *
     * @param action  The handler to call
     */
    public void addOnCollectionEmpty(CollectionEmptyAction<TimedSetMap<K, V>> action) {
        PreCon.notNull(action);

        _onEmpty.add(action);
    }

    /**
     * Remove a handler.
     *
     * @param action  The handler to remove.
     */
    public void removeOnCollectionEmpty(CollectionEmptyAction<TimedSetMap<K, V>> action) {
        PreCon.notNull(action);

        _onEmpty.remove(action);
    }

    private void onEmpty() {
        if (!isEmpty() || _onEmpty.isEmpty())
            return;

        for (CollectionEmptyAction<TimedSetMap<K, V>> action : _onEmpty) {
            action.onEmpty(this);
        }
    }

    private TimedSet<V> newTimedSet(final K key) {
        TimedSet<V> set = new TimedSet<>(15, _defaultLifespan);
        set.addOnCollectionEmpty(new CollectionEmptyAction<TimedSet<V>>() {

            @Override
            public void onEmpty(TimedSet<V> emptyCollection) {
                remove(key);
            }
        });

        return set;
    }
}
