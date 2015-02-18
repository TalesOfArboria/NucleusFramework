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

import com.jcwhatever.nucleus.utils.PreCon;

/**
 * An abstract {@link INpcBehaviour} implementation that allows scripts to attach implementation
 * handlers via shorthand functions.
 */
public abstract class NpcScriptBehaviour implements INpcBehaviour {

    private IResetHandler _onReset;
    private ICanRunHandler _canRunHandler;
    private ICostHandler _costHandler;
    private IOnPauseHandler _pauseHandler;

    @Override
    public void reset(INpcState state) {
        if (_onReset != null)
            _onReset.reset(state);
    }

    @Override
    public boolean canRun(INpcState state) {
        return _canRunHandler == null || _canRunHandler.canRun(state);
    }

    @Override
    public float getCost(INpcState state) {
        if (_costHandler == null)
            return 1.0f;

        return _costHandler.getCost(state);
    }

    @Override
    public void pause(INpcState state) {
        if (_pauseHandler == null)
            return;

        _pauseHandler.onPause(state);
    }

    /**
     * Attach the reset handler. Optional.
     *
     * @param handler  The reset handler.
     *
     * @return  Self for chaining.
     */
    public NpcScriptBehaviour onReset(IResetHandler handler) {
        PreCon.notNull(handler, "handler");
        _onReset = handler;

        return this;
    }

    /**
     * Attach the canRun handler. Optional.
     *
     * <p>{@link #canRun} returns true if not provided.</p>
     *
     * @param handler  The canRun handler.
     *
     * @return  Self for chaining.
     */
    public NpcScriptBehaviour onCanRun(ICanRunHandler handler) {
        PreCon.notNull(handler, "handler");

        _canRunHandler = handler;

        return this;
    }

    /**
     * Attach the onGetCost handler. Optional.
     *
     * <p>{@link #getCost} returns a value of 1.0f if not provided.</p>
     *
     * @param handler  The cost handler.
     *
     * @return  Self for chaining.
     */
    public NpcScriptBehaviour onGetCost(ICostHandler handler) {
        PreCon.notNull(handler, "handler");

        _costHandler = handler;

        return this;
    }

    /**
     * Attach the onPause handler. Optional.
     *
     * @param handler  The pause handler.
     *
     * @return  Self for chaining.
     */
    public NpcScriptBehaviour onPause(IOnPauseHandler handler) {
        PreCon.notNull(handler, "handler");

        _pauseHandler = handler;

        return this;
    }

    /**
     * Reset handler for use by a script. Supports
     * shorthand functions.
     */
    public interface IResetHandler {
        void reset(INpcState state);
    }

    /**
     * canRun handler for use by a script. Supports
     * shorthand functions.
     */
    public interface ICanRunHandler {
        boolean canRun(INpcState state);
    }

    /**
     * canRun handler for use by a script. Supports
     * shorthand functions.
     */
    public interface ICostHandler {
        float getCost(INpcState state);
    }

    /**
     * onPause handler for use by a script. Supports
     * shorthand functions.
     */
    public interface IOnPauseHandler {
        void onPause(INpcState state);
    }
}
