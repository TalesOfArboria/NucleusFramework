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

package com.jcwhatever.nucleus.collections.observer.agent;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.jcwhatever.nucleus.utils.observer.ISubscriberAgent;

/**
 * An {@link AgentMultimap} implementation that uses hash keys and hash set values.
 *
 * <p>Thread safe.</p>
 *
 * <p>The maps iterators must be used inside a synchronized block which locks the
 * map instance. Otherwise, a {@link java.lang.IllegalStateException} is thrown.</p>
 */
public class AgentSetMultimap<K, V extends ISubscriberAgent> extends AgentMultimap<K, V> {

    private final Multimap<K, V> _map;

    /**
     * Constructor.
     *
     * <p>The initial key capacity is 7. The initial value
     * capacity is 3.</p>
     */
    public AgentSetMultimap() {
        this(7, 3);
    }

    /**
     * Constructor.
     *
     * @param keySize    The initial key set capacity.
     * @param valueSize  The initial value collection capacity.
     */
    public AgentSetMultimap(int keySize, int valueSize) {
        _map = MultimapBuilder.hashKeys(keySize).hashSetValues(valueSize).build();
    }

    @Override
    protected Multimap<K, V> map() {
        return _map;
    }
}
