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

import javax.annotation.Nullable;

/**
 * Used to define a database tables.
 */
public interface ISqlTableBuilder {

    /**
     * Specify that the table will most often be used for reading and
     * inserting data.
     */
    ISqlTableBuilderTransact usageReadInsert();

    /**
     * Specify that the table will most often be used for writing data.
     */
    ISqlTableBuilderTransact usageReadInsertUpdate();

    /**
     * Specify that the table is a temporary working table that will be
     * automatically removed after transactions/sessions.
     */
    ISqlTableBuilderColumns usageTemporary();

    /**
     * Skip table engine specification and set the explicit name
     * of the table engine to use.
     *
     * <p>Not recommended as some environments may require a different
     * engine. (i.e. MySql Cluster) or the specified engine may
     * not be available.</p>
     *
     * @param engineName  The name of the engine to use.
     */
    ISqlTableBuilderColumns setEngine(String engineName);

    /**
     * Use the sql servers default engine.
     */
    ISqlTableBuilderColumns defaultEngine();


    interface ISqlTableBuilderTransact {

        /**
         * Specify that the table will be used for ACID transactions
         * and/or requires excellent data integrity.
         */
        ISqlTableBuilderColumns transactional();

        /**
         * Specify that the table will not use ACID transactions.
         */
        ISqlTableBuilderColumns nonTransactional();
    }

    interface ISqlTableBuilderColumns {

        /**
         * Add a data column.
         *
         * @param name  The name of the column.
         * @param type  The column data type.
         */
        ISqlTableBuilderConstraints column(String name, ISqlDbType type);
    }

    interface ISqlTableBuilderConstraints extends
            ISqlTableBuilderFinal, ISqlTableBuilderNullable,
            ISqlTableBuilderIndex, ISqlTableBuilderDefaultValue {

        /**
         * Set the previously specified column as the primary key.
         *
         * <p>When added to more than one column, a composite primary key is created.</p>
         */
        ISqlTableBuilderPrimaryKey primary();

        /**
         * Specify a unique key column.
         */
        ISqlTableBuilderFinal unique();

        /**
         * Specify the previously specified column as a foreign key constraint
         */
        ISqlTableBuilderForeign foreign(String tableName, String primaryKey);
    }

    interface ISqlTableBuilderNullable {

        /**
         * Specify a column as allowing null values.
         */
        ISqlTableBuilderFinal nullable();
    }

    interface ISqlTableBuilderIndex {

        /**
         * Specify the previously specified column as having an index.
         *
         * <p>The default index type is used.</p>
         *
         * @param indexNames  The name of the index or indexes to add the column to. The names
         *                    are primarily used by the API to assign index context. The
         *                    implementation may or may not use the specified names for the
         *                    actual indexes.
         */
        ISqlTableBuilderIndexFinal index(String... indexNames);
    }


    interface ISqlTableBuilderDefaultValue {

        /**
         * Specify the default value for the previously specified column.
         *
         * <p>Note that not all providers will be able to support default values
         * for compound data types.</p>
         *
         * @param value  The default value.
         */
        ISqlTableBuilderDefaultValueFinal defaultValue(@Nullable Object value);
    }

    interface ISqlTableBuilderIndexFinal extends
            ISqlTableBuilderFinal, ISqlTableBuilderNullable, ISqlTableBuilderDefaultValue {
    }


    interface ISqlTableBuilderDefaultValueFinal extends
            ISqlTableBuilderFinal, ISqlTableBuilderNullable, ISqlTableBuilderIndex {
    }

    interface ISqlTableBuilderPrimaryKey extends ISqlTableBuilderFinal {

        /**
         * Add auto incrementing to a primary key.
         *
         * @throws IllegalStateException if the primary key data type does not
         * support auto increment.
         */
        ISqlTableBuilderFinal autoIncrement();
    }

    interface ISqlTableBuilderForeign extends ISqlTableBuilderFinal {

        /**
         * Add cascade delete clause for a foreign key.
         */
        ISqlTableBuilderFinal cascadeDelete();
    }

    interface ISqlTableBuilderFinal extends ISqlTableBuilderColumns {

        /**
         * Define the database table.
         */
        ISqlTableDefinition define();
    }
}
