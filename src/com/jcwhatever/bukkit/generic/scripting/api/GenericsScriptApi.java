package com.jcwhatever.bukkit.generic.scripting.api;

import com.jcwhatever.bukkit.generic.scripting.IScriptApiInfo;
import org.bukkit.plugin.Plugin;

/**
 * Abstract implementation of an api variable within a script.
 */
public abstract class GenericsScriptApi implements IScriptApi {

    private final Plugin _plugin;
    private final IScriptApiInfo _info;

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin
     */
    public GenericsScriptApi(Plugin plugin) {
        _plugin = plugin;

        IScriptApiInfo info = getClass().getAnnotation(IScriptApiInfo.class);
        if (info == null)
            throw new RuntimeException("Script api missing its IScriptApiInfo annotation.");

        _info = info;

    }

    /**
     * Get the owning plugin.
     */
    @Override
    public final Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public final String getVariableName() {
        return _info.variableName();
    }
}
