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

import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * A wrapper for a java class that provides reflection helpers.
 *
 */
public class ReflectedType {

    private final CachedReflectedType _cached;
    private Map<String, Method> _aliasMethods;

    /**
     * Constructor.
     */
    ReflectedType(CachedReflectedType cached) {
        PreCon.notNull(cached);

        _cached = cached;
    }

    /**
     * Get the encapsulated class.
     */
    public Class<?> getHandle() {
        return _cached.getHandle();
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
        return CollectionUtils.unmodifiableList(_cached._fields.get(fieldType));
    }

    /**
     * Create a new instance of the encapsulated class.
     *
     * @param arguments  The constructor arguments.
     *
     * @return  The new instance.
     */
    public Object newInstance(Object... arguments) {

        Collection<Constructor<?>> constructors = _cached._constructors.get(arguments.length);

        Constructor<?> constructor;

        if (constructors.size() == 0) {
            throw new RuntimeException("No constructors to instantiate.");
        }
        else {
            constructor = constructors.size() == 1
                    ? constructors.iterator().next()
                    : ReflectionUtils.findConstructorByArgs(constructors, arguments);
        }

        if (constructor == null)
            throw new RuntimeException("Failed to find a matching constructor.");

        Object instance;

        try {
            instance = constructor.newInstance(arguments);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to instantiate constructor.");
        }

        return instance;
    }

    /**
     * Create a new instance of the encapsulated class.
     *
     * @param arguments  The constructor arguments.
     *
     * @return  The new instance.
     */
    public ReflectedInstance newReflectedInstance(Object... arguments) {
        Object instance = newInstance(arguments);
        return new ReflectedInstance(this, instance);
    }

    /**
     * Create a new array whose component type is
     * the encapsulated class.
     *
     * @param size  The size of the array.
     *
     * @return  {@code ReflectedArray} instance.
     */
    public ReflectedArray newArray(int size) {
        PreCon.positiveNumber(size);

        Object array = Array.newInstance(_cached.getHandle(), size);

        return new ReflectedArray(this, array);
    }

    /**
     * Create a new array whose component type is
     * the encapsulated class.
     *
     * @param sizes  The size of each array dimension.
     *
     * @return  {@code ReflectedArray} instance.
     */
    public ReflectedArray newArray(int... sizes) {
        PreCon.greaterThanZero(sizes.length, "At least one array dimension must be specified.");

        Object array = Array.newInstance(_cached.getHandle(), sizes);

        return new ReflectedArray(this, array);
    }

    /**
     * Create a new {@code ReflectedInstance} to encapsulate
     * an object instance that is known to be of the same
     * type as the encapsulated class.
     *
     * @param instance  The instance to wrap.
     */
    public ReflectedInstance reflect(Object instance) {
        return new ReflectedInstance(this, instance);
    }

    /**
     * Create a new {@code ReflectedArray} to encapsulate
     * an array instance that is known to be of the same
     * type as the encapsulated class.
     *
     * @param instance  The instance to wrap.
     */
    public ReflectedArray reflectArray(Object instance) {
        return new ReflectedArray(this, instance);
    }

    /**
     * Create a method alias name.
     *
     * @param alias       The alias for the method. Cannot match any method name in the type.
     * @param methodName  The name of the method.
     * @param argTypes    The argument types of the method.
     *
     * @return Self for chaining.
     */
    public ReflectedType methodAlias(String alias, String methodName, Class<?>... argTypes) {

        if (_aliasMethods == null) {
            _aliasMethods = new HashMap<>(10);
        }

        if (_aliasMethods.containsKey(alias))
            throw new RuntimeException("Alias already registered: " + alias);

        Method method;

        try {
            method = getHandle().getDeclaredMethod(methodName, argTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException("Method not found.");
        }

        method.setAccessible(true);

        _aliasMethods.put(alias, method);

        return this;
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

        Method method = _aliasMethods.get(methodName);

        if (method == null) {

            Collection<Method> methods = instance != null
                    ? _cached._methods.get(methodName)
                    : _cached._staticMethods.get(methodName);

            // get method definition
            if (methods.size() == 1) {
                method = methods.iterator().next();
            } else {
                method = ReflectionUtils.findMethodByArgs(methods, methodName, arguments);
                if (method == null)
                    throw new RuntimeException("Method '" + methodName +
                            "' not found in type " + _cached.getHandle().getCanonicalName());
            }
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
}
