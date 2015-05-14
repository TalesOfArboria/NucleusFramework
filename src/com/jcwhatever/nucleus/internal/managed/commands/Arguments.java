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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.blockselect.IBlockSelectHandler;
import com.jcwhatever.nucleus.managed.blockselect.IBlockSelector.BlockSelectResult;
import com.jcwhatever.nucleus.managed.commands.IRegisteredCommand;
import com.jcwhatever.nucleus.managed.commands.arguments.ArgumentValueType;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArgument;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.arguments.ILocationHandler;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.exceptions.DuplicateArgumentException;
import com.jcwhatever.nucleus.managed.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.nucleus.managed.commands.exceptions.InvalidParameterException;
import com.jcwhatever.nucleus.managed.commands.exceptions.TooManyArgsException;
import com.jcwhatever.nucleus.managed.commands.parameters.IParameterDescriptions;
import com.jcwhatever.nucleus.managed.items.serializer.InvalidItemStackStringException;
import com.jcwhatever.nucleus.managed.messaging.IMessenger;
import com.jcwhatever.nucleus.utils.EnumUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.items.ItemStackMatcher;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import com.jcwhatever.nucleus.utils.items.MatchableItem;
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
class Arguments implements ICommandArguments {

    private final Plugin _plugin;
    private final IRegisteredCommand _command;
    private final IMessenger _msg;
    private final String[] _rawArguments;
    private final ArgumentParseResults _parseResults;
    private final IParameterDescriptions _paramDescriptions;

