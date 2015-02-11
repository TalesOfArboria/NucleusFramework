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

package com.jcwhatever.nucleus.providers.npc.goals;

import com.jcwhatever.nucleus.providers.npc.INpc;

import java.util.Collection;

/**
 * Interface for an NPC's goal manager.
 */
public interface INpcGoals {

    /**
     * Get the NPC the goals are for.
     */
    INpc getNPC();

    /**
     * Get all goals.
     */
    Collection<INpcGoal> all();

    /**
     * Add a goal.
     *
     * @param priority  The priority of the goal. A larger number is higher priority.
     * @param goal      The goal to add.
     *
     * @return  Self for chaining.
     */
    INpcGoals add(int priority, INpcGoal goal);

    /**
     * Remove a goal.
     *
     * @param goal  The goal to remove.
     *
     * @return  True if the goal was found and removed.
     */
    boolean remove(INpcGoal goal);

    /**
     * Clear all goals.
     *
     * @return  Self for chaining.
     */
    INpcGoals clear();

    /**
     * Set the currently running goal.
     *
     * @param goal The goal to run.
     */
    INpcGoals setGoal(INpcGoal goal);

    /**
     * runs the provided goals simultaneously with the current running goal.
     *
     * <p>The goals {@code #shouldRun} method is still invoked to determine if the
     * goals should be run.</p>
     *
     * @param goals  The goals to run.
     */
    INpcGoals runGoals(INpcGoal... goals);
}
