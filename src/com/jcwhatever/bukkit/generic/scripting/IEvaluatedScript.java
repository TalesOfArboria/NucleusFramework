package com.jcwhatever.bukkit.generic.scripting;

import com.jcwhatever.bukkit.generic.scripting.api.IScriptApi;

import javax.script.ScriptEngine;
import java.util.List;

/**
 * A script that has been evaluated by a script engine.
 */
public interface IEvaluatedScript {

    /**
     * Get the script that was evaluated.
     */
    IScript getParentScript();

    /**
     * Get the scripting engine used to evaluate.
     */
    ScriptEngine getScriptEngine();

    /**
     * Get included script api.
     */
    List<IScriptApi> getScriptApi();

    /**
     * Add a script api.
     */
    void addScriptApi(IScriptApi scriptApi);

    /**
     * Invoke a function in the script.
     *
     * @param functionName  The name of the function.
     * @param parameters    Function parameters.
     *
     * @return  Object returned by the function.
     */
    Object invokeFunction(String functionName, Object... parameters);

    /**
     * Evaluate a script into the evaluated script.
     *
     * @param script  The script to evaluated.
     */
    Object evaluate(IScript script);

    /**
     * Reset all API's.
     */
    void resetApi();
}
