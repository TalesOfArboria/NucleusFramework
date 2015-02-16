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

/**
 * An object used to add {@link FutureSubscriber}'s to the
 * futures agent in order to receive updates about the result
 * of an operation.
 */
public interface IFuture<R> {

    /**
     * Called when a result is available. Always called
     * along with {@link #onSuccess}, {@link #onCancel}, or
     * {@link #onError}.
     *
     * @param subscriber  The result update subscriber.
     */
    IFuture<R> onResult(FutureSubscriber<R> subscriber);

    /**
     * Adds an update subscriber to receive an update when and
     * if the result is successful.
     *
     * @param subscriber  The result update subscriber.
     */
    IFuture<R> onSuccess(FutureSubscriber<R> subscriber);

    /**
     * Adds an update subscriber to receive an update when and
     * if the result is cancelled.
     *
     * @param subscriber  The result update subscriber.
     */
    IFuture<R> onCancel(FutureSubscriber<R> subscriber);

    /**
     * Adds an update subscriber to receive an update when and
     * if the result fails.
     *
     * @param subscriber  The result update subscriber.
     */
    IFuture<R> onError(FutureSubscriber<R> subscriber);
}
