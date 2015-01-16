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

import com.jcwhatever.nucleus.utils.PreCon;

import java.util.Map.Entry;

/*
 * 
 */
public abstract class SyncConversionEntry<K, V, I> implements Entry<K, V> {

    private final Object _sync;

    public SyncConversionEntry() {
        this(new Object());
    }

    public SyncConversionEntry(Object sync) {
        PreCon.notNull(sync);

        _sync = sync;
    }

    protected abstract Entry<K, I> entry();

    protected abstract V convertFrom(K key, I internalVal);

    protected abstract I convertTo(K key, V value);

    @Override
    public K getKey() {
        return entry().getKey();
    }

    @Override
    public V getValue() {
        synchronized (_sync) {
            return convertFrom(entry().getKey(),  entry().getValue());
        }
    }

    @Override
    public V setValue(V value) {
        synchronized (_sync) {
            I current = entry().getValue();
            entry().setValue(convertTo(entry().getKey(), value));
            return convertFrom(entry().getKey(), current);
        }
    }
}
