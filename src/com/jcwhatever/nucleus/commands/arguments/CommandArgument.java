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


package com.jcwhatever.nucleus.commands.arguments;

import com.jcwhatever.nucleus.commands.parameters.CommandParameter;
import com.jcwhatever.nucleus.utils.PreCon;

import javax.annotation.Nullable;

/**
 * A data storage object that represents
 * a single supplied parameter to a command.
 */
public class CommandArgument {

    private final CommandParameter _parameter;
    private final String _argument;
    private final boolean _isDefaultValue;

    /**
     * Constructor.
     *
     * @param parameter     The parameter the argument is for.
     * @param argument      The argument.
     */
    public CommandArgument (CommandParameter parameter, @Nullable String argument) {
        PreCon.notNull(parameter);

        _isDefaultValue = argument == null;

        _parameter = parameter;
        _argument = _isDefaultValue ? parameter.getDefaultValue() : argument;
    }

    /**
     * Get the predefined name of the parameter.
     */
    public String getParameterName() {
        return _parameter.getName();
    }

    /**
     * Get the supplied value.
     */
    @Nullable
    public String getValue() {
        return _argument;
    }

    /**
     * Determine if the default value was used.
     */
    public boolean isDefaultValue() {
        return _isDefaultValue;
    }

    @Override
    public final int hashCode() {
        return _parameter.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return obj instanceof CommandArgument &&
               ((CommandArgument) obj)._parameter.equals(_parameter);
    }
}
