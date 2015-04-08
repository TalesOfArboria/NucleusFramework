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

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.ISubscriber;
import com.jcwhatever.nucleus.utils.observer.ISubscriberAgent;

import java.util.Set;

/**
 * A {@link ISubscriber} that collections of {@link ISubscriberAgent} can use for the purpose
 * of automatically removing elements that are disposed.
 *
 * <p>The {@link CollectionSubscriber} is a wrapper in case an implementation of the
 * collection needs to add its subscriber to a super class that uses {@link CollectionSubscriber}.
 */
public abstract class CollectionSubscriber implements ISubscriber {

    private final ISubscriber _subscriber;

    /**
     * Constructor.
     *
     * @param subscriber  The subscriber to encapsulate.
     */
    protected CollectionSubscriber(ISubscriber subscriber) {
        PreCon.notNull(subscriber);

        _subscriber = subscriber;
    }

    /**
     * Get the encapsulated subscriber.
     */
    public ISubscriber getHandle() {
        return _subscriber;
    }

    /**
     * Invoked when an agent is removed from the {@link CollectionSubscriber} so a collection
     * can handle removing the appropriate element.
     *
     * @param agent  The removed agent.
     */
    protected abstract void removeFromCollection(ISubscriberAgent agent);

    @Override
    public boolean subscribe(ISubscriberAgent agent) {
        if (!_subscriber.registerReference(agent))
            return false;

        agent.registerReference(this);
        return true;
    }

    @Override
    public boolean unsubscribe(ISubscriberAgent agent) {
        if (!_subscriber.unregisterReference(agent))
            return false;

        agent.unregisterReference(this);

        removeFromCollection(agent);

        return true;
    }

    public final boolean safeUnregister(ISubscriberAgent agent) {
        return _subscriber.unsubscribe(agent);
    }

    @Override
    public boolean registerReference(ISubscriberAgent agent) {
        return _subscriber.registerReference(agent);
    }

    @Override
    public boolean unregisterReference(ISubscriberAgent agent) {
        if (_subscriber.unregisterReference(agent)) {
            removeFromCollection(agent);
            return true;
        }
        return false;
    }

    @Override
    public Set<ISubscriberAgent> getAgents() {
        return _subscriber.getAgents();
    }

    @Override
    public boolean isDisposed() {
        return _subscriber.isDisposed();
    }

    @Override
    public void dispose() {
        _subscriber.dispose();
    }

    @Override
    public int hashCode() {
        return _subscriber.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CollectionSubscriber) {
            return ((CollectionSubscriber) obj)._subscriber.equals(obj);
        }
        return _subscriber.equals(obj);
    }
}
