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

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Abstract implementation of a {@code Multimap} wrapper.
 */
public abstract class AbstractMultimapWrapper<K, V> implements Multimap<K, V> {

    @Override
    public int size() {
        return getMap().size();
    }

    @Override
    public boolean isEmpty() {
        return getMap().isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return getMap().containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return getMap().containsValue(o);
    }

    @Override
    public boolean containsEntry(Object o, Object o1) {
        return getMap().containsEntry(o, o1);
    }

    @Override
    public boolean put(K k, V v) {
        return getMap().put(k, v);
    }

    @Override
    public boolean remove(Object o, Object o1) {
        return getMap().remove(o, o1);
    }

    @Override
    public boolean putAll(K k, Iterable<? extends V> iterable) {
        return getMap().putAll(k, iterable);
    }

    @Override
    public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
        return getMap().putAll(multimap);
    }

    @Override
    public Collection<V> replaceValues(K k, Iterable<? extends V> iterable) {
        return getMap().replaceValues(k, iterable);
    }

    @Override
    public Collection<V> removeAll(Object o) {
        return getMap().removeAll(o);
    }

    @Override
    public void clear() {
        getMap().clear();
    }

    @Override
    public Collection<V> get(K k) {
        return getMap().get(k);
    }

    @Override
    public Set<K> keySet() {
        return getMap().keySet();
    }

    @Override
    public Multiset<K> keys() {
        return getMap().keys();
    }

    @Override
    public Collection<V> values() {
        return getMap().values();
    }

    @Override
    public Collection<Entry<K, V>> entries() {
        return getMap().entries();
    }

    @Override
    public Map<K, Collection<V>> asMap() {
        return getMap().asMap();
    }

    protected abstract Multimap<K, V> getMap();
}
