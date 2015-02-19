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

package com.jcwhatever.nucleus.providers.npc.ai.actions;

import com.jcwhatever.nucleus.providers.npc.ai.INpcState;
import com.jcwhatever.nucleus.providers.npc.ai.NpcScriptBehaviour;
import com.jcwhatever.nucleus.utils.PreCon;

/**
 * An {@link INpcAction} implementation that allows scripts to attach implementation
 * handlers via shorthand functions.
 */
public class NpcScriptAction extends NpcScriptBehaviour implements INpcAction {

    private IOnRunHandler _onRunHandler;
    private IOnFirstRun _firstRunHandler;

    /**
     * Constructor.
     *
     * @param actionName The name of the action.
     */
    public NpcScriptAction(String actionName) {
        super(actionName);
    }

    /**
     * Returns the result of the handler added via the {@link #onCanRun} method.
     *
     * <p>If a handler was not provided, returns true so long as a run handler
     * was added via the {@link #run} method.</p>
     *
     * <p>{@inheritDoc}</p>
     */
    @Override
    public boolean canRun(INpcState state) {
        return _onRunHandler != null && super.canRun(state);
    }

    @Override
    public void firstRun(INpcActionAgent agent) {
        if (_firstRunHandler == null)
            return;

        _firstRunHandler.onFirstRun(agent);
    }

    @Override
    public void run(INpcActionAgent agent) {
        if (_onRunHandler != null)
            _onRunHandler.run(agent);
        else
            agent.finish();
    }

    /**
     * Attach the onFirstRun handler. Optional.
     *
     * <p>Invoked just before the first time the action is run.</p>
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
    public NpcScriptAction onRun(IOnRunHandler handler) {
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
         * Invoked just before the action is run for the first time.
         *
         * @param agent  The actions agent.
         */
        void onFirstRun(INpcActionAgent agent);
    }

    /**
     * Run handler for use by a script. Supports
     * shorthand functions.
     */
    public interface IOnRunHandler {

        /**
         * Invoked when the actions {@link INpcAction#run}
         * method is invoked.
         *
         * @param agent  The actions agent.
         */
        void run(INpcActionAgent agent);
    }
}
