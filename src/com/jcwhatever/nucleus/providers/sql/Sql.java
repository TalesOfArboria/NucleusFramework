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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.providers.sql.datanode.ISqlDataNodeBuilder;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.observer.future.IFuture;
import com.jcwhatever.nucleus.utils.observer.future.IFutureResult;

import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Sql provider convenience methods.
 */
public class Sql {

    /**
     * Determine if an Sql provider is installed.
     */
    public static boolean hasProvider() {
        return Nucleus.getProviders().getSql() != null;
    }

    /**
     * Connect to a database.
     *
     * @param address       The address of the database.
     * @param databaseName  The name of the database.
     * @param userName      The user login name.
     * @param password      The user password.
     *
     * @throws UnsupportedOperationException if an Sql provider is not installed.
     */
    public static IFutureResult<ISqlDatabase> connect(
            String address, String databaseName, String userName, String password) {
        return provider().connect(address, databaseName, userName, password);
    }

    /**
     * Execute a query statement.
     *
     * <p>Provides access to the providers async statement execution.</p>
     *
     * @param statement  The query statement.
     *
     * @return  A future to return the {@link ResultSet} when it is ready.
     *
     * @throws UnsupportedOperationException if an Sql provider is not installed.
     */
    public static IFutureResult<ISqlQueryResult> executeQuery(PreparedStatement statement) {
        return provider().executeQuery(statement);
    }

    /**
     * Execute an update/insert statement.
     *
     * <p>Provides access to the providers async statement execution.</p>
     *
     * @param statement  The statement.
     *
     * @return  A future to return the result.
     *
     * @throws UnsupportedOperationException if an Sql provider is not installed.
     */
    public static IFuture execute(PreparedStatement statement) {
        return provider().execute(statement);
    }

    /**
     * Create a new SQL based {@link IDataNode} using a new builder.
     *
     * @param plugin  The data nodes owning plugin.
     *
     * @return  A new {@link ISqlDataNodeBuilder}.
     */
    public static ISqlDataNodeBuilder createDataNodeBuilder(Plugin plugin) {
        return provider().createDataNodeBuilder(plugin);
    }

    /**
     * Get the sql provider.
     *
     * @throws UnsupportedOperationException if an Sql provider is not installed.
     */
    public static ISqlProvider provider() {
        ISqlProvider provider = Nucleus.getProviders().getSql();
        if (provider == null)
            throw new UnsupportedOperationException("Sql provider not installed.");

        return provider;
    }
}
