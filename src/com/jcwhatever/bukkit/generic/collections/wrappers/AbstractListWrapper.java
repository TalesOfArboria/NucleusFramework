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

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/*
 * 
 */
public abstract class AbstractListWrapper<E> extends AbstractCollectionWrapper<E> implements List<E> {

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return getList().addAll(index, c);
    }

    @Override
    public E get(int index) {
        return getList().get(index);
    }

    @Override
    public E set(int index, E element) {
        return getList().set(index, element);
    }

    @Override
    public void add(int index, E element) {
        getList().add(index, element);
    }

    @Override
    public E remove(int index) {
        return getList().remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return getList().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return getList().lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return getList().listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return getList().listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return getList().subList(fromIndex, toIndex);
    }

    @Override
    protected final Collection<E> getCollection() {
        return getList();
    }

    protected abstract List<E> getList();

}
