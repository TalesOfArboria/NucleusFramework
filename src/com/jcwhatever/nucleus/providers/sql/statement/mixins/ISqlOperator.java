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

import com.jcwhatever.nucleus.utils.converters.IConverter;

import java.util.Collection;
import javax.annotation.Nullable;

/**
 * Sql statement conditional operators mixin.
 */
public interface ISqlOperator<T> {

    /**
     * Determine if the previously specified column is equal to the specified
     * value.
     *
     * @param value  The value to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T isEqualTo(@Nullable Object value);

    /**
     * Determine if the previously specified column is equal to 1 or more of the
     * specified values.
     *
     * @param values  The values to compare.
     *
     * @param <T1>  The value type.
     */
    <T1> T isEqualToAny(Collection<T1> values);

    /**
     * Determine if the previously specified column is equal to 1 or more of the
     * specified values.
     *
     * @param values     The values to compare.
     * @param converter  Converter used to convert objects in collection to the
     *                   proper value.
     *
     * @param <T1>  The value type.
     */
    <T1, T2> T isEqualToAny(Collection<T1> values, IConverter<T1, T2> converter);

    /**
     * Determine if the previously specified column is equal to the specified
     * column.
     *
     * @param columnName  The column to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T isEqualToColumn(String columnName);

    /**
     * Determine if the previously specified column is equal to 1 or more of the
     * specified columns.
     *
     * @param columnNames  The columns to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T isEqualToAnyColumn(String... columnNames);

    /**
     * Determine if the previously specified column is NOT equal to the specified
     * value.
     *
     * @param value  The value to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T isNotEqualTo(@Nullable Object value);

    /**
     * Determine if the previously specified column is NOT equal to all of the
     * specified values.
     *
     * @param values  The values to compare.
     *
     * @param <T1>  The value type.
     */
    <T1> T isNotEqualToAll(Collection<T1> values);

    /**
     * Determine if the previously specified column is NOT equal to all of the
     * specified values.
     *
     * @param values     The values to compare.
     * @param converter  Converter used to convert objects in collection to the
     *                   proper value.
     *
     * @param <T1>  The value type.
     */
    <T1, T2> T isNotEqualToAll(Collection<T1> values, IConverter<T1, T2> converter);

    /**
     * Determine if the previously specified column is NOT equal to the specified
     * column.
     *
     * @param columnName  The column to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T isNotEqualToColumn(String columnName);

    /**
     * Determine if the previously specified column is NOT equal to all of the
     * specified columns.
     *
     * @param columnNames  The columns to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T isNotEqualToAllColumns(String... columnNames);

    /**
     * Determine if the previously specified column is greater than the
     * specified value.
     *
     * @param value  The value to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T isGreaterThan(@Nullable Object value);

    /**
     * Determine if the previously specified column is greater than 1 or more
     * of the specified values.
     *
     * @param values  The value to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    <T1> T isGreaterThanAny(Collection<T1> values);

    /**
     * Determine if the previously specified column is greater than all
     * of the specified values.
     *
     * @param values  The value to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    <T1> T isGreaterThanAll(Collection<T1> values);

    /**
     * Determine if the previously specified column is greater than 1 or more
     * of the specified values.
     *
     * @param values     The value to compare.
     * @param converter  Converter used to convert objects in collection to the
     *                   proper value.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    <T1, T2> T isGreaterThanAny(Collection<T1> values, IConverter<T1, T2> converter);

    /**
     * Determine if the previously specified column is greater than all
     * of the specified values.
     *
     * @param values     The value to compare.
     * @param converter  Converter used to convert objects in collection to the
     *                   proper value.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    <T1, T2> T isGreaterThanAll(Collection<T1> values, IConverter<T1, T2> converter);

    /**
     * Determine if the previously specified column is greater than the
     * specified column.
     *
     * @param columnName  The column to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T isGreaterThanColumn(String columnName);

    /**
     * Determine if the previously specified column is greater than 1 or more
     * of the specified columns.
     *
     * @param columnNames  The columns to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T isGreaterThanAnyColumn(String... columnNames);

    /**
     * Determine if the previously specified column is greater than all
     * of the specified columns.
     *
     * @param columnNames  The columns to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T isGreaterThanAllColumns(String... columnNames);

    /**
     * Determine if the previously specified column is greater than or
     * equal to the specified value.
     *
     * @param value  The value to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T isGreaterOrEqualTo(@Nullable Object value);

    /**
     * Determine if the previously specified column is greater than or
     * equal to 1 or more of the specified values.
     *
     * @param values  The values to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    <T1> T isGreaterOrEqualToAny(Collection<T1> values);

    /**
     * Determine if the previously specified column is greater than or
     * equal to all of the specified values.
     *
     * @param values  The values to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    <T1> T isGreaterOrEqualToAll(Collection<T1> values);

    /**
     * Determine if the previously specified column is greater than or
     * equal to 1 or more of the specified values.
     *
     * @param values     The values to compare.
     * @param converter  Converter used to convert objects in collection to the
     *                   proper value.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    <T1, T2> T isGreaterOrEqualToAny(Collection<T1> values, IConverter<T1, T2> converter);

    /**
     * Determine if the previously specified column is greater than or
     * equal to all of the specified values.
     *
     * @param values     The values to compare.
     * @param converter  Converter used to convert objects in collection to the
     *                   proper value.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    <T1, T2> T isGreaterOrEqualToAll(Collection<T1> values, IConverter<T1, T2> converter);

    /**
     * Determine if the previously specified column is greater than or
     * equal to the specified column.
     *
     * @param columnName  The column to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T isGreaterOrEqualToColumn(String columnName);

    /**
     * Determine if the previously specified column is greater than or
     * equal to 1 or more of the specified columns.
     *
     * @param columnNames  The columns to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T isGreaterOrEqualToAnyColumn(String... columnNames);

    /**
     * Determine if the previously specified column is greater than or
     * equal to all of the specified columns.
     *
     * @param columnNames  The columns to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T isGreaterOrEqualToAllColumns(String... columnNames);

    /**
     * Determine if the previously specified column is less than the
     * specified value.
     *
     * @param value  The value to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T isLessThan(@Nullable Object value);

    /**
     * Determine if the previously specified column is less than 1 or
     * more of the specified values.
     *
     * @param values  The values to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    <T1> T isLessThanAny(Collection<T1> values);

    /**
     * Determine if the previously specified column is less than all
     * of the specified values.
     *
     * @param values  The values to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    <T1> T isLessThanAll(Collection<T1> values);

    /**
     * Determine if the previously specified column is less than 1 or
     * more of the specified values.
     *
     * @param values     The values to compare.
     * @param converter  Converter used to convert objects in collection to the
     *                   proper value.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    <T1, T2> T isLessThanAny(Collection<T1> values, IConverter<T1, T2> converter);

    /**
     * Determine if the previously specified column is less than all
     * of the specified values.
     *
     * @param values     The values to compare.
     * @param converter  Converter used to convert objects in collection to the
     *                   proper value.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    <T1, T2> T isLessThanAll(Collection<T1> values, IConverter<T1, T2> converter);

    /**
     * Determine if the previously specified column is less than the
     * specified column.
     *
     * @param columnName  The column to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T isLessThanColumn(String columnName);

    /**
     * Determine if the previously specified column is less than 1 or
     * more of the specified columns.
     *
     * @param columnNames  The columns to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T isLessThanAnyColumn(String... columnNames);

    /**
     * Determine if the previously specified column is less than all
     * of the specified columns.
     *
     * @param columnNames  The columns to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T isLessThanAllColumns(String... columnNames);

    /**
     * Determine if the previously specified column is less than or
     * equal to the specified value.
     *
     * @param value  The value to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T isLessOrEqualTo(@Nullable Object value);

    /**
     * Determine if the previously specified column is less than or
     * equal to 1 or more of the specified values.
     *
     * @param values  The values to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    <T1> T isLessOrEqualToAny(Collection<T1> values);

    /**
     * Determine if the previously specified column is less than or
     * equal to all of the specified values.
     *
     * @param values  The values to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    <T1> T isLessOrEqualToAll(Collection<T1> values);

    /**
     * Determine if the previously specified column is less than or
     * equal to 1 or more of the specified values.
     *
     * @param values     The values to compare.
     * @param converter  Converter used to convert objects in collection to the
     *                   proper value.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    <T1, T2> T isLessOrEqualToAny(Collection<T1> values, IConverter<T1, T2> converter);

    /**
     * Determine if the previously specified column is less than or
     * equal to all of the specified values.
     *
     * @param values     The values to compare.
     * @param converter  Converter used to convert objects in collection to the
     *                   proper value.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    <T1, T2> T isLessOrEqualToAll(Collection<T1> values, IConverter<T1, T2> converter);

    /**
     * Determine if the previously specified column is less than or
     * equal to the specified column.
     *
     * @param columnName  The column to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T isLessOrEqualToColumn(String columnName);

    /**
     * Determine if the previously specified column is less than or
     * equal to 1 or more of the specified columns.
     *
     * @param columnNames  The columns to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T isLessOrEqualToAnyColumn(String... columnNames);

    /**
     * Determine if the previously specified column is less than or
     * equal to all of the specified columns.
     *
     * @param columnNames  The columns to compare.
     *
     * @throws IllegalStateException if the statement is finalized.
     */
    T isLessOrEqualToAllColumns(String... columnNames);
}
