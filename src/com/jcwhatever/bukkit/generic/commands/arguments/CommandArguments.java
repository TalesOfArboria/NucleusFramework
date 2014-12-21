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


package com.jcwhatever.bukkit.generic.commands.arguments;

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.exceptions.DuplicateParameterException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidParameterException;
import com.jcwhatever.bukkit.generic.commands.exceptions.MissingArgumentException;
import com.jcwhatever.bukkit.generic.commands.exceptions.TooManyArgsException;
import com.jcwhatever.bukkit.generic.commands.parameters.ParameterDescriptions;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.items.ItemStackComparer;
import com.jcwhatever.bukkit.generic.items.ItemWrapper;
import com.jcwhatever.bukkit.generic.items.serializer.InvalidItemStackStringException;
import com.jcwhatever.bukkit.generic.messaging.IMessenger;
import com.jcwhatever.bukkit.generic.messaging.MessengerFactory;
import com.jcwhatever.bukkit.generic.player.PlayerBlockSelect;
import com.jcwhatever.bukkit.generic.player.PlayerBlockSelect.PlayerBlockSelectHandler;
import com.jcwhatever.bukkit.generic.utils.EnumUtils;
import com.jcwhatever.bukkit.generic.utils.ItemStackUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.text.TextUtils;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import javax.annotation.Nullable;

/**
 * Processes command arguments.
 *
 * <p>Commands can request parameters by name and have them parsed into the expected
 * object type at the same time. If the parameter has no provided value, cannot be parsed 
 * or required conditions for parsing the object are not met, an {@code InvalidValueException}
 * is thrown and should be caught by the command handler.</p>
 */
public class CommandArguments implements Iterable<CommandArgument> {

    private final Plugin _plugin;
    private final IMessenger _msg;
    private final ArgumentParseResults _parseResults;
    private final ParameterDescriptions _paramDescriptions;

    /**
     * Constructor. Validates while assuming no arguments are provided.
     *
     * @param plugin       The owning plugin.
     * @param command      The commands the arguments are being parsed for.
     *
     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException        If a value provided is not valid.
     * @throws DuplicateParameterException  If a parameter is defined in the arguments more than once.
     * @throws InvalidParameterException    If a parameter int the arguments is not found for the command.
     * @throws TooManyArgsException         If the provided arguments are more than is expected.
     */
    public CommandArguments(Plugin plugin, AbstractCommand command)
            throws InvalidArgumentException, DuplicateParameterException, InvalidParameterException,
            TooManyArgsException, MissingArgumentException {

        this(plugin, command, null);
    }

    /**
     * Constructor. Parses the provided arguments and validates against information provided
     * by the {@code CommandInfoContainer}.
     *
     * @param plugin   The owning plugin.
     * @param command  The commands info annotation container.
     * @param args     The command arguments.
     *
     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException        If a value provided is not valid.
     * @throws DuplicateParameterException  If a parameter is defined in the arguments more than once.
     * @throws InvalidParameterException    If a parameter int the arguments is not found for the command.
     * @throws TooManyArgsException         If the provided arguments are more than is expected.
     */
    public CommandArguments(Plugin plugin, AbstractCommand command, @Nullable String[] args)
            throws InvalidArgumentException, DuplicateParameterException, InvalidParameterException,
            TooManyArgsException, MissingArgumentException {

        PreCon.notNull(plugin);
        PreCon.notNull(command);

        _plugin = plugin;
        _msg = MessengerFactory.get(plugin);
        _paramDescriptions = command.getInfo().getParamDescriptions();


        // substitute empty string to remove the need for null checks while parsing
        if (args == null)
            args = ArrayUtils.EMPTY_STRING_ARRAY;

        // parse arguments
        _parseResults = new ArgumentParser().parse(command, args);
    }

    /**
     * Get the owning plugin.
     */
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * The number of static arguments in the collection.
     *
     * <p>Does not include the number of floating arguments.</p>
     */
    public int staticSize() {
        return _parseResults.getStaticArgs().size();
    }

    /**
     * The number of floating arguments in the collection.
     */
    public int floatingSize () {
        return _parseResults.getFloatingArgs().size();
    }

    /**
     * Get a {@code CommandArgument} by parameter name
     *
     * @param parameterName  The name of the parameter
     */
    @Nullable
    public CommandArgument get(String parameterName) {
        PreCon.notNullOrEmpty(parameterName);

        return _parseResults.getArgMap().get(parameterName);
    }

