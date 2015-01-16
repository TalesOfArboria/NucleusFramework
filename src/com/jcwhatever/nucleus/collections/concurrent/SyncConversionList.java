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

package com.jcwhatever.nucleus.collections.concurrent;

import com.jcwhatever.nucleus.utils.PreCon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * An abstract implementation of a synchronized {@link List} wrapper
 * designed to convert between the encapsulated list element type and
 * an externally visible type.
 *
 * <p>The actual list is provided to the abstract implementation by
 * overriding and returning it from the {@link #iterator} method.</p>
 *
 * <p>If the wrapper is being used to wrap a list that is part of the internals
 * of another type, the other types synchronization object can be used by passing
 * it into the wrappers constructor.</p>
 *
 * <p>The {@link #convert} and {@link #unconvert} abstract methods are used to
 * convert between the internal iterators element type and the external type.</p>
 *
 * <p>When using the {@link #unconvert} method, a {@link ClassCastException} can be
 * thrown to indicate the value cannot be converted. The exception is caught and handled
 * where it is appropriate to do so.</p>
 */
public abstract class SyncConversionList<E, T> implements List<E> {

    private final Object _sync;

    /**
     * Constructor.
     */
    public SyncConversionList() {
        this(new Object());
    }

    /**
     * Constructor.
     *
     * @param sync  The synchronization object to use.
     */
    protected SyncConversionList(Object sync) {
        PreCon.notNull(sync);

        _sync = sync;
    }

    /**
     * Convert an internal list type to the external type.
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
    protected abstract List<T> list();

    @Override
    public int size() {
        return list().size();
    }

    @Override
    public boolean isEmpty() {
        synchronized (_sync) {
            return list().isEmpty();
        }
    }

    @Override
    public boolean contains(Object o) {
        try {
            synchronized (_sync) {
                return list().contains(new Matcher(o));
            }
        }
        catch(ClassCastException e) {
            return false;
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new SyncConversionIterator<E, T>() {

            Iterator<T> iterator = list().iterator();

            @Override
            protected E convert(T internal) {
                return SyncConversionList.this.convert(internal);
            }

            @Override
            protected Iterator<T> iterator() {
                return iterator;
            }
        };
    }

    @Override
    public Object[] toArray() {
        synchronized (_sync) {
            return unconvert(list()).toArray();
        }
    }

    @Override
    public <A> A[] toArray(A[] a) {
        synchronized (_sync) {
            //noinspection SuspiciousToArrayCall
            return unconvert(list()).toArray(a);
        }
    }

    @Override
    public boolean add(E e) {
        synchronized (_sync) {
            return list().add(unconvert(e));
        }
    }

    @Override
    public boolean remove(Object o) {
        try {
            synchronized (_sync) {
                return list().remove(new Matcher(o));
            }
        }
        catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object obj : c) {
            //noinspection SuspiciousMethodCalls
            if (!list().contains(new Matcher(obj)))
                return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        synchronized (_sync) {
            return list().addAll(unconvert(c));
        }
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        synchronized (_sync) {
            return list().addAll(index, unconvert(c));
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        synchronized (_sync) {
            return list().removeAll(unconvert(c));
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        synchronized (_sync) {
            return list().retainAll(unconvert(c));
        }
    }

    @Override
    public void clear() {
        synchronized (_sync) {
            list().clear();
        }
    }

    @Override
    public E get(int index) {
        synchronized (_sync) {
            T result = list().get(index);
            if (result == null)
                return null;

            return convert(result);
        }
    }

    @Override
    public E set(int index, E element) {

        synchronized (_sync) {
            T previous = list().set(index, unconvert(element));
            if (previous == null)
                return null;

            return convert(previous);
        }
    }

    @Override
    public void add(int index, E element) {
        synchronized (_sync) {
            list().add(index, unconvert(element));
        }
    }

    @Override
    public E remove(int index) {
        synchronized (_sync) {
            T removed = list().remove(index);
            if (removed == null)
                return null;

            return convert(removed);
        }
    }

    @Override
    public int indexOf(Object o) {
        try {
            synchronized (_sync) {
                return list().indexOf(new Matcher(o));
            }
        }
        catch (ClassCastException e) {
            return -1;
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        try {
            synchronized (_sync) {
                return list().lastIndexOf(new Matcher(o));
            }
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

        return new SyncConversionListIterator<E, T>() {

            ListIterator<T> iterator = list().listIterator(index);

            @Override
            protected E convert(T internal) {
                return SyncConversionList.this.convert(internal);
            }

            @Override
            protected T unconvert(Object external) {
                return SyncConversionList.this.unconvert(external);
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
        final SyncConversionList<E, T> parent = this;

        return new SyncConversionList<E, T>() {

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

    private List<T> unconvert(Collection<?> c) {
        List<T> list = new ArrayList<T>(c.size());
        for (Object obj : c) {
            try {
                list.add(unconvert(obj));
            }
            catch(ClassCastException ignore) {}
        }
        return list;
    }

    private class Matcher {
        final T matcher;

        Matcher(Object obj) {
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
