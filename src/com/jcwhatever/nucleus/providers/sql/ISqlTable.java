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
import com.jcwhatever.nucleus.providers.sql.statement.ISqlLogicalOperator;
import com.jcwhatever.nucleus.providers.sql.statement.ISqlClauseLimit;
import com.jcwhatever.nucleus.providers.sql.statement.ISqlClauseLimitOffset;
import com.jcwhatever.nucleus.providers.sql.statement.ISqlClauseOrder;
import com.jcwhatever.nucleus.providers.sql.statement.ISqlClauseWhere;
import com.jcwhatever.nucleus.providers.sql.statement.ISqlDataSetter;
import com.jcwhatever.nucleus.providers.sql.statement.ISqlJoin;
import com.jcwhatever.nucleus.providers.sql.statement.ISqlJoinClause;
import com.jcwhatever.nucleus.providers.sql.statement.ISqlOperator;
import com.jcwhatever.nucleus.providers.sql.statement.ISqlQueryExecutable;
import com.jcwhatever.nucleus.providers.sql.statement.ISqlUpdateExecutable;

/**
 * Database table.
 */
public interface ISqlTable extends INamed {

    /**
     * Get the tables database.
     */
    ISqlDatabase getDatabase();

    /**
     * Get the table definition.
     */
    ISqlTableDefinition getDefinition();

    /**
     * Construct a Select query.
     *
     * <p>Equivalent to invoking {@link #selectRows}.</p>
     *
     * @param columns  The columns to select. Leave empty to select all.
     */
    ISqlTableSelect selectRow(String... columns);

    /**
     * Construct a Select query.
     *
     * <p>Equivalent to invoking {@link #selectRow}.</p>
     *
     * @param columns  The columns to select. Leave empty to select all.
     */
    ISqlTableSelect selectRows(String... columns);

    /**
     * Construct an Update statement.
     *
     * <p>Equivalent to invoking {@link #updateRows}.</p>
     */
    ISqlTableUpdate updateRow();

    /**
     * Construct an Update statement.
     *
     * <p>Equivalent to invoking {@link #updateRow}.</p>
     */
    ISqlTableUpdate updateRows();

    /**
     * Construct an Insert statement.
     *
     * <p>Equivalent to invoking {@link #insertRows}.</p>
     *
     * @param columns  The columns with values to insert. Leave empty to auto
     *                 insert all.
     */
    ISqlTableInsert insertRow(String... columns);

    /**
     * Construct an Insert statement.
     *
     * <p>Equivalent to invoking {@link #insertRow}.</p>
     *
     * @param columns  The columns with values to insert. Leave empty to auto
     *                 insert all.
     */
    ISqlTableInsert insertRows(String... columns);

    /**
     * Construct a Delete statement.
     *
     * <p>Equivalent to invoking {@link #deleteRows}.</p>
     */
    ISqlTableDelete deleteRow();

    /**
     * Construct a Delete statement.
     *
     * <p>Equivalent to invoking {@link #deleteRow}.</p>
     */
    ISqlTableDelete deleteRows();

    // Row Delete

    interface ISqlTableDelete extends ISqlClauseWhere<ISqlTableDeleteOperator>, ISqlUpdateExecutable {}

    interface ISqlTableDeleteOperator extends ISqlOperator<ISqlTableDeleteBoolOperator>{}

    interface ISqlTableDeleteBoolOperator extends
            ISqlLogicalOperator<ISqlTableDeleteOperator>, ISqlUpdateExecutable {}

    // Table Insert

    interface ISqlTableInsert {

        ISqlTableInsertFinal values(Object... values);
    }

    interface ISqlTableInsertFinal extends ISqlTableInsert, ISqlUpdateExecutable {

        ISqlTableInsertExists ifExists();
    }

    interface ISqlTableInsertExists extends
            ISqlDataSetter<ISqlTableInsertExists>, ISqlUpdateExecutable {}

    // Table Update

    interface ISqlTableUpdate extends
            ISqlDataSetter<ISqlTableUpdateFinal> {}

    interface ISqlTableUpdateFinal extends ISqlTableUpdate, ISqlUpdateExecutable {

        ISqlTableUpdateOperator where(String column);
    }

    interface ISqlTableUpdateOperator extends
            ISqlOperator<ISqlTableUpdateBoolOperator> {}

    interface ISqlTableUpdateBoolOperator extends
            ISqlLogicalOperator<ISqlTableUpdateOperator>,
            ISqlUpdateExecutable, ISqlTableUpdateClause {}

    interface ISqlTableUpdateClause extends
            ISqlUpdateExecutable,
            ISqlClauseOrder<ISqlTableUpdateClause>,
            ISqlClauseLimit<ISqlTableUpdateClause> {}

    // Table Select

    interface ISqlTableSelect extends ISqlTableSelectJoin, ISqlTableSelectWhere, ISqlTableSelectClause {

        ISqlTableSelectWhere into(String tableName);
    }

    interface ISqlTableSelectJoin extends ISqlJoin<ISqlTableSelectJoinClause>, ISqlTableSelectWhere {}

    interface ISqlTableSelectJoinClause extends ISqlTableSelectWhere, ISqlJoinClause<ISqlTableSelectJoin> {}

    interface ISqlTableSelectWhere extends ISqlQueryExecutable {

        ISqlTableSelectOperator where(String column);
    }

    interface ISqlTableSelectOperator extends
            ISqlOperator<ISqlTableSelectBoolOperator> {}

    interface ISqlTableSelectBoolOperator extends
            ISqlLogicalOperator<ISqlTableSelectOperator>, ISqlTableSelectClause, ISqlQueryExecutable {

        /**
         * End the current query and add a union select.
         */
        ISqlTableSelect unionSelect(String... columnNames);

        /**
         * End the current query and add a union all select.
         */
        ISqlTableSelect unionAllSelect(String... columnNames);
    }

    interface ISqlTableSelectClause extends
            ISqlQueryExecutable, ISqlClauseLimit<ISqlTableSelectClause>,
            ISqlClauseLimitOffset<ISqlTableSelectClause>,
            ISqlClauseOrder<ISqlTableSelectClause> {}

}
