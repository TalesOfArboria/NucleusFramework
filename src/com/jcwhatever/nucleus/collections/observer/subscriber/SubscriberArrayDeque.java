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

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

import com.jcwhatever.nucleus.utils.observer.ISubscriber;

/**
 * An array deque of {@link ISubscriber} which automatically removes agents
 * when they are disposed.
 *
 * <p>Thread safe.</p>
 *
 * <p>The lists iterators must be used inside a synchronized block which locks the
 * list instance. Otherwise, a {@link java.lang.IllegalStateException} is thrown.</p>
 */
public class SubscriberArrayDeque<E extends ISubscriber> extends SubscriberDeque<E> {

    private final ArrayDeque<E> _deque;

    /**
     * Constructor.
     */
    public SubscriberArrayDeque(int capacity) {
        _deque = new ArrayDeque<>(capacity);
    }

    /**
     * Constructor.
     *
     * @param collection  The initial collection to add.
     */
    public SubscriberArrayDeque(Collection<? extends E> collection) {
        _deque = new ArrayDeque<>(collection);
    }

    @Override
    protected Deque<E> queue() {
        return _deque;
    }
}
