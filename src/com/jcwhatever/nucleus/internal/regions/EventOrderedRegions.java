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

package com.jcwhatever.nucleus.internal.regions;

import com.jcwhatever.nucleus.regions.IRegion;
import com.jcwhatever.nucleus.regions.options.RegionEventPriority;
import com.jcwhatever.nucleus.regions.options.RegionEventPriority.PriorityType;
import com.jcwhatever.nucleus.utils.PreCon;

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
 * {@link java.util.HashSet#contains} method performance.</p>
 *
 * <p>If only the {@link java.util.Set} implemented methods are used, the collection
 * performance should remain nearly the same as a {@link java.util.HashSet}.</p>
 */
class EventOrderedRegions<E extends IRegion> implements Set<E> {

    // primary hash set
    private final Set<E> _hashSet;

    // enter event sorted list cache
    private List<E> _enterList;
    private boolean _isEnterSorted;

    // leave event sorted list cache
    private List<E> _leaveList;
    private boolean _isLeaveSorted;

    /**
     * Constructor.
     */
    public EventOrderedRegions() {
        this(10);
    }

    /**
     * Constructor.
     *
     * @param size  The initial capacity.
     */
    public EventOrderedRegions(int size) {
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
                        if (_leaveList != null) {
                            _leaveList.remove(_current);
                        }
                        break;

                    // leave list removed
                    case LEAVE:
                        // now remove from enter list
                        if (_enterList != null) {
                            _enterList.remove(_current);
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

            if (_enterList != null) {
                _enterList.add(entry);
            }

            if (_leaveList != null) {
                _leaveList.add(entry);
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

            if (_enterList != null) {
                //noinspection SuspiciousMethodCalls
                _enterList.remove(obj);
            }

            if (_leaveList != null) {
                //noinspection SuspiciousMethodCalls
                _leaveList.remove(obj);
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
            isModified = add(entry) || isModified;
        }
        return isModified;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        PreCon.notNull(collection);

        if (_hashSet.retainAll(collection)) {
            if (_enterList != null) {
                _enterList.retainAll(collection);
            }

            if (_leaveList != null) {
                _leaveList.retainAll(collection);
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
            isModified = remove(entry) || isModified;
        }
        return isModified;
    }

    @Override
    public void clear() {
        _hashSet.clear();

        if (_enterList != null)
            _enterList.clear();

        if (_leaveList != null)
            _leaveList.clear();
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
                RegionEventPriority p1 = o1.getEventPriority(priorityType);
                RegionEventPriority p2 = o2.getEventPriority(priorityType);

                return Integer.compare(p1.getSortOrder(), p2.getSortOrder());
            }
        });
    }

    private List<E> getList(PriorityType priorityType) {

        switch (priorityType) {

            case ENTER:
                if (_enterList == null) {
                    _enterList = new ArrayList<>(_hashSet);
                }
                return _enterList;

            case LEAVE:
                if (_leaveList == null) {
                    _leaveList = new ArrayList<>(_hashSet);
                }
                return _leaveList;

            default:
                throw new AssertionError();
        }
    }
}