    /**
     * Get an iterator which iterates over the arguments
     * provided by the command sender. Optional static parameters
     * are included even if not provided.
     */
    @Override
    public Iterator<CommandArgument> iterator() {

        return new Iterator<CommandArgument>() {

            int index = 0;

            @Override
            public boolean hasNext () {
                return index < staticSize() + floatingSize();
            }

            @Override
            public CommandArgument next () {
                if (!hasNext())
                    throw new IndexOutOfBoundsException("No more elements.");



                CommandArgument arg = index < staticSize()
                        ? _parseResults.getStaticArgs().get(index)
                        : _parseResults.getFloatingArgs().get(index);

                index++;
                //noinspection ConstantConditions
                return arg;
            }

            @Override
            public void remove () {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Get an argument as a {@code String} and ensures it meets proper naming conventions.
     *
     * <p>The name must be alphanumeric characters only, must not start with a number,
     * no spaces, underscores are allowed. Must be no more than 16 characters in length.</p>
     *
     * @param parameterName  The name of the parameter to get
     *
     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException  If the argument for the parameter is not a valid name.
     */
    public String getName(String parameterName) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        return getName(parameterName, 16);
    }

    /**
     * Get an argument as a {@code String} and ensures it meets proper naming conventions.
     *
     * <p>The name must be alphanumeric characters only, must not start with a number,
     * no spaces, underscores are allowed. </p>
     *
     * @param parameterName  The name of the arguments parameter
     * @param maxLen         The maximum length of the value
     *
     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException  If the argument for the parameter is not a valid name.
     */
    public String getName(String parameterName, int maxLen) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.greaterThanZero(maxLen);

        // get the raw argument
        String arg = getString(parameterName);

        // make sure the argument is a valid name
        if (!TextUtils.isValidName(arg, maxLen)) {
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, ArgumentValueType.NAME, maxLen));
        }

