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
 * Interface for a collection of fields in a type with an underlying instance
 * of the type.
 */
public interface IReflectedInstanceFields extends IReflectedTypeFields {

    /**
     * Get the instance from which field values retrieved and set.
     */
    IReflectedInstance getReflectedInstance();

    /**
     * Get the value of a field by index position.
     *
     * @param index  The fields index position.
     *
     * @param <T>  The return type.
     *
     * @return  Null if failed to access the field.
     */
    <T> T get(int index);

    /**
     * Get the value of a field by name.
     *
     * @param fieldName  The name of the field.
     *
     * @param <T>  The return type.
     *
     * @return Null if value is null or failed to access the field.
     */
    <T> T get(String fieldName);

    /**
     * Set the value of a field at the specified index position.
     *
     * @param index  The fields index position.
     * @param value  The value to set.
     */
    void set(int index, Object value);

    /**
     * Set the value of a field by name.
     *
     * @param fieldName  The name of the field.
     * @param value      The value to set.
     */
    void set(String fieldName, Object value);
}
