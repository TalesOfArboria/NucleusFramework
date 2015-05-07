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

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Sql data types.
 */
public class SqlDbType {

    private SqlDbType() {}

    public static final ISqlDbType BOOLEAN = new DbTypeWrapper(boolean.class);

    public static final ISqlDbType BYTE = new DbTypeWrapper(byte.class);

    public static final ISqlDbType SHORT = new DbTypeWrapper(short.class);

    public static final ISqlDbType INTEGER = new DbTypeWrapper(int.class);

    public static final ISqlDbType LONG = new DbTypeWrapper(long.class);

    public static final ISqlDbType FLOAT = new DbTypeWrapper(float.class);

    public static final ISqlDbType DOUBLE = new DbTypeWrapper(double.class);

    public static final ISqlDbType BIG_DECIMAL = new DbTypeWrapper(BigDecimal.class);

    public static final ISqlDbType LOCATION = new DbTypeWrapper(Location.class);

    public static final ISqlDbType VECTOR = new DbTypeWrapper(Vector.class);

    public static final ISqlDbType UNIQUE_ID = new DbTypeWrapper(UUID.class);

    public static final ISqlDbType ITEM_STACKS = new DbTypeWrapper(ItemStack[].class);

    /**
     * Get a data type by value type.
     *
     * @param typeClazz  The java class data type equivalent. For primitives, use
     *                   the primitive class, not the wrapper.
     *
     * @return  The database data type or null if an equivalent could not
     * be found.
     */
    @Nullable
    public static ISqlDbType getDataType(Class<?> typeClazz) {
        return Sql.provider().getDataType(typeClazz);
    }

    /**
     * Get a data type by name.
     *
     * <p>Does not return data types that require extra parameters such
     * as size.</p>
     *
     * @param typeName  The name of the data type.
     *
     * @return  The data type or null if not found.
     */
    @Nullable
    public static ISqlDbType getDataType(String typeName) {
        return Sql.provider().getDataType(typeName);
    }

    /**
     * Get all available data types for the database.
     *
     * <p>Does not return data types that require extra parameters such
     * as size.</p>
     */
    public static Collection<ISqlDbType> getDataTypes() {
        return Sql.provider().getDataTypes();
    }

    /**
     * Get a data type for storing a string of a specified fixed size.
     *
     * @param size  The data storage size.
     */
    public static ISqlDbType getFixedString(int size) {
        return Sql.provider().getFixedString(size);
    }

    /**
     * Get a data type for storing a variable length string with the
     * specified max size.
     *
     * @param size  The max data storage size.
     */
    public static ISqlDbType getString(int size) {
        return Sql.provider().getString(size);
    }

    /**
     * Get a data type for storing a fixed size byte array with the
     * specified size.
     *
     * @param size  The data storage size.
     */
    public static ISqlDbType getFixedByteArray(int size) {
        return Sql.provider().getFixedByteArray(size);
    }

    /**
     * Get a data type for storing a variable length byte array with
     * the specified size.
     *
     * @param size  The data storage size.
     */
    public static ISqlDbType getByteArray(int size) {
        return Sql.provider().getByteArray(size);
    }

    private static class DbTypeWrapper implements ISqlDbType {

        private final Class<?> _dataClass;
        private ISqlDbType _type;

        DbTypeWrapper(Class<?> dataClass) {
            _dataClass = dataClass;
        }

        @Override
        public int size() {
            return type().size();
        }

        @Override
        public Class<?> getDataClass() {
            return _dataClass;
        }

        @Override
        public boolean isCompound() {
            return type().isCompound();
        }

        @Override
        public String getName() {
            return type().getName();
        }

        private ISqlDbType type() {
            if (_type == null) {
                _type = Sql.provider().getDataType(_dataClass);
                if (_type == null) {
                    throw new UnsupportedOperationException("Sql provider does not support a " +
                            "data type for class: " + _dataClass.getName());
                }
            }

            return _type;
        }
    }
}
