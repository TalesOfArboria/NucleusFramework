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


package com.jcwhatever.nucleus.managed.commands.utils;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.ICommand;
import com.jcwhatever.nucleus.managed.commands.ICommandDispatcher;
import com.jcwhatever.nucleus.managed.commands.IRegisteredCommand;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.arguments.ILocationHandler;
import com.jcwhatever.nucleus.managed.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.nucleus.managed.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.nucleus.managed.commands.mixins.IInitializableCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.messaging.ChatPaginator;
import com.jcwhatever.nucleus.managed.messaging.IMessenger;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.providers.permissions.Permissions;
import com.jcwhatever.nucleus.providers.regionselect.IRegionSelection;
import com.jcwhatever.nucleus.providers.regionselect.RegionSelection;
import com.jcwhatever.nucleus.regions.SimpleRegionSelection;
import com.jcwhatever.nucleus.storage.settings.ISettingsManager;
import com.jcwhatever.nucleus.storage.settings.PropertyDefinition;
import com.jcwhatever.nucleus.storage.settings.PropertyValueType;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

/**
 * Base implementation of a command
 *
 * <p>The command implementation must have an {@link CommandInfo} annotation.</p>
 */
public abstract class AbstractCommand implements IInitializableCommand, IPluginOwned {

    @Localizable private static final String _SAME_WORLD_REGION_SELECT =
            "You need to be in the same world as the region selection.";

    @Localizable private static final String _INVALID_REGION =
            "Invalid region. Both points must be in the same world.";

    @Localizable private static final String _SET_SELECTION_FAILED =
            "Failed to set region selection.";

    @Localizable private static final String _NO_REGION_SELECTED =
            "No cuboid region selection found. Please select a region first.";

    @Localizable private static final String _UNRECOGNIZED_PROPERTY =
            "The property '{0: property name}' is not a recognized setting.";

    @Localizable private static final String _CLEAR_PROPERTY_FAILED =
            "Failed to clear value on property '{0: property name}'.";

    @Localizable private static final String _CLEAR_PROPERTY_SUCCESS =
            "'{0: property name}' value cleared.";

    @Localizable private static final String _INVALID_PROPERTY_VALUE =
            "'{0: property value}' is not a valid value for the property called '{1: property name}'.";

    @Localizable private static final String _SET_PROPERTY_FAILED =
            "Failed to set property '{0: property name}'.";

    @Localizable private static final String _SET_PROPERTY_SUCCESS =
            "'{0: property value}' value changed to {1: property name}.";

    @Localizable private static final String _PROPERTY_DESCRIPTION =
            "Description for '{0: property value}':\n{GRAY}{1: description}";

    private static final Pattern FORMAT_ENABLE = Pattern.compile("\\{e}");

    private LinkedList<Class<? extends ICommand>> _registerQueue;
    private IRegisteredCommand _command;
    private IMessenger _msg;

    @Override
    public void init(IRegisteredCommand registeredCommand) {
        PreCon.notNull(registeredCommand);

        _command = registeredCommand;
        _msg = Nucleus.getMessengerFactory().get(registeredCommand.getPlugin());

        // register sub commands that were registered before the command was initialized
        if (_registerQueue != null) {
            while (!_registerQueue.isEmpty()) {
                Class<? extends ICommand> commandClass = _registerQueue.removeFirst();

                _command.registerCommand(commandClass);
            }
            _registerQueue = null;
        }
    }

    @Override
    public Plugin getPlugin() {
        return _command.getPlugin();
    }

    /**
     * Get the commands dispatcher.
     */
    public ICommandDispatcher getDispatcher() {
        return _command.getDispatcher();
    }

    /**
     * Get the registered command.
     */
    public IRegisteredCommand getRegistered() {
        return _command;
    }

    /**
     * Determine if a command sender can see the command in help.
     */
    protected boolean isHelpVisible(CommandSender sender) {

        // determine if the CommandSender has permission to use the command
        if (sender instanceof Player && !Permissions.has((Player) sender, _command.getPermission().getName()))
            return false;

        // determine if the commands is visible in help
        return _command.getInfo().isHelpVisible();
    }

    /**
     * Send a debug message to the console.
     *
     * @param msg     The message to send.
     * @param params  The message format parameters.
     */
    protected void debug(String msg, Object... params) {
        _msg.debug(msg, params);
    }

    /**
     * Tell the {@link org.bukkit.command.CommandSender} a generic message.
     */
    protected void tell(CommandSender sender, String msg, Object... params) {
        _msg.tell(sender, TextUtils.format(msg, params));
    }

