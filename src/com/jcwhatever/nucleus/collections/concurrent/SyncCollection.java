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

import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.validate.IValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * An abstract implementation of a synchronized {@link Collection} wrapper.
 *
 * <p>The actual collection is provided to the abstract implementation by
 * overriding and returning it from the {@link #collection} method.</p>
 *
 * <p>If the wrapper is being used to wrap a collection that is part of the internals
 * of another type, the other types synchronization object can be used by passing
 * it into the wrappers constructor.</p>
 *
 * <p>In order to make using the wrapper as an extension of a collection easier,
 * several protected methods are provided for optional override.
 * See {@link #onPreAdd}, {@link #onAdded}, {@link #onPreRemove}, {@link #onRemoved},
 * {@link #onPreClear}, {@link #onClear}</p>
 */
public abstract class SyncCollection<E> implements Collection<E> {

    private final Object _sync;

    /**
     * Constructor.
     */
    public SyncCollection() {
        this(new Object());
    }

    /**
     * Constructor.
     *
     * @param sync  The synchronization object to use.
     */
    protected SyncCollection(Object sync) {
        PreCon.notNull(sync);

        _sync = sync;
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
        synchronized (_sync) {
            return collection().size();
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (_sync) {
            return collection().isEmpty();
        }
    }

    @Override
    public boolean contains(Object o) {
        synchronized (_sync) {
            return collection().contains(o);
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    @Override
    public Object[] toArray() {
        synchronized (_sync) {
            return collection().toArray();
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        synchronized (_sync) {
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

        synchronized (_sync) {
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

        synchronized (_sync) {
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

        synchronized (_sync) {
            return collection().containsAll(c);
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        PreCon.notNull(c);

        synchronized (_sync) {

            boolean isChanged = false;
            for (E e : c) {
                isChanged = add(e) || isChanged;
            }
            return isChanged;
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        PreCon.notNull(c);

        synchronized (_sync) {

            boolean isChanged = false;
            for (Object obj : c) {
                isChanged = remove(obj) || isChanged;
            }
            return isChanged;
        }
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        PreCon.notNull(c);
        List<E> removed;

        synchronized (_sync) {
            removed = CollectionUtils.retainAll(collection(), new IValidator<E>() {
                @Override
                public boolean isValid(E element) {
                    return !c.contains(element) && onPreRemove(element);
                }
            });
        }

        for (E agent : removed) {
            onRemoved(agent);
        }

        return !removed.isEmpty();
    }

    @Override
    public void clear() {

        onPreClear();

        Collection<E> values;

        synchronized (_sync) {

            values = new ArrayList<E>(collection());

            collection().clear();
        }

        onClear(values);
    }

    private class Itr extends SyncIterator<E> {

        @Override
        protected boolean onRemove(E element) {
            if (!SyncCollection.this.onPreRemove(element))
                return false;

            SyncCollection.this.onRemoved(element);
            return true;
        }

        @Override
        protected Iterator<E> iterator() {
            return collection().iterator();
        }
    }
}