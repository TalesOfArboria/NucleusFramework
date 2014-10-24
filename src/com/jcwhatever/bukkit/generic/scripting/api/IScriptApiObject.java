package com.jcwhatever.bukkit.generic.scripting.api;

/**
 * Represents an api object to be included with an evaluated script.
 */
public interface IScriptApiObject {

    /**
     * Reset object, release resources to prevent memory leaks.
     */
    public void reset();
}
