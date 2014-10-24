package com.jcwhatever.bukkit.generic.scripting.api;

import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import org.bukkit.plugin.Plugin;

/**
 * Represents an api object dispatcher.
 */
public interface IScriptApi extends IScriptApiObject {

    /**
     * The owning plugin.
     */
    public Plugin getPlugin();

    /**
     * Get the name of the script variable.
     */
    public String getVariableName();

    /**
     * Get the Api as an object.
     * @return
     */
    public IScriptApiObject getApiObject(IEvaluatedScript script);

}
