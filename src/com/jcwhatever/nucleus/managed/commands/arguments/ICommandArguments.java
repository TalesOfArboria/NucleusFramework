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

import com.jcwhatever.nucleus.managed.commands.exceptions.DuplicateArgumentException;
import com.jcwhatever.nucleus.managed.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.nucleus.managed.commands.exceptions.InvalidParameterException;
import com.jcwhatever.nucleus.managed.commands.exceptions.TooManyArgsException;
import com.jcwhatever.nucleus.mixins.IPluginOwned;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;

/**
 * Processes command arguments.
 *
 * <p>Commands can request parameters by name and have them parsed into the
 * expected object type at the same time. If the parameter has no provided
 * value, cannot be parsed or required conditions for parsing the object are
 * not met, an {@link InvalidArgumentException} is thrown and should be caught
 * by the command dispatcher.</p>
 *
 * <p>Other exceptions thrown are {@link DuplicateArgumentException},
 * {@link InvalidParameterException} and {@link TooManyArgsException}.</p>
 */
public interface ICommandArguments extends IPluginOwned, Iterable<ICommandArgument> {

    /**
     * Get the raw unparsed arguments.
     */
    String[] getRawArguments();

    /**
     * The number of static arguments in the collection.
     *
     * <p>Does not include the number of floating arguments.</p>
     */
    int staticSize();

    /**
     * The number of floating arguments in the collection.
     */
    int floatingSize ();

    /**
     * Get a {@link ICommandArgument} by parameter name
     *
     * @param parameterName  The name of the parameter
     */
    @Nullable
    ICommandArgument get(String parameterName);

    /**
     * Get an iterator which iterates over the arguments provided by the
     * command sender.
     *
     * <p>Optional static parameters are included even if not provided.</p>
     */
    @Override
    Iterator<ICommandArgument> iterator();

    /**
     * Get an argument as a {@link String} and ensures it meets proper
     * naming conventions.
     *
     * <p>The name must be alphanumeric characters only, must not start
     * with a number, no spaces, underscores are allowed. Must be no more
     * than 16 characters in length.</p>
     *
     * @param parameterName  The name of the parameter to get
     *
     * @throws InvalidArgumentException  If the argument for the parameter
     * is not a valid name.
     */
    String getName(String parameterName) throws InvalidArgumentException;

    /**
     * Get an argument as a {@link String} and ensures it meets proper naming
     * conventions.
     *
     * <p>The name must be alphanumeric characters only, must not start with
     * a number, no spaces, underscores are allowed. </p>
     *
     * @param parameterName  The name of the arguments parameter
     * @param maxLen         The maximum length of the value
     *
     * @throws InvalidArgumentException  If the argument for the parameter is
     * not a valid name.
     */
    String getName(String parameterName, int maxLen) throws InvalidArgumentException;

    /**
     * Get an argument as a string.
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws InvalidArgumentException  If the argument is not present or is
     * not an expected value.
     */
    String getString(String parameterName) throws InvalidArgumentException;

    /**
     * Gets an argument as a boolean.
     *
     * <p>true,on,yes = true</p>
     * <p>false,off,no = false</p>
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws InvalidArgumentException  If the argument cannot be parsed or
     * recognized as a boolean.
     */
    boolean getBoolean(String parameterName) throws InvalidArgumentException;

    /**
     * Get an argument as a character.
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws InvalidArgumentException  If the argument is not present, is
     * not an expected value, or is more than a single character.
     */
    char getChar(String parameterName) throws InvalidArgumentException;

    /**
     * Gets an argument as a byte.
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws InvalidArgumentException  If the argument is not parsable into
     * a byte value.
     */
    byte getByte(String parameterName) throws InvalidArgumentException;

    /**
     * Gets an argument as a byte.
     *
     * @param parameterName  The name of the arguments parameter
     * @param minRange       The minimum value.
     * @param maxRange       The maximum value.
     *
     * @throws InvalidArgumentException
     */
    byte getByte(String parameterName, byte minRange, byte maxRange)
            throws InvalidArgumentException;

    /**
     * Gets an argument as a short.
     *
     * @param parameterName  The name of the arguments parameter.
     *
     * @throws InvalidArgumentException  If the argument is not parsable into
     * a short value.
     */
    short getShort(String parameterName) throws InvalidArgumentException;

    /**
     * Gets an argument as a short.
     *
     * @param parameterName  The name of the arguments parameter.
     * @param minRange       The minimum value.
     * @param maxRange       The maximum value.
     *
     * @throws InvalidArgumentException  If the argument is not parsable into
     * a short value or does not meet range specs.
     */
    short getShort(String parameterName, short minRange, short maxRange)
            throws InvalidArgumentException;

