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
public abstract class NpcScriptBehaviour<A extends INpcBehaviourAgent> implements INpcBehaviour<A> {

    private final String _name;
    private IResetHandler _resetHandler;
    private ICanRunHandler _canRunHandler;
    private ICostHandler _costHandler;
    private IOnRunHandler _runHandler;
    private IOnFirstRun _firstRunHandler;
    private IOnPauseHandler _pauseHandler;

    /**
     * Constructor.
     *
     * @param behaviourName  The name of the behaviour.
     */
    public NpcScriptBehaviour (String behaviourName) {
        _name = behaviourName;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public void reset(INpcState state) {
        if (_resetHandler != null)
            _resetHandler.reset(state);
    }

    @Override
    public boolean canRun(INpcState state) {
        return hasRunHandler() && (_canRunHandler == null || _canRunHandler.canRun(state));
    }

    @Override
    public float getCost(INpcState state) {
        if (_costHandler == null)
            return 1.0f;

        return _costHandler.getCost(state);
    }

    @Override
    public void firstRun(A agent) {
        if (_firstRunHandler == null)
            return;

        _firstRunHandler.onFirstRun(agent);
    }

    @Override
    public void run(A agent) {
        if (_runHandler != null)
            _runHandler.run(agent);
        else
            agent.finish();
    }

    @Override
    public void pause(INpcState state) {
        if (_pauseHandler == null)
            return;

        _pauseHandler.onPause(state);
    }

    /**
     * Determine if the reset handler is set.
     */
    public boolean hasResetHandler() {
        return _resetHandler != null;
    }

    /**
     * Determine if canRun handler is set.
     */
    public boolean hasCanRunHandler() {
        return _canRunHandler != null;
    }

    /**
     * Determine if getCost handler is set.
     */
    public boolean hasCostHandler() {
        return _costHandler != null;
    }

    /**
     * Determine if firstRun handler is set.
     */
    public boolean hasFirstRunHandler() {
        return _firstRunHandler != null;
    }

    /**
     * Determine if run handler is set.
     */
    public boolean hasRunHandler() {
        return _runHandler != null;
    }

    /**
     * Determine if pause handler is set.
     */
    public boolean hasPauseHandler() {
        return _pauseHandler != null;
    }

    /**
     * Attach the reset handler. Optional.
     *
     * @param handler  The reset handler.
     *
     * @return  Self for chaining.
     */
    public NpcScriptBehaviour<A> onReset(IResetHandler handler) {
        PreCon.notNull(handler, "handler");
        _resetHandler = handler;

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
    public NpcScriptBehaviour<A> onCanRun(ICanRunHandler handler) {
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
    public NpcScriptBehaviour<A> onGetCost(ICostHandler handler) {
        PreCon.notNull(handler, "handler");

        _costHandler = handler;

        return this;
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
    public NpcScriptBehaviour<A> onFirstRun(IOnFirstRun handler) {
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
    public NpcScriptBehaviour<A> onRun(IOnRunHandler handler) {
        PreCon.notNull(handler, "handler");

        _runHandler = handler;

        return this;
    }

    /**
     * Attach the onPause handler. Optional.
     *
     * @param handler  The pause handler.
     *
     * @return  Self for chaining.
     */
    public NpcScriptBehaviour<A> onPause(IOnPauseHandler handler) {
        PreCon.notNull(handler, "handler");

        _pauseHandler = handler;

        return this;
    }

    /**
     * Reset handler for use by a script.
     *
     * <p>Supports shorthand functions.</p>
     */
    public interface IResetHandler {

        /**
         * Invoked when the behaviour is reset.
         *
         * @param state  The NPC state.
         */
        void reset(INpcState state);
    }

    /**
     * canRun handler for use by a script.
     *
     * <p>Supports shorthand functions.</p>
     */
    public interface ICanRunHandler {

        /**
         * Invoked when the behaviour is paused.
         *
         * @param state  The NPC state.
         */
        boolean canRun(INpcState state);
    }

    /**
     * canRun handler for use by a script.
     *
     * <p>Supports shorthand functions.</p>
     */
    public interface ICostHandler {

        /**
         * Invoked to get the cost of the behaviour.
         *
         * @param state  The NPC state.
         */
        float getCost(INpcState state);
    }

    /**
     * onFirstRun handler for use by a script.
     *
     * <p>Supports shorthand functions.</p>
     */
    public interface IOnFirstRun {

        /**
         * Invoked just before the goal is run for the first time.
         *
         * @param agent  The goals agent.
         */
        <A extends INpcBehaviourAgent> void onFirstRun(A agent);
    }

    /**
     * run handler for use by a script.
     *
     * <p>Supports shorthand functions.</p>
     */
    public interface IOnRunHandler {

        /**
         * Invoked when the behaviours {@link#run} method is invoked.
         *
         * @param agent  The behaviour agent.
         */
        <A extends INpcBehaviourAgent> void run(A agent);
    }

    /**
     * onPause handler for use by a script.
     *
     * <p>Supports shorthand functions.</p>
     */
    public interface IOnPauseHandler {

        /**
         * Invoked when the behaviour is paused.
         *
         * @param state  The NPC state.
         */
        void onPause(INpcState state);
    }
}
