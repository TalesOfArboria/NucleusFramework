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

import java.util.Collection;
import javax.annotation.Nullable;

/**
 * Interface for a type that manages data types.
 */
public interface ISqlDataTypes {

    /**
     * Get a data type by the Java value type.
     *
     * <p>Does not return data types that require extra parameters such
     * as size.</p>
     *
     * <p>If the data type is a numerical type that can be signed or unsigned,
     * the database default is returned.</p>
     *
     * @param typeClazz  The java class data type equivalent. For primitives, use
     *                   the primitive class, not the wrapper.
     *
     * @return  The database data type or null if an equivalent could not
     * be found.
     */
    @Nullable
    ISqlDbType getDataType(Class<?> typeClazz);

    /**
     * Get a data type by value type.
     *
     * <p>If the data type is not a signable type, the</p>
     *
     * @param typeClazz  The java class data type equivalent. For primitives, use
     *                   the primitive class, not the wrapper.
     * @param isSigned   Specify if the data type is a signed or unsigned numerical
     *                   value. If the requested data type is not signable, this
     *                   argument is ignored.
     *
     * @return  The database data type or null if an equivalent could not
     * be found.
     */
    @Nullable
    ISqlDbType getDataType(Class<?> typeClazz, boolean isSigned);

    /**
     * Get a data type by name.
     *
     * <p>Does not return data types that require extra parameters such
     * as size.</p>
     *
     * <p>If the data type is a numerical type that can be signed or unsigned,
     * the database default is returned.</p>
     *
     * @param typeName  The name of the data type.
     *
     * @return  The data type or null if not found.
     */
    @Nullable
    ISqlDbType getDataType(String typeName);

    /**
     * Get a data type by name.
     *
     * <p>Does not return data types that require extra parameters such
     * as size.</p>
     *
     * @param typeName  The name of the data type.
     * @param isSigned  Specify if the data type is a signed or unsigned numerical
     *                  value. If the requested data type is not signable, this
     *                  argument is ignored.
     *
     * @return  The data type or null if not found.
     */
    @Nullable
    ISqlDbType getDataType(String typeName, boolean isSigned);

    /**
     * Get all available data types for the database.
     *
     * <p>Does not return data types that require extra parameters such
     * as size.</p>
     */
    Collection<ISqlDbType> getDataTypes();

    /**
     * Get the data type for storing booleans.
     */
    ISqlDbType getBoolean();

    /**
     * Get the data type for storing a signed byte.
     */
    ISqlDbType getSignedByte();

    /**
     * Get the data type for storing an unsigned byte.
     */
    ISqlDbType getUnsignedByte();

    /**
     * Get the data type for storing a signed short.
     */
    ISqlDbType getSignedShort();

    /**
     * Get the data type for storing an unsigned short.
     */
    ISqlDbType getUnsignedShort();

    /**
     * Get the data type for storing a 3-byte signed integer.
     */
    ISqlDbType getSignedMediumInteger();

    /**
     * Get the data type for storing a 3-byte unsigned integer.
     */
    ISqlDbType getUnsignedMediumInteger();

    /**
     * Get the data type for storing a 4-byte signed integer.
     */
    ISqlDbType getSignedInteger();

    /**
     * Get the data type for storing a 4-byte unsigned integer.
     */
    ISqlDbType getUnsignedInteger();

    /**
     * Get the data type for storing a signed long.
     */
    ISqlDbType getSignedLong();

    /**
     * Get the data type for storing an unsigned long.
     */
    ISqlDbType getUnsignedLong();

    /**
     * Get the data type for storing a float.
     */
    ISqlDbType getFloat();

    /**
     * Get the data type for storing a double.
     */
    ISqlDbType getDouble();

    /**
     * Get the data type for storing a decimal.
     */
    ISqlDbType getBigDecimal();

    /**
     * Get the data type for storing a date/timestamp.
     */
    ISqlDbType getDate();

    /**
     * Get the data type for a location.
     */
    ISqlDbType getLocation();

    /**
     * Get the data type for a vector.
     */
    ISqlDbType getVector();

    /**
     * Get the data type for a UUID.
     */
    ISqlDbType getUUID();

    /**
     * Get the data type for an item stack.
     */
    ISqlDbType getItemStacks();

    /**
     * Get a data type for storing a string of a specified fixed size.
     *
     * @param size  The data storage size.
     */
    ISqlDbType getFixedString(int size);

    /**
     * Get a data type for storing a variable length string with the
     * specified max size.
     *
     * @param size  The max data storage size.
     */
    ISqlDbType getString(int size);

    /**
     * Get a data type for storing a fixed size byte array with the
     * specified size.
     *
     * @param size  The data storage size.
     */
    ISqlDbType getFixedByteArray(int size);

    /**
     * Get a data type for storing a variable length byte array with
     * the specified size.
     *
     * @param size  The data storage size.
     */
    ISqlDbType getByteArray(int size);
}
