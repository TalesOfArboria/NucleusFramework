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

import javax.annotation.Nullable;

/**
 * Stores info about a result that is used with {@link FutureResultAgent}
 * and subscribers.
 *
 * @see FutureResultAgent
 * @see FutureResultSubscriber
 * @see IFutureResult
 * @see ResultBuilder
 */
public class Result<R> {

    private final double _totalCompletion;
    private final double _resultCompletion;
    private final R _result;
    private final String _message;
    private final Exception _exception;

    /**
     * Constructor.
     *
     * @param result   A pre-existing result object to copy values from.
     * @param message  A result message.
     */
    public Result(Result<R> result, @Nullable String message) {
        this(result.getTotalCompletion(), result.getResultCompletion(),
                result.getResult(), message, result.getException());
    }

    /**
     * Constructor.
     *
     * @param result       A pre-existing result to copy values from.
     * @param resultObject Optional result object.
     * @param message      A result message.
     */
    public Result(Result<R> result, @Nullable R resultObject, @Nullable String message) {
        this(result.getTotalCompletion(), result.getResultCompletion(),
                resultObject, message, result.getException());
    }

    /**
     * Constructor.
     *
     * @param totalCompletion   The completion percent where 1.0D is
     *                          completed and 0.0D is not completed at all. A value of
     *                          1.0D indicates total completion and the
     *                          resultCompletion value is final.
     * @param resultCompletion  Indicates the completion percent of the result
     *                          where 1.0D is successfully completed and 0.0D
     *                          is complete failure when totalCompletion
     *                          is at 1.0D. A value of less than 0.0D indicates the
     *                          the task to perform the result was cancelled.
     * @param message     A result message.
     */
    public Result(double totalCompletion, double resultCompletion, String message) {
        this(totalCompletion, resultCompletion, null, message);
    }

    /**
     * Constructor.
     *
     * @param totalCompletion   The completion percent where 1.0D is
     *                          completed and 0.0D is not completed at all. A value of
     *                          1.0D indicates total completion and the
     *                          resultCompletion value is final.
     * @param resultCompletion  Indicates the completion percent of the result
     *                          where 1.0D is successfully completed and 0.0D
     *                          is complete failure when totalCompletion
     *                          is at 1.0D.
     * @param result      Optional result object.
     * @param message     A result message.
     */
    public Result(double totalCompletion, double resultCompletion, @Nullable R result,
                  @Nullable String message) {
        this(totalCompletion, resultCompletion, result, message, null);
    }

    /**
     * Constructor.
     *
     * @param totalCompletion   The completion percent where 1.0D is
     *                          completed and 0.0D is not completed at all. A value of
     *                          1.0D indicates total completion and the
     *                          resultCompletion value is final.
     * @param resultCompletion  Indicates the completion percent of the result
     *                          where 1.0D is successfully completed and 0.0D
     *                          is complete failure when totalCompletion
     *                          is at 1.0D.
     * @param exception   The exception that was thrown.
     */
    public Result(double totalCompletion, double resultCompletion, Exception exception) {
        this(totalCompletion, resultCompletion, null, null, exception);
    }

    /**
     * Constructor.
     *
     * @param totalCompletion   The completion percent where 1.0D is
     *                          completed and 0.0D is not completed at all. A value of
     *                          1.0D indicates total completion and the
     *                          resultCompletion value is final.
     * @param resultCompletion  Indicates the completion percent of the result
     *                          where 1.0D is successfully completed and 0.0D
     *                          is complete failure when totalCompletion
     *                          is at 1.0D.
     * @param result      Optional result object.
     * @param message     A result message.
     * @param exception   The exception that was thrown.
     */
    public Result(double totalCompletion, double resultCompletion,
                  @Nullable R result, @Nullable String message, @Nullable Exception exception) {

        _totalCompletion = totalCompletion;
        _resultCompletion = resultCompletion;
        _result = result;
        _message = message;
        _exception = exception;
    }

    /**
     * Determine if the result is complete.
     */
    public boolean isComplete() {
        return _totalCompletion >= 1.0D;
    }

    /**
     * Determine if the result is successful.
     *
     * @return True if successful, false if failed or not yet complete.
     */
    public boolean isSuccess() {
        return _exception == null && isComplete() && _resultCompletion >= 1.0D;
    }

    /**
     * Determine if the result is cancelled.
     */
    public boolean isCancelled() {
        return isComplete() && Double.compare(_resultCompletion, 0.0D) == 0;
    }

    /**
     * Get the overall completion percent.
     */
    public double getTotalCompletion() {
        return _totalCompletion;
    }

    /**
     * Get the result completion percent.
     */
    public double getResultCompletion() {
        return _resultCompletion;
    }

    /**
     * Determine if there is a result object.
     */
    public boolean hasResult() {
        return _result != null;
    }

    /**
     * Get the result.
     *
     * @return  The result or null.
     */
    @Nullable
    public R getResult() {
        return _result;
    }

    /**
     * Determine if there is a message.
     */
    public boolean hasMessage() {
        return _message != null;
    }

    /**
     * Get the result message.
     */
    @Nullable
    public String getMessage() {
        return _message;
    }

    /**
     * Determine if there is an exception.
     */
    public boolean hasException() {
        return _exception != null;
    }

    /**
     * Get the exception.
     */
    @Nullable
    public Exception getException() {
        return _exception;
    }
}
