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

package com.jcwhatever.nucleus.internal.scripting;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.jcwhatever.nucleus.scripting.IScriptApi;
import com.jcwhatever.nucleus.scripting.IScriptApiRepo;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * NucleusFramework's script api repository.
 */
public final class InternalScriptApiRepo implements IScriptApiRepo {

    private final Map<String, IScriptApi> _scriptApis = new HashMap<>(50);
    private final Multimap<Plugin, IScriptApi> _pluginScriptApis =
            MultimapBuilder.hashKeys(50).hashSetValues(5).build();

    /**
     * Constructor.
     */
    public InternalScriptApiRepo() {
        super();
    }

    @Override
    public boolean registerApi(IScriptApi api) {
        PreCon.notNull(api);

        String apiKey = getApiKey(api.getPlugin(), api.getName());

        if (_scriptApis.containsKey(apiKey))
            return false;

        _scriptApis.put(apiKey, api);
        _pluginScriptApis.put(api.getPlugin(), api);

        return true;
    }

    @Override
    public boolean unregisterApi(IScriptApi api) {
        PreCon.notNull(api);

        String apiKey = getApiKey(api.getPlugin(), api.getName());

        IScriptApi removed = _scriptApis.remove(apiKey);

        if (removed != null) {
            _pluginScriptApis.remove(removed.getPlugin(), removed);
            return true;
        }

        return false;
    }

    @Override
    @Nullable
    public IScriptApi get(String pluginName, String variableName) {
        PreCon.notNullOrEmpty(pluginName);
        PreCon.notNullOrEmpty(variableName);

        String apiKey = getApiKey(pluginName, variableName);

        return _scriptApis.get(apiKey);
    }

    @Override
    @Nullable
    public IScriptApi get(Plugin plugin, String variableName) {
        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(variableName);

        String apiKey = getApiKey(plugin, variableName);

        return _scriptApis.get(apiKey);
    }

    /**
     * Unregister all script API's registered by the specified plugin.
     *
     * @param plugin  The plugin.
     */
    @Override
    public void unregisterPlugin(Plugin plugin) {
        PreCon.notNull(plugin);

        Collection<IScriptApi> apis = _pluginScriptApis.removeAll(plugin);

        for (IScriptApi api : apis) {

            String apiKey = getApiKey(plugin, api.getName());

            IScriptApi removed = _scriptApis.remove(apiKey);

            // if for some reason this isn't the correct api, put it back.
            if (removed != api) {
                _scriptApis.put(apiKey, removed);
            }
        }
    }

    // get api key from plugin and api variable name
    private String getApiKey(Plugin plugin, String apiName) {
        return plugin.getName().toLowerCase() + ':' + apiName;
    }

    // get api key from plugin name and api variable name
    private String getApiKey(String pluginName, String apiName) {
        return pluginName.toLowerCase() + ':' + apiName;
    }
}
