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

import com.jcwhatever.nucleus.mixins.INamed;
import com.jcwhatever.nucleus.utils.observer.future.IFutureResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.annotation.Nullable;

/**
 * Sql database.
 */
public interface ISqlDatabase extends INamed {

    /**
     * Get the database connection.
     */
    Connection getConnection();

    /**
     * Create a new table builder used to define and/or build
     * a table in the database.
     *
     * @return  The table builder.
     */
    ISqlTableBuilder createTableBuilder();

    /**
     * Create or get a table with the specified table definition.
     *
     * <p>If the table already exists, the table is returned an no action
     * is taken.</p>
     *
     * @param name        The name of the table.
     * @param definition  The table definition.
     */
    IFutureResult<ISqlTable> createTable(String name, ISqlTableDefinition definition);

    /**
     * Get a table from the database.
     *
     * <p>The table must be created and/or defined via {@link #createTable} before it
     * cab be returned from this method.</p>
     *
     * @param name  The name of the table.
     *
     * @return  The table or null if not found or loaded yet.
     */
    @Nullable
    ISqlTable getTable(String name);

    /**
     * Create a prepared sql statement using the database connection.
     *
     * @param sql  The sql statement.
     */
    PreparedStatement prepareStatement(String sql) throws SQLException;
}
