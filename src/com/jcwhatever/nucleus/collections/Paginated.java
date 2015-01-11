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

import com.jcwhatever.nucleus.collections.wrappers.AbstractListWrapper;
import com.jcwhatever.nucleus.mixins.IPaginator;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * An {@code IPaginator} implementation that wraps a {@code List} implementation.
 */
public class Paginated<E> extends AbstractListWrapper<E> implements IPaginator<E> {

    private final PageStartIndex _start;
    private final List<E> _list;
    private int _itemsPerPage;

    /**
     * Constructor.
     *
     * @param startIndex    The index of the first page.
     * @param itemsPerPage  The number if items per page.
     * @param list          The list to wrap.
     */
    public Paginated(PageStartIndex startIndex, int itemsPerPage, List<E> list) {
        PreCon.notNull(startIndex);
        PreCon.greaterThanZero(itemsPerPage);
        PreCon.notNull(list);

        _start = startIndex;
        _itemsPerPage = itemsPerPage;
        _list = list;
    }

    @Override
    public PageStartIndex getPageStartIndex() {
        return _start;
    }

    @Override
    public int getTotalPages() {
        return (int)Math.ceil((double)size() / _itemsPerPage);
    }

    @Override
    public int getItemsPerPage() {
        return _itemsPerPage;
    }

    @Override
    public void setItemsPerPage(int itemsPerPage) {
        _itemsPerPage = itemsPerPage;
    }

    @Override
    public List<E> getPage(int page) {
        checkPage(page);

        if (size() == 0)
            return new ArrayList<E>(0);

        int start = getStartIndex(page);
        int end = getEndIndex(page);

        if (start >= size())
            return new ArrayList<E>(0);

        return subList(start, end);
    }

    @Override
    public ListIterator<E> iterator(int page) {
        checkPage(page);

        return new PaginatorIterator(page);
    }

    @Override
    protected List<E> getList() {
        return _list;
    }

    protected int getStartIndex(int page) {
        return (page - _start.getStartIndex()) * _itemsPerPage;
    }

    protected int getEndIndex(int page) {
        return Math.min(size(), getStartIndex(page) + _itemsPerPage);
    }

    protected void checkPage(int page) {
        switch (_start) {
            case ZERO:
                PreCon.positiveNumber(page, "page");
                PreCon.lessThan(page, getTotalPages(), "page");
                break;
            case ONE:
                PreCon.greaterThanZero(page, "page");
                PreCon.lessThanEqual(page, getTotalPages(), "page");
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
            return size() > 0 && _index < _endIndex && _index < size();
        }

        @Override
        public E next() {
            E entry = Paginated.this.get(_index);
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
            return Paginated.this.get(_index);
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
            Paginated.this.remove(_index);
        }

        @Override
        public void set(E e) {
            Paginated.this.set(_index, e);
        }

        @Override
        public void add(E e) {
            Paginated.this.add(_index, e);
        }
    }
}
