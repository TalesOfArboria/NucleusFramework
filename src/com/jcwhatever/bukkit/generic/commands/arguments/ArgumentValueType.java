package com.jcwhatever.bukkit.generic.commands.arguments;

import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.language.Localized;
import com.jcwhatever.bukkit.generic.utils.TextUtils;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Used as an enum with localizable value type descriptions.
 *
 * <p>Used to display information about a value type for a command
 * argument.</p>
 */
public class ArgumentValueType {

    @Localizable static final String _UNKNOWN = "Unspecified. Consult documentation, if any.";
    @Localizable static final String _NAME = "Name. The name must be alphanumeric characters only, must not start with a number, " +
            "no spaces, underscores are allowed. Should not be more than 16 characters unless otherwise specified.";
    @Localizable static final String _NAME_CUSTOM = "Name. The name must be alphanumeric characters only, must not start with a number, " +
            "no spaces, underscores are allowed. Should not be more than {0} characters.";
    @Localizable static final String _STRING = "Text";
    @Localizable static final String _CHARACTER = "Single character";
    @Localizable static final String _BYTE = "Whole number no larger than " + Byte.MAX_VALUE + '.';
    @Localizable static final String _SHORT = "Whole number no larger than " + Short.MAX_VALUE + '.';
    @Localizable static final String _INTEGER = "Whole number no larger than " + Integer.MAX_VALUE + '.';
    @Localizable static final String _LONG = "Whole number no larger than " + Long.MAX_VALUE + '.';
    @Localizable static final String _FLOAT = "Number with or without a decimal point.";
    @Localizable static final String _DOUBLE = "Number with or without a decimal point.";
    @Localizable static final String _BOOLEAN = "Boolean. i.e. true, false, yes, no, on, off";
    @Localizable static final String _PERCENT = "Percentage. i.e 32.2%";
    @Localizable static final String _ITEMSTACK = "Item stack. Use 'inhand' for the item in your hand. " +
                                                  "'inventory' uses all items in your inventory. " +
                                                  "'hotbar' uses items in your hotbar. Parsable text " +
                                                  "can also be provided in the following format: " +
                                                  "<materialName>[:<data>][;<quantity>]";
    @Localizable static final String _LOCATION = "Use 'current' to provide you current location. 'select' will allow you " +
                                                 "to click on a block to provide it's location.";
    @Localizable static final String _ENUM = "Unspecified enumeration constant. Consult documentation, if any.";
    @Localizable static final String _USE_ONE_OF = "Use one of the following values: {0}";



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
    public static String getDescription(@Nullable String parameterName, ArgumentValueType argumentValueType) {

        if (argumentValueType == CHARACTER || argumentValueType == STRING) {
            if (parameterName != null && parameterName.contains("|")) {

                String values = TextUtils.concat(TextUtils.PATTERN_PIPE.split(parameterName), ", ");
                return Lang.get("Use one of the following values: {0}", values);
            }
            else {
                return Lang.get(argumentValueType._description);
            }
        }
        else if (argumentValueType == ENUM) {
            if (parameterName == null || !parameterName.contains("|"))
                return argumentValueType._description;
            else {
                String values = TextUtils.concat(TextUtils.PATTERN_PIPE.split(parameterName), ", ");
                return Lang.get("Use one of the following values: ", values);
            }
        }
        else {
            return Lang.get(argumentValueType._description);
        }
    }

    /**
     * Get a value type description for an enum.
     *
     * @param enumClass  The enum.
     * @param <T>        The enum type.
     */
    @Localized
    public static <T extends Enum<T>> String getEnumDescription(Class<T> enumClass) {
        Enum<?>[] constants = enumClass.getEnumConstants();
        String values = TextUtils.concat(constants, ", ");
        return Lang.get(_USE_ONE_OF, values);
    }

    /**
     * Get a value type description for an enum.
     *
     * @param validValues  Valid enum values.
     * @param <T>          The enum type.
     */
    @Localized
    public static <T extends Enum<T>> String getEnumDescription(T[] validValues) {
        String values = TextUtils.concat(validValues, ", ");
        return Lang.get(_USE_ONE_OF, values);
    }

    /**
     * Get a value type description for an enum.
     *
     * @param validValues  Valid enum values.
     * @param <T>          The enum type.
     */
    @Localized
    public static <T extends Enum<T>> String getEnumDescription(Collection<T> validValues) {
        String values = TextUtils.concat(validValues, ", ");
        return Lang.get(_USE_ONE_OF, values);
    }

    /**
     * Get a name value type description.
     *
     * @param maxLen  The max length of the name.
     */
    @Localized
    public static String getNameDescription(int maxLen) {
        return Lang.get(_NAME_CUSTOM, maxLen);
    }
}
