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

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.Rand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * An array list of weighted items. The weight of an item
 * affects the results when an item is randomly chosen from the
 * list using the {@code getRandom} method.
 * <p>
 *     Adding items using the {@code List} implementation
 *     gives items the default weight.
 * </p>
 *
 * @param <T>  The item type.
 */
public class WeightedArrayList<T> implements List<T> {

    private int _sumOfWeight = 0;
    private List<Weighted<T>> _weightedItems;


    public WeightedArrayList() {
        _weightedItems = new ArrayList<>(20);
    }

    public WeightedArrayList(int size) {
        PreCon.positiveNumber(size);

        _weightedItems = new ArrayList<>(size);
    }

    /**
     * Get the sum of weight of items in the list.
     */
    public int getSumOfWeight() {
        return _sumOfWeight;
    }

    /**
     * Get a random item from the list.
     */
    public T getRandom() {

        int sumOfWeights = getSumOfWeight();

        int randomInt = Rand.getInt(sumOfWeights) + 1;

        for (Weighted<T> weighted : _weightedItems) {
            randomInt -= weighted.getWeight();

            if (randomInt <= 0) {
                return weighted.getItem();
            }
        }

        Weighted<T> result = Rand.get(_weightedItems);
        return result.getItem();
    }

    /**
     * Add an item to the list with the specified weight.
     *
     * @param item    The item to add.
     * @param weight  The item weight.
     */
    public boolean add(T item, int weight) {
        PreCon.notNull(item);
        PreCon.greaterThanZero(weight);

        Weighted<T> weighted = new Weighted<T>(item, weight);
        _sumOfWeight += weight;
        return _weightedItems.add(weighted);
    }

