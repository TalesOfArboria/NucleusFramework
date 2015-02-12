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

import com.jcwhatever.nucleus.utils.PreCon;

/**
 * A goal designed to be added from a script. Supports script
 * shorthand functions.
 */
public class NpcScriptGoal implements INpcGoal {

    private Runnable _onReset;
    private IOnRunHandler _onRunHandler;
    private IShouldRunHandler _shouldRunHandler;
    private NpcGoalResult _result = NpcGoalResult.CONTINUE;
    private ResultSetter _resultSetter = new ResultSetter();

    @Override
    public void reset() {
        if (_onReset != null)
            _onReset.run();

        _result = NpcGoalResult.CONTINUE;
    }

    @Override
    public NpcGoalResult run() {

        if (_onRunHandler == null)
            return NpcGoalResult.FINISH;

        _onRunHandler.run(_resultSetter);

        return _result;
    }

    @Override
    public boolean shouldRun() {
        if (_shouldRunHandler != null && _shouldRunHandler.shouldRun()) {
            _result = NpcGoalResult.CONTINUE;
            return true;
        }
        return false;
    }

    /**
     * Attach the reset handler.
     *
     * @param runnable  The reset handler.
     *
     * @return  Self for chaining.
     */
    public NpcScriptGoal onReset(Runnable runnable) {
        PreCon.notNull(runnable, "runnable");
        _onReset = runnable;

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
     * Attach the shouldRun handler.
     *
     * @param handler  The shouldRun handler.
     *
     * @return  Self for chaining.
     */
    public NpcScriptGoal onShouldRun(IShouldRunHandler handler) {
        PreCon.notNull(handler, "handler");

        _shouldRunHandler = handler;

        return this;
    }

    /**
     * shouldRun handler for use by a script. Supports
     * shorthand functions.
     */
    public interface IShouldRunHandler {
        boolean shouldRun();
    }

    /**
     * run handler for use by a script. Supports
     * shorthand functions.
     */
    public interface IOnRunHandler {

        /**
         * Invoked when the goals {@code run} method is invoked.
         *
         * @param setter  The result setter. By default the goal returns the
         *                {@code CONTINUE} result. Invoke the {@code #finish} method
         *                in the setter to finish the goal or invoke the {@code #finishAndRemove}
         *                method to finish and remove the goal.
         */
        void run(ResultSetter setter);
    }

    /**
     * Result setter that is passed into the scripts run handler.
     */
    public class ResultSetter {

        /**
         * Invoke to finish the goal.
         */
        public void finish() {
            _result = NpcGoalResult.FINISH;
        }

        /**
         * Invoke to finish and remove the goal.
         */
        public void finishAndRemove() {
            _result = NpcGoalResult.FINISH_REMOVE;
        }
    }
}
