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

package com.jcwhatever.nucleus.mixins;

import java.util.Iterator;
import java.util.List;

/**
 * Represents a type that acts as a paginator.
 */
public interface IPaginator<E> {

    /**
     * Determine the index number used to represent
     * the first page.
     */
    PageStartIndex getPageStartIndex();

    /**
     * Get the total number of individual items.
     */
    int size();

    /**
     * Get the total number of pages.
     */
    int getTotalPages();

    /**
     * Get the max number of items per page.
     */
    int getItemsPerPage();

    /**
     * Set the max number of items per page.
     *
     * @param itemsPerPage  The number of items per page.
     */
    void setItemsPerPage(int itemsPerPage);

    /**
     * Get a list of items from the specified page.
     *
     * @param page  The page index.
     */
    List<E> getPage(int page);

    /**
     * Get an iterator to iterate over the elements of the
     * specified page.
     */
    Iterator<E> iterator(int page);


    public enum PageStartIndex {
        /**
         * Page indexes are zero based, meaning
         * the first page is specified with the
         * integer 0.
         */
        ZERO (0),
        /**
         * Page indexes are one based, meaning
         * the first page is specified with the
         * integer 1.
         */
        ONE (1);

        private final int _startIndex;

        PageStartIndex(int startIndex) {
            _startIndex = startIndex;
        }

        public int getStartIndex() {
            return _startIndex;
        }
    }
}
