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


package com.jcwhatever.nucleus.scripting;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.jcwhatever.nucleus.scripting.api.IScriptApi;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Repository of script API's that can be instantiated and retrieved.
 */
public class ScriptApiRepo {

    protected ScriptApiRepo() {}

    protected Map<String, ApiPackage> _scriptApis = new HashMap<>(50);
    protected Multimap<Plugin, ApiPackage> _pluginScriptApis =
            MultimapBuilder.hashKeys(50).hashSetValues(5).build();

    /**
     * Register a script api with the repository so
     * other plugins can use it.
     *
     * @param plugin    The api owning plugin.
     * @param apiClass  The api type to register.
     */
    public boolean registerApiType(Plugin plugin, Class<? extends IScriptApi> apiClass) {
        PreCon.notNull(apiClass);

        ScriptApiInfo info = apiClass.getAnnotation(ScriptApiInfo.class);
        if (info == null)
            throw new RuntimeException("Cannot register scripting api because it has no ScriptApiInfo annotation.");


        String apiKey = getApiKey(plugin, info.variableName());

        if (_scriptApis.containsKey(apiKey))
            return false;

        Constructor<? extends IScriptApi> constructor;
        try {
            constructor = apiClass.getConstructor(Plugin.class);
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to register a script api because it does not have the correct constructor.");
        }

        ApiPackage api = new ApiPackage(info.variableName(), apiClass, constructor);
        _scriptApis.put(apiKey, api);
        _pluginScriptApis.put(plugin, api);

        return true;
    }

    /**
     * Unregister a script api from the repository.
     *
     * <p>Plugins are expected to unregister their API types
     * when they are disabled.</p>
     *
     * @param plugin    The api owning plugin.
     * @param apiClass  The api type to remove.
     */
    public boolean unregisterApiType(Plugin plugin, Class<? extends IScriptApi> apiClass) {
        PreCon.notNull(apiClass);

        ScriptApiInfo info = apiClass.getAnnotation(ScriptApiInfo.class);
        if (info == null)
            throw new RuntimeException("Cannot unregister scripting api because it has no ScriptApiInfo annotation.");

        String apiKey = getApiKey(plugin, info.variableName());

        ApiPackage api = _scriptApis.remove(apiKey);

        if (api != null) {
            _pluginScriptApis.remove(plugin, api);
            return true;
        }

        return false;
    }

    /**
     * Get an api from the repository by the name of its owning plugin
     * and variable name and instantiate for the specified plugin.
     *
     * @param plugin            The plugin to instantiate the api for.
     * @param owningPluginName  The name of the api owning plugin.
     * @param variableName      Case sensitive variable name.
     *
     * @return  An {@code IScriptApi} instance or null if not found.
     */
    @Nullable
    public IScriptApi getApi(Plugin plugin, String owningPluginName, String variableName) {
        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(owningPluginName);
        PreCon.notNullOrEmpty(variableName);

        String apiKey = getApiKey(owningPluginName, variableName);

        ApiPackage apiPackage = _scriptApis.get(apiKey);
        if (apiPackage == null)
            return null;

        return instantiate(plugin, apiPackage);
    }

    /**
     * Get an api from the repository by its
     * owning plugin and variable name and instantiate it
     * for the owning plugin.
     *
     * @param plugin        The api owning plugin and plugin to instantiate for.
     * @param variableName  Case sensitive variable name.
     *
     * @return  The {@code IScriptApi} instance or null if not found.
     */
    @Nullable
    public IScriptApi getApi(Plugin plugin, String variableName) {
        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(variableName);

        String apiKey = getApiKey(plugin, variableName);

        ApiPackage apiPackage = _scriptApis.get(apiKey);
        if (apiPackage == null)
            return null;

        return instantiate(plugin, apiPackage);
    }

    /**
     * Unregister all script API's registered by the
     * specified plugin.
     *
     * @param plugin  The plugin.
     */
    public void unregisterPlugin(Plugin plugin) {
        PreCon.notNull(plugin);

        Collection<ApiPackage> apis = _pluginScriptApis.removeAll(plugin);

        for (ApiPackage api : apis) {
            ApiPackage apiPackage = _scriptApis.remove(api.name);

            // if for some reason this isn't the correct api, put it back.
            if (apiPackage != api) {
                _scriptApis.put(api.name, apiPackage);
            }
        }
    }

    // get api key from plugin and api variable name
    protected String getApiKey(Plugin plugin, String apiName) {
        return plugin.getName().toLowerCase() + ':' + apiName;
    }

    // get api key from plugin name and api variable name
    protected String getApiKey(String pluginName, String apiName) {
        return pluginName.toLowerCase() + ':' + apiName;
    }

    protected IScriptApi instantiate(Plugin plugin, ApiPackage apiPackage) {

        Class<? extends IScriptApi> apiClass = apiPackage.apiClass;
        Constructor<? extends IScriptApi> constructor = apiPackage.constructor;

        ScriptApiInfo info = apiClass.getAnnotation(ScriptApiInfo.class);
        if (info == null)
            throw new RuntimeException("Registered script api class does not have required ScriptApiInfo annotation.");

        try {
            return constructor.newInstance(plugin);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to instantiate a script api.");
        }
    }

    protected static class ApiPackage {
        final String name;
        final Class<? extends IScriptApi> apiClass;
        final Constructor<? extends IScriptApi> constructor;

        ApiPackage(String name, Class<? extends IScriptApi> apiClass, Constructor<? extends IScriptApi> constructor) {
            this.name = name;
            this.apiClass = apiClass;
            this.constructor = constructor;
        }
    }
}
