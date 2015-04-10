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
 * A reflected instance of an object.
 */
public interface IReflectedInstance extends IWrapper<Object> {

    /**
     * Get the {@link IReflectedType} of the instance.
     */
    IReflectedType getReflectedType();

    /**
     * Get an instance field value.
     *
     * @param fieldName  The name of the field. If an alias is defined in the
     *                   parent {@link IReflectedType}, the alias can be used.
     *
     * @see IReflectedType#fieldAlias
     */
    Object get(String fieldName);

    /**
     * Set an instance field value.
     *
     * @param fieldName  The name of the field. If an alias is defined in the
     *                   parent {@link IReflectedType}, the alias can be used.
     * @param value      The value to set.
     *
     * @see IReflectedType#fieldAlias
     */
    void set(String fieldName, Object value);

    /**
     * Get fields from the instance of the specified class type.
     *
     * @param fieldType  The field class type.
     */
    IReflectedInstanceFields getFields(Class<?> fieldType);

    /**
     * Get all fields from the instance.
     */
    IReflectedInstanceFields getFields();

    /**
     * Invoke a method on the instance using the provided arguments.
     *
     * @param methodName  The name of the method to invoke. If an alias is
     *                    defined in the parent {@link IReflectedType}, the
     *                    alias can be used.
     * @param arguments   The arguments to pass into the method.
     *
     * @return  Null if the method returns null or void.
     *
     * @see IReflectedType#method
     * @see IReflectedType#methodAlias
     */
    @Nullable
    Object invoke(String methodName, Object... arguments);

    /**
     * Get the encapsulated object instance.
     */
    @Override
    Object getHandle();
}
