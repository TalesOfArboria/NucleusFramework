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
 * A type that intermediates between a producer and subscriber and may possibly
 * act as a producer.
 *
 * <p>The type must keep a reference to all of its subscribers.</p>
 *
 * <p>The implementation is expected to remove itself from all of its subscribers
 * using the {@link ISubscriber#unsubscribe} method that subscribers provide if
 * the agent is disposed.</p>
 *
 * <p>The implementation is expected to hold a reference to any and all
 * {@link ISubscriber}'s that register or add themselves, even if the agent
 * implementation cannot interact with the subscriber implementation. The agent must
 * keep the reference to the subscriber and treat it as a member through methods of
 * the {@link ISubscriberAgent} interface until the subscriber is
 * de-registered/removed.</p>
 */
public interface ISubscriberAgent extends IDisposable {

    /**
     * Subscribe a subscriber to the agent.
     *
     * <p>Equivalent to invoking {@link #registerReference} except the subscribers
     * {@link ISubscriber#registerReference} method is invoked to simultaneously add
     * the agent to the subscriber being registered.</p>
     *
     * @param subscriber  The subscriber to register.
     */
    boolean addSubscriber(ISubscriber subscriber);

    /**
     * Unsubscribe a subscriber from the agent.
     *
     * <p>Equivalent to invoking {@link #unregisterReference} except the subscribers
     * {@link ISubscriber#unregisterReference} method is invoked with to simultaneously
     * remove the agent from the subscriber being unregistered.</p>
     *
     * @param subscriber  The subscriber to unregister.
     */
    boolean removeSubscriber(ISubscriber subscriber);

    /**
     * Adds a subscriber.
     *
     * <p>Equivalent to invoking {@link #addSubscriber} except that the subscribers
     * {@link ISubscriber#registerReference} method is NOT invoked.</p>
     *
     * <p>Should only be invoked by an {@link ISubscriber} implementation that
     * already has a reference to the subscriber due to its
     * {@link ISubscriber#subscribe(ISubscriberAgent)} method being invoked.
     * Avoid this method for all other contexts.</p>
     *
     * @param subscriber  The subscriber to add.
     *
     * @return  True if the subscriber was added.
     */
    boolean registerReference(ISubscriber subscriber);

    /**
     * Remove a subscriber.
     *
     * <p>Equivalent to invoking {@link #removeSubscriber} except that the subscribers
     * {@link ISubscriber#unregisterReference} method is NOT invoked.
     *
     * <p>Should only be invoked by an {@link ISubscriber} implementation that
     * already has already removed its reference to the subscriber due to its
     * {@link ISubscriber#unsubscribe(ISubscriberAgent)} method being invoked.
     * Avoid this method for all other contexts.</p>
     *
     * @param subscriber  The subscriber to remove.
     *
     * @return  True if found and removed.
     */
    boolean unregisterReference(ISubscriber subscriber);

    /**
     * Get all subscribers of the agent.
     */
    Set<ISubscriber> getSubscribers();
}
