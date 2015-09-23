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


package com.jcwhatever.nucleus.managed.commands.arguments;

import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.language.Localized;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Used as an enum with localizable value type descriptions.
 *
 * <p>Used to display information about a value type for a command
 * argument.</p>
 */
public class ArgumentValueType {

    @Localizable static final String _UNKNOWN =
            "Unspecified. Consult documentation, if any.";

    @Localizable static final String _NAME =
            "Name. The name must be alphanumeric characters only, must not start with a number, " +
            "no spaces, underscores are allowed. Should not be more than {0} characters.";

    @Localizable static final String _STRING =
            "Text";

    @Localizable static final String _CHARACTER =
            "Single character";

    @Localizable static final String _BYTE =
            "Whole number no smaller than {0} and no larger than {1}.";

    @Localizable static final String _SHORT =
            "Whole number no smaller than {0} and no larger than {1}.";

    @Localizable static final String _INTEGER =
            "Whole number no smaller than {0} and no larger than {1}.";

    @Localizable static final String _LONG =
            "Whole number no smaller than {0} and no larger than {1}.";

    @Localizable static final String _FLOAT =
            "Number with or without a decimal point. Must be no smaller than {0} and no larger than {1}.";

    @Localizable static final String _DOUBLE =
            "Number with or without a decimal point. Must be no smaller than {0} and no larger than {1}.";

    @Localizable static final String _BOOLEAN =
            "Boolean. i.e. true, false, yes, no, on, off";

    @Localizable static final String _PERCENT =
            "Percentage. i.e 32.2%";

    @Localizable static final String _ITEMSTACK =
            "Use 'inhand' for the item in your hand. 'inventory' for all items in your inventory. " +
            "'hotbar' for items in your hotbar. Parsable text can also be provided in the following " +
                    "format: <materialName>[:<data>][;<quantity>]";

    @Localizable static final String _LOCATION =
            "Use 'current' to provide you current location. 'select' will allow you " +
            "to click on a block to provide its location.";

    @Localizable static final String _ENUM =
            "Unspecified enumeration constant. Consult documentation, if any.";

    @Localizable static final String _USE_ONE_OF =
            "Use one of the following values: {0}";

    public static final ArgumentValueType UNKNOWN = new ArgumentValueType(_UNKNOWN);
    public static final ArgumentValueType NAME = new ArgumentValueType(_NAME);
    public static final ArgumentValueType STRING = new ArgumentValueType(_STRING);
    public static final ArgumentValueType CHARACTER = new ArgumentValueType(_CHARACTER);
    public static final ArgumentValueType BYTE = new ArgumentValueType(_BYTE);
    public static final ArgumentValueType SHORT = new ArgumentValueType(_SHORT);
    public static final ArgumentValueType INTEGER = new ArgumentValueType(_INTEGER);
    public static final ArgumentValueType LONG = new ArgumentValueType(_LONG);
    public static final ArgumentValueType FLOAT = new ArgumentValueType(_FLOAT);
    public static final ArgumentValueType DOUBLE = new ArgumentValueType(_DOUBLE);
    public static final ArgumentValueType BOOLEAN = new ArgumentValueType(_BOOLEAN);
    public static final ArgumentValueType PERCENT = new ArgumentValueType(_PERCENT);
    public static final ArgumentValueType ITEMSTACK = new ArgumentValueType(_ITEMSTACK);
    public static final ArgumentValueType LOCATION = new ArgumentValueType(_LOCATION);
    public static final ArgumentValueType ENUM = new ArgumentValueType(_ENUM);

    private final String _description;

    /**
     * Constructor.
     *
     * @param description  The description of the value type.
     */
    private ArgumentValueType(String description) {
        _description = description;
    }

    /**
     * Get a value type description.
     *
     * @param argumentValueType  The value type to get a description for.
     */
    @Localized
    public static String getDescription(ArgumentValueType argumentValueType) {
        return getDescription(null, argumentValueType);
    }

    /**
     * Get a value type description.
     *
     * @param parameterName      The name of the command parameter the argument is for.
     * @param argumentValueType  The argument value type.
     */
    @Localized
    public static String getDescription(
            @Nullable String parameterName, ArgumentValueType argumentValueType, Object... params) {

        if (argumentValueType == CHARACTER || argumentValueType == STRING) {
            if (parameterName != null && parameterName.contains("|")) {

                String values = TextUtils.concat(TextUtils.PATTERN_PIPE.split(parameterName), ", ");
                return NucLang.get(_USE_ONE_OF, values).toString();
            }
            else {
                return NucLang.get(argumentValueType._description, params).toString();
            }
        }
        else if (argumentValueType == ENUM) {
            if (parameterName == null || !parameterName.contains("|"))
                return argumentValueType._description;
            else {
                String values = TextUtils.concat(TextUtils.PATTERN_PIPE.split(parameterName), ", ");
                return NucLang.get(_USE_ONE_OF, values).toString();
            }
        }
        else {
            return NucLang.get(argumentValueType._description, params).toString();
        }
    }

    /**
     * Get a value type description for an enum.
     *
     * @param enumClass  The enum.
     *
     * @param <T> The enum type.
     */
    @Localized
    public static <T extends Enum<T>> String getEnumDescription(Class<T> enumClass) {
        Enum<?>[] constants = enumClass.getEnumConstants();
        String values = TextUtils.concat(constants, ", ");
        return NucLang.get(_USE_ONE_OF, values).toString();
    }

    /**
     * Get a value type description for an enum.
     *
     * @param validValues  Valid enum values.
     *
     * @param <T> The enum type.
     */
    @Localized
    public static <T extends Enum<T>> String getEnumDescription(T[] validValues) {
        String values = TextUtils.concat(validValues, ", ");
        return NucLang.get(_USE_ONE_OF, values).toString();
    }

    /**
     * Get a value type description for an enum.
     *
     * @param validValues  Valid enum values.
     *
     * @param <T> The enum type.
     */
    @Localized
    public static <T extends Enum<T>> String getEnumDescription(Collection<T> validValues) {
        String values = TextUtils.concat(validValues, ", ");
        return NucLang.get(_USE_ONE_OF, values).toString();
    }
}
