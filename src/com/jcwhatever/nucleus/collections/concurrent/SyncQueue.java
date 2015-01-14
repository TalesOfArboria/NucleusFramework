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

import java.util.Collection;
import java.util.Queue;

/**
 * An abstract implementation of a synchronized {@link Queue} wrapper.
 *
 * <p>The actual collection is provided to the abstract implementation by
 * overriding and returning it from the {@link #queue} method.</p>
 *
 * <p>If the wrapper is being used to wrap a collection that is part of the internals
 * of another type, the other types synchronization object can be used by passing
 * it into the wrappers constructor.</p>
 *
 * <p>In order to make using the wrapper as an extension of a collection easier,
 * several protected methods are provided for optional override. These methods
 * are provided by the superclass {@link SyncCollection}.
 */
public abstract class SyncQueue<E> extends SyncCollection<E> implements Queue<E> {

    private final Object _sync;

    /**
     * Constructor.
     */
    public SyncQueue() {
        this(new Object());
    }

    /**
     * Constructor.
     *
     * @param sync  The synchronization object to use.
     */
    protected SyncQueue(Object sync) {
        super(sync);

        _sync = sync;
    }

    /**
     * Invoked from a synchronized block to get the
     * encapsulated {@code Queue}.
     */
    protected abstract Queue<E> queue();

    @Override
    protected Collection<E> collection() {
        return queue();
    }

    @Override
    public boolean offer(E e) {

        if (!onPreAdd((e)))
            return false;

        boolean isAdded;

        synchronized (_sync) {
            isAdded = queue().offer(e);
        }

        if (isAdded)
            onAdded(e);

        return isAdded;
    }

    @Override
    public E remove() {

        if (!onPreRemove(peek()))
            throw new UnsupportedOperationException();

        E removed;

        synchronized (_sync) {
            removed = queue().remove();
        }

        onRemoved(removed);

        return removed;
    }

    @Override
    public E poll() {

        E element;

        synchronized (_sync) {
            element = queue().peek();

            if (element == null)
                return null;

            if (!onPreRemove(element))
                throw new UnsupportedOperationException();

            queue().poll();
        }

        onRemoved(element);

        return element;
    }

    @Override
    public E element() {
        synchronized (_sync) {
            return queue().element();
        }
    }

    @Override
    public E peek() {
        synchronized (_sync) {
            return queue().peek();
        }
    }
}
