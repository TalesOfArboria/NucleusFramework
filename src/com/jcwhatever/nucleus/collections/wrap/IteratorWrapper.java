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

import com.jcwhatever.nucleus.utils.PreCon;

import java.util.Iterator;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * An abstract implementation of a synchronized {@link Iterator} wrapper.
 * The wrapper is optionally synchronized via a sync object or {@link ReadWriteLock}
 * passed into the constructor using a {@link SyncStrategy}.
 *
 * <p>If the iterator is synchronized, the sync object must be externally locked while
 * the iterator is in use. Otherwise, a {@link java.lang.IllegalStateException} will
 * be thrown.</p>
 *
 * <p>The actual iterator is provided to the abstract implementation by
 * overriding and returning it from the {@link #iterator} method.</p>
 *
 * <p>In order to make using the wrapper as an extension of a iterator easier,
 * the {@link #onRemove} method is provided for optional override.</p>
 */
public abstract class IteratorWrapper<E> implements Iterator<E> {

    /**
     * Ensure the current thread has a lock on the specified object
     * for the purpose of iterating the collection.
     *
     * @param object  The object.
     */
    public static void assertIteratorLock(Object object) {
        if (!Thread.holdsLock(object)) {
            throw new IllegalStateException("Iteration must be performed while locked on the " +
                    "object returned from the method #iteratorSync");
        }
    }

    protected final Object _sync;
    protected final ReadWriteLock _lock;
    protected final SyncStrategy _strategy;

    private E _current;

    /**
     * Constructor.
     *
     * <p>No synchronization.</p>
     */
    public IteratorWrapper() {
        this(SyncStrategy.NONE);
    }

    /**
     * Constructor.
     *
     * @param strategy  The synchronization strategy to use.
     */
    public IteratorWrapper(SyncStrategy strategy) {
        PreCon.notNull(strategy);

        _sync = strategy.getSync(this);
        _strategy = new SyncStrategy(_sync);
        _lock = _sync instanceof ReadWriteLock
                ? (ReadWriteLock)_sync
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
     * @return  True to allow, false to throw an {@link UnsupportedOperationException}.
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
     * Invoked from a synchronized block to get the encapsulated {@link Iterator}.
     */
    protected abstract Iterator<E> iterator();

    /**
     * Get the current element of the iterator.
     */
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
        else {

            if (_sync != null)
                IteratorWrapper.assertIteratorLock(_sync);

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
        else {

            if (_sync != null)
                IteratorWrapper.assertIteratorLock(_sync);

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
        else {

            if (_sync != null)
                IteratorWrapper.assertIteratorLock(_sync);

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
