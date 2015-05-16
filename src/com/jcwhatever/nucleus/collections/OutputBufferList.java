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

import com.jcwhatever.nucleus.utils.ArrayUtils;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

/**
 * An array based list used for buffering an output collection.
 *
 * <p>Optimized for repeated add and clear operations.</p>
 */
public class OutputBufferList<E> extends AbstractList<E> implements List<E> {

    private Object[] _list;
    private int _size;
    private boolean _isPendingClear;
    private int _preClearSize;

    /**
     * Constructor.
     *
     * <p>Initial capacity is 10.</p>
     */
    public OutputBufferList() {
        this(10);
    }

    /**
     * Constructor.
     *
     * @param capacity  The initial capacity.
     */
    public OutputBufferList(int capacity) {
        _list = new Object[capacity];
    }

    @Override
    public int size() {
        return _size;
    }

    @Override
    public boolean isEmpty() {
        return _size == 0;
    }

    @Override
    public boolean contains(@Nullable Object o) {

        for (Object element : _list) {
            if (o == null) {

                if (element == null)
                    return true;
            }
            else if (o.equals(element)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Object[] toArray() {
        return ArrayUtils.copyFromStart(_list, new Object[_size]);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        PreCon.notNull(a);

        for (int i=0; i < a.length && i < _size; i++) {

            @SuppressWarnings("unchecked")
            T element = (T)_list[i];

            a[i] = element;
        }

        return a;
    }

    @Override
    public boolean add(@Nullable E e) {
        ensureCapacity(_size + 1);

        _list[_size] = e;
        _size++;

        return true;
    }

    @Override
    public boolean remove(@Nullable Object o) {

        boolean isCompacting = false;

        for (int i=0; i < _list.length; i++) {

            Object element = _list[i];

            if (!isCompacting) {
                if (o == null) {

                    if (element == null) {
                        isCompacting = true;
                    }
                } else if (o.equals(element)) {
                    isCompacting = true;
                }
            }

            if (isCompacting) {
                _list[i] = i < _size - 1 ? _list[i + 1] : null;
            }
        }

        if (isCompacting) {
            _size--;
            return true;
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        PreCon.notNull(c);

        for (Object obj : c) {

            boolean contains = false;

            for (int i=0; i < _size; i++) {
                if (obj == null) {
                    if (_list[i] == null) {
                        contains = true;
                        break;
                    }
                }
                else if (obj.equals(_list[i])) {
                    contains = true;
                    break;
                }
            }

            if (!contains)
                return false;
        }

        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        PreCon.notNull(c);
        ensureCapacity(_size + c.size());

        for (E element : c) {
            add(element);
        }

        commitClear();
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        PreCon.notNull(c);
        PreCon.positiveNumber(index, "index", IndexOutOfBoundsException.class);
        PreCon.lessThanEqual(index, _size, "index", IndexOutOfBoundsException.class);
        ensureCapacity(_size + c.size());

        // displace current elements
        int displacement = c.size();
        displace(index, displacement);

        for (E element : c) {
            _list[index] = element;
            index++;
        }

        _size += displacement;
        commitClear();

        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        PreCon.notNull(c);

        boolean isModified = false;

        for (Object obj : c) {
            isModified = remove(obj) || isModified;
        }

        return isModified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        PreCon.notNull(c);

        boolean isModified = false;

        for (int i=0; i < _size; i++) {
            if (!c.contains(_list[i])) {
                remove(i);
                i--;
                isModified = true;
            }
        }

        return isModified;
    }

    @Override
    public void clear() {
        commitClear();
        _preClearSize = _size;
        _size = 0;
        _isPendingClear = true;
    }

    @Override
    public E get(int index) {
        PreCon.positiveNumber(index, "index", IndexOutOfBoundsException.class);
        PreCon.lessThan(index, _size, "index", IndexOutOfBoundsException.class);

        @SuppressWarnings("unchecked")
        E element = (E)_list[index];

        return element;
    }

    @Override
    public E set(int index, @Nullable E element) {
        PreCon.positiveNumber(index, "index", IndexOutOfBoundsException.class);
        PreCon.lessThan(index, _size, "index", IndexOutOfBoundsException.class);

        @SuppressWarnings("unchecked")
        E previous = (E)_list[index];

        _list[index] = element;

        return previous;
    }

    @Override
    public void add(int index, @Nullable E element) {
        PreCon.positiveNumber(index, "index", IndexOutOfBoundsException.class);
        PreCon.lessThanEqual(index, _size, "index", IndexOutOfBoundsException.class);
        ensureCapacity(_size + 1);

        // displace current elements
        displace(index, 1);

        _list[index] = element;
        _size++;
    }

    @Override
    public E remove(int index) {
        PreCon.positiveNumber(index, "index", IndexOutOfBoundsException.class);
        PreCon.lessThan(index, _size, "index", IndexOutOfBoundsException.class);

        @SuppressWarnings("unchecked")
        E previous = (E)_list[index];

        for (int i=index; i <= _size; i++) {
            _list[i] = i < _size - 1 ? _list[i + 1] : null;
        }
        _size--;

        return previous;
    }

    @Override
    public int indexOf(@Nullable Object o) {

        for (int i=0; i < _size; i++) {
            if (o == null) {
                if (_list[i] == null)
                    return i;
            }
            else if (o.equals(_list[i])) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public int lastIndexOf(@Nullable Object o) {

        for (int i= _size - 1; i >= 0; i--) {
            if (o == null) {
                if (_list[i] == null)
                    return i;
            }
            else if (o.equals(_list[i])) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Ensure the internal array has at least the specified capacity.
     *
     * @param capacity  The capacity to ensure.
     */
    public void ensureCapacity(int capacity) {

        if (_list.length >= capacity)
            return;

        Object[] list = new Object[capacity];
        System.arraycopy(_list, 0, list, 0, _size);

        _list = list;
        _isPendingClear = false;
    }

    /**
     * Fully clear all elements immediately.
     */
    public void fullClear() {
        for (int i=0; i < _list.length; i++) {
            _list[i] = null;
        }
        _size = 0;
        _isPendingClear = false;
    }

    /**
     * Commit a {@link #clear} operation after adding new elements.
     *
     * <p>Is automatically invoked after {@link #addAll} is invoked.</p>
     *
     * <p>When {@link #clear} is invoked, the previous clear operation is committed.</p>
     */
    public void commitClear() {
        if (!_isPendingClear)
            return;

        if (_preClearSize <= _size)
            return;

        for (int i=_size; i < _preClearSize - 1; i++) {
            _list[i] = null;
        }

        _isPendingClear = false;
    }

    // does not null displacement gap
    private void displace(int index, int amount) {

        if (amount >= _size - index) {
            System.arraycopy(_list, index, _list, index + amount, _size - index);
        }
        else {

            Object[] buffer = new Object[amount];
            System.arraycopy(_list, index, buffer, 0, buffer.length);
            System.arraycopy(buffer, 0, _list, index + amount, buffer.length);
        }
    }
}
