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

package com.jcwhatever.nucleus.providers.sql.observer;

import com.jcwhatever.nucleus.providers.sql.ISqlResult;
import com.jcwhatever.nucleus.utils.observer.future.FutureResultSubscriber;
import com.jcwhatever.nucleus.utils.observer.future.Result;

import javax.annotation.Nullable;

/**
 * Abstract delegate SQL implementation of a {@link FutureResultSubscriber}.
 *
 * <p>Automatically closes query results after {@link #onResult} is invoked.</p>
 */
public abstract class SqlAutoCloseSubscriber extends FutureResultSubscriber<ISqlResult> {

    @Override
    public final void on(Result<ISqlResult> result) {

        ISqlResult sqlResult = result.getResult();

        onResult(sqlResult, result.getMessage());

        if (sqlResult != null)
            sqlResult.closeQueryResults();
    }

    /**
     * Invoked when the observed SQL operation has a result.
     *
     * @param result   The result. If the result is not null, the results is
     *                 SQL query results are automatically closed after the
     *                 method is finished.
     * @param message  The result message.
     */
    public abstract void onResult(@Nullable ISqlResult result, @Nullable String message);
}
