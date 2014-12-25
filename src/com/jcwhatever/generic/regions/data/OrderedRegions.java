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

package com.jcwhatever.generic.regions.data;

import com.jcwhatever.generic.regions.IRegionComparable;
import com.jcwhatever.generic.regions.Region.PriorityType;
import com.jcwhatever.generic.regions.Region.RegionPriority;
import com.jcwhatever.generic.utils.PreCon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Ordering collection container for regions.
 *
 * <p>Allows retrieving regions sorted by priority while maintaining
 * {@code HashSet} {@code contains} method performance.</p>
 *
 * <p>If only the {@code Set} implemented methods are used, the collection
 * performance should remain nearly the same as a {@code HashSet}.</p>
 */
public class OrderedRegions<E extends IRegionComparable> implements Set<E> {

    private final Set<E> _hashSet;
    private List<E> _enterlist;
    private List<E> _leavelist;
    private boolean _isEnterSorted;
    private boolean _isLeaveSorted;

    /**
     * Constructor.
     */
    public OrderedRegions() {
        this(10);
    }

    /**
     * Constructor.
     *
     * @param size  The initial capacity.
     */
    public OrderedRegions(int size) {
        _hashSet = new HashSet<>(size);
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
        //noinspection SuspiciousMethodCalls
        return _hashSet.contains(obj);
    }

    @Override
    public Iterator<E> iterator() {
        return _hashSet.iterator();
    }

    /**
     * Get an iterator whose elements are sorted by the
     * specified priority.
     *
     * @param priorityType  The sorting priority type.
     */
    public Iterator<E> iterator(final PriorityType priorityType) {
        PreCon.notNull(priorityType);

        sort(priorityType);
        return new Iterator<E>() {

            private Iterator<E> _iterator = getList(priorityType).iterator();
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

                switch (priorityType) {

                    // enter list removed
                    case ENTER:
                        // now remove from leave list
                        if (_leavelist != null) {
                            _leavelist.remove(_current);
                        }
                        break;

                    // leave list removed
                    case LEAVE:
                        // now remove from enter list
                        if (_enterlist != null) {
                            _enterlist.remove(_current);
                        }
                        break;
                    default:
                        throw new AssertionError();
                }

                _hashSet.remove(_current);
            }
        };
    }

    @Override
    public Object[] toArray() {
        return _hashSet.toArray();
    }

    /**
     * Get an object array from the collection whose
     * elements are sorted by the specified priority type.
     *
     * @param priorityType  The priority type.
     */
    public Object[] toArray(PriorityType priorityType) {
        PreCon.notNull(priorityType);

        sort(priorityType);

        return getList(priorityType).toArray();
    }

    @Override
    public <T> T[] toArray(T[] array) {
        //noinspection SuspiciousToArrayCall
        return _hashSet.toArray(array);
    }

    /**
     * Get an array from the collection whose elements
     * are sorted by the specified priority type.
     *
     * @param priorityType  The priority type.
     * @param array         The array to place the elements into.
     *
     * @param <T>  The array type.
     */
    public <T> T[] toArray(PriorityType priorityType, T[] array) {
        PreCon.notNull(priorityType);
        PreCon.notNull(array);

        sort(priorityType);

        //noinspection SuspiciousToArrayCall
        return getList(priorityType).toArray(array);
    }

    @Override
    public boolean add(E entry) {
        PreCon.notNull(entry);

        if (_hashSet.add(entry)) {

            if (_enterlist != null) {
                _enterlist.add(entry);
            }

            if (_leavelist != null) {
                _leavelist.add(entry);
            }

            _isEnterSorted = false;
            _isLeaveSorted = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object obj) {
        PreCon.notNull(obj);

        //noinspection SuspiciousMethodCalls
        if (_hashSet.remove(obj)) {

            if (_enterlist != null) {
                //noinspection SuspiciousMethodCalls
                _enterlist.remove(obj);
            }

            if (_leavelist != null) {
                //noinspection SuspiciousMethodCalls
                _leavelist.remove(obj);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        PreCon.notNull(collection);

        return _hashSet.containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        PreCon.notNull(collection);

        boolean isModified = false;
        for (E entry : collection) {
            isModified = isModified || add(entry);
        }
        return isModified;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        PreCon.notNull(collection);

        if (_hashSet.retainAll(collection)) {
            if (_enterlist != null) {
                _enterlist.retainAll(collection);
            }

            if (_leavelist != null) {
                _leavelist.retainAll(collection);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        PreCon.notNull(collection);

        boolean isModified = false;
        for (Object entry : collection) {
            isModified = isModified || remove(entry);
        }
        return isModified;
    }

    @Override
    public void clear() {
        _hashSet.clear();

        if (_enterlist != null)
            _enterlist.clear();

        if (_leavelist != null)
            _leavelist.clear();
    }

    /**
     * Sort the collection.
     *
     * <p>Will not sort if the collection does not need to be sorted.</p>
     */
    public void sort(final PriorityType priorityType) {
        PreCon.notNull(priorityType);

        switch (priorityType) {

            case ENTER:
                if (_isEnterSorted)
                    return;
                _isEnterSorted = true;
                break;

            case LEAVE:
                if (_isLeaveSorted)
                    return;
                _isLeaveSorted = true;
                break;

            default:
                throw new AssertionError();
        }

        List<E> list = getList(priorityType);

        Collections.sort(list, new Comparator<E>() {
            @Override
            public int compare(E o1, E o2) {
                RegionPriority p1 = o1.getPriority(priorityType);
                RegionPriority p2 = o2.getPriority(priorityType);

                return Integer.compare(p1.getSortOrder(), p2.getSortOrder());
            }
        });
    }

    private List<E> getList(PriorityType priorityType) {

        switch (priorityType) {

            case ENTER:
                if (_enterlist == null) {
                    _enterlist = new ArrayList<>(_hashSet);
                }
                return _enterlist;

            case LEAVE:
                if (_leavelist == null) {
                    _leavelist = new ArrayList<>(_hashSet);
                }
                return _leavelist;

            default:
                throw new AssertionError();
        }
    }
}
