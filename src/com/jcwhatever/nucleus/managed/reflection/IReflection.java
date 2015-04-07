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
 * Interface for a reflection context.
 */
public interface IReflection {

    /**
     * Get a {@link IReflectedType} instance from the specified class name.
     *
     * <p>For primitive types, use the primitive name ie "int".</p>
     *
     * <p>For NMS and CraftBukkit classes, use the class name without
     * the craft package version.</p>
     *
     * <p>All other types use full class name.</p>
     *
     * @param className  The name of the class.
     */
    IReflectedType type(String className);

    /**
     * Get a {@link IReflectedType} instance from the specified NMS class name.
     *
     * <p>Returns a cached version or a new version.</p>
     *
     * <p>Use the class name excluding the base package (net.minecraft.server) and
     * nms version.</p>
     *
     * @param nmsClassName  The name of the class.
     */
    IReflectedType nmsType(String nmsClassName);

    /**
     * Get a {@link IReflectedType} instance from the specified Craft class name.
     *
     * <p>Returns a cached version or a new version.</p>
     *
     * <p>Use the class name excluding the base package (org.bukkit.craftbukkit) and
     * nms version.</p>
     *
     * @param craftClassName  The name of the class.
     */
    IReflectedType craftType(String craftClassName);

    /**
     * Get a {@link IReflectedType} instance from the specified class.
     *
     * <p>Returns a cached version or a new version.</p>
     *
     * @param clazz  The class to wrap.
     */
    IReflectedType type(Class<?> clazz);
}
