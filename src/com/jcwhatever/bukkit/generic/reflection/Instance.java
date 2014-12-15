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

import com.jcwhatever.bukkit.generic.utils.PreCon;

import javax.annotation.Nullable;

/**
 * Base implementation of an instance wrapper.
 */
public abstract class Instance<T> {

    private final ReflectedType<T> _type;
    private final Object _instance;

    /**
     * Constructor.
     *
     * @param type      The instances class type represented by a {@code ReflectedType} instance.
     * @param instance  The instance to encapsulate.
     */
    protected Instance(ReflectedType<T> type, Object instance) {
        PreCon.notNull(type);
        PreCon.notNull(instance);

        if (instance instanceof Instance)
            instance = ((Instance) instance)._instance;

        checkInstance(type, instance);

        _type = type;
        _instance = instance;
    }

    /**
     * Get the {@code ReflectedType} of the instance.
     */
    public ReflectedType<T> getReflectedType() {
        return _type;
    }

    /**
     * Call a method on the instance using the provided arguments.
     *
     * @param methodName  The name of the method to call.
     * @param arguments   The arguments to pass into the method.
     *
     * @return  Null if the method returns null or void.
     */
    @Nullable
    public Object call(String methodName, Object...arguments) {
        return _type.call(_instance, methodName, arguments);
    }

    /**
     * Get the encapsulated object instance.
     * @return
     */
    public Object getHandle() {
        return _instance;
    }

    /**
     * Called to make sure the instance is valid for the wrapper
     * and reflected type.
     *
     * @param type      The reflected type.
     * @param instance  The instance.
     */
    protected void checkInstance(ReflectedType<T> type, Object instance) {
        if (!type.getHandle().isAssignableFrom(instance.getClass())) {
            throw new RuntimeException("Failed to cast instance to reflected type.");
        }
    }
}
