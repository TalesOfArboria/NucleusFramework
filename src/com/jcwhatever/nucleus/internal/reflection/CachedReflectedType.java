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

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.jcwhatever.nucleus.utils.CollectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Stores globally available data about a class type.
 */
class CachedReflectedType {

    private final Class<?> _clazz;

    private final Multimap<Integer, Constructor<?>> _constructors =
            MultimapBuilder.hashKeys(3).arrayListValues().build();

    private final Multimap<Class<?>, ReflectedField> _fieldsByType =
            MultimapBuilder.hashKeys(10).arrayListValues().build();

    private final Map<String, ReflectedField> _fieldsByName = new HashMap<>(10);

    private final Multimap<String, Method> _methods =
            MultimapBuilder.hashKeys(10).arrayListValues().build();

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

    /**
     * Get all constructors with the specified number of parameters.
     *
     * @param count  The parameter count.
     */
    public Collection<Constructor<?>> constructorsByCount(int count) {
        synchronized (_constructors) {
            return CollectionUtils.unmodifiableList(_constructors.get(count));
        }
    }

    /**
     * Get a field by name.
     *
     * @param name  The name of the field.
     *
     * @return the {@link ReflectedField} or null if not found.
     */
    @Nullable
    public ReflectedField fieldByName(String name) {
        synchronized (_fieldsByName) {
            return _fieldsByName.get(name);
        }
    }

    /**
     * Get all fields of a specified value type.
     *
     * @param type  The value type class.
     */
    public List<ReflectedField> fieldsByType(Class<?> type) {
        synchronized (_fieldsByType) {
            return CollectionUtils.unmodifiableList(_fieldsByType.get(type));
        }
    }

    /**
     * Get all methods with the specified name.
     *
     * @param name  The method name.
     */
    public Collection<Method> methodsByName(String name) {
        synchronized (_methods) {
            return CollectionUtils.unmodifiableList(_methods.get(name));
        }
    }

    /**
     * Get the encapsulated class.
     */
    public Class<?> getHandle() {
        return _clazz;
    }

    // load all constructors from the encapsulated class.
    private void loadConstructors() {
        Constructor<?>[] constructors = _clazz.getDeclaredConstructors();

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

            ReflectedField reflectedField = new ReflectedField(this, field);

            _fieldsByType.put(field.getType(), reflectedField);
            _fieldsByType.put(Object.class, reflectedField);

            _fieldsByName.put(field.getName(), reflectedField);
        }
    }

    // load all methods from the encapsulated class.
    private void loadMethods() {
        Method[] methods = _clazz.getDeclaredMethods();

        for (Method method : methods) {
            method.setAccessible(true);

            _methods.put(method.getName(), method);
        }
    }
}
