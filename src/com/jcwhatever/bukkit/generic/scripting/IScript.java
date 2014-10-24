package com.jcwhatever.bukkit.generic.scripting;

import com.jcwhatever.bukkit.generic.scripting.api.IScriptApi;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * A data object that holds information and source for a script.
 */
public interface IScript {

    /**
     * Get the name of the script.
     */
    String getName();

    /**
     * Get the script source.
     */
    String getScript();

    /**
     * Get the script type.
     */
    String getType();

    /**
     * Evaluate the script.
     *
     * @param apiCollection  The api to include.
     */
    @Nullable
    IEvaluatedScript evaluate(@Nullable Collection<? extends IScriptApi> apiCollection);

}