    /**
     * Tell the {@link org.bukkit.command.CommandSender} that something is enabled
     * or disabled.
     *
     * <p>Use format tag {e} to specify where to place the word Enabled or Disabled.</p>
     */
    protected void tellEnabled(CommandSender sender, String msg, boolean isEnabled, Object...params) {
        PreCon.notNull(sender);
        PreCon.notNull(msg);
        PreCon.notNull(params);

        Matcher matcher = FORMAT_ENABLE.matcher(msg);
        msg = matcher.replaceAll("{" + params.length +'}');

        params = ArrayUtils.add(params, isEnabled ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled");

        _msg.tell(sender, msg, params);
    }

    /**
     * Tell the {@link org.bukkit.command.CommandSender} the command
     * executed the request successfully.
     */
    protected void tellSuccess(CommandSender sender, String msg, Object... params) {
        _msg.tell(sender, ChatColor.GREEN + msg, params);
    }

    /**
     * Tell the {@link org.bukkit.command.CommandSender} the command failed to
     * perform the requested task.
     */
    protected void tellError(CommandSender sender, String msg, Object... params) {
        _msg.tell(sender, ChatColor.RED + msg, params);
    }

    /**
     * Set the specified players region selection.
     * Handles error message if any.
     *
     * @param player  The player
     * @param p1      The first location of the selection.
     * @param p2      The second location of the selection.
     */
    protected boolean setRegionSelection(Player player, Location p1, Location p2) {
        PreCon.notNull(player);
        PreCon.notNull(p1);
        PreCon.notNull(p2);

        if (!player.getWorld().equals(p1.getWorld())) {
            tellError(player, NucLang.get(_SAME_WORLD_REGION_SELECT));
            return false;
        }

        if (!p1.getWorld().equals(p2.getWorld())) {
            tellError(player, NucLang.get(_INVALID_REGION));
            return false;
        }

        boolean isSuccess = Nucleus.getProviders()
                .getRegionSelection().setSelection(player, new SimpleRegionSelection(p1, p2));

        if (!isSuccess) {
            tellError(player, NucLang.get(_SET_SELECTION_FAILED));
            return false;
        }

        return true;
    }

    /**
     * Get the specified players current region selection.
     * Handles error message if any.
     *
     * @param player  The player
     *
     * @return  {@link SimpleRegionSelection} object that defines the selection.
     */
    @Nullable
    protected IRegionSelection getRegionSelection(Player player) {
        PreCon.notNull(player);

        IRegionSelection selection = RegionSelection.get(player);

        // Check for region selection
        if (selection == null) {
            tellError(player, NucLang.get(_NO_REGION_SELECTED));
            return null;
        }

        return selection;
    }

    /**
     * Create a new {@link ChatPaginator}.
     *
     * @param title  The paginator title.
     * @param args   The paginator title format arguments.
     */
    protected ChatPaginator createPagin(String title, Object... args) {
        return createPagin(6, title, args);
    }

    /**
     * Create a new {@link ChatPaginator}.
     *
     * @param itemsPerPage  The number of items per page.
     * @param title         The paginator title.
     * @param args          The paginator title format arguments.
     */
    protected ChatPaginator createPagin(int itemsPerPage, String title, Object... args) {
        return new ChatPaginator(getPlugin(), itemsPerPage, title, args);
    }

    /**
     * Clear a setting from a settings manager back to it's default value.
     * Handles error and success messages.
     *
     * @param sender           The command sender
     * @param settings         The settings manager that contains and defines possible settings.
     * @param args             The command arguments provided by the command sender.
     * @param propertyArgName  The name of the command argument parameter that contains the
     *                         property name of the setting.
     *
     * @return True if completed successfully.
     *
     * @throws InvalidArgumentException
     */
    protected void clearSetting(CommandSender sender, final ISettingsManager settings,
                             ICommandArguments args, String propertyArgName) throws InvalidArgumentException {

        final String settingName = args.getString(propertyArgName);

        PropertyDefinition defs = settings.getDefinitions().get(settingName);
        if (defs == null) {
            tellError(sender, NucLang.get(_UNRECOGNIZED_PROPERTY, settingName));
            return; // finish
        }

        if (!settings.set(settingName, null)) {
            tellError(sender, NucLang.get(_CLEAR_PROPERTY_FAILED, settingName));
            return; // finish
        }

        tellSuccess(sender, NucLang.get(_CLEAR_PROPERTY_SUCCESS, settingName));
    }

    /**
     * Set a setting into a settings manager using user command input. Handles error and success
     * messages to the user.
     *
     * @param sender           The command sender
     * @param settings         The settings manager that contains and defines possible settings.
     * @param args             The command arguments provided by the command sender.
     * @param propertyArgName  The name of the command argument parameter that contains the property
     *                         name of the setting.
     * @param valueArgName     The name of the command argument parameter that contains the value
     *                         of the property.
     *
     * @return  True if operation completed successfully.
     *
     * @throws InvalidArgumentException       If the value provided by the command sender is not valid.
     * @throws InvalidCommandSenderException  If the command sender cannot set the value due to sender type.
     */
    protected void setSetting(CommandSender sender, final ISettingsManager settings,
                           ICommandArguments args, String propertyArgName, String valueArgName)
            throws InvalidArgumentException, InvalidCommandSenderException {

        setSetting(sender, settings, args, propertyArgName, valueArgName, null);
    }

    /**
     * Set a setting into a settings manager using user command input. Handles error and success
     * messages to the user.
     *
     * @param sender           The command sender
     * @param settings         The settings manager that contains and defines possible settings.
     * @param args             The command arguments provided by the command sender.
     * @param propertyArgName  The name of the command argument parameter that contains the property name
     *                         of the setting.
     * @param valueArgName     The name of the command argument parameter that contains the value of the
     *                         property.
     * @param onSuccess        A runnable to run if the setting is successfully set.
     *
     * @return  True if operation completed successfully.
     *
     * @throws InvalidArgumentException       If the value provided by the command sender is not valid.
     * @throws InvalidCommandSenderException  If the command sender cannot set the value due to sender type.
     */
    protected void setSetting(CommandSender sender, final ISettingsManager settings,
                           ICommandArguments args, String propertyArgName,
                           String valueArgName, @Nullable final Runnable onSuccess)
            throws InvalidArgumentException, InvalidCommandSenderException {

        PreCon.notNull(sender);
        PreCon.notNull(settings);
        PreCon.notNull(args);
        PreCon.notNullOrEmpty(propertyArgName);
        PreCon.notNullOrEmpty(valueArgName);

        final String settingName = args.getString(propertyArgName);
        Object value;

        // get settings definitions
        final PropertyDefinition propertyDefinition = settings.getDefinitions().get(settingName);
        if (propertyDefinition == null) {
            tellError(sender, NucLang.get(_UNRECOGNIZED_PROPERTY, settingName));
            return; // finish
        }

        PropertyValueType valueType = propertyDefinition.getValueType();

        switch (valueType.getType()) {

            case LOCATION:

                // get location value to use
                args.getLocation(sender, valueArgName, new ILocationHandler() {

                    @Override
                    public void onLocationRetrieved (Player player, Location location) {

                        if (settings.set(settingName, location)) {
                            String successMessage = NucLang.get(_SET_PROPERTY_SUCCESS, settingName,
                                    TextUtils.formatLocation(location, true));

                            tellSuccess(player, successMessage);

                            if (onSuccess != null)
                                onSuccess.run();

                        } else {

                            tellError(player, NucLang.get(_SET_PROPERTY_FAILED, settingName));
                            tell(player, NucLang.get(_PROPERTY_DESCRIPTION,
                                    propertyDefinition.getName(), propertyDefinition.getDescription()));
                        }
                    }

                });
                return; // finish

            case ITEM_STACK_ARRAY:
                value = args.getItemStack(sender, valueArgName);
                break;

            default:
                value = args.getString(valueArgName);
                break;
        }

        // make sure the result is valid
        if (!settings.set(settingName, value)) {
            tellError(sender, NucLang.get(_INVALID_PROPERTY_VALUE, value, settingName));
            tell(sender, NucLang.get(_PROPERTY_DESCRIPTION,
                    propertyDefinition.getName(), propertyDefinition.getDescription()));
            return; // finish
        }

        tellSuccess(sender, NucLang.get(_SET_PROPERTY_SUCCESS, settingName, args.getString(valueArgName)));

        if (onSuccess != null)
            onSuccess.run();
    }

    /**
     * Register a sub command.
     *
     * @param subCommandClass  The sub command class.
     *
     * @return  True if the sub command was registered.
     */
    protected boolean registerCommand(Class<? extends ICommand> subCommandClass) {
        PreCon.notNull(subCommandClass);

        if (_command == null) {

            if (_registerQueue == null)
                _registerQueue = new LinkedList<>();

            _registerQueue.addLast(subCommandClass);
            return true;
        }

        return _command.registerCommand(subCommandClass);
    }

    /**
     * Get a sub command by name.
     *
     * @param commandName  The name of the command.
     *
     * @return  The command or null if not found.
     */
    @Nullable
    protected IRegisteredCommand getCommand(String commandName) {
        PreCon.notNull(commandName);

        if (_command == null)
            return null;

        return _command.getCommand(commandName);
    }
}