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

import com.jcwhatever.bukkit.generic.GenericsPlugin;
import com.jcwhatever.bukkit.generic.collections.HashMapMap;
import com.jcwhatever.bukkit.generic.internal.Msg;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.ReflectUtils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Manages reflection and non API code.
 */
public class ReflectionManager {

    private HashMapMap<String, String, Class<? extends INonApiHandler>>
            _reflectionHandlerClasses = new HashMapMap<>(10);

    private Map<String, INonApiHandler> _reflectionHandlers = new HashMap<>(10);

    private String _nmsVersion;

    /**
     * Constructor.
     *
     * @param plugin              The owning plugin.
     * @param compatibleVersions  Compatible NMS package versions.
     */
    public ReflectionManager(Plugin plugin, String... compatibleVersions) {
        PreCon.notNull(plugin);
        PreCon.notNull(compatibleVersions);

        _nmsVersion = ReflectUtils.getNmsVersion();

        enforceNmsVersion(plugin, compatibleVersions);
    }

    /**
     * Get the NMS package version the manager is operating
     * under. This is normally the current NMS package version
     * but may be different if overridden by a server operator.
     */
    public String getNmsVersion() {
        return _nmsVersion;
    }

    /**
     * Register a non API handler (Uses reflection or non-api code) for a specific NMS version.
     *
     * @param nmsVersion    The NMS package version the handler is for.
     * @param name          The name of the handler.
     * @param handlerClass  The handler class.
     */
    public void registerNonApiHandler(String nmsVersion, String name, Class<? extends INonApiHandler> handlerClass) {
        _reflectionHandlerClasses.put(nmsVersion, name, handlerClass);
    }

    /**
     * Get a non API handler instance for the current NMS package version.
     *
     * @param name  The name of the handler.
     *
     * @param <T>  The handler type.
     *
     * @return  Null if not found.
     */
    @Nullable
    public <T extends INonApiHandler> T getNonApiHandler(String name) {

        INonApiHandler handler = _reflectionHandlers.get(name);
        if (handler != null) {

            @SuppressWarnings("unchecked")
            T result = (T)handler;

            return result;
        }

        Class<? extends INonApiHandler> handlerClass =
                _reflectionHandlerClasses.get(ReflectUtils.getNmsVersion(), name);

        if (handlerClass == null)
            return null;

        Constructor<? extends INonApiHandler> constructor = getConstructor(handlerClass);
        if (constructor == null) {
            throw new RuntimeException("Failed to instantiate an instance of " +
                    "INonApiHandler because a suitable constructor was not found.");
        }

        try {
            handler = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }

        _reflectionHandlers.put(name, handler);

        @SuppressWarnings("unchecked")
        T result = (T)handler;

        return result;
    }

    /**
     * Get a {@code ReflectedType} instance from the specified class.
     *
     * <p>Returns a cached version or a new version.</p>
     *
     * @param clazz  The class to wrap.
     */
    public ReflectedType<?> typeFrom(Class<?> clazz) {
        ReflectedType<?> type = ReflectedType._typeCache.get(clazz);
        if (type != null)
            return type;

        type = new ReflectedType<>(clazz);
        ReflectedType._typeCache.put(clazz, type);

        return type;
    }

    /**
     * Get a {@code ReflectedType} instance from the specified class name.
     *
     * <p>Returns a cached version or a new version.</p>
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
    public ReflectedType<?> typeFrom(String className) {
        PreCon.notNullOrEmpty(className);

        ReflectedType<?> type = ReflectedType._typeNameCache.get(className);
        if (type != null)
            return type;

        Class<?> clazz = ReflectUtils.classFromName(className);

        type = typeFrom(clazz);

        ReflectedType._typeNameCache.put(className, type);

        return type;
    }

    /*
     * Enforce the compatible NMS versions
     */
    private void enforceNmsVersion(Plugin plugin, String... compatibleVersions) {

        if (!ReflectUtils.isVersionCompatible(compatibleVersions)) {

            if (plugin instanceof GenericsPlugin) {

                // allow server administrator the option to disable NMS version enforcement
                boolean enforceNmsVersion = ((GenericsPlugin) plugin).getDataNode()
                        .getBoolean("enforce-nms-version", true);

                _nmsVersion = ((GenericsPlugin) plugin).getDataNode()
                        .getString("nms-version", _nmsVersion);

                if (!enforceNmsVersion) {
                    Msg.warning(plugin, "Plugin {0} is not compatible with NMS version {1}.",
                            plugin.getName(), ReflectUtils.getNmsVersion());

                    Msg.warning(plugin, "enforce-nms-version has been set to false in the plugins config.");
                    return;
                }
            }

            // disable incompatible plugin
            Bukkit.getPluginManager().disablePlugin(plugin);

            Msg.warning(plugin, "Disabling {0} because it's not compatible with NMS version {1}",
                    plugin.getName(), ReflectUtils.getNmsVersion());
        }
    }

    @Nullable
    private Constructor<? extends INonApiHandler> getConstructor(
            Class<? extends INonApiHandler> handlerClass, Class<?>... parameters) {
        try {
            return handlerClass.getConstructor(parameters);
        } catch (NoSuchMethodException ignore) {
            return null;
        }
    }
}
