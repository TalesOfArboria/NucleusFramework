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

import com.jcwhatever.nucleus.utils.observer.Subscriber;
import com.jcwhatever.nucleus.utils.observer.update.IUpdateSubscriber;

/**
 * A subscriber delegate for use with {@link FutureResultAgent}.
 *
 * @see FutureResultAgent
 * @see IFutureResult
 */
public abstract class FutureResultSubscriber<R> extends Subscriber
        implements IUpdateSubscriber<Result<R>> {

    /**
     * Invoked when the subscriber receives any result.
     *
     * @param result  The result
     */
    @Override
    public abstract void on(Result<R> result);

    /**
     * Invoked when the subscriber receives a success result.
     *
     * <p>Intended for optional override.</p>
     *
     * @param result  The result
     */
    public void onSuccess(Result<R> result) {}

    /**
     * Invoked when the subscriber receives a cancel result.
     *
     * <p>Intended for optional override.</p>
     *
     * @param result  The result.
     */
    public void onCancel(Result<R> result) {}

    /**
     * Invoked when the subscriber receives an error result.
     *
     * <p>Intended for optional override.</p>
     *
     * @param result  The result.
     */
    public void onError(Result<R> result) {}
}
