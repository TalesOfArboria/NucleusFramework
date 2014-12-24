/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

package com.jcwhatever.bukkit.generic.collections;

import java.util.Deque;
import java.util.Iterator;
import java.util.Queue;

/**
 * Abstract implementation of a {@code Deque} wrapper.
 */
public abstract class AbstractDeqeueWrapper<E> extends AbstractQueueWrapper<E> implements Deque<E> {

    @Override
    public void addFirst(E e) {
        getDeque().addFirst(e);
    }

    @Override
    public void addLast(E e) {
        getDeque().addLast(e);
    }

    @Override
    public boolean offerFirst(E e) {
        return getDeque().offerFirst(e);
    }

    @Override
    public boolean offerLast(E e) {
        return getDeque().offerLast(e);
    }

    @Override
    public E removeFirst() {
        return getDeque().removeFirst();
    }

    @Override
    public E removeLast() {
        return getDeque().removeLast();
    }

    @Override
    public E pollFirst() {
        return getDeque().pollFirst();
    }

    @Override
    public E pollLast() {
        return getDeque().pollLast();
    }

    @Override
    public E getFirst() {
        return getDeque().getFirst();
    }

    @Override
    public E getLast() {
        return getDeque().getLast();
    }

    @Override
    public E peekFirst() {
        return getDeque().peekFirst();
    }

    @Override
    public E peekLast() {
        return getDeque().peekLast();
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        return getDeque().removeFirstOccurrence(o);
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        return getDeque().removeLastOccurrence(o);
    }

    @Override
    public void push(E e) {
        getDeque().push(e);
    }

    @Override
    public E pop() {
        return getDeque().pop();
    }

    @Override
    public Iterator<E> descendingIterator() {
        return getDeque().descendingIterator();
    }

    @Override
    protected final Queue<E> getQueue() {
        return getDeque();
    }

    protected abstract Deque<E> getDeque();
}
