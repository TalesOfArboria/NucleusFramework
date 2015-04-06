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

package com.jcwhatever.nucleus.utils.observer.event;

import com.jcwhatever.nucleus.utils.observer.ISubscriberAgent;

import javax.annotation.Nullable;

/**
 * An agent between producers of events and {@link IEventSubscriber}'s registered
 * with the agent.
 *
 * <p>The agent accepts {@link com.jcwhatever.nucleus.utils.observer.ISubscriber}
 * instances for registration as all {@link ISubscriberAgent} implementations must.
 * However the {@link IEventSubscriber} interface should be used by subscribers that
 * wish to receive events from the agent. The implementation decides how
 * the non-event subscribers are used but must at the least hold a reference
 * to the subscriber until it un-registers.</p>
 *
 * <p>If you're usage context does not require event priorities and event cancelling,
 * it is recommended to use {@link com.jcwhatever.nucleus.utils.observer.update.IUpdateSubscriber}
 * in conjunction with {@link com.jcwhatever.nucleus.utils.observer.update.IUpdateAgent}
 * instead.</p>
 */
public interface IEventAgent<E> extends ISubscriberAgent {

    /**
     * Invoked by an event producer to notify event subscribers.
     *
     * @param caller  Optional object that is the caller of the event.
     * @param event   The event.
     */
    void call(@Nullable Object caller, E event);
}
