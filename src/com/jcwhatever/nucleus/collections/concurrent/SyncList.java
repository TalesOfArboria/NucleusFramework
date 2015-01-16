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

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * An abstract implementation of a synchronized {@link List} wrapper.
 *
 * <p>The actual collection is provided to the abstract implementation by
 * overriding and returning it from the {@link #list} method.</p>
 *
 * <p>If the wrapper is being used to wrap a collection that is part of the internals
 * of another type, the other types synchronization object can be used by passing
 * it into the wrappers constructor.</p>
 *
 * <p>In order to make using the wrapper as an extension of a collection easier,
 * several protected methods are provided for optional override. These methods
 * are provided by the superclass {@link SyncCollection}.
 */
public abstract class SyncList<E> extends SyncCollection<E> implements List<E> {

    private final Object _sync;

    /**
     * Constructor.
     */
    public SyncList() {
        this(new Object());
    }

    /**
     * Constructor.
     *
     * @param sync  The synchronization object to use.
     */
    protected SyncList(Object sync) {
        PreCon.notNull(sync);

        _sync = sync;
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
        synchronized (_sync) {
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
    }

    @Override
    public E get(int index) {
        return list().get(index);
    }

    @Override
    public E set(int index, E element) {
        synchronized (_sync) {
            E previous = list().set(index, element);
            onRemoved(previous);
            onAdded(element);
            return previous;
        }
    }

    @Override
    public void add(int index, E element) {
        synchronized (_sync) {
            list().add(index, element);
            onAdded(element);
        }
    }

    @Override
    public E remove(int index) {
        synchronized (_sync) {
            E removed = list().remove(index);
            onRemoved(removed);
            return removed;
        }
    }

    @Override
    public int indexOf(Object o) {
        synchronized (_sync) {
            return list().indexOf(o);
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        synchronized (_sync) {
            return list().lastIndexOf(o);
        }
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
        return new SyncListIterator<E>() {

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
        final SyncList<E> parent = this;

        return new SyncList<E>() {

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
