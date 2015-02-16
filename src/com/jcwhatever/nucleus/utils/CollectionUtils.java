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

package com.jcwhatever.nucleus.utils;

import com.google.common.collect.Multimap;
import com.jcwhatever.nucleus.utils.validate.IValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Collection utilities
 */
public class CollectionUtils {

    private CollectionUtils() {}

    /**
     * Removes all matching instances of a value from the specified
     * {@link com.google.common.collect.Multimap}.
     *
     * @param multimap  The multimap.
     * @param value     The value to remove.
     *
     * @param <K>  The key type.
     * @param <V>  The value type.
     *
     * @return  True if the {@link com.google.common.collect.Multimap} was modified.
     */
    public static <K, V> boolean removeValue(Multimap<K, V> multimap, @Nullable Object value) {
        return removeValue(multimap.entries(), value);
    }

    /**
     * Removes all matching instances of a value from the specified
     * {@link java.util.Map}.
     *
     * @param map    The map.
     * @param value  The value to remove.
     *
     * @param <K>  The key type.
     * @param <V>  The value type.
     *
     * @return  True if the {@link java.util.Map} was modified.
     */
    public static <K, V> boolean removeValue(Map<K, V> map, @Nullable Object value) {
        return removeValue(map.entrySet(), value);
    }

    /**
     * Removes all entries with matching specified value from the specified
     * {@link java.util.Collection} of {@link java.util.Map.Entry}.
     *
     * @param entries  The map.
     * @param value    The value to remove.
     *
     * @param <K>  The key type.
     * @param <V>  The value type.
     *
     * @return  True if the {@link Collection} was modified.
     */
    public static <K, V> boolean removeValue(Collection<Entry<K, V>> entries, @Nullable Object value) {
        PreCon.notNull(entries);

        Iterator<Entry<K, V>> iterator = entries.iterator();

        boolean isChanged = false;

        while (iterator.hasNext()) {
            Entry<? extends K, ? extends V> entry = iterator.next();

            if ((value == null && entry.getValue() == null) ||
                    (value != null && value.equals(entry.getValue()))) {
                iterator.remove();
                isChanged = true;
            }
        }

        return isChanged;
    }

    /**
     * Removes all elements from the target collection that are not present
     * in the retain collection. Useful if the removed elements are needed.
     *
     * @param target  The target collection.
     * @param retain  The collection of items that must be retained.
     *
     * @param <T>  The element type.
     *
     * @return  A {@link java.util.List} of elements removed from the target.
     */
    public static <T> List<T> retainAll(Collection<T> target, Collection<?> retain) {

        List<T> removed = new ArrayList<>(Math.abs(target.size() - retain.size()));

        Iterator<T> iterator = target.iterator();
        while (iterator.hasNext()) {
            T element = iterator.next();

            if (!retain.contains(element)) {
                removed.add(element);
                iterator.remove();
            }
        }

        return removed;
    }

    /**
     * Removes all elements from the target collection that are not valid
     * according to the supplied {@link IValidator}.
     *
     * @param target     The target collection.
     * @param validator  The validator that will validate each element in the collection.
     *
     * @param <T>  The element type.
     *
     * @return  A {@link java.util.List} of elements removed from the target.
     */
    public static <T> List<T> retainAll(Collection<T> target, IValidator<T> validator) {

        List<T> removed = new ArrayList<>(10);

        Iterator<T> iterator = target.iterator();
        while (iterator.hasNext()) {
            T element = iterator.next();

            if (!validator.isValid(element)) {
                removed.add(element);
                iterator.remove();
            }
        }

        return removed;
    }

    /**
     * Wrap a {@link java.util.Collection} in an unmodifiable {@link java.util.List}.
     * If the collection is already a {@link java.util.List} then it is cast, otherwise
     * its elements are copied into a new {@link java.util.List}.
     *
     * @param collection  The collection to wrap.
     *
     * @param <E>  The collection element type.
     */
    public static <E> List<E> unmodifiableList(Collection<E> collection) {
        return collection instanceof List
                ? Collections.unmodifiableList((List<E>) collection)
                : Collections.unmodifiableList(new ArrayList<E>(collection));
    }

