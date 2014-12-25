/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

package com.jcwhatever.generic.utils;

import javax.annotation.Nullable;

/**
 * Used as a method return value to return a nullable result
 * along with a flag indicating if the operation to retrieve
 * the result is a success.
 *
 * <p>Useful when an operation can succeed but still return a null result.</p>
 *
 * @param <T>  The result type.
 */
public class Result<T> {

    private final boolean _hasResult;
    private final T _result;

    /**
     * Constructor.
     *
     * @param hasResult  True if the operation was successful.
     */
    public Result(boolean hasResult) {
        this(hasResult, null);
    }

    /**
     * Constructor.
     *
     * @param hasResult  True if the operation was successful.
     * @param result     The result of the operation.
     */
    public Result(boolean hasResult, @Nullable T result) {
        _hasResult = hasResult;
        _result = result;
    }

    /**
     * Determine if the result is from a successful operation.
     *
     * <p>A true return value does not indicate the result is not null</p>
     */
    public boolean hasResult() {
        return _hasResult;
    }

    /**
     * Determine if the result is from a successful operation.
     *
     * <p>Alternative syntax for {@code hasResult} method to aid in code clarity.</p>
     */
    public boolean isSuccess() {
        return _hasResult;
    }

    /**
     * Get the returned result.
     */
    @Nullable
    public T getResult() {
        return _result;
    }
}
