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

/**
 * Used to define a database tables.
 */
public interface ISqlTableDefiner {

    /**
     * Specify that the table will most often be used for reading and
     * inserting data.
     */
    ISqlTableDefinerTransact usageReadInsert();

    /**
     * Specify that the table will most often be used for writing data.
     */
    ISqlTableDefinerTransact usageReadInsertUpdate();

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
    ISqlTableDefinerColumns setEngine(String engineName);

    /**
     * Use the sql servers default engine.
     */
    ISqlTableDefinerColumns defaultEngine();


    interface ISqlTableDefinerTransact {

        /**
         * Specify that the table will be used for ACID transactions
         * and/or requires excellent data integrity.
         */
        ISqlTableDefinerColumns transactional();

        /**
         * Specify that the table will not use ACID transactions.
         */
        ISqlTableDefinerColumns nonTransactional();
    }

    interface ISqlTableDefinerColumns {

        /**
         * Add a data column.
         *
         * @param name  The name of the column.
         * @param type  The column data type.
         */
        ISqlTableDefinerConstraints column(String name, ISqlDbType type);
    }

    interface ISqlTableDefinerConstraints extends ISqlTableDefinerFinal {

        /**
         * Set the previously specified column as the primary key.
         */
        ISqlTableDefinerPrimaryKey primary();

        /**
         * Specify a unique key column.
         */
        ISqlTableDefinerFinal unique();

        /**
         * Specify the previously specified column as a foreign key constraint
         */
        ISqlTableDefinerForeign foreign(String tableName, String primaryKey);

        /**
         * Specify a column as allowing null values.
         */
        ISqlTableDefinerFinal nullable();
    }

    interface ISqlTableDefinerPrimaryKey extends ISqlTableDefinerFinal {

        /**
         * Add auto incrementing to a primary key.
         *
         * @throws IllegalStateException if the primary key data type does not
         * support auto increment.
         */
        ISqlTableDefinerFinal autoIncrement();

        /**
         * Add auto incrementing to a primary key.
         *
         * @param start      The initial value.
         * @param increment  The incremental value.
         *
         * @throws IllegalStateException if the primary key data type does not
         * support auto increment.
         */
        ISqlTableDefinerFinal autoIncrement(long start, long increment);
    }

    interface ISqlTableDefinerForeign extends ISqlTableDefinerFinal {

        /**
         * Add cascade delete clause for a foreign key.
         */
        ISqlTableDefinerFinal cascadeDelete();
    }

    interface ISqlTableDefinerFinal extends ISqlTableDefinerColumns {

        /**
         * Define the database table.
         */
        ISqlTableDefinition define();
    }
}
