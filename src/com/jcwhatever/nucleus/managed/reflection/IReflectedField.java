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

import com.jcwhatever.nucleus.mixins.INamed;
import com.jcwhatever.nucleus.mixins.IWrapper;

import java.lang.reflect.Field;
import javax.annotation.Nullable;

/**
 * Interface for a wrapper of a {@link Field}.
 */
public interface IReflectedField extends INamed, IWrapper<Field> {

    /**
     * Get the field type.
     */
    IReflectedType getReflectedType();

    /**
     * Get the field modifiers as they were when reflected.
     */
    int getModifiers();

    /**
     * Get the field modifiers as they are now.
     */
    int getCurrentModifiers();

    /**
     * Determine if the field is static.
     */
    boolean isStatic();

    /**
     * Determine if the field is final.
     */
    boolean isFinal();

    /**
     * Determine if the field is private.
     */
    boolean isPrivate();

    /**
     * Determine if the field is native.
     */
    boolean isNative();

    /**
     * Determine if the field is protected.
     */
    boolean isProtected();

    /**
     * Determine if the field is public.
     */
    boolean isPublic();

    /**
     * Determine if the field is strict.
     */
    boolean isStrict();

    /**
     * Determine if the field is transient.
     */
    boolean isTransient();

    /**
     * Determine if the field is volatile.
     */
    boolean isVolatile();

    /**
     * Get the field value.
     *
     * @param instance  The instance to get the value from. Null for static.
     */
    Object get(@Nullable Object instance);

    /**
     * Set the field value.
     *
     * @param instance  The instance to set the value on. Null for static.
     * @param value     The value to set.
     */
    void set(@Nullable Object instance, @Nullable Object value);

    /**
     * Get the {@link java.lang.reflect.Field} object.
     */
    @Override
    Field getHandle();
}
