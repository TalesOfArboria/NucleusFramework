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


package com.jcwhatever.bukkit.generic.scripting.api;

import com.jcwhatever.bukkit.generic.messaging.Messenger;
import com.jcwhatever.bukkit.generic.scripting.GenericsScriptManager;
import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import com.jcwhatever.bukkit.generic.scripting.IScript;
import com.jcwhatever.bukkit.generic.scripting.IScriptApiInfo;
import com.jcwhatever.bukkit.generic.scripting.ScriptApiRepo;
import com.jcwhatever.bukkit.generic.scripting.ScriptHelper;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Provide scripts with ability to include other scripts.
 */
@IScriptApiInfo(
        variableName = "include",
        description = "Provide script with ability to include other scripts.")
public class ScriptApiInclude extends GenericsScriptApi {

    private final GenericsScriptManager _manager;

    /**
     * Constructor. Automatically adds variable to script.
     *
     * @param plugin The owning plugin
     */
    public ScriptApiInclude(Plugin plugin, GenericsScriptManager manager) {
        super(plugin);

        PreCon.notNull(manager);

        _manager = manager;
    }

    @Override
    public IScriptApiObject getApiObject(IEvaluatedScript script) {
        return new ApiObject(script);
    }

    public class ApiObject implements IScriptApiObject {

        private final IEvaluatedScript _script;

        ApiObject(IEvaluatedScript script) {
            _script = script;
        }

        /**
         * Include library script from scripts/libs folder within the plugin data folder.
         *
         * @param fileNames  The names of the scripts to include
         */
        public void lib(String... fileNames) {
            List<IScript> scripts = new ArrayList<>(fileNames.length);
            for (String fileName : fileNames) {
                File scriptsDir = new File(getPlugin().getDataFolder(), "scripts");
                File libsDir = new File(scriptsDir, "libs");
                File file = new File(libsDir, fileName);
                if (file.exists()) {

                    IScript script = ScriptHelper.loadScript(scriptsDir, file, _manager.getScriptConstructor());

                    if (script != null)
                        scripts.add(script);
                }
                else {
                    Messenger.warning(getPlugin(), "Failed to include script named '{0}'. " +
                            "File not found.", file.getName());
                }
            }

            for (IScript script : scripts) {
                _script.evaluate(script);
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

            IScriptApi api = ScriptApiRepo.getApi(getPlugin(), owningPluginName, apiName);
            if (api == null) {
                Messenger.warning(getPlugin(), "Failed to include script api named '{0}' from plugin '{1}'. " +
                        "Api not found.", apiName, owningPluginName);
                return false;
            }

            _script.addScriptApi(api, variableName);

            return true;
        }

        @Override
        public void reset() {
            // do nothing
        }
    }
}
