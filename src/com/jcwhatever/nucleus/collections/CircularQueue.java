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

import com.jcwhatever.nucleus.utils.PreCon;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;

/**
 * A {@code Deque} implementation whose head and tail are connected. The head and tail
 * can be moved by invoking the {@code #next} or {@code #prev} methods.
 */
public class CircularQueue<E> implements Deque<E> {

    private Entry _current; // current is head, left is tail
    private volatile int _size;

    /**
     * Moves the head and tail of the deque to the right (direction towards tail).
     *
     * @return  The new head element.
     *
     * @throws  java.util.NoSuchElementException if the collection is empty.
     */
    @Nullable
    public E next() {

        if (_size == 0)
            throw new NoSuchElementException();

        _current = _current.right;
        return _current.value;
    }

    /**
     * Moves the head and tail of the deque to the left. (direction towards head)
     *
     * @return  The new head element.
     *
     * @throws java.util.NoSuchElementException if the collection is empty.
     */
    @Nullable
    public E prev() {

        if (_size == 0)
            throw new NoSuchElementException();

        _current = _current.left;
        return _current.value;
    }

    @Override
    public void addFirst(@Nullable E value) {

        Entry entry = new Entry(value);

        addEntry(entry);

        _current = entry;
    }

    @Override
    public void addLast(@Nullable E value) {

        Entry entry = new Entry(value);

        addEntry(entry);
    }

    @Override
    public boolean offerFirst(@Nullable E value) {
        addFirst(value);
        return true;
    }

    @Override
    public boolean offerLast(@Nullable E value) {
        addLast(value);
        return true;
    }

    @Override
    @Nullable
    public E removeFirst() {

        if (_size == 0)
            throw new NoSuchElementException("Deque is empty.");

        return pollFirst();
    }

    @Override
    public E removeLast() {

        if (_size == 0)
            throw new NoSuchElementException("Deque is empty.");

        return pollLast();
    }

    @Override
    @Nullable
    public E pollFirst() {

        if (_size == 0)
            return null;

        Entry removed = _current;

        if (_size == 1) {
            _current = null;
            _size = 0;
            return removed.value;
        }

        _current.left.right = _current.right;
        _current.right.left = _current.left;
        _current = _current.right;

        _size--;

        return removed.value;
    }

    @Override
    @Nullable
    public E pollLast() {

        if (_size == 0)
            return null;

        Entry removed;
        if (_size == 1) {
            removed = _current;
            _current = null;
            _size = 0;

            return removed.value;
        }

        removed = _current.left;

        removed.left.right = _current;
        _current.left = removed.left;

        _size--;

        return removed.value;
    }

    @Override
    public E getFirst() {
        if (_size == 0)
            throw new NoSuchElementException("Deque is empty.");

        return peekFirst();
    }

    @Override
    public E getLast() {
        if (_size == 0)
            throw new NoSuchElementException("Deque is empty.");

        return peekLast();
    }

    @Override
    @Nullable
    public E peekFirst() {

        if (_size == 0)
            return null;

        return _current.value;
    }

    @Override
    public E peekLast() {

        if (_size == 0)
            return null;

        return _current.left.value;
    }

    @Override
    public boolean removeFirstOccurrence(@Nullable Object o) {

        Iterator<E> iterator = new ItrRight();
        return removeOccurrence(o, iterator);
    }

    @Override
    public boolean removeLastOccurrence(@Nullable Object o) {

        Iterator<E> iterator = new ItrLeft();
        return removeOccurrence(o, iterator);
    }

    @Override
    public boolean add(@Nullable E value) {
        addLast(value);
        return true;
    }

    @Override
    public boolean offer(@Nullable E value) {
        return offerLast(value);
    }

    @Override
    @Nullable
    public E remove() {
        return removeFirst();
    }

    @Override
    @Nullable
    public E poll() {
        return pollFirst();
    }

    @Override
    @Nullable
    public E element() {
        if (_size == 0)
            throw new NoSuchElementException("Deque is empty.");

        return peek();
    }

    @Override
    @Nullable
    public E peek() {
        return peekFirst();
    }

