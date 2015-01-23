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

import com.jcwhatever.nucleus.utils.observer.ISubscriberAgent;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

/**
 * A linked list of {@link ISubscriberAgent} which automatically removes agents
 * when they are disposed.
 *
 * <p>Thread safe.</p>
 *
 * <p>Implementations may need to use their own synchronization object, in which case it can be
 * passed in via the constructor using a {@link com.jcwhatever.nucleus.collections.wrap.SyncStrategy}.</p>
 *
 * <p>The lists iterators must be used inside a synchronized block which locks the
 * list instance. Otherwise, a {@link java.lang.IllegalStateException} is thrown.</p>
 */
public class AgentLinkedList<E extends ISubscriberAgent> extends AgentDeque<E> {

    private final LinkedList<E> _list;

    /**
     * Constructor.
     */
    public AgentLinkedList() {
        _list = new LinkedList<>();
    }

    /**
     * Constructor.
     *
     * @param collection  The initial collection to start with.
     */
    public AgentLinkedList(Collection<? extends E> collection) {
        _list = new LinkedList<>(collection);
    }

    @Override
    protected Deque<E> queue() {
        return _list;
    }
}
