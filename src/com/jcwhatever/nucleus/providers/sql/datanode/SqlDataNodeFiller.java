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
import com.jcwhatever.nucleus.providers.sql.ISqlTableDefinition;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;

import java.sql.SQLException;

/**
 * Fills a data node with data from an {@link ISqlQueryResult}.
 */
public class SqlDataNodeFiller {

    private IDataNode _dataNode;
    private ISqlQueryResult _source;

    /**
     * Constructor.
     *
     * @param source  The sql data source.
     * @param output  The data node to copy source data to.
     */
    public SqlDataNodeFiller(ISqlQueryResult source, IDataNode output) {
        PreCon.notNull(source);
        PreCon.notNull(output);
        _dataNode = output;
        _source = source;
    }

    /**
     * Copy all columns specified in a table definition from the query results current
     * row into the data node using the same name as the column.
     *
     * @param definition  The table definition.
     *
     * @return  Self for chaining.
     *
     * @throws SQLException
     */
    public SqlDataNodeFiller copy(ISqlTableDefinition definition) throws SQLException {
        PreCon.notNull(definition);

        String[] columns = definition.getColumnNames();

        for (String columnName : columns) {

            _dataNode.set(columnName, _source.getObject(columnName));
        }

        return this;
    }

    /**
     * Copy the specified column name from the query results current row into
     * the data node using the same name as the column.
     *
     * @param columnName  The name of the column.
     *
     * @return  Self for chaining.
     *
     * @throws SQLException
     */
    public SqlDataNodeFiller copy(String columnName) throws SQLException {
        return copy(columnName, columnName);
    }

    /**
     * Copy the specified column name from the query results current row into
     * the data node using the specified data key name or path.
     *
     * @param columnName   The name of the column.
     * @param dataKeyName  The name of the data key or path.
     *
     * @return  Self for chaining.
     *
     * @throws SQLException
     */
    public SqlDataNodeFiller copy(String columnName, String dataKeyName) throws SQLException {
        PreCon.notNullOrEmpty(columnName);
        PreCon.notNull(dataKeyName);

        _dataNode.set(dataKeyName, _source.getObject(columnName));

        return this;
    }

    /**
     * Copy data from the specified column index from the query results current row
     * into the data node using the specified data key name or path.
     *
     * @param columnIndex  The index position of the column. Index starts at 1.
     * @param dataKeyName  The name of the data key or path.
     *
     * @return  Self for chaining.
     *
     * @throws SQLException
     */
    public SqlDataNodeFiller copy(int columnIndex, String dataKeyName) throws SQLException {
        PreCon.notNull(dataKeyName);

        _dataNode.set(dataKeyName, _source.getObject(columnIndex));

        return this;
    }
}
