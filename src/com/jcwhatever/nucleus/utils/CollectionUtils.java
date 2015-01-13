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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Collection utilities
 */
public class CollectionUtils {

    private CollectionUtils() {}

    /**
     * Removes all matching instances of a value from the specified
     * {@code Multimap}.
     *
     * @param multimap  The multimap.
     * @param value     The value to remove.
     *
     * @param <K>  The key type.
     * @param <V>  The value type.
     *
     * @return  True if the {@code Multimap} was modified.
     */
    public static <K, V> boolean removeValue(Multimap<K, V> multimap, V value) {
        PreCon.notNull(multimap);
        PreCon.notNull(value);

        Iterator<Entry<K, V>> iterator = multimap.entries().iterator();

        boolean isChanged = false;

        while (iterator.hasNext()) {
            Entry<K, V> entry = iterator.next();

            if (value.equals(entry.getValue())) {
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
     * @return  A {@code List} of elements removed from the target.
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
     * Wrap a {@code Collection} in an unmodifiable {@code List}. If the
     * collection is already a {@code List} then it is cast, otherwise
     * its elements are copied into a new {@code List}.
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
     * Get an empty unmodifiable {@code List}.
     *
     * @param <E>  The collection element type.
     */
    public static <E> List<E> unmodifiableList() {
        return Collections.unmodifiableList(new ArrayList<E>(0));
    }

    /**
     * Wrap a {@code Collection} in an unmodifiable {@code Set}. If the
     * collection is already a {@code Set} then it is cast, otherwise
     * its elements are copied into a new {@code Set}.
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
     * Get an empty unmodifiable {@code Set}.
     *
     * @param <E>  The collection element type.
     */
    public static <E> Set<E> unmodifiableSet() {
        return Collections.unmodifiableSet(new HashSet<E>(0));
    }
}
