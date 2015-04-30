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

package com.jcwhatever.nucleus.utils.observer;

import com.jcwhatever.nucleus.utils.PreCon;

import java.util.HashSet;
import java.util.Set;

/**
 * An abstract implementation of an {@link ISubscriber}.
 *
 * <p>Provides the basic implementation of subscribing to and un-subscribing from
 * agents as well as handling disposal.</p>
 */
public abstract class Subscriber implements ISubscriber {

    private Set<ISubscriberAgent> _agents;
    private volatile boolean _isDisposed;
    private final Object _sync = new Object();

    @Override
    public boolean subscribe(ISubscriberAgent agent) {
        PreCon.notNull(agent);

        synchronized (getSync()) {
            if (registerReference(agent)) {
                agent.registerReference(this);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean unsubscribe(ISubscriberAgent agent) {
        synchronized (getSync()) {

            if (unregisterReference(agent)) {
                agent.unregisterReference(this);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean registerReference(ISubscriberAgent agent) {
        PreCon.notNull(agent);

        synchronized (getSync()) {

            if (isDisposed())
                throw new IllegalStateException("Cannot use a disposed subscriber.");

            return agents().add(agent);
        }
    }

    @Override
    public boolean unregisterReference(ISubscriberAgent agent) {
        PreCon.notNull(agent);

        synchronized (getSync()) {
            return hasAgents() && agents().remove(agent);
        }
    }

    @Override
    public Set<ISubscriberAgent> getAgents() {
        synchronized (getSync()) {

            if (!hasAgents())
                return new HashSet<>(0);

            return new HashSet<>(agents());
        }
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {

        if (_isDisposed)
            return;

        synchronized (getSync()) {

            if (_isDisposed)
                return;

            if (hasAgents()) {

                for (ISubscriberAgent agent : agents()) {
                    agent.unregisterReference(this);
                }

                agents().clear();
            }

            _isDisposed = true;
        }
    }

    /**
     * Get the set of agents.
     */
    protected Set<ISubscriberAgent> agents() {
        if (_agents == null)
            _agents = new HashSet<>(3);

        return _agents;
    }

    /**
     * Determine if there is at least 1 agent.
     */
    protected boolean hasAgents() {
        return _agents != null && !_agents.isEmpty();
    }

    /**
     * Get the object used for synchronization.
     */
    protected Object getSync() {
        return _sync;
    }
}