    /**
     * Get an iterator that can return the current items
     * weighted value.
     */
    public WeightedIterator<T> weightedIterator() {
        return new WeightedIterator<T>() {

            Iterator<Weighted<T>> iterator = _weightedItems.iterator();
            Weighted<T> current;

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                current = iterator.next();
                return current.getItem();
            }

            @Override
            public int weight() {
                return current.getWeight();
            }
        };
    }

    @Override
    public int indexOf(Object obj) {
        PreCon.notNull(obj);

        return _weightedItems.indexOf(obj);
    }

    @Override
    public int lastIndexOf(Object obj) {
        PreCon.notNull(obj);

        return _weightedItems.lastIndexOf(obj);
    }

    @Override
    public ListIterator<T> listIterator() {
        return new WeightedListIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        PreCon.positiveNumber(index);
        PreCon.lessThanEqual(index, _weightedItems.size());

        return new WeightedListIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        PreCon.positiveNumber(fromIndex);
        PreCon.lessThanEqual(toIndex, _weightedItems.size());

        List<Weighted<T>> list = _weightedItems.subList(fromIndex, toIndex);
        List<T> result = new ArrayList<>(list.size());
        for (Weighted<T> weighted : list) {
            result.add(weighted.getItem());
        }

        return result;
    }

    @Override
    public int size() {
        return _weightedItems.size();
    }

    @Override
    public boolean isEmpty() {
        return _weightedItems.isEmpty();
    }

    @Override
    public boolean contains(Object obj) {
        PreCon.notNull(obj);

        for (Weighted<T> weighted : _weightedItems) {
            if (weighted.getItem().equals(obj))
                return true;
        }

        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            Iterator<Weighted<T>> iterator = _weightedItems.iterator();
            Weighted<T> current;

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                current = iterator.next();
                return current.getItem();
            }

            @Override
            public void remove() {
                iterator.remove();
                if (current != null) {
                    _sumOfWeight -= current.getWeight();
                }
            }
        };
    }

    @Override
    public Object[] toArray() {

        Object[] array = new Object[_weightedItems.size()];

        for (int i=0; i < array.length; i++)
            array[i] = _weightedItems.get(i).getItem();

        return array;
    }

    @Override
    public <T1> T1[] toArray(T1[] array) {
        PreCon.notNull(array);

        for (int i=0; i < array.length; i++) {
            @SuppressWarnings("unchecked") T1 item = (T1) _weightedItems.get(i).getItem();
            array[i] = item;
        }
        return array;
    }

    @Override
    public boolean add(T item) {
        PreCon.notNull(item);

        if (_weightedItems.add(new Weighted<T>(item, 1))) {
            _sumOfWeight += 1;
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object obj) {
        PreCon.notNull(obj);

        ListIterator<Weighted<T>> iterator = _weightedItems.listIterator();

        while (iterator.hasNext()) {
            Weighted<T> weighted = iterator.next();

            if (weighted.getItem().equals(obj)) {
                _sumOfWeight -= weighted.getWeight();
                iterator.remove();
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        PreCon.notNull(collection);

        for (Object obj : collection) {

            ListIterator<Weighted<T>> iterator = _weightedItems.listIterator();

            boolean contains = false;

            while (iterator.hasNext()) {
                Weighted<T> weighted = iterator.next();
                if (weighted.getItem().equals(obj)) {
                    contains = true;
                    break;
                }
            }

            if (!contains)
                return false;
        }

        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        PreCon.notNull(collection);

        for (T item : collection) {
            add(item);
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> collection) {
        PreCon.positiveNumber(index);
        PreCon.lessThanEqual(index, _weightedItems.size());

        List<Weighted<T>> weightedList = new ArrayList<>(collection.size());

        for (T item : collection) {
            weightedList.add(new Weighted<T>(item, 1));
            _sumOfWeight += 1;
        }

        return _weightedItems.addAll(index, weightedList);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        PreCon.notNull(collection);

        Iterator<Weighted<T>> iterator = _weightedItems.iterator();
        int startSize = _weightedItems.size();

        while(iterator.hasNext()) {
            Weighted<T> weighted = iterator.next();

            for (Object obj : collection) {
                if (weighted.getItem().equals(obj)) {
                    iterator.remove();
                    _sumOfWeight -= weighted.getWeight();
                    break;
                }
            }
        }

        return startSize != _weightedItems.size();
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        PreCon.notNull(collection);

        Iterator<Weighted<T>> iterator = _weightedItems.iterator();
        int startSize = _weightedItems.size();

        while(iterator.hasNext()) {
            Weighted<T> weighted = iterator.next();

            boolean contains = false;

            for (Object obj : collection) {
                if (weighted.getItem().equals(obj)) {
                    contains = true;
                    break;
                }
            }

            if (!contains) {
                iterator.remove();
                _sumOfWeight -= weighted.getWeight();
            }
        }

        return startSize != _weightedItems.size();
    }

    @Override
    public void clear() {
        _sumOfWeight = 0;
        _weightedItems.clear();
    }

    @Override
    public T get(int index) {
        PreCon.positiveNumber(index);
        PreCon.lessThan(index, _weightedItems.size());

        Weighted<T> weighted = _weightedItems.get(index);
        return weighted.getItem();
    }

    @Override
    public T set(int index, T element) {
        PreCon.positiveNumber(index);
        PreCon.lessThan(index, _weightedItems.size());
        PreCon.notNull(element);

        Weighted<T> weighted = _weightedItems.get(index);
        _sumOfWeight -= weighted.getWeight();

        weighted = new Weighted<>(element, 1);
        _sumOfWeight += 1;

        _weightedItems.set(index, weighted);
        return element;
    }

    @Override
    public void add(int index, T element) {
        PreCon.positiveNumber(index);
        PreCon.lessThan(index, _weightedItems.size());
        PreCon.notNull(element);

        Weighted<T> weighted = new Weighted<>(element, 1);
        _weightedItems.add(index, weighted);

        _sumOfWeight += 1;
    }

    @Override
    public T remove(int index) {
        PreCon.positiveNumber(index);
        PreCon.lessThan(index, _weightedItems.size());

        Weighted<T> weighted = _weightedItems.remove(index);
        _sumOfWeight -= weighted.getWeight();
        return weighted.getItem();
    }


    private static class Weighted<T> {

        private T _item;
        private int _weight;

        public Weighted(T item, int weight) {
            _item = item;
            _weight = weight;
        }

        public T getItem() {
            return _item;
        }

        public int getWeight() {
            return _weight;
        }

    }

    /**
     * WeightedList iterator.
     *
     * @param <T>  Item type.
     */
    public interface WeightedIterator<T> {

        /**
         * Determine if there is a next item.
         */
        boolean hasNext();

        /**
         * Get the next item.
         */
        T next();

        /**
         * Get the weight of the current item.
         */
        int weight();
    }

    private class WeightedListIterator implements ListIterator<T> {

        private ListIterator<Weighted<T>> iterator;
        private Weighted<T> current;

        public WeightedListIterator(int index) {
            iterator = _weightedItems.listIterator(index);
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public T next() {
            current = iterator.next();
            return current.getItem();
        }

        @Override
        public boolean hasPrevious() {
            return iterator.hasPrevious();
        }

        @Override
        public T previous() {
            current = iterator.previous();
            return current.getItem();
        }

        @Override
        public int nextIndex() {
            return iterator.nextIndex();
        }

        @Override
        public int previousIndex() {
            return iterator.previousIndex();
        }

        @Override
        public void remove() {
            iterator.remove();
            if (current != null) {
                _sumOfWeight -= current.getWeight();
            }
        }

        @Override
        public void set(T t) {
            PreCon.notNull(t);

            if (current != null) {
                _sumOfWeight -= current.getWeight();
            }

            Weighted<T> weighted = new Weighted<>(t, 1);
            iterator.set(weighted);
            _sumOfWeight += 1;
        }

        @Override
        public void add(T t) {
            Weighted<T> weighted = new Weighted<>(t, 1);
            iterator.set(weighted);
            _sumOfWeight += 1;
        }
    }

}
