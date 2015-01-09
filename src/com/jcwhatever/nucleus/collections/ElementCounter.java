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

import com.jcwhatever.nucleus.utils.PreCon;

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
 * <p>Depending on the removal policy, when the items count reaches 0 the item is or isn't removed.</p>
 *
 * @param <E>  The element type.
 */
public class ElementCounter<E> implements Iterable<E> {

    private Map<E, Counter> _countMap;
    private RemovalPolicy _removalPolicy;

    // cache to quickly increment a value without having to look it up in the type count map.
    private transient E _current;
    private transient Counter _currentCounter;

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
    public ElementCounter(RemovalPolicy removalPolicy) {
        _removalPolicy = removalPolicy;
        _countMap = new HashMap<>(25);
    }

    /**
     * Constructor.
     *
     * @param removalPolicy Specify how items are handled when their count reaches 0.
     */
    public ElementCounter(RemovalPolicy removalPolicy, int size) {
        _removalPolicy = removalPolicy;
        _countMap = new HashMap<>(size);
    }

    /**
     * Add a collection of items to the counter
     *
     * @param collection  The collection to add
     */
    public void addAll(Collection<E> collection) {
        for (E item : collection)
            add(item);
    }

    /**
     * Add an item to the counter.
     *
     * @param element  The item to add.
     *
     * @return  The new count for the item.
     */
    public int add(E element) {
        PreCon.notNull(element);

        Counter counter;

        counter = _current != null && _current.equals(element)
                ? _currentCounter
                : _countMap.get(element);

        // establish baseline value
        if (counter == null) {
            counter = new Counter(1);
            _countMap.put(element, counter);
        }
        // increment value
        else {
            counter.increment(1);
        }

        // place value in cache
        _current = element;
        _currentCounter = counter;

        return counter.count();
    }

    /**
     * Subtract a collection of items from the counter
     *
     * @param collection  The collection to subtract.
     */
    public void subtractAll(Collection<E> collection) {
        for (E item : collection)
            subtract(item);
    }

    /**
     * Subtract the count from an item.
     *
     * @param element  The item to subtract.
     *
     * @return  The new count for the item.
     */
    public int subtract(E element) {
        PreCon.notNull(element);

        Counter counter;

        counter = _current != null && _current.equals(element)
                ? _currentCounter
                : _countMap.get(element);

        // Check if item is in counter
        if (counter == null) {
            if (_removalPolicy == RemovalPolicy.REMOVE) {
                return 0;
            }
            else {
                counter = new Counter(-1);
                _countMap.put(element, counter);
            }
        }
        // decrement count
        else {
            counter.increment(-1);
        }

        // check if the item needs to be removed
        if (_removalPolicy == RemovalPolicy.REMOVE && counter.count() <= 0) {
            _current = null;
            _currentCounter = null;
            _countMap.remove(element);
            return 0;
        }

        // place value in cache
        _current = element;
        _currentCounter = counter;

        return counter.count();
    }

    /**
     * Get the current counter value for the specified item.
     *
     * @param element  The item to get the count value for
     */
    public int getCount(E element) {
        PreCon.notNull(element);

        if (_current != null && _current.equals(element)) {
            return _currentCounter.count();
        }

        Counter counter = _countMap.get(element);

        if (counter == null) {
            return 0;
        }

        return counter.count();
    }

    /**
     * Get the number of elements that are counted.
     */
    public int size() {
        return _countMap.keySet().size();
    }

    /**
     * Determine if an item is in the counter.
     *
     * @param element  The item to check.
     */
    public boolean contains(E element) {
        PreCon.notNull(element);

        return _countMap.keySet().contains(element);
    }


    /**
     * Get a new hash set containing the items that were counted.
     */
    public Set<E> getElements() {

        return new HashSet<E>(_countMap.keySet());
    }

    /**
     * Clear all items and counts.
     */
    public void reset() {
        _countMap.clear();
        _current = null;
        _currentCounter = null;
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
    public Iterator<E> iterator() {

        List<E> types = new ArrayList<E>(_countMap.keySet());
        return types.iterator();
    }

    private class Counter {

        private int _count;

        Counter(int count) {
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
