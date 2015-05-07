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

import javax.annotation.Nullable;

/**
 * Statement data setter.
 */
public interface ISqlDataSetter<T> {

    /**
     * Set the value of a column.
     *
     * @param columnName  The name of the column.
     * @param value       The value to set. If the value is null and the column does not
     *                    accept null values, the columns default value is used.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T set(String columnName, @Nullable Object value);

    /**
     * Set the value of the specified column to the value of another column.
     *
     * <p>Useful for joins.</p>
     *
     * @param columnName       The column to set.
     * @param otherColumnName  The other column to get a value from.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T setColumn(String columnName, String otherColumnName);
}