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

import com.jcwhatever.nucleus.providers.npc.ai.INpcState;
import com.jcwhatever.nucleus.providers.npc.ai.NpcScriptBehaviour;
import com.jcwhatever.nucleus.utils.PreCon;

/**
 * An {@link INpcGoal} implementation that allows scripts to attach implementation
 * handlers via shorthand functions.
 */
public class NpcScriptGoal extends NpcScriptBehaviour implements INpcGoal {

    private IOnRunHandler _onRunHandler;
    private IOnFirstRun _firstRunHandler;

    /**
     * Constructor.
     *
     * @param goalName The name of the goal.
     */
    public NpcScriptGoal(String goalName) {
        super(goalName);
    }

    /**
     * Returns the result of the handler added via the {@link #onCanRun} method.
     *
     * <p>If a handler was not provided, returns true so long as a run handler
     * was added via the {@link #onRun(IOnRunHandler)} method.</p>
     *
     * <p>{@inheritDoc}</p>
     */
    @Override
    public boolean canRun(INpcState state) {
        return _onRunHandler != null && super.canRun(state);
    }

    @Override
    public void firstRun(INpcGoalAgent agent) {
        if (_firstRunHandler == null)
            return;

        _firstRunHandler.onFirstRun(agent);
    }

    @Override
    public void run(INpcGoalAgent agent) {
        if (_onRunHandler != null)
            _onRunHandler.run(agent);
        else
            agent.finish();
    }

    /**
     * Attach the onFirstRun handler. Optional.
     *
     * <p>Invoked the just before the first time the goal is run.</p>
     *
     * @param handler  The firstRun handler.
     *
     * @return  Self for chaining.
     */
    public NpcScriptBehaviour onFirstRun(IOnFirstRun handler) {
        PreCon.notNull(handler, "handler");

        _firstRunHandler = handler;

        return this;
    }

    /**
     * Attach the run handler.
     *
     * @param handler  The run handler.
     *
     * @return  Self for chaining.
     */
    public NpcScriptGoal onRun(IOnRunHandler handler) {
        PreCon.notNull(handler, "handler");

        _onRunHandler = handler;

        return this;
    }

    /**
     * onFirstRun handler for use by a script. Supports
     * shorthand functions.
     */
    public interface IOnFirstRun {

        /**
         * Invoked just before the goal is run for the first time.
         *
         * @param agent  The goals agent.
         */
        void onFirstRun(INpcGoalAgent agent);
    }

    /**
     * run handler for use by a script. Supports
     * shorthand functions.
     */
    public interface IOnRunHandler {

        /**
         * Invoked when the goals {@link INpcGoal#run}
         * method is invoked.
         *
         * @param agent  The goals agent.
         */
        void run(INpcGoalAgent agent);
    }
}
