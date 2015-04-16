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
import com.jcwhatever.nucleus.utils.PreCon;

import javax.annotation.Nullable;

/**
 * Abstract implementation of an array based object pool.
 */
public abstract class AbstractPool<E> {

    private final Class<E> _clazz;
    private final IPoolElementFactory<E> _elementFactory;
    private final IPoolRecycleHandler<E> _recycleHandler;

    private E[] _pool;
    private int _poolIndex = -1;
    private int _maxSize = -1;

    /**
     * Constructor.
     *
     * @param clazz  The class of the pool type.
     * @param size   The initial capacity of the pool.
     */
    public AbstractPool(Class<E> clazz, int size) {
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
    public AbstractPool(Class<E> clazz, int size,
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
    public AbstractPool(Class<E> clazz, int size,
                      @Nullable IPoolElementFactory<E> elementFactory,
                      @Nullable IPoolRecycleHandler<E> recycleHandler) {

        PreCon.notNull(clazz);

        _clazz = clazz;
        _pool = ArrayUtils.newArray(clazz, size);
        _elementFactory = elementFactory;
        _recycleHandler = recycleHandler;
    }

    /**
     * Get the pooled element class.
     */
    public Class<E> getElementClass() {
        return _clazz;
    }

    /**
     * Get the number of pooled elements.
     */
    public int size() {
        return _poolIndex + 1;
    }

    /**
     * Get the maximum size of the pool.
     *
     * @return  The maximum size or -1 to indicate "infinite" capacity.
     */
    public int maxSize() {
        return _maxSize;
    }

    /**
     * Set the maximum size of the pool.
     *
     * <p>Unless the new size is -1, the pool is resized to the max size.</p>
     *
     * @param maxSize  The maximum number of elements to hold in the pool. -1 to
     *                 indicate "infinite" capacity.
     */
    public void setMaxSize(int maxSize) {
        PreCon.greaterThan(maxSize, -2);

        _maxSize = maxSize;

        if (_maxSize > -1 && _maxSize < pool().length)
            resizePool(maxSize);
    }

    /**
     * Determine if the pool contains the specified element.
     *
     * @param element  The element.
     */
    public boolean contains(@Nullable Object element) {

        if (element == null)
            return false;

        for (int i=0; i < size(); i++) {
            if (element.equals(_pool[i]))
                return true;
        }
        return false;
    }

    /**
     * Clear all elements from the pool.
     */
    public void clear() {
        ArrayUtils.reset(_pool);
        _poolIndex = -1;
    }

    /**
     * Resize the pool to the number of pooled elements.
     */
    public void fitToSize() {
        resizePool(size());
    }

    /**
     * Resize the pool to the number of elements
     *
     * @param padding  The number of extra pool slots to pad the size with.
     */
    public void fitToSize(int padding) {
        PreCon.positiveNumber(padding);

        resizePool(size() + padding);
    }

    /**
     * Retrieve an element from the pool.
     *
     * <p>If the pool is empty, a new element is created.</p>
     *
     * @return An element or null if the pool is empty and a pooled element factory
     * is not being used.
     */
    @Nullable
    protected E retrieve() {

        if (_poolIndex == -1)
            return _elementFactory == null ? null : _elementFactory.create();

        E element = _pool[_poolIndex];
        _pool[_poolIndex] = null;

        _poolIndex--;

        return element;
    }

    /**
     * Recycle an element back into the pool.
     *
     * @param element  The element to recycle.
     *
     * @return  True if the element was added into the pool, false if there is
     * no room in the pool.
     */
    protected boolean recycle(E element) {
        PreCon.notNull(element);

        if (_maxSize > -1 && _poolIndex + 1 >= _maxSize)
            return false;

        if (_poolIndex + 1 >= _pool.length)
            expandPool();

        _poolIndex++;

        _pool[_poolIndex] = element;

        if (_recycleHandler != null)
            _recycleHandler.onRecycle(element);

        return true;
    }

    /**
     * Recycle an array of elements.
     *
     * @param elements  The elements to recycle.
     * @param start     The start index to of the elements to recycle.
     * @param length    The number of elements to recycle.
     *
     * @return  The number of elements recycled.
     */
    protected int recycleAll(E[] elements, int start, int length) {
        PreCon.notNull(elements, "elements");
        PreCon.positiveNumber(start, "start");
        PreCon.lessThan(start, elements.length, "start");
        PreCon.lessThanEqual(length, elements.length - start, "length");

        int room = _pool.length - size();

        if (maxSize() < 0 && room < length)
            resizePool(_pool.length - room + length);

        int recycleCount = 0;

        for (int i = 0; i < length; i++) {

            if (maxSize() > -1 && _poolIndex + i + 2 > maxSize())
                break;

            E element = elements[start + i];
            if (element == null)
                continue;

            _pool[i + _poolIndex + 1] = element;

            if (_recycleHandler != null)
                _recycleHandler.onRecycle(element);

            recycleCount++;
        }

        _poolIndex += recycleCount;

        return recycleCount;
    }

    /**
     * Expand the size of the pool.
     */
    protected void expandPool() {
        _pool = expand(_pool, _maxSize);
    }

    /**
     * Resize the pool to a specific size.
     */
    protected void resizePool(int size) {
        _pool = resize(_pool, size, _maxSize);
    }

    /**
     * Determine if a resize is possible given the current size of
     * an array and its maximum size.
     */
    protected boolean canResize(int size, int maxSize) {
        return maxSize < 0 || size <= _maxSize;
    }

    /**
     * Limit the resize size given the desired size and the maximum
     * size of an array.
     */
    protected int limitResize(int desiredSize, int maxSize) {
        if (maxSize > -1 && desiredSize > _maxSize)
            return maxSize;

        return desiredSize;
    }

    /**
     * Expand the size of a pool.
     */
    protected E[] expand(E[] array, int maxSize) {
        int newSize = array.length + (int)Math.max(10, Math.min(100, Math.ceil(array.length * 0.15D)));
        return resize(array, newSize, maxSize);
    }

    /**
     * Resize a pool to a specific size.
     */
    protected E[] resize(E[] array, int size, int maxSize) {

        if (!canResize(array.length, maxSize))
            return array;

        size = limitResize(size, maxSize);

        E[] newArray = ArrayUtils.newArray(getElementClass(), size);
        return ArrayUtils.copyFromStart(array, newArray);
    }


    /**
     * Get the pool array.
     */
    protected E[] pool() {
        return _pool;
    }

    /**
     * Get the index of the most recent pool element.
     */
    protected int getPoolIndex() {
        return _poolIndex;
    }

    /**
     * Get the element factory.
     */
    @Nullable
    protected IPoolElementFactory<E> getElementFactory() {
        return _elementFactory;
    }

    /**
     * Get the element recycle handler.
     */
    @Nullable
    protected IPoolRecycleHandler<E> getRecycler() {
        return _recycleHandler;
    }
}