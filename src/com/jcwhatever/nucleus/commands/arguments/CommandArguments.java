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

import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.nucleus.commands.exceptions.InvalidParameterException;
import com.jcwhatever.nucleus.commands.exceptions.DuplicateArgumentException;
import com.jcwhatever.nucleus.commands.exceptions.TooManyArgsException;
import com.jcwhatever.nucleus.commands.parameters.ParameterDescriptions;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.utils.items.ItemStackMatcher;
import com.jcwhatever.nucleus.utils.items.MatchableItem;
import com.jcwhatever.nucleus.utils.items.serializer.InvalidItemStackStringException;
import com.jcwhatever.nucleus.messaging.IMessenger;
import com.jcwhatever.nucleus.messaging.MessengerFactory;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.utils.player.PlayerBlockSelect;
import com.jcwhatever.nucleus.utils.player.PlayerBlockSelect.BlockSelectResult;
import com.jcwhatever.nucleus.utils.player.PlayerBlockSelect.PlayerBlockSelectHandler;
import com.jcwhatever.nucleus.utils.EnumUtils;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;
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
 * or required conditions for parsing the object are not met, an {@link InvalidArgumentException}
 * is thrown and should be caught by the command handler.</p>
 *
 * <p>Other exceptions thrown are {@link DuplicateArgumentException}, {@link InvalidParameterException}
 * and {@link TooManyArgsException}.</p>
 */
public class CommandArguments implements Iterable<CommandArgument>, IPluginOwned {

    private final Plugin _plugin;
    private final AbstractCommand _command;
    private final IMessenger _msg;
    private final String[] _rawArguments;
    private final ArgumentParseResults _parseResults;
    private final ParameterDescriptions _paramDescriptions;

    /**
     * Constructor. Validates while assuming no arguments are provided.
     *
     * @param command      The command the arguments are being parsed for.
     *
     * @throws InvalidArgumentException    If a value provided is not valid.
     * @throws DuplicateArgumentException  If a parameter is defined in the arguments more than once.
     * @throws InvalidParameterException   If a parameter in the arguments is not found for the command.
     * @throws TooManyArgsException        If the provided arguments are more than is expected.
     */
    public CommandArguments(AbstractCommand command)
            throws CommandException {

        this(command, null);
    }

    /**
     * Constructor. Parses the provided arguments.
     *
     * @param command  The commands info annotation container.
     * @param args     The command arguments.
     *
     * @throws InvalidArgumentException    If a value provided is not valid.
     * @throws DuplicateArgumentException  If a parameter is defined in the arguments more than once.
     * @throws InvalidParameterException   If a parameter int the arguments is not found for the command.
     * @throws TooManyArgsException        If the provided arguments are more than is expected.
     */
    public CommandArguments(AbstractCommand command, @Nullable String[] args)
            throws CommandException {

        PreCon.notNull(command);

        _plugin = command.getPlugin();
        _command = command;
        _msg = MessengerFactory.get(_plugin);
        _paramDescriptions = command.getInfo().getParamDescriptions();


        // substitute empty string to remove the need for null checks while parsing
        if (args == null)
            args = ArrayUtils.EMPTY_STRING_ARRAY;

        _rawArguments = args;

        // parse arguments
        _parseResults = new ArgumentParser().parse(command, args);
    }

    /**
     * Get the owning plugin.
     */
    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the raw unparsed arguments.
     */
    public String[] getRawArguments() {
        //noinspection ConstantConditions
        return _rawArguments.clone();
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
     * Get a {@link CommandArgument} by parameter name
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
                        : _parseResults.getFloatingArgs().get(index - (staticSize() > 0 ? 1 : 0));

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
     * Get an argument as a {@link String} and ensures it meets proper naming conventions.
     *
     * <p>The name must be alphanumeric characters only, must not start with a number,
     * no spaces, underscores are allowed. Must be no more than 16 characters in length.</p>
     *
     * @param parameterName  The name of the parameter to get
     *
     * @throws InvalidArgumentException  If the argument for the parameter is not a valid name.
     */
    public String getName(String parameterName) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        return getName(parameterName, 16);
    }

