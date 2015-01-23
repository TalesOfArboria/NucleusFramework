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

import com.jcwhatever.nucleus.collections.wrap.QueueWrapper;
import com.jcwhatever.nucleus.collections.wrap.SyncStrategy;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.observer.ISubscriber;
import com.jcwhatever.nucleus.utils.observer.ISubscriberAgent;
import com.jcwhatever.nucleus.utils.observer.SubscriberAgent;

import java.util.Collection;
import javax.annotation.Nullable;

/**
 * A queue of {@link ISubscriber} which automatically removes subscribers
 * when they are disposed.
 *
 * <p>Assumes the subscriber is properly implemented and calls the {@code #unregister} method
 * of all {@link ISubscriberAgent} instances that are registered to it when it's disposed.</p>
 *
 * <p>The collection has its own internal agent which is used to track the subscribers in
 * the collection. If an implementation wishes to add its own agent, it can do so by passing it
 * into the constructor.</p>
 *
 * <p>Implementations may need to use their own synchronization object, in which case it can be
 * passed in via the constructor using a {@link SyncStrategy}.</p>
 *
 * <p>The queues iterators must be used inside a synchronized block which locks the
 * queue instance. Otherwise, a {@link java.lang.IllegalStateException} is thrown.</p>
 */
public abstract class SubscriberQueue<E extends ISubscriber>
        extends QueueWrapper<E> implements IDisposable {

    protected final InternalAgent _collectionAgent;

    private transient boolean _isDisposed;

    /**
     * Constructor.
     *
     * <p>Synchronizes self and provides own subscriber agent.</p>
     */
    public SubscriberQueue() {
        this(SyncStrategy.SYNC, null);
    }

    /**
     * Constructor.
     *
     * @param strategy  The synchronization strategy to use.
     * @param agent     The agent to use. Optional. Creates a new one if null.
     */
    protected SubscriberQueue(SyncStrategy strategy, @Nullable ISubscriberAgent agent) {
        super(strategy);

        _collectionAgent = new InternalAgent(agent != null ? agent : new SubscriberAgent() {});
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        if (isDisposed())
            return;

        synchronized (_sync) {
            if (isDisposed())
                return;

            _collectionAgent.dispose();
            collection().clear();

            _isDisposed = true;
        }
    }

    @Override
    protected void onAdded(E e) {
        if (isDisposed())
            throw new RuntimeException("Cannot use a disposed SubscriberQueue.");

        _collectionAgent.register(e);
    }

    @Override
    protected void onRemoved(Object o) {
        _collectionAgent.safeUnregister((ISubscriber) o);
    }

    @Override
    protected void onClear(Collection<E> values) {
        for (E subscriber : values) {
            _collectionAgent.safeUnregister(subscriber);
        }
    }

    private class InternalAgent extends CollectionAgent {

        InternalAgent(ISubscriberAgent agent) {
            super(agent);
        }

        @Override
        protected void removeFromCollection(ISubscriber subscriber) {
            synchronized (_sync) {
                //noinspection SuspiciousMethodCalls
                collection().remove(subscriber);
            }
        }
    }
}
