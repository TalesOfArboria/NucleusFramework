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

package com.jcwhatever.nucleus.collections.wrap;

import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * An abstract implementation of a synchronized {@link Deque} wrapper. The wrapper is
 * optionally synchronized via a sync object or {@link ReadWriteLock} passed into the
 * constructor using a {@link SyncStrategy}.
 *
 * <p>If the deque is synchronized, the sync object must be externally locked while
 * the iterator is in use. Otherwise, a {@link java.lang.IllegalStateException} will
 * be thrown.</p>

 * <p>The actual collection is provided to the abstract implementation by
 * overriding and returning it from the {@link #queue} method.</p>
 *
 * <p>In order to make using the wrapper as an extension of a collection easier,
 * several protected methods are provided for optional override. These methods
 * are provided by the superclass {@link CollectionWrapper}.
 */
public abstract class DequeWrapper<E> extends QueueWrapper<E> implements Deque<E> {

    /**
     * Constructor.
     *
     * <p>No synchronization.</p>
     */
    public DequeWrapper() {
        this(SyncStrategy.NONE);
    }

    /**
     * Constructor.
     *
     * @param strategy  The synchronization strategy to use.
     */
    public DequeWrapper(SyncStrategy strategy) {
        super(strategy);
    }

    /**
     * Invoked from a synchronized block to get the
     * encapsulated {@code Deque}.
     */
    @Override
    protected abstract Deque<E> queue();

    @Override
    public void addFirst(E e) {
        if (!onPreAdd(e))
            throw new UnsupportedOperationException();

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                queue().addFirst(e);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                queue().addFirst(e);
            }
        } else {
            queue().addFirst(e);
        }

        onAdded(e);
    }

    @Override
    public void addLast(E e) {

        if (!onPreAdd(e))
            throw new UnsupportedOperationException();

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                queue().addLast(e);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                queue().addLast(e);
            }
        } else {
            queue().addLast(e);
        }

        onAdded(e);
    }

    @Override
    public boolean offerFirst(E e) {

        if (!onPreAdd(e))
            return false;

        boolean isAdded;

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                isAdded = queue().offerFirst(e);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                isAdded = queue().offerFirst(e);
            }
        } else {
            isAdded = queue().offerFirst(e);
        }

        if (isAdded)
            onAdded(e);

        return isAdded;
    }

    @Override
    public boolean offerLast(E e) {
        if (!onPreAdd(e))
            return false;

        boolean isAdded;

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                isAdded = queue().offerLast(e);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                isAdded = queue().offerLast(e);
            }
        } else {
            isAdded = queue().offerLast(e);
        }

        if (isAdded)
            onAdded(e);

        return isAdded;
    }

    @Override
    public E removeFirst() {
        E element;

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                element = removeFirstSource();
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                element = removeFirstSource();
            }
        } else {
            element = removeFirstSource();
        }

        onRemoved(element);

        return element;
    }

    private E removeFirstSource() {

        E element = queue().peekFirst();

        if (element == null)
            return null;

        if (!onPreRemove(element))
            throw new UnsupportedOperationException();

        queue().removeFirst();

        return element;
    }

    @Override
    public E removeLast() {
        E element;

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                element = removeLastSource();
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                element = removeLastSource();
            }
        } else {
            element = removeLastSource();
        }

        onRemoved(element);

        return element;
    }

    private E removeLastSource() {
        E element = queue().peekLast();

        if (element == null)
            return null;

        if (!onPreRemove(element))
            throw new UnsupportedOperationException();

        queue().removeLast();

        return element;
    }

    @Override
    public E pollFirst() {
        E element;

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                element = pollFirstSource();
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                element = pollFirstSource();
            }
        } else {
            element = pollFirstSource();
        }

        onRemoved(element);

        return element;
    }

    private E pollFirstSource() {
        E element = queue().peekFirst();

        if (element == null) {
            queue().pollFirst();
            return null;
        }

        if (!onPreRemove(element))
            return null;

        queue().pollFirst();

        return element;
    }

    @Override
    public E pollLast() {
        E element;

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                element = pollLastSource();
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                element = pollLastSource();
            }
        } else {
            element = pollLastSource();
        }

        onRemoved(element);

        return element;
    }

    private E pollLastSource() {
        E element = queue().peekLast();

        if (element == null) {
            queue().pollLast();
            return null;
        }

        if (!onPreRemove(element))
            return null;

        queue().pollLast();

        return element;
    }

    @Override
    public E getFirst() {

        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return queue().getFirst();
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return queue().getFirst();
            }
        } else {
            return queue().getFirst();
        }
    }

    @Override
    public E getLast() {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return queue().getLast();
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return queue().getLast();
            }
        } else {
            return queue().getLast();
        }
    }

    @Override
    public E peekFirst() {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return queue().peekFirst();
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return queue().peekFirst();
            }
        } else {
            return queue().peekFirst();
        }
    }

    @Override
    public E peekLast() {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return queue().peekLast();
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return queue().peekLast();
            }
        } else {
            return queue().peekLast();
        }
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {

        if (!onPreRemove(o))
            return false;

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                return removeFirstOccurrenceSource(o);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return removeFirstOccurrenceSource(o);
            }
        } else {
            return removeFirstOccurrenceSource(o);
        }
    }

    private boolean removeFirstOccurrenceSource(Object o) {
        if (removeFirstOccurrence(o)) {
            onRemoved(o);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeLastOccurrence(Object o) {

        if (!onPreRemove(o))
            return false;

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                return removeLastOccurenceSource(o);
            } finally {
                _lock.writeLock().unlock();
            }
        } else if (_sync != null) {
            synchronized (_sync) {
                return removeLastOccurenceSource(o);
            }
        } else {
            return removeLastOccurenceSource(o);
        }
    }

    private boolean removeLastOccurenceSource(Object o) {
        if (removeLastOccurrence(o)) {
            onRemoved(o);
            return true;
        }
        return false;
    }

    @Override
    public void push(E e) {

        if (!onPreAdd(e))
            throw new RuntimeException();

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                queue().push(e);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                queue().push(e);
            }
        } else {
            queue().push(e);
        }

        onAdded(e);
    }


    @Override
    public E pop() {

        E element;

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                element = popSource();
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                element = popSource();
            }
        } else {
            element = popSource();
        }

        onRemoved(element);

        return element;
    }

    private E popSource() {
        E element = queue().peek();
        if (element == null)
            throw new NoSuchElementException();

        if (!onPreRemove(element))
            throw new UnsupportedOperationException();

        queue().pop();
        return element;
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new IteratorWrapper<E>(_strategy) {

            Iterator<E> iterator = queue().descendingIterator();

            @Override
            protected boolean onRemove(E removed) {
                if (!onPreRemove(removed))
                    return false;

                onRemoved(removed);
                return true;
            }

            @Override
            protected Iterator<E> iterator() {
                return iterator;
            }
        };
    }
}
