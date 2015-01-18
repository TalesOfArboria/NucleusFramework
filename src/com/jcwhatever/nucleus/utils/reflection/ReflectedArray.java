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

import com.jcwhatever.nucleus.utils.PreCon;

import java.lang.reflect.Array;

/**
 * Encapsulates an array and provides reflection utilities.
 */
public class ReflectedArray extends Instance {

    private final int _length;
    private final int _dimensions;

    /**
     * Constructor.
     *
     * @param type      The array component type.
     * @param instance  The array instance.
     */
    ReflectedArray(ReflectedType type, Object instance) {
        super(type, instance);

        _length = Array.getLength(instance);
        _dimensions = ReflectionUtils.getArrayDimensions(instance);
    }

    /**
     * Get the length of the arrays first dimension.
     */
    public int length() {
        return _length;
    }

    /**
     * Get the number of dimensions in the array.
     */
    public int getDimensions() {
        return _dimensions;
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
     * index position.
     *
     * @param indexes  The index position.
     */
    public Object get(int... indexes) {
        PreCon.lessThanEqual(indexes.length, _dimensions, "Too many dimensions.");

        Object obj = getHandle();

        for (int index : indexes) {

            Class<?> componentType = obj.getClass().getComponentType();

            if (!componentType.isArray())
                throw new RuntimeException("Not an array");

            obj = Array.get(obj, index);
        }

        return obj;
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

        return new ReflectedInstance(getReflectedType(), object);
    }

    /**
     * Get a value from the array at the specified
     * index position and encapsulate the value in
     * a {@code ReflectedInstance}.
     *
     * @param indexes  The index position.
     */
    public ReflectedInstance getReflected(int... indexes) {
        Object object = get(indexes);

        return new ReflectedInstance(getReflectedType(), object);
    }

    /**
     * Set the value at the specified index position.
     *
     * @param value  The value to set.
     * @param index  The index position.
     */
    public void set(Object value, int index) {
        Array.set(getHandle(), index, value);
    }

    /**
     * Set the value at the specified index position.
     *
     * @param value  The value to set.
     * @param indexes  The index position.
     */
    public void set(Object value, int... indexes) {
        PreCon.lessThanEqual(indexes.length, _dimensions, "Too many dimensions.");

        Object obj = getHandle();

        for (int i=0; i < indexes.length; i++) {

            Class<?> componentType = obj.getClass().getComponentType();

            if (!componentType.isArray())
                throw new RuntimeException("Not an array");

            if (i < indexes.length - 1) {
                obj = Array.get(obj, indexes[i]);
            }
            else {
                Array.set(obj, indexes[i], value);
            }
        }
    }

    @Override
    protected void checkInstance(ReflectedType type, Object instance) {
        if (!instance.getClass().isArray()) {
            throw new RuntimeException("Instance is not an array.");
        }

        Class<?> componentType = instance.getClass().getComponentType();

        while (componentType.isArray()) {
            componentType = componentType.getComponentType();
        }

        if (!type.getHandle().isAssignableFrom(componentType)) {
            throw new RuntimeException("Array components don't match type.");
        }
    }
}
