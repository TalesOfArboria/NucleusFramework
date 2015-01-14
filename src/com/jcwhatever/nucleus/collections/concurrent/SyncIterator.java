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

import java.util.Iterator;

/**
 * An abstract implementation of a synchronized {@link Iterator} wrapper.
 *
 * <p>The actual iterator is provided to the abstract implementation by
 * overriding and returning it from the {@link #iterator} method.</p>
 *
 * <p>If the wrapper is being used to wrap an iterator returned from a
 * collection or other type, the other types synchronization object can
 * be used by passing it into the wrappers constructor.</p>
 *
 * <p>In order to make using the wrapper as an extension of a iterator easier,
 * the {@link #onRemove} method is provided for optional override.</p>
 */
public abstract class SyncIterator<E> implements Iterator<E> {

    private final Object _sync;
    private E _current;

    /**
     * Constructor.
     */
    public SyncIterator() {
        this(new Object());
    }

    /**
     * Constructor.
     *
     * @param sync  The synchronization object to use.
     */
    protected SyncIterator(Object sync) {
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
    protected abstract Iterator<E> iterator();

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
    public void remove() {

        synchronized (_sync) {

            if (!onRemove(_current))
                throw new UnsupportedOperationException();

            iterator().remove();
        }
    }
}
