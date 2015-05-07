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

package com.jcwhatever.nucleus.providers.sql;

import com.jcwhatever.nucleus.providers.IProvider;
import com.jcwhatever.nucleus.utils.observer.future.IFuture;
import com.jcwhatever.nucleus.utils.observer.future.IFutureResult;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Interface for the global MySql data manager.
 */
public interface ISqlProvider extends IProvider, ISqlDataTypes {

    /**
     * Connect to a database.
     *
     * @param address       The address of the database.
     * @param databaseName  The name of the database.
     * @param userName      The user login name.
     * @param password      The user password.
     */
    IFutureResult<ISqlDatabase> connect(
            String address, String databaseName, String userName, String password);

    /**
     * Create a new statement transaction.
     */
    ISqlTransaction createTransaction(ISqlDatabase database);

    /**
     * Execute a query statement.
     *
     * <p>Provides access to the providers async statement execution.</p>
     *
     * @param statement  The query statement.
     *
     * @return  A future to return the {@link ResultSet} when it is ready.
     */
    IFutureResult<ISqlResult> executeQuery(PreparedStatement statement);

    /**
     * Execute an update/insert statement.
     *
     * <p>Provides access to the providers async statement execution.</p>
     *
     * @param statement  The statement.
     *
     * @return  A future to return the result.
     */
    IFuture execute(PreparedStatement statement);
}