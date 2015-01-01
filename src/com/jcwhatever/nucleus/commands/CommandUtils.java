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

package com.jcwhatever.nucleus.commands;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.arguments.LocationResponse;
import com.jcwhatever.nucleus.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.nucleus.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.language.Localizable;
import com.jcwhatever.nucleus.messaging.IMessenger;
import com.jcwhatever.nucleus.messaging.MessengerFactory;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.regions.selection.IRegionSelection;
import com.jcwhatever.nucleus.regions.selection.RegionSelection;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

/*
 * 
 */
public class CommandUtils implements IPluginOwned {

    @Localizable
    private static final String _SAME_WORLD_REGION_SELECT =
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

    private final Plugin _plugin;
    protected IMessenger _msg;

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin
     */
    public CommandUtils (Plugin plugin) {
        _plugin = plugin;
        _msg = MessengerFactory.create(plugin);
    }

    /**
     * Get the owning plugin
     */
    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Send a debug message to the console.
     *
     * @param msg     The message to send.
     * @param params  The message format parameters.
     */
    public void debug(String msg, Object... params) {
        _msg.debug(msg, params);
    }

    /**
     * Tell the {@code CommandSender} a generic message.
     */
    public void tell(CommandSender sender, String msg, Object... params) {
        _msg.tell(sender, TextUtils.format(msg, params));
    }

    /**
     * Tell the {@code CommandSender} that something is enabled or disabled.
     *
     * <p>Use format code {e} to specify where to place the word Enabled or Disabled.</p>
     */
    public void tellEnabled(CommandSender sender, String msg, boolean isEnabled, Object...params) {
        PreCon.notNull(sender);
        PreCon.notNull(msg);
        PreCon.notNull(params);

        Matcher matcher = FORMAT_ENABLE.matcher(msg);
        msg = matcher.replaceAll("{" + params.length +'}');

        params = ArrayUtils.add(params, isEnabled ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled");

        _msg.tell(sender, msg, params);
    }

    /**
     * Tell the {@code CommandSender} the command executed the request successfully.
     */
    public void tellSuccess(CommandSender sender, String msg, Object... params) {
        _msg.tell(sender, ChatColor.GREEN + msg, params);
    }

    /**
     * Tell the {@code CommandSender} the command failed to perform the requested task.
     */
    public void tellError(CommandSender sender, String msg, Object... params) {
        _msg.tell(sender, ChatColor.RED + msg, params);
    }

    /**
     * Set the specified players region selection.
     * Handles error message if any.
     *
     * @param p   The player
     * @param p1  The first location of the selection.
     * @param p2  The second location of the selection.
     */
    public boolean setRegionSelection(Player p, Location p1, Location p2) {
        PreCon.notNull(p);
        PreCon.notNull(p1);
        PreCon.notNull(p2);

        if (!p.getWorld().equals(p1.getWorld())) {
            tellError(p, NucLang.get(_SAME_WORLD_REGION_SELECT));
            return false;
        }

        if (!p1.getWorld().equals(p2.getWorld())) {
            tellError(p, NucLang.get(_INVALID_REGION));
            return false;
        }

        boolean isSuccess = Nucleus.getProviderManager()
                .getRegionSelectionProvider().setSelection(p, new RegionSelection(p1, p2));

        if (!isSuccess) {
            tellError(p, NucLang.get(_SET_SELECTION_FAILED));
            return false;
        }

        return true;

    }

    /**
     * Get the specified players current region selection.
     * Handles error message if any.
     *
     * @param p  The player
     *
     * @return  {@code AreaSelection} object that defines the selection.
     */
    @Nullable
    public IRegionSelection getRegionSelection(Player p) {
        PreCon.notNull(p);

        IRegionSelection selection = RegionSelection.get(p);

        // Check for region selection
        if (selection == null) {
            tellError(p, NucLang.get(_NO_REGION_SELECTED));
            return null;
        }

        return selection;
    }

    /**
     * Clear a setting from a settings manager back to it's default value.
     * Handles error and success messages.
     *
     * @param sender           The command sender
     * @param settings         The settings manager that contains and defines possible settings.
     * @param args             The command arguments provided by the command sender.
     * @param propertyArgName  The name of the command argument parameter that contains the property name of the setting.
     *
     * @return True if completed successfully.
     *
     * @throws InvalidArgumentException
     */
    public void clearSetting(CommandSender sender, final ISettingsManager settings,
                                CommandArguments args, String propertyArgName) throws InvalidArgumentException {

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
     * @param propertyArgName  The name of the command argument parameter that contains the property name of the setting..
     * @param valueArgName     The name of the command argument parameter that contains the value of the property.
     *
     * @return  True if operation completed successfully.
     *
     * @throws InvalidArgumentException       If the value provided by the command sender is not valid.
     * @throws InvalidCommandSenderException  If the command sender cannot set the value due to sender type.
     */
    public void setSetting(CommandSender sender, final ISettingsManager settings,
                              CommandArguments args, String propertyArgName, String valueArgName)
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
     * @param propertyArgName  The name of the command argument parameter that contains the property name of the setting..
     * @param valueArgName     The name of the command argument parameter that contains the value of the property.
     * @param onSuccess        A runnable to run if the setting is successfully set.
     *
     * @return  True if operation completed successfully.
     *
     * @throws InvalidArgumentException       If the value provided by the command sender is not valid.
     * @throws InvalidCommandSenderException  If the command sender cannot set the value due to sender type.
     */
    public void setSetting(CommandSender sender, final ISettingsManager settings,
                              CommandArguments args, String propertyArgName,
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
                args.getLocation(sender, valueArgName, new LocationResponse() {

                    @Override
                    public void onLocationRetrieved (Player p, Location location) {

                        if (settings.set(settingName, location)) {
                            String successMessage = NucLang.get(_SET_PROPERTY_SUCCESS, settingName,
                                    TextUtils.formatLocation(location, true));

                            tellSuccess(p, successMessage);

                            if (onSuccess != null)
                                onSuccess.run();

                        } else {

                            tellError(p, NucLang.get(_SET_PROPERTY_FAILED, settingName));
                            tell(p, NucLang.get(_PROPERTY_DESCRIPTION,
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
}
