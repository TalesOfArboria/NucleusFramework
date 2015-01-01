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
import com.jcwhatever.nucleus.utils.DateUtils;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.annotation.Nullable;

/**
 * An element distributor that must be polled for the current element. The current element
 * changes to the next when its specified time span expires. The distributor is circular,
 * meaning once it reaches the end of the collection it will go back to the beginning.
 *
 * <p>The current element is passively changed. The {@code #current} method must be
 * invoked at interval for the distributor to be effective.</p>
 */
public class TimedDistributor<T> {

    private CircularQueue<Element<T>> _queue = new CircularQueue<>();
    private Date _nextRotation;

    /**
     * Add an element.
     *
     * @param element         The element to add.
     * @param timeSpanTicks   The number of ticks the element will be current for.
     */
    public boolean add(T element, int timeSpanTicks) {

        PreCon.notNull(element);
        PreCon.greaterThanZero(timeSpanTicks);

        Element<T> timedElement = new Element<>(element, timeSpanTicks);

        if (_queue.offerFirst(timedElement)) {
            _nextRotation = null;
            return true;
        }

        return false;
    }

    /**
     * Remove an element.
     *
     * @param element  The element to remove.
     */
    public boolean remove(T element) {
        PreCon.notNull(element);

        return _queue.removeFirstOccurrence(new Element<T>(element, -1));
    }

    /**
     * Get the current element.
     *
     * @return  Null if no actions or the time slice cycles
     * are not started or have been stopped.
     */
    @Nullable
    public T current() {

        if (_queue.isEmpty())
            return null;

        Element<T> element;
        if (_nextRotation == null) {
            element = _queue.element();
            assert element != null;

            _nextRotation = DateUtils.addTicks(new Date(), element.timeSpan);
        }
        else if (_nextRotation.compareTo(new Date()) <= 0) {
            element = _queue.next();
            assert element != null;

            _nextRotation = DateUtils.addTicks(new Date(), element.timeSpan);
        }
        else {
            element = _queue.element();
            assert element != null;
        }

        return element.value;
    }

    /**
     * Get all elements.
     */
    public Collection<T> getAll() {
        ArrayList<T> result = new ArrayList<>(_queue.size());
        for (Element<T> element : _queue) {
            result.add(element.value);
        }
        return result;
    }

    private static class Element<T> {
        T value;
        int timeSpan;

        Element(T value, int timeSpan) {
            this.value = value;
            this.timeSpan = timeSpan;
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        public boolean equals(Object obj) {
            return obj instanceof Element
                    ? ((Element) obj).value.equals(value)
                    : value.equals(obj);
        }
    }
}
