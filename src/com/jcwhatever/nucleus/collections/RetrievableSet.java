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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * A {@code Set} that allows retrieving the items without iteration.
 *
 * <p>Implements a {@code HashMap} internally with both key and value
 * set to the same object.</p>
 *
 * <p>Useful for retrieving items from a collection using a substitute object
 * while maintaining the performance of a {@code HashMap} or {@code HashSet} in
 * most cases.</p>
 */
public class RetrievableSet<T> implements Set<T> {

    private final Map<T, T> _map;

    /**
     * Constructor.
     */
    public RetrievableSet() {
        this(10);
    }

    /**
     * Constructor.
     *
     * @param size  The initial collection capacity.
     */
    public RetrievableSet(int size) {
        _map = new HashMap<>(size);
    }

    /**
     * Constructor.
     *
     * @param collection  The initial collection.
     */
    public RetrievableSet(Collection<? extends T> collection) {
        this(collection.size() + 5);

        for (T element : collection) {
            _map.put(element, element);
        }
    }

    /**
     * Retrieve an element.
     *
     * @param element  The element or substitute element whose hash and
     *                 equals point to the object to retrieve.
     *
     * @return  The element if found or null.
     */
    @Nullable
    public T retrieve(Object element) {
        //noinspection SuspiciousMethodCalls
        return _map.get(element);
    }

    /**
     * Remove and retrieve an element.
     *
     * @param element  The element or substitute element whose hash and
     *                 equals point to the object to retrieve.
     *
     * @return  The element if found or null.
     */
    public T removeRetrieve(Object element) {
        //noinspection SuspiciousMethodCalls
        return _map.remove(element);
    }

    /**
     * Remove all items in the specified collection and
     * return the removed items as a new {@code RetrievableSet}.
     *
     * @param collection  The collection.
     */
    public RetrievableSet<T> removeAllRetrieve(Collection<?> collection) {

        RetrievableSet<T> result = new RetrievableSet<>(collection.size());

        for (Object o : collection) {

            //noinspection SuspiciousMethodCalls
            T removed = _map.remove(o);

            if (removed != null) {
                result.add(removed);
            }
        }
        return result;
    }

    /**
     * Retain all items in the specified collection and
     * return the removed items as a new {@code RetrievableSet}.
     *
     * @param collection  The collection.
     */
    public RetrievableSet<T> retainAllRetrieve(Collection<?> collection) {

        RetrievableSet<T> result = new RetrievableSet<>(collection.size());

        for (Object o : collection) {

            //noinspection SuspiciousMethodCalls
            T element = _map.get(o);

            if (element != null) {
                result.add(element);
            }
        }

        //noinspection SuspiciousMethodCalls
        result.removeAll(collection);
        _map.keySet().retainAll(collection);

        return result;
    }

    @Override
    public int size() {
        return _map.size();
    }

    @Override
    public boolean isEmpty() {
        return _map.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        //noinspection SuspiciousMethodCalls
        return _map.containsKey(o);
    }

    @Override
    public Iterator<T> iterator() {
        return _map.keySet().iterator();
    }

    @Override
    public Object[] toArray() {
        return _map.keySet().toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        //noinspection SuspiciousToArrayCall
        return _map.keySet().toArray(a);
    }

    @Override
    public boolean add(T t) {
        _map.put(t, t);
        return true;
    }

    /**
     * Remove an element.
     */
    @Override
    public boolean remove(Object o) {
        return _map.remove(o) != null;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return _map.keySet().containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        boolean isChanged = false;
        for (T element : collection) {
            isChanged = add(element) || isChanged;
        }
        return isChanged;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return _map.keySet().retainAll(collection);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return _map.keySet().removeAll(collection);
    }

    @Override
    public void clear() {
        _map.clear();
    }
}
