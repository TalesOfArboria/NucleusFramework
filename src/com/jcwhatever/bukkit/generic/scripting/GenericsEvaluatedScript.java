package com.jcwhatever.bukkit.generic.scripting;

import com.jcwhatever.bukkit.generic.scripting.api.IScriptApi;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import javax.annotation.Nullable;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generics default {@code IEvaluatedScript} implementation.
 *
 * <p>An evaluated script</p>
 */
public class GenericsEvaluatedScript implements IEvaluatedScript {

    private final IScript _parentScript;
    private final ScriptEngine _engine;
    private final Map<String, IScriptApi> _scriptApis;

    /**
     * Constructor.
     *
     * @param parentScript  The script that was evaluated.
     * @param engine        The engine that evaluated the script.
     * @param scriptApis    The api that was included.
     */
    public GenericsEvaluatedScript (IScript parentScript, ScriptEngine engine,
                                    @Nullable Collection<? extends IScriptApi> scriptApis) {

        PreCon.notNull(parentScript);
        PreCon.notNull(engine);

        _parentScript = parentScript;
        _engine = engine;
        _scriptApis = new HashMap<>(scriptApis == null ? 10 : scriptApis.size());

        if (scriptApis != null) {
            for (IScriptApi api : scriptApis) {
                _scriptApis.put(api.getVariableName(), api);
            }
        }
    }

    /**
     * Get the script that was evaluated.
     */
    @Override
    public IScript getParentScript() {
        return _parentScript;
    }

    /**
     * Get the script engine that evaluated the script.
     */
    @Override
    public ScriptEngine getScriptEngine() {
        return _engine;
    }

    /**
     * Get the script api included during evaluation.
     */
    @Override
    public List<IScriptApi> getScriptApi() {
        return _scriptApis == null
                ? new ArrayList<IScriptApi>(0)
                : new ArrayList<>(_scriptApis.values());
    }

    @Override
    public void addScriptApi(IScriptApi scriptApi) {

        if (_scriptApis.containsKey(scriptApi.getVariableName()))
            return;

        _scriptApis.put(scriptApi.getVariableName(), scriptApi);

        _engine.put(scriptApi.getVariableName(), scriptApi.getApiObject(this));
    }

    /**
     * Invoke a function in the evaluated script.
     *
     * @param functionName  The name of the function.
     * @param parameters    Function parameters.
     *
     * @return
     */
    @Override
    public Object invokeFunction(String functionName, Object... parameters) {

        Invocable inv = (Invocable)_engine;

        try {
            return inv.invokeFunction(functionName, parameters);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
        catch (ScriptException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Evaluate another script into the scripts engine.
     *
     * @param script  The script to evaluated.
     */
    @Override
    public Object evaluate(IScript script) {

        try {
            return _engine.eval(script.getScript());
        } catch (ScriptException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Reset the included api.
     */
    @Override
    public void resetApi() {
        if (_scriptApis == null)
            return;

        for (IScriptApi api : _scriptApis.values()) {
            api.reset();
        }
    }
}
