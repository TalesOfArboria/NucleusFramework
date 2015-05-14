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

package com.jcwhatever.nucleus.utils.observer.future;

import com.jcwhatever.nucleus.collections.observer.subscriber.SubscriberSetMultimap;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.ISubscriber;
import com.jcwhatever.nucleus.utils.observer.SubscriberAgent;
import com.jcwhatever.nucleus.utils.observer.future.IFuture.FutureStatus;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Used for returning asynchronous operation result status from a method.
 *
 * @see FutureSubscriber
 * @see IFuture
 */
public class FutureAgent extends SubscriberAgent {

    private final IFuture _future;
    private final SubscriberSetMultimap<String, FutureSubscriber> _subscribers =
            new SubscriberSetMultimap<>();

    private FutureStatus _finalStatus;
    private String _finalMessage;
    private boolean _hasFutureSubscribers;

    /**
     * Constructor.
     */
    public FutureAgent() {
        _future = new Future(this);
    }

    @Override
    public boolean hasSubscribers() {
        return super.hasSubscribers() || _hasFutureSubscribers;
    }

    /**
     * Send operation status to subscribers.
     *
     * @param status   The status.
     * @param message  Optional status message.
     * @param args     Optional message format arguments.
     */
    public void sendStatus(FutureStatus status, @Nullable String message, Object... args) {
        PreCon.notNull(status);

        _finalStatus = status;
        _finalMessage = message;

        if (!hasSubscribers())
            return;

        switch (status) {
            case CANCEL:
                sendToSubscribers("onCancel", status, message, args);
                break;
            case ERROR:
                sendToSubscribers("onError", status, message, args);
                break;
            case SUCCESS:
                sendToSubscribers("onSuccess", status, message, args);
                break;
        }

        sendToSubscribers("onStatus", status, message, args);
    }

    /**
     * Declare the futures operation cancelled.
     *
     * @return The agents future.
     */
    public IFuture cancel() {
        return cancel(null);
    }

    /**
     * Declare the futures operation cancelled.
     *
     * @param message  Optional cancel message.
     * @param args     Optional message format arguments.
     *
     * @return The agents future.
     */
    public IFuture cancel(@Nullable String message, Object... args) {
        sendStatus(FutureStatus.CANCEL, message, args);
        return getFuture();
    }


    /**
     * Declare the futures operation status as error.
     *
     * @return The agents future.
     */
    public IFuture error() {
        return error(null);
    }

    /**
     * Declare the futures operation status as error.
     *
     * @param message  Optional error message.
     * @param args     Optional message format arguments.
     *
     * @return The agents future.
     */
    public IFuture error(@Nullable String message, Object... args) {
        sendStatus(FutureStatus.ERROR, message, args);
        return getFuture();
    }

    /**
     * Declare the futures operation status as success.
     *
     * @return The agents future.
     */
    public IFuture success() {
        return success(null);
    }

    /**
     * Declare the futures operation status as success.
     *
     * @param message  Optional success message.
     * @param args     Optional message format arguments.
     *
     * @return The agents future.
     */
    public IFuture success(@Nullable String message, Object... args) {
        sendStatus(FutureStatus.SUCCESS, message, args);
        return getFuture();
    }

    /**
     * Get a future that can be returned so that a method caller can
     * attach {@link com.jcwhatever.nucleus.utils.observer.update.IUpdateSubscriber}'s.
     */
    public IFuture getFuture() {
        return _future;
    }

    @Override
    public void dispose() {
        super.dispose();

        _subscribers.dispose();
    }

    private void sendToSubscribers(
            String agentName, FutureStatus status, @Nullable String message, Object[] args) {

        if (message != null)
            message = TextUtils.format(message, args);

        List<ISubscriber> list = new ArrayList<>(subscribers());

        for (ISubscriber subscriber : list) {
            if (subscriber instanceof FutureSubscriber) {
                //noinspection unchecked
                ((FutureSubscriber) subscriber).on(status, message);
            }
        }

        synchronized (_subscribers) {
            Collection<FutureSubscriber> subscribers = _subscribers.get(agentName);
            for (FutureSubscriber subscriber : subscribers) {
                subscriber.on(status, message);
            }
        }
    }

    private static class Future implements IFuture {

        FutureAgent parent;

        private Future(FutureAgent parent) {
            this.parent = parent;
        }

        @Override
        public Future onStatus(FutureSubscriber subscriber) {
            PreCon.notNull(subscriber);

            addSubscriber("onStatus", subscriber);

            return this;
        }

        @Override
        public Future onSuccess(FutureSubscriber subscriber) {
            PreCon.notNull(subscriber);

            addSubscriber("onSuccess", subscriber);

            return this;
        }

        @Override
        public Future onCancel(FutureSubscriber subscriber) {
            PreCon.notNull(subscriber);

            addSubscriber("onCancel", subscriber);

            return this;
        }

        @Override
        public Future onError(FutureSubscriber subscriber) {
            PreCon.notNull(subscriber);

            addSubscriber("onError", subscriber);

            return this;
        }

        private void addSubscriber(String agentName, FutureSubscriber subscriber) {

            if (parent._finalStatus != null) {

                FutureStatus status = parent._finalStatus;

                if (agentName.equals("onStatus") ||
                        (status == FutureStatus.SUCCESS && agentName.equals("onSuccess")) ||
                        (status == FutureStatus.CANCEL && agentName.equals("onCancel")) ||
                        (status == FutureStatus.ERROR && agentName.equals("onError"))) {
                    subscriber.on(parent._finalStatus, parent._finalMessage);
                }
            }

            if (!parent.isDisposed()) {
                parent._subscribers.put(agentName, subscriber);
                parent._hasFutureSubscribers = true;
            }
        }
    }
}


