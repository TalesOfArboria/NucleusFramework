/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

import com.jcwhatever.bukkit.generic.commands.CommandInfoContainer;
import com.jcwhatever.bukkit.generic.commands.exceptions.DuplicateParameterException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidParameterException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.commands.exceptions.TooManyArgsException;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.items.ItemStackComparer;
import com.jcwhatever.bukkit.generic.items.ItemStackHelper;
import com.jcwhatever.bukkit.generic.items.ItemWrapper;
import com.jcwhatever.bukkit.generic.messaging.Messenger;
import com.jcwhatever.bukkit.generic.player.PlayerBlockSelect;
import com.jcwhatever.bukkit.generic.player.PlayerBlockSelect.PlayerBlockSelectHandler;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

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
    private final List<CommandArgument> _staticArgs = new ArrayList<CommandArgument>(10);
    private final List<CommandArgument> _floatingArgs = new ArrayList<CommandArgument>(10);
    private final ParameterDescriptions _paramDescriptions;

    private Map<String, CommandArgument> _argMap = new HashMap<String, CommandArgument>(10);
    private int _expectedSize;

    /**
     * Constructor. Validates while assuming no arguments are provided.
     *
     * @param plugin       The owning plugin.
     * @param commandInfo  The commands info annotation container.
     *
     * @throws InvalidValueException        If a value provided is not valid.
     * @throws DuplicateParameterException  If a parameter is defined in the arguments more than once.
     * @throws InvalidParameterException    If a parameter int the arguments is not found for the command.
     * @throws TooManyArgsException         If the provided arguments are more than is expected.
     */
    public CommandArguments(Plugin plugin, CommandInfoContainer commandInfo)
            throws InvalidValueException, DuplicateParameterException, InvalidParameterException, TooManyArgsException {

        this(plugin, commandInfo, null);
    }

    /**
     * Constructor. Parses the provided arguments and validates against information provided
     * by the {@code CommandInfoContainer}.
     *
     * @param plugin       The owning plugin.
     * @param commandInfo  The commands info annotation container.
     * @param args         The command arguments.
     *
     * @throws InvalidValueException        If a value provided is not valid.
     * @throws DuplicateParameterException  If a parameter is defined in the arguments more than once.
     * @throws InvalidParameterException    If a parameter int the arguments is not found for the command.
     * @throws TooManyArgsException         If the provided arguments are more than is expected.
     */
    public CommandArguments(Plugin plugin, CommandInfoContainer commandInfo, @Nullable String[] args)
            throws InvalidValueException, DuplicateParameterException, InvalidParameterException, TooManyArgsException {

        PreCon.notNull(plugin);
        PreCon.notNull(commandInfo);

        _plugin = plugin;
        _expectedSize = commandInfo.getStaticParams().length;
        _paramDescriptions = new ParameterDescriptions(commandInfo);

        // substitute null value to remove the need for null checks while parsing
        if (args == null)
            args = new String[0];

        // parse arguments
        parseArguments(commandInfo.getStaticParams(), commandInfo.getFloatingParams(), args);
    }

    /**
     * The number of arguments in the collection.
     *
     * <p>Does not include the number of optional arguments.</p>
     *
     * <p>Includes arguments automatically added due to the
     * parameter having a default value.</p>
     */
    public int staticSize() {
        return _staticArgs.size();
    }

    /**
     * The number of optional arguments in the collection.
     */
    public int floatingSize () {
        return _floatingArgs.size();
    }

    /**
     * The number of arguments expected.
     *
     * <p>Used by the command handler to quickly determine if the number of 
     * arguments provided is valid.</p>
     */
    public int expectedSize() {
        return _expectedSize;
    }


    /**
     * Get an {@code ICommandParameter} by index
     *
     * @param index  The index position of the parameter as provided by the command sender
     */
    @Nullable
    public CommandArgument get(int index) {
        PreCon.positiveNumber(index);

        // check if getting a floating argument
        if (index >= _staticArgs.size()) {

            int floatingIndex = _staticArgs.size() + index - 1;

            // make sure index is in range
            if (floatingIndex >= _staticArgs.size() + _floatingArgs.size())
                return null;

            // return floating argument
            return _floatingArgs.get(index);
        }

        // return static argument
        return _staticArgs.get(index);
    }

    /**
     * Get an {@code ICommandParameter} by parameter name
     *
     * @param parameterName  The name of the parameter
     */
    @Nullable
    public CommandArgument get(String parameterName) {
        return _argMap.get(parameterName);
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
                return index < _staticArgs.size() + _floatingArgs.size();
            }

            @Override
            public CommandArgument next () {
                CommandArgument arg = get(index);
                index++;
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
     * @throws InvalidValueException  If the argument for the parameter is not a valid name.
     */
    public String getName(String parameterName) throws InvalidValueException {
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
     * @throws InvalidValueException  If the argument for the parameter is not a valid name.
     */
    public String getName(String parameterName, int maxLen) throws InvalidValueException {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.greaterThanZero(maxLen);

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // make sure the argument is a valid name
        if (!TextUtils.isValidName(arg, maxLen)) {
            throw new InvalidValueException(parameterName,
                    _paramDescriptions.get(parameterName, maxLen));
        }

        return arg;
    }

    /**
     * Get an argument as a string.
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws InvalidValueException  If the argument is not present or is not an expected value.
     */
    public String getString(String parameterName) throws InvalidValueException {
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument
        String value = getRawArgument(parameterName);
        if (value == null)
            throw new InvalidValueException(parameterName,
                    _paramDescriptions.get(parameterName, ArgumentValueType.STRING));

        // make sure if pipes are used, that the argument is one of the possible values
        if (parameterName.indexOf('|') != -1) {
            String[] options = TextUtils.PATTERN_PIPE.split(parameterName, 0);
            for (String option : options) {
                if (value.equalsIgnoreCase(option))
                    return option;
            }
            throw new InvalidValueException(parameterName,
                    _paramDescriptions.get(parameterName, ArgumentValueType.STRING));
        }

        return value;
    }


    /**
     * Get an argument as a character.
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws InvalidValueException  If the argument is not present, is not an expected value, or is more than a single character.
     */
    public char getChar(String parameterName) throws InvalidValueException {
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument
        String value = getRawArgument(parameterName);
        if (value == null)
            throw new InvalidValueException(parameterName,
                    _paramDescriptions.get(parameterName, ArgumentValueType.CHARACTER));

        // make sure if pipes are used, that the argument is one of the possible values
        if (parameterName.indexOf('|') != -1) {
            String[] options = StringUtils.split(parameterName, "|");
            for (String option : options) {
                if (value.equalsIgnoreCase(option)) {

                    if (option.length() != 1) {
                        throw new InvalidValueException(parameterName,
                                _paramDescriptions.get(parameterName, ArgumentValueType.CHARACTER));
                    }

                    return option.charAt(0);
                }
            }
            throw new InvalidValueException(parameterName,
                    _paramDescriptions.get(parameterName, ArgumentValueType.CHARACTER));
        }

        // make sure the length of the value is exactly 1
        if (value.length() != 1)
            throw new InvalidValueException(parameterName,
                    _paramDescriptions.get(parameterName, ArgumentValueType.CHARACTER));

        return value.charAt(0);
    }

    /**
     * Gets an argument as a byte.
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws InvalidValueException  If the argument is not parsable into a byte value.
     */
    public byte getByte(String parameterName) throws InvalidValueException {
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // parse argument to byte
        try {
            return Byte.parseByte(arg);
        }
        catch (NumberFormatException nfe) {
            throw new InvalidValueException(parameterName,
                    _paramDescriptions.get(parameterName, ArgumentValueType.BYTE));
        }

    }

    /**
     * Gets an argument as a short.
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws InvalidValueException  If the argument is not parsable into a short value.
     */
    public short getShort(String parameterName) throws InvalidValueException {
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // parse argument to short
        try {
            return Short.parseShort(arg);
        }
        catch (NumberFormatException nfe) {
            throw new InvalidValueException(parameterName,
                    _paramDescriptions.get(parameterName, ArgumentValueType.SHORT));
        }
    }

    /**
     * Gets an argument as an integer.
     *
     * @param parameterName  The name of the arguments parameter 
     *
     * @throws InvalidValueException  If the argument is not parsable into an integer.
     */
    public int getInt(String parameterName) throws InvalidValueException {
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // parse argument to integer
        try {
            return Integer.parseInt(arg);
        }
        catch (NumberFormatException nfe) {
            throw new InvalidValueException(parameterName,
                    _paramDescriptions.get(parameterName, ArgumentValueType.INTEGER));
        }
    }

    /**
     * Gets an argument as a 64 bit number.
     *
     * @param parameterName  The name of the arguments parameter 
     *
     * @throws InvalidValueException  If the argument is not parsable into a long value.
     */
    public long getLong(String parameterName) throws InvalidValueException {
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // parse argument to long
        try {
            return Long.parseLong(arg);
        }
        catch (NumberFormatException nfe) {
            throw new InvalidValueException(parameterName,
                    _paramDescriptions.get(parameterName, ArgumentValueType.LONG));
        }
    }

    /**
     * Gets an argument as a float.
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws InvalidValueException  If the argument is not parsable into a float value.
     */
    public float getFloat(String parameterName) throws InvalidValueException {
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // parse argument into float
        try {
            return Float.parseFloat(arg);
        }
        catch (NumberFormatException nfe) {
            throw new InvalidValueException(parameterName,
                    _paramDescriptions.get(parameterName, ArgumentValueType.FLOAT));
        }
    }

    /**
     * Gets an argument as a double.
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws InvalidValueException  If the argument is not parsable into a double value.
     */
    public double getDouble(String parameterName) throws InvalidValueException {
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // parse argument into double
        try {
            return Double.parseDouble(arg);
        }
        catch (NumberFormatException nfe) {
            throw new InvalidValueException(parameterName,
                    _paramDescriptions.get(parameterName, ArgumentValueType.DOUBLE));
        }
    }

    /**
     * Gets an argument as a boolean.
     *
     * <p>true,on,yes = true</p>
     * <p>false,off,no = false</p>
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws InvalidValueException  If the argument cannot be parsed or recognized as a boolean.
     */
    public boolean getBoolean(String parameterName) throws InvalidValueException {
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // make sure the argument was provided
        if (arg == null)
            throw new InvalidValueException(parameterName,
                    _paramDescriptions.get(parameterName, ArgumentValueType.BOOLEAN));

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
                throw new InvalidValueException(parameterName,
                        _paramDescriptions.get(parameterName, ArgumentValueType.BOOLEAN));
        }
    }

    /**
     * Gets an argument as a double. "%" characters are ignored.
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws InvalidValueException  If the argument cannot be parsed or recognizes as a double.
     */
    public double getPercent(String parameterName) throws InvalidValueException {
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // make sure the argument was provided
        if (arg == null)
            throw new InvalidValueException(parameterName,
                    _paramDescriptions.get(parameterName, ArgumentValueType.PERCENT));

        // remove percent sign
        Matcher matcher = TextUtils.PATTERN_PERCENT.matcher(arg);
        arg = matcher.replaceAll("");

        // parse argument into double
        try {
            return Double.parseDouble(arg);
        }
        catch (NumberFormatException nfe) {
            throw new InvalidValueException(parameterName,
                    _paramDescriptions.get(parameterName, ArgumentValueType.PERCENT));
        }
    }

    /**
     * Gets an arguments raw {@code String} value, splits it at the space character
     * and returns it as a {@code String[]}
     *
     * @param parameterName  The name of the arguments parameter
     */
    public String[] getParams(String parameterName) throws InvalidValueException {
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
     * "inventory" to return all items in the players inventory, or "hotbar" to return
     * all items in the players hotbar. All items returned from the player are cloned objects.</p>
     *
     * <p>Use getString(parameterName) method on the same parameter to determine if the player
     * typed "inhand", "inventory", or "hotbar" if that information is needed.</p>
     *
     * <p>If the command sender is not a player, and therefore has no inventory, the argument 
     * will only be valid if a parsable item stack string was provided.</p>
     *
     * @param sender         The {@code CommandSender} who executed the command
     * @param parameterName  The name of the arguments parameter
     *
     * @throws InvalidValueException If the argument is not a recognized keyword and cannot be parsed to an {@code ItemStack}.
     */
    public ItemStack[] getItemStack(CommandSender sender, String parameterName) throws InvalidValueException {
        PreCon.notNull(sender);
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // make sure the argument was provided
        if (arg == null)
            throw new InvalidValueException(parameterName,
                    _paramDescriptions.get(parameterName, ArgumentValueType.ITEMSTACK));

        // Check for "inhand" keyword as argument
        if (arg.equalsIgnoreCase("inhand")) {

            // sender must be a player to use "inhand" keyword
            if (!(sender instanceof Player)) {
                throw new InvalidValueException(parameterName,
                        _paramDescriptions.get(parameterName, ArgumentValueType.ITEMSTACK));
            }

            Player p = (Player)sender;

            ItemStack inhand = p.getItemInHand();

            // sender must have an item in hand
            if (inhand == null || inhand.getType() == Material.AIR) {
                throw new InvalidValueException(parameterName,
                        _paramDescriptions.get(parameterName, ArgumentValueType.ITEMSTACK));
            }

            return new ItemStack[] { inhand }; // finished
        }

        // Check for "hotbar" keyword as argument
        if (arg.equalsIgnoreCase("hotbar")) {

            // sender must be a player to use "hotbar" keyword
            if (!(sender instanceof Player)) {
                throw new InvalidValueException(parameterName,
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

        // Check for "inventory" keyword as argument
        if (arg.equalsIgnoreCase("inventory")) {

            // sender must be player to use "inventory" keyword
            if (!(sender instanceof Player)) {
                throw new InvalidValueException(parameterName,
                        _paramDescriptions.get(parameterName, ArgumentValueType.ITEMSTACK));
            }

            Player p = (Player)sender;

            Inventory inventory = p.getInventory();

            int len = inventory.getContents().length;
            Set<ItemWrapper> wrappers = new HashSet<ItemWrapper>(len);

            // iterate and add players inventory items to wrapper set
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
        ItemStack[] stacks = ItemStackHelper.parse(arg);
        if (stacks == null)
            throw new InvalidValueException(parameterName,
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
     * @throws InvalidValueException If the sender is not a player, or the argument is not "current" or "select"
     */
    public void getLocation (final CommandSender sender, String parameterName,
                             final LocationResponse locationHandler) throws InvalidValueException {
        PreCon.notNull(sender);
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(locationHandler);

        String arg = getRawArgument(parameterName);

        // command sender must be a player
        if (!(sender instanceof Player)) {
            throw new InvalidValueException(parameterName,
                    _paramDescriptions.get(parameterName, ArgumentValueType.LOCATION));
        }

        Player p = (Player)sender;

        // use players current location
        if (arg.equalsIgnoreCase("current")) {
            locationHandler.onLocationRetrieved(p, p.getLocation());
        }

        // select location
        else if (arg.equalsIgnoreCase("select")) {

            Messenger.tell(_plugin, p, "Click a block to select it's location...");

            PlayerBlockSelect.query(p, new PlayerBlockSelectHandler() {

                @Override
                public boolean onBlockSelect(Player p, Block selectedBlock, Action clickAction) {

                    Location location = selectedBlock.getLocation();

                    String message = Lang.get("Location selected: {0} ", TextUtils.formatLocation(location, true));
                    Messenger.tell(_plugin, p, message);

                    locationHandler.onLocationRetrieved(p, location);

                    return true;
                }

            });
        }
        else {
            throw new InvalidValueException(parameterName,
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
     * @throws InvalidValueException  If the argument is not the name of one of the enums constants.
     */
    public <T extends Enum<T>> T getEnum(String parameterName,  Class<T> enumClass) throws InvalidValueException {
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
     * @throws InvalidValueException If the argument is not the name of one of the valid enum constants.
     */
    public <T extends Enum<T>> T getEnum(String parameterName,  Class<T> enumClass, T[] validValues) throws InvalidValueException {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(enumClass);
        PreCon.notNull(validValues);

        // get raw argument
        String arg = getRawArgument(parameterName);

        T evalue;

        // get enum constant from argument
        try {
            evalue = Enum.valueOf(enumClass, arg.toUpperCase());
        }
        catch (Exception e) {

            try {
                evalue = Enum.valueOf(enumClass, arg);
            }
            catch (Exception e2) {

                throw new InvalidValueException(parameterName,
                        _paramDescriptions.get(parameterName, validValues));
            }
        }

        // make sure the enum constant is valid
        for (T validValue : validValues) {
            if (validValue == evalue)
                return evalue;
        }

        throw new InvalidValueException(parameterName,
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
     * @throws InvalidValueException  If the argument is not one of the valid enum constants.
     */
    public <T extends Enum<T>> T getEnum(String parameterName, Class<T> enumClass, Collection<T> validValues)
            throws InvalidValueException {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(enumClass);
        PreCon.notNull(validValues);

        // get the raw argument
        String arg = getRawArgument(parameterName);

        T evalue;

        // get enum constant from argument
        try {
            evalue = Enum.valueOf(enumClass, arg.toUpperCase());
        }
        catch (Exception e) {

            try {
                evalue = Enum.valueOf(enumClass, arg);
            }
            catch (Exception e2) {

                throw new InvalidValueException(parameterName,
                        _paramDescriptions.get(parameterName, validValues));
            }
        }

        // make sure the enum constant is valid
        for (T val : validValues) {
            if (val == evalue)
                return evalue;
        }

        throw new InvalidValueException(parameterName,
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

        CommandArgument argument = _argMap.get(parameterName);
        if (argument == null)
            return false;

        if (argument.getArgumentType() == ArgumentType.UNDEFINED)
            return false;

        try {
            return getRawArgument(parameterName) != null;
        }
        catch (InvalidValueException ive) {
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
        catch (InvalidValueException ive) {
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
            getInt(parameterName);
        }
        catch (InvalidValueException ive) {
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
        catch (InvalidValueException ive) {
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
        catch (InvalidValueException ive) {
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
        catch (InvalidValueException ive) {
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
        catch (InvalidValueException ive) {
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
        catch (InvalidValueException ive) {
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
        catch (InvalidValueException ive) {
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
        catch (InvalidValueException ive) {
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
        } catch (InvalidValueException e) {
            return false;
        }
        return true;
    }


    /**
     * Get the raw argument provided for a parameter.
     *
     * @param parameterName  The name of the arguments parameter.

     * @throws InvalidValueException
     */
    private String getRawArgument(String parameterName) throws InvalidValueException {

        CommandArgument param = _argMap.get(parameterName);
        if (param == null)
            return null;

        String value = param.getValue();
        String defaultVal = param.getDefaultValue();

        switch (param.getParameterType()) {

            case FLOATING:
                if (value == null && defaultVal == null) {

                    if (param.getArgumentType() == ArgumentType.UNDEFINED) {
                        return "false"; // insert false for flag argument
                    }

                    return param.getArgumentType() == ArgumentType.DEFINED_PARAM_UNDEFINED_VALUE
                            ? "true" // insert true for flag argument
                            : null;

                }
                // fall through

            case STATIC:
                return value != null && !value.isEmpty()
                        ? param.getValue()
                        : param.getDefaultValue();
        }

        return null;
    }


    /**
     * Parse command arguments
     *
     * @param staticParams    Names of required parameters in the order expected  
     * @param floatingParams  Names of optional parameters
     * @param args            The provided arguments
     *
     * @return left over arguments
     */
    private void parseArguments(String[] staticParams, String[] floatingParams, String[] args)
            throws InvalidValueException, DuplicateParameterException, InvalidParameterException, TooManyArgsException {

        LinkedList<String> staticQueue = new LinkedList<String>();
        LinkedList<String> argsQueue = new LinkedList<String>();

        Collections.addAll(staticQueue, staticParams);
        Collections.addAll(argsQueue, args);

        while (!staticQueue.isEmpty()) {

            // split on '=' to get default value
            String rawParam = staticQueue.removeFirst();
            String[] paramComp = TextUtils.PATTERN_EQUALS.split(rawParam, -1);

            String paramName = paramComp[0].trim();
            String defaultValue = paramComp.length > 1 ? paramComp[1].trim() : null;
            String value = null;

            if (!argsQueue.isEmpty()) {
                String arg = argsQueue.removeFirst();

                // Should not be any optional before required arguments
                if (arg.startsWith("--")) {

                    // no default value defined means a discreet value is expected
                    if (defaultValue == null) {
                        throw new InvalidValueException(paramName);
                    }
                    // re-insert argument
                    else {
                        argsQueue.addFirst(arg);
                    }
                }
                else {

                    // get parameter value
                    value = parseValue(arg, argsQueue);
                }
            }

            // add argument
            if (value != null || defaultValue != null) {
                addArgument(new CommandArgument(paramName, value, defaultValue, ParameterType.STATIC));
            }

        }

        // check if there are floating parameters for the command
        if (floatingParams == null || floatingParams.length == 0) {

            // make sure there are not too many arguments
            if (!argsQueue.isEmpty()) {
                throw new TooManyArgsException();
            }

            // nothing left to do
            return;
        }

        // prepare optional parameters for easy lookup and
        // add defaults
        for (String floatingParam : floatingParams) {

            OptionalParameter param = new OptionalParameter(floatingParam);
            CommandArgument argument =
                    new CommandArgument(param.parameterName, null, param.defaultValue,
                            ParameterType.FLOATING, ArgumentType.UNDEFINED);

            // make sure the predefined floating parameter does not have
            // the same name as a static parameter or duplicate floating parameter name.
            if (_argMap.containsKey(param.parameterName)) {
                throw new DuplicateParameterException('\'' + param.parameterName + '\'' +
                        " is the name of a static parameter and is not a valid floating parameter name.");
            }

            // put the argument in the argument map with default values
            // in case the command sender does not reference it
            _argMap.put(argument.getParameterName(), argument);
        }

        // check for optional arguments
        while (!argsQueue.isEmpty()) {

            String arg = argsQueue.removeFirst();

            // check for invalid floating argument
            if (!arg.startsWith("--"))
                throw new InvalidValueException('\'' + arg + '\'' +
                        " is not a valid parameter name for this command. The parameter name of a floating " +
                        "argument should be prefixed with a double dash. i.e. --" + arg);

            // remove double dash
            String paramName = arg.substring(2, arg.length());


            // get value
            String value = null;
            ArgumentType argType = ArgumentType.DEFINED_VALUE;

            if (argsQueue.isEmpty()) {
                argType = ArgumentType.DEFINED_PARAM_UNDEFINED_VALUE;
            }
            else {
                arg = argsQueue.removeFirst();

                // make sure the argument value isn't actually the next floating parameter name.
                if (arg.startsWith("--")) {

                    argsQueue.addFirst(arg); // put arg back
                    argType = ArgumentType.DEFINED_PARAM_UNDEFINED_VALUE;
                } else {
                    // parse argument value
                    value = parseValue(arg, argsQueue);
                }
            }


            // check parameter name is valid
            CommandArgument commandArg = _argMap.get(paramName);
            if (commandArg == null)
                throw new InvalidParameterException('\'' + arg + '\'' +
                        " is not a valid parameter name for this command.");

            // check attempting to reference a static parameter with a floating argument
            if (commandArg.getParameterType() == ParameterType.STATIC)
                throw new DuplicateParameterException('\'' + arg + '\'' +
                        " is the name of a required parameter and is not a valid optional parameter.");

            // check if the argument value has already has already been set
            if (commandArg.getValue() != null)
                throw new DuplicateParameterException(
                        "Duplicate argument detected for parameter named '" + arg + "'.");

            // reinsert argument into argument map with value
            addArgument(new CommandArgument(
                    commandArg.getParameterName(), value, commandArg.getDefaultValue(),
                    ParameterType.FLOATING, argType));
        }
    }

    /**
     * parses quotes or returns current argument.
     *
     * @param currentArg  The current argument
     * @param argsQueue   The queue of arguments words
     */
    private String parseValue(String currentArg, LinkedList<String> argsQueue) {

        // check to see if parsing a literal
        String quote = null;

        // detect double quote
        if (currentArg.startsWith("\"")) {
            quote = "\"";
        }
        // detect single quote
        else if (currentArg.startsWith("'")) {
            quote = "'";
        }

        // check for quoted literal
        if (quote != null) {


            String firstWord = currentArg.substring(1); // remove quotation

            // make sure the literal isn't closed on the same word
            if (firstWord.endsWith(quote)) {

                // remove end quote
                return firstWord.substring(0, firstWord.length() - 1);
            }
            // otherwise parse ahead until end of literal
            else {

                StringBuilder literal = new StringBuilder(currentArg.length() * argsQueue.size());
                literal.append(firstWord);

                while (!argsQueue.isEmpty()) {
                    String nextArg = argsQueue.removeFirst();

                    // check if this is the final word in the literal
                    if (nextArg.endsWith(quote)) {

                        //remove end quote
                        nextArg = nextArg.substring(0, nextArg.length() - 1);

                        literal.append(' ');
                        literal.append(nextArg);
                        break;
                    }

                    literal.append(' ');
                    literal.append(nextArg);
                }
                return literal.toString();
            }
        }
        // value is unquoted argument
        else {
            return currentArg;
        }
    }

    private void addArgument(CommandArgument argument) {
        switch (argument.getParameterType()) {
            case STATIC:
                _staticArgs.add(argument);
                break;
            case FLOATING:
                _floatingArgs.remove(argument); // prevent duplicates
                _floatingArgs.add(argument);
                break;
        }
        _argMap.put(argument.getParameterName(), argument);
    }

    private static class OptionalParameter {

        String parameterName;
        String defaultValue;

        OptionalParameter(String parameter) {
            String[] paramComp = TextUtils.PATTERN_EQUALS.split(parameter);
            parameterName = paramComp[0];

            defaultValue = paramComp.length > 1 ? paramComp[1] : null;
        }
    }

}

