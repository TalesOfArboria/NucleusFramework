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

import com.jcwhatever.nucleus.providers.npc.ai.actions.INpcActionSelector;
import com.jcwhatever.nucleus.utils.PreCon;

/**
 * A goal designed to be added from a script. Supports script
 * shorthand functions.
 */
public class NpcScriptGoal implements INpcGoal {

    private Runnable _onReset;
    private IOnRunHandler _onRunHandler;
    private ICanRunHandler _canRunHandler;
    private boolean _isTransient;

    @Override
    public void reset() {
        if (_onReset != null)
            _onReset.run();
    }

    @Override
    public void run(INpcActionSelector director) {
        if (_onRunHandler != null)
            _onRunHandler.run(director);
        else
            director.finish(null);
    }

    @Override
    public boolean canRun() {
        if (_canRunHandler != null && _canRunHandler.canRun()) {
            return true;
        }
        return false;
    }

    /**
     * Determine if the goal is removed when it is finished.
     *
     * <p>Default value is false.</p>
     */
    public boolean isTransient() {
        return _isTransient;
    }

    /**
     * Set the transient flag on the goal.
     *
     * @param isTransient  True to remove the goal when it finishes, otherwise false.
     *
     * @return  Self for chaining.
     */
    public NpcScriptGoal setTransient(boolean isTransient) {
        _isTransient = isTransient;
        return this;
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
     * Attach the canRun handler.
     *
     * @param handler  The canRun handler.
     *
     * @return  Self for chaining.
     */
    public NpcScriptGoal onCanRun(ICanRunHandler handler) {
        PreCon.notNull(handler, "handler");

        _canRunHandler = handler;

        return this;
    }

    /**
     * canRun handler for use by a script. Supports
     * shorthand functions.
     */
    public interface ICanRunHandler {
        boolean canRun();
    }

    /**
     * run handler for use by a script. Supports
     * shorthand functions.
     */
    public interface IOnRunHandler {

        /**
         * Invoked when the goals {@code run} method is invoked.
         *
         * @param selector  The {@code INpcActionSelector}.
         */
        void run(INpcActionSelector selector);
    }
}
