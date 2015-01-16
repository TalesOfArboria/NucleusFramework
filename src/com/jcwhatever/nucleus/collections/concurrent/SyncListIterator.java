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

import java.util.ListIterator;

/**
 * An abstract implementation of a synchronized {@link ListIterator} wrapper.
 *
 * <p>The actual iterator is provided to the abstract implementation by
 * overriding and returning it from the {@link #iterator} method.</p>
 *
 * <p>If the wrapper is being used to wrap an iterator that is part of the internals
 * of another type, the other types synchronization object can be used by passing
 * it into the wrappers constructor.</p>
 *
 * <p>In order to make using the wrapper as an extension of a collection easier,
 * the {@link #onRemove} method is provided for optional override.
 */
public abstract class SyncListIterator<E> implements ListIterator<E> {

    private final Object _sync;
    private E _current;

    /**
     * Constructor.
     */
    public SyncListIterator() {
        this(new Object());
    }

    /**
     * Constructor.
     *
     * @param sync  The synchronization object to use.
     */
    protected SyncListIterator(Object sync) {
        PreCon.notNull(sync);

        _sync = sync;
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
     * Invoked from a synchronized block to get the encapsulated {@code Iterator}.
     */
    protected abstract ListIterator<E> iterator();

    @Override
    public boolean hasNext() {
        synchronized (_sync) {
            return iterator().hasNext();
        }
    }

    @Override
    public E next() {
        synchronized (_sync) {
            return _current = iterator().next();
        }
    }

    @Override
    public boolean hasPrevious() {
        synchronized (_sync) {
            return iterator().hasPrevious();
        }
    }

    @Override
    public E previous() {
        synchronized (_sync) {
            return _current = iterator().previous();
        }
    }

    @Override
    public int nextIndex() {
        synchronized (_sync) {
            return iterator().nextIndex();
        }
    }

    @Override
    public int previousIndex() {
        synchronized (_sync) {
            return iterator().previousIndex();
        }
    }

    @Override
    public void remove() {
        synchronized (_sync) {

            if (!onRemove(_current))
                throw new UnsupportedOperationException();

            iterator().remove();
        }
    }

    @Override
    public void set(E e) {
        synchronized (_sync) {
            iterator().set(e);
            _current = e;
        }
    }

    @Override
    public void add(E e) {
        synchronized (_sync) {
            iterator().add(e);
        }
    }
}