    /**
     * Gets an argument as an integer.
     *
     * @param parameterName  The name of the arguments parameter.
     *
     * @throws InvalidArgumentException  If the argument is not parsable into
     * an integer.
     */
    int getInteger(String parameterName) throws InvalidArgumentException;

    /**
     * Gets an argument as an integer.
     *
     * @param parameterName  The name of the arguments parameter.
     * @param minRange       The minimum value.
     * @param maxRange       The maximum value.
     *
     * @throws InvalidArgumentException  If the argument is not parsable into
     * an integer or does not meet range specs.
     */
    int getInteger(String parameterName, int minRange, int maxRange)
            throws InvalidArgumentException;

    /**
     * Gets an argument as a 64 bit number.
     *
     * @param parameterName  The name of the arguments parameter.
     *
     * @throws InvalidArgumentException  If the argument is not parsable into
     * a long value.
     */
    long getLong(String parameterName) throws InvalidArgumentException;

    /**
     * Gets an argument as a 64 bit number.
     *
     * @param parameterName  The name of the arguments parameter.
     * @param minRange       The minimum value.
     * @param maxRange       The maximum value.
     *
     * @throws InvalidArgumentException  If the argument is not parsable into
     * a long value or does not meet range specs.
     */
    long getLong(String parameterName, long minRange, long maxRange)
            throws InvalidArgumentException;

    /**
     * Gets an argument as a float.
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws InvalidArgumentException  If the argument is not parsable into
     * a float value.
     */
    float getFloat(String parameterName) throws InvalidArgumentException;

    /**
     * Gets an argument as a float.
     *
     * @param parameterName  The name of the arguments parameter.
     * @param minRange       The minimum value.
     * @param maxRange       The maximum value.
     *
     * @throws InvalidArgumentException  If the argument is not parsable into
     * a float or does not meet range specs.
     */
    float getFloat(String parameterName, float minRange, float maxRange)
            throws InvalidArgumentException;

    /**
     * Gets an argument as a double.
     *
     * @param parameterName  The name of the arguments parameter.
     *
     * @throws InvalidArgumentException  If the argument is not parsable into
     * a double value.
     */
    double getDouble(String parameterName) throws InvalidArgumentException;

    /**
     * Gets an argument as a double.
     *
     * @param parameterName  The name of the arguments parameter.
     * @param minRange       The minimum value.
     * @param maxRange       The maximum value.
     *
     * @throws InvalidArgumentException  If the argument is not parsable into a
     * double value or does not meet range specs.
     */
    double getDouble(String parameterName, double minRange, double maxRange)
            throws InvalidArgumentException;

    /**
     * Gets an argument as a double.
     *
     * <p>"%" characters are ignored.</p>
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws InvalidArgumentException  If the argument cannot be parsed or
     * recognizes as a double.
     */
    double getPercent(String parameterName) throws InvalidArgumentException;

    /**
     * Gets an arguments raw {@link String} value, splits it at the space
     * character and returns it as a {@link String[]}
     *
     * @param parameterName  The name of the arguments parameter
     */
    String[] getParams(String parameterName) throws InvalidArgumentException;

    /**
     * Gets an argument as an {@link ItemStack[]}.
     *
     * <p>The supplied argument can be a parsable string representing an
     * {@link ItemStack}</p>
     *
     * <p>The supplied argument can also be "inhand" for the stack in the
     * players hand, "inventory" to return all items in the players inventory,
     * or "hotbar" to return all items in the players hotbar. All items returned
     * from the player are cloned objects.</p>
     *
     * <p>Use getString(parameterName) method on the same parameter to determine
     * if the player typed "inhand", "chest", or "hotbar" if that information is
     * needed.</p>
     *
     * <p>If the command sender is not a player, and therefore has no chest, the
     * argument will only be valid if a parsable item stack string was provided.</p>
     *
     * @param sender         The {@link CommandSender} who executed the command
     * @param parameterName  The name of the arguments parameter
     *
     * @throws InvalidArgumentException If the argument is not a recognized keyword
     * and cannot be parsed to an {@link ItemStack}.
     */
    ItemStack[] getItemStack(@Nullable CommandSender sender, String parameterName)
            throws InvalidArgumentException;

