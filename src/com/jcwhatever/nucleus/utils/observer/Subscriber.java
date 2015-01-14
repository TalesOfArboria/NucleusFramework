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
 * An abstract implementation of a subscriber.
 *
 * <p>Provides the basic implementation of tracking and removing
 * agents and handling disposal.</p>
 */
public abstract class Subscriber implements ISubscriber {

    private Set<ISubscriberAgent> _agents;
    private volatile boolean _isDisposed;
    protected final Object _sync = new Object();

    @Override
    public boolean register(ISubscriberAgent agent) {
        PreCon.notNull(agent);

        synchronized (_sync) {
            if (addAgent(agent)) {
                agent.addSubscriber(this);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean unregister(ISubscriberAgent agent) {
        synchronized (_sync) {

            if (removeAgent(agent)) {
                agent.removeSubscriber(this);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addAgent(ISubscriberAgent agent) {
        PreCon.notNull(agent);

        synchronized (_sync) {

            if (isDisposed())
                throw new RuntimeException("Cannot use a disposed subscriber.");

            return agents().add(agent);
        }
    }

    @Override
    public boolean removeAgent(ISubscriberAgent agent) {
        PreCon.notNull(agent);

        synchronized (_sync) {
            return hasAgents() && agents().remove(agent);
        }
    }

    @Override
    public Set<ISubscriberAgent> getAgents() {
        synchronized (_sync) {

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

        synchronized (_sync) {

            if (_isDisposed)
                return;

            if (hasAgents()) {

                for (ISubscriberAgent agent : agents()) {
                    agent.removeSubscriber(this);
                }

                _agents.clear();
            }

            _isDisposed = true;
        }
    }

    protected Set<ISubscriberAgent> agents() {
        if (_agents == null)
            _agents = new HashSet<>(3);

        return _agents;
    }

    protected boolean hasAgents() {
        return _agents != null && !_agents.isEmpty();
    }
}
