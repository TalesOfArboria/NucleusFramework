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

/**
 * An object used to add {@link FutureSubscriber}'s to the futures agent in
 * order to receive an update about the outcome of an operation.
 *
 * @see FutureAgent
 * @see FutureSubscriber
 */
public interface IFuture {

    /**
     * Specifies the status of an operation.
     */
    enum FutureStatus {
        CANCEL,
        ERROR,
        SUCCESS
    }

    /**
     * Add a {@link FutureSubscriber} to be invoked when the related operation
     * is completed.
     *
     * <p>Always invoked along with {@link #onSuccess}, {@link #onCancel}, or
     * {@link #onError}.</p>
     *
     * @param subscriber  The subscriber.
     */
    IFuture onStatus(FutureSubscriber subscriber);

    /**
     * Add a {@link FutureSubscriber} to be invoked if the related operation
     * is completed successfully.
     *
     * @param subscriber  The subscriber.
     */
    IFuture onSuccess(FutureSubscriber subscriber);

    /**
     * Add a {@link FutureSubscriber} to be invoked if the related operation
     * is cancelled.
     *
     * @param subscriber  The subscriber.
     */
    IFuture onCancel(FutureSubscriber subscriber);

    /**
     * Add a {@link FutureSubscriber} to be invoked if the related operation
     * ends due to an error.
     *
     * @param subscriber  The subscriber.
     */
    IFuture onError(FutureSubscriber subscriber);
}
