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

import com.jcwhatever.nucleus.managed.reflection.IReflectedField;
import com.jcwhatever.nucleus.managed.reflection.IReflectedType;
import com.jcwhatever.nucleus.managed.reflection.IReflectedTypeFields;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Internal implementation of {@link IReflectedTypeFields}.
 */
class ReflectedTypeFields implements IReflectedTypeFields {

    private final List<ReflectedField> _fields;
    private final ReflectedType _type;

    private Map<String, ReflectedField> _nameMap;

    /**
     * Constructor.
     *
     * @param type    The type the fields are from.
     * @param fields  The fields in the instance of the specified type.
     */
    ReflectedTypeFields(ReflectedType type, List<ReflectedField> fields) {
        PreCon.notNull(fields);

        _type = type;
        _fields = fields;
    }

    @Override
    public IReflectedType getReflectedType() {
        return _type;
    }

    @Override
    public int size() {
        return _fields.size();
    }

    @Override
    public String name(int index) {
        PreCon.positiveNumber(index);

        return _fields.get(index).getName();
    }

    @Override
    public ReflectedField getField(int index) {
        return _fields.get(index);
    }

    // initialize and get the name map
    protected Map<String, ReflectedField> getNameMap() {
        if (_nameMap == null) {
            _nameMap = new HashMap<>(_fields.size());
            for (ReflectedField field : _fields) {
                _nameMap.put(field.getName(), field);
            }
        }
        return _nameMap;
    }

    @Override
    public Iterator<IReflectedField> iterator() {
        return new Iterator<IReflectedField>() {

            int index = -1;

            @Override
            public boolean hasNext() {
                return _fields.size() > index + 1;
            }

            @Override
            public IReflectedField next() {
                index++;
                return _fields.get(index);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
