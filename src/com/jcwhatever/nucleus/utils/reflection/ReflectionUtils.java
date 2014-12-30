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

import com.google.common.collect.ImmutableMap;
import com.jcwhatever.nucleus.utils.PreCon;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Reflection utilities.
 */
public class ReflectionUtils {

    private ReflectionUtils () {}

    /**
     * Maps primitive types to wrapper types.
     */
    public static final Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS
            = new ImmutableMap.Builder<Class<?>, Class<?>>()
            .put(boolean.class, Boolean.class)
            .put(byte.class, Byte.class)
            .put(char.class, Character.class)
            .put(short.class, Short.class)
            .put(int.class, Integer.class)
            .put(long.class, Long.class)
            .put(float.class, Float.class)
            .put(double.class, Double.class)
            .put(void.class, Void.class)
            .build();

    /**
     * Maps primitive wrapper types to primitives.
     */
    public static final Map<Class<?>, Class<?>> WRAPPERS_TO_PRIMITIVES
            = new ImmutableMap.Builder<Class<?>, Class<?>>()
            .put(Boolean.class, boolean.class)
            .put(Byte.class, byte.class)
            .put(Character.class, char.class)
            .put(Short.class, short.class)
            .put(Integer.class, int.class)
            .put(Long.class, long.class)
            .put(Float.class, float.class)
            .put(Double.class, double.class)
            .put(Void.class, void.class)
            .build();

    /**
     * Maps primitive names to primitive type.
     */
    public static final Map<String, Class<?>> NAMES_TO_PRIMITIVES
            = new ImmutableMap.Builder<String, Class<?>>()
            .put("boolean", boolean.class)
            .put("byte", byte.class)
            .put("char", char.class)
            .put("short", short.class)
            .put("int", int.class)
            .put("long", long.class)
            .put("float", float.class)
            .put("double", double.class)
            .put("void", void.class)
            .build();

    /**
     * Create a new {@code Reflection} instance for the specified
     * NMS version.
     *
     * @param nmsVersion  The nms version.
     */
    public static Reflection newReflection(String nmsVersion) {
        return new Reflection(nmsVersion);
    }

    /**
     * Determine if an object is an array.
     *
     * @param object  The object to check.
     */
    public static int getArrayDimensions(Object object) {
        PreCon.notNull(object);

        String className = object.getClass().getName();

        for (int i=0; i < className.length(); i++) {
            char ch = className.charAt(i);
            if (ch != '[')
                return i;
        }

        return 0;
    }

    /**
     * Searches the provided collection of constructor candidates for a constructor
     * that can be used with the provided arguments.
     *
     * @param candidates  The candidate constructors to check.
     * @param args        The arguments to check.
     *
     * @return  Null if a matching constructor was not found.
     */
    @Nullable
    public static Constructor<?> findConstructorByArgs(Collection<Constructor<?>> candidates, Object... args) {
        for (Constructor<?> c : candidates) {

            Class<?>[] ptypes = c.getParameterTypes();
            if (ptypes.length != args.length)
                continue;

            boolean isMatch = true;

            for (int i=0; i < ptypes.length; i++) {

                Class<?> arg = args[i].getClass();

                if (ptypes[i].isPrimitive() && !arg.isPrimitive()) {
                    arg = WRAPPERS_TO_PRIMITIVES.get(args[i].getClass());
                    if (arg == null)
                        continue;
                }

                if (!ptypes[i].isAssignableFrom(arg)) {
                    isMatch = false;
                    break;
                }
            }

            if (isMatch) {
                return c;
            }
        }

        return null;
    }

    /**
     * Searches the provided collection of method candidates for a method
     * that can be used with the provided arguments.
     *
     * @param candidates  The candidate methods to check.
     * @param methodName  The name of the method to find.
     * @param args        The arguments to check.
     *
     * @return  Null if a matching method was not found.
     */
    @Nullable
    public static Method findMethodByArgs(Collection<Method> candidates, String methodName, Object... args) {

        for (Method method : candidates) {
            if (!method.getName().equals(methodName))
                continue;

            Class<?>[] ptypes = method.getParameterTypes();
            if (ptypes.length != args.length)
                continue;

            boolean isMatch = true;

            for (int i=0; i < ptypes.length; i++) {

                Class<?> arg = args[i].getClass();

                if (ptypes[i].isPrimitive() && !arg.isPrimitive()) {
                    arg = WRAPPERS_TO_PRIMITIVES.get(args[i].getClass());
                    if (arg == null)
                        continue;
                }

                if (!ptypes[i].isAssignableFrom(arg)) {
                    isMatch = false;
                    break;
                }
            }

            if (isMatch) {
                return method;
            }
        }

        return null;
    }
}
