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

package com.jcwhatever.nucleus.providers.npc.navigator;

import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.utils.observer.script.IScriptUpdateSubscriber;

/**
 * Interface for an object that accepts NPC navigator script event subscribers.
 *
 * <p>The implementing type represents the scope of the subscription. For example,
 * a subscriber attached to a {@link com.jcwhatever.nucleus.providers.npc.INpcRegistry}
 * will receive updates for all NPC's created by the registry whereas a subscriber attached to a
 * {@link INpc} will receive updates only for the NPC it is attached to and only for
 * the lifetime of the NPC.</p>
 */
public interface INpcNavScriptEvents {

    /**
     * Attaches a subscriber to the navigator that is updated
     * whenever there is a navigator starts.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcNavScriptEvents onNavStart(IScriptUpdateSubscriber<INpc> subscriber);

    /**
     * Attaches a subscriber to the navigator that is updated
     * whenever the navigator is paused.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcNavScriptEvents onNavPause(IScriptUpdateSubscriber<INpc> subscriber);

    /**
     * Attaches a subscriber to the navigator that is updated
     * whenever the navigator is stopped.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcNavScriptEvents onNavCancel(IScriptUpdateSubscriber<INpc> subscriber);

    /**
     * Attaches a subscriber to the navigator that is updated
     * whenever the navigator completes.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcNavScriptEvents onNavComplete(IScriptUpdateSubscriber<INpc> subscriber);

    /**
     * Attaches a subscriber to the navigator that is updated
     * whenever the navigator times out (gets stuck).
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcNavScriptEvents onNavTimeout(IScriptUpdateSubscriber<INpc> subscriber);
}
