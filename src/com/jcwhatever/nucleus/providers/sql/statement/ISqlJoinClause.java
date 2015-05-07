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

/**
 * Sql Join clause mixin.
 */
public interface ISqlJoinClause<T> {

    /**
     * "ON" clause of "JOIN" syntax.
     *
     * <p>Matches the specified column name to a column from the
     * primary table. Assumes that the column names are the same.</p>
     *
     * <p>The column name is extracted from the provided column name
     * (which should also contain the table name or alias) to get the
     * column name to match on the primary table.</p>
     *
     * @param column  The column from the joining table.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T on(String column);

    /**
     * "ON" clause of "JOIN" syntax.
     *
     * @param column       The column from the joining table.
     * @param otherColumn  The matching column from another joined table or
     *                     the primary table.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T on(String column, String otherColumn);
}
