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

package com.jcwhatever.nucleus.collections.wrappers;

import java.util.Collection;
import java.util.Queue;

/**
 * Abstract implementation of a {@code Queue} wrapper.
 */
public abstract class AbstractQueueWrapper<E> extends AbstractCollectionWrapper<E> implements Queue<E> {

    @Override
    public boolean offer(E e) {
        return getQueue().offer(e);
    }

    @Override
    public E remove() {
        return getQueue().remove();
    }

    @Override
    public E poll() {
        return getQueue().poll();
    }

    @Override
    public E element() {
        return getQueue().element();
    }

    @Override
    public E peek() {
        return getQueue().peek();
    }

    @Override
    protected final Collection<E> getCollection() {
        return getQueue();
    }

    protected abstract Queue<E> getQueue();
}
