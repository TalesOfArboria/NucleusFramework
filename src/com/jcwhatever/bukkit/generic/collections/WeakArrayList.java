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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.annotation.Nullable;

/**
 * An array list with weak references to its items.
 *
 * <p>Automatically removes items that have been garbage collected.</p>
 *
 * <p>Using index positions should be used with caution. Its possible for an item
 * to get garbage collected after checking the size of the list and before trying to
 * affect an item at a specified index position. Iteration should be used instead of index positions.</p>
 *
 * @param <T> List element type
 */
public class WeakArrayList<T> implements List<T> {

    private List<WeakReference<T>> _references;

    /**
     * Constructor.
     */
    public WeakArrayList() {
        _references = new ArrayList<WeakReference<T>>(10);
    }

    /**
     * Constructor.
     *
     * @param size  The initial capacity.
     */
    public WeakArrayList(int size) {
        PreCon.positiveNumber(size);

        _references = new ArrayList<WeakReference<T>>(size);
    }


    /**
     * Get an item by index position.
     *
     * @param index  The index position
     *
     * @return  The item or null.
     */
    @Override
    @Nullable
    public T get(int index) {
        PreCon.positiveNumber(index);

        if (index >= _references.size())
            return null;

        return _references.get(index).get();
    }

    /**
     * Get the size of the list.
     */
    @Override
    public int size() {
        clean();
        return _references.size();
    }

    /**
     * Add an item.
     *
     * @param item  The item to add.
     */
    @Override
    public boolean add(T item) {
        PreCon.notNull(item);

        return _references.add(new WeakReference<T>(item));
    }

    /**
     * Insert an item at the specified index position.
     *
     * <p>If the index position is greater than the size of the collection,
     * the item is appended to the end of the list.</p>
     *
     * @param index  The index position.
     * @param item   The item to insert.
     */
    @Override
    public void add(int index, T item) {
        PreCon.positiveNumber(index);
        PreCon.notNull(item);

        if (index >= _references.size()) {
            this.add(item);
            return;
        }

        _references.add(index, new WeakReference<T>(item));
    }

    /**
     * Add all items from a collection.
     *
     * @param collection  The collection to add.
     */
    @Override
    public boolean addAll(Collection<? extends T> collection) {
        PreCon.notNull(collection);

        for (T item : collection)
            add(item);
        return true;
    }

    /**
     * Insert all items from a collection at the specified
     * index position.
     *
     * @param index       The index position.
     * @param collection  The collection to insert.
     */
    @Override
    public boolean addAll(int index, Collection<? extends T> collection) {
        PreCon.positiveNumber(index);
        PreCon.notNull(collection);

        for (T item : collection) {
            add(index, item);
            index++;
        }
        return true;
    }

    /**
     * Remove all items.
     */
    @Override
    public void clear() {
        _references.clear();
    }

    /**
     * Determine if the list contains the specified item.
     *
     * @param obj  The item to check.
     */
    @Override
    public boolean contains(Object obj) {
        PreCon.notNull(obj);

        Iterator<WeakReference<T>> wIterator = _references.iterator();

        while (wIterator.hasNext()) {
            WeakReference<T> item = wIterator.next();
            if (item.get() == null) {
                wIterator.remove();
            }
            else {
                T value = item.get();
                if (value != null && value.equals(obj))
                    return true;
            }
        }

        return false;
    }

    /**
     * Determine if the items of a collection are
     * contained within the list.
     *
     * @param collection  The collection to check.
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
     * Get the index position of the specified object.
     *
     * @param obj  The object to check.
     */
    @Override
    public int indexOf(Object obj) {
        PreCon.notNull(obj);

        Iterator<WeakReference<T>> wIterator = _references.iterator();

        int index = 0;

        while (wIterator.hasNext()) {
            WeakReference<T> item = wIterator.next();
            if (item.get() == null) {
                wIterator.remove();
            }
            else {
                T value = item.get();
                if (value != null && value.equals(obj))
                    return index;
                index++;
            }
        }

        return -1;
    }

    /**
     * Determine if the collection is empty.
     */
    @Override
    public boolean isEmpty() {
        return _references.isEmpty();
    }

    /**
     * Get an iterator.
     */
    @Override
    public Iterator<T> iterator() {
        clean();
        return new WeakIterator();
    }

    /**
     * Get the last index of the specified value.
     *
     * @param obj  The value to search for.
     */
    @Override
    public int lastIndexOf(Object obj) {
        PreCon.notNull(obj);

        Iterator<WeakReference<T>> wIterator = _references.iterator();

        int index = 0;
        int lastIndex = -1;

        while (wIterator.hasNext()) {
            WeakReference<T> item = wIterator.next();
            if (item.get() == null) {
                wIterator.remove();
            }
            else {
                T value = item.get();
                if (value != null && value.equals(obj))
                    lastIndex = index;
                index++;
            }
        }

        return lastIndex;
    }

    /**
     * Get a list iterator.
     */
    @Override
    public ListIterator<T> listIterator() {
        clean();
        return new WeakListIterator(0);
    }

    /**
     * Get a list iterator.
     *
     * @param start  The start position of the iterator.
     */
    @Override
    public ListIterator<T> listIterator(int start) {
        PreCon.positiveNumber(start);

        clean();
        return new WeakListIterator(start);
    }

