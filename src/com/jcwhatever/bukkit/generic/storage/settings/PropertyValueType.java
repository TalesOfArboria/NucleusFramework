/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.storage.settings;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Valid {@code ISettingsManager} property value types.
 */
public final class PropertyValueType<T> {

    public static final PropertyValueType<Boolean> BOOLEAN =
            new PropertyValueType<>(ValueType.BOOLEAN, "boolean");

    public static final PropertyValueType<Integer> INTEGER =
            new PropertyValueType<>(ValueType.INTEGER, "number");

    public static final PropertyValueType<Long> LONG =
            new PropertyValueType<>(ValueType.LONG, "number");

    public static final PropertyValueType<Double> DOUBLE =
            new PropertyValueType<>(ValueType.DOUBLE, "floating point number");

    public static final PropertyValueType<String> STRING =
            new PropertyValueType<>(ValueType.STRING, "text");

    public static final PropertyValueType<ItemStack[]> ITEM_STACK_ARRAY =
            new PropertyValueType<>(ValueType.ITEM_STACK_ARRAY, "item stacks");

    public static final PropertyValueType<Location> LOCATION =
            new PropertyValueType<>(ValueType.LOCATION, "location");

    public static final PropertyValueType<UUID> UNIQUE_ID =
            new PropertyValueType<>(ValueType.UNIQUE_ID, "unique id");

    public static final PropertyValueType<Enum> ENUM =
            new PropertyValueType<>(ValueType.ENUM, "enum constant");

    public enum ValueType {
        BOOLEAN (Boolean.class),
        INTEGER (Integer.class),
        LONG (Long.class),
        DOUBLE (Double.class),
        STRING (String.class),
        ITEM_STACK_ARRAY (ItemStack[].class),
        LOCATION (Location.class),
        UNIQUE_ID (UUID.class),
        ENUM (Enum.class);

        private final Class<?> _clazz;

        ValueType(Class<?> clazz) {
            _clazz = clazz;
        }

        public Class<?> getType() {
            return _clazz;
        }
    }

    private final ValueType _type;
    private final String _display;

    PropertyValueType(ValueType type, String display) {
        _type = type;
        _display = display;
    }

    public String getDisplay() {
        return _display;
    }

    public ValueType getType() {
        return _type;
    }

    public Class<?> getTypeClass() {
        return _type.getType();
    }

    public boolean isAssignable(Object object) {
        return object instanceof Class
                ? _type._clazz.isAssignableFrom((Class<?>) object)
                : _type._clazz.isAssignableFrom(object.getClass());
    }
}
