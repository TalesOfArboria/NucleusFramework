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

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * An abstract implementation of a {@link List} wrapper
 * designed to convert between the encapsulated list element type and
 * an externally visible type. The wrapper is optionally synchronized via a sync
 * object or {@link java.util.concurrent.locks.ReadWriteLock} passed into the
 * constructor using a {@link SyncStrategy}.
 *
 * <p>If the list is synchronized, the sync object must be externally locked while
 * the iterator is in use. Otherwise, a {@link java.lang.IllegalStateException} will
 * be thrown.</p>
 *
 * <p>The actual list is provided to the abstract implementation by
 * overriding and returning it from the {@link #list} method.</p>
 *
 * <p>The {@link #convert} and {@link #unconvert} abstract methods are used to
 * convert between the internal list element type and the external type.</p>
 *
 * <p>When using the {@link #unconvert} method, a {@link ClassCastException} can be
 * thrown to indicate the value cannot be converted. The exception is caught and handled
 * where it is appropriate to do so.</p>
 */
public abstract class ConversionListWrapper<E, T>
        extends ConversionCollectionWrapper<E, T> implements List<E> {

    /**
     * Constructor.
     *
     * <p>No synchronization.</p>
     */
    public ConversionListWrapper() {
        this(SyncStrategy.NONE);
    }

    /**
     * Constructor.
     *
     * @param strategy  The synchronization strategy to use.
     */
    public ConversionListWrapper(SyncStrategy strategy) {
        super(strategy);
    }

    /**
     * Invoked from a synchronized block to get the encapsulated {@code List}.
     */
    protected abstract List<T> list();

    @Override
    protected Collection<T> collection() {
        return list();
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        PreCon.notNull(c);

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                return list().addAll(index, unconvert(c));
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return list().addAll(index, unconvert(c));
            }
        } else {
            return list().addAll(index, unconvert(c));
        }
    }

    @Override
    public E get(int index) {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return getSource(index);
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return getSource(index);
            }
        } else {
            return getSource(index);
        }
    }

    private E getSource(int index) {
        T result = list().get(index);
        if (result == null)
            return null;

        return convert(result);
    }

    @Override
    public E set(int index, E element) {
        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                return setSource(index, element);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return setSource(index, element);
            }
        } else {
            return setSource(index, element);
        }
    }

    private E setSource(int index, E element) {
        T previous = list().set(index, unconvert(element));
        if (previous == null)
            return null;

        return convert(previous);
    }

    @Override
    public void add(int index, E element) {
        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                list().add(index, unconvert(element));
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                list().add(index, unconvert(element));
            }
        } else {
            list().add(index, unconvert(element));
        }
    }

    @Override
    public E remove(int index) {
        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                return removeSource(index);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return removeSource(index);
            }
        } else {
            return removeSource(index);
        }
    }

    private E removeSource(int index) {
        T removed = list().remove(index);
        if (removed == null)
            return null;

        return convert(removed);
    }

    @Override
    public int indexOf(Object o) {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return indexOfSource(o);
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return indexOfSource(o);
            }
        } else {
            return indexOfSource(o);
        }
    }

    private int indexOfSource(Object o) {
        try {
            //noinspection SuspiciousMethodCalls
            return list().indexOf(new Matcher(o));
        }
        catch (ClassCastException e) {
            return -1;
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return lastIndexOfSource(o);
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return lastIndexOfSource(o);
            }
        } else {
            return lastIndexOfSource(o);
        }
    }

    private int lastIndexOfSource(Object o) {
        try {
            //noinspection SuspiciousMethodCalls
            return list().lastIndexOf(new Matcher(o));
        }
        catch(ClassCastException e) {
            return -1;
        }
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(final int index) {

        return new ConversionListIteratorWrapper<E, T>(_strategy) {

            ListIterator<T> iterator = list().listIterator(index);

            @Override
            protected E convert(T internal) {
                return ConversionListWrapper.this.convert(internal);
            }

            @Override
            protected T unconvert(Object external) {
                return ConversionListWrapper.this.unconvert(external);
            }

            @Override
            protected ListIterator<T> iterator() {
                return iterator;
            }
        };
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {

        final List<T> list = list().subList(fromIndex, toIndex);
        final ConversionListWrapper<E, T> parent = this;

        return new ConversionListWrapper<E, T>(_strategy) {

            @Override
            protected List<T> list() {
                return list;
            }

            @Override
            protected E convert(T internal) {
                return parent.convert(internal);
            }

            @Override
            protected T unconvert(Object external) {
                return parent.unconvert(external);
            }
        };
    }
}
