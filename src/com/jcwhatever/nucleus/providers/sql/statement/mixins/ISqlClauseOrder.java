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

package com.jcwhatever.nucleus.providers.sql.statement.mixins;

import com.jcwhatever.nucleus.providers.sql.ISqlTable;
import com.jcwhatever.nucleus.providers.sql.SqlOrder;
import com.jcwhatever.nucleus.providers.sql.statement.generators.IOrderGenerator;

/**
 * Sql statement sort order clause mixin.
 */
public interface ISqlClauseOrder<T> {

    /**
     * Order rows in ascending order using the specified column.
     *
     * @param columnName  The name of the column to order by.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T orderByAscend(String columnName);

    /**
     * Order rows in ascending order using the specified column.
     *
     * @param table       The table the specified column is from.
     * @param columnName  The name of the column to order by.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T orderByAscend(ISqlTable table, String columnName);

    /**
     * Order rows in descending order using the specified column.
     *
     * @param columnName  The name of the column to order by.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T orderByDescend(String columnName);

    /**
     * Order rows in descending order using the specified column.
     *
     * @param table       The table the specified column is from.
     * @param columnName  The name of the column to order by.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T orderByDescend(ISqlTable table, String columnName);

    /**
     * Order rows in the specified order using the specified column.
     *
     * @param columnName  The name of the column to order by.
     * @param order       The order to use.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T orderBy(String columnName, SqlOrder order);

    /**
     * Order rows in the specified order using the specified column.
     *
     * @param table       The table the specified column is from.
     * @param columnName  The name of the column to order by.
     * @param order       The order to use.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T orderBy(ISqlTable table, String columnName, SqlOrder order);

    /**
     * Order rows in the specified order using the specified columns.
     *
     * @param orderGenerator  The generator used to create column orders.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T orderBy(IOrderGenerator orderGenerator);
}
