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

import com.sun.istack.internal.Nullable;

import java.util.Collection;
import java.util.Queue;

/**
 * An abstract implementation of a {@link Queue} wrapper
 * designed to convert between the encapsulated queue element type and
 * an externally visible type. The wrapper is optionally synchronized via a sync
 * object or {@link java.util.concurrent.locks.ReadWriteLock} passed into the constructor.
 *
 * <p>The actual queue is provided to the abstract implementation by
 * overriding and returning it from the {@link #queue} method.</p>
 *
 * <p>The {@link #convert} and {@link #unconvert} abstract methods are used to
 * convert between the internal queue element type and the external type.</p>
 *
 * <p>When using the {@link #unconvert} method, a {@link ClassCastException} can be
 * thrown to indicate the value cannot be converted. The exception is caught and handled
 * where it is appropriate to do so.</p>
 */
public abstract class ConversionQueueWrapper<E, T>
        extends ConversionCollectionWrapper<E, T> implements Queue<E> {

    /**
     * Constructor.
     */
    public ConversionQueueWrapper() {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param sync  The synchronization object to use.
     */
    public ConversionQueueWrapper(@Nullable Object sync) {
        super(sync);
    }

    /**
     * Invoked from a synchronized block to get the encapsulated {@code Queue}.
     */
    protected abstract Queue<T> queue();

    @Override
    protected Collection<T> collection() {
        return queue();
    }

    @Override
    public boolean offer(E e) {
        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                return offerSource(e);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return offerSource(e);
            }
        } else {
            return offerSource(e);
        }
    }

    private boolean offerSource(E e) {

        T internal = unconvert(e);

        if (queue().offer(internal)) {
            onAdded(internal);
            return true;
        }
        return false;
    }

    @Override
    public E remove() {
        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                return removeSource();
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return removeSource();
            }
        } else {
            return removeSource();
        }
    }

    private E removeSource() {
        T internal = queue().remove();
        if (internal == null)
            return null;

        onRemoved(internal);
        return convert(internal);
    }

    @Override
    public E poll() {
        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                return pollSource();
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return pollSource();
            }
        } else {
            return pollSource();
        }
    }

    private E pollSource() {
        T internal = queue().poll();
        if (internal == null)
            return null;

        onRemoved(internal);
        return convert(internal);
    }

    @Override
    public E element() {
        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                return convert(queue().element());
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return convert(queue().element());
            }
        } else {
            return convert(queue().element());
        }
    }

    @Override
    public E peek() {
        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                return convert(queue().peek());
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return convert(queue().peek());
            }
        } else {
            return convert(queue().peek());
        }
    }
}