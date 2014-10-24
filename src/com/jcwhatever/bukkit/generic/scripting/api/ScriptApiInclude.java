package com.jcwhatever.bukkit.generic.scripting.api;

import com.jcwhatever.bukkit.generic.scripting.GenericsScriptManager;
import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import com.jcwhatever.bukkit.generic.scripting.IScript;
import com.jcwhatever.bukkit.generic.scripting.IScriptApiInfo;
import com.jcwhatever.bukkit.generic.scripting.ScriptHelper;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void reset() {
        // do nothing
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
            }

            for (IScript script : scripts) {
                _script.evaluate(script);
            }
        }

        @Override
        public void reset() {
            // do nothing
        }
    }
}
