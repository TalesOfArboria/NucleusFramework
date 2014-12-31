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

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates an object instance and provides reflection
 * utilities.
 */
public class ReflectedInstance extends Instance {

    private Map<Class<?>, Fields> _fields;

    /**
     * Constructor.
     *
     * @param type      The reflected type of the instance.
     * @param instance  The instance to encapsulate.
     */
    ReflectedInstance(ReflectedType type, Object instance) {
        super(type, instance);
    }

    /**
     * Get an instance field value.
     *
     * @param fieldName  The name of the field.
     */
    public Object get(String fieldName) {
        ReflectedField field = getReflectedType().getField(fieldName);
        return field.get(getHandle());
    }

    /**
     * Set an instance field value.
     *
     * @param fieldName  The name of the field.
     * @param value      The value to set.
     */
    public void set(String fieldName, Object value) {
        ReflectedField field = getReflectedType().getField(fieldName);
        field.set(getHandle(), value);
    }

    /**
     * Get fields from the instance of the specified class
     * type.
     *
     * @param fieldType  The field class type.
     */
    public Fields getFields(Class<?> fieldType) {

        if (_fields == null) {
            _fields = new HashMap<>(10);
        }

        Fields fields = _fields.get(fieldType);
        if (fields == null) {
            fields = new Fields(getReflectedType().getFields(fieldType), getHandle());
            _fields.put(fieldType, fields);
        }

        return fields;
    }

    /**
     * Get all fields from the instance.
     */
    public Fields getFields() {
        return getFields(Object.class);
    }
}