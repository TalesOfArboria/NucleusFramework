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

package com.jcwhatever.bukkit.generic.reflection;

import com.jcwhatever.bukkit.generic.collections.ArrayListMap;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.ReflectUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

/**
 * A wrapper for a java class that provides reflection helpers.
 *
 * @param <T>  The wrapped class type.
 */
public class ReflectedType<T> {

    static Map<Class<?>, ReflectedType> _typeCache = new WeakHashMap<>(100);
    static Map<String, ReflectedType> _typeNameCache = new WeakHashMap<>(100);

    private final Class<T> _clazz;

    private ArrayListMap<Integer, Constructor<T>> _constructors;
    private ArrayListMap<Class<?>, Field> _fields = new ArrayListMap<>(10);
    private ArrayListMap<String, Method> _methods = new ArrayListMap<>(10);
    private ArrayListMap<String, Method> _staticMethods = new ArrayListMap<>(10);

    /**
     * Private Constructor.
     */
    ReflectedType(Class<T> clazz) {
        PreCon.notNull(clazz);

        _clazz = clazz;

        loadFields();
        loadMethods();
        loadConstructors();
    }

    /**
     * Get the encapsulated class.
     */
    public Class<T> getHandle() {
        return _clazz;
    }

    /**
     * Get fields of the specified type.
     *
     * <p>Only stores by the field type. Super types
     * are not stored.</p>
     *
     * <p>Specifying the Object type returns all fields.</p>
     *
     * @param fieldType  The field type.
     */
    public List<Field> getFields(Class<?> fieldType) {
        return _fields.getAll(fieldType);
    }

    /**
     * Create a new instance of the encapsulated class.
     *
     * @param arguments  The constructor arguments.
     *
     * @return  The new instance.
     */
    public Object newInstance(Object... arguments) {

        List<Constructor<T>> constructors = _constructors.getAll(arguments.length);

        Constructor<T> constructor;

        if (constructors.size() == 0) {
            return null;
        }
        else {
            constructor = constructors.size() == 1
                    ? constructors.get(0)
                    : ReflectUtils.findConstructorByArgs(constructors, arguments);
        }

        if (constructor == null)
            return null;

        Object instance;

        try {
            instance = constructor.newInstance(arguments);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

        return instance;
    }

    /**
     * Create a new array whose component type is
     * the encapsulated class.
     *
     * @param size  The size of the array.
     *
     * @return  {@code ReflectedArray} instance.
     */
    public ReflectedArray<T> newArray(int size) {
        PreCon.positiveNumber(size);

        Object array = Array.newInstance(_clazz, size);

        return new ReflectedArray<>(this, array);
    }

    /**
     * Create a new {@code ReflectedInstance} to encapsulate
     * an object instance that is known to be of the same
     * type as the encapsulated class.
     *
     * @param instance  The instance to wrap.
     */
    public ReflectedInstance<T> reflect(Object instance) {
        return new ReflectedInstance<>(this, instance);
    }

    /**
     * Create a new {@code ReflectedArray} to encapsulate
     * an object instance that is known to be of the same
     * type as the encapsulated class.
     *
     * @param instance  The instance to wrap.
     */
    public ReflectedArray<T> reflectArray(Object instance) {
        return new ReflectedArray<>(this, instance);
    }

    /**
     * Call a static method on the encapsulated class.
     *
     * @param staticMethodName  The name of the static method.
     * @param arguments         The method arguments.
     *
     * @param <V>  The return type.
     *
     * @return  Null if the method returns null or void.
     */
    @Nullable
    public <V> V call(String staticMethodName, Object...arguments) {
        return call(null, staticMethodName, arguments);
    }

    /**
     * Call a method on an instance of the encapsulated class.
     *
     * @param instance    The instance.
     * @param methodName  The name of the method.
     * @param arguments   The method arguments.
     *
     * @param <V>  The return type.
     *
     * @return  Null if the method returns null or void.
     */
    @Nullable
    public <V> V call(@Nullable Object instance, String methodName, Object...arguments) {
        PreCon.notNullOrEmpty(methodName);
        PreCon.notNull(arguments);

        List<Method> methods = instance != null
                ? _methods.getAll(methodName)
                : _staticMethods.getAll(methodName);

        Method method;

        // get method definition
        if (methods.size() == 1) {
            method = methods.get(0);
        }
        else {
            method = ReflectUtils.findMethodByArgs(methods, methodName, arguments);
            if (method == null)
                throw new RuntimeException("Method '" + methodName +
                        "' not found in type " + _clazz.getCanonicalName());
        }

        Object result;

        try {
            result = method.invoke(instance, arguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }

        @SuppressWarnings("unchecked")
        V castedResult = (V)result;

        return castedResult;
    }

    // load all constructors from the encapsulated class.
    private void loadConstructors() {
        Constructor<?>[] constructors = _clazz.getDeclaredConstructors();

        _constructors = new ArrayListMap<>(constructors.length);

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
}
