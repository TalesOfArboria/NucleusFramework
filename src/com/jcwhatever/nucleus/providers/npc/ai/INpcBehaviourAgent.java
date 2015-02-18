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

import com.jcwhatever.nucleus.providers.npc.INpcScriptEvents;
import com.jcwhatever.nucleus.providers.npc.ai.actions.INpcAction;
import com.jcwhatever.nucleus.providers.npc.navigator.INpcNavScriptEvents;

/**
 * Interface for a behaviour agent.
 *
 * <p>Used to run actions in the agent action pool and declare when the current
 * behaviour is finished.</p>
 */
public interface INpcBehaviourAgent extends INpcScriptEvents, INpcNavScriptEvents {

    /**
     * Get the number of times the agent has been run since the
     * last time its behaviour was reset.
     */
    long getRunCount();

    /**
     * Determine if the agent run count is 0.
     */
    boolean isFirstRun();

    /**
     * Get the NPC state.
     */
    INpcState getState();

    /**
     * Get the behaviour pool.
     *
     * <p>The returned pool is the pool of child behaviours of the behaviour the
     * agent is responsible for, not the pool the agent behaviour is part of.</p>
     */
    INpcBehaviourPool getPool();

    /**
     * Ends the running behaviour.
     */
    void finish();

    /**
     * Create an action composed of multiple actions that run in parallel.
     *
     * <p>The parallel action does not finish until all child actions finish.</p>
     *
     * @param actions  The actions to include.
     *
     * @return  The parallel action.
     */
    INpcAction createParallelActions(INpcAction... actions);

    /**
     * Create an action composed of multiple actions that are run in parallel.
     *
     * <p>Similar to a parallel action except that the blended action finishes when
     * any of the child actions finish.</p>
     *
     * @param actions  The actions to include.
     *
     * @return  The blended action.
     */
    INpcAction createBlendedActions(INpcAction... actions);

    /**
     * Create an action composed of multiple actions that run one after the other.
     *
     * <p>The serial action does not finish until all child actions finish.</p>
     *
     * <p>Child actions are run in order provided, one at a time. Each child
     * action is run until it declares itself finished by invoking the
     * {@link #finish} method, at which point the next action is run.</p>
     *
     * <p>If an action cannot run, it is skipped.</p>
     *
     * @param actions  The actions to include.
     *
     * @return  The serial action.
     */
    INpcAction createSerialActions(INpcAction... actions);
}
