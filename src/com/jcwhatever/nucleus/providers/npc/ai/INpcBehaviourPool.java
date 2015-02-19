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

package com.jcwhatever.nucleus.providers.npc.ai;

import com.jcwhatever.nucleus.providers.npc.INpc;

import javax.annotation.Nullable;

/**
 * A pool of behaviours.
 *
 * <p>The behaviour pool is used to select 1 of possibly many behaviours
 * based on the cost of each behaviour or other factors determined by
 * the implementation.</p>
 */
public interface INpcBehaviourPool<T extends INpcBehaviour> {

    /**
     * Get the NPC the behaviour pool is for.
     */
    INpc getNpc();

    /**
     * Reset all behaviours in the pool.
     *
     * @return  Self for chaining.
     */
    INpcBehaviourPool reset();

    /**
     * Add a behaviour to the pool.
     *
     * @param behaviour  The behaviour.
     *
     * @return  Self for chaining.
     */
    INpcBehaviourPool add(T behaviour);

    /**
     * Remove a behaviour from the pool.
     *
     * @param behaviour  The behaviour to remove.
     *
     * @return  True if found and removed, otherwise false.
     */
    @Nullable
    boolean remove(T behaviour);

    /**
     * Clear all behaviours.
     *
     * @return  Self for chaining.
     */
    INpcBehaviourPool clear();

    /**
     * Run a behaviour without adding it.
     *
     * <p>Temporarily gives the behaviour highest priority until it finishes. When
     * the behaviour finishes it is removed.</p>
     *
     * <p>If another goal is already running, that goal is paused.</p>
     *
     * @param behaviour  The behaviour to run.
     *
     * @return  Self for chaining.
     */
    INpcBehaviourPool run(T behaviour);

    /**
     * Select the behaviour to run from the current pool of goals.
     *
     * <p>Used to force a behaviour to run. Temporarily gives the
     * behaviour highest priority until it finishes.</p>
     *
     * <p>If another behaviour is already running, that behaviour is paused.
     * The behaviour will only be selected if its {@link INpcBehaviour#canRun} method
     * returns true.</p>
     *
     * @param goalName  The name of the behaviour to select.
     *
     * @return  Self for chaining.
     */
    INpcBehaviourPool select(String goalName);

}
