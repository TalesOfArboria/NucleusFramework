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


package com.jcwhatever.bukkit.generic.commands;

import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.arguments.LocationResponse;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.messaging.Messenger;
import com.jcwhatever.bukkit.generic.regions.data.RegionSelection;
import com.jcwhatever.bukkit.generic.storage.settings.ISettingsManager;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.generic.storage.settings.ValidationResults;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import com.jcwhatever.bukkit.generic.utils.WorldEditUtils;
import com.sk89q.worldedit.bukkit.selections.Selection;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

/**
 * Utilities used by inheriting implementations to reduce redundant code
 * in commands and provide a central place to modify complex but generic
 * functionality.
 */
public abstract class AbstractCommandUtils {

    @Localizable private static final String _INSTALL_WORLD_EDIT = "Please install World Edit plugin.";
    @Localizable private static final String _SAME_WORLD_REGION_SELECT = "You need to be in the same world as the region selection.";
    @Localizable private static final String _INVALID_REGION = "Invalid region. Both points must be in the same world.";
    @Localizable private static final String _SET_SELECTION_FAILED = "Failed to set world edit selection.";
    @Localizable private static final String _NO_REGION_SELECTED = "No cuboid region selection found. Use World Edit to select an area.";
    @Localizable private static final String _REGION_SELECTION_INCOMPLETE = "Area selection incomplete. Please select both points of the cuboid region.";
    @Localizable private static final String _UNRECOGNIZED_PROPERTY = "The property '{0}' is not a recognized setting.";
    @Localizable private static final String _CLEAR_PROPERTY_FAILED = "Failed to clear value on property '{0}'.";
    @Localizable private static final String _CLEAR_PROPERTY_SUCCESS = "'{0}' value cleared.";
    @Localizable private static final String _INVALID_PROPERTY_VALUE = "'{0}' is not a valid value for the property called '{1}'.";
    @Localizable private static final String _SET_PROPERTY_FAILED = "Failed to set property '{0}'.";
    @Localizable private static final String _SET_PROPERTY_SUCCESS = "'{0}' value changed to {1}.";

    private static final Pattern FORMAT_ENABLE = Pattern.compile("\\{e}");

    Plugin _plugin;

    AbstractCommandUtils() {}

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin
     */
    public AbstractCommandUtils (Plugin plugin) {
        _plugin = plugin;
    }

    /**
     * Get the owning plugin
     */
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Tell the {@code CommandSender} a generic message.
     */
    protected void tell(CommandSender sender, String msg, Object... params) {
        Messenger.tell(_plugin, sender, TextUtils.format(msg, params));
    }

