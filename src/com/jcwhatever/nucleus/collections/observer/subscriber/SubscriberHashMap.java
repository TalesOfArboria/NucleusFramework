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

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.ISubscriber;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link HashMap} based implementation of a {@link SubscriberMap} which automatically
 * removes subscribers when they are disposed.
 */
public class SubscriberHashMap<K, V extends ISubscriber> extends SubscriberMap<K, V> {

    private final Map<K, V> _map;

    /**
     * Constructor.
     *
     * <p>The initial capacity is 7. The load factor is 0.75</p>
     */
    public SubscriberHashMap() {
        this(7, 0.75f);
    }

    /**
     * Constructor.
     *
     * <p>The load factor is 0.75</p>
     *
     * @param size  The initial capacity of the map.
     */
    public SubscriberHashMap(int size) {
        this(size, 0.75f);
    }

    /**
     * Constructor.
     *
     * @param size        The initial capacity of the map.
     * @param loadFactor  The map load factor.
     */
    public SubscriberHashMap(int size, float loadFactor) {
        _map = new HashMap<>(size, loadFactor);
    }

    /**
     * Constructor.
     *
     * @param map  The initial map entries.
     */
    public SubscriberHashMap(Map<? extends K, ? extends V> map) {
        PreCon.notNull(map);
        _map = new HashMap<>(map);
    }

    @Override
    protected Map<K, V> map() {
        return _map;
    }
}
