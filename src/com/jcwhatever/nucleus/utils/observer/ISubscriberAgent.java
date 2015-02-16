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
 * A type that intermediates between a producer and subscriber and may possibly act
 * as a producer. The type must carry a reference to all of its subscribers/observers.
 *
 * <p>The {@link ISubscriberAgent} implementation is expected to remove
 * itself from all of its subscribers using the {@link ISubscriber#unregister} method
 * that subscribers provide if it is disposed.</p>
 *
 * <p>The agent is expected to hold a reference to any and all {@link ISubscriber}'s
 * that register/add, even if the agent implementation cannot interact with the
 * subscriber implementation. The agent must hold the reference to the subscriber
 * and treat it as a member through methods of the {@link ISubscriberAgent} interface
 * until the subscriber is de-registered/removed. (i.e all registered subscribers are
 * visible through the {@link java.util.Set} returned by the {@link #getSubscribers} method.)</p>
 */
public interface ISubscriberAgent extends IDisposable {

    /**
     * Register a subscriber. Functionally the same as {@link #addSubscriber}
     * except the subscribers {@link ISubscriber#addAgent} method is invoked with
     * the current {@link ISubscriberAgent} as its argument.
     *
     * @param subscriber  The subscriber to register.
     */
    public boolean register(ISubscriber subscriber);

    /**
     * Unregister a subscriber. Functionally the same as {@link #removeSubscriber}
     * except the subscribers {@link ISubscriber#removeAgent} method is invoked with
     * the {@link ISubscriberAgent} as its argument.
     *
     * @param subscriber  The subscriber to unregister.
     */
    public boolean unregister(ISubscriber subscriber);

    /**
     * Adds a subscriber. Functionally the same as {@link #register}
     * except that the subscribers {@link ISubscriber#addAgent} method isn't
     * invoked. Should only be invoked by a subscriber that already has a reference
     * to the agent.
     *
     * @param subscriber  The subscriber to add.
     *
     * @return  True if the subscriber was added.
     */
    public boolean addSubscriber(ISubscriber subscriber);

    /**
     * Remove a subscriber. Functionally the same as {@link #unregister}
     * except that the subscribers {@link ISubscriber#removeAgent} method isn't
     * invoked. Should only be invoked by a subscriber that has already removed its
     * reference to the agent.
     *
     * @param subscriber  The subscriber to remove.
     *
     * @return  True if found and removed.
     */
    public boolean removeSubscriber(ISubscriber subscriber);

    /**
     * Get all subscribers of the agent.
     */
    public Set<ISubscriber> getSubscribers();
}
