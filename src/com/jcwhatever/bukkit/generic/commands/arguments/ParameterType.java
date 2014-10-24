package com.jcwhatever.bukkit.generic.commands.arguments;

/**
 * Specifies the type of command parameter
 * @author JC The Pants
 *
 */
public enum ParameterType {
    /**
     * Command parameter is static, meaning it's in a predefined
     * position within the command.
     */
    STATIC,
    
    /**
     * Command parameter is floating, meaning it is not in a predefined 
     * position and can be placed in any order after all of the static
     * arguments have been entered.
     */
    FLOATING
}
