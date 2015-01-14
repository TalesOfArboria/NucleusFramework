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

import com.jcwhatever.nucleus.collections.ElementCounter;
import com.jcwhatever.nucleus.collections.ElementCounter.RemovalPolicy;
import com.jcwhatever.nucleus.collections.concurrent.SyncMultimap;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.ISubscriber;
import com.jcwhatever.nucleus.utils.observer.ISubscriberAgent;
import com.jcwhatever.nucleus.utils.observer.Subscriber;
import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

/**
 * A {@link com.google.common.collect.Multimap} of {@link ISubscriberAgent} which
 * automatically removes agents when they are disposed.
 *
 * <p>Assumes the agent is properly implemented and calls the {@code #unregister} method
 * of all {@link com.jcwhatever.nucleus.utils.observer.ISubscriber} instances that are
 * observing it when it's disposed.</p>
 *
 * <p>The map has its own internal subscriber which is used to observe the agents in
 * the map. If an implementation wishes to add its own subscriber, it can do so by
 * passing it into the constructor.</p>
 *
 * <p>Implementations may need to use their own synchronization object, in which case it can be
 * passed in via the constructor.</p>
 */
public abstract class AgentMultimap<K, V extends ISubscriberAgent>
        extends SyncMultimap<K, V> implements IDisposable {

    private final Object _sync;
    private final MapSubscriber _mapSubscriber;
    private final ElementCounter<ISubscriberAgent> _counter = new ElementCounter<>(RemovalPolicy.REMOVE);

    private volatile boolean _isDisposed;

    /**
     * Constructor.
     *
     * <p>Uses a private synchronization object and subscriber.</p>
     */
    public AgentMultimap() {
        this(new Object(), null);
    }

    /**
     * Constructor.
     *
     * @param sync        The synchronization object to use.
     * @param subscriber  The subscriber to use. Optional. A new one is created if null.
     */
    protected AgentMultimap(Object sync, @Nullable ISubscriber subscriber) {
        super(sync);

        _sync = sync;
        _mapSubscriber = new MapSubscriber(subscriber != null ? subscriber : new Subscriber() {});
    }

    /**
     * Get all subscribers for all agents in the map.
     */
    public List<ISubscriber> getSubscribers() {

        Collection<V> agents;

        synchronized (_sync) {
            agents = new ArrayList<>(map().values());
        }

        List<ISubscriber> result = new ArrayList<>(agents.size() * 3);

        for (ISubscriberAgent agent : agents) {
            result.addAll(agent.getSubscribers());
        }

        return result;
    }

    /**
     * Unregister a subscriber from all agents in the collection.
     *
     * @param subscriber  The subscriber to unregister.
     *
     * @return  True if the subscriber was found in 1 or more producers and removed.
     */
    public boolean unregisterAll(ISubscriber subscriber) {
        PreCon.notNull(subscriber);

        Collection<V> agents;

        synchronized (_sync) {
            agents = new ArrayList<>(map().values());
        }

        boolean isChanged = false;
        for (ISubscriberAgent agent : agents) {
            isChanged = agent.unregister(subscriber) || isChanged;
        }

        return isChanged;
    }

    /**
     * Same as {@code #put} except returns own instance
     * for chaining.
     *
     * @param key    The key.
     * @param value  The agent.
     *
     * @param <T> Self instance type.
     *
     * @return  Self for chaining.
     */
    public <T extends AgentMultimap<K, V>> T set(K key, V value) {
        put(key, value);

        @SuppressWarnings("unchecked")
        T self = (T)this;

        return self;
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        if (_isDisposed)
            return;

        synchronized (_sync) {

            if (_isDisposed)
                return;

            _mapSubscriber.dispose();
            map().clear();

            _isDisposed = true;
        }
    }

    @Override
    protected void onPut(K key, V value) {
        if (isDisposed())
            throw new RuntimeException("Cannot use a disposed AgentMultimap.");

        registerAgent(value);
    }

    @Override
    protected void onPutAll(K key, Iterable<? extends V> values) {
        for (V value : values) {
            registerAgent(value);
        }
    }

    @Override
    protected void onRemove(Object key, Object value) {

        @SuppressWarnings("unchecked")
        V agent = (V)value;

        unregisterAgent(agent);
    }

    @Override
    protected void onRemoveAll(Object key, Collection<V> values) {
        for (V value : values) {
            unregisterAgent(value);
        }
    }

    @Override
    protected void onClear(Collection<Entry<K, V>> entries) {
        for (Entry<K, V> entry : entries) {
            unregisterAgent(entry.getValue());
        }
    }

    private void registerAgent(V agent) {
        int count;
        synchronized (_sync) {
            _counter.add(agent);
            count = _counter.count(agent);
        }

        if (count == 1) {
            _mapSubscriber.register(agent);
        }
    }

    private void unregisterAgent(V agent) {
        int count;
        synchronized (_sync) {
            _counter.subtract(agent);
            count = _counter.count(agent);
        }

        if (count == 0) {
            _mapSubscriber.safeUnregister(agent);
        }
    }

    private class MapSubscriber extends CollectionSubscriber {

        MapSubscriber(ISubscriber subscriber) {
            super(subscriber);
        }

        @Override
        protected void removeFromCollection(ISubscriberAgent agent) {
            synchronized (_sync) {
                //noinspection SuspiciousMethodCalls
                CollectionUtils.removeValue(map(), agent);
            }
        }
    }
}
