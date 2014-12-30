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

package com.jcwhatever.nucleus.utils.reflection;

import com.jcwhatever.nucleus.utils.PreCon;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores fields for a reflected instance.
 */
public class Fields {

    private final List<Field> _fields;
    private final Object _instance;

    private Map<String, Field> _nameMap;

    /**
     * Constructor.
     *
     * @param fields     The fields in the instance of the specified type.
     * @param instance   The instance the fields are from.
     */
    public Fields(List<Field> fields, Object instance) {
        PreCon.notNull(fields);
        PreCon.notNull(instance);

        _instance = instance;
        _fields = fields;
    }

    /**
     * Get the total number of stored fields.
     */
    public int size() {
        return _fields.size();
    }

    /**
     * Get the name of a field by index order.
     *
     * @param index  The field index position.
     */
    public String name(int index) {
        PreCon.positiveNumber(index);

        return _fields.get(index).getName();
    }

    /**
     * Get the value of a field by index position.
     *
     * @param index  The fields index position.
     *
     * @param <T>  The return type.
     *
     * @return  Null if failed to access the field.
     */
    public <T> T get(int index) {
        return get(_fields.get(index));
    }

    /**
     * Get the value of a field by name.
     *
     * @param fieldName  The name of the field.
     *
     * @param <T>  The return type.
     *
     * @return Null if value is null or failed to access the field.
     */
    public <T> T get(String fieldName) {
        Map<String, Field> nameMap = getNameMap();

        Field field = nameMap.get(fieldName);
        if (field == null)
            throw new RuntimeException("Field named " + fieldName + " not found.");

        return get(field);
    }

    /**
     * Set the value of a field at the specified index position.
     *
     * @param index  The fields index position.
     * @param value  The value to set.
     */
    public void set(int index, Object value) {
        try {
            _fields.get(index).set(_instance, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the value of a field by name.
     *
     * @param fieldName  The name of the field.
     * @param value      The value to set.
     */
    public void set(String fieldName, Object value) {
        Map<String, Field> nameMap = getNameMap();

        Field field = nameMap.get(fieldName);
        if (field == null)
            throw new RuntimeException("Field named " + fieldName + " not found.");

        try {
            field.set(_instance, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the encapsulated field by index position.
     *
     * @param index  The fields index position.
     */
    public Field getHandle(int index) {
        return _fields.get(index);
    }

    // get the value of a field
    private <T> T get(Field field) {
        try {
            //noinspection unchecked
            return (T)field.get(_instance);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    // initialize and get the name map
    private Map<String, Field> getNameMap() {
        if (_nameMap == null) {
            _nameMap = new HashMap<>(_fields.size());
            for (Field field : _fields) {
                _nameMap.put(field.getName(), field);
            }
        }
        return _nameMap;
    }
}
