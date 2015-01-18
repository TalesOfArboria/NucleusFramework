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
    private Map<String, ReflectedField> _aliasFields;
    private Map<String, Constructor<?>> _aliasConstructors;
    private Map<String, Object> _aliasEnum;

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
    public List<ReflectedField> getFields(Class<?> fieldType) {
        PreCon.notNull(fieldType, "fieldType");

        return CollectionUtils.unmodifiableList(_cached.fieldsByType(fieldType));
    }

    /**
     * Get a field from the type by name.
     *
     * <p>If an alias is defined, the alias can also be used.</p>
     *
     * @param name  The field name.
     */
    public ReflectedField getField(String name) {
        PreCon.notNullOrEmpty(name, "name");

        if (_aliasFields != null) {
            ReflectedField field = _aliasFields.get(name);
            if (field != null)
                return field;
        }

        ReflectedField field = _cached.fieldByName(name);
        if (field == null) {
            throw new RuntimeException("Field " + name + " not found in type " + getHandle().getName());
        }

        return field;
    }

    /**
     * Get a static field from the type by name.
     *
     * <p>If an alias is defined, the alias can also be used.</p>
     *
     * @param name  The field name.
     */
    public ReflectedField getStaticField(String name) {
        PreCon.notNullOrEmpty(name, "name");

        if (_aliasFields != null) {
            ReflectedField field = _aliasFields.get(name);
            if (field != null)
                return field;
        }

        ReflectedField field = _cached.staticFieldByName(name);
        if (field == null) {
            throw new RuntimeException("Field " + name + " not found in type " + getHandle().getName());
        }

        return field;
    }

    /**
     * Get an enum constant from the type.
     *
     * @param constantName  The enum constant name.
     *
     * @return  The enum constant.
     */
    public Object getEnum(String constantName) {
        PreCon.notNullOrEmpty(constantName, "constantName");

        if (_aliasEnum != null) {
            Object constant = _aliasEnum.get(constantName);
            if (constant != null)
                return constant;
        }

        return getEnumConstant(constantName);
    }

    /**
     * Get a static field value.
     *
     * @param fieldName  The name of the field.
     */
    public Object get(String fieldName) {
        ReflectedField field = getStaticField(fieldName);
        return field.get(null);
    }

    /**
     * Set a static field value.
     *
     * @param fieldName  The name of the field.
     * @param value      The value to set.
     */
    public void set(String fieldName, Object value) {
        ReflectedField field = getStaticField(fieldName);
        field.set(null, value);
    }

    /**
     * Create a new instance of the encapsulated class using
     * a pre-registered constructor alias.
     *
     * @param arguments  The constructor arguments.
     *
     * @return  The new instance.
     */
    public Object construct(String alias, Object... arguments) {
        PreCon.notNullOrEmpty(alias, "alias");
        PreCon.notNull(arguments, "arguments");

        if (_aliasConstructors == null)
            throw new RuntimeException("No constructor aliases registered.");

        Constructor<?> constructor = _aliasConstructors.get(alias);
        if (constructor == null)
            throw new RuntimeException("Constructor alias not found : " + alias);

        try {
            return constructor.newInstance(arguments);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to instantiate constructor.");
        }
    }

    /**
     * Create a new instance of the encapsulated class using
     * a pre-registered constructor alias and wrap in
     * {@code ReflectedInstance}.
     *
     * @param arguments  The constructor arguments.
     *
     * @return  The new instance.
     */
    public ReflectedInstance constructReflect(String alias, Object... arguments) {
        PreCon.notNullOrEmpty(alias, "alias");
        PreCon.notNull(arguments, "arguments");

        Object instance = construct(alias, arguments);
        assert instance != null;

        return new ReflectedInstance(this, instance);
    }

    /**
     * Create a new instance of the encapsulated class.
     * Searches for the correct constructor based on
     * provided arguments.
     *
     * @param arguments  The constructor arguments.
     *
     * @return  The new instance.
     */
    public Object newInstance(Object... arguments) {
        PreCon.notNull(arguments, "arguments");

        Collection<Constructor<?>> constructors = _cached.constructorsByCount(arguments.length);

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
     * Searches for the correct constructor based on
     * provided arguments.
     *
     * @param arguments  The constructor arguments.
     *
     * @return  The new instance.
     */
    public ReflectedInstance newReflectedInstance(Object... arguments) {
        PreCon.notNull(arguments, "arguments");

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
        PreCon.notNull(instance, "instance");

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
        PreCon.notNull(instance, "instance");

        return new ReflectedArray(this, instance);
    }

    /**
     * Register a constructor signature under an alias name. Improves performance
     * by allowing the specific constructor to be retrieved by the alias name.
     * Use {@code #construct} or {@code @constructReflect} to create new instances
     * using the alias name.
     *
     * @param alias      The constructor alias.
     * @param signature  The constructor signature.
     *
     * @return  Self for chaining.
     */
    public ReflectedType constructorAlias(String alias, Class<?>... signature) {
        PreCon.notNullOrEmpty(alias, "alias");
        PreCon.notNull(signature, "signature");

        if (_aliasConstructors == null)
            _aliasConstructors = new HashMap<>(10);

        if (_aliasConstructors.containsKey(alias))
            throw new RuntimeException("Constructor alias already registered: " + alias);

        Constructor<?> constructor;
        try {
            constructor = getHandle().getDeclaredConstructor(signature);
            constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException("Constructor not found.");
        }

        constructor.setAccessible(true);
        _aliasConstructors.put(alias, constructor);

        return this;
    }

    /**
     * Register a field alias name.
     *
     * @param fieldAlias  The field alias.
     * @param fieldName   The field name.
     *
     * @return  Self for chaining.
     */
    public ReflectedType fieldAlias(String fieldAlias, String fieldName) {
        PreCon.notNullOrEmpty(fieldAlias, "fieldAlias");
        PreCon.notNullOrEmpty(fieldName, "fieldName");

        if (_aliasFields == null)
            _aliasFields = new HashMap<>(10);

        if (_aliasFields.containsKey(fieldAlias))
            throw new RuntimeException("Field alias already registered: " + fieldAlias);

        ReflectedField field = getField(fieldName);
        if (field == null)
            throw new RuntimeException("Field " + fieldName + " not found in type " + getHandle().getName());

        _aliasFields.put(fieldAlias, field);

        return this;
    }

    /**
     * Register an enum constant for faster lookup under an
     * alias name.
     *
     * @param constantAlias  The alias name.
     * @param constantName   The enum constant name.
     *
     * @return  Self for chaining.
     */
    public ReflectedType enumAlias(String constantAlias, String constantName) {
        PreCon.notNullOrEmpty(constantAlias, "constantAlias");
        PreCon.notNullOrEmpty(constantName, "constantName");

        if (_aliasEnum == null)
            _aliasEnum = new HashMap<>(5);

        if (_aliasEnum.containsKey(constantAlias))
            throw new RuntimeException("Enum alias already registered: " + constantAlias);

        _aliasEnum.put(constantAlias, getEnumConstant(constantName));

        return this;
    }

    /**
     * Register an enum constant for faster lookup.
     *
     * @param constantName  The constant name.
     *
     * @return  Self for chaining.
     */
    public ReflectedType enumConst(String constantName) {
        PreCon.notNullOrEmpty(constantName, "constantName");

        if (_aliasEnum == null)
            _aliasEnum = new HashMap<>(5);

        if (_aliasEnum.containsKey(constantName))
            throw new RuntimeException("Enum constant already registered: " + constantName);

        _aliasEnum.put(constantName, getEnumConstant(constantName));

        return this;
    }

    /**
     * Registers a method name to a specific signature. Does not allow
     * for overloaded methods. Improves code readability and method lookup performance.
     *
     * @param methodName  The method name.
     * @param signature   The argument types of the method.
     *
     * @return Self for chaining.
     */
    public ReflectedType method(String methodName, Class<?>... signature) {
        PreCon.notNullOrEmpty(methodName, "methodName");
        PreCon.notNull(signature, "argTypes");

        if (_aliasMethods == null) {
            _aliasMethods = new HashMap<>(10);
        }

        if (_aliasMethods.containsKey(methodName))
            throw new RuntimeException("Method already registered: " + methodName);

        Method method;

        try {
            method = getHandle().getDeclaredMethod(methodName, signature);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException("Method not found.");
        }

        method.setAccessible(true);

        _aliasMethods.put(methodName, method);
        return this;
    }

    /**
     * Register a method alias name to a specific signature to aid
     * in code readability and method lookup performance.
     *
     * @param alias       The alias for the method. Can match an actual method name
     *                    but will prevent use of overloads.
     * @param methodName  The name of the method.
     * @param signature   The argument types of the method.
     *
     * @return Self for chaining.
     */
    public ReflectedType methodAlias(String alias, String methodName, Class<?>... signature) {
        PreCon.notNullOrEmpty(alias, "alias");
        PreCon.notNullOrEmpty(methodName, "methodName");
        PreCon.notNull(signature, "argTypes");

        if (_aliasMethods == null) {
            _aliasMethods = new HashMap<>(10);
        }

        if (_aliasMethods.containsKey(alias))
            throw new RuntimeException("Alias already registered: " + alias);

        Method method;

        try {
            method = getHandle().getDeclaredMethod(methodName, signature);
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
    public <V> V invokeStatic(String staticMethodName, Object... arguments) {
        PreCon.notNull(staticMethodName, "staticMethodName");
        PreCon.notNull(arguments, "arguments");

        return invoke(null, staticMethodName, arguments);
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
    public <V> V invoke(@Nullable Object instance, String methodName, Object... arguments) {
        PreCon.notNullOrEmpty(methodName, "methodName");
        PreCon.notNull(arguments, "arguments");

        Method method = _aliasMethods != null ? _aliasMethods.get(methodName) : null;

        if (method == null) {

            Collection<Method> methods = instance != null
                    ? _cached.methodsByName(methodName)
                    : _cached.staticMethodsByName(methodName);

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

    private Object getEnumConstant(String constantName) {

        if (!getHandle().isEnum())
            throw new RuntimeException("Type '" + getHandle().getName() + "' is not an enum.");

        Object[] constants = getHandle().getEnumConstants();

        Method nameMethod;

        try {
            nameMethod = Enum.class.getMethod("name");
        } catch (NoSuchMethodException e) {
            throw new AssertionError();
        }

        nameMethod.setAccessible(true);

        for (Object constant : constants) {

            String name;
            try {
                name = (String) nameMethod.invoke(constant);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new AssertionError();
            }

            if (constantName.equals(name)) {
                return constant;
            }
        }

        throw new RuntimeException("Failed to find enum constant named " +
                constantName + " in type " + getHandle().getName());
    }

}
