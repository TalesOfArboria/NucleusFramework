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

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * An abstract implementation of a synchronized {@link Queue} wrapper. The wrapper is
 * optionally synchronized via a sync object or {@link ReadWriteLock} passed into the
 * constructor using a {@link SyncStrategy}.
 *
 * <p>If the queue is synchronized, the sync object must be externally locked while
 * the iterator is in use. Otherwise, a {@link java.lang.IllegalStateException} will
 * be thrown.</p>
 *
 * <p>The actual collection is provided to the abstract implementation by
 * overriding and returning it from the {@link #queue} method.</p>
 *
 * <p>In order to make using the wrapper as an extension of a collection easier,
 * several protected methods are provided for optional override. These methods
 * are provided by the superclass {@link CollectionWrapper}.
 */
public abstract class QueueWrapper<E> extends CollectionWrapper<E> implements Queue<E> {

    /**
     * Constructor.
     *
     * <p>No synchronization.</p>
     */
    public QueueWrapper() {
        this(SyncStrategy.NONE);
    }

    /**
     * Constructor.
     *
     * @param strategy  The synchronization object to use.
     */
    public QueueWrapper(SyncStrategy strategy) {
        super(strategy);
    }

    /**
     * Invoked from a synchronized block to get the
     * encapsulated {@link Queue}.
     */
    protected abstract Queue<E> queue();

    @Override
    protected Collection<E> collection() {
        return queue();
    }

    @Override
    public boolean offer(E e) {

        if (!onPreAdd((e)))
            return false;

        boolean isAdded;

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                isAdded = queue().offer(e);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                isAdded = queue().offer(e);
            }
        } else {
            isAdded = queue().offer(e);
        }

        if (isAdded)
            onAdded(e);

        return isAdded;
    }

    @Override
    public E remove() {

        if (!onPreRemove(peek()))
            throw new UnsupportedOperationException();

        E removed;

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                removed = queue().remove();
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                removed = queue().remove();
            }
        } else {
            removed = queue().remove();
        }

        onRemoved(removed);

        return removed;
    }

    @Override
    public E poll() {

        E element;

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                element = pollSource();
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                element = pollSource();
            }
        } else {
            element = pollSource();
        }

        onRemoved(element);

        return element;
    }

    private E pollSource() {
        E element = queue().peek();

        if (element == null)
            return null;

        if (!onPreRemove(element))
            throw new UnsupportedOperationException();

        queue().poll();

        return element;
    }

    @Override
    public E element() {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return queue().element();
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return queue().element();
            }
        } else {
            return queue().element();
        }
    }

    @Override
    public E peek() {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return queue().peek();
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return queue().peek();
            }
        } else {
            return queue().peek();
        }
    }
}
