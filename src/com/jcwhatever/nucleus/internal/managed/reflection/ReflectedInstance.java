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

package com.jcwhatever.nucleus.internal.managed.reflection;

import com.jcwhatever.nucleus.managed.reflection.IReflectedInstance;

import java.util.HashMap;
import java.util.Map;

/**
 * Internal implementation of {@link IReflectedInstance}.
 */
class ReflectedInstance extends Instance implements IReflectedInstance {

    private Map<Class<?>, ReflectedInstanceFields> _fields;

    /**
     * Constructor.
     *
     * @param type      The reflected type of the instance.
     * @param instance  The instance to encapsulate.
     */
    ReflectedInstance(ReflectedType type, Object instance) {
        super(type, instance);
    }

    @Override
    public Object get(String fieldName) {
        ReflectedField field = getReflectedType().getField(fieldName);
        return field.get(getHandle());
    }

    @Override
    public void set(String fieldName, Object value) {
        ReflectedField field = getReflectedType().getField(fieldName);
        field.set(getHandle(), value);
    }

    @Override
    public ReflectedInstanceFields getFields() {
        return getFields(Object.class);
    }

    @Override
    public ReflectedInstanceFields getFields(Class<?> fieldType) {

        if (_fields == null) {
            _fields = new HashMap<>(10);
        }

        ReflectedInstanceFields fields = _fields.get(fieldType);
        if (fields == null) {

            fields = new ReflectedInstanceFields(
                    this, getReflectedType().getCachedType().fieldsByType(fieldType));

            _fields.put(fieldType, fields);
        }

        return fields;
    }
}