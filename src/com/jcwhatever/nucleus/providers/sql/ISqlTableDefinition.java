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

import javax.annotation.Nullable;

/**
 * Interface for a table definition.
 */
public interface ISqlTableDefinition {

    /**
     * Get the total number of columns in the table.
     */
    int totalColumns();

    /**
     * Get the total number of columns using a compound data type
     * in the table.
     */
    int totalCompoundColumns();

    /**
     * Determine if the table has a primary key.
     */
    boolean hasPrimaryKey();

    /**
     * Determine if the table contains compound data types.
     */
    boolean hasCompoundDataTypes();

    /**
     * Get columns that are using compound data types.
     */
    ISqlTableColumn[] getCompoundColumns();

    /**
     * Get all columns of the table.
     */
    ISqlTableColumn[] getColumns();

    /**
     * Get all column names.
     */
    String[] getColumnNames();

    /**
     * Get a table column by name.
     *
     * @param name  The name of the column.
     *
     * @return  The column or null if not found.
     */
    @Nullable
    ISqlTableColumn getColumn(String name);

    /**
     * Get the tables primary key definition.
     *
     * @return  The primary key definition or null if the table has no
     * primary key.
     */
    @Nullable
    ISqlTableColumn getPrimaryKey();

    /**
     * Get the name of the preferred database engine for the
     * table.
     *
     * @return  The engine or null if not specified. If not specified, the database
     * default engine is used.
     */
    @Nullable
    String getEngineName();

    /**
     * Interface for a table column definition.
     */
    interface ISqlTableColumn extends INamed {

        /**
         * Get the column data type.
         */
        ISqlDbType getDataType();

        /**
         * Determine if the column is a primary key.
         */
        boolean isPrimary();

        /**
         * Determine if the column has the unique constraint.
         */
        boolean isUnique();

        /**
         * Determine if the column is a foreign key.
         */
        boolean isForeign();

        /**
         * Determine if the column can have null values.
         */
        boolean isNullable();

        /**
         * Determine if the column is auto incrementing.
         */
        boolean isAutoIncrement();

        /**
         * Get the foreign key table name.
         *
         * @return  The table name or null if the column does not have
         * the foreign key constraint.
         */
        @Nullable
        String getForeignTableName();

        /**
         * Get the foreign key table primary key name.
         *
         * @return  The primary key name or null if the column does not have
         * the foreign key constraint.
         */
        @Nullable
        String getForeignTablePrimary();

        /**
         * Get the foreign key cascade delete clause.
         */
        boolean isCascadeDelete();

        /**
         * Get the initial auto increment start value.
         *
         * @return  The start value or -1 if the column is not auto incrementing.
         */
        long getAutoIncrementStart();

        /**
         * Get the incremental value.
         *
         * @return  The incremental value or -1 if the column is not auto
         * incrementing.
         */
        long getAutoIncrement();
    }
}
