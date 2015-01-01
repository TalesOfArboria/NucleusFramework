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

package com.jcwhatever.nucleus.collections.timed;

import com.jcwhatever.nucleus.collections.CircularQueue;
import com.jcwhatever.nucleus.collections.wrappers.AbstractConversionIterator;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import javax.annotation.Nullable;

/**
 * An element distributor that must be polled for the current element. The current element
 * changes to the next when its specified time span expires. The distributor is circular,
 * meaning once it reaches the end of the collection it will go back to the beginning.
 *
 * <p>The current element is passively changed. The {@code #current} method must be
 * invoked at interval for the distributor to be effective.</p>
 */
public class TimedDistributor<E> implements Collection<E> {

    private final CircularQueue<Element<E>> _queue = new CircularQueue<>();
    private final int _defaultTime;
    private final TimeScale _defaultTimeScale;

    private long _nextRotation;

    /**
     * Constructor.
     *
     * <p>Default rotation time is 3 seconds.</p>
     */
    public TimedDistributor() {
        this(3, TimeScale.SECONDS);
    }

    /**
     * Constructor.
     *
     * @param defaultTime       The default rotation time.
     * @param defaultTimeScale  The default time scale.
     */
    public TimedDistributor(int defaultTime, TimeScale defaultTimeScale) {
        PreCon.greaterThanZero(defaultTime);
        PreCon.notNull(defaultTimeScale);

        _defaultTime = defaultTime;
        _defaultTimeScale = defaultTimeScale;
    }

    /**
     * Constructor.
     *
     * @param defaultTime       The default rotation time.
     * @param defaultTimeScale  The default time scale.
     * @param collection        The initial collection to start with.
     */
    public TimedDistributor(int defaultTime, TimeScale defaultTimeScale,
                            Collection<? extends E> collection) {
        PreCon.greaterThanZero(defaultTime);
        PreCon.notNull(defaultTimeScale);
        PreCon.notNull(collection);

        _defaultTime = defaultTime;
        _defaultTimeScale = defaultTimeScale;

        for (E value : collection) {
            add(value, _defaultTime, _defaultTimeScale, false);
        }
    }

    /**
     * Add an element to the head of the distributor.
     *
     * @param element    The element to add.
     * @param timeSpan   The number of ticks the element will be current for.
     */
    public boolean add(@Nullable E element, int timeSpan, TimeScale timeScale) {
        PreCon.greaterThanZero(timeSpan);
        PreCon.notNull(timeScale);

        return add(element, timeSpan, timeScale, true);
    }

    /**
     * Get the current element.
     *
     * @return  Null if no actions or the time slice cycles
     * are not started or have been stopped.
     */
    @Nullable
    public E current() {

        if (_queue.isEmpty())
            return null;

        Element<E> element;
        long currentTime = System.currentTimeMillis();

        if (_nextRotation == 0) {
            element = _queue.element();
            assert element != null;

            _nextRotation = currentTime + element.timeSpan;
        }
        else if (_nextRotation <= currentTime) {
            element = _queue.next();
            assert element != null;

            _nextRotation = currentTime + element.timeSpan;
        }
        else {
            element = _queue.element();
            assert element != null;
        }

        @SuppressWarnings("unchecked")
        E value = (E)element.value;

        return value;
    }

    @Override
    public int size() {
        return _queue.size();
    }

    @Override
    public boolean isEmpty() {
        return _queue.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return _queue.contains(new Element<E>(o));
    }

    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[_queue.size()];
        int index = 0;
        for (Element<E> element : _queue) {
            result[index] = element.value;
            index++;
        }
        return result;
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        int index = 0;
        for (Element<E> element : _queue) {

            @SuppressWarnings("unchecked")
            T1 value = (T1)element.value;

            a[index] = value;
            index++;
        }
        return a;
    }

    @Override
    public boolean add(@Nullable E element) {
        return add(element, _defaultTime, _defaultTimeScale);
    }

    @Override
    public boolean remove(@Nullable Object o) {
        return _queue.removeFirstOccurrence(new Element<E>(o));
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        PreCon.notNull(c);

        for (Object o : c) {
            if (!contains(o))
                return false;
        }
        return true;
    }

    /**
     * Add all items from the collection. The collection is
     * added to the current head of the distributor and the order
     * of the collection, if any, is maintained.
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {

        LinkedList<E> list = new LinkedList<>(c);

        while (!list.isEmpty()) {

            E element = list.removeLast();
            add(element);
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        PreCon.notNull(c);

        boolean isChanged = false;
        for (Object o : c) {
            isChanged = isChanged || remove(o);
        }
        return isChanged;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        PreCon.notNull(c);

        Iterator<E> iterator = iterator();
        while (iterator.hasNext()) {
            E element = iterator.next();

            if (!c.contains(element))
                iterator.remove();
        }

        return false;
    }

    @Override
    public void clear() {
        _queue.clear();
    }

    private boolean add(E element, int timeSpan, TimeScale timeScale, boolean first) {

        Element<E> timedElement = new Element<>(element, timeSpan, timeScale);

        if ((first && _queue.offerFirst(timedElement)) ||
                (!first && _queue.offerLast(timedElement))) {
            _nextRotation = 0;
            return true;
        }

        return false;
    }

    private static class Element<T> {
        Object value;
        int timeSpan;

        Element(T value, int timeSpan, TimeScale timeScale) {
            this.value = value;
            this.timeSpan = timeSpan * timeScale.getTimeFactor();
        }

        Element(Object value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            return value == null
                    ? super.hashCode()
                    : value.hashCode();
        }

        public boolean equals(Object obj) {
            if (obj instanceof Element) {
                Element other = (Element)obj;

                if (other.value == null && value == null) {
                    return true;
                } else if (other.value != null) {
                    return other.value.equals(value);
                }
            }
            else {
                if (value == null && obj == null) {
                    return true;
                }
                else if (value != null) {
                    return value.equals(obj);
                }
            }
            return false;
        }
    }

    private class Itr extends AbstractConversionIterator<E, Element<E>> {

        Iterator<Element<E>> iterator = _queue.iterator();

        @Override
        protected E getElement(Element<E> trueElement) {

            @SuppressWarnings("unchecked")
            E result = (E)trueElement.value;

            return result;
        }

        @Override
        protected Iterator<Element<E>> getIterator() {
            return iterator;
        }
    }
}
