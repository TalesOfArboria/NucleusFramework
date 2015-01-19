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
import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * An abstract implementation of a {@link Collection} wrapper designed to convert
 * between the collections internal element type and the publicly visible element
 * type. The wrapper is optionally synchronized via a sync object or {@link ReadWriteLock}
 * passed in through the constructor.
 *
 * <p>The actual collection is provided to the abstract implementation by
 * overriding and returning it from the {@link #collection} method.</p>
 *
 * <p>In order to make using the wrapper as an extension of a collection easier,
 * a few protected methods are provided for optional override.
 * See {@link #onRemoved}, {@link #onAdded}.</p>
 */
public abstract class ConversionCollectionWrapper <E, T> implements Collection<E> {

    protected final Object _sync;
    protected final ReadWriteLock _lock;

    /**
     * Constructor.
     */
    public ConversionCollectionWrapper() {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param sync  The synchronization object to use.
     */
    public ConversionCollectionWrapper(@Nullable Object sync) {

        _sync = sync;
        _lock = sync instanceof ReadWriteLock
                ? (ReadWriteLock)sync
                : null;
    }

    /**
     * Invoked after removing an element.
     *
     * @param element  The element that will be removed.
     *
     * <p>Not guaranteed to be called from a synchronized block.</p>
     *
     * <p>Intended to be optionally overridden by implementation.</p>
     *
     * @return  True to allow, false to throw an {@code UnsupportedOperationException}.
     */
    protected void onRemoved(@SuppressWarnings("unused") Object element) { }

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
     * Convert an internal queue element type to the external type.
     *
     * @param internal  The internal type to convert.
     *
     * @return  An external type instance.
     */
    protected abstract E convert(T internal);

    /**
     * Unconvert an external type to an internal type.
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
     * Invoked from a synchronized block to get the encapsulated list.
     */
    protected abstract Collection<T> collection();

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
        } else {
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
        } else {
            return collection().isEmpty();
        }
    }

    @Override
    public boolean contains(Object o) {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return containsSource(o);
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return containsSource(o);
            }
        } else {
            return containsSource(o);
        }
    }

    private boolean containsSource(Object o) {
        try {
            //noinspection SuspiciousMethodCalls
            return collection().contains(new Matcher(o));
        }
        catch(ClassCastException e) {
            return false;
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new ConversionIteratorWrapper<E, T>(_sync) {

            Iterator<T> iterator = collection().iterator();

            @Override
            protected boolean onRemove(T internal) {
                ConversionCollectionWrapper.this.onRemoved(internal);
                return true;
            }

            @Override
            protected E convert(T internal) {
                return ConversionCollectionWrapper.this.convert(internal);
            }

            @Override
            protected Iterator<T> iterator() {
                return iterator;
            }
        };
    }

    @Override
    public Object[] toArray() {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return unconvert(collection()).toArray();
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return unconvert(collection()).toArray();
            }
        } else {
            return unconvert(collection()).toArray();
        }
    }

    @Override
    public <A> A[] toArray(A[] a) {
        if (_lock != null) {
            _lock.readLock().lock();
            try {
                //noinspection SuspiciousToArrayCall
                return unconvert(collection()).toArray(a);
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                //noinspection SuspiciousToArrayCall
                return unconvert(collection()).toArray(a);
            }
        } else {
            //noinspection SuspiciousToArrayCall
            return unconvert(collection()).toArray(a);
        }
    }

    @Override
    public boolean add(E e) {
        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                return addSource(e);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return addSource(e);
            }
        } else {
            return addSource(e);
        }
    }

    private boolean addSource(E e) {

        T internal = unconvert(e);

        if (collection().add(internal)) {
            onAdded(internal);
            return true;
        }

        return false;
    }

    @Override
    public boolean remove(Object o) {
        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                return removeSource(o);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return removeSource(o);
            }
        } else {
            return removeSource(o);
        }
    }

    private boolean removeSource(Object o) {
        try {
            //noinspection SuspiciousMethodCalls
            if (collection().remove(new Matcher(o))) {
                onRemoved(o);
                return true;
            }
            return false;
        }
        catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        PreCon.notNull(c);

        if (_lock != null) {
            _lock.readLock().lock();
            try {
                return containsAllSource(c);
            }
            finally {
                _lock.readLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return containsAllSource(c);
            }
        } else {
            return containsAllSource(c);
        }
    }

    private boolean containsAllSource(Collection<?> c) {
        for (Object obj : c) {
            //noinspection SuspiciousMethodCalls
            if (!collection().contains(new Matcher(obj)))
                return false;
        }
        return true;
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
        } else {
            return addAllSource(c);
        }
    }

    private boolean addAllSource(Collection<? extends E> c) {

        boolean isChanged = false;
        for (E element : c) {
            isChanged = add(element) || isChanged;
        }
        return isChanged;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
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
        } else {
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
    public boolean retainAll(Collection<?> c) {
        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                return retainAllSource(c);
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                return retainAllSource(c);
            }
        } else {
            return retainAllSource(c);
        }
    }

    private boolean retainAllSource(final Collection<?> c) {

        List<T> removed = CollectionUtils.retainAll(collection(), new IValidator<T>() {
            @Override
            public boolean isValid(T element) {
                return c.contains(convert(element));
            }
        });

        for (T element : removed) {
            onRemoved(element);
        }

        return !removed.isEmpty();
    }

    @Override
    public void clear() {
        if (_lock != null) {
            _lock.writeLock().lock();
            try {
                clearSource();
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        else if (_sync != null) {
            synchronized (_sync) {
                clearSource();
            }
        } else {
            clearSource();
        }
    }

    private void clearSource() {
        List<T> cleared = new ArrayList<T>(collection());
        collection().clear();

        for (T removed : cleared) {
            onRemoved(removed);
        }
    }

    protected Collection<T> unconvert(Collection<?> c) {
        List<T> list = new ArrayList<T>(c.size());
        for (Object obj : c) {
            try {
                list.add(unconvert(obj));
            }
            catch(ClassCastException ignore) {}
        }
        return list;
    }

    protected class Matcher {
        final T matcher;

        public Matcher(Object obj) {
            matcher = unconvert(obj);
        }

        @Override
        public int hashCode() {
            return matcher != null ? matcher.hashCode() : 0;
        }

        @Override
        public boolean equals(Object obj) {
            if (matcher == null)
                return obj == null;

            return matcher.equals(obj);
        }
    }
}