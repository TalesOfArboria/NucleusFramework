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

package com.jcwhatever.nucleus.regions.collections;

import com.jcwhatever.nucleus.collections.wrap.SetWrapper;
import com.jcwhatever.nucleus.regions.IRegion;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Region {@link java.util.Set} that sorts regions by priority.
 */
public class RegionSet<E extends IRegion> extends SetWrapper<E> {

    // set used when sorting is not needed
    private Set<E> _set;

    // set used for sorting
    private TreeSet<E> _treeSet;

    /**
     * Constructor.
     *
     * <p>Initial capacity is 10.</p>
     *
     * @param sorted  The initial sorting state. True if sorting is expected.
     */
    public RegionSet(boolean sorted) {
        this(10, sorted);
    }

    /**
     * Constructor.
     *
     * @param size    The initial capacity.
     * @param sorted  The initial sorting state. True if sorting is expected.
     */
    public RegionSet(int size, boolean sorted) {

        if (sorted)
            switchTreeSet();
        else
            _set = new HashSet<>(size);
    }

    /**
     * Constructor.
     *
     * @param sorted      The initial sorting state. True if sorting is expected.
     * @param collection  The collection to add.
     */
    public RegionSet(boolean sorted, Collection<? extends E> collection) {
        this(collection.size(), sorted);

        addAll(collection);
    }

    @Override
    protected boolean onPreAdd(E e) {
        if (e.getPriority() > 0)
            switchTreeSet();
        return true;
    }

    @Override
    protected Set<E> set() {
        if (_set != null)
            return _set;
        return _treeSet;
    }

    /**
     * Switch to using a {@link java.util.TreeSet} for sorting.
     */
    protected void switchTreeSet() {

        if (_treeSet != null)
            return;

        _treeSet = new TreeSet<>(getComparator());

        if (_set != null) {
            _treeSet.addAll(_set);
        }

        _set = null;
    }

    /**
     * Get a new {@link java.util.TreeSet} comparator.
     */
    protected Comparator<E> getComparator() {
        return new Comparator<E>() {
            @Override
            public int compare(E region1, E region2) {

                // sort regions with highest priority (highest value) first
                return Integer.compare(region2.getPriority(), region1.getPriority());
            }
        };
    }
}
