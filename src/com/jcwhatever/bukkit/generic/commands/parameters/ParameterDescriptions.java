/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.jcwhatever.bukkit.generic.commands.parameters;

import com.jcwhatever.bukkit.generic.commands.CommandInfoContainer;
import com.jcwhatever.bukkit.generic.commands.arguments.ArgumentValueType;
import com.jcwhatever.bukkit.generic.language.Localized;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Parses parameter description from a commands info annotation
 * and makes them available.
 */
public class ParameterDescriptions {

    private CommandInfoContainer _commandInfo;
    private Map<String, ParameterDescription> _descriptionMap = null;

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
    public ParameterDescription get(String parameterName) {
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
    public ParameterDescription get(String parameterName, ArgumentValueType valueType, Object... params) {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(valueType);

        ParameterDescription description = get(parameterName);
        if (description != null)
            return description;

        return new ParameterDescription(parameterName,
                ArgumentValueType.getDescription(parameterName, valueType, params));
    }

    /**
     * Get a description of a parameter by name for an enum value type.
     *
     * @param parameterName  The name of the parameter.
     * @param enumClass      The enum class.
     *
     * @param <T>  The enum type.
     */
    public <T extends Enum<T>> ParameterDescription get(String parameterName, Class<T> enumClass) {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(enumClass);

        ParameterDescription description = get(parameterName);
        if (description != null)
            return description;

        return new ParameterDescription(parameterName, ArgumentValueType.getEnumDescription(enumClass));
    }

    /**
     * Get a description of an enum type parameter by name.
     *
     * @param parameterName    The name of the parameter.
     * @param validEnumValues  The valid enum values.
     *
     * @param <T>  The enum type.
     */
    public <T extends Enum<T>> ParameterDescription get(String parameterName, T[] validEnumValues) {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(validEnumValues);

        ParameterDescription description = get(parameterName);
        if (description != null)
            return description;

        return new ParameterDescription(parameterName, ArgumentValueType.getEnumDescription(validEnumValues));
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
    public <T extends Enum<T>> ParameterDescription get(String parameterName, Collection<T> validEnumValues) {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(validEnumValues);

        ParameterDescription description = get(parameterName);
        if (description != null)
            return description;

        return new ParameterDescription(parameterName, ArgumentValueType.getEnumDescription(validEnumValues));
    }


    private void parseDescriptions() {
        if (_descriptionMap != null)
            return;

        String[] descriptions = _commandInfo.getRawParamDescriptions();
        _descriptionMap = new HashMap<>(descriptions.length);

        for (String desc : descriptions) {

            ParameterDescription description = new ParameterDescription(_commandInfo, desc);

            _descriptionMap.put(description.getParameterName(), description);
        }
    }
}
