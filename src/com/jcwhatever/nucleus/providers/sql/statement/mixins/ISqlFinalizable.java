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
import com.jcwhatever.nucleus.providers.sql.statement.ISqlNextStatementBuilder;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Sql statement finalizing methods mixin.
 */
public interface ISqlFinalizable extends ISqlCommittable {

    /**
     * Terminate the current statement to begin the next statement.
     *
     * <p>Causes the current statement to be finalized.</p>
     */
    ISqlNextStatementBuilder endStatement();

    /**
     * Set the current table.
     *
     * <p>Causes the current statement to be finalized.</p>
     *
     * @param table  The table the next statements apply to.
     */
    ISqlNextStatementBuilder setTable(ISqlTable table);

    /**
     * Create a prepared statement.
     *
     * <p>Causes the current statement to be finalized.</p>
     *
     * @throws SQLException
     */
    PreparedStatement[] prepareStatements() throws SQLException;
}
