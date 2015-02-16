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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.annotation.Nullable;

/**
 * A collection with weak references to its elements.
 * Uses an {@link java.util.ArrayList} internally.
 *
 * <p>Automatically removes items that have been garbage collected.</p>
 *
 * @param <E> List element type
 */
public class WeakCollection<E> implements Collection<E> {

    private final transient List<ListEntry<E>> _list;

    /**
     * Constructor.
     */
    public WeakCollection() {
        this(10);
    }

    /**
     * Constructor.
     *
     * @param size  The initial capacity.
     */
    public WeakCollection(int size) {
        _list = new ArrayList<>(size);
    }

    /**
     * The number of elements in the collection.
     */
    @Override
    public int size() {

        Iterator<ListEntry<E>> iterator = _list.iterator();
        int result = 0;

        while (iterator.hasNext()) {
            ListEntry<E> entry = iterator.next();

            E entryRef = entry.get();
            if (entryRef == null) {
                iterator.remove();
            } else {
                result++;
            }
        }

        return result;
    }

    /**
     * Determine if the collection is empty.
     */
    @Override
    public boolean isEmpty() {

        Iterator<ListEntry<E>> iterator = _list.iterator();

        while (iterator.hasNext()) {
            ListEntry<E> entry = iterator.next();

            E entryRef = entry.get();
            if (entryRef == null) {
                iterator.remove();
            } else {
                return false;
            }
        }

        return true;

    }

    /**
     * Clear all elements.
     */
    @Override
    public void clear() {
        _list.clear();
    }

    /**
     * Determine if the collection contains
     * the specified object.
     *
     * @param object  The object.
     */
    @Override
    public boolean contains(Object object) {
        PreCon.notNull(object);

        Iterator<ListEntry<E>> iterator = _list.iterator();

        while (iterator.hasNext()) {
            ListEntry<E> entry = iterator.next();

            E entryRef = entry.get();
            if (entryRef == null) {
                iterator.remove();
            } else if (entryRef.equals(object)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Convert the collection to an array.
     */
    @Override
    public Object[] toArray() {

        List<E> list = toList();

        Object[] items = new Object[list.size()];

        for (int i=0; i < items.length; i++) {
            items[i] = list.get(i);
        }

        return items;
    }

    /**
     * Copy the collection elements to an array.
     */
    @Override
    public <T> T[] toArray(T[] array) {
        PreCon.notNull(array);

        //noinspection SuspiciousToArrayCall
        return toList().toArray(array);
    }

    /**
     * Add an element
     */
    @Override
    public boolean add(E element) {
        PreCon.notNull(element);

        return _list.add(new ListEntry<E>(element));
    }

    /**
     * Remove an element.
     */
    @Override
    public boolean remove(Object object) {
        PreCon.notNull(object);

        Iterator<ListEntry<E>> iterator = _list.iterator();

        while (iterator.hasNext()) {
            ListEntry<E> entry = iterator.next();

            E entryRef = entry.get();
            if (entryRef == null) {
                iterator.remove();
                continue;
            }

            if (entryRef.equals(object)) {
                iterator.remove();
                return true;
            }
        }

        return false;
    }

    /**
     * Determine if all items in the specified collection
     * are present in the {@link WeakList}.
     */
    @Override
    public boolean containsAll(Collection<?> collection) {
        PreCon.notNull(collection);

        for (Object item : collection) {
            if (!contains(item))
                return false;
        }
        return true;
    }

    /**
     * Add all items from a collection.
     */
    @Override
    public boolean addAll(Collection<? extends E> collection) {
        PreCon.notNull(collection);

        for (E entry : collection)
            add(entry);
        return true;
    }

    /**
     * Remove all items that are present in the
     * specified collection.
     */
    @Override
    public boolean removeAll(Collection<?> collection) {
        PreCon.notNull(collection);

        Iterator<ListEntry<E>> iterator = _list.iterator();

        boolean isChanged = false;

        while (iterator.hasNext()) {
            ListEntry<E> entry = iterator.next();

            E entryRef = entry.get();
            if (entryRef == null) {
                iterator.remove();
                continue;
            }

            if (collection.contains(entryRef)) {
                iterator.remove();
                isChanged = true;
            }

        }

        return isChanged;
    }

    /**
     * Retain only the items present in the specified
     * collection.
     */
    @Override
    public boolean retainAll(Collection<?> collection) {
        PreCon.notNull(collection);

        Iterator<ListEntry<E>> iterator = _list.iterator();

        boolean isChanged = false;

        while (iterator.hasNext()) {
            ListEntry<E> entry = iterator.next();

            E entryRef = entry.get();
            if (entryRef == null) {
                iterator.remove();
                continue;
            }

            if (!collection.contains(entryRef)) {
                iterator.remove();
                isChanged = true;
            }
        }

        return isChanged;
    }

    /**
     * Copy the collection elements into a {@link List}.
     */
    public List<E> toList() {

        List<E> result = new ArrayList<>(_list.size());

        Iterator<ListEntry<E>> iterator = _list.iterator();

        while (iterator.hasNext()) {
            ListEntry<E> entry = iterator.next();

            E entryRef = entry.get();
            if (entryRef == null) {
                iterator.remove();
                continue;
            }
            result.add(entryRef);
        }

        return result;
    }

    /**
     * Get an element iterator.
     */
    @Override
    public Iterator<E> iterator() {
        return new WeakIterator();
    }

    private static class ListEntry<T> extends WeakReference<T> {

        private int hash;

        public ListEntry(T referent) {
            super(referent);

            hash = referent.hashCode();
        }

        @Override
        public int hashCode() {
            T entry = get();
            return entry == null ? hash : entry.hashCode();
        }

        @Override
        public boolean equals(Object object) {

            if (object == null)
                return false;

            T entry = get();
            if (entry == null)
                return false;

            if (object instanceof ListEntry) {
                return entry.equals(((ListEntry)object).get()) || object == this;
            }

            return entry.equals(object);
        }
    }

    private class WeakIterator implements Iterator<E> {

        ListIterator<ListEntry<E>> iterator = _list.listIterator(0);
        E currentRef;

        @Override
        public boolean hasNext() {

            if (!iterator.hasNext())
                return false;

            ListEntry<E> entry = iterator.next();
            currentRef = entry.get();

            if (currentRef == null)
                return false;

            iterator.previous();
            return true;
        }

        @Override
        @Nullable
        public E next() {
            ListEntry<E> entry = iterator.next();
            return currentRef = entry.get();
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }
}
