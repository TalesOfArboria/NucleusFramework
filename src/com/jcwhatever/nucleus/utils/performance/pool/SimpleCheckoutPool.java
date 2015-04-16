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

import com.jcwhatever.nucleus.utils.ArrayUtils;

import java.util.Iterator;
import javax.annotation.Nullable;

/**
 * A pool of objects that tracks elements that have been checked out of the pool.
 *
 * <p>The checked out elements can be retrieved by invoking {@link #getCheckedOut}
 * and recycled by invoking the returned objects {@link CheckedOutElements#recycle}
 * method. All checked out elements are recycled at once.</p>
 */
public class SimpleCheckoutPool<E> extends AbstractPool<E> {

    private final CheckedOutElements<E> _checkedOut = new CheckedOutElements<>(this);
    private final Object _sync = new Object();

    /**
     * Constructor.
     *
     * @param clazz           The class of the pool type.
     * @param size            The initial capacity of the pool.
     */
    public SimpleCheckoutPool(Class<E> clazz, int size) {
        this(clazz, size, null, null);
    }

    /**
     * Constructor.
     *
     * @param clazz           The class of the pool type.
     * @param size            The initial capacity of the pool.
     * @param elementFactory  The element factory used to create new elements when
     *                        the pool is empty.
     */
    public SimpleCheckoutPool(Class<E> clazz, int size,
                              @Nullable IPoolElementFactory<E> elementFactory) {

        this(clazz, size, elementFactory, null);
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
    public SimpleCheckoutPool(Class<E> clazz, int size,
                              @Nullable IPoolElementFactory<E> elementFactory,
                              @Nullable IPoolRecycleHandler<E> recycleHandler) {
        super(clazz, size, elementFactory, recycleHandler);

        _checkedOut.array = ArrayUtils.newArray(clazz, size);
    }

    /**
     * Checkout an element from the pool.
     *
     * <p>If the pool is empty, a new element is created if the pool has
     * an element factory.</p>
     *
     * @return  The element or null if the pool is empty and a pooled element
     * factory is not being used.
     */
    @Nullable
    public E checkout() {

        E element;

        synchronized (_sync) {

            element = super.retrieve();
            if (element == null)
                return null;

            if (maxSize() > -1 && _checkedOut.size() >= maxSize())
                return element;

            if (_checkedOut.size() + 1 > _checkedOut.array.length)
                _checkedOut.array = expand(_checkedOut.array, maxSize());

            _checkedOut.arrayIndex++;
            _checkedOut.array[_checkedOut.arrayIndex] = element;
        }

        return element;
    }

    /**
     * Get the elements that have been checked out.
     */
    public CheckedOutElements<E> getCheckedOut() {
        return _checkedOut;
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
    public void clear() {
        synchronized (_sync) {
            super.clear();
        }
    }

    @Override
    public void fitToSize(int padding) {

        synchronized (_sync) {
            super.fitToSize(padding);

            if (_checkedOut.size() <= pool().length) {
                _checkedOut.array = resize(_checkedOut.array, pool().length, -1);
            }
        }
    }

    @Override
    public boolean recycle(E element) {

        boolean result = super.recycle(element);
        _checkedOut.recycled(element);
        return result;
    }

    /**
     * Get the internal synchronization object.
     */
    protected Object getSync() {
        return _sync;
    }

    /**
     * Elements that have been checked out of a checkout pool.
     *
     * <p>Iterable to iterate checked out elements. The iterator does not
     * support the {@link Iterator#remove} method.</p>
     *
     * @param <E>  The element type.
     */
    public static class CheckedOutElements<E> implements Iterable<E> {

        final SimpleCheckoutPool<E> parent;
        E[] array;
        int arrayIndex = -1;

        /**
         * Constructor.
         *
         * @param pool  The owning pool.
         */
        CheckedOutElements(SimpleCheckoutPool<E> pool) {
            this.parent = pool;
        }

        /**
         * Get the owning pool.
         */
        public SimpleCheckoutPool<E> getPool() {
            return parent;
        }

        /**
         * Get the number of checked out elements.
         */
        public int size() {
            synchronized (this) {
                return arrayIndex + 1;
            }
        }

        /**
         * Recycle checked out elements back into the pool.
         */
        public void recycle() {

            int size = size();

            if (size == 0)
                return;

            synchronized (this) {

                synchronized (parent._sync) {

                    parent.recycleAll(array, 0, size);
                    clear();
                }
            }
        }

        /**
         * Clear all checked out elements.
         *
         * <p>Does not put elements back into the pool.</p>
         */
        public void clear() {
            synchronized (this) {
                ArrayUtils.reset(array);
                arrayIndex = -1;
            }
        }

        @Override
        public Iterator<E> iterator() {
            return new Iterator<E>() {

                int index = -1;

                @Override
                public boolean hasNext() {
                    return index + 1 < size();
                }

                @Override
                public E next() {
                    index++;
                    return array[index];
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        void recycled(E element) {

            if (arrayIndex == -1)
                return;

            int i = 0;

            // remove element from array
            for (; i <= arrayIndex; i++) {
                if (array[i].equals(element)) {
                    array[i] = null;
                    break;
                }
            }

            // compact array
            for (i++; i <= arrayIndex; i++) {
                array[i - 1] = array[i];
            }

            array[arrayIndex] = null;
            arrayIndex--;
        }
    }
}
