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
 * Concurrent implementation of {@link SimplePool}.
 *
 * <p>Uses synchronization blocks that sync on a private object.</p>
 */
public class SimpleConcurrentPool<E> extends SimplePool<E> {

    private final Object _sync = new Object();

    /**
     * Constructor.
     *
     * @param clazz           The class of the pool type.
     * @param size            The initial capacity of the pool.
     * @param elementFactory  The element factory used to create new elements when
     *                        the pool is empty.
     */
    public SimpleConcurrentPool(Class<E> clazz, int size, IPoolElementFactory<E> elementFactory) {
        super(clazz, size, elementFactory, null);
    }

    /**
     * Constructor.
     *
     * @param clazz           The class of the pool type.
     * @param size            The initial capacity of the pool.
     * @param elementFactory  The element factory used to create new elements when
     *                        the pool is empty.
     * @param recycleHandler  The handler to give a recycled element to for object teardown.
     */
    public SimpleConcurrentPool(Class<E> clazz, int size,
                      IPoolElementFactory<E> elementFactory,
                      @Nullable IPoolRecycleHandler<E> recycleHandler) {
        super(clazz, size, elementFactory, recycleHandler);
    }

    @Override
    public int size() {
        synchronized (_sync) {
            return super.size();
        }
    }

    @Override
    public int maxSize() {
        synchronized (_sync) {
            return super.maxSize();
        }
    }

    @Override
    public void setMaxSize(int maxSize) {
        synchronized (_sync) {
            super.setMaxSize(maxSize);
        }
    }

    @Override
    public boolean contains(@Nullable Object element) {
        synchronized (_sync) {
            return super.contains(element);
        }
    }

    @Override
    public E retrieve() {
        synchronized (_sync) {
            return super.retrieve();
        }
    }

    @Override
    public boolean recycle(E element) {
        synchronized (_sync) {
            return super.recycle(element);
        }
    }

    @Override
    public int recycleAll(E[] elements, int start, int length) {
        synchronized (_sync) {
            return super.recycleAll(elements, start, length);
        }
    }

    @Override
    public void fitToSize(int padding) {
        synchronized (_sync) {
            super.fitToSize(padding);
        }
    }

    @Override
    public void clear() {
        synchronized (_sync) {
            super.clear();
        }
    }

    /**
     * Get the internal synchronization object.
     */
    protected Object getSync() {
        return _sync;
    }
}
