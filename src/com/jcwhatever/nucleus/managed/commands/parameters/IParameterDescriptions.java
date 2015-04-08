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

package com.jcwhatever.nucleus.managed.commands.parameters;

import com.jcwhatever.nucleus.managed.commands.IRegisteredCommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ArgumentValueType;
import com.jcwhatever.nucleus.managed.language.Localized;
import com.jcwhatever.nucleus.mixins.IPluginOwned;

import java.util.Collection;
import javax.annotation.Nullable;

/**
 * Interface for a commands parameter descriptions.
 */
public interface IParameterDescriptions extends IPluginOwned {

    /**
     * Determine if there are descriptions available.
     */
    boolean isEmpty();

    /**
     * Get the command info used to get the parameter descriptions.
     */
    IRegisteredCommandInfo getCommandInfo();

    /**
     * Get a description by parameter name.
     *
     * @param parameterName  The name of the parameter.
     */
    @Nullable
    @Localized
    IParameterDescription get(String parameterName);

    /**
     * Get a description by parameter name.
     *
     * <p>The provided value type is used to get an description if
     * no description is provided by the command info.</p>
     *
     * @param parameterName  The name of the parameter.
     * @param valueType      The expected value type of the parameter.
     */
    IParameterDescription get(String parameterName, ArgumentValueType valueType, Object... params);

    /**
     * Get a description of a parameter by name for an enum value type.
     *
     * @param parameterName  The name of the parameter.
     * @param enumClass      The enum class.
     *
     * @param <T>  The enum type.
     */
    <T extends Enum<T>> IParameterDescription get(String parameterName, Class<T> enumClass);

    /**
     * Get a description of an enum type parameter by name.
     *
     * @param parameterName    The name of the parameter.
     * @param validEnumValues  The valid enum values.
     *
     * @param <T>  The enum type.
     */
    <T extends Enum<T>> IParameterDescription get(String parameterName, T[] validEnumValues);

    /**
     * Get a description of an enum type parameter by name.
     *
     * @param parameterName    The name of the parameter.
     * @param validEnumValues  The valid enum values.
     *
     * @param <T>  The enum type.
     */
    @Localized
    <T extends Enum<T>> IParameterDescription get(String parameterName, Collection<T> validEnumValues);
}
