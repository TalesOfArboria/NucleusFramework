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

import javax.annotation.Nullable;

/**
 * Data setter operators.
 */
public interface ISqlDataSetterOperator<T> {

    /**
     * Set the value of a column.
     *
     * @param value  The value.
     */
    T value(@Nullable Object value);

    /**
     * Set a column to equal the value of the specified column.
     *
     * @param columnName  The name of the column.
     */
    T equalsColumn(String columnName);

    /**
     * Set a column to equal the value of the specified column.
     *
     * @param table       The table the specified column is from.
     * @param columnName  The name of the column.
     */
    T equalsColumn(ISqlTable table, String columnName);

    /**
     * Add the value to the column.
     *
     * @param amount  The value to increment.
     */
    T add(int amount);

    /**
     * Add the value to the column.
     *
     * @param amount  The value to increment.
     */
    T add(double amount);

    /**
     * Add the value of the specified column.
     *
     * @param columnName  The name of the column.
     */
    T addColumn(String columnName);

    /**
     * Add the value of the specified column.
     *
     * @param table       The table the specified column is from.
     * @param columnName  The name of the column.
     */
    T addColumn(ISqlTable table, String columnName);

    /**
     * Subtract the value from the column.
     *
     * @param amount  The value to increment.
     */
    T subtract(int amount);

    /**
     * Subtract the value from the column.
     *
     * @param amount  The value to increment.
     */
    T subtract(double amount);

    /**
     * Subtract the value of the specified column.
     *
     * @param columnName  The name of the column.
     */
    T subtractColumn(String columnName);

    /**
     * Subtract the value of the specified column.
     *
     * @param table       The table the specified column is from.
     * @param columnName  The name of the column.
     */
    T subtractColumn(ISqlTable table, String columnName);

    /**
     * Set the value of a column to the value of the larger of the
     * column or the specified column.
     *
     * @param table       Table of the specified column.
     * @param columnName  The name of the column.
     */
    T largerColumn(ISqlTable table, String columnName);

    /**
     * Set the value of a column to the value of the larger of 2
     * specified columns.
     *
     * @param table1       Table of the first column.
     * @param columnName1  The name of the first column.
     * @param table2       Table of the seconds column.
     * @param columnName2  The name of the seconds column.
     */
    T largerColumn(ISqlTable table1, String columnName1, ISqlTable table2, String columnName2);

    /**
     * Set the value of a column to the value of the smaller of the
     * column or the specified columns.
     *
     * @param table       Table of the specified column.
     * @param columnName  The name of the column.
     */
    T smallerColumn(ISqlTable table, String columnName);

    /**
     * Set the value of a column to the value of the smaller of 2
     * specified columns.
     *
     * @param table1       Table of the first column.
     * @param columnName1  The name of the first column.
     * @param table2       Table of the second column.
     * @param columnName2  The name of the seconds column.
     */
    T smallerColumn(ISqlTable table1, String columnName1, ISqlTable table2, String columnName2);
}
