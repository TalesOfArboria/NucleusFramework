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

/**
 * Represents a timed collection which allows
 * adding callbacks.
 */
public interface ITimedCallbacks<E, C> {

    /**
     * Adds a callback to be called whenever an items
     * lifespan ends.
     *
     * @param callback  The callback to call.
     */
    void addOnLifespanEnd(LifespanEndAction<E> callback);

    /**
     * Removes a callback that is called whenever an items
     * lifespan ends..
     *
     * @param callback  The callback to remove.
     */
    void removeOnLifespanEnd(LifespanEndAction<E> callback);

    /**
     * Adds a callback to be called whenever the
     * collection becomes empty.
     *
     * @param callback  The callback to add.
     */
    void addOnCollectionEmpty(CollectionEmptyAction<C> callback);

    /**
     * Removes a callback that is called whenever the
     * collection becomes empty.
     *
     * @param callback  The callback to remove.
     */
    void removeOnCollectionEmpty(CollectionEmptyAction<C> callback);
}
