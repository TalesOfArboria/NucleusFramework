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
 * A utility to help with building a {@link Result}.
 *
 * @see Result
 */
public class ResultBuilder<R> {

    private double _totalCompletion;
    private double _resultCompletion;
    private String _message;
    private R _result;

    /**
     * Set the total completion percent. The total completion is
     * the percentage of the overall task that is done. When it reaches
     * 1.0D, the task is considered complete.
     *
     * @param completion  The completion percent.
     *
     * @return  Self for chaining.
     */
    public ResultBuilder<R> totalCompletion(double completion) {
        _totalCompletion = completion;
        return this;
    }

    /**
     * Set the result completion percent. The result completion is
     * the percentage of the desired work to be done that is completed. When
     * it reaches 1.0D, the task is considered a success.
     *
     * @param completion  The result completion percent.
     *
     * @return  Self for chaining.
     */
    public ResultBuilder<R> resultCompletion(double completion) {
        _resultCompletion = completion;
        return this;
    }

    /**
     * Sets the total completion and result completion values to generic
     * values which indicate an error.
     *
     * <p>The total completion is set to 1.0D to indicate the task is completed,
     * the result completion is set to -1.0D to indicate the error.</p>
     *
     * @return  Self for chaining.
     */
    public ResultBuilder<R> error() {
        _totalCompletion = 1.0D;
        _resultCompletion = -1.0D;
        return this;
    }

    /**
     * Sets the total completion and result completion values to generic
     * values which indicate the task was cancelled.
     *
     * <p>The total completion is set to 1.0D to indicate the task is completed,
     * the result completion is set to 0.0D to indicate the result is not complete.</p>
     *
     * @return  Self for chaining.
     */
    public ResultBuilder<R> cancel() {
        _totalCompletion = 1.0D;
        _resultCompletion = 0.0D;
        return this;
    }

    /**
     * Sets the total completion and result completion values to generic
     * value which indicate the task is a success.
     *
     * <p>The total completion is set to 1.0D to indicate the task is completed,
     * the result completion is set to 1.0D to indicate the result is complete.</p>
     *
     * @return  Self for chaining.
     */
    public ResultBuilder<R> success() {
        _totalCompletion = 1.0D;
        _resultCompletion = 1.0D;
        return this;
    }

    /**
     * Set the result message.
     *
     * @param message  The message.
     *
     * @return  Self for chaining.
     */
    public ResultBuilder<R> message(String message) {
        _message = message;
        return this;
    }

    /**
     * Set the result object.
     *
     * @param result  The result object.
     *
     * @return  Self for chaining.
     */
    public ResultBuilder<R> result(R result) {
        _result = result;
        return this;
    }

    /**
     * Generate a new {@link Result}.
     */
    public Result<R> build() {
        return new Result<R>(_totalCompletion, _resultCompletion, _result, _message);
    }
}
