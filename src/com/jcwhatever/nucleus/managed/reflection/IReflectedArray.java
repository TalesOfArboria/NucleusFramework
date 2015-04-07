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

/**
 * Interface for a reflected array instance.
 */
public interface IReflectedArray {

    /**
     * Get the {@link IReflectedType} of the instance.
     */
    IReflectedType getReflectedType();

    /**
     * Get the length of the arrays first dimension.
     */
    int length();

    /**
     * Get the number of dimensions in the array.
     */
    int getDimensions();

    /**
     * Get a value from the array at the specified index position.
     *
     * @param index  The index position.
     */
    Object get(int index);

    /**
     * Get a value from the array at the specified index position.
     *
     * @param indexes  The index position.
     */
    Object get(int... indexes);

    /**
     * Get a value from the array at the specified index position
     * and encapsulate the value in a {@link IReflectedInstance}.
     *
     * @param index  The index position.
     */
    IReflectedInstance getReflected(int index);

    /**
     * Get a value from the array at the specified index position
     * and encapsulate the value in a {@link IReflectedInstance}.
     *
     * @param indexes  The index position.
     */
    IReflectedInstance getReflected(int... indexes);

    /**
     * Set the value at the specified index position.
     *
     * @param value  The value to set.
     * @param index  The index position.
     */
    void set(Object value, int index);

    /**
     * Set the value at the specified index position.
     *
     * @param value    The value to set.
     * @param indexes  The index position.
     */
    void set(Object value, int... indexes);

    /**
     * Get the encapsulated array instance.
     */
    Object getHandle();
}
