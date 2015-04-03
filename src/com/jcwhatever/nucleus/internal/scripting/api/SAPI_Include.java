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


package com.jcwhatever.nucleus.internal.scripting.api;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.managed.scripting.IEvaluatedScript;
import com.jcwhatever.nucleus.managed.scripting.IScript;
import com.jcwhatever.nucleus.managed.scripting.IScriptApi;
import com.jcwhatever.nucleus.managed.scripting.IScriptManager;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.ScriptUtils;

import org.bukkit.plugin.Plugin;

import java.io.File;
import javax.annotation.Nullable;

/**
 * Provide scripts with ability to include other scripts.
 */
public class SAPI_Include implements IDisposable {

    private final Plugin _plugin;
    private final IScriptManager _manager;
    private final File _includeFolder;
    private final IEvaluatedScript _script;
    private boolean _isDisposed;

    public SAPI_Include(Plugin plugin, IEvaluatedScript script, IScriptManager manager) {
        PreCon.notNull(plugin);
        PreCon.notNull(script);
        PreCon.notNull(manager);

        _plugin = plugin;
        _manager = manager;
        _script = script;

        File includeFolder = _manager.getIncludeFolder();

        if (includeFolder == null) {

            File scriptsDir = new File(plugin.getDataFolder(), "scripts");
            includeFolder = new File(scriptsDir, "includes");
        }

        _includeFolder = includeFolder;
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        _isDisposed = true;
    }

    /**
     * Include script from plugins script include folder.
     *
     * @param fileNames  The relative path of the scripts to include.
     */
    @Nullable
    public void script(String... fileNames) {

        for (String fileName : fileNames) {
            File file = new File(_includeFolder, fileName);

            if (file.exists()) {

                IScript script = ScriptUtils.loadScript(_plugin,
                        _includeFolder, file, _manager.getScriptFactory());

                if (script == null) {
                    NucMsg.warning(_plugin, "Failed to load script named '{0}'.");
                    continue;
                }

                _script.evaluate(script);

            } else {
                NucMsg.warning(_plugin, "Failed to include script named '{0}'. " +
                        "File not found.", file.getName());
            }
        }
    }

    /**
     * Add an api object from the script api repository.
     *
     * @param owningPluginName  The name of the api owning plugin.
     * @param apiName           The variable name of the api.
     * @param variableName      The variable name to use in the script.
     *
     * @return  True if the api was found and included.
     */
    public boolean api(String owningPluginName, String apiName, @Nullable String variableName) {
        PreCon.notNull(owningPluginName);
        PreCon.notNullOrEmpty(apiName);

        if (variableName == null)
            variableName = apiName;

        IScriptApi api = Nucleus.getScriptApiRepo().get(owningPluginName, apiName);
        if (api == null) {
            NucMsg.warning(_plugin, "Failed to include script api named '{0}' from plugin '{1}'. " +
                    "Api not found.", apiName, owningPluginName);
            return false;
        }

        _script.addScriptApi(api, variableName);

        return true;
    }

    /**
     * Get and return a new api object from the script api repository.
     *
     * @param owningPluginName  The name of the api owning plugin.
     * @param apiName           The variable name of the api.
     *
     * @return  The api object or null if not found.
     */
    @Nullable
    public IDisposable apiLocal(String owningPluginName, String apiName) {
        PreCon.notNull(owningPluginName);
        PreCon.notNullOrEmpty(apiName);

        IScriptApi api = Nucleus.getScriptApiRepo().get(owningPluginName, apiName);
        if (api == null) {
            NucMsg.warning(_plugin, "Failed to find script api named '{0}' from plugin '{1}'. " +
                    "Api not found.", apiName, owningPluginName);
            return null;
        }

        return api.createApi(_plugin, _script);
    }
}