    /**
     * Get an empty unmodifiable {@link java.util.List}.
     *
     * @param <E>  The collection element type.
     */
    public static <E> List<E> unmodifiableList() {

        @SuppressWarnings("unchecked")
        List<E> list = (List<E>) UNMODIFIABLE_EMPTY_LIST;

        return list;
    }

    /**
     * Get an empty unmodifiable {@link java.util.List}.
     *
     * <p>Used when the empty signature method cannot be used.
     * Prevents errors and warnings.</p>
     *
     * @param clazz  The component type class.
     *
     * @param <E>  The collection element type.
     */
    public static <E> List<E> unmodifiableList(
            @SuppressWarnings("unused ")Class<E> clazz) {

        @SuppressWarnings("unchecked")
        List<E> list = (List<E>) UNMODIFIABLE_EMPTY_LIST;

        return list;
    }

    /**
     * Wrap a {@link java.util.Collection} in an unmodifiable {@link java.util.Set}.
     * If the collection is already a {@link java.util.Set} then it is cast, otherwise
     * its elements are copied into a new {@link java.util.Set}.
     *
     * @param collection  The collection to wrap.
     *
     * @param <E>  The collection element type.
     */
    public static <E> Set<E> unmodifiableSet(Collection<E> collection) {
        return collection instanceof Set
                ? Collections.unmodifiableSet((Set<E>) collection)
                : Collections.unmodifiableSet(new HashSet<E>(collection));
    }

    /**
     * Get an empty unmodifiable {@link java.util.Set}.
     *
     * @param <E>  The collection element type.
     */
    public static <E> Set<E> unmodifiableSet() {

        @SuppressWarnings("unchecked")
        Set<E> set = (Set<E>)UNMODIFIABLE_EMPTY_SET;

        return set;
    }

    /**
     * Get an empty unmodifiable {@link java.util.Set}.
     *
     * <p>Convenience method to use when the empty signature method
     * cannot be used. Prevents errors and warnings.</p>
     *
     * @param clazz  The component type class.
     *
     * @param <E>  The collection element type.
     */
    public static <E> Set<E> unmodifiableSet(
            @SuppressWarnings("unused")Class<E> clazz) {

        @SuppressWarnings("unchecked")
        Set<E> set = (Set<E>)UNMODIFIABLE_EMPTY_SET;

        return set;
    }

    public static final List UNMODIFIABLE_EMPTY_LIST = new List() {

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator iterator() {
            return new Iterator() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public Object next() {
                    return null;
                }

                @Override
                public void remove() {

                }
            };
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public boolean add(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, Collection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object get(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object set(int index, Object element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, Object element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object remove(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(Object o) {
            return -1;
        }

        @Override
        public int lastIndexOf(Object o) {
            return -1;
        }

        @Override
        public ListIterator listIterator() {
            return listIterator(0);
        }

        @Override
        public ListIterator listIterator(int index) {
            return new ListIterator() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public Object next() {
                    return null;
                }

                @Override
                public boolean hasPrevious() {
                    return false;
                }

                @Override
                public Object previous() {
                    return null;
                }

                @Override
                public int nextIndex() {
                    return 0;
                }

                @Override
                public int previousIndex() {
                    return 0;
                }

                @Override
                public void remove() {

                }

                @Override
                public void set(Object o) {

                }

                @Override
                public void add(Object o) {

                }
            };
        }

        @Override
        public List subList(int fromIndex, int toIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection c) {
            return false;
        }

        @Override
        public Object[] toArray(Object[] a) {
            return a;
        }
    };

    public static final Set UNMODIFIABLE_EMPTY_SET = new Set() {

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator iterator() {
            return new Iterator() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public Object next() {
                    return null;
                }

                @Override
                public void remove() {

                }
            };
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public boolean add(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object[] toArray(Object[] a) {
            return a;
        }
    };
}
