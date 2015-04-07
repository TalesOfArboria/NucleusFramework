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

package com.jcwhatever.nucleus.internal.reflection;

import com.jcwhatever.nucleus.managed.reflection.IReflectedInstanceFields;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.List;
import java.util.Map;

/**
 * Internal implementation of {@link IReflectedInstanceFields}.
 */
public class ReflectedInstanceFields extends ReflectedTypeFields implements IReflectedInstanceFields {

    private final List<ReflectedField> _fields;
    private final ReflectedInstance _instance;

    /**
     * Constructor.
     *
     * @param fields     The fields in the instance of the specified type.
     * @param instance   The instance the fields are from.
     */
    ReflectedInstanceFields(ReflectedInstance instance, List<ReflectedField> fields) {
        super(instance.getReflectedType(), fields);

        PreCon.notNull(instance);
        PreCon.notNull(fields);

        _instance = instance;
        _fields = fields;
    }

    @Override
    public ReflectedInstance getReflectedInstance() {
        return _instance;
    }

    @Override
    public <T> T get(int index) {
        return get(_fields.get(index));
    }

    @Override
    public <T> T get(String fieldName) {
        Map<String, ReflectedField> nameMap = getNameMap();

        ReflectedField field = nameMap.get(fieldName);
        if (field == null)
            throw new RuntimeException("Field named " + fieldName + " not found.");

        return get(field);
    }

    @Override
    public void set(int index, Object value) {
        _fields.get(index).set(_instance.getHandle(), value);
    }

    @Override
    public void set(String fieldName, Object value) {
        Map<String, ReflectedField> nameMap = getNameMap();

        ReflectedField field = nameMap.get(fieldName);
        if (field == null)
            throw new RuntimeException("Field named " + fieldName + " not found.");

        field.set(_instance.getHandle(), value);
    }

    // get the value of a field
    private <T> T get(ReflectedField field) {
        //noinspection unchecked
        return (T)field.get(_instance.getHandle());
    }
}
