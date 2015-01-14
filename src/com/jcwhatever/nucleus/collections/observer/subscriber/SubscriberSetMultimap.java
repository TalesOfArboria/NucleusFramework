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

package com.jcwhatever.nucleus.collections.observer.subscriber;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.jcwhatever.nucleus.utils.observer.ISubscriber;

/**
 * A {@link SubscriberMultimap} implementation that uses hash keys and hash set values.
 */
public class SubscriberSetMultimap<K, V extends ISubscriber> extends SubscriberMultimap<K, V> {

    private Multimap<K, V> _multimap;

    /**
     * Constructor.
     */
    public SubscriberSetMultimap() {
        this(7, 3);
    }

    /**
     * Constructor.
     *
     * @param keySize    The initial key set capacity.
     * @param valueSize  The initial capacity of value collections.
     */
    public SubscriberSetMultimap(int keySize, int valueSize) {
        _multimap = MultimapBuilder.hashKeys(keySize).hashSetValues(valueSize).build();
    }

    @Override
    protected Multimap<K, V> map() {
        return _multimap;
    }
}