        return arg;
    }

    /**
     * Get an argument as a string.
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException  If the argument is not present or is not an expected value.
     */
    public String getString(String parameterName) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument
        String value = getRawArgument(parameterName);
        if (value == null)
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, ArgumentValueType.STRING));

        // make sure if pipes are used, that the argument is one of the possible values
        if (parameterName.indexOf('|') != -1) {
            String[] options = TextUtils.PATTERN_PIPE.split(parameterName, 0);
            for (String option : options) {
                if (value.equalsIgnoreCase(option))
                    return option;
            }
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, ArgumentValueType.STRING));
        }

        return value;
    }


    /**
     * Get an argument as a character.
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException  If the argument is not present, is not an expected value, or is more than a single character.
     */
    public char getChar(String parameterName) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument
        String value = getRawArgument(parameterName);
        if (value == null)
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, ArgumentValueType.CHARACTER));

        // make sure if pipes are used, that the argument is one of the possible values
        if (parameterName.indexOf('|') != -1) {
            String[] options = StringUtils.split(parameterName, "|");
            for (String option : options) {
                if (value.equalsIgnoreCase(option)) {

                    if (option.length() != 1) {
                        throw new InvalidArgumentException(
                                _paramDescriptions.get(parameterName, ArgumentValueType.CHARACTER));
                    }

                    return option.charAt(0);
                }
            }
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, ArgumentValueType.CHARACTER));
        }

        // make sure the length of the value is exactly 1
        if (value.length() != 1)
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, ArgumentValueType.CHARACTER));

        return value.charAt(0);
    }

    /**
     * Gets an argument as a byte.
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException  If the argument is not parsable into a byte value.
     */
    public byte getByte(String parameterName) throws InvalidArgumentException {
        return getByte(parameterName, Byte.MIN_VALUE, Byte.MAX_VALUE);
    }

    /**
     * Gets an argument as a byte.
     *
     * @param parameterName  The name of the arguments parameter
     * @param minRange       The minimum value.
     * @param maxRange       The maximum value.
     *
     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException
     */
    public byte getByte(String parameterName, byte minRange, byte maxRange) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        byte result;

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // parse argument to byte
        try {
            //noinspection ConstantConditions
            result = Byte.parseByte(arg);
        }
        catch (NullPointerException | NumberFormatException nfe) {
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, ArgumentValueType.BYTE, minRange, maxRange));
        }

        if (result < minRange || result > maxRange) {
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, ArgumentValueType.BYTE, minRange, maxRange));
        }

        return result;
    }

    /**
     * Gets an argument as a short.
     *
     * @param parameterName  The name of the arguments parameter.
     *
     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException  If the argument is not parsable into a short value.
     */
    public short getShort(String parameterName) throws InvalidArgumentException {
        return getShort(parameterName, Short.MIN_VALUE, Short.MAX_VALUE);
    }

    /**
     * Gets an argument as a short.
     *
     * @param parameterName  The name of the arguments parameter.
     * @param minRange       The minimum value.
     * @param maxRange       The maximum value.
     *
     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException  If the argument is not parsable into a short value or does not meet range specs.
     */
    public short getShort(String parameterName, short minRange, short maxRange) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        short result;

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // parse argument to short
        try {
            //noinspection ConstantConditions
            result = Short.parseShort(arg);
        }
        catch (NullPointerException | NumberFormatException nfe) {
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, ArgumentValueType.SHORT, minRange, maxRange));
        }

        if (result < minRange || result > maxRange) {
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, ArgumentValueType.SHORT, minRange, maxRange));
        }

        return result;
    }

    /**
     * Gets an argument as an integer.
     *
     * @param parameterName  The name of the arguments parameter.
     *
     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException  If the argument is not parsable into an integer.
     */
    public int getInteger(String parameterName) throws InvalidArgumentException {
        return getInteger(parameterName, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Gets an argument as an integer.
     *
     * @param parameterName  The name of the arguments parameter.
     * @param minRange       The minimum value.
     * @param maxRange       The maximum value.
     *
     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException  If the argument is not parsable into an integer or does not meet range specs.
     */
    public int getInteger(String parameterName, int minRange, int maxRange) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        int result;

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // parse argument to integer
        try {
            //noinspection ConstantConditions
            result = Integer.parseInt(arg);
        }
        catch (NullPointerException | NumberFormatException nfe) {
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, ArgumentValueType.INTEGER, minRange, maxRange));
        }

        if (result < minRange || result > maxRange) {
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, ArgumentValueType.INTEGER, minRange, maxRange));
        }

        return result;
    }

    /**
     * Gets an argument as a 64 bit number.
     *
     * @param parameterName  The name of the arguments parameter.
     *
     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException  If the argument is not parsable into a long value.
     */
    public long getLong(String parameterName) throws InvalidArgumentException {
        return getLong(parameterName, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    /**
     * Gets an argument as a 64 bit number.
     *
     * @param parameterName  The name of the arguments parameter.
     * @param minRange       The minimum value.
     * @param maxRange       The maximum value.
     *
     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException  If the argument is not parsable into a long value or does not meet range specs.
     */
    public long getLong(String parameterName, long minRange, long maxRange) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        long result;

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // parse argument to long
        try {
            //noinspection ConstantConditions
            result = Long.parseLong(arg);
        }
        catch (NullPointerException | NumberFormatException nfe) {
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, ArgumentValueType.LONG, minRange, maxRange));
        }

        if (result < minRange || result > maxRange) {
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, ArgumentValueType.LONG, minRange, maxRange));
        }

        return result;
    }

    /**
     * Gets an argument as a float.
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException  If the argument is not parsable into a float value.
     */
    public float getFloat(String parameterName) throws InvalidArgumentException {
        return getFloat(parameterName, Float.MIN_VALUE, Float.MAX_VALUE);
    }

    /**
     * Gets an argument as a float.
     *
     * @param parameterName  The name of the arguments parameter.
     * @param minRange       The minimum value.
     * @param maxRange       The maximum value.
     *
     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException  If the argument is not parsable into a float or does not meet range specs.
     */
    public float getFloat(String parameterName, float minRange, float maxRange) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        float result;

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // parse argument into float
        try {
            //noinspection ConstantConditions
            result = arg.indexOf('.') == -1
                    ? Integer.parseInt(arg)
                    : Float.parseFloat(arg);
        }
        catch (NullPointerException | NumberFormatException nfe) {
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, ArgumentValueType.FLOAT, minRange, maxRange));
        }

        if (result < minRange || result > maxRange) {
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, ArgumentValueType.FLOAT, minRange, maxRange));
        }

        return result;
    }

    /**
     * Gets an argument as a double.
     *
     * @param parameterName  The name of the arguments parameter.
     *
     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException  If the argument is not parsable into a double value.
     */
    public double getDouble(String parameterName) throws InvalidArgumentException {
        return getDouble(parameterName, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    /**
     * Gets an argument as a double.
     *
     * @param parameterName  The name of the arguments parameter.
     * @param minRange       The minimum value.
     * @param maxRange       The maximum value.
     *
     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException  If the argument is not parsable into a double value or does not meet range specs.
     */
    public double getDouble(String parameterName, double minRange, double maxRange) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        double result;

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // parse argument into double
        try {
            //noinspection ConstantConditions
            result = arg.indexOf('.') == -1
                    ? Integer.parseInt(arg)
                    : Double.parseDouble(arg);
        }
        catch (NullPointerException | NumberFormatException nfe) {
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, ArgumentValueType.DOUBLE, minRange, maxRange));
        }

        if (result < minRange || result > maxRange) {
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, ArgumentValueType.DOUBLE, minRange, maxRange));
        }

        return result;
    }

    /**
     * Gets an argument as a boolean.
     *
     * <p>true,on,yes = true</p>
     * <p>false,off,no = false</p>
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException  If the argument cannot be parsed or recognized as a boolean.
     */
    public boolean getBoolean(String parameterName) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // make sure the argument was provided
        if (arg == null) {

            Boolean flag = _parseResults.getFlag(parameterName);

            if (flag == null) {
                throw new InvalidArgumentException(
                        _paramDescriptions.get(parameterName, ArgumentValueType.BOOLEAN));
            }
            else {
                return flag;
            }
        }

        // get boolean based on value
        arg = arg.toLowerCase();
        switch (arg.toLowerCase()) {
            case "true":
            case "allow":
            case "1":
            case "yes":
            case "on":
                return true;
            case "false":
            case "deny":
            case "0":
            case "no":
            case "off":
                return false;
            default:
                throw new InvalidArgumentException(
                        _paramDescriptions.get(parameterName, ArgumentValueType.BOOLEAN));
        }
    }

    /**
     * Gets an argument as a double. "%" characters are ignored.
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException  If the argument cannot be parsed or recognizes as a double.
     */
    public double getPercent(String parameterName) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // make sure the argument was provided
        if (arg == null)
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, ArgumentValueType.PERCENT));

        // remove percent sign
        Matcher matcher = TextUtils.PATTERN_PERCENT.matcher(arg);
        arg = matcher.replaceAll("");

        // parse argument into double
        try {
            return arg.indexOf('.') == -1
                    ? Integer.parseInt(arg)
                    : Double.parseDouble(arg);
        }
        catch (NumberFormatException nfe) {
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, ArgumentValueType.PERCENT));
        }
    }

    /**
     * Gets an arguments raw {@code String} value, splits it at the space character
     * and returns it as a {@code String[]}
     *
     * @param parameterName  The name of the arguments parameter
     */
    public String[] getParams(String parameterName) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument.
        String arg = getRawArgument(parameterName);
        if (arg == null)
            return new String[0];

        return TextUtils.PATTERN_SPACE.split(arg);
    }

    /**
     * Gets an argument as an {@code ItemStack[]}.
     *
     * <p>The supplied argument can be a parsable string representing an {@code ItemStack}</p>
     *
     * <p>The supplied argument can also be "inhand" for the stack in the players hand,
     * "chest" to return all items in the players chest, or "hotbar" to return
     * all items in the players hotbar. All items returned from the player are cloned objects.</p>
     *
     * <p>Use getString(parameterName) method on the same parameter to determine if the player
     * typed "inhand", "chest", or "hotbar" if that information is needed.</p>
     *
     * <p>If the command sender is not a player, and therefore has no chest, the argument
     * will only be valid if a parsable item stack string was provided.</p>
     *
     * @param sender         The {@code CommandSender} who executed the command
     * @param parameterName  The name of the arguments parameter
     *
     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException If the argument is not a recognized keyword and cannot be parsed to an {@code ItemStack}.
     */
    public ItemStack[] getItemStack(@Nullable CommandSender sender, String parameterName) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // make sure the argument was provided
        if (arg == null)
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, ArgumentValueType.ITEMSTACK));

        // Check for "inhand" keyword as argument
        if (arg.equalsIgnoreCase("inhand")) {

            // sender must be a player to use "inhand" keyword
            if (!(sender instanceof Player)) {
                throw new InvalidArgumentException(
                        _paramDescriptions.get(parameterName, ArgumentValueType.ITEMSTACK));
            }

            Player p = (Player)sender;

            ItemStack inhand = p.getItemInHand();

            // sender must have an item in hand
            if (inhand == null || inhand.getType() == Material.AIR) {
                throw new InvalidArgumentException(
                        _paramDescriptions.get(parameterName, ArgumentValueType.ITEMSTACK));
            }

            return new ItemStack[] { inhand }; // finished
        }

        // Check for "hotbar" keyword as argument
        if (arg.equalsIgnoreCase("hotbar")) {

            // sender must be a player to use "hotbar" keyword
            if (!(sender instanceof Player)) {
                throw new InvalidArgumentException(
                        _paramDescriptions.get(parameterName, ArgumentValueType.ITEMSTACK));
            }

            Player p = (Player)sender;

            Inventory inventory = p.getInventory();

            Set<ItemWrapper> wrappers = new HashSet<ItemWrapper>(9);

            // iterate and add players hotbar items to wrapper set.
            for (int i=0; i < 9; i++) {
                ItemStack item = inventory.getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    ItemWrapper wrapper = new ItemWrapper(item, ItemStackComparer.getDefault());
                    wrappers.add(wrapper);
                }
            }

            // generate result {@code ItemStack} array
            ItemStack[] result = new ItemStack[wrappers.size()];
            int index = 0;
            for (ItemWrapper wrapper : wrappers) {
                result[index] = wrapper.getItem();
                index++;
            }

            return result; // finished
        }

        // Check for "chest" keyword as argument
        if (arg.equalsIgnoreCase("chest")) {

            // sender must be player to use "chest" keyword
            if (!(sender instanceof Player)) {
                throw new InvalidArgumentException(
                        _paramDescriptions.get(parameterName, ArgumentValueType.ITEMSTACK));
            }

            Player p = (Player)sender;

            Inventory inventory = p.getInventory();

            int len = inventory.getContents().length;
            Set<ItemWrapper> wrappers = new HashSet<ItemWrapper>(len);

            // iterate and add players chest items to wrapper set
            for (int i=0; i < len; i++) {
                ItemStack item = inventory.getItem(i);
                if (item != null && item.getType() != Material.AIR) {

                    // use item wrapper to prevent duplicates in set
                    ItemWrapper wrapper = new ItemWrapper(item);
                    wrappers.add(wrapper);
                }
            }

            // generate result {@code ItemStack} array.
            ItemStack[] result = new ItemStack[wrappers.size()];
            int index = 0;
            for (ItemWrapper wrapper : wrappers) {
                result[index] = wrapper.getItem();
                index++;
            }

            return result; // finished
        }

        // no keywords used, attempt to parse the argument into an {@code ItemStack} array.
        ItemStack[] stacks = null;

        try {
            stacks = ItemStackUtils.parse(arg);
        } catch (InvalidItemStackStringException ignore) {
            // do nothing
        }

        if (stacks == null)
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, ArgumentValueType.ITEMSTACK));

        // return result
        return stacks;
    }


    /**
     * Gets an argument as a location.
     *
     * <p>Possible values are "current" or "select"</p>
     *
     * <p>If the argument value is "current", the players current location is returned via
     * the {@code LocationHandler}.</p>
     *
     * <p>If the argument value is "select", the player is asked to click on the location
     * to be selected and the value is return via the {@code LocationHandler}.</p>
     *
     * <p>If the {@CommandSender} is not a player the argument is always considered invalid.</p>
     *
     * @param sender           The {@code CommandSender} who executed the command
     * @param parameterName    The name of the arguments parameter
     * @param locationHandler  The {@code LocationHandler} responsible for dealing with the return location.
     *
     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException If the sender is not a player, or the argument is not "current" or "select"
     */
    public void getLocation (final CommandSender sender, String parameterName,
                             final LocationResponse locationHandler) throws InvalidArgumentException {
        PreCon.notNull(sender);
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(locationHandler);

        String arg = getString(parameterName);

        // command sender must be a player
        if (!(sender instanceof Player)) {
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, ArgumentValueType.LOCATION));
        }

        Player p = (Player)sender;

        // use players current location
        if (arg.equalsIgnoreCase("current")) {
            locationHandler.onLocationRetrieved(p, p.getLocation());
        }

        // select location
        else if (arg.equalsIgnoreCase("select")) {

            _msg.tell(p, "Click a block to select it's location...");

            PlayerBlockSelect.query(p, new PlayerBlockSelectHandler() {

                @Override
                public boolean onBlockSelect(Player p, Block selectedBlock, Action clickAction) {

                    Location location = selectedBlock.getLocation();

                    String message = Lang.get("Location selected: {0} ", TextUtils.formatLocation(location, true));
                    _msg.tell(p, message);

                    locationHandler.onLocationRetrieved(p, location);

                    return true;
                }

            });
        }
        else {
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, ArgumentValueType.LOCATION));
        }
    }

    /**
     * Gets an argument as an enum. The argument must be the name of the enum constant and
     * is not case sensitive. The enum should use proper naming conventions by having 
     * all constant names in upper case.
     *
     * @param parameterName  The name of the arguments parameter
     * @param enumClass      The enums class
     *
     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException  If the argument is not the name of one of the enums constants.
     */
    public <T extends Enum<T>> T getEnum(String parameterName,  Class<T> enumClass) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(enumClass);

        return getEnum(parameterName, enumClass, enumClass.getEnumConstants());
    }

    /**
     * Gets an argument as an enum. The argument must be the name of the enum constant and
     * is not case sensitive. The enum should use proper naming conventions by having 
     * all constant names in upper case.
     *
     * <p>Valid values can be specified to prevent all of an enums constants from being valid.
     * Use if you have no control over the enum and it isn't practical to make a 
     * new enum.</p>
     *
     * @param parameterName  The name of the arguments parameter
     * @param enumClass      The enums class
     * @param validValues    an array of valid enum constants
     *
     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException If the argument is not the name of one of the valid enum constants.
     */
    public <T extends Enum<T>> T getEnum(String parameterName,  Class<T> enumClass, T[] validValues) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(enumClass);
        PreCon.notNull(validValues);

        // get raw argument
        String arg = getString(parameterName);

        T evalue = EnumUtils.searchEnum(arg, enumClass);

        if (evalue == null) {
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, validValues));
        }

        // make sure the enum constant is valid
        for (T validValue : validValues) {
            if (validValue == evalue)
                return evalue;
        }

        throw new InvalidArgumentException(
                _paramDescriptions.get(parameterName, validValues));
    }

    /**
     * Gets an argument as an enum. The argument must be the name of the enum constant and
     * is not case sensitive. The enum should use proper naming conventions by having all
     * constant names in upper case.
     *
     * <p>Valid values can be specified to prevent all of an enums constants from being valid.
     * Use if you have no control over the enum and it isn't practical to make a 
     * new enum.</p>
     *
     * @param parameterName  The name of the arguments parameter
     * @param enumClass      The enums class
     * @param validValues    A collection of valid enum constants
     *
     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException  If the argument is not one of the valid enum constants.
     */
    public <T extends Enum<T>> T getEnum(String parameterName, Class<T> enumClass, Collection<T> validValues)
            throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(enumClass);
        PreCon.notNull(validValues);

        // get the raw argument
        String arg = getString(parameterName);

        T evalue = EnumUtils.searchEnum(arg, enumClass);

        if (evalue == null) {
            throw new InvalidArgumentException(
                    _paramDescriptions.get(parameterName, validValues));
        }

        // make sure the enum constant is valid
        for (T val : validValues) {
            if (val == evalue)
                return evalue;
        }

        throw new InvalidArgumentException(
                _paramDescriptions.get(parameterName, validValues));
    }

    /**
     * Determine if an argument is provided.
     *
     * <p>When used on floating parameters, returns false
     * if the command sender does not reference the 
     * parameter.</p>
     *
     * @param parameterName  The name of the arguments parameter
     */
    public boolean hasValue(String parameterName) {
        PreCon.notNullOrEmpty(parameterName);

        CommandArgument argument = _parseResults.getArgMap().get(parameterName);
        if (argument == null)
            return false;

        try {
            return getRawArgument(parameterName) != null;
        }
        catch (InvalidArgumentException ive) {
            return false;
        }
    }

    /**
     * Determine if an argument is provided.
     *
     * <p>When used on floating parameters, returns the string "false"
     * if the command sender does not reference the parameter</p>
     *
     * @param parameterName  The name of the arguments parameter
     */
    public boolean hasString(String parameterName) {
        PreCon.notNullOrEmpty(parameterName);

        return hasValue(parameterName);
    }

    /**
     * Determine if an argument is provided and is a single character.
     *
     * @param parameterName  The name of the arguments parameter
     */
    public boolean hasChar(String parameterName) {
        PreCon.notNullOrEmpty(parameterName);

        String value;
        try {
            value = getRawArgument(parameterName);
        }
        catch (InvalidArgumentException ive) {
            return false;
        }

        if (value == null)
            return false;

        if (value.length() != 1)
            return false;

        return true;
    }

    /**
     * Determine if an argument is provided and can be used as an integer value.
     *
     * @param parameterName  The name of the arguments parameter
     */
    public boolean hasInt(String parameterName) {
        PreCon.notNullOrEmpty(parameterName);

        try {
            getInteger(parameterName);
        }
        catch (InvalidArgumentException ive) {
            return false;
        }
        return true;
    }

    /**
     * Determine if an argument is provided and can be used as a short value.
     *
     * @param parameterName  The name of the arguments parameter
     */
    public boolean hasShort(String parameterName) {
        PreCon.notNullOrEmpty(parameterName);

        try {
            getShort(parameterName);
        }
        catch (InvalidArgumentException ive) {
            return false;
        }
        return true;
    }

    /**
     * Determine if an argument is provided and can be used as a byte value.
     *
     * @param parameterName  The name of the arguments parameter
     */
    public boolean hasByte(String parameterName) {
        PreCon.notNullOrEmpty(parameterName);

        try {
            getByte(parameterName);
        }
        catch (InvalidArgumentException ive) {
            return false;
        }
        return true;

    }

    /**
     * Determine if an argument is provided and can be used as a double value.
     *
     * @param parameterName  The name of the arguments parameter
     */
    public boolean hasDouble(String parameterName) {
        PreCon.notNullOrEmpty(parameterName);

        try {
            getDouble(parameterName);
        }
        catch (InvalidArgumentException ive) {
            return false;
        }
        return true;

    }

    /**
     * Determine if an argument is provided and can be used as a float value.
     *
     * @param parameterName  The name of the arguments parameter
     */
    public boolean hasFloat(String parameterName) {
        PreCon.notNullOrEmpty(parameterName);

        try {
            getFloat(parameterName);
        }
        catch (InvalidArgumentException ive) {
            return false;
        }
        return true;

    }

    /**
     * Determine if an argument is provided and can be used as a boolean value.
     *
     * @param parameterName  The name of the arguments parameter
     */
    public boolean hasBoolean(String parameterName) {
        PreCon.notNullOrEmpty(parameterName);

        try {
            getBoolean(parameterName);
        }
        catch (InvalidArgumentException ive) {
            return false;
        }
        return true;
    }

    /**
     * Determine if an argument is provided and can be used as an item stack array.
     *
     * @param parameterName  The name of the arguments parameter
     */
    public boolean hasItemStack(String parameterName) {
        PreCon.notNullOrEmpty(parameterName);

        try {
            getItemStack(null, parameterName);
        }
        catch (InvalidArgumentException ive) {
            return false;
        }
        return true;
    }

    /**
     * Determine if an argument is provided and can be used as a double value.
     *
     * <p>A '%' character in the value will not invalidate the argument.</p>
     *
     * @param parameterName  The name of the arguments parameter
     */
    public boolean hasPercent(String parameterName) {
        PreCon.notNullOrEmpty(parameterName);

        try {
            getPercent(parameterName);
        }
        catch (InvalidArgumentException ive) {
            return false;
        }
        return true;
    }

    /**
     * Determine if an argument is provided and can be used as an enum.
     *
     * @param parameterName  The name of the arguments parameter
     * @param enumClass      The enum class the argument must be used as
     */
    public <T extends Enum<T>> boolean hasEnum(String parameterName, Class<T> enumClass) {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(enumClass);

        try {
            getEnum(parameterName, enumClass);
        } catch (InvalidArgumentException e) {
            return false;
        }
        return true;
    }


    /**
     * Get the raw argument provided for a parameter.
     *
     * @param parameterName  The name of the arguments parameter.

     * @throws com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException
     */
    @Nullable
    private String getRawArgument(String parameterName) throws InvalidArgumentException {

        CommandArgument param = _parseResults.getArgMap().get(parameterName);
        if (param == null)
            return null;

        String value = param.getValue();
        String defaultVal = param.getDefaultValue();

        return value != null ? value : defaultVal;
    }
}

