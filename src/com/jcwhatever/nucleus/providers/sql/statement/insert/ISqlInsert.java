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

package com.jcwhatever.nucleus.providers.sql.statement.insert;

import java.util.Collection;

/**
 * Sql table insert.
 */
public interface ISqlInsert {

    /**
     * Insert a row of values.
     *
     * @param values  The values to insert.
     */
    ISqlInsertFinal values(Object... values);

    /**
     * Insert a collection of row values.
     *
     * @param values           The collection of row values.
     * @param valuesConverter  The converter used to convert objects to a row of values.
     *
     * @param <T>  The collection element type.
     */
    <T> ISqlInsertFinal values(Collection<T> values, ISqlInsertValueConverter<T> valuesConverter);

    /**
     * Converter used to convert an element into row values.
     *
     * @param <T>  The element type.
     */
    interface ISqlInsertValueConverter<T> {

        Object[] getRowValues(T element);
    }
}
