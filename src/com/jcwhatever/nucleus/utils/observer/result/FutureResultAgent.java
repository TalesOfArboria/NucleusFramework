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

package com.jcwhatever.nucleus.utils.observer.result;

import com.jcwhatever.nucleus.collections.observer.agent.AgentHashMap;
import com.jcwhatever.nucleus.collections.observer.agent.AgentMap;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.update.IUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.update.UpdateAgent;

/**
 * Used for returning asynchronous results from a method. Intended
 * for transient use. Once the final result is achieved the agent should be
 * discarded/disposed.
 */
public class FutureResultAgent<R> extends ResultAgent<R> implements IResultAgent<R> {

    private Result<R> _finalResult;
    private final Future<R> _future;
    private AgentMap<String, UpdateAgent<Result<R>>> _updateAgents =
            new AgentHashMap<String, UpdateAgent<Result<R>>>()
                    .set("onResult", new UpdateAgent<Result<R>>())
                    .set("onSuccess", new UpdateAgent<Result<R>>())
                    .set("onCancel", new UpdateAgent<Result<R>>())
                    .set("onError", new UpdateAgent<Result<R>>());

    boolean _hasFutureSubscribers;

    public FutureResultAgent() {
        _future = new Future<>(this);
    }

    @Override
    public void sendResult(Result<R> result) {
        PreCon.notNull(result);

        if (result.isComplete()) {
            _finalResult = result;
        }

        super.sendResult(result);
    }

    /**
     * Get a future that can be returned so that a method caller can
     * attach {@code IUpdateSubscriber}'s.
     */
    public Future<R> getFuture() {
        return _future;
    }

    // send the result update
    @Override
    protected void sendResultOnly(Result<R> result) {

        super.sendResultOnly(result);

        UpdateAgent<Result<R>> agent = _updateAgents.get("onResult");
        agent.update(result);
    }

    // send the result update for success
    @Override
    protected void sendSuccess(Result<R> result) {

        super.sendSuccess(result);

        UpdateAgent<Result<R>> agent = _updateAgents.get("onSuccess");
        agent.update(result);
    }

    // send the result update for an error
    @Override
    protected void sendError(Result<R> result) {

        super.sendError(result);

        UpdateAgent<Result<R>> agent = _updateAgents.get("onError");
        agent.update(result);
    }

    // send the result update for cancelled
    @Override
    protected void sendCancel(Result<R> result) {

        super.sendCancel(result);

        UpdateAgent<Result<R>> agent = _updateAgents.get("onCancel");
        agent.update(result);
    }

    @Override
    protected boolean hasSubscribers() {
        return super.hasSubscribers() || _hasFutureSubscribers;
    }

    public static class Future<R> {

        FutureResultAgent<R> parent;

        private Future(FutureResultAgent<R> parent) {
            this.parent = parent;
        }

        /**
         * Called when a result is available. Always called
         * along with {@code onSuccess}, {@code onCancel}, or
         * {@code onFail}.
         *
         * @param subscriber  The result update subscriber.
         */
        public Future<R> onResult(IUpdateSubscriber<Result<R>> subscriber) {
            PreCon.notNull(subscriber);

            addSubscriber("onResult", subscriber, parent._finalResult != null);

            return this;
        }

        /**
         * Adds an update subscriber to receive an update when and
         * if the result is successful.
         *
         * @param subscriber  The result update subscriber.
         */
        public Future<R> onSuccess(IUpdateSubscriber<Result<R>> subscriber) {
            PreCon.notNull(subscriber);

            addSubscriber("onSuccess", subscriber, parent._finalResult != null &&
                    parent._finalResult.isSuccess());

            return this;
        }

        /**
         * Adds an update subscriber to receive an update when and
         * if the result is cancelled.
         *
         * @param subscriber  The result update subscriber.
         */
        public Future<R> onCancel(IUpdateSubscriber<Result<R>> subscriber) {
            PreCon.notNull(subscriber);

            addSubscriber("onCancel", subscriber, parent._finalResult != null &&
                    parent._finalResult.isCancelled());

            return this;
        }

        /**
         * Adds an update subscriber to receive an update when and
         * if the result fails.
         *
         * @param subscriber  The result update subscriber.
         */
        public Future<R> onError(IUpdateSubscriber<Result<R>> subscriber) {
            PreCon.notNull(subscriber);

            addSubscriber("onError", subscriber, parent._finalResult != null &&
                    !parent._finalResult.isSuccess() && !parent._finalResult.isCancelled());

            return this;
        }

        private void addSubscriber(String agentName,
                                   IUpdateSubscriber<Result<R>> subscriber, boolean updateNow) {
            UpdateAgent<Result<R>> agent = parent._updateAgents.get(agentName);

            if (updateNow) {
                subscriber.onUpdate(agent, parent._finalResult);
            }

            agent.register(subscriber);

            parent._hasFutureSubscribers = true;
        }
    }

}
