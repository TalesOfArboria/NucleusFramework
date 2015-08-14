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

import com.jcwhatever.nucleus.providers.sql.ISqlTable;
import com.jcwhatever.nucleus.providers.sql.statement.delete.ISqlDelete;
import com.jcwhatever.nucleus.providers.sql.statement.generators.IColumnNameGenerator;
import com.jcwhatever.nucleus.providers.sql.statement.insert.ISqlInsert;
import com.jcwhatever.nucleus.providers.sql.statement.insertinto.ISqlInsertInto;
import com.jcwhatever.nucleus.providers.sql.statement.select.ISqlSelect;
import com.jcwhatever.nucleus.providers.sql.statement.update.ISqlUpdate;

/**
 * Sql statement builder.
 */
public interface ISqlStatementBuilder {

    /**
     * Insert the beginning of a transaction.
     */
    ISqlStatementBuilder beginTransaction();

    /**
     * Construct a Select query.
     *
     * <p>Equivalent to invoking {@link #selectRows}.</p>
     *
     * @param columns  The columns to select. Leave empty to select all.
     */
    ISqlSelect selectRow(String... columns);

    /**
     * Construct a Select query.
     *
     * <p>Equivalent to invoking {@link #selectRow}.</p>
     *
     * @param columns  The columns to select. Leave empty to select all.
     */
    ISqlSelect selectRows(String... columns);

    /**
     * Construct a Select query
     *
     * @param nameGenerator  The generator that will supply the column names.
     */
    ISqlSelect selectRows(IColumnNameGenerator nameGenerator);

    /**
     * Construct an Update statement.
     *
     * <p>Equivalent to invoking {@link #updateRows}.</p>
     */
    ISqlUpdate updateRow();

    /**
     * Construct an Update statement.
     *
     * <p>Equivalent to invoking {@link #updateRow}.</p>
     */
    ISqlUpdate updateRows();

    /**
     * Construct an Insert statement.
     *
     * <p>Equivalent to invoking {@link #insertRows}.</p>
     *
     * @param columns  The columns with values to insert. Leave empty to auto
     *                 insert all.
     */
    ISqlInsert insertRow(String... columns);

    /**
     * Construct an Insert statement.
     *
     * <p>Equivalent to invoking {@link #insertRow}.</p>
     *
     * @param columns  The columns with values to insert. Leave empty to auto
     *                 insert all.
     */
    ISqlInsert insertRows(String... columns);

    /**
     * Construct a Select query
     *
     * @param nameGenerator  The generator that will supply the column names.
     */
    ISqlInsert insertRows(IColumnNameGenerator nameGenerator);

    /**
     * Construct an Insert statement.
     *
     * @param table  The table to insert rows into from the
     *               current table.
     */
    ISqlInsertInto insertInto(ISqlTable table);

    /**
     * Construct a Delete statement.
     *
     * <p>Equivalent to invoking {@link #deleteRows}.</p>
     */
    ISqlDelete deleteRow();

    /**
     * Construct a Delete statement.
     *
     * <p>Equivalent to invoking {@link #deleteRow}.</p>
     */
    ISqlDelete deleteRows();
}
