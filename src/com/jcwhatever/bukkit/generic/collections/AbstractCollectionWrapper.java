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

import java.util.Collection;
import java.util.Iterator;

/**
 * Abstract implementation of a collection wrapper.
 */
public abstract class AbstractCollectionWrapper<E> implements Collection<E> {

    @Override
    public int size() {
        return getCollection().size();
    }

    @Override
    public boolean isEmpty() {
        return getCollection().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getCollection().contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return getCollection().iterator();
    }

    @Override
    public Object[] toArray() {
        return getCollection().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return getCollection().toArray(a);
    }

    @Override
    public boolean add(E e) {
        return getCollection().add(e);
    }

    @Override
    public boolean remove(Object o) {
        return getCollection().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return getCollection().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return getCollection().addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return getCollection().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return getCollection().retainAll(c);
    }

    @Override
    public void clear() {
        getCollection().clear();
    }

    protected abstract Collection<E> getCollection();
}
