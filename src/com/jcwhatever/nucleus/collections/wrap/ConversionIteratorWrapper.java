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
import javax.annotation.Nullable;

/**
 * An abstract implementation of a {@link Iterator} wrapper
 * designed to convert between the encapsulated collection element type and
 * an externally visible type. The wrapper is optionally synchronized via a sync
 * object or {@link java.util.concurrent.locks.ReadWriteLock} passed into the
 * constructor using a {@link SyncStrategy}.
 *
 * <p>If the iterator is synchronized, the sync object must be externally locked while
 * in use. Otherwise, a {@link java.lang.IllegalStateException} will be thrown.</p>
 *
 * <p>The actual iterator is provided to the abstract implementation by
 * overriding and returning it from the {@link #iterator} method.</p>
 *
 * <p>The {@link #convert} method is used to convert between the internal list element
 * type and the external type.</p>
 */
public abstract class ConversionIteratorWrapper<E, T> implements Iterator<E> {

    protected final Object _sync;
    protected final ReadWriteLock _lock;
    protected final SyncStrategy _strategy;

    private T _current;

    /**
     * Constructor.
     */
    public ConversionIteratorWrapper() {
        this(SyncStrategy.NONE);
    }

    /**
     * Constructor.
     *
     * @param strategy  The synchronization strategy to use.
     */
    public ConversionIteratorWrapper(SyncStrategy strategy) {
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
     * @return  True to allow, false to throw an {@code UnsupportedOperationException}.
     */
    protected boolean onRemove(@SuppressWarnings("unused") T element) { return true; }

    /**
     * Convert an internal list type to the external type.
     *
     * @param internal  The internal type to convert.
     *
     * @return  An external type instance.
     */
    protected abstract E convert(T internal);

    /**
     * Invoked from a synchronized block to get the encapsulated {@code Iterator}.
     */
    protected abstract Iterator<T> iterator();

    /**
     * Get the current iterator internal element.
     */
    @Nullable
    public T getCurrent() {
        return _current;
    }

    private boolean _isNextInvoked;

    @Override
    public boolean hasNext() {

        _isNextInvoked = false;

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

        _isNextInvoked = true;

        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return nextSource();
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else {

            if (_sync != null)
                IteratorWrapper.assertIteratorLock(_sync);

            return nextSource();
        }
    }

    private E nextSource() {
        _current = iterator().next();
        return convert(_current);
    }

    @Override
    public void remove() {

        if (!_isNextInvoked)
            throw new IllegalStateException("#next method must be invoked before invoking #remove method");

        _isNextInvoked = false;

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                removeSource();
            }
            finally {
                _lock.writeLock().unlock();
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
    }
}
