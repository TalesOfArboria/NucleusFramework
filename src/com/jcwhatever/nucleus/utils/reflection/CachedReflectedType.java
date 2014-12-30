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

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Stores globally available data about a class type.
 */
public class CachedReflectedType {

    private final Class<?> _clazz;

    Multimap<Integer, Constructor<?>> _constructors;
    Multimap<Class<?>, Field> _fields = MultimapBuilder.hashKeys(10).arrayListValues().build();
    Multimap<String, Method> _methods = MultimapBuilder.hashKeys(10).arrayListValues().build();
    Multimap<String, Method> _staticMethods = MultimapBuilder.hashKeys(10).arrayListValues().build();

    /**
     * Constructor.
     *
     * @param clazz
     */
    CachedReflectedType(Class<?> clazz) {
        _clazz = clazz;

        loadFields();
        loadMethods();
        loadConstructors();
    }


    // load all constructors from the encapsulated class.
    private void loadConstructors() {
        Constructor<?>[] constructors = _clazz.getDeclaredConstructors();

        _constructors = MultimapBuilder.hashKeys(constructors.length).arrayListValues().build();

        for (Constructor c : constructors) {
            c.setAccessible(true);

            //noinspection unchecked
            _constructors.put(c.getParameterTypes().length, c);
        }
    }

    // load all fields from the encapsulated class.
    private void loadFields() {

        Field[] fields = _clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            _fields.put(field.getType(), field);
            _fields.put(Object.class, field);
        }
    }

    // load all methods from the encapsulated class.
    private void loadMethods() {
        Method[] methods = _clazz.getDeclaredMethods();

        for (Method method : methods) {
            method.setAccessible(true);

            if (Modifier.isStatic(method.getModifiers())) {
                _staticMethods.put(method.getName(), method);
            }
            else {
                _methods.put(method.getName(), method);
            }
        }
    }

    /**
     * Get the encapsulated class.
     */
    public Class<?> getHandle() {
        return _clazz;
    }
}
