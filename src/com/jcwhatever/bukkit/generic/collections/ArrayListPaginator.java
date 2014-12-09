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

import com.jcwhatever.bukkit.generic.mixins.IPaginator;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * An ArrayList that implements {@link IPaginator}.
 */
public class ArrayListPaginator<E> extends ArrayList<E> implements IPaginator<E> {

    private final PageStartIndex _start;
    private int _itemsPerPage;

    /**
     * Constructor.
     *
     * @param pageStartIndex  The index of the first page.
     * @param itemsPerPage    The number of items per page.
     */
    public ArrayListPaginator(PageStartIndex pageStartIndex, int itemsPerPage) {
        this(pageStartIndex, itemsPerPage, 10);
    }

    /**
     * Constructor.
     *
     * @param pageStartIndex  The index of the first page.
     * @param itemsPerPage    The number if items per page.
     * @param size            The initial capacity of the {@code ArrayList}.
     */
    public ArrayListPaginator(PageStartIndex pageStartIndex, int itemsPerPage, int size) {
        super(size);

        PreCon.notNull(pageStartIndex);
        PreCon.greaterThanZero(itemsPerPage);

        _itemsPerPage = itemsPerPage;
        _start = pageStartIndex;
    }

    /**
     * Constructor.
     *
     * @param pageStartIndex  The index of the first page.
     * @param itemsPerPage    The number if items per page.
     * @param collection      The initial collection.
     */
    public ArrayListPaginator(PageStartIndex pageStartIndex, int itemsPerPage,
                              Collection<? extends E> collection) {
        super(collection);

        PreCon.notNull(pageStartIndex);
        PreCon.greaterThanZero(itemsPerPage);

        _itemsPerPage = itemsPerPage;
        _start = pageStartIndex;
    }

    /**
     * Get the {@code PageStartIndex} constant that defines
     * the index of page 1.
     */
    @Override
    public PageStartIndex getPageStartIndex() {
        return _start;
    }

    /**
     * Get the total number of pages in the paginator.
     */
    @Override
    public int getTotalPages() {
        return (int)Math.ceil((double)size() / _itemsPerPage);
    }

    /**
     * Get the max number of items per page.
     */
    @Override
    public int getItemsPerPage() {
        return _itemsPerPage;
    }

    /**
     * Set the max number of items per page.
     *
     * @param itemsPerPage  The number of items per page.
     */
    @Override
    public void setItemsPerPage(int itemsPerPage) {
        _itemsPerPage = itemsPerPage;
    }

    /**
     * Get a sub list of items from the specified page.
     *
     * @param page  The page index.
     */
    @Override
    public List<E> getPage(int page) {
        checkPage(page);

        if (size() == 0)
            return new ArrayList<E>(0);

        int start = getStartIndex(page);
        int end = getEndIndex(page);

        return subList(start, end);
    }

    /**
     * Get an iterator to iterate over the items of
     * the specified page.
     *
     * @param page  The page index.
     */
    @Override
    public ListIterator<E> iterator(int page) {
        checkPage(page);

        return new PaginatorIterator(page);
    }

    private int getStartIndex(int page) {
        return (page - _start.getStartIndex()) * _itemsPerPage;
    }

    private int getEndIndex(int page) {
        return Math.min(size(), getStartIndex(page) + _itemsPerPage - 1);
    }

    private void checkPage(int page) {
        switch (_start) {
            case ZERO:
                PreCon.positiveNumber(page);
                break;
            case ONE:
                PreCon.greaterThanZero(page);
                break;
            default:
                throw new AssertionError();
        }
    }

    /**
     * List iterator for a specific page.
     */
    public class PaginatorIterator implements ListIterator<E> {

        int _index;
        final int _startIndex;
        final int _endIndex;

        PaginatorIterator(int page) {
            _startIndex = getStartIndex(page);
            _endIndex = getEndIndex(page);
            _index = _startIndex;
        }

        @Override
        public boolean hasNext() {
            return size() > 0 && _index <= _endIndex && _index <= size();
        }

        @Override
        public E next() {
            E entry = ArrayListPaginator.this.get(_index);
            _index++;
            return entry;
        }

        @Override
        public boolean hasPrevious() {
            return _index > _startIndex;
        }

        @Override
        public E previous() {
            _index--;
            return ArrayListPaginator.this.get(_index);
        }

        @Override
        public int nextIndex() {
            return _index + 1;
        }

        @Override
        public int previousIndex() {
            return _index - 1;
        }

        @Override
        public void remove() {
            ArrayListPaginator.this.remove(_index);
        }

        @Override
        public void set(E e) {
            ArrayListPaginator.this.set(_index, e);
        }

        @Override
        public void add(E e) {
            ArrayListPaginator.this.add(_index, e);
        }
    }
}
