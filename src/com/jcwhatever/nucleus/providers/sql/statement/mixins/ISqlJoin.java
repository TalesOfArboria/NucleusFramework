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

/**
 * Sql Join mixin.
 */
public interface ISqlJoin<T> {

    /**
     * Inner join a table.
     *
     * @param table  The table to join.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T innerJoin(ISqlTable table);

    /**
     * Inner join a table.
     *
     * @param table  The table to join.
     * @param alias  The alias to assign to the table.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T innerJoin(ISqlTable table, String alias);

    /**
     * Left join a table.
     *
     * @param table  The table to join.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T leftJoin(ISqlTable table);

    /**
     * Left join a table.
     *
     * @param table  The table to join.
     * @param alias  The alias to assign to the table.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T leftJoin(ISqlTable table, String alias);

    /**
     * Right join a table.
     *
     * @param table  The table to join.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T rightJoin(ISqlTable table);

    /**
     * Right join a table.
     *
     * @param table  The table to join.
     * @param alias  The alias to assign to the table.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T rightJoin(ISqlTable table, String alias);
}
