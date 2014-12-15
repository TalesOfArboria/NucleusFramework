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

import com.jcwhatever.bukkit.generic.utils.ReflectUtils;

import java.lang.reflect.Array;

/**
 * Encapsulates an array and provides reflection utilities.
 *
 * @param <T>  The encapsulated type.
 */
public class ReflectedArray<T> extends Instance<T> {

    private final int _length;

    /**
     * Constructor.
     *
     * @param type      The array component type.
     * @param instance  The array instance.
     */
    ReflectedArray(ReflectedType<T> type, Object instance) {
        super(type, instance);

        _length = Array.getLength(instance);
    }

    /**
     * Get the length of the array.
     */
    public int length() {
        return _length;
    }

    /**
     * Get a value from the array at the specified
     * index position.
     *
     * @param index  The index position.
     */
    public Object get(int index) {
        return Array.get(getHandle(), index);
    }

    /**
     * Get a value from the array at the specified
     * index position and encapsulate the value in
     * a {@code ReflectedInstance}.
     *
     * @param index  The index position.
     */
    public ReflectedInstance getReflected(int index) {
        Object object = get(index);

        return new ReflectedInstance<T>(getReflectedType(), object);
    }

    /**
     * Set the value at the specified index position.
     *
     * @param index  The index position.
     * @param value  The value to set.
     */
    public void set(int index, Object value) {
        Array.set(getHandle(), index, value);
    }

    @Override
    protected void checkInstance(ReflectedType<T> type, Object instance) {
        if (!ReflectUtils.isArray(instance)) {
            throw new RuntimeException("Instance is not an array.");
        }

        if (!type.getHandle().isAssignableFrom(
                instance.getClass().getComponentType())) {
            throw new RuntimeException("Array components don't match type.");
        }
    }
}
