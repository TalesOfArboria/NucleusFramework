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

import com.jcwhatever.nucleus.managed.reflection.IReflectedArray;
import com.jcwhatever.nucleus.managed.reflection.IReflectedInstance;
import com.jcwhatever.nucleus.managed.reflection.IReflectedType;
import com.jcwhatever.nucleus.managed.reflection.Reflection;
import com.jcwhatever.nucleus.utils.PreCon;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Internal implementation of {@link IReflectedType}.
 */
class ReflectedType implements IReflectedType {

    private final CachedReflectedType _cached;
    private Map<String, Method> _aliasMethods;
    private Map<String, ReflectedField> _aliasFields;
    private Map<String, Constructor<?>> _aliasConstructors;
    private Map<String, Object> _aliasEnum;

    /**
     * Constructor.
     *
     * <p>The reflected types cache.</p>
     */
    ReflectedType(CachedReflectedType cached) {
        PreCon.notNull(cached);

        _cached = cached;
    }

    /**
     * Get the cached reflected type.
     */
    public CachedReflectedType getCachedType() {
        return _cached;
    }

    @Override
    public ReflectedTypeFields getFields() {
        return getFields(Object.class);
    }

    @Override
    public ReflectedTypeFields getFields(Class<?> fieldType) {
        PreCon.notNull(fieldType, "fieldType");

        return new ReflectedTypeFields(this, _cached.fieldsByType(fieldType));
    }

    @Override
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

    @Override
    public Object getEnum(String constantName) {
        PreCon.notNullOrEmpty(constantName, "constantName");

        if (_aliasEnum != null) {
            Object constant = _aliasEnum.get(constantName);
            if (constant != null)
                return constant;
        }

        return getEnumConstant(constantName);
    }

    @Override
    public Object get(String fieldName) {
        ReflectedField field = getField(fieldName);

        if (!field.isStatic()) {
            throw new RuntimeException("Field " + fieldName + " is not static.");
        }

        return field.get(null);
    }

    @Override
    public void set(String fieldName, Object value) {
        ReflectedField field = getField(fieldName);

        if (!field.isStatic()) {
            throw new RuntimeException("Field " + fieldName + " is not static.");
        }

        field.set(null, value);
    }

    @Override
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

    @Override
    public IReflectedInstance constructReflect(String alias, Object... arguments) {
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
                    : Reflection.findConstructorByArgs(constructors, arguments);
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

    @Override
    public IReflectedArray newArray(int size) {
        PreCon.positiveNumber(size);

        Object array = Array.newInstance(_cached.getHandle(), size);

        return new ReflectedArray(this, array);
    }


    @Override
    public IReflectedArray newArray(int... sizes) {
        PreCon.greaterThanZero(sizes.length, "At least one array dimension must be specified.");

        Object array = Array.newInstance(_cached.getHandle(), sizes);

        return new ReflectedArray(this, array);
    }

    @Override
    public ReflectedInstance reflect(Object instance) {
        PreCon.notNull(instance, "instance");

        return new ReflectedInstance(this, instance);
    }

    @Override
    public ReflectedArray reflectArray(Object instance) {
        PreCon.notNull(instance, "instance");

        return new ReflectedArray(this, instance);
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public ReflectedType enumConst(String constantName) {
        PreCon.notNullOrEmpty(constantName, "constantName");

        if (_aliasEnum == null)
            _aliasEnum = new HashMap<>(5);

        if (_aliasEnum.containsKey(constantName))
            throw new RuntimeException("Enum constant already registered: " + constantName);

        _aliasEnum.put(constantName, getEnumConstant(constantName));

        return this;
    }

    @Override
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

    @Override
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

    @Override
    @Nullable
    public <V> V invokeStatic(String staticMethodName, Object... arguments) {
        PreCon.notNull(staticMethodName, "staticMethodName");
        PreCon.notNull(arguments, "arguments");

        return invoke(null, staticMethodName, arguments);
    }

    @Override
    @Nullable
    public <V> V invoke(@Nullable Object instance, String methodName, Object... arguments) {
        PreCon.notNullOrEmpty(methodName, "methodName");
        PreCon.notNull(arguments, "arguments");

        Method method = _aliasMethods != null ? _aliasMethods.get(methodName) : null;

        if (method == null) {

            Collection<Method> methods = _cached.methodsByName(methodName);

            // get method definition
            if (methods.size() == 1) {
                method = methods.iterator().next();
            } else {
                method = Reflection.findMethodByArgs(methods, methodName, arguments);
                if (method == null)
                    throw new RuntimeException("Method '" + methodName +
                            "' not found in type " + _cached.getHandle().getCanonicalName());
            }
        }

        if (instance == null && !Modifier.isStatic(method.getModifiers())) {
            throw new RuntimeException("Method " + methodName + " is not static.");
        }
        else if (instance != null && Modifier.isStatic(method.getModifiers())) {
            throw new RuntimeException("Method " + methodName + " is static.");
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

    @Override
    public Class<?> getHandle() {
        return _cached.getHandle();
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
