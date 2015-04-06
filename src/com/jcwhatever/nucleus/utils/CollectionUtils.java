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

import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Collection utilities.
 */
public class CollectionUtils {

    private CollectionUtils() {}

    /**
     * Search a collection for valid candidates using an {@link IValidator} to validate.
     *
     * @param searchCandidates  The search candidates.
     * @param validator         The validator.
     */
    public static <T> List<T> search(Collection<T> searchCandidates, IValidator<T> validator) {
        PreCon.notNull(searchCandidates);
        PreCon.notNull(validator);

        List<T> result = new ArrayList<>(searchCandidates.size());

        for (T candidate : searchCandidates) {

            if (validator.isValid(candidate)) {
                result.add(candidate);
            }
        }

        return result;
    }

    /**
     * Search a collection for elements that contain the specified text search term.
     *
     * <p>Collection is ordered by best matches. Best match is based on case sensitivity match,
     * the index position of the text, and alphabetical sorting.</p>
     *
     * <p>The search term is matched against the elements {@link Object#toString} result.</p>
     *
     * @param candidates  A collection of candidate elements to search.
     * @param searchTerm  The text to match.
     *
     * @param <T>  The element type.
     */
    public static <T> List<T> textSearch(Collection<T> candidates, String searchTerm) {
        return textSearch(candidates, searchTerm, new ISearchTextGetter<T>() {
            @Override
            public String getText(T element) {
                return element.toString();
            }
        });
    }

    /**
     * Search a collection for elements that contain the specified text search term.
     *
     * <p>Collection is ordered by best matches. Best match is based on case sensitivity match,
     * the index position of the text, and alphabetical sorting.</p>
     *
     * @param candidates  A collection of candidate elements to search.
     * @param searchTerm  The text to match.
     * @param textGetter  A {@link ISearchTextGetter} to get text to match against from elements.
     *
     * @param <T>  The element type.
     */
    public static <T> List<T> textSearch(Collection<T> candidates, String searchTerm,
                                  ISearchTextGetter<T> textGetter) {
        PreCon.notNull(candidates);
        PreCon.notNull(searchTerm);
        PreCon.notNull(textGetter);

        if (candidates.size() == 0)
            return new ArrayList<>(0);

        PriorityQueue<WeightedSearchResult<T>> queue = new PriorityQueue<>(candidates.size());

        String caseSearch = searchTerm.toUpperCase();

        for (T candidate : candidates) {

            String text = textGetter.getText(candidate);
            if (text == null)
                continue;

            int weight = text.indexOf(searchTerm);
            if (weight == -1) {

                String caseText = text.toUpperCase();
                weight = caseText.indexOf(caseSearch);
                if (weight == -1) {
                    continue;
                }
                else {
                    weight++; // increase weight of non-case matching
                }
            }

            queue.add(new WeightedSearchResult<T>(weight, candidate, text));
        }

        List<T> results = new ArrayList<>(queue.size());
        while (!queue.isEmpty()) {
            results.add(queue.remove().item);
        }

        return results;
    }

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

    /**
     * Interface used for {@link CollectionUtils#textSearch} method.
     */
    public interface ISearchTextGetter<T> {

        /**
         * Get the text to match against from an element.
         *
         * @param element  The element to get text from.
         */
        String getText(T element);
    }

    private static class WeightedSearchResult<T> implements Comparable<WeightedSearchResult<T>> {
        int weight;
        T item;
        String text;

        WeightedSearchResult(int weight, T item, String text) {
            this.weight = weight;
            this.item = item;
            this.text = text;
        }

        @Override
        public int compareTo(WeightedSearchResult<T> o) {

            int result = Integer.compare(weight, o.weight);

            if (result == 0)
                result = text.compareTo(o.text);

            return result;
        }
    }

    public static final List UNMODIFIABLE_EMPTY_LIST = new AbstractList() {

        @Override
        public int size() {
            return 0;
        }

        @Override
        public Object get(int index) {
            throw new UnsupportedOperationException();
        }
    };

    public static final Set UNMODIFIABLE_EMPTY_SET = new AbstractSet() {

        @Override
        public int size() {
            return 0;
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
    };
}
