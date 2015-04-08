/*
 * This file is part of NucleusFramework for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.nucleus.internal.managed.commands;

import com.jcwhatever.nucleus.managed.commands.IRegisteredCommand;
import com.jcwhatever.nucleus.managed.commands.IRegisteredCommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ArgumentValueType;
import com.jcwhatever.nucleus.managed.commands.parameters.IParameterDescriptions;
import com.jcwhatever.nucleus.managed.language.Localized;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Parses parameter description from a commands info annotation and makes them
 * available.
 */
class ParameterDescriptions implements IParameterDescriptions {

    private final IRegisteredCommand _command;
    private final IRegisteredCommandInfo _commandInfo;
    private Map<String, ParameterDescription> _descriptionMap = null;

    /**
     * Constructor.
     *
     * @param command  The command to get parameter descriptions from..
     */
    public ParameterDescriptions(IRegisteredCommand command) {
        PreCon.notNull(command);

        _command = command;
        _commandInfo = command.getInfo();

        parseDescriptions();
    }

    @Override
    public Plugin getPlugin() {
        return _command.getPlugin();
    }

    @Override
    public boolean isEmpty() {
        return _descriptionMap.isEmpty();
    }

    @Override
    public IRegisteredCommandInfo getCommandInfo() {
        return _commandInfo;
    }

    @Override
    @Nullable
    @Localized
    public ParameterDescription get(String parameterName) {
        PreCon.notNullOrEmpty(parameterName);

        parseDescriptions();

        return _descriptionMap.get(parameterName);
    }

    @Override
    public ParameterDescription get(String parameterName, ArgumentValueType valueType, Object... params) {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(valueType);

        ParameterDescription description = get(parameterName);
        if (description != null)
            return description;

        return new ParameterDescription(_command, parameterName,
                ArgumentValueType.getDescription(parameterName, valueType, params));
    }

    @Override
    public <T extends Enum<T>> ParameterDescription get(String parameterName, Class<T> enumClass) {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(enumClass);

        ParameterDescription description = get(parameterName);
        if (description != null)
            return description;

        return new ParameterDescription(_command, parameterName,
                ArgumentValueType.getEnumDescription(enumClass));
    }

    @Override
    public <T extends Enum<T>> ParameterDescription get(String parameterName, T[] validEnumValues) {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(validEnumValues);

        ParameterDescription description = get(parameterName);
        if (description != null)
            return description;

        return new ParameterDescription(_command, parameterName,
                ArgumentValueType.getEnumDescription(validEnumValues));
    }

    @Override
    @Localized
    public <T extends Enum<T>> ParameterDescription get(String parameterName, Collection<T> validEnumValues) {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(validEnumValues);

        ParameterDescription description = get(parameterName);
        if (description != null)
            return description;

        return new ParameterDescription(_command, parameterName,
                ArgumentValueType.getEnumDescription(validEnumValues));
    }


    private void parseDescriptions() {
        if (_descriptionMap != null)
            return;

        String[] descriptions = _commandInfo.getRawParamDescriptions();
        _descriptionMap = new HashMap<>(descriptions.length);

        for (String desc : descriptions) {

            ParameterDescription description = new ParameterDescription(_command, desc);

            _descriptionMap.put(description.getName(), description);
        }
    }
}
