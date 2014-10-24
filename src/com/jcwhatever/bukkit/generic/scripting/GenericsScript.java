package com.jcwhatever.bukkit.generic.scripting;

import com.jcwhatever.bukkit.generic.scripting.api.IScriptApi;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import javax.annotation.Nullable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Collection;

/**
 * Generics default {@code IScript} implementation
 */
public class GenericsScript implements IScript {

    private final GenericsScriptManager _manager;
    private final String _name;
    private final String _type;
    private final String _script;

    /**
     * Constructor.
     *
     * @param name    The name of the script.
     * @param type    The script type.
     * @param script  The script source.
     */
    public GenericsScript(GenericsScriptManager manager, String name, String type, String script) {
        PreCon.notNull(manager);
        PreCon.notNullOrEmpty(name);
        PreCon.notNullOrEmpty(type);
        PreCon.notNull(script);

        _manager = manager;
        _name = name;
        _type = type;
        _script = script;
    }

    /**
     * Get the name of the script.
     */
    @Override
    public String getName() {
        return _name;
    }

    /**
     * Get the script source.
     */
    @Override
    public String getScript() {
        return _script;
    }

    /**
     * Get the script type. (file extension)
     */
    @Override
    public String getType() {
        return _type;
    }

    /**
     * Evaluate the script.
     *
     * @param apiCollection  The api to include.
     */
    @Override
    @Nullable
    public IEvaluatedScript evaluate(@Nullable Collection<? extends IScriptApi> apiCollection) {

        ScriptEngine engine = _manager.getEngineManager().getEngineByExtension(getType());
        if (engine == null)
            return null;

        GenericsEvaluatedScript script = new GenericsEvaluatedScript(this, engine, apiCollection);

        // evaluate api
        if (apiCollection != null) {
            for (IScriptApi api : apiCollection) {
                engine.put(api.getVariableName(), api.getApiObject(script));
            }
        }

        try {
            // evaluate script
            engine.eval(getScript());

            return script;

        } catch (ScriptException e) {
            e.printStackTrace();
            return null;
        }
    }

}
