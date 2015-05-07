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

import com.jcwhatever.nucleus.utils.coords.SyncLocation;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Sql results.
 */
public interface ISqlResult extends ResultSet {

    /**
     * Retrieves the value of the specified column in the current row
     * as a {@link UUID}.
     *
     * @param columnIndex The column index. Starts at 1.
     *
     * @throws SQLException
     */
    UUID getUUID(int columnIndex) throws SQLException;

    /**
     * Retrieves the value of the specified column in the current row
     * as a {@link UUID}.
     *
     * @param columnName  The column name.
     *
     * @throws SQLException
     */
    UUID getUUID(String columnName) throws SQLException;

    /**
     * Retrieves the value of the specified column in the current row
     * as a {@link SyncLocation}.
     *
     * @param columnIndex  The column index. Starts at 1.
     *
     * @throws SQLException
     */
    SyncLocation getLocation(int columnIndex) throws SQLException;

    /**
     * Retrieves the value of the specified column in the current row
     * as a {@link SyncLocation}.
     *
     * @param columnName  The column name.
     *
     * @throws SQLException
     */
    SyncLocation getLocation(String columnName) throws SQLException;

    /**
     * Retrieves the value of the specified column in the current row
     * as a {@link Vector}.
     *
     * @param columnIndex  The column index. Starts at 1.
     *
     * @throws SQLException
     */
    Vector getVector(int columnIndex) throws SQLException;

    /**
     * Retrieves the value of the specified column in the current row
     * as a {@link Vector}.
     *
     * @param columnName  The column name.
     *
     * @throws SQLException
     */
    Vector getVector(String columnName) throws SQLException;

    /**
     * Retrieves the value of the specified column in the current row
     * as an {@link ItemStack}.
     *
     * @param columnIndex  The column index. Starts at 1.
     *
     * @throws SQLException
     */
    ItemStack[] getItemStacks(int columnIndex) throws SQLException;

    /**
     * Retrieves the value of the specified column in the current row
     * as an {@link ItemStack}.
     *
     * @param columnName  The column name.
     *
     * @throws SQLException
     */
    ItemStack[] getItemStacks(String columnName) throws SQLException;
}
