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

package com.jcwhatever.bukkit.generic.collections.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Designed to give the sorted functionality of a {@code TreeSet}
 * but primarily keep the {@code contains} method performance of
 * a {@code HashSet}.
 */
public class OrderedHashSet<E extends Comparable<E>> implements Set<E> {

    private Set<E> _hashSet;
    private List<E> _list;
    private boolean _isSorted;

    public OrderedHashSet(int size) {
        _hashSet = new HashSet<>(size);
        _list = new ArrayList<>(size);
    }

    @Override
    public int size() {
        return _hashSet.size();
    }

    @Override
    public boolean isEmpty() {
        return _hashSet.isEmpty();
    }

    @Override
    public boolean contains(Object obj) {
        return _hashSet.contains(obj);
    }

    @Override
    public Iterator<E> iterator() {
        sort();
        return new Iterator<E>() {

            private Iterator<E> _iterator = _list.iterator();
            private E _current;

            @Override
            public boolean hasNext() {
                return _iterator.hasNext();
            }

            @Override
            public E next() {
                return _current = _iterator.next();
            }

            @Override
            public void remove() {
                _iterator.remove();
                _hashSet.remove(_current);
            }
        };
    }

    @Override
    public Object[] toArray() {
        sort();
        return _list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] array) {
        sort();
        //noinspection SuspiciousToArrayCall
        return _list.toArray(array);
    }

    @Override
    public boolean add(E entry) {
        if (_hashSet.add(entry)) {
            _list.add(entry);
            _isSorted = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object obj) {
        if (_hashSet.remove(obj)) {
            _list.remove(obj);
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return _hashSet.containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        boolean isModified = false;
        for (E entry : collection) {
            isModified = isModified || add(entry);
        }
        return isModified;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        _hashSet.retainAll(collection);
        return _list.retainAll(collection);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean isModified = false;
        for (Object entry : collection) {
            isModified = isModified || remove(entry);
        }
        return isModified;
    }

    @Override
    public void clear() {
        _hashSet.clear();
        _list.clear();
    }

    /**
     * Sort the collection.
     *
     * <p>Will not sort if the collection does not need to be sorted.</p>
     */
    public void sort() {
        if (_isSorted)
            return;

        _isSorted = true;
        Collections.sort(_list);
    }
}
