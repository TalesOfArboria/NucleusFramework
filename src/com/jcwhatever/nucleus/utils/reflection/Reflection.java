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

import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

/**
 * A reflection manager used for a specific NMS version.
 *
 * <p>Intended for use as a modular reflection system where reflection code for different
 * NMS versions are kept separate and used as needed for the detected version.</p>
 */
public class Reflection {

    static final String CRAFT_BASE_PACKAGE = "org.bukkit.craftbukkit";
    static final String NMS_BASE_PACKAGE = "net.minecraft.server";
    static final Pattern CRAFT_BASE_PACKAGE_PATTERN = Pattern.compile(CRAFT_BASE_PACKAGE);
    static final Pattern NMS_BASE_PACKAGE_PATTERN = Pattern.compile(NMS_BASE_PACKAGE);

    static final Map<Class<?>, CachedReflectedType> _typeCache = new WeakHashMap<>(30);
    static final Map<String, CachedReflectedType> _typeNameCache = new WeakHashMap<>(30);

    final String _nmsVersion;

    /**
     * Constructor.
     *
     * @param nmsVersion  The nms version the instance is for. This is the version
     *                    package name used by the NMS classes. (i.e v1_8_R1)
     */
    public Reflection(String nmsVersion) {
        PreCon.notNullOrEmpty(nmsVersion);

        _nmsVersion = nmsVersion;
    }

    /**
     * Get a {@link ReflectedType} instance from the specified class name.
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
    public ReflectedType type(String className) {
        PreCon.notNullOrEmpty(className);

        CachedReflectedType type = _typeNameCache.get(className);
        if (type != null)
            return new ReflectedType(type);

        Class<?> clazz = classFromName(className);

        type = new CachedReflectedType(clazz);

        _typeNameCache.put(className, type);
        _typeCache.put(clazz, type);

        return new ReflectedType(type);
    }

    /**
     * Get a {@link ReflectedType} instance from the specified NMS class name.
     *
     * <p>Returns a cached version or a new version.</p>
     *
     * <p>Use the class name excluding the base package (net.minecraft.server) and
     * nms version.</p>
     *
     * @param nmsClassName  The name of the class.
     */
    public ReflectedType nmsType(String nmsClassName) {
        PreCon.notNullOrEmpty(nmsClassName);

        nmsClassName = NMS_BASE_PACKAGE + '.' + _nmsVersion + '.' + nmsClassName;

        return type(nmsClassName);
    }

    /**
     * Get a {@link ReflectedType} instance from the specified Craft class name.
     *
     * <p>Returns a cached version or a new version.</p>
     *
     * <p>Use the class name excluding the base package (org.bukkit.craftbukkit) and
     * nms version.</p>
     *
     * @param craftClassName  The name of the class.
     */
    public ReflectedType craftType(String craftClassName) {
        PreCon.notNullOrEmpty(craftClassName);

        craftClassName = CRAFT_BASE_PACKAGE + '.' + _nmsVersion + '.'  + craftClassName;

        return type(craftClassName);
    }

    /**
     * Get a {@link ReflectedType} instance from the specified class.
     *
     * <p>Returns a cached version or a new version.</p>
     *
     * @param clazz  The class to wrap.
     */
    public ReflectedType type(Class<?> clazz) {
        CachedReflectedType type = _typeCache.get(clazz);
        if (type != null)
            return new ReflectedType(type);

        type = new CachedReflectedType(clazz);
        _typeCache.put(clazz, type);

        return new ReflectedType(type);
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
    private Class<?> classFromName(String versionlessClassName) {

        Class<?> primitive = ReflectionUtils.NAMES_TO_PRIMITIVES.get(versionlessClassName);
        if (primitive != null)
            return primitive;

        // try the name first
        try {
            return Class.forName(versionlessClassName);
        } catch (ClassNotFoundException ignore) {}

        // try alternatives
        String className;

        if (versionlessClassName.startsWith(CRAFT_BASE_PACKAGE)) {
            className = getCraftClassName(versionlessClassName);
        }
        else
            className = versionlessClassName.startsWith(NMS_BASE_PACKAGE)
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
     * Get the current CraftBukkit class name using a class name without a version.
     *
     * <p>If the package name is org.bukkit.craftbukkit.v1_7_R0.CraftServer, then
     * the correct input is org.bukkit.craftbukkit.CraftServer</p>
     *
     * @param versionlessClassName  The class name without the CraftBukkit version package name.
     */
    private String getCraftClassName(String versionlessClassName) {
        return CRAFT_BASE_PACKAGE + CRAFT_BASE_PACKAGE_PATTERN.matcher(versionlessClassName)
                .replaceFirst("");
    }

    /**
     * Get the current NMS class name using a class name without a version.
     *
     * <p>If the package name is net.minecraft.server.v1_8_R1.CraftServer, then
     * the correct input is net.minecraft.server.CraftServer</p>
     *
     * @param versionlessClassName  The class name without the CraftBukkit version package name.
     */
    private String getNMSClassName(String versionlessClassName) {
        return NMS_BASE_PACKAGE + NMS_BASE_PACKAGE_PATTERN.matcher(versionlessClassName)
                .replaceFirst("");
    }
}
