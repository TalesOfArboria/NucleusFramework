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

import com.jcwhatever.nucleus.collections.ElementCounter.ElementCount;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Counts the number of times an item is added but only holds a single reference to the item.
 *
 * <p>Depending on the removal policy, when the items count reaches 0 the item is or isn't removed.</p>
 *
 * @param <E>  The element type.
 */
public class ElementCounter<E> implements Iterable<ElementCount<E>> {

    private Map<E, ElementCount<E>> _countMap;
    private RemovalPolicy _policy;

    // cache to quickly increment a value without having to look it up in the type count map.
    private transient E _current;
    private transient ElementCount<E> _currentCounter;

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
     * @param removalPolicy  Specify how items are handled when their count reaches 0.
     */
    public ElementCounter(RemovalPolicy removalPolicy) {
        this(removalPolicy, 10);
    }

    /**
     * Constructor.
     *
     * @param removalPolicy  Specify how items are handled when their count reaches 0.
     * @param capacity       The initial capacity of the internal collection.
     */
    public ElementCounter(RemovalPolicy removalPolicy, int capacity) {
        PreCon.notNull(removalPolicy);

        _policy = removalPolicy;
        _countMap = new HashMap<>(capacity);
    }

    /**
     * Constructor.
     *
     * @param removalPolicy  Specify how items are handled when their count reaches 0.
     * @param iterable       The initial elements to count.
     */
    public ElementCounter(RemovalPolicy removalPolicy, Iterable<? extends E> iterable) {
        PreCon.notNull(removalPolicy);
        PreCon.notNull(iterable);

        _policy = removalPolicy;
        _countMap = new HashMap<>(10);

        for (E element : iterable) {
            add(element);
        }
    }

    /**
     * Constructor.
     *
     * @param removalPolicy  Specify how items are handled when their count reaches 0.
     * @param collection     The initial collection of elements to count.
     */
    public ElementCounter(RemovalPolicy removalPolicy, Collection<? extends E> collection) {
        PreCon.notNull(removalPolicy);
        PreCon.notNull(collection);

        _policy = removalPolicy;
        _countMap = new HashMap<>(collection.size());

        for (E element : collection) {
            add(element);
        }
    }

    /**
     * Get the number of elements that are counted.
     */
    public int size() {
        return _countMap.keySet().size();
    }

    /**
     * Determine if an element is in the counter.
     *
     * @param element  The element to check.
     */
    public boolean contains(Object element) {
        PreCon.notNull(element);

        //noinspection SuspiciousMethodCalls
        return _countMap.keySet().contains(element);
    }

    /**
     * Increment elements in the counter.
     *
     * @param iterable  The elements to increment.
     */
    public void addAll(Iterable<? extends E> iterable) {
        addAll(iterable, 1);
    }

    /**
     * Increment elements in the counter.
     *
     * @param iterable  The elements to increment.
     * @param amount    The amount to add to each element.
     */
    public void addAll(Iterable<? extends E> iterable, int amount) {
        PreCon.notNull(iterable);

        for (E element : iterable)
            modifyCount(element, amount);
    }

    /**
     * Increment an element in the counter.
     *
     * @param element  The element to increment.
     *
     * @return  The new count for the element.
     */
    public int add(E element) {
        return modifyCount(element, 1);
    }

    /**
     * Increment an element in the counter.
     *
     * @param element  The element to increment.
     * @param amount   The amount to increment.
     *
     * @return  The new count for the element.
     */
    public int add(E element, int amount) {
        PreCon.notNull(element);

        return modifyCount(element, amount);
    }

    /**
     * Add from the specified {@code ElementCounter}'s counts
     * to the current {@code ElementCounter}.
     *
     * @param counter  The counter.
     *
     * @return  Self for chaining.
     */
    public ElementCounter<E> add(ElementCounter<E> counter) {
        PreCon.notNull(counter);

        for (ElementCount<E> element : counter) {
            modifyCount(element.getElement(), element.getCount());
        }

        return this;
    }

