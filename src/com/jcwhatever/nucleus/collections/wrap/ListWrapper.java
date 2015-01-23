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
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * An abstract implementation of a synchronized {@link List} wrapper. The wrapper is
 * optionally synchronized via a sync object or {@link ReadWriteLock} passed into the
 * constructor using a {@link SyncStrategy}.
 *
 * <p>If the list is synchronized, the sync object must be externally locked while
 * the iterator is in use. Otherwise, a {@link java.lang.IllegalStateException} will
 * be thrown.</p>
 *
 * <p>The actual collection is provided to the abstract implementation by
 * overriding and returning it from the {@link #list} method.</p>
 *
 * <p>In order to make using the wrapper as an extension of a collection easier,
 * several protected methods are provided for optional override. These methods
 * are provided by the superclass {@link CollectionWrapper}.
 */
public abstract class ListWrapper<E> extends CollectionWrapper<E> implements List<E> {

    /**
     * Constructor.
     *
     * <p>No synchronization.</p>
     */
    public ListWrapper() {
        this(SyncStrategy.NONE);
    }

    /**
     * Constructor.
     *
     * @param strategy  The synchronization strategy to use.
     */
    public ListWrapper(SyncStrategy strategy) {
        super(strategy);
    }

    /**
     * Invoked from a synchronized block to get the encapsulated list.
     */
    protected abstract List<E> list();

    @Override
    protected Collection<E> collection() {
        return list();
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                return addAllSource(index, c);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return addAllSource(index, c);
            }
        } else {
            return addAllSource(index, c);
        }
    }

    private boolean addAllSource(int index, Collection<? extends E> c) {
        boolean isChanged = false;
        for (E element : c) {

            if (!onPreAdd(element))
                continue;

            add(index, element);

            onAdded(element);
            isChanged = true;
        }
        return isChanged;
    }

    @Override
    public E get(int index) {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return list().get(index);
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return list().get(index);
            }
        } else {
            return list().get(index);
        }
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
        E previous = list().set(index, element);
        onRemoved(previous);
        onAdded(element);
        return previous;
    }

    @Override
    public void add(int index, E element) {
        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                addSource(index, element);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                addSource(index, element);
            }
        } else {
            addSource(index, element);
        }
    }

    private void addSource(int index, E element) {
        list().add(index, element);
        onAdded(element);
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
        E removed = list().remove(index);
        onRemoved(removed);
        return removed;
    }

    @Override
    public int indexOf(Object o) {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return list().indexOf(o);
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return list().indexOf(o);
            }
        } else {
            return list().indexOf(o);
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return list().lastIndexOf(o);
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return list().lastIndexOf(o);
            }
        } else {
            return list().lastIndexOf(o);
        }
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
        return new ListIteratorWrapper<E>(_strategy) {

            ListIterator<E> iterator = list().listIterator(index);

            @Override
            protected boolean onRemove(E element) {
                if (onPreRemove(element)) {
                    onRemoved(element);
                    return true;
                }
                return false;
            }

            @Override
            protected ListIterator<E> iterator() {
                return iterator;
            }
        };
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {

        final List<E> subList = list().subList(fromIndex, toIndex);
        final ListWrapper<E> parent = this;

        return new ListWrapper<E>(_strategy) {

            @Override
            protected boolean onPreAdd(E element) {
                return parent.onPreAdd(element);
            }

            @Override
            protected void onAdded(E element) {
                parent.onAdded(element);
            }

            @Override
            protected boolean onPreRemove(Object obj) {
                return parent.onPreRemove(obj);
            }

            @Override
            protected void onRemoved(Object obj) {
                parent.onRemoved(obj);
            }

            @Override
            protected void onPreClear() {
                parent.onPreClear();
            }

            @Override
            protected void onClear(Collection<E> cleared) {
                parent.onClear(cleared);
            }

            @Override
            protected List<E> list() {
                return subList;
            }
        };
    }
}
