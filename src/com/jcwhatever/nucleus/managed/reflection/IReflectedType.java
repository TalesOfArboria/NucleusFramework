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

package com.jcwhatever.nucleus.managed.reflection;

import com.jcwhatever.nucleus.mixins.IWrapper;

import javax.annotation.Nullable;

/**
 * Interface for a reflected type.
 */
public interface IReflectedType extends IWrapper<Class<?>> {

    /**
     * Get all fields of the specified type.
     */
    IReflectedTypeFields getFields();

    /**
     * Get fields of the specified type.
     *
     * <p>Only stores by the field type. Super types
     * are not stored.</p>
     *
     * <p>Specifying the {@link java.lang.Object} type returns all fields.</p>
     *
     * @param fieldType  The field type.
     */
    IReflectedTypeFields getFields(Class<?> fieldType);

    /**
     * Get a field from the type by name.
     *
     * @param name  The field name. If an alias is defined,
     *              the alias can be used.
     *
     * @see #fieldAlias
     */
    IReflectedField getField(String name);

    /**
     * Get an enum constant from the type.
     *
     * @param constantName  The enum constant name. If an alias is defined,
     *                      the alias can be used.
     *
     * @return  The enum constant.
     *
     * @see #enumConst
     * @see #enumAlias
     */
    Object getEnum(String constantName);

    /**
     * Get a static field value.
     *
     * @param fieldName  The name of the field. If an alias is defined,
     *                   the alias can be used.
     *
     * @see #fieldAlias
     */
    Object get(String fieldName);

    /**
     * Set a static field value.
     *
     * @param fieldName  The name of the field. If an alias is defined,
     *                   the alias can be used.
     * @param value      The value to set.
     *
     * @see #fieldAlias
     */
    void set(String fieldName, Object value);

    /**
     * Create a new instance of the encapsulated class using
     * a pre-registered constructor alias.
     *
     * @param alias      The registered alias name of the constructor signature.
     * @param arguments  The constructor arguments.
     *
     * @return  The new instance.
     *
     * @see #constructorAlias
     */
    Object construct(String alias, Object... arguments);

    /**
     * Create a new instance of the encapsulated class using
     * a pre-registered constructor alias and wrap in
     * {@link IReflectedInstance}.
     *
     * @param alias      The registered alias name of the constructor signature.
     * @param arguments  The constructor arguments.
     *
     * @return  The new instance.
     *
     * @see #constructorAlias
     */
    IReflectedInstance constructReflect(String alias, Object... arguments);

    /**
     * Create a new array whose component type is
     * the encapsulated class.
     *
     * @param size  The size of the array.
     *
     * @return  {@link IReflectedArray} instance.
     */
    IReflectedArray newArray(int size);

    /**
     * Create a new array whose component type is
     * that of the encapsulated class.
     *
     * @param sizes  The size of each array dimension.
     *
     * @return  {@link IReflectedArray} instance.
     */
    IReflectedArray newArray(int... sizes);

    /**
     * Create a new {@link IReflectedInstance} to encapsulate
     * an object instance that is known to be of the same
     * type as the encapsulated class.
     *
     * @param instance  The instance to wrap.
     */
    IReflectedInstance reflect(Object instance);

    /**
     * Create a new {@link IReflectedArray} to encapsulate
     * an array instance that is known to be of the same
     * type as the encapsulated class.
     *
     * @param instance  The instance to wrap.
     */
    IReflectedArray reflectArray(Object instance);

    /**
     * Register a constructor signature under an alias name.
     *
     * <p></p>Improves performance by allowing the specific constructor to be
     * retrieved by the alias name. Use {@link #construct} or {@link #constructReflect}
     * to create new instances using the alias name.
     *
     * @param alias      The constructor alias.
     * @param signature  The constructor signature.
     *
     * @return  Self for chaining.
     */
    IReflectedType constructorAlias(String alias, Class<?>... signature);

    /**
     * Register a field alias name.
     *
     * <p>Used to assign a readable name to obfuscated fields and/or
     * improve performance by pre-defining a field.</p>
     *
     * <p>Can be used for static or non-static fields.</p>
     *
     * @param fieldAlias  The field alias. Can be the actual field name.
     * @param fieldName   The actual field name.
     *
     * @return  Self for chaining.
     *
     * @see #get
     * @see #set
     */
    IReflectedType fieldAlias(String fieldAlias, String fieldName);

    /**
     * Register an enum constant alias name.
     *
     * <p>Used to assign a readable name to obfuscated enum constants and/or
     * improve performance by pre-defining and caching enum constant.</p>
     *
     * @param constantAlias  The alias name.
     * @param constantName   The actual enum constant name.
     *
     * @return  Self for chaining.
     */
    IReflectedType enumAlias(String constantAlias, String constantName);

    /**
     * Register an enum constant for faster lookup.
     *
     * @param constantName  The constant name.
     *
     * @return  Self for chaining.
     */
    IReflectedType enumConst(String constantName);

    /**
     * Registers a method name to a specific signature.
     *
     * <p>Does not allow for overloaded methods. Each registered name must be unique. Use an
     * alias for overloads.</p>
     *
     * <p>Improves code readability and method lookup performance.</p>
     *
     * <p>Can be used for static or non-static methods.</p>
     *
     * @param methodName  The method name.
     * @param signature   The argument types of the method.
     *
     * @return Self for chaining.
     *
     * @see IReflectedInstance#invoke
     * @see #invoke
     * @see #invokeStatic
     * @see #methodAlias
     */
    IReflectedType method(String methodName, Class<?>... signature);

    /**
     * Register a method alias name to a specific method signature to aid
     * in code readability and method lookup performance.
     *
     * <p>Can also be used to assign a unique alias to a method that has overloads.</p>
     *
     * <p>Can be used for static or non-static methods.</p>
     *
     * @param alias       The alias for the method. Can match an actual method name
     *                    but will prevent use of overloads.
     * @param methodName  The name of the method.
     * @param signature   The argument types of the method.
     *
     * @return Self for chaining.
     *
     * @see IReflectedInstance#invoke
     * @see #invoke
     * @see #invokeStatic
     * @see #method
     */
    IReflectedType methodAlias(String alias, String methodName, Class<?>... signature);

    /**
     * Invoke a static method on the encapsulated class.
     *
     * @param staticMethodName  The name of the static method.
     * @param arguments         The method arguments.
     *
     * @param <V>  The return type.
     *
     * @return  Null if the method returns null or void.
     *
     * @see #method
     * @see #methodAlias
     */
    @Nullable
    <V> V invokeStatic(String staticMethodName, Object... arguments);

    /**
     * Invoke a method on an instance of the encapsulated class.
     *
     * @param instance    The instance.
     * @param methodName  The name of the method. If an alias is defined,
     *                    the alias can be used.
     * @param arguments   The method arguments.
     *
     * @param <V>  The return type.
     *
     * @return  Null if the method returns null or void.
     *
     * @see #method
     * @see #methodAlias
     */
    @Nullable
    <V> V invoke(@Nullable Object instance, String methodName, Object... arguments);

    /**
     * Get the encapsulated class.
     */
    @Override
    Class<?> getHandle();
}