    @Override
    public void push(@Nullable E value) {
        addFirst(value);
    }

    @Override
    @Nullable
    public E pop() {
        return removeFirst();
    }

    @Override
    public boolean remove(@Nullable Object o) {
        return removeFirstOccurrence(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        PreCon.notNull(c);

        for (Object o : c) {
            if (!contains(o))
                return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        PreCon.notNull(c);

        boolean isChanged = false;
        for (E value : c) {
            isChanged =  add(value) || isChanged;
        }
        return isChanged;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        PreCon.notNull(c);

        boolean isChanged = false;
        for (Object o : c) {
            isChanged = remove(o) || isChanged;
        }
        return isChanged;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        PreCon.notNull(c);

        boolean isChanged = false;

        Iterator<E> iterator = new ItrRight();
        while (iterator.hasNext()) {
            E element = iterator.next();

            if (!c.contains(element)) {
                iterator.remove();
                isChanged = true;
            }
        }

        return isChanged;
    }

    @Override
    public void clear() {
        _size = 0;
        _current = null;
    }

    @Override
    public boolean contains(@Nullable Object o) {
        Iterator<E> iterator = new ItrRight();
        while (iterator.hasNext()) {
            E element = iterator.next();

            if (o == null && element == null)
                return true;

            if (o != null && o.equals(element))
                return true;
        }
        return false;
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
    public Iterator<E> iterator() {
        return new ItrRight();
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[_size];

        Iterator<E> iterator = new ItrRight();
        int index = 0;
        while (iterator.hasNext()) {
            array[index] = iterator.next();
            index++;
        }

        return array;
    }

    @Override
    public <T1> T1[] toArray(T1[] array) {

        Iterator<E> iterator = new ItrRight();
        int index = 0;
        while (iterator.hasNext()) {

            @SuppressWarnings("unchecked")
            T1 value = (T1)iterator.next();

            array[index] = value;
            index++;
        }

        return array;
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new ItrLeft();
    }

    private void addEntry (Entry entry) {

        if (_size == 0) {
            _current = entry;
            entry.left = entry;
            entry.right = entry;
        }
        else {
            entry.left = _current.left;
            entry.right = _current;
            _current.left.right = entry;
            _current.left = entry;
        }
        _size++;
    }

    private boolean removeOccurrence(Object o, Iterator<E> iterator) {
        while (iterator.hasNext()) {
            E value = iterator.next();

            if (o == null && value == null) {
                iterator.remove();
                return true;
            }
            else if (o != null && o.equals(value)) {
                iterator.remove();
                return true;
            }
        }

        return false;
    }

    private class ItrRight implements Iterator<E> {

        Entry next = null;
        int steps = 0;

        @Override
        public boolean hasNext() {
            return _size > 0 && steps < _size;
        }

        @Override
        public E next() {
            steps++;
            if (next == null) {
                next = _current.right;
                return _current.value;
            }

            next = next.right;
            return next.left.value;
        }

        @Override
        public void remove() {
            steps--;
            if (_size == 1) {
                _current = null;
                _size = 0;
                next = null;
            }
            else {

                if (next.left == _current) {
                    _current = next;
                }

                next.left.left.right = next;
                next.left = next.left.left;
                _size--;
            }
        }
    }

    private class ItrLeft implements Iterator<E> {

        Entry next;
        int steps = 0;

        @Override
        public boolean hasNext() {
            return _size > 0 && steps < _size;
        }

        @Override
        public E next() {
            steps++;
            if (next == null) {
                next = _current.left.left;
                return _current.left.value;
            }

            next = next.left;
            return next.right.value;
        }

        @Override
        public void remove() {
            steps--;
            if (_size == 1) {
                _current = null;
                _size = 0;
                next = null;
            }
            else {

                if (next.right == _current) {
                    _current = next;
                }

                next.right.right.left = next;
                next.right = next.right.right;
                _size--;
            }
        }
    }

    private class Entry {
        Entry left;
        E value;
        Entry right;

        Entry(E value) {
            this.value = value;
        }
    }
}