    /**
     * Tell the {@code CommandSender} that something is enabled or disabled.
     *
     * <p>Use format code {e} to specify where to place the word Enabled or Disabled.</p>
     */
    protected void tellEnabled(CommandSender sender, String msg, boolean isEnabled, Object...params) {
        PreCon.notNull(sender);
        PreCon.notNull(msg);
        PreCon.notNull(params);

        Matcher matcher = FORMAT_ENABLE.matcher(msg);
        msg = matcher.replaceAll("{" + params.length +'}');

        params = ArrayUtils.add(params, isEnabled ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled");

        Messenger.tell(_plugin, sender, msg, params);
    }

    /**
     * Tell the {@code CommandSender} the command executed the request successfully.
     */
    protected void tellSuccess(CommandSender sender, String msg, Object... params) {
        Messenger.tell(_plugin, sender, ChatColor.GREEN + msg, params);
    }

    /**
     * Tell the {@code CommandSender} the command failed to perform the requested task.
     */
    protected void tellError(CommandSender sender, String msg, Object... params) {
        Messenger.tell(_plugin, sender, ChatColor.RED + msg, params);
    }


    /**
     * Determine if World Edit is installed.
     * Handles error message if world edit is not installed and
     * command sender is provided.
     *
     * @param sender  The command sender.
     */
    protected boolean isWorldEditInstalled(@Nullable CommandSender sender) {
        // Check that World Edit is installed
        if (!WorldEditUtils.isWorldEditInstalled()) {
            if (sender != null) {
                tellError(sender, Lang.get(_INSTALL_WORLD_EDIT));
            }
            return false;
        }
        return true;
    }

    /**
     * Set the specified players world edit selection.
     * Handles error message if any.
     *
     * @param p   The player
     * @param p1  The first location of the selection.
     * @param p2  The second location of the selection.
     */
    protected boolean setWorldEditSelection(Player p, Location p1, Location p2) {
        PreCon.notNull(p);
        PreCon.notNull(p1);
        PreCon.notNull(p2);

        // Check that World Edit is installed
        if (!isWorldEditInstalled(p)) {
            return false;
        }

        if (!p.getWorld().equals(p1.getWorld())) {
            tellError(p, Lang.get(_SAME_WORLD_REGION_SELECT));
            return false;
        }

        if (!p1.getWorld().equals(p2.getWorld())) {
            tellError(p, Lang.get(_INVALID_REGION));
            return false;
        }

        if (!WorldEditUtils.setWorldEditSelection(p, p1, p2)) {
            tellError(p, Lang.get(_SET_SELECTION_FAILED));
            return false;
        }

        return true;

    }

    /**
     * Get the specified players current world edit selection.
     * Handles error message if any.
     *
     * @param p  The player
     *
     * @return  {@code AreaSelection} object that defines the selection.
     */
    @Nullable
    protected RegionSelection getWorldEditSelection(Player p) {
        PreCon.notNull(p);

        // Check that World Edit is installed
        if (!isWorldEditInstalled(p)) {
            return null;
        }

        Selection sel = WorldEditUtils.getWorldEditSelection(p);

        // Check for World Edit selection
        if (sel == null) {
            tellError(p, Lang.get(_NO_REGION_SELECTED));
            return null;
        }

        if (sel.getMinimumPoint() == null || sel.getMaximumPoint() == null ||
                !sel.getMinimumPoint().getWorld().equals(sel.getMaximumPoint().getWorld())) {

            tellError(p, Lang.get(_REGION_SELECTION_INCOMPLETE));
            return null; // finish
        }

        return new RegionSelection(sel.getMinimumPoint(), sel.getMaximumPoint());
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
     * @throws InvalidValueException
     */
    protected void clearSetting(CommandSender sender, final ISettingsManager settings,
                                CommandArguments args, String propertyArgName) throws InvalidValueException {

        final String settingName = args.getString(propertyArgName);

        SettingDefinitions defs = settings.getPossibleSettings();
        if (!defs.containsKey(settingName)) {
            tellError(sender, Lang.get(_UNRECOGNIZED_PROPERTY, settingName));
            return; // finish
        }

        ValidationResults result = settings.set(settingName, null);

        if (!result.isValid()) {

            if (!result.tellMessage(_plugin, sender)) {
                tellError(sender, Lang.get(_CLEAR_PROPERTY_FAILED, settingName));
            }

            return; // finish
        }

        tellSuccess(sender, Lang.get(_CLEAR_PROPERTY_SUCCESS, settingName));
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
     * @throws InvalidValueException          If the value provided by the command sender is not valid.
     * @throws InvalidCommandSenderException  If the command sender cannot set the value due to sender type.
     */
    protected void setSetting(CommandSender sender, final ISettingsManager settings,
                              CommandArguments args, String propertyArgName, String valueArgName)
            throws InvalidValueException, InvalidCommandSenderException {
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
     * @throws InvalidValueException          If the value provided by the command sender is not valid.
     * @throws InvalidCommandSenderException  If the command sender cannot set the value due to sender type.
     */
    protected void setSetting(CommandSender sender, final ISettingsManager settings,
                              CommandArguments args, String propertyArgName,
                              String valueArgName, @Nullable final Runnable onSuccess)
            throws InvalidValueException, InvalidCommandSenderException {
        PreCon.notNull(sender);
        PreCon.notNull(settings);
        PreCon.notNull(args);
        PreCon.notNullOrEmpty(propertyArgName);
        PreCon.notNullOrEmpty(valueArgName);

        final String settingName = args.getString(propertyArgName);
        Object value;

        // get settings definitions
        SettingDefinitions defs = settings.getPossibleSettings();
        if (!defs.containsKey(settingName)) {
            tellError(sender, Lang.get(_UNRECOGNIZED_PROPERTY, settingName));
            return; // finished
        }

        Class<?> valueType = defs.get(settingName).getValueType();

        // Special case: Location
        if (valueType.isAssignableFrom(Location.class)) {

            // get location value to use
            args.getLocation(sender, valueArgName, new LocationResponse() {

                @Override
                public void onLocationRetrieved (Player p, Location location) {

                    ValidationResults result = settings.set(settingName, location);

                    if (result.isValid()) {
                        String successMessage = Lang.get(_SET_PROPERTY_SUCCESS, settingName,
                                TextUtils.formatLocation(location, true));

                        tellSuccess(p, successMessage);

                        if (onSuccess != null)
                            onSuccess.run();

                    } else if (!result.tellMessage(_plugin, p)) {

                        tellError(p, Lang.get(_SET_PROPERTY_FAILED, settingName));
                    }
                }

            });
            return; // finished
        }
        else {

            value = valueType.isAssignableFrom(ItemStack.class)  // Special case: ItemStack
                    ? args.getItemStack(sender, valueArgName)
                    : args.getString(valueArgName);
        }

        ValidationResults result = settings.set(settingName, value);

        // make sure the result is valid
        if (!result.isValid()) {

            if (!result.tellMessage(_plugin, sender)) {
                tellError(sender, Lang.get(_INVALID_PROPERTY_VALUE, value, settingName));
            }

            return; // finished
        }

        tellSuccess(sender, Lang.get(_SET_PROPERTY_SUCCESS, settingName, args.getString(valueArgName)));

        if (onSuccess != null)
            onSuccess.run();
    }

}
