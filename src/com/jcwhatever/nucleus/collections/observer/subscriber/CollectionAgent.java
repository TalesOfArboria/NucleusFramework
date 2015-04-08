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
import com.jcwhatever.nucleus.utils.observer.ISubscriberAgent;

import java.util.Set;

/**
 * A subscriber agent that collections can use for the purpose of automatically
 * removing elements that are disposed.
 *
 * <p>The {@link CollectionAgent} is a wrapper in case an implementation of the
 * collection needs to add its agent to a super class that uses {@link CollectionAgent}.
 */
public abstract class CollectionAgent implements ISubscriberAgent {

    private final ISubscriberAgent _agent;

    /**
     * Constructor.
     *
     * @param agent  The agent to encapsulate.
     */
    protected CollectionAgent(ISubscriberAgent agent)  {
        PreCon.notNull(agent);

        _agent = agent;
    }

    /**
     * Get the encapsulated agent.
     */
    public ISubscriberAgent getHandle() {
        return _agent;
    }


    /**
     * Invoked when a subscriber is removed from the {@link CollectionAgent}
     * so a collection can handle removing the appropriate element.
     *
     * @param subscriber  The removed subscriber.
     */
    protected abstract void removeFromCollection(ISubscriber subscriber);

    @Override
    public boolean addSubscriber(ISubscriber subscriber) {

        if (!_agent.registerReference(subscriber))
            return false;

        subscriber.registerReference(this);
        return true;
    }

    @Override
    public boolean removeSubscriber(ISubscriber subscriber) {
        if (safeUnregister(subscriber)) {
            removeFromCollection(subscriber);
            return true;
        }
        return false;
    }

    public final boolean safeUnregister(ISubscriber subscriber) {
        if (!_agent.unregisterReference(subscriber))
            return false;

        subscriber.unregisterReference(this);
        return true;
    }

    @Override
    public boolean registerReference(ISubscriber subscriber) {

        return _agent.registerReference(subscriber);
    }

    @Override
    public boolean unregisterReference(ISubscriber subscriber) {
        if (_agent.unregisterReference(subscriber)) {
            removeFromCollection(subscriber);
            return true;
        }
        return false;
    }

    @Override
    public Set<ISubscriber> getSubscribers() {
        return _agent.getSubscribers();
    }

    @Override
    public boolean isDisposed() {
        return _agent.isDisposed();
    }

    @Override
    public void dispose() {
        _agent.dispose();
    }

    @Override
    public int hashCode() {
        return _agent.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CollectionAgent) {
            return ((CollectionAgent) obj)._agent.equals(obj);
        }

        return obj != null && (obj == this || _agent.equals(obj));
    }
}