    /**
     * Remove an item from the list.
     *
     * @param obj  The item to remove.
     */
    @Override
    public boolean remove(Object obj) {
        PreCon.notNull(obj);

        Iterator<WeakReference<T>> wIterator = _references.iterator();

        boolean found = false;

        while (wIterator.hasNext()) {
            WeakReference<T> item = wIterator.next();
            if (item.get() == null) {
                wIterator.remove();
            }
            else {
                T value = item.get();
                if (value != null && value.equals(obj)) {
                    wIterator.remove();
                    found = true;
                }
            }
        }

        return found;
    }

    /**
     * Remove the item at the specified index.
     *
     * @param index  The index position.
     */
    @Override
    @Nullable
    public T remove(int index) {
        PreCon.positiveNumber(index);

        WeakReference<T> item = _references.remove(index);
        return item.get();
    }

    /**
     * Remove all items from the list that are in the specified collection.
     *
     * @param collection  The collection.
     */
    @Override
    public boolean removeAll(Collection<?> collection) {
        PreCon.notNull(collection);

        Iterator<WeakReference<T>> wIterator = _references.iterator();

        boolean found = false;

        while (wIterator.hasNext()) {
            WeakReference<T> item = wIterator.next();
            if (item.get() == null) {
                wIterator.remove();
            }
            else {
                T inner = item.get();
                if (collection.contains(inner)) {
                    wIterator.remove();
                    found = true;
                }
            }
        }

        return found;
    }

    /**
     * Remove all items from the list with the exception of items contained
     * in the specified collection.
     *
     * @param collection  The collection.
     */
    @Override
    public boolean retainAll(Collection<?> collection) {
        PreCon.notNull(collection);

        Iterator<WeakReference<T>> wIterator = _references.iterator();

        boolean found = false;

        while (wIterator.hasNext()) {
            WeakReference<T> item = wIterator.next();
            if (item.get() == null) {
                wIterator.remove();
            }
            else {
                T inner = item.get();
                if (!collection.contains(inner)) {
                    wIterator.remove();
                    found = true;
                }
            }
        }

        return found;
    }

    /**
     * Set the value of an item at the specified index location.
     *
     * @param index  The index position.
     * @param item   The item to set.
     * @return
     */
    @Override
    public T set(int index, T item) {
        PreCon.positiveNumber(index);
        PreCon.notNull(item);

        _references.set(index, new WeakReference<T>(item));
        return item;
    }

    /**
     * Get a segment of the list as a new list.
     *
     * @param start  The index position of the first item to add to the new list.
     * @param end    The index position of the last item to add to the new list.
     */
    @Override
    public List<T> subList(int start, int end) {
        PreCon.positiveNumber(start);
        PreCon.positiveNumber(end);
        PreCon.isValid(start <= end);

        clean();

        List<WeakReference<T>> subList = _references.subList(start, end);

        List<T> items = new ArrayList<T>(end - start);
        for (WeakReference<T> item : subList) {
            items.add(item.get());
        }
        return items;
    }

    /**
     * Convert the list into an object array.
     */
    @Override
    public Object[] toArray() {

        clean();

        Object[] items = new Object[_references.size()];
        for (int i=0; i < items.length; i++) {
            WeakReference<T> item = _references.get(i);
            items[i] = item.get();
        }
        return items;
    }

    /**
     * Convert the list into an array.
     */
    @Override
    public <E> E[] toArray(E[] array) {
        PreCon.notNull(array);

        for (int i=0; i < array.length; i++) {
            WeakReference<T> item = _references.get(i);

            @SuppressWarnings("unchecked") E reference = (E)item.get();
            array[i] = reference;
        }
        return array;
    }

    // remove lost references
    private void clean() {
        Iterator<WeakReference<T>> wIterator = _references.iterator();
        while (wIterator.hasNext()) {
            WeakReference<T> item = wIterator.next();
            if (item.get() == null) {
                wIterator.remove();
            }
        }
    }

    private class WeakIterator implements Iterator<T> {

        private Iterator<WeakReference<T>> wIterator = _references.iterator();

        public WeakIterator () {
            clean();
        }

        @Override
        public boolean hasNext() {
            return wIterator.hasNext();
        }

        @Override
        @Nullable
        public T next() {
            while (wIterator.hasNext()) {
                WeakReference<T> item = wIterator.next();
                T value = item.get();
                if (value != null)
                    return value;
            }
            return null;
        }

        @Override
        public void remove() {
            wIterator.remove();
        }

    }

    private class WeakListIterator implements ListIterator<T> {

        private ListIterator<WeakReference<T>> iterator;

        public WeakListIterator(int start) {
            iterator = _references.listIterator(start);
        }

        @Override
        public void add(T item) {
            iterator.add(new WeakReference<T>(item));

        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return iterator.hasPrevious();
        }

        @Override
        @Nullable
        public T next() {
            while (iterator.hasNext()) {
                WeakReference<T> item = iterator.next();
                T value = item.get();
                if (value != null)
                    return value;
            }
            return null;
        }

        @Override
        public int nextIndex() {
            return iterator.nextIndex();
        }

        @Override
        @Nullable
        public T previous() {
            while (iterator.hasPrevious()) {
                WeakReference<T> item = iterator.previous();
                T value = item.get();
                if (value != null)
                    return value;
            }

            return null;
        }

        @Override
        public int previousIndex() {
            return iterator.previousIndex();
        }

        @Override
        public void remove() {
            iterator.remove();

        }

        @Override
        public void set(T item) {
            iterator.set(new WeakReference<T>(item));

        }

    }


}