    /**
     * Get an argument as a {@link String} and ensures it meets proper naming conventions.
     *
     * <p>The name must be alphanumeric characters only, must not start with a number,
     * no spaces, underscores are allowed. </p>
     *
     * @param parameterName  The name of the arguments parameter
     * @param maxLen         The maximum length of the value
     *
     * @throws InvalidArgumentException  If the argument for the parameter is not a valid name.
     */
    public String getName(String parameterName, int maxLen) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.greaterThanZero(maxLen);

        // get the raw argument
        String arg = getString(parameterName);

        // make sure the argument is a valid name
        if (!TextUtils.isValidName(arg, maxLen)) {
            invalidArg(parameterName, ArgumentValueType.NAME, maxLen);
        }

        return arg;
    }

    /**
     * Get an argument as a string.
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws InvalidArgumentException  If the argument is not present or is not an expected value.
     */
    public String getString(String parameterName) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument
        String value = getRawArgument(parameterName);
        if (value == null) {
            invalidArg(parameterName, ArgumentValueType.STRING);
        }

        // make sure if pipes are used, that the argument is one of the possible values
        if (parameterName.indexOf('|') != -1) {
            String[] options = TextUtils.PATTERN_PIPE.split(parameterName, 0);
            for (String option : options) {
                if (value.equalsIgnoreCase(option))
                    return option;
            }
            invalidArg(parameterName, ArgumentValueType.STRING);
        }

        return value;
    }

    /**
     * Gets an argument as a boolean.
     *
     * <p>true,on,yes = true</p>
     * <p>false,off,no = false</p>
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws InvalidArgumentException  If the argument cannot be parsed or recognized as a boolean.
     */
    public boolean getBoolean(String parameterName) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // make sure the argument was provided
        if (arg == null) {

            Boolean flag = _parseResults.getFlag(parameterName);

            if (flag == null) {
                invalidArg(parameterName, ArgumentValueType.BOOLEAN);
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
                invalidArg(parameterName, ArgumentValueType.BOOLEAN);
                return false;
        }
    }

    /**
     * Get an argument as a character.
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws InvalidArgumentException  If the argument is not present, is not an
     * expected value, or is more than a single character.
     */
    public char getChar(String parameterName) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument
        String value = getRawArgument(parameterName);
        if (value == null) {
            invalidArg(parameterName, ArgumentValueType.CHARACTER);
        }

        // make sure if pipes are used, that the argument is one of the possible values
        if (parameterName.indexOf('|') != -1) {
            String[] options = StringUtils.split(parameterName, "|");
            for (String option : options) {
                if (value.equalsIgnoreCase(option)) {

                    if (option.length() != 1) {
                        invalidArg(parameterName, ArgumentValueType.CHARACTER);
                    }

                    return option.charAt(0);
                }
            }
            invalidArg(parameterName, ArgumentValueType.CHARACTER);
        }

        // make sure the length of the value is exactly 1
        if (value.length() != 1)
            invalidArg(parameterName, ArgumentValueType.CHARACTER);

        return value.charAt(0);
    }

    /**
     * Gets an argument as a byte.
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws InvalidArgumentException  If the argument is not parsable into a byte value.
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
     * @throws InvalidArgumentException
     */
    public byte getByte(String parameterName, byte minRange, byte maxRange)
            throws InvalidArgumentException {

        PreCon.notNullOrEmpty(parameterName);

        byte result = 0;

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // parse argument to byte
        try {
            //noinspection ConstantConditions
            result = Byte.parseByte(arg);
        }
        catch (NullPointerException | NumberFormatException nfe) {
            invalidArg(parameterName, ArgumentValueType.BYTE, minRange, maxRange);
        }

        if (result < minRange || result > maxRange) {
            invalidArg(parameterName, ArgumentValueType.BYTE, minRange, maxRange);
        }

        return result;
    }

    /**
     * Gets an argument as a short.
     *
     * @param parameterName  The name of the arguments parameter.
     *
     * @throws InvalidArgumentException  If the argument is not parsable into a short value.
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
     * @throws InvalidArgumentException  If the argument is not parsable into a short value or does not meet range specs.
     */
    public short getShort(String parameterName, short minRange, short maxRange)
            throws InvalidArgumentException {

        PreCon.notNullOrEmpty(parameterName);

        short result = 0;

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // parse argument to short
        try {
            //noinspection ConstantConditions
            result = Short.parseShort(arg);
        }
        catch (NullPointerException | NumberFormatException nfe) {
            invalidArg(parameterName, ArgumentValueType.SHORT, minRange, maxRange);
        }

        if (result < minRange || result > maxRange) {
            invalidArg(parameterName, ArgumentValueType.BYTE, minRange, maxRange);
        }

        return result;
    }

    /**
     * Gets an argument as an integer.
     *
     * @param parameterName  The name of the arguments parameter.
     *
     * @throws InvalidArgumentException  If the argument is not parsable into an integer.
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
     * @throws InvalidArgumentException  If the argument is not parsable into an integer or does not
     * meet range specs.
     */
    public int getInteger(String parameterName, int minRange, int maxRange) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        int result = 0;

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // parse argument to integer
        try {
            //noinspection ConstantConditions
            result = Integer.parseInt(arg);
        }
        catch (NullPointerException | NumberFormatException nfe) {
            invalidArg(parameterName, ArgumentValueType.INTEGER, minRange, maxRange);
        }

        if (result < minRange || result > maxRange) {
            invalidArg(parameterName, ArgumentValueType.INTEGER, minRange, maxRange);
        }

        return result;
    }

    /**
     * Gets an argument as a 64 bit number.
     *
     * @param parameterName  The name of the arguments parameter.
     *
     * @throws InvalidArgumentException  If the argument is not parsable into a long value.
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
     * @throws InvalidArgumentException  If the argument is not parsable into a long value or does
     * not meet range specs.
     */
    public long getLong(String parameterName, long minRange, long maxRange) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        long result = 0;

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // parse argument to long
        try {
            //noinspection ConstantConditions
            result = Long.parseLong(arg);
        }
        catch (NullPointerException | NumberFormatException nfe) {
            invalidArg(parameterName, ArgumentValueType.LONG, minRange, maxRange);
        }

        if (result < minRange || result > maxRange) {
            invalidArg(parameterName, ArgumentValueType.LONG, minRange, maxRange);
        }

        return result;
    }

    /**
     * Gets an argument as a float.
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws InvalidArgumentException  If the argument is not parsable into a float value.
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
     * @throws InvalidArgumentException  If the argument is not parsable into a float or does not
     * meet range specs.
     */
    public float getFloat(String parameterName, float minRange, float maxRange)
            throws InvalidArgumentException {

        PreCon.notNullOrEmpty(parameterName);

        float result = 0;

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
            invalidArg(parameterName, ArgumentValueType.FLOAT, minRange, maxRange);
        }

        if (result < minRange || result > maxRange) {
            invalidArg(parameterName, ArgumentValueType.FLOAT, minRange, maxRange);
        }

        return result;
    }

    /**
     * Gets an argument as a double.
     *
     * @param parameterName  The name of the arguments parameter.
     *
     * @throws InvalidArgumentException  If the argument is not parsable into a double value.
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
     * @throws InvalidArgumentException  If the argument is not parsable into a double value or does not meet range specs.
     */
    public double getDouble(String parameterName, double minRange, double maxRange) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        double result = 0;

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
            invalidArg(parameterName, ArgumentValueType.DOUBLE, minRange, maxRange);
        }

        if (result < minRange || result > maxRange) {
            invalidArg(parameterName, ArgumentValueType.DOUBLE, minRange, maxRange);
        }

        return result;
    }

    /**
     * Gets an argument as a double. "%" characters are ignored.
     *
     * @param parameterName  The name of the arguments parameter
     *
     * @throws InvalidArgumentException  If the argument cannot be parsed or recognizes as a double.
     */
    public double getPercent(String parameterName) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // make sure the argument was provided
        if (arg == null)
            invalidArg(parameterName, ArgumentValueType.PERCENT);

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
            invalidArg(parameterName, ArgumentValueType.PERCENT);
            return 0;
        }
    }

    /**
     * Gets an arguments raw {@link String} value, splits it at the space character
     * and returns it as a {@link String[]}
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
     * Gets an argument as an {@link ItemStack[]}.
     *
     * <p>The supplied argument can be a parsable string representing an {@link ItemStack}</p>
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
     * @param sender         The {@link CommandSender} who executed the command
     * @param parameterName  The name of the arguments parameter
     *
     * @throws InvalidArgumentException If the argument is not a recognized keyword and cannot be parsed to an {@link ItemStack}.
     */
    public ItemStack[] getItemStack(@Nullable CommandSender sender, String parameterName) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // make sure the argument was provided
        if (arg == null)
            invalidArg(parameterName, ArgumentValueType.ITEMSTACK);

        // Check for "inhand" keyword as argument
        if (arg.equalsIgnoreCase("inhand")) {

            // sender must be a player to use "inhand" keyword
            if (!(sender instanceof Player)) {
                invalidArg(parameterName, ArgumentValueType.ITEMSTACK);
            }

            Player p = (Player)sender;

            ItemStack inhand = p.getItemInHand();

            // sender must have an item in hand
            if (inhand == null || inhand.getType() == Material.AIR) {
                invalidArg(parameterName, ArgumentValueType.ITEMSTACK);
            }

            return new ItemStack[] { inhand }; // finished
        }

        // Check for "hotbar" keyword as argument
        if (arg.equalsIgnoreCase("hotbar")) {

            // sender must be a player to use "hotbar" keyword
            if (!(sender instanceof Player)) {
                invalidArg(parameterName, ArgumentValueType.ITEMSTACK);
            }

            Player p = (Player)sender;

            Inventory inventory = p.getInventory();

            Set<MatchableItem> wrappers = new HashSet<MatchableItem>(9);

            // iterate and add players hotbar items to wrapper set.
            for (int i=0; i < 9; i++) {
                ItemStack item = inventory.getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    MatchableItem wrapper = new MatchableItem(item, ItemStackMatcher.getDefault());
                    wrappers.add(wrapper);
                }
            }

            // generate result {@link ItemStack} array
            ItemStack[] result = new ItemStack[wrappers.size()];
            int index = 0;
            for (MatchableItem wrapper : wrappers) {
                result[index] = wrapper.getItem();
                index++;
            }

            return result; // finished
        }

        // Check for "inventory" keyword as argument
        if (arg.equalsIgnoreCase("inventory")) {

            // sender must be player to use "chest" keyword
            if (!(sender instanceof Player)) {
                invalidArg(parameterName, ArgumentValueType.ITEMSTACK);
            }

            Player p = (Player)sender;

            Inventory inventory = p.getInventory();

            int len = inventory.getContents().length;
            Set<MatchableItem> wrappers = new HashSet<MatchableItem>(len);

            // iterate and add players chest items to wrapper set
            for (int i=0; i < len; i++) {
                ItemStack item = inventory.getItem(i);
                if (item != null && item.getType() != Material.AIR) {

                    // use item wrapper to prevent duplicates in set
                    MatchableItem wrapper = new MatchableItem(item);
                    wrappers.add(wrapper);
                }
            }

            // generate result {@link ItemStack} array.
            ItemStack[] result = new ItemStack[wrappers.size()];
            int index = 0;
            for (MatchableItem wrapper : wrappers) {
                result[index] = wrapper.getItem();
                index++;
            }

            return result; // finished
        }

        // no keywords used, attempt to parse the argument into an {@link ItemStack} array.
        ItemStack[] stacks = null;

        try {
            stacks = ItemStackUtils.parse(arg);
        } catch (InvalidItemStackStringException ignore) {
            // do nothing
        }

        if (stacks == null)
            invalidArg(parameterName, ArgumentValueType.ITEMSTACK);

        // return result
        return stacks;
    }


    /**
     * Gets an argument as a location.
     *
     * <p>Possible values are "current" or "select"</p>
     *
     * <p>If the argument value is "current", the players current location is returned via
     * the {@link LocationResponse}.</p>
     *
     * <p>If the argument value is "select", the player is asked to click on the location
     * to be selected and the value is return via the {@link LocationResponse}.</p>
     *
     * <p>If the {@link CommandSender} is not a player, the argument is always considered invalid.</p>
     *
     * @param sender           The {@link CommandSender} who executed the command
     * @param parameterName    The name of the arguments parameter
     * @param locationHandler  The {@link LocationResponse} responsible for dealing with the return location.
     *
     * @throws InvalidArgumentException If the sender is not a player, or the argument is not "current" or "select"
     */
    public void getLocation (final CommandSender sender, String parameterName,
                             final LocationResponse locationHandler) throws InvalidArgumentException {
        PreCon.notNull(sender);
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(locationHandler);

        String arg = getString(parameterName);

        // command sender must be a player
        if (!(sender instanceof Player)) {
            invalidArg(parameterName, ArgumentValueType.LOCATION);
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
                public BlockSelectResult onBlockSelect(Player player, Block selectedBlock, Action clickAction) {

                    Location location = selectedBlock.getLocation();

                    String message = NucLang.get("Location selected: {0} ", TextUtils.formatLocation(location, true));
                    _msg.tell(player, message);

                    locationHandler.onLocationRetrieved(player, location);

                    return BlockSelectResult.FINISHED;
                }

            });
        }
        else {
            invalidArg(parameterName, ArgumentValueType.LOCATION);
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
     * @throws InvalidArgumentException  If the argument is not the name of one of the enums constants.
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
     * @throws InvalidArgumentException If the argument is not the name of one of the valid enum constants.
     */
    public <T extends Enum<T>> T getEnum(String parameterName,  Class<T> enumClass, T[] validValues) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(enumClass);
        PreCon.notNull(validValues);

        // get raw argument
        String arg = getString(parameterName);

        T evalue = EnumUtils.searchEnum(arg, enumClass);

        if (evalue == null) {
            invalidArg(parameterName, ArgumentValueType.LOCATION);

            CommandException.invalidArgument(_command,
                    _paramDescriptions.get(parameterName, validValues)
            );
        }

        // make sure the enum constant is valid
        for (T validValue : validValues) {
            if (validValue == evalue)
                return evalue;
        }

        CommandException.invalidArgument(_command,
                _paramDescriptions.get(parameterName, validValues)
        );

        return null;
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
     * @throws InvalidArgumentException  If the argument is not one of the valid enum constants.
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
            CommandException.invalidArgument(_command,
                    _paramDescriptions.get(parameterName, validValues)
            );
        }

        // make sure the enum constant is valid
        for (T val : validValues) {
            if (val == evalue)
                return evalue;
        }

        CommandException.invalidArgument(_command,
                _paramDescriptions.get(parameterName, validValues)
        );
        return null;
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
     * Determine if an argument is provided and is a single character.
     *
     * @param parameterName  The name of the arguments parameter
     */
    public boolean hasChar(String parameterName) {
        PreCon.notNullOrEmpty(parameterName);

        String value = getRawArgument(parameterName);

        if (value == null)
            return false;

        if (value.length() != 1)
            return false;

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
     * Determine if an argument is provided and can be used as an integer value.
     *
     * @param parameterName  The name of the arguments parameter
     */
    public boolean hasInteger(String parameterName) {
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
     * Determine if the value of an argument was omitted and
     * the default value was inserted.
     *
     * @param parameterName  The name of the arguments parameter.
     */
    public boolean isDefaultValue(String parameterName) {

        CommandArgument param = _parseResults.getArgMap().get(parameterName);
        if (param == null) {
            throw new RuntimeException("A parameter named '" + parameterName +
                    "' is not defined by the command: " + _command.getClass().getName());
        }

        return param.isDefaultValue();
    }

    /**
     * Get the raw argument provided for a parameter.
     *
     * @param parameterName  The name of the arguments parameter.
     */
    @Nullable
    private String getRawArgument(String parameterName) {

        CommandArgument param = _parseResults.getArgMap().get(parameterName);
        if (param == null)
            return null;

        return param.getValue();
    }

    private void invalidArg(String parameterName, ArgumentValueType type, Object... args) throws InvalidArgumentException {
        CommandException.invalidArgument(_command,
                _paramDescriptions.get(parameterName, type, args)
        );
    }
}

