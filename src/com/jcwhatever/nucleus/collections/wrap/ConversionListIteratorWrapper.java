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

import java.util.ListIterator;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * An abstract implementation of a {@link ListIterator} wrapper
 * designed to convert between the encapsulated list element type and
 * an externally visible type. The wrapper is optionally synchronized via a sync
 * object or {@link java.util.concurrent.locks.ReadWriteLock} passed into the
 * constructor using a {@link SyncStrategy}.
 *
 * <p>If the iterator is synchronized, the sync object must be externally locked while
 * in use. Otherwise, a {@link java.lang.IllegalStateException} will be thrown.</p>
 *
 * <p>The actual list is provided to the abstract implementation by
 * overriding and returning it from the {@link #iterator} method.</p>
 *
 * <p>The {@link #convert} and {@link #unconvert} abstract methods are used to
 * convert between the internal list element type and the external type.</p>
 *
 * <p>When using the {@link #unconvert} method, a {@link ClassCastException} can be
 * thrown to indicate the value cannot be converted. The exception is caught and handled
 * where it is appropriate to do so.</p>
 */
public abstract class ConversionListIteratorWrapper<E, T> implements ListIterator<E> {

    protected final Object _sync;
    protected final ReadWriteLock _lock;
    protected final SyncStrategy _strategy;
    private T _current;

    /**
     * Constructor.
     */
    public ConversionListIteratorWrapper() {
        this(SyncStrategy.NONE);
    }

    /**
     * Constructor.
     *
     * @param strategy  The synchronization strategy to use.
     */
    public ConversionListIteratorWrapper(SyncStrategy strategy) {
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
    protected boolean onRemove(@SuppressWarnings("unused") T element) { return true; }

    /**
     * Invoked after adding an element.
     *
     * @param element  The element that was added.
     *
     * <p>Not guaranteed to be called from a synchronized block.</p>
     *
     * <p>Intended to be optionally overridden by implementation.</p>
     */
    protected void onAdded(@SuppressWarnings("unused") T element) {}

    /**
     * Convert an internal element type to the external type.
     *
     * @param internal  The internal type to convert.
     *
     * @return  An external type instance.
     */
    protected abstract E convert(T internal);

    /**
     * Unconvert an external type to an internal element type.
     *
     * @param external  The external type to unconvert.
     *
     * @return  An internal type instance.
     *
     * @throws java.lang.ClassCastException if the external
     * type cannot be converted.
     */
    protected abstract T unconvert(Object external);

    /**
     * Invoked from a synchronized block to get the encapsulated {@link Iterator}.
     */
    protected abstract ListIterator<T> iterator();

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
                return convert(_current = iterator().next());
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else {

            if (_sync != null)
                IteratorWrapper.assertIteratorLock(_sync);

            return convert(_current = iterator().next());
        }
    }

    @Override
    public boolean hasPrevious() {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return iterator().hasPrevious();
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else {

            if (_sync != null)
                IteratorWrapper.assertIteratorLock(_sync);

            return iterator().hasPrevious();
        }
    }

    @Override
    public E previous() {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return convert(_current = iterator().previous());
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else {

            if (_sync != null)
                IteratorWrapper.assertIteratorLock(_sync);

            return convert(_current = iterator().previous());
        }
    }

    @Override
    public int nextIndex() {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return iterator().nextIndex();
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else {

            if (_sync != null)
                IteratorWrapper.assertIteratorLock(_sync);

            return iterator().nextIndex();
        }
    }

    @Override
    public int previousIndex() {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return iterator().previousIndex();
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else {

            if (_sync != null)
                IteratorWrapper.assertIteratorLock(_sync);

            return iterator().previousIndex();
        }
    }

    @Override
    public void remove() {
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

    @Override
    public void set(E e) {
        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                setSource(e);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else {

            if (_sync != null)
                IteratorWrapper.assertIteratorLock(_sync);

            setSource(e);
        }
    }

    private void setSource(E e) {
        if (!onRemove(_current))
            throw new UnsupportedOperationException();

        T t = unconvert(e);
        iterator().set(t);
        _current = t;
        onAdded(t);
    }

    @Override
    public void add(E e) {
        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                addSource(e);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else {

            if (_sync != null)
                IteratorWrapper.assertIteratorLock(_sync);

            addSource(e);
        }
    }

    private void addSource(E e) {
        T t = unconvert(e);
        iterator().add(t);
        onAdded(t);
    }
}

