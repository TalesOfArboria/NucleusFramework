package com.jcwhatever.bukkit.generic.commands.arguments;

import com.jcwhatever.bukkit.generic.commands.CommandInfoContainer;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidParameterDescriptionException;
import com.jcwhatever.bukkit.generic.language.Localized;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.TextUtils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses parameter description from a commands info annotation
 * and makes them available.
 */
public class ParameterDescriptions {
        
    private CommandInfoContainer _commandInfo;
    private Map<String, String> _descriptionMap = null;

    /**
     * Constructor.
     *
     * @param commandInfo  The container for the commands info annotation.
     */
    public ParameterDescriptions(CommandInfoContainer commandInfo) {
        _commandInfo = commandInfo;
    }

    /**
     * Get the command info used to get the parameter descriptions.
     */
    public CommandInfoContainer getCommandInfo() {
        return _commandInfo;
    }

    /**
     * Get a description by parameter name.
     *
     * @param parameterName  The name of the parameter.
     */
    @Nullable
    @Localized
    public String get(String parameterName) {
        PreCon.notNullOrEmpty(parameterName);
        
        parseDescriptions();
        
        return _descriptionMap.get(parameterName);
    }

    /**
     * Get a description by parameter name.
     *
     * <p>The provided value type is used to get an description if
     * no description is provided by the command info.</p>
     *
     * @param parameterName  The name of the parameter.
     * @param valueType      The expected value type of the parameter.
     */
    @Localized
    public String get(String parameterName, ArgumentValueType valueType) {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(valueType);
        
        String description = get(parameterName);
        if (description != null)
            return description;
        
        return ArgumentValueType.getDescription(parameterName, valueType);
    }

    /**
     * Get a description by parameter name.
     *
     * @param parameterName  The name of the parameter.
     * @param maxNameLength  The max length of the argument as a name.
     */
    @Localized
    public String get(String parameterName, int maxNameLength) {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.positiveNumber(maxNameLength);
        
        String description = get(parameterName);
        if (description != null)
            return description;
        
        return ArgumentValueType.getNameDescription(maxNameLength);
    }

    /**
     * Get a description of a parameter by name for an enum value type.
     *
     * @param parameterName  The name of the parameter.
     * @param enumClass      The enum class.
     *
     * @param <T>  The enum type.
     */
    @Localized
    public <T extends Enum<T>> String get(String parameterName, Class<T> enumClass) {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(enumClass);
        
        String description = get(parameterName);
        if (description != null)
            return description;

        return ArgumentValueType.getEnumDescription(enumClass);
    }

    /**
     * Get a description of an enum type parameter by name.
     *
     * @param parameterName    The name of the parameter.
     * @param validEnumValues  The valid enum values.
     *
     * @param <T>  The enum type.
     */
    @Localized
    public <T extends Enum<T>> String get(String parameterName, T[] validEnumValues) {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(validEnumValues);
        
        String description = get(parameterName);
        if (description != null)
            return description;

        return ArgumentValueType.getEnumDescription(validEnumValues);
    }

    /**
     * Get a description of an enum type parameter by name.
     *
     * @param parameterName    The name of the parameter.
     * @param validEnumValues  The valid enum values.
     *
     * @param <T>  The enum type.
     */
    @Localized
    public <T extends Enum<T>> String get(String parameterName, Collection<T> validEnumValues) {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(validEnumValues);
        
        String description = get(parameterName);
        if (description != null)
            return description;
        
        return ArgumentValueType.getEnumDescription(validEnumValues);
    }
    

    private void parseDescriptions() {
        if (_descriptionMap != null)
            return;
        
        String[] descriptions = _commandInfo.getParamDescriptions();
        _descriptionMap = new HashMap<String, String>(descriptions.length);

        for (String desc : descriptions) {
            String[] descComp =  TextUtils.PATTERN_EQUALS.split(desc, -1);
            
            String parameterName = descComp[0].trim();
            
            if (descComp.length < 2) {
                throw new InvalidParameterDescriptionException(_commandInfo, descComp[0]);
            }
            
            String description = TextUtils.concat(1, descComp, "=");
            
            _descriptionMap.put(parameterName, description);
        }
    }
}