    /**
     * Gets an argument as a location.
     *
     * <p>Possible values are "current" or "select"</p>
     *
     * <p>If the argument value is "current", the players current location is
     * returned via the {@link ILocationHandler}.</p>
     *
     * <p>If the argument value is "select", the player is asked to click on the
     * location to be selected and the value is return via the
     * {@link ILocationHandler}.</p>
     *
     * <p>If the {@link CommandSender} is not a player, the argument is always
     * considered invalid.</p>
     *
     * @param sender           The {@link CommandSender} who executed the command
     * @param parameterName    The name of the arguments parameter
     * @param locationHandler  The {@link ILocationHandler} responsible for dealing
     *                         with the return location.
     *
     * @throws InvalidArgumentException If the sender is not a player, or the
     * argument is not "current" or "select"
     */
    void getLocation (CommandSender sender,
                             String parameterName,
                             ILocationHandler locationHandler)
            throws InvalidArgumentException;

    /**
     * Gets an argument as an enum.
     *
     * <p>The argument must be the name of the enum constant and is not case
     * sensitive. The enum should use proper naming conventions by having
     * all constant names in upper case.</p>
     *
     * @param parameterName  The name of the arguments parameter
     * @param enumClass      The enums class
     *
     * @throws InvalidArgumentException  If the argument is not the name of one
     * of the enums constants.
     */
    <T extends Enum<T>> T getEnum(String parameterName,  Class<T> enumClass)
            throws InvalidArgumentException;

    /**
     * Gets an argument as an enum.
     *
     * <p>The argument must be the name of the enum constant and is not case
     * sensitive. The enum should use proper naming conventions by having all
     * constant names in upper case.</p>
     *
     * <p>Valid values can be specified to prevent all of an enums constants
     * from being valid. Use if you have no control over the enum and it isn't
     * practical to make a new enum.</p>
     *
     * @param parameterName  The name of the arguments parameter
     * @param enumClass      The enums class
     * @param validValues    an array of valid enum constants
     *
     * @throws InvalidArgumentException If the argument is not the name of one of
     * the valid enum constants.
     */
    <T extends Enum<T>> T getEnum(
            String parameterName,  Class<T> enumClass, T[] validValues)
            throws InvalidArgumentException;

    /**
     * Gets an argument as an enum.
     *
     * <p>The argument must be the name of the enum constant and is not case
     * sensitive. The enum should use proper naming conventions by having all
     * constant names in upper case.</p>
     *
     * <p>Valid values can be specified to prevent all of an enums constants
     * from being valid. Use if you have no control over the enum and it isn't
     * practical to make a new enum.</p>
     *
     * @param parameterName  The name of the arguments parameter
     * @param enumClass      The enums class
     * @param validValues    A collection of valid enum constants
     *
     * @throws InvalidArgumentException  If the argument is not one of the valid
     * enum constants.
     */
    <T extends Enum<T>> T getEnum(
            String parameterName, Class<T> enumClass, Collection<T> validValues)
            throws InvalidArgumentException;

    /**
     * Determine if an argument is provided and can be used as a boolean value.
     *
     * @param parameterName  The name of the arguments parameter
     */
    boolean hasBoolean(String parameterName);

    /**
     * Determine if an argument is provided and is a single character.
     *
     * @param parameterName  The name of the arguments parameter
     */
    boolean hasChar(String parameterName);

    /**
     * Determine if an argument is provided and can be used as a byte value.
     *
     * @param parameterName  The name of the arguments parameter
     */
    boolean hasByte(String parameterName);

    /**
     * Determine if an argument is provided and can be used as a short value.
     *
     * @param parameterName  The name of the arguments parameter
     */
    boolean hasShort(String parameterName);

    /**
     * Determine if an argument is provided and can be used as an integer value.
     *
     * @param parameterName  The name of the arguments parameter
     */
    boolean hasInteger(String parameterName);

    /**
     * Determine if an argument is provided and can be used as a float value.
     *
     * @param parameterName  The name of the arguments parameter
     */
    boolean hasFloat(String parameterName);

    /**
     * Determine if an argument is provided and can be used as a double value.
     *
     * @param parameterName  The name of the arguments parameter
     */
    boolean hasDouble(String parameterName);

    /**
     * Determine if an argument is provided and can be used as an item stack array.
     *
     * @param parameterName  The name of the arguments parameter
     */
    boolean hasItemStack(String parameterName);

    /**
     * Determine if an argument is provided and can be used as a double value.
     *
     * <p>A '%' character in the value will not invalidate the argument.</p>
     *
     * @param parameterName  The name of the arguments parameter
     */
    boolean hasPercent(String parameterName);

    /**
     * Determine if an argument is provided and can be used as an enum.
     *
     * @param parameterName  The name of the arguments parameter
     * @param enumClass      The enum class the argument must be used as
     */
    <T extends Enum<T>> boolean hasEnum(String parameterName, Class<T> enumClass);

    /**
     * Determine if the value of an argument was omitted and
     * the default value was inserted.
     *
     * @param parameterName  The name of the arguments parameter.
     */
    boolean isDefaultValue(String parameterName);
}
