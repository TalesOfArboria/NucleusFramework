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

import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.validate.IValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import javax.annotation.Nullable;

/**
 * An abstract implementation of a {@link Collection} wrapper. The wrapper is
 * optionally synchronized via a sync object or {@link ReadWriteLock}.
 *
 * <p>The actual collection is provided to the abstract implementation by
 * overriding and returning it from the {@link #collection} method.</p>
 *
 * <p>In order to make using the wrapper as an extension of a collection easier,
 * several protected methods are provided for optional override.
 * See {@link #onPreAdd}, {@link #onAdded}, {@link #onPreRemove}, {@link #onRemoved},
 * {@link #onPreClear}, {@link #onClear}</p>
 */
public abstract class CollectionWrapper<E> implements Collection<E> {

    protected final Object _sync;
    protected final ReadWriteLock _lock;

    /**
     * Constructor.
     */
    public CollectionWrapper() {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param sync  The synchronization object to use.
     */
    public CollectionWrapper(@Nullable Object sync) {

        _sync = sync;
        _lock = _sync instanceof ReadWriteLock
                ? (ReadWriteLock) sync
                : null;
    }

    /**
     * Invoked before an element is added.
     *
     * <p>Not guaranteed to be called from a synchronized block.</p>
     *
     * <p>Intended to be optionally overridden by implementation.</p>
     *
     * @param e  The element that will be added.
     *
     * @return  True to allow, false to deny.
     */
    protected boolean onPreAdd(E e) { return true; }

    /**
     * Invoked after an element is added.
     *
     * <p>Not guaranteed to be called from a synchronized block.</p>
     *
     * <p>Intended to be optionally overridden by implementation.</p>
     *
     * @param e  The element that was added.
     */
    protected void onAdded(E e) {}

    /**
     * Invoked before an element is removed except when
     * {@code #clear} is invoked.
     *
     * <p>Not guaranteed to be called from a synchronized block.</p>
     *
     * <p>Intended to be optionally overridden by implementation.</p>
     *
     * @param o  The object that will match the item to removed.
     *
     * @return  True to allow the removal, false to deny.
     */
    protected boolean onPreRemove(Object o) { return true; }

    /**
     * Invoked after an element is removed except when
     * {@code #clear} is invoked.
     *
     * <p>Not guaranteed to be called from a synchronized block.</p>
     *
     * <p>Intended to be optionally overridden by implementation.</p>
     *
     * @param o  The removed object.
     */
    protected void onRemoved(Object o) {}

    /**
     * Invoked before the collection is cleared.
     *
     * <p>Not guaranteed to be called from a synchronized block.</p>
     *
     * <p>Intended to be optionally overridden by implementation.</p>
     */
    protected void onPreClear() {}

    /**
     * Invoked after {@code #clear} is invoked.
     *
     * <p>Not guaranteed to be called from a synchronized block.</p>
     *
     * <p>Intended to be optionally overridden by implementation.</p>
     *
     * @param values  The values that were cleared from the collection.
     */
    protected void onClear(Collection<E> values) {}

    /**
     * Invoked from a synchronized block to get the collection.
     */
    protected abstract Collection<E> collection();

    @Override
    public int size() {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return collection().size();
            }
            finally {
                _lock.readLock().unlock();
            }

        }
        else if (_sync != null) {
            synchronized (_sync) {
                return collection().size();
            }
        }
        else {
            return collection().size();
        }
    }

    @Override
    public boolean isEmpty() {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return collection().isEmpty();
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return collection().isEmpty();
            }
        }
        else {
            return collection().isEmpty();
        }
    }

    @Override
    public boolean contains(Object o) {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return collection().contains(o);
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return collection().contains(o);
            }
        }
        else {
            return collection().contains(o);
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    @Override
    public Object[] toArray() {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return collection().toArray();
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return collection().toArray();
            }
        }
        else {
            return collection().toArray();
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                //noinspection SuspiciousToArrayCall
                return collection().toArray(a);
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                //noinspection SuspiciousToArrayCall
                return collection().toArray(a);
            }
        }
        else {
            //noinspection SuspiciousToArrayCall
            return collection().toArray(a);
        }
    }

    @Override
    public boolean add(E e) {
        PreCon.notNull(e);

        if (!onPreAdd(e))
            return false;

        boolean isAdded;

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                isAdded = collection().add(e);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                isAdded = collection().add(e);
            }
        }
        else {
            isAdded = collection().add(e);
        }

        if (isAdded)
            onAdded(e);

        return isAdded;
    }

    @Override
    public boolean remove(Object o) {

        if (!onPreRemove(o))
            return false;

        boolean isRemoved;

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                isRemoved = collection().remove(o);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                isRemoved = collection().remove(o);
            }
        }
        else {
            isRemoved = collection().remove(o);
        }

        if (isRemoved) {
            onRemoved(o);
        }

        return isRemoved;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        PreCon.notNull(c);

        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return collection().containsAll(c);
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return collection().containsAll(c);
            }
        }
        else {
            return collection().containsAll(c);
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        PreCon.notNull(c);

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                return addAllSource(c);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return addAllSource(c);
            }
        }
        else {
            return addAllSource(c);
        }
    }

    private boolean addAllSource(Collection<? extends E> c) {
        boolean isChanged = false;
        for (E e : c) {
            isChanged = add(e) || isChanged;
        }
        return isChanged;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        PreCon.notNull(c);

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                return removeAllSource(c);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return removeAllSource(c);
            }
        }
        else {
            return removeAllSource(c);
        }
    }

    private boolean removeAllSource(Collection<?> c) {
        boolean isChanged = false;
        for (Object obj : c) {
            isChanged = remove(obj) || isChanged;
        }
        return isChanged;
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        PreCon.notNull(c);
        List<E> removed;

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                removed = retainAllSource(c);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                removed = retainAllSource(c);
            }
        }
        else {
            removed = retainAllSource(c);
        }

        for (E agent : removed) {
            onRemoved(agent);
        }

        return !removed.isEmpty();
    }

    private List<E> retainAllSource(final Collection<?> c) {
        return CollectionUtils.retainAll(collection(), new IValidator<E>() {
            @Override
            public boolean isValid(E element) {
                return c.contains(element) && onPreRemove(element);
            }
        });
    }

    @Override
    public void clear() {

        onPreClear();

        Collection<E> values;

        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                values = clearSource();
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                values = clearSource();
            }
        }
        else {
            values = clearSource();
        }

        onClear(values);
    }

    private Collection<E> clearSource() {
        Collection<E> values = new ArrayList<E>(collection());

        collection().clear();

        return values;
    }

    private class Itr extends IteratorWrapper<E> {

        Iterator<E> iterator = collection().iterator();

        @Override
        protected boolean onRemove(E element) {
            if (!CollectionWrapper.this.onPreRemove(element))
                return false;

            CollectionWrapper.this.onRemoved(element);
            return true;
        }

        @Override
        protected Iterator<E> iterator() {
            return iterator;
        }
    }
}