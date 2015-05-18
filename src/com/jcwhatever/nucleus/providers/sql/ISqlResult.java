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
 * Results for 1 or more executed sql statements.
 */
public interface ISqlResult {

    /**
     * Determine if the result contains query results.
     */
    boolean hasQueryResults();

    /**
     * Determine if the result contains row update counts.
     */
    boolean hasUpdatedRows();

    /**
     * Get the first query result.
     *
     * @return  The result or null if there are no query results.
     */
    @Nullable
    ISqlQueryResult getFirstResult();

    /**
     * Get the first row update count.
     *
     * @return  The count or 0 if there are no row update counts.
     */
    int getFirstRowUpdate();

    /**
     * Get all query results in the order they were executed.
     *
     * @return  The results.
     */
    ISqlQueryResult[] getQueryResults();

    /**
     * Get all row update counts in the order they were executed.
     */
    int[] getRowsUpdated();

    /**
     * Close all query results.
     *
     * <p>Invoke when finished with results. If there are no query results,
     * nothing happens.</p>
     */
    void closeQueryResults();
}
