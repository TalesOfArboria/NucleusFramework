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

import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An abstract implementation of a synchronized {@link Deque} wrapper.
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
public abstract class SyncDeque<E> extends SyncQueue<E> implements Deque<E> {

    private final Object _sync;

    /**
     * Constructor.
     */
    public SyncDeque() {
        this(new Object());
    }

    /**
     * Constructor.
     *
     * @param sync  The synchronization object to use.
     */
    protected SyncDeque(Object sync) {
        super(sync);

        _sync = sync;
    }

    /**
     * Invoked from a synchronized block to get the
     * encapsulated {@code Deque}.
     */
    @Override
    protected abstract Deque<E> queue();

    @Override
    public void addFirst(E e) {
        if (!onPreAdd(e))
            throw new UnsupportedOperationException();

        synchronized (_sync) {
            queue().addFirst(e);
        }

        onAdded(e);
    }

    @Override
    public void addLast(E e) {

        if (!onPreAdd(e))
            throw new UnsupportedOperationException();

        synchronized (_sync) {
            queue().addLast(e);
        }

        onAdded(e);
    }

    @Override
    public boolean offerFirst(E e) {

        if (!onPreAdd(e))
            return false;

        boolean isAdded;

        synchronized (_sync) {
            isAdded = queue().offerFirst(e);
        }

        if (isAdded)
            onAdded(e);

        return isAdded;
    }

    @Override
    public boolean offerLast(E e) {
        if (!onPreAdd(e))
            return false;

        boolean isAdded;

        synchronized (_sync) {
            isAdded = queue().offerLast(e);
        }

        if (isAdded)
            onAdded(e);

        return isAdded;
    }

    @Override
    public E removeFirst() {
        E element;

        synchronized (_sync) {
            element = queue().peekFirst();

            if (element == null)
                return null;

            if (!onPreRemove(element))
                throw new UnsupportedOperationException();

            queue().removeFirst();
        }

        onRemoved(element);

        return element;
    }

    @Override
    public E removeLast() {
        E element;

        synchronized (_sync) {
            element = queue().peekLast();

            if (element == null)
                return null;

            if (!onPreRemove(element))
                throw new UnsupportedOperationException();

            queue().removeLast();
        }

        onRemoved(element);

        return element;
    }

    @Override
    public E pollFirst() {
        E element;

        synchronized (_sync) {
            element = queue().peekFirst();

            if (element == null) {
                queue().pollFirst();
                return null;
            }

            if (!onPreRemove(element))
                return null;

            queue().pollFirst();
        }

        onRemoved(element);

        return element;
    }

    @Override
    public E pollLast() {
        E element;

        synchronized (_sync) {
            element = queue().peekLast();

            if (element == null) {
                queue().pollLast();
                return null;
            }

            if (!onPreRemove(element))
                return null;

            queue().pollLast();
        }

        onRemoved(element);

        return element;
    }

    @Override
    public E getFirst() {
        synchronized (_sync) {
            return queue().getFirst();
        }
    }

    @Override
    public E getLast() {
        synchronized (_sync) {
            return queue().getLast();
        }
    }

    @Override
    public E peekFirst() {
        synchronized (_sync) {
            return queue().peekFirst();
        }
    }

    @Override
    public E peekLast() {
        synchronized (_sync) {
            return queue().peekLast();
        }
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {

        if (!onPreRemove(o))
            return false;

        synchronized (_sync) {
            if (removeFirstOccurrence(o)) {
                onRemoved(o);
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean removeLastOccurrence(Object o) {

        if (!onPreRemove(o))
            return false;

        synchronized (_sync) {
            if (removeLastOccurrence(o)) {
                onRemoved(o);
                return true;
            }
            return false;
        }
    }

    @Override
    public void push(E e) {

        if (!onPreAdd(e))
            throw new RuntimeException();

        synchronized (_sync) {
            queue().push(e);
        }

        onAdded(e);
    }

    @Override
    public E pop() {

        E element;

        synchronized (_sync) {
            element = queue().peek();
            if (element == null)
                throw new NoSuchElementException();

            if (!onPreRemove(element))
                throw new UnsupportedOperationException();

            queue().pop();
        }

        onRemoved(element);

        return element;
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new SyncIterator<E>() {

            Iterator<E> iterator = queue().descendingIterator();

            @Override
            protected boolean onRemove(E removed) {
                if (!onPreRemove(removed))
                    return false;

                onRemoved(removed);
                return true;
            }

            @Override
            protected Iterator<E> iterator() {
                return iterator;
            }
        };
    }
}
