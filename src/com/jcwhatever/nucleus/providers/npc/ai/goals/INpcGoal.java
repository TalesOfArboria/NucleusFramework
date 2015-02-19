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

package com.jcwhatever.nucleus.providers.npc.ai.goals;

import com.jcwhatever.nucleus.providers.npc.ai.INpcBehaviour;

/**
 * Interface for an NPC Goal.
 *
 * <p>A goal is a top level behaviour with priority. Goals with higher priorities
 * are selected to run over goals with lower priority. When 2 or more goals with
 * the same priority are able to run, the goal with the least cost is selected.</p>
 */
public interface INpcGoal extends INpcBehaviour {

    /**
     * Invoked just before the behaviour is run for the first time.
     *
     * @param state  The npc state.
     */
    void firstRun(INpcGoalAgent state);

    /**
     * Invoked every tick while the goal is running.
     *
     * @param agent  An {@link INpcGoalAgent} for use by the goal.
     *
     * @return  The action result.
     */
    void run(INpcGoalAgent agent);
}
