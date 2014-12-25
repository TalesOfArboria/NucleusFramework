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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Counts the number of times an item is added but only holds a single reference to the item.
 *
 * <p>Depending on the removal policy, when the items count goes below 0 the item is or isn't removed.</p>
 *
 * @param <T>
 */
public class EntryCounter<T> implements Iterable<T> {

    private Map<T, Entry> _countMap;
    private RemovalPolicy _removalPolicy;

    // cache to quickly increment a value without having to look it up in the type count map.
    private transient T _currentItem;
    private transient Entry _currentEntry;

    /**
     * Used to specify if the counter should remove
     * an item when its count reaches 0 or continue counting
     * into negative numbers.
     */
    public enum RemovalPolicy {
        /**
         * Remove item if its count reaches 0.
         */
        REMOVE,
        /**
         * Allow item count to go below 0. The item is not removed.
         */
        KEEP_COUNTING,
        /**
         * Item count never goes below 0. The item is not removed.
         */
        BOTTOM_OUT
    }

    /**
     * Constructor.
     *
     * @param removalPolicy Specify how items are handled when their count reaches 0.
     */
    public EntryCounter(RemovalPolicy removalPolicy) {
        _removalPolicy = removalPolicy;
        _countMap = new HashMap<>(25);
    }

    /**
     * Constructor.
     *
     * @param removalPolicy Specify how items are handled when their count reaches 0.
     */
    public EntryCounter(RemovalPolicy removalPolicy, int size) {
        _removalPolicy = removalPolicy;
        _countMap = new HashMap<>(size);
    }

    /**
     * Add a collection of items to the counter
     *
     * @param items  The collection to add
     */
    public void addAll(Collection<T> items) {
        for (T item : items)
            add(item);
    }

    /**
     * Add an item to the counter.
     *
     * @param item  The item to add.
     *
     * @return  The new count for the item.
     */
    public int add(T item) {
        PreCon.notNull(item);

        Entry entry;

        entry = _currentItem != null && _currentItem.equals(item)
                ? _currentEntry
                : _countMap.get(item);

        // establish baseline value
        if (entry == null) {
            entry = new Entry(1);
            _countMap.put(item, entry);
        }
        // increment value
        else {
            entry.increment(1);
        }

        // place value in cache
        _currentItem = item;
        _currentEntry = entry;

        return entry.count();
    }

    /**
     * Subtract a collection of items from the counter
     *
     * @param items  The collection to subtract.
     */
    public void subtractAll(Collection<T> items) {
        for (T item : items)
            subtract(item);
    }

    /**
     * Subtract the count from an item.
     *
     * @param item  The item to subtract.
     *
     * @return  The new count for the item.
     */
    public int subtract(T item) {
        PreCon.notNull(item);

        Entry entry;

        entry = _currentItem != null && _currentItem.equals(item)
                ? _currentEntry
                : _countMap.get(item);

        // Check if item is in counter
        if (entry == null) {
            if (_removalPolicy == RemovalPolicy.REMOVE) {
                return 0;
            }
            else {
                entry = new Entry(-1);
                _countMap.put(item, entry);
            }
        }
        // decrement count
        else {
            entry.increment(-1);
        }

        // check if the item needs to be removed
        if (_removalPolicy == RemovalPolicy.REMOVE && entry.count() <= 0) {
            _currentItem = null;
            _currentEntry = null;
            _countMap.remove(item);
            return 0;
        }

        // place value in cache
        _currentItem = item;
        _currentEntry = entry;

        return entry.count();
    }

    /**
     * Get the current counter value for the specified item.
     *
     * @param item  The item to get the count value for
     */
    public int getCount(T item) {
        PreCon.notNull(item);

        if (_currentItem != null && _currentItem.equals(item)) {
            return _currentEntry.count();
        }

        Entry entry = _countMap.get(item);

        if (entry == null) {
            return 0;
        }

        return entry.count();
    }

    /**
     * Get the number of types that are counted.
     */
    public int getEntrySize() {
        return _countMap.keySet().size();
    }

    /**
     * Determine if an item is in the counter.
     *
     * @param item  The item to check.
     */
    public boolean contains(T item) {
        PreCon.notNull(item);

        return _countMap.keySet().contains(item);
    }


    /**
     * Get a new hash set containing the items that were counted.
     */
    public Set<T> getEntries() {

        return new HashSet<T>(_countMap.keySet());
    }

    /**
     * Clear all items and counts.
     */
    public void reset() {
        _countMap.clear();
        _currentItem = null;
        _currentEntry = null;
    }

    /**
     * Get an iterator to iterate over the items in the counter.
     *
     * <p>The iterator returned is from a copied list and does not affect
     * the collection.</p>
     *
     * <p>Each item appears only once in the iteration regardless of its count.</p>
     */
    @Override
    public Iterator<T> iterator() {

        List<T> types = new ArrayList<T>(_countMap.keySet());
        return types.iterator();
    }

    private class Entry {

        private int _count;

        Entry(int count) {
            _count = _removalPolicy == RemovalPolicy.BOTTOM_OUT
                    ? Math.max(0, count)
                    : count;
        }

        int count() {
            return _count;
        }

        void increment(int amount) {
            if (_removalPolicy == RemovalPolicy.BOTTOM_OUT)
                _count = Math.max(0, _count + amount);
            else
                _count += amount;
        }
    }

}
