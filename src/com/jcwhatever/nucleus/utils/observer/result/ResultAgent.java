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

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.ISubscriber;
import com.jcwhatever.nucleus.utils.observer.SubscriberAgent;

/**
 * A basic implementation of a {@link IResultAgent} used to allow a
 * result producer to send results to the agents subscribers.
 */
public class ResultAgent<R> extends SubscriberAgent implements IResultAgent<R> {

    @Override
    public void sendResult(Result<R> result) {
        PreCon.notNull(result);

        if (!hasSubscribers())
            return;

        if (result.isSuccess()) {
            sendSuccess(result);
        }
        else if (result.isComplete()) {

            if (result.isCancelled()) {
                sendCancel(result);
            }
            else {
                sendError(result);
            }
        }

        sendResultOnly(result);
    }

    protected void sendResultOnly(Result<R> result) {

        for (ISubscriber subscriber : subscribers()) {
            if (subscriber instanceof ResultSubscriber) {
                ((ResultSubscriber<R>) subscriber).onResult(result);
            }
        }
    }

    protected void sendSuccess(Result<R> result) {

        for (ISubscriber subscriber : subscribers()) {
            if (subscriber instanceof ResultSubscriber) {
                ((ResultSubscriber<R>) subscriber).onSuccess(result);
            }
        }
    }

    protected void sendError(Result<R> result) {

        for (ISubscriber subscriber : subscribers()) {
            if (subscriber instanceof ResultSubscriber) {
                ((ResultSubscriber<R>) subscriber).onError(result);
            }
        }
    }

    protected void sendCancel(Result<R> result) {

        for (ISubscriber subscriber : subscribers()) {
            if (subscriber instanceof ResultSubscriber) {
                ((ResultSubscriber<R>) subscriber).onCancel(result);
            }
        }
    }
}