    /**
     * Constructor. Validates while assuming no arguments are provided.
     *
     * @param command  The command the arguments are being parsed for.
     *
     * @throws InvalidArgumentException    If a value provided is not valid.
     * @throws DuplicateArgumentException  If a parameter is defined in the arguments more than once.
     * @throws InvalidParameterException   If a parameter in the arguments is not found for the command.
     * @throws TooManyArgsException        If the provided arguments are more than is expected.
     */
    public Arguments(IRegisteredCommand command)
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
    public Arguments(IRegisteredCommand command, @Nullable String[] args)
            throws CommandException {

        PreCon.notNull(command);

        _plugin = command.getPlugin();
        _command = command;
        _msg = Nucleus.getMessengerFactory().get(_plugin);
        _paramDescriptions = command.getInfo().getParamDescriptions();


        // substitute empty string to remove the need for null checks while parsing
        if (args == null)
            args = ArrayUtils.EMPTY_STRING_ARRAY;

        _rawArguments = args;

        // parse arguments
        _parseResults = new ArgumentParser().parse(command, args);
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public String[] getRawArguments() {
        //noinspection ConstantConditions
        return _rawArguments.clone();
    }

    @Override
    public int staticSize() {
        return _parseResults.getStaticArgs().size();
    }

    @Override
    public int floatingSize () {
        return _parseResults.getFloatingArgs().size();
    }

    @Override
    @Nullable
    public Argument get(String parameterName) {
        PreCon.notNullOrEmpty(parameterName);

        return _parseResults.getArgMap().get(parameterName);
    }

    @Override
    public Iterator<ICommandArgument> iterator() {

        return new Iterator<ICommandArgument>() {

            int index = 0;

            @Override
            public boolean hasNext () {
                return index < staticSize() + floatingSize();
            }

            @Override
            public Argument next () {
                if (!hasNext())
                    throw new IndexOutOfBoundsException("No more elements.");

                Argument arg = index < staticSize()
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

    @Override
    public String getName(String parameterName) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        return getName(parameterName, 16);
    }

    @Override
    public String getName(String parameterName, int maxLen) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.greaterThanZero(maxLen);

        // get the raw argument
        String arg = getString(parameterName);

        // make sure the argument is a valid name
        if (!TextUtils.isValidName(arg, maxLen))
            throw invalidArg(parameterName, ArgumentValueType.NAME, maxLen);

        return arg;
    }

    @Override
    public String getString(String parameterName) throws InvalidArgumentException {
        return getString(parameterName, 1, Integer.MAX_VALUE);
    }

    @Override
    public String getString(String parameterName, int maxLen) throws InvalidArgumentException {
        return getString(parameterName, 1, maxLen);
    }

    @Override
    public String getString(String parameterName, int minLen, int maxLen) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.positiveNumber(minLen);
        PreCon.positiveNumber(maxLen);
        PreCon.isValid(maxLen >= minLen, "maxLen cannot be less than minLen.");

        // get the raw argument
        String value = getRawArgument(parameterName);
        if (value == null)
            throw invalidArg(parameterName, ArgumentValueType.STRING);

        if (value.length() < minLen || value.length() > maxLen)
            throw invalidArg(parameterName, ArgumentValueType.STRING);

        // make sure if pipes are used, that the argument is one of the possible values
        if (parameterName.indexOf('|') != -1) {
            String[] options = TextUtils.PATTERN_PIPE.split(parameterName, 0);
            for (String option : options) {
                if (value.equalsIgnoreCase(option))
                    return option;
            }
            throw invalidArg(parameterName, ArgumentValueType.STRING);
        }

        return value;
    }

    @Override
    public boolean getBoolean(String parameterName) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        Boolean flag = _parseResults.getFlag(parameterName);
        if (flag != null)
            return flag;

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // make sure the argument was provided
        if (arg == null)
            throw invalidArg(parameterName, ArgumentValueType.BOOLEAN);

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
                throw invalidArg(parameterName, ArgumentValueType.BOOLEAN);
        }
    }

    @Override
    public char getChar(String parameterName) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument
        String value = getRawArgument(parameterName);
        if (value == null)
            throw invalidArg(parameterName, ArgumentValueType.CHARACTER);

        // make sure if pipes are used, that the argument is one of the possible values
        if (parameterName.indexOf('|') != -1) {
            String[] options = StringUtils.split(parameterName, "|");
            for (String option : options) {
                if (value.equalsIgnoreCase(option)) {

                    if (option.length() != 1)
                        throw invalidArg(parameterName, ArgumentValueType.CHARACTER);

                    return option.charAt(0);
                }
            }
            throw invalidArg(parameterName, ArgumentValueType.CHARACTER);
        }

        // make sure the length of the value is exactly 1
        if (value.length() != 1)
            throw invalidArg(parameterName, ArgumentValueType.CHARACTER);

        return value.charAt(0);
    }

    @Override
    public byte getByte(String parameterName) throws InvalidArgumentException {
        return getByte(parameterName, Byte.MIN_VALUE, Byte.MAX_VALUE);
    }

    @Override
    public byte getByte(String parameterName, byte minRange, byte maxRange)
            throws InvalidArgumentException {

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
            throw invalidArg(parameterName, ArgumentValueType.BYTE, minRange, maxRange);
        }

        if (result < minRange || result > maxRange) {
            throw invalidArg(parameterName, ArgumentValueType.BYTE, minRange, maxRange);
        }

        return result;
    }

    @Override
    public short getShort(String parameterName) throws InvalidArgumentException {
        return getShort(parameterName, Short.MIN_VALUE, Short.MAX_VALUE);
    }

    @Override
    public short getShort(String parameterName, short minRange, short maxRange)
            throws InvalidArgumentException {

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
            throw invalidArg(parameterName, ArgumentValueType.SHORT, minRange, maxRange);
        }

        if (result < minRange || result > maxRange) {
            throw invalidArg(parameterName, ArgumentValueType.BYTE, minRange, maxRange);
        }

        return result;
    }

    @Override
    public int getInteger(String parameterName) throws InvalidArgumentException {
        return getInteger(parameterName, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public int getInteger(String parameterName, int minRange, int maxRange)
            throws InvalidArgumentException {
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
            throw invalidArg(parameterName, ArgumentValueType.INTEGER, minRange, maxRange);
        }

        if (result < minRange || result > maxRange) {
            throw invalidArg(parameterName, ArgumentValueType.INTEGER, minRange, maxRange);
        }

        return result;
    }

    @Override
    public long getLong(String parameterName) throws InvalidArgumentException {
        return getLong(parameterName, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    @Override
    public long getLong(String parameterName, long minRange, long maxRange)
            throws InvalidArgumentException {
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
            throw invalidArg(parameterName, ArgumentValueType.LONG, minRange, maxRange);
        }

        if (result < minRange || result > maxRange) {
            throw invalidArg(parameterName, ArgumentValueType.LONG, minRange, maxRange);
        }

        return result;
    }

    @Override
    public float getFloat(String parameterName) throws InvalidArgumentException {
        return getFloat(parameterName, -Float.MIN_NORMAL, Float.MAX_VALUE);
    }

    @Override
    public float getFloat(String parameterName, float minRange, float maxRange)
            throws InvalidArgumentException {

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
            throw invalidArg(parameterName, ArgumentValueType.FLOAT, minRange, maxRange);
        }

        if (result < minRange || result > maxRange) {
            throw invalidArg(parameterName, ArgumentValueType.FLOAT, minRange, maxRange);
        }

        return result;
    }

    @Override
    public double getDouble(String parameterName) throws InvalidArgumentException {
        return getDouble(parameterName, -Double.MIN_NORMAL, Double.MAX_VALUE);
    }

    @Override
    public double getDouble(String parameterName, double minRange, double maxRange)
            throws InvalidArgumentException {
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
            throw invalidArg(parameterName, ArgumentValueType.DOUBLE, minRange, maxRange);
        }

        if (result < minRange || result > maxRange) {
            throw invalidArg(parameterName, ArgumentValueType.DOUBLE, minRange, maxRange);
        }

        return result;
    }

    @Override
    public double getPercent(String parameterName) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // make sure the argument was provided
        if (arg == null)
            throw invalidArg(parameterName, ArgumentValueType.PERCENT);

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
            throw invalidArg(parameterName, ArgumentValueType.PERCENT);
        }
    }

    @Override
    public String[] getParams(String parameterName) throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument.
        String arg = getRawArgument(parameterName);
        if (arg == null)
            return new String[0];

        return TextUtils.PATTERN_SPACE.split(arg);
    }

    @Override
    public ItemStack[] getItemStack(@Nullable CommandSender sender, String parameterName)
            throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);

        // get the raw argument
        String arg = getRawArgument(parameterName);

        // make sure the argument was provided
        if (arg == null)
            throw invalidArg(parameterName, ArgumentValueType.ITEMSTACK);

        // Check for "inhand" keyword as argument
        if (arg.equalsIgnoreCase("inhand")) {

            // sender must be a player to use "inhand" keyword
            if (!(sender instanceof Player))
                throw invalidArg(parameterName, ArgumentValueType.ITEMSTACK);


            Player p = (Player)sender;

            ItemStack inhand = p.getItemInHand();

            // sender must have an item in hand
            if (inhand == null || inhand.getType() == Material.AIR) {
                throw invalidArg(parameterName, ArgumentValueType.ITEMSTACK);
            }

            return new ItemStack[] { inhand }; // finished
        }

        // Check for "hotbar" keyword as argument
        if (arg.equalsIgnoreCase("hotbar")) {

            // sender must be a player to use "hotbar" keyword
            if (!(sender instanceof Player))
                throw invalidArg(parameterName, ArgumentValueType.ITEMSTACK);

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
            if (!(sender instanceof Player))
                throw invalidArg(parameterName, ArgumentValueType.ITEMSTACK);


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
            throw invalidArg(parameterName, ArgumentValueType.ITEMSTACK);

        // return result
        return stacks;
    }

    @Override
    public void getLocation (final CommandSender sender, String parameterName,
                             final ILocationHandler locationHandler)
            throws InvalidArgumentException {
        PreCon.notNull(sender);
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(locationHandler);

        String arg = getString(parameterName);

        // command sender must be a player
        if (!(sender instanceof Player))
            throw invalidArg(parameterName, ArgumentValueType.LOCATION);

        Player p = (Player)sender;

        // use players current location
        if (arg.equalsIgnoreCase("current")) {
            locationHandler.onLocationRetrieved(p, p.getLocation());
        }

        // select location
        else if (arg.equalsIgnoreCase("select")) {

            _msg.tell(p, "Click a block to select it's location...");

            Nucleus.getBlockSelector().query(p, new IBlockSelectHandler() {

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
            throw invalidArg(parameterName, ArgumentValueType.LOCATION);
        }
    }

    @Override
    public <T extends Enum<T>> T getEnum(String parameterName,  Class<T> enumClass)
            throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(enumClass);

        return getEnum(parameterName, enumClass, enumClass.getEnumConstants());
    }

    @Override
    public <T extends Enum<T>> T getEnum(
            String parameterName,  Class<T> enumClass, T[] validValues)
            throws InvalidArgumentException {

        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(enumClass);
        PreCon.notNull(validValues);

        // get raw argument
        String arg = getString(parameterName);

        T evalue = EnumUtils.searchEnum(arg, enumClass);

        if (evalue == null) {
            throw CommandException.invalidArgument(_command,
                    _paramDescriptions.get(parameterName, validValues)
            );
        }

        // make sure the enum constant is valid
        for (T validValue : validValues) {
            if (validValue == evalue)
                return evalue;
        }

        throw CommandException.invalidArgument(_command,
                _paramDescriptions.get(parameterName, validValues)
        );
    }

    @Override
    public <T extends Enum<T>> T getEnum(
            String parameterName, Class<T> enumClass, Collection<T> validValues)
            throws InvalidArgumentException {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(enumClass);
        PreCon.notNull(validValues);

        // get the raw argument
        String arg = getString(parameterName);

        T evalue = EnumUtils.searchEnum(arg, enumClass);

        if (evalue == null) {
            throw CommandException.invalidArgument(_command,
                    _paramDescriptions.get(parameterName, validValues)
            );
        }

        // make sure the enum constant is valid
        for (T val : validValues) {
            if (val == evalue)
                return evalue;
        }

        throw CommandException.invalidArgument(_command,
                _paramDescriptions.get(parameterName, validValues)
        );
    }

    @Override
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

    @Override
    public boolean hasChar(String parameterName) {
        PreCon.notNullOrEmpty(parameterName);

        String value = getRawArgument(parameterName);

        if (value == null)
            return false;

        if (value.length() != 1)
            return false;

        return true;
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public boolean isDefaultValue(String parameterName) {

        Argument param = _parseResults.getArgMap().get(parameterName);
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

        Argument param = _parseResults.getArgMap().get(parameterName);
        if (param == null) {
            throw new RuntimeException("A parameter named '" + parameterName +
                    "' is not defined by the command: " + _command.getClass().getName());
        }

        return param.getValue();
    }

    private InvalidArgumentException invalidArg(
            String parameterName, ArgumentValueType type, Object... args) {

        return CommandException.invalidArgument(_command,
                _paramDescriptions.get(parameterName, type, args)
        );
    }
}

