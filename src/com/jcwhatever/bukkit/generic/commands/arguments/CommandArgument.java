package com.jcwhatever.bukkit.generic.commands.arguments;

import com.jcwhatever.bukkit.generic.utils.PreCon;

/**
 * A data storage object that represents
 * a single supplied parameter to a command.
 *
 */
public class CommandArgument {

    private final String _name;
    private final String _value;
    private final String _defaultValue;
    private final ParameterType _paramType;
    private final ArgumentType _argType;

    /**
     * Constructor.
     *
     * @param name          The name of the parameter.
     * @param value         The value of the parameter argument.
     * @param defaultValue  The default value.
     * @param paramType     The parameter type.
     */
    public CommandArgument (String name, String value, String defaultValue, ParameterType paramType) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(paramType);

        _name = name;
        _value = value;
        _defaultValue = defaultValue;
        _paramType = paramType;

        if (paramType == ParameterType.STATIC) {
            _argType = value != null
                    ? ArgumentType.DEFINED_VALUE
                    : ArgumentType.DEFAULT_VALUE;
        }
        else if (paramType == ParameterType.FLOATING) {
            _argType = value != null
                    ? ArgumentType.DEFINED_VALUE
                    : ArgumentType.UNDEFINED;
        }
        else {
            _argType = ArgumentType.UNDEFINED;
        }
    }

    /**
     * Constructor.
     *
     * @param name          The name of the parameter.
     * @param value         The value of the parameter argument.
     * @param defaultValue  The default value.
     * @param paramType     The parameter type.
     * @param argType       The argument type.
     */
    public CommandArgument (String name, String value, String defaultValue, 
                            ParameterType paramType, ArgumentType argType) {
        
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(paramType);
        PreCon.notNull(argType);
        
        _name = name;
        _value = value;
        _defaultValue = defaultValue;
        _paramType = paramType;
        _argType = argType;
    }
    
    /**
     * Get the predefined name of the parameter.
     * @return
     */
    public String getParameterName() {
        return _name;
    }
    
    /**
     * Get the type of parameter 
     */
    public ParameterType getParameterType () {
        return _paramType;
    }

    /**
     * Get the argument type
     */
    public ArgumentType getArgumentType () {
        return _argType;
    }
    
    /**
     * Get the supplied value
     */
    public String getValue() {
        return _value;
    }
    
    /**
     * Get the default value for the parameter, if any.
     */
    public String getDefaultValue() {
        return _defaultValue;
    }
    
    @Override
    public int hashCode() {
        return _name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CommandArgument &&
               ((CommandArgument) obj)._name.equals(_name);
    }
}
