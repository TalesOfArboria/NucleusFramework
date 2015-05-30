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

package com.jcwhatever.nucleus.providers.sql.statement;

import com.jcwhatever.nucleus.providers.sql.ISqlDatabase;
import com.jcwhatever.nucleus.providers.sql.ISqlResult;
import com.jcwhatever.nucleus.utils.observer.future.IFutureResult;

import java.util.Collection;

/**
 * Holds multiple SQL statements to be executed in order.
 */
public interface ISqlTransaction {

    /**
     * Get the transaction database.
     */
    ISqlDatabase getDatabase();

    /**
     * Append an Sql statement.
     *
     * @param statement  The statement to append.
     *
     * @return  A future result. The result only contains data created in the statement
     * added to the transaction. Closing any sql results will not effect other statements.
     *
     * @throws IllegalArgumentException if the statement is not for the same database
     * as the transaction or the {@link ISqlStatement} implementation is not supported.
     */
    IFutureResult<ISqlResult> append(ISqlStatement statement);

    /**
     * Append multiple Sql statements.
     *
     * @param statements  The statements to append.
     *
     * @return  A future result. The result only contains data created in the statement
     * added to the transaction. Closing any sql results will not effect other statements.
     *
     * @throws IllegalArgumentException if the statement is not for the same database
     * as the transaction or the {@link ISqlStatement} implementations are not supported.
     */
    IFutureResult<ISqlResult> append(Collection<? extends ISqlStatement> statements);

    /**
     * Execute the statements in the transaction.
     *
     * @return  A future result. The result data is for all data in the transaction.
     */
    IFutureResult<ISqlResult> execute();

    /**
     * Get the transactions future result.
     *
     * @return  A future result. The result data is for all data in the transaction.
     */
    IFutureResult<ISqlResult> future();
}
