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
import com.jcwhatever.nucleus.providers.sql.datanode.ISqlDataNodeBuilder;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.observer.future.IFuture;
import com.jcwhatever.nucleus.utils.observer.future.IFutureResult;

import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;

/**
 * Interface for the global MySql data manager.
 */
public interface ISqlProvider extends IProvider, ISqlDataTypes {

    /**
     * MySql brand name.
     */
    String MYSQL = "MySQL";

    /**
     * Microsoft SQL brand name.
     */
    String MSSQL = "MSSQL";

    /**
     * Oracle SQL brand name.
     */
    String ORACLE = "Oracle";

    /**
     * PostgreSQL brand name.
     */
    String POSTGRE = "PostgreSQL";

    /**
     * Get the database brand.
     *
     * <p>Returns one of the four SQL brand constants or other brand name.</p>
     */
    String getDatabaseBrand();

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
     * Execute a query statement.
     *
     * <p>Provides access to the providers async statement execution.</p>
     *
     * @param statement  The query statement.
     *
     * @return  A future to return the {@link ISqlQueryResult} when it is ready.
     */
    IFutureResult<ISqlQueryResult> executeQuery(PreparedStatement statement);

    /**
     * Execute an update/insert statement.
     *
     * <p>Provides access to the providers async statement execution.</p>
     *
     * @param statement  The statement.
     *
     * @return  A future to to indicate success or failure.
     */
    IFuture execute(PreparedStatement statement);

    /**
     * Create a new SQL based {@link IDataNode} using a new builder.
     *
     * @param plugin  The data nodes owning plugin.
     *
     * @return  A new {@link ISqlDataNodeBuilder}.
     */
    ISqlDataNodeBuilder createDataNodeBuilder(Plugin plugin);
}