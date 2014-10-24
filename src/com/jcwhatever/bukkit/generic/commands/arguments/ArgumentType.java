package com.jcwhatever.bukkit.generic.commands.arguments;


public enum ArgumentType {
    /**
     * An argument whose parameter was not referenced at all
     * by the command sender. Only possible with floating parameters.
     */
    UNDEFINED,

    /**
     * An argument whose parameter was referenced by the command sender
     * but no value provided. Only possible with floating parameters.
     * 
     * <p>Indicates the use of the parameter flag without providing a value.</p>
     * 
     */
    DEFINED_PARAM_UNDEFINED_VALUE,

    /**
     * Indicates the argument value was provided by the command sender.
     */
    DEFINED_VALUE,

    /**
     * Indicates no argument provided but a default value is available.
     * Only possible with static parameters.
     */
    DEFAULT_VALUE
}