    /**
     * Decrement elements in the counter.
     *
     * @param iterable  The iterable collection of elements to subtract.
     */
    public void subtractAll(Iterable<? extends E> iterable) {
        subtractAll(iterable, -1);
    }

    /**
     * Decrement elements in the counter.
     *
     * @param iterable  The iterable collection of elements to subtract.
     * @param amount    The amount to subtract from each element.
     */
    public void subtractAll(Iterable<? extends E> iterable, int amount) {
        PreCon.notNull(iterable);

        for (E element : iterable) {
            if (element == null)
                continue;

            modifyCount(element, -amount);
        }
    }

    /**
     * Decrement an elements count.
     *
     * @param element  The element to subtract.
     *
     * @return  The new count for the element.
     */
    public int subtract(E element) {
        PreCon.notNull(element);

        return modifyCount(element, -1);
    }

    /**
     * Decrement an elements count.
     *
     * @param element  The element to subtract.
     * @param amount   The amount to subtract.
     *
     * @return  The new count for the element.
     */
    public int subtract(E element, int amount) {
        PreCon.notNull(element);

        return modifyCount(element, -amount);
    }

    /**
     * Subtract from the current {@code ElementCounter} all the items
     * counted by the specified {@code ElementCounter}.
     *
     * @param counter  The counter.
     *
     * @return  Self for chaining.
     */
    public ElementCounter<E> subtract(ElementCounter<E> counter) {
        PreCon.notNull(counter);

        for (ElementCount<E> element : counter) {
            modifyCount(element.getElement(), -element.getCount());
        }

        return this;
    }

    /**
     * Get the current counter value for the specified item.
     *
     * @param element  The item to get the count value for
     */
    public int count(Object element) {
        PreCon.notNull(element);

        if (_current != null && _current.equals(element)) {
            return _currentCounter.count;
        }

        //noinspection SuspiciousMethodCalls
        ElementCount counter = _countMap.get(element);

        if (counter == null) {
            return 0;
        }

        return counter.count;
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
    public Iterator<ElementCount<E>> iterator() {

        return _countMap.values().iterator();
    }

    /**
     * Modify the count of an element
     */
    private int modifyCount(E element, int amount) {
        PreCon.notNull(element);

        ElementCount<E> counter;

        counter = _current != null && _current.equals(element)
                ? _currentCounter
                : _countMap.get(element);

        // Check if item is in counter
        if (counter == null) {
            if (_policy == RemovalPolicy.REMOVE) {
                return 0;
            }
            else {
                counter = new ElementCount<>(_policy, element, amount);
                _countMap.put(element, counter);
            }
        }
        // modify count
        else {
            counter.increment(amount);
        }

        // check if the item needs to be removed
        if (_policy == RemovalPolicy.REMOVE && counter.count <= 0) {
            _current = null;
            _currentCounter = null;
            _countMap.remove(element);
            return 0;
        }

        // place value in cache
        _current = element;
        _currentCounter = counter;

        return counter.count;
    }

    /**
     * Contains count information for a single element.
     *
     * @param <E>  The element type.
     */
    public static class ElementCount<E> {

        final RemovalPolicy policy;
        final E element;
        int count;

        ElementCount(RemovalPolicy policy, E element, int count) {
            this.policy = policy;
            this.element = element;
            this.count = policy == RemovalPolicy.BOTTOM_OUT
                    ? Math.max(0, count)
                    : count;
        }

        /**
         * Get the elements count.
         */
        public int getCount() {
            return count;
        }

        /**
         * Get the element.
         */
        public E getElement() {
            return element;
        }

        void increment(int amount) {
            if (policy == RemovalPolicy.BOTTOM_OUT)
                count = Math.max(0, count + amount);
            else
                count += amount;
        }
    }
}
