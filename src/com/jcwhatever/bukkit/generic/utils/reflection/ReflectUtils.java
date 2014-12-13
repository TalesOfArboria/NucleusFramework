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

package com.jcwhatever.bukkit.generic.utils.reflection;

import com.google.common.collect.ImmutableMap;
import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.internal.Msg;
import com.jcwhatever.bukkit.generic.storage.IDataNode;

import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

/**
 * Reflection utilities.
 */
public class ReflectUtils {

    private ReflectUtils() {}

    private static final String CRAFT_BASE_PACKAGE = "org.bukkit.craftbukkit";
    private static final String NMS_BASE_PACKAGE = "net.minecraft.server";
    private static Pattern PATTERN_VERSION = Pattern.compile(".*\\.(v\\d+_\\d+_\\w*\\d+)");

    /**
     * Maps primitive types to wrapper types.
     */
    public static final Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS
            = new ImmutableMap.Builder<Class<?>, Class<?>>()
            .put(boolean.class, Boolean.class)
            .put(byte.class, Byte.class)
            .put(char.class, Character.class)
            .put(double.class, Double.class)
            .put(float.class, Float.class)
            .put(int.class, Integer.class)
            .put(long.class, Long.class)
            .put(short.class, Short.class)
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
            .put(Double.class, double.class)
            .put(Float.class, float.class)
            .put(Integer.class, int.class)
            .put(Long.class, long.class)
            .put(Short.class, short.class)
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
            .put("double", double.class)
            .put("float", float.class)
            .put("int", int.class)
            .put("long", long.class)
            .put("short", short.class)
            .put("void", void.class)
            .build();

    private static String _version; // package version

    private static String _craftBasePackage;
    private static String _craftPackage;

    private static String _nmsBasePackage;
    private static String _nmsPackage;

    static {

        IDataNode dataNode = GenericsLib.getPlugin().getDataNode();

        _nmsBasePackage = dataNode.getString("nms-base-package", NMS_BASE_PACKAGE);
        _craftBasePackage = dataNode.getString("craft-base-package", CRAFT_BASE_PACKAGE);

        loadPackageVersion();

        if (canReflect()) {
            _craftPackage = _craftBasePackage + '.' + _version;
            _nmsPackage = _nmsBasePackage + '.' + _version;
        }
    }

    /**
     * Determine if reflection is possible.
     *
     * <p>This is determine by weather or not a package version
     * was set or detected.</p>
     */
    public static boolean canReflect() {
        return _version != null;
    }

    /**
     * Get the package name from a class.
     *
     * @param clazz  The class.
     */
    public static String getPackage(Class<?> clazz) {
        return getPackage(clazz.getCanonicalName());
    }

    /**
     * Get the package name from a class name.
     *
     * @param className  The class name.
     */
    public static String getPackage(String className) {

        int index = className.lastIndexOf('.');

        return index > 0
                ? className.substring(0, index)
                : "";
    }

    /**
     * Determine if an object is an array.
     *
     * @param object  The object to check.
     */
    public static boolean isArray(Object object) {
        return object.getClass().getName().indexOf('[') == 0;
    }

    /**
     * Get the current CraftBukkit class name using a class name without a version.
     *
     * <p>If the package name is org.bukkit.craftbukkit.v1_7_R0.CraftServer, then
     * the correct input is org.bukkit.craftbukkit.CraftServer</p>
     *
     * @param versionlessClassName  The class name without the CraftBukkit version package name.
     */
    public static String getCraftClassName(String versionlessClassName) {
        return _craftPackage + versionlessClassName.replaceFirst(_craftBasePackage, "");
    }

    /**
     * Get the current NMS class name using a class name without a version.
     *
     * <p>If the package name is net.minecraft.server.v1_8_R1.CraftServer, then
     * the correct input is net.minecraft.server.CraftServer</p>
     *
     * @param versionlessClassName  The class name without the CraftBukkit version package name.
     */
    public static String getNMSClassName(String versionlessClassName) {
        return _nmsPackage + versionlessClassName.replaceFirst(_nmsBasePackage, "");
    }

    /**
     * Gets a class from a class name.
     *
     * <p>For primitive types, use the primitive name ie "int".</p>
     *
     * <p>For NMS and CraftBukkit classes, use the class name without
     * the craft package version.</p>
     *
     * <p>All other types, use full class name.</p>
     *
     * @param versionlessClassName
     */
    public static Class<?> classFromName(String versionlessClassName) {

        Class<?> primitive = NAMES_TO_PRIMITIVES.get(versionlessClassName);
        if (primitive != null)
            return primitive;

        String className;

        if (versionlessClassName.startsWith(_craftBasePackage)) {
            className = getCraftClassName(versionlessClassName);
        }
        else
            className = versionlessClassName.startsWith(_nmsBasePackage)
                    ? getNMSClassName(versionlessClassName)
                    : versionlessClassName;

        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to find class " + className);
        }
    }

    /**
     * Searches the provided collection of constructor candidates for a constructor
     * that can be used with the provided arguments.
     *
     * @param candidates  The candidate constructors to check.
     * @param args        The arguments to check.
     *
     * @param <T>  The constructor type.
     *
     * @return  Null if a matching constructor was not found.
     */
    @Nullable
    public static <T> Constructor<T> findConstructorByArgs(Collection<Constructor<T>> candidates, Object... args) {
        for (Constructor<T> c : candidates) {

            Class<?>[] ptypes = c.getParameterTypes();
            if (ptypes.length != args.length)
                continue;

            boolean isMatch = true;

            for (int i=0; i < ptypes.length; i++) {

                Class<?> arg = args[i].getClass();

                if (ptypes[i].isPrimitive() && !arg.isPrimitive()) {
                    arg = ReflectUtils.WRAPPERS_TO_PRIMITIVES.get(args[i].getClass());
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
                    arg = ReflectUtils.WRAPPERS_TO_PRIMITIVES.get(args[i].getClass());
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

    // load the the craft package version from GenericsLib config
    // or detect the version.
    private static void loadPackageVersion() {
        _version = GenericsLib.getPlugin().getDataNode().getString("package-version");
        if (_version == null) {

            Class<? extends Server> serverClass = Bukkit.getServer().getClass();

            Matcher versionMatcher = PATTERN_VERSION.matcher(getPackage(serverClass));

            if (versionMatcher.matches()) {
                _version = versionMatcher.group(1);

                Msg.info("Reflection: CraftBukkit version found: {0}", _version);
            }
        }
        else {
            Msg.info("Reflection: Using craft version from GenericsLib config: {0}", _version);
        }

        if (_version == null) {
            Msg.severe("Failed to find CraftBukkit version for reflection purposes.");
        }
    }
}
