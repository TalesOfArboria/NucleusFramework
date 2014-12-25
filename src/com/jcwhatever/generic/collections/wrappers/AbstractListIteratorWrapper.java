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

package com.jcwhatever.generic.collections.wrappers;

import java.util.ListIterator;

/**
 * Abstract implementation of a {@code ListIterator} wrapper.
 */
public abstract class AbstractListIteratorWrapper<E> implements ListIterator<E> {

    protected E _current;

    @Override
    public boolean hasNext() {
        return getIterator().hasNext();
    }

    @Override
    public E next() {
        return _current = getIterator().next();
    }

    @Override
    public boolean hasPrevious() {
        return getIterator().hasPrevious();
    }

    @Override
    public E previous() {
        return _current = getIterator().previous();
    }

    @Override
    public int nextIndex() {
        return getIterator().nextIndex();
    }

    @Override
    public int previousIndex() {
        return getIterator().previousIndex();
    }

    @Override
    public void remove() {
        getIterator().remove();
    }

    @Override
    public void set(E e) {
        getIterator().set(e);
    }

    @Override
    public void add(E e) {
        getIterator().add(e);
    }

    protected abstract ListIterator<E> getIterator();
}
