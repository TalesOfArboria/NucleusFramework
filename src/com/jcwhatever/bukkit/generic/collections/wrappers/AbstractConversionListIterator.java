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

package com.jcwhatever.bukkit.generic.collections.wrappers;

import java.util.ListIterator;

/**
 * An abstract implementation of a {@code ListIterator} that
 * iterates over a collection of one type but returns a
 * different type.
 */
public abstract class AbstractConversionListIterator<E, T> implements ListIterator<E> {

    protected T _current;

    @Override
    public boolean hasNext() {
        return getIterator().hasNext();
    }

    @Override
    public E next() {
        _current = getIterator().next();
        return getFalseElement(_current);
    }

    @Override
    public boolean hasPrevious() {
        return getIterator().hasPrevious();
    }

    @Override
    public E previous() {
        _current = getIterator().previous();
        return getFalseElement(_current);
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
        _current = getTrueElement(e);
        getIterator().set(_current);
    }

    @Override
    public void add(E e) {
        T t = getTrueElement(e);
        getIterator().add(t);
    }

    protected abstract E getFalseElement(T trueElement);

    protected abstract T getTrueElement(E falseElement);

    protected abstract ListIterator<T> getIterator();
}
