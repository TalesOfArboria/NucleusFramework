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

import com.jcwhatever.nucleus.mixins.IDisposable;

import java.util.Set;

/**
 * A type that observes an agent. When the type is disposed, it must unregister
 * itself from all of its agents as well as release its agent references.
 *
 * <p>The subscriber is expected to hold a reference to any and all
 * {@link ISubscriberAgent}'s that register or add themselves to the subscriber,
 * even if the subscriber implementation is not compatible with the agent
 * implementation. The subscriber must hold the reference and treat it as a
 * member in any {@link ISubscriber} methods until the agent is
 * unregistered/removed.</p>
 *
 * <p>When the subscriber is disposed, it removes itself from all its registered
 * agents by invoking the agents {@link ISubscriberAgent#removeSubscriber} method.</p>
 */
public interface ISubscriber extends IDisposable {

    /**
     * Register an agent.
     *
     * <p>Equivalent to invoking {@link #addAgent} except the
     * {@link ISubscriberAgent#addSubscriber} method of the agent being registered
     * is invoked with the subscriber as its argument.</p>
     *
     * @param agent  The agent to register.
     *
     * @return  True if the agent was registered.
     */
    boolean register(ISubscriberAgent agent);

    /**
     * Unregister an agent.
     *
     * <p>Equivalent to invoking {@link #removeAgent} except the
     * {@link ISubscriberAgent#removeSubscriber} method of the agent being unregistered
     * is invoked with the subscriber as its argument.</p>
     *
     * @param agent  The agent to unregister.
     *
     * @return  True if the agent was unregistered.
     */
    boolean unregister(ISubscriberAgent agent);

    /**
     * Add an agent.
     *
     * <p>Equivalent to invoking {@link #register} except the added agents
     * {@link ISubscriberAgent#addSubscriber} method isn't invoked.</p>
     *
     * <p>Should only be invoked by an {@link ISubscriberAgent} implementation that
     * already has a reference to the subscriber due to its
     * {@link ISubscriberAgent#register(ISubscriber)} method being invoked.
     * Avoid this method for all other contexts.</p>
     *
     * @param agent  The agent to add.
     *
     * @return  True if the agent was added.
     */
    boolean addAgent(ISubscriberAgent agent);

    /**
     * Remove an agent.
     *
     * <p>Functionally the same as {@link #unregister} except the agents
     * {@link ISubscriberAgent#removeSubscriber} method isn't invoked. Should only
     * be invoked by an agent that has already removed its reference to the
     * subscriber.</p>
     *
     * <p>Should only be invoked by an {@link ISubscriberAgent} implementation that
     * already removed its reference to the subscriber due to its
     * {@link ISubscriberAgent#unregister(ISubscriber)} method being invoked.
     * Avoid this method for all other contexts.</p>
     *
     * @param agent  The agent to remove.
     *
     * @return  True if the agent was removed.
     */
    boolean removeAgent(ISubscriberAgent agent);

    /**
     * Get all of the agents that are subscribed to by the instance.
     */
    Set<ISubscriberAgent> getAgents();
}
