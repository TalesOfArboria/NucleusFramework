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

package com.jcwhatever.nucleus.utils.performance.pool;

import javax.annotation.Nullable;

/**
 * Simple object pool.
 *
 * <p>Does not track objects from the pool. When an object from the pool is retrieved,
 * it is removed from the pool. The object must be recycled back into the pool via the
 * {@link #recycle} method when it is no longer in use.</p>
 */
public class SimplePool<E> extends AbstractPool<E> {

    /**
     * Constructor.
     *
     * @param capacity  The initial capacity of the pool.
     */
    public SimplePool(int capacity) {
        this(capacity, null, null);
    }

    /**
     * Constructor.
     *
     * @param capacity        The initial capacity of the pool.
     * @param elementFactory  The element factory used to create new elements when
     *                        the pool is empty.
     */
    public SimplePool(int capacity,
                      @Nullable IPoolElementFactory<E> elementFactory) {

        this(capacity, elementFactory, null);
    }

    /**
     * Constructor.
     *
     * @param capacity        The initial capacity of the pool.
     * @param elementFactory  The element factory used to create new elements when
     *                        the pool is empty.
     * @param recycleHandler  The handler to give a recycled element to for object teardown.
     */
    public SimplePool(int capacity,
                      @Nullable IPoolElementFactory<E> elementFactory,
                      @Nullable IPoolRecycleHandler<E> recycleHandler) {
        super(capacity, elementFactory, recycleHandler);
    }

    @Override
    @Nullable
    public E retrieve() {
        return super.retrieve();
    }

    @Override
    public boolean recycle(E element) {
        return super.recycle(element);
    }

    @Override
    public <T> int recycleAll(T[] elements, int start, int length) {
        return super.recycleAll(elements, start, length);
    }
}
