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

import com.jcwhatever.nucleus.mixins.INamed;

/**
 * Interface for an NPC behaviour.
 *
 * <p>A behaviour is selected to run from a pool/collection of behaviours
 * based on if it can be run. Among the behaviours that can run, the behaviour
 * with the least cost is selected.</p>
 */
public interface INpcBehaviour extends INamed {

    /**
     * Invoke to reset the behaviour.
     *
     * @param state  The npc state.
     */
    void reset(INpcState state);

    /**
     * Determine if the behaviour can be run.
     *
     * <p>This is invoked before invoking {@link #getCost}</p>
     *
     * @param state  The npc state.
     *
     * @return True if the action can be run, otherwise false. Returning true does not
     * guarantee the action will be run.
     */
    boolean canRun(INpcState state);

    /**
     * Get the cost of running the behaviour.
     *
     * <p>Invoked if {@link #canRun} returns true. Used to find the behaviour with
     * the least cost to perform.</p>
     *
     * @param state  The npc state.
     */
    float getCost(INpcState state);

    /**
     * Invoked when the behaviour is paused.
     *
     * @param state  The npc state.
     */
    void pause(INpcState state);
}
