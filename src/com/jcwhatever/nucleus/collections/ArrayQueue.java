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

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import javax.annotation.Nullable;

/**
 * Array based {@link Queue} implementation.
 *
 * <p>Does not allocate new objects in order to add items to the queue.</p>
 */
public class ArrayQueue<E> implements Queue<E> {

    private Object[] _queue;
    private int _size = 0;

    // offset from beginning of array where front element is located
    private int _frontIndex = 0;

    /**
     * Constructor.
     *
     * <p>Initial capacity of 15.</p>
     */
    public ArrayQueue() {
        this(15);
    }

    /**
     * Constructor.
     *
     * @param capacity  The initial capacity.
     */
    public ArrayQueue(int capacity) {
        _queue = new Object[capacity];
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

        for (int i= 0; i < _size; i++) {

            int index = translateIndex(i);

            if  (o == null) {

                if (_queue[index] == null)
                    return true;

                continue;
            }

            if (o.equals(_queue[index]))
                return true;
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            int index = 0;
            boolean nextInvoked = false;

            @Override
            public boolean hasNext() {
                return index < _size;
            }

            @Override
            public E next() {

                if (index >= _size)
                    throw new NoSuchElementException();

                nextInvoked = true;

                @SuppressWarnings("unchecked")
                E element = (E)_queue[translateIndex(index)];

                index++;

                return element;
            }

            @Override
            public void remove() {

                if (!nextInvoked)
                    throw new IllegalStateException("next must be invoked before remove");

                nextInvoked = false;

                _queue[translateIndex(index)] = null;

                for (int i = index; i < _size; i++) {

                    _queue[translateIndex(i)] = i < _size - 1
                            ? _queue[translateIndex(i + 1)]
                            : null;
                }

                index--;
                _size--;
            }
        };
    }

    @Override
    public Object[] toArray() {

        Object[] array = new Object[_size];

        Iterator<E> iterator = iterator();
        int index = 0;
        while (iterator.hasNext()) {
            array[index] = iterator.next();
            index++;
        }

        return array;
    }

    @Override
    public <T> T[] toArray(T[] a) {

        Iterator<E> iterator = iterator();
        int index = 0;
        while (iterator.hasNext()) {

            @SuppressWarnings("unchecked")
            T element = (T)iterator.next();

            a[index] = element;
            index++;
        }

        return a;
    }

    @Override
    public boolean add(@Nullable E e) {

        _size++;

        if (_size > _queue.length)
            expandCapacity();

        int index = translateIndex(_frontIndex + _size - 1);

        _queue[index] = e;

        return true;
    }

    @Override
    public boolean remove(@Nullable Object o) {

        boolean isCompacting = false;

        for (int i= 0; i < _size; i++) {

            int index = translateIndex(i);

            if (!isCompacting) {

                if (o == null) {

                    if (_queue[index] == null) {
                        isCompacting = true;
                    } else {
                        continue;
                    }
                }
                else if (o.equals(_queue[index])) {
                    isCompacting = true;
                }
            }

            if (isCompacting) {

                _queue[index] = i < _size - 1
                        ? _queue[translateIndex(i + 1)]
                        : null;
            }
        }

        if (isCompacting) {
            _size --;

            if (isEmpty())
                _frontIndex = 0;

            return true;
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        PreCon.notNull(c);

        for (Object obj : c) {
            if (!contains(obj))
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

        Iterator<E> iterator = iterator();
        while (iterator.hasNext()) {
            E element = iterator.next();

            if (!c.contains(element)) {
                iterator.remove();
                isModified = true;
            }
        }

        if (isEmpty())
            _frontIndex = 0;

        return isModified;
    }

    @Override
    public void clear() {
        ArrayUtils.reset(_queue);
        _size = 0;
        _frontIndex = 0;
    }

    @Override
    public boolean offer(@Nullable E e) {
        return add(e);
    }

    @Override
    public E remove() {

        if (isEmpty())
            throw new NoSuchElementException();

        @SuppressWarnings("unchecked")
        E element = (E)_queue[_frontIndex];

        _size--;

        _queue[_frontIndex] = null;
        _frontIndex = isEmpty() ? 0 : (_frontIndex + 1) % _queue.length;

        return element;
    }

    @Override
    @Nullable
    public E poll() {

        if (isEmpty())
            return null;

        return remove();
    }

    @Override
    public E element() {

        if (isEmpty())
            throw new NoSuchElementException();

        @SuppressWarnings("unchecked")
        E element = (E)_queue[_frontIndex];

        return element;
    }

    @Override
    @Nullable
    public E peek() {

        if (isEmpty())
            return null;

        return element();
    }

    private int translateIndex(int index) {
        return (index + _frontIndex) % _queue.length;
    }

    private void expandCapacity() {

        int newSize = _queue.length + (int)Math.max(10, (_queue.length * 0.2));
        ensureCapacity(newSize);
    }

    private void ensureCapacity(int size) {

        if (_queue.length >= size)
            return;

        Object[] newArray = new Object[size];

        int index = 0;
        for (E element : this) {
            newArray[index] = element;
            index++;
        }

        _frontIndex = 0;
        _queue = newArray;
    }
}
