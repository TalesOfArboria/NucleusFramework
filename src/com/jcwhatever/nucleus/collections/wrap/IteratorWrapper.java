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

import java.util.Iterator;
import java.util.concurrent.locks.ReadWriteLock;
import javax.annotation.Nullable;

/**
 * An abstract implementation of a synchronized {@link Iterator} wrapper.
 * The wrapper is optionally synchronized via a sync object or {@link ReadWriteLock}
 * passed in through the constructor.
 *
 * <p>The actual iterator is provided to the abstract implementation by
 * overriding and returning it from the {@link #iterator} method.</p>
 *
 * <p>In order to make using the wrapper as an extension of a iterator easier,
 * the {@link #onRemove} method is provided for optional override.</p>
 */
public abstract class IteratorWrapper<E> implements Iterator<E> {

    private final Object _sync;
    private final ReadWriteLock _lock;
    private E _current;

    /**
     * Constructor.
     */
    public IteratorWrapper() {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param sync  The synchronization object to use.
     */
    public IteratorWrapper(@Nullable Object sync) {

        _sync = sync;
        _lock = sync instanceof ReadWriteLock
                ? (ReadWriteLock)sync
                : null;
    }

    /**
     * Invoked before removing an element.
     *
     * @param element  The element that will be removed.
     *
     * <p>Not guaranteed to be called from a synchronized block.</p>
     *
     * <p>Intended to be optionally overridden by implementation.</p>
     *
     * @return  True to allow, false to throw an {@code UnsupportedOperationException}.
     */
    protected boolean onRemove(E element) { return true; }

    /**
     * Invoked after removing an element.
     *
     * @param element  The element that was removed.
     *
     * <p>Not guaranteed to be called from a synchronized block.</p>
     *
     * <p>Intended to be optionally overridden by implementation.</p>
     */
    protected void onRemoved(@SuppressWarnings("unused") E element) { }

    /**
     * Invoked from a synchronized block to get the encapsulated {@code Iterator}.
     */
    protected abstract Iterator<E> iterator();

    @Nullable
    public Object getSync() {
        return _sync;
    }

    public E getCurrent() {
        return _current;
    }

    @Override
    public boolean hasNext() {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return iterator().hasNext();
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return iterator().hasNext();
            }
        } else {
            return iterator().hasNext();
        }
    }

    @Override
    public E next() {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return _current = iterator().next();
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return _current = iterator().next();
            }
        } else {
            return _current = iterator().next();
        }
    }

    @Override
    public void remove() {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                removeSource();
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                removeSource();
            }
        } else {
            removeSource();
        }
    }

    private void removeSource() {
        if (!onRemove(_current))
            throw new UnsupportedOperationException();

        iterator().remove();

        onRemoved(_current);
    }
}
