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

package com.jcwhatever.nucleus.providers.sql.datanode;

import com.jcwhatever.nucleus.providers.sql.ISqlQueryResult;
import com.jcwhatever.nucleus.providers.sql.ISqlTable;

import java.sql.SQLException;

/**
 * Sql based data node builder.
 *
 * <p>The data node the builder created is able to save changes back to the
 * table the node was originally set from.</p>
 */
public interface ISqlDataNodeBuilder {

    /**
     * Set the source context for setter methods.
     *
     * @param table      The table context.
     * @param result     The query result to get data from.
     * @param pKeyValue  The value of the current query result rows primary key column.
     */
    ISqlDataNodeBuilderSetter fromSource(ISqlTable table, ISqlQueryResult result, Object pKeyValue);

    /**
     * Set the source context for the setter methods.
     *
     * <p>Setters will have initial values of null or the columns default value
     * where no value is specified.</p>
     *
     * @param table     The table context.
     * @param pKeyValue  The primary key column of the current row.
     */
    ISqlDataNodeBuilderEmptySetter withoutSource(ISqlTable table, Object pKeyValue);


    interface ISqlDataNodeBuilderSetter extends ISqlDataNodeBuilder {

        /**
         * Set all values defined by the current source table.
         */
        ISqlDataNodeBuilderSetter setAll() throws SQLException;

        /**
         * Set a data node value using the specified column name from the current
         * query result row.
         *
         * <p>The node name is the same as the column name.</p>
         *
         * @param columnName  The name of the column.
         *
         * @return  Self for chaining.
         */
        ISqlDataNodeBuilderSetter set(String columnName) throws SQLException;

        /**
         * Set a data node value using the specified column name from the current
         * query result row.
         *
         * @param columnName  The name of the table column.
         * @param nodeName    The name of the node to set.
         *
         * @return  Self for chaining.
         */
        ISqlDataNodeBuilderSetter set(String columnName, String nodeName) throws SQLException;

        /**
         * Set a data node value using the specified column name from the current
         * query result row.
         *
         * @param columnIndex  The index of the column in the results.
         * @param nodeName     The name of the node to set.
         *
         * @return  Self for chaining.
         */
        ISqlDataNodeBuilderSetter set(int columnIndex, String nodeName) throws SQLException;

        /**
         * Build the data node.
         */
        ISqlDataNode build();
    }

    interface ISqlDataNodeBuilderEmptySetter extends ISqlDataNodeBuilder {

        /**
         * Set all values defined by the current source table.
         */
        ISqlDataNodeBuilderEmptySetter setAll();

        /**
         * Set a data node value using the specified column name from the current
         * query result row.
         *
         * <p>The node name is the same as the column name.</p>
         *
         * @param columnName  The name of the column.
         *
         * @return  Self for chaining.
         */
        ISqlDataNodeBuilderEmptySetter set(String columnName);

        /**
         * Set a data node value using the specified column name from the current
         * query result row.
         *
         * @param columnName  The name of the table column.
         * @param nodeName    The name of the node to set.
         *
         * @return  Self for chaining.
         */
        ISqlDataNodeBuilderEmptySetter set(String columnName, String nodeName);

        /**
         * Set a data node value using the specified column name from the current
         * query result row.
         *
         * <p>The node name is the same as the column name.</p>
         *
         * @param columnName  The name of the column.
         *
         * @return  Self for chaining.
         */
        ISqlDataNodeBuilderEmptySetter set(String columnName, Object value);

        /**
         * Set a data node value using the specified column name from the current
         * query result row.
         *
         * @param columnName  The name of the table column.
         * @param nodeName    The name of the node to set.
         *
         * @return  Self for chaining.
         */
        ISqlDataNodeBuilderEmptySetter set(String columnName, String nodeName, Object value);

        /**
         * Build the data node.
         */
        ISqlDataNode build();
    }
}
