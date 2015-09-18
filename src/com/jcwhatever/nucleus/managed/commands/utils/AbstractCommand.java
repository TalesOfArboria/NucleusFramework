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
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.nucleus.managed.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.nucleus.managed.commands.mixins.IInitializableCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.messaging.ChatPaginator;
import com.jcwhatever.nucleus.managed.messaging.IMessenger;
import com.jcwhatever.nucleus.mixins.INamed;
import com.jcwhatever.nucleus.mixins.IPlayerReference;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.providers.regionselect.IRegionSelection;
import com.jcwhatever.nucleus.providers.regionselect.RegionSelection;
import com.jcwhatever.nucleus.regions.SimpleRegionSelection;
import com.jcwhatever.nucleus.storage.settings.ISettingsManager;
import com.jcwhatever.nucleus.storage.settings.PropertyDefinition;
import com.jcwhatever.nucleus.storage.settings.PropertyValueType;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;
import com.jcwhatever.nucleus.utils.text.TextColor;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.UUID;

/**
 * An abstract implementation of a command that adds common protected
 * utility methods for use in the command implementation.
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

    private static final CollectionUtils.ISearchTextGetter<Object> GENERIC_TEXT_GETTER =
            new CollectionUtils.ISearchTextGetter<Object>() {
                @Override
                public String getText(Object obj) {

                    if (obj == null)
                        return null;

                    if (obj instanceof INamed)
                        return ((INamed) obj).getName();

                    return obj.toString();
                }
            };

    private static final CollectionUtils.ISearchTextGetter<Player> PLAYER_OBJECT_NAME_GETTER =
            new CollectionUtils.ISearchTextGetter<Player>() {
                @Override
                public String getText(Player player) {
                    return player.getName();
                }
            };

    private static final CollectionUtils.ISearchTextGetter<Object> PLAYER_NAME_GETTER =
            new CollectionUtils.ISearchTextGetter<Object>() {
                @Override
                public String getText(Object obj) {

                    if (obj instanceof Player)
                        return ((Player) obj).getName();

                    if (obj instanceof UUID)
                        return PlayerUtils.getPlayerName((UUID) obj);

                    if (obj instanceof String)
                        return (String)obj;

                    if (obj instanceof IPlayerReference)
                        return ((IPlayerReference) obj).getPlayer().getName();

                    return GENERIC_TEXT_GETTER.getText(obj);
                }
            };

    private static final CollectionUtils.ISearchTextGetter<Object> WORLD_NAME_GETTER =
            new CollectionUtils.ISearchTextGetter<Object>() {
                @Override
                public String getText(Object obj) {

                    if (obj instanceof World)
                        return ((World) obj).getName();

                    if (obj instanceof UUID) {
                        World world = Bukkit.getWorld((UUID) obj);
                        return world == null ? null : world.getName();
                    }

                    if (obj instanceof String)
                        return (String)obj;

                    return GENERIC_TEXT_GETTER.getText(obj);
                }
            };

    private Deque<Class<? extends ICommand>> _registerQueue;
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
     * Tell the {@link org.bukkit.command.CommandSender} the command
     * executed the request successfully.
     */
    protected void tellSuccess(CommandSender sender, String msg, Object... params) {
        _msg.tell(sender, TextColor.GREEN + msg, params);
    }

    /**
     * Tell the {@link org.bukkit.command.CommandSender} the command failed to
     * perform the requested task.
     */
    protected void tellError(CommandSender sender, String msg, Object... params) {
        _msg.tell(sender, TextColor.RED + msg, params);
    }

    /**
     * Get the command to type to get help for the current command.
     */
    protected String getInlineHelpCommand() {
        return getInlineHelpCommand(getRegistered());
    }

    /**
     * Get the command to type to get help for the specified sub command.
     *
     * @param subCommandName  The name of the sub-command.
     */
    protected String getInlineHelpCommand(String subCommandName) {
        PreCon.notNullOrEmpty(subCommandName);

        IRegisteredCommand command = getCommand(subCommandName);
        if (command == null) {
            throw new IllegalArgumentException("Command not found: "
                    + subCommandName);
        }

        return getInlineHelpCommand(command);
    }

    /**
     * Get the command to type to get help for the specified command.
     *
     * @param command  The command.
     */
    protected String getInlineHelpCommand(IRegisteredCommand command) {
        PreCon.notNull(command);

        ICommandUsageGenerator generator =
                Nucleus.getCommandManager()
                        .getUsageGenerator(ICommandUsageGenerator.INLINE_HELP);

        return generator.generate(command);
    }

    /**
     * Get the command to type for the current command.
     */
    protected String getInlineCommand() {
        return getInlineCommand(getRegistered());
    }

    /**
     * Get the command to type for the specified sub command.
     *
     * @param subCommandName  The name of the sub-command.
     */
    protected String getInlineCommand(String subCommandName) {
        PreCon.notNullOrEmpty(subCommandName);

        IRegisteredCommand command = getCommand(subCommandName);
        if (command == null) {
            throw new IllegalArgumentException("Command not found: "
                    + subCommandName);
        }

        return getInlineCommand(command);
    }

    /**
     * Get the command to type for the specified command.
     *
     * @param command  The command.
     */
    protected String getInlineCommand(IRegisteredCommand command) {
        PreCon.notNull(command);

        ICommandUsageGenerator generator =
                Nucleus.getCommandManager()
                        .getUsageGenerator(ICommandUsageGenerator.INLINE_COMMAND);

        return generator.generate(command);
    }

    /**
     * Set the specified players region selection.
     *
     * <p>Handles error message if any.</p>
     *
     * @param player  The player
     * @param p1      The first location of the selection.
     * @param p2      The second location of the selection.
     *
     * @throws CommandException if failed to set region selection.
     */
    protected void setRegionSelection(Player player, Location p1, Location p2) throws CommandException {
        PreCon.notNull(player);
        PreCon.notNull(p1);
        PreCon.notNull(p2);

        if (!player.getWorld().equals(p1.getWorld()))
            throw new CommandException(NucLang.get(_SAME_WORLD_REGION_SELECT));

        if (!p1.getWorld().equals(p2.getWorld()))
            throw new CommandException(NucLang.get(_INVALID_REGION));

        boolean isSuccess = Nucleus.getProviders()
                .getRegionSelection().setSelection(player, new SimpleRegionSelection(p1, p2));

        if (!isSuccess)
            throw new CommandException(NucLang.get(_SET_SELECTION_FAILED));
    }

    /**
     * Get the specified players current region selection.
     *
     * <p>Handles error message if any.</p>
     *
     * @param player  The player
     *
     * @return  {@link IRegionSelection} object that defines the selection.
     *
     * @throws CommandException if the player does not have a region selected.
     */
    protected IRegionSelection getRegionSelection(Player player) throws CommandException {
        PreCon.notNull(player);

        IRegionSelection selection = RegionSelection.get(player);

        // Check for region selection
        if (selection == null)
            throw new CommandException(NucLang.get(_NO_REGION_SELECTED));

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
     *
     * <p>Handles error and success messages.</p>
     *
     * @param sender           The command sender
     * @param settings         The settings manager that contains and defines possible settings.
     * @param args             The command arguments provided by the command sender.
     * @param propertyArgName  The name of the command argument parameter that contains the
     *                         property name of the setting.
     *
     * @throws InvalidArgumentException if the setting cannot be cleared due to an incorrect argument
     * or other problem.
     */
    protected void clearSetting(CommandSender sender, final ISettingsManager settings,
                                ICommandArguments args, String propertyArgName)
            throws CommandException {

        final String settingName = args.getString(propertyArgName);

        PropertyDefinition defs = settings.getDefinitions().get(settingName);
        if (defs == null)
            throw new CommandException(NucLang.get(_UNRECOGNIZED_PROPERTY, settingName));

        if (!settings.set(settingName, null))
            throw new CommandException(NucLang.get(_CLEAR_PROPERTY_FAILED, settingName));

        tellSuccess(sender, NucLang.get(_CLEAR_PROPERTY_SUCCESS, settingName));
    }

    /**
     * Set a setting into a settings manager using user command input.
     *
     * <p>Handles error and success messages to the user.</p>
     *
     * @param sender           The command sender
     * @param settings         The settings manager that contains and defines possible settings.
     * @param args             The command arguments provided by the command sender.
     * @param propertyArgName  The name of the command argument parameter that contains the property
     *                         name of the setting.
     * @param valueArgName     The name of the command argument parameter that contains the value
     *                         of the property.
     *
     * @throws InvalidArgumentException       If the value provided by the command sender is not valid.
     * @throws InvalidCommandSenderException  If the command sender cannot set the value due to sender type.
     * @throws CommandException               for all other problems.
     */
    protected void setSetting(CommandSender sender, final ISettingsManager settings,
                              ICommandArguments args, String propertyArgName, String valueArgName)
            throws CommandException {

        setSetting(sender, settings, args, propertyArgName, valueArgName, null);
    }

    /**
     * Set a setting into a settings manager using user command input.
     *
     * <p>Handles error and success messages to the user.</p>
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
     * @throws InvalidArgumentException       if the value provided by the command sender is not valid.
     * @throws InvalidCommandSenderException  if the command sender cannot set the value due to sender type.
     * @throws CommandException               for all other problems.
     */
    protected void setSetting(CommandSender sender, final ISettingsManager settings,
                              ICommandArguments args, String propertyArgName,
                              String valueArgName, @Nullable final Runnable onSuccess)
            throws CommandException {

        PreCon.notNull(sender);
        PreCon.notNull(settings);
        PreCon.notNull(args);
        PreCon.notNullOrEmpty(propertyArgName);
        PreCon.notNullOrEmpty(valueArgName);

        final String settingName = args.getString(propertyArgName);
        Object value;

        // get settings definitions
        final PropertyDefinition propertyDefinition = settings.getDefinitions().get(settingName);
        if (propertyDefinition == null)
            throw new CommandException(NucLang.get(_UNRECOGNIZED_PROPERTY, settingName));

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
            throw new CommandException(
                    NucLang.get(_INVALID_PROPERTY_VALUE, value, settingName) + '\n' +
                            NucLang.get(_PROPERTY_DESCRIPTION,
                                    propertyDefinition.getName(), propertyDefinition.getDescription())
            );
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
                _registerQueue = new ArrayDeque<>(10);

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

    /**
     * Fill a "matches" collection with names of online players that are possible matches
     * for the last argument.
     *
     * <p>Use with tab completion for player name arguments.</p>
     *
     * @param arguments  The current arguments.
     * @param matches    The collection of tab completion matches.
     */
    protected void tabCompletePlayerName(String[] arguments, Collection<String> matches) {
        PreCon.notNull(arguments);
        PreCon.notNull(matches);

        if (arguments.length == 0)
            return;

        String tabArg = arguments[arguments.length - 1];

        searchOnlinePlayers(tabArg, matches);
    }

    /**
     * Fill a "matches" collection with names of players that are possible matches
     * for the last argument.
     *
     * <p>Use with tab completion for player name arguments.</p>
     *
     * @param arguments   The current arguments.
     * @param candidates  A collection of candidates to search.
     * @param matches     The collection of tab completion matches.
     */
    protected void tabCompletePlayerName(String[] arguments,
                                         Collection candidates, Collection<String> matches) {
        PreCon.notNull(arguments);
        PreCon.notNull(candidates);
        PreCon.notNull(matches);

        if (arguments.length == 0)
            return;

        String tabArg = arguments[arguments.length - 1];

        searchPlayers(tabArg, candidates, matches);
    }

    /**
     * Fill a "matches" collection with names of worlds that are possible matches
     * for the last argument.
     *
     * <p>Use with tab completion for world name arguments.</p>
     *
     * @param arguments   The current arguments.
     * @param matches     The collection of tab completion matches.
     */
    protected void tabCompleteWorldName(String[] arguments, Collection<String> matches) {
        PreCon.notNull(arguments);
        PreCon.notNull(matches);

        if (arguments.length == 0)
            return;

        String tabArg = arguments[arguments.length - 1];

        searchWorlds(tabArg, Bukkit.getWorlds(), matches);
    }

    /**
     * Fill a "matches" collection with names of worlds that are possible matches
     * for the last argument.
     *
     * <p>Use with tab completion for world name arguments.</p>
     *
     * @param arguments   The current arguments.
     * @param candidates  A collection of candidates to search.
     * @param matches     The collection of tab completion matches.
     */
    protected void tabCompleteWorldName(String[] arguments,
                                        Collection candidates, Collection<String> matches) {
        PreCon.notNull(arguments);
        PreCon.notNull(candidates);
        PreCon.notNull(matches);

        if (arguments.length == 0)
            return;

        String tabArg = arguments[arguments.length - 1];

        searchWorlds(tabArg, candidates, matches);
    }

    /**
     * Fill a "matches" collection with names of worlds that are possible matches
     * for the last argument.
     *
     * <p>Use with tab completion for enum arguments.</p>
     *
     * @param arguments   The current arguments.
     * @param enumType    The enum type.
     * @param matches     The collection of tab completion matches.
     */
    protected void tabCompleteEnum(String[] arguments, Class<? extends Enum> enumType,
                                   Collection<String> matches) {
        PreCon.notNull(arguments);
        PreCon.notNull(enumType);
        PreCon.notNull(matches);

        if (arguments.length == 0)
            return;

        String tabArg = arguments[arguments.length - 1];

        searchEnum(tabArg, enumType, matches);
    }

    /**
     * Fill a "matches" collection with names of worlds that are possible matches
     * for the last argument.
     *
     * <p>Use with tab completion for generic arguments.</p>
     *
     * @param arguments   The current arguments.
     * @param candidates  A collection of candidates to search.
     * @param matches     The collection of tab completion matches.
     */
    protected void tabCompleteSearch(String[] arguments,
                                     Collection candidates, Collection<String> matches) {
        tabCompleteSearch(arguments, candidates, matches, GENERIC_TEXT_GETTER);
    }

    /**
     * Fill a "matches" collection with names of worlds that are possible matches
     * for the last argument.
     *
     * <p>Use with tab completion for generic arguments.</p>
     *
     * @param arguments   The current arguments.
     * @param candidates  A collection of candidates to search.
     * @param matches     The collection of tab completion matches.
     * @param textGetter  A text getter to convert candidate objects into searchable text.
     */
    protected void tabCompleteSearch(String[] arguments,
                                     Collection candidates, Collection<String> matches,
                                     CollectionUtils.ISearchTextGetter textGetter) {
        PreCon.notNull(arguments);
        PreCon.notNull(candidates);
        PreCon.notNull(matches);
        PreCon.notNull(textGetter);

        if (arguments.length == 0)
            return;

        String tabArg = arguments[arguments.length - 1];

        search(tabArg, candidates, matches, textGetter);
    }

    /**
     * Fill an output collection with names of online players that are possible matches
     * for the specified search term.
     *
     * @param searchTerm  The search term.
     * @param output      The output collection of name matches.
     *
     * @return  The output collection.
     */
    protected Collection<String> searchOnlinePlayers(String searchTerm, Collection<String> output) {
        PreCon.notNull(searchTerm);
        PreCon.notNull(output);

        Collection<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        List<Player> matchingPlayers = CollectionUtils.textSearch(players, searchTerm, PLAYER_OBJECT_NAME_GETTER);

        if (output instanceof ArrayList)
            ((ArrayList) output).ensureCapacity(matchingPlayers.size());

        for (Player player : matchingPlayers) {
            output.add(player.getName());
        }

        return output;
    }

    /**
     * Fill an output collection with names of players that are possible matches
     * for the specified search term.
     *
     * @param searchTerm  The search term.
     * @param candidates  The candidates to search.
     * @param output      The output collection of name matches.
     *
     * @return  The output collection.
     */
    protected Collection<String> searchPlayers(String searchTerm,
                                               Collection candidates,
                                               Collection<String> output) {
        return search(searchTerm, candidates, output, PLAYER_NAME_GETTER);
    }

    /**
     * Fill an output collection with names of worlds that are possible matches
     * for the specified search term.
     *
     * @param searchTerm  The search term.
     * @param candidates  The candidates to search.
     * @param output      The output collection of name matches.
     *
     * @return  The output collection.
     */
    protected Collection<String> searchWorlds(String searchTerm,
                                              Collection candidates,
                                              Collection<String> output) {
        return search(searchTerm, candidates, output, WORLD_NAME_GETTER);
    }

    /**
     * Fill an output collection with names of enum constants that are possible matches
     * for the specified search term.
     *
     * @param searchTerm  The search term.
     * @param enumType    The enum type.
     * @param output      The output collection of name matches.
     *
     * @return  The output collection.
     */
    protected Collection<String> searchEnum(String searchTerm,
                                            Class<? extends Enum> enumType,
                                            Collection<String> output) {
        Collection<String> candidates = new ArrayList<>(enumType.getEnumConstants().length);

        for (Enum e : enumType.getEnumConstants()) {
            candidates.add(e.name().toLowerCase());
        }

        return search(searchTerm, candidates, output, GENERIC_TEXT_GETTER);
    }

    /**
     * Generic candidate search.
     *
     * <p>Use candidates toString method to retrieve searchable text.</p>
     *
     * @param searchTerm  The search term.
     * @param candidates  The search candidates.
     * @param output      The output result collection.
     *
     * @return  The output result collection.
     */
    protected Collection<String> search(String searchTerm,
                                        Collection candidates,
                                        Collection<String> output) {
        return search(searchTerm, candidates, output, GENERIC_TEXT_GETTER);
    }

    /**
     * Generic candidate search.
     *
     * @param searchTerm  The search term.
     * @param candidates  The search candidates.
     * @param output      The output result collection.
     * @param textGetter  The text getter for converting objects to strings.
     *
     * @return  The output result collection.
     */
    protected Collection<String> search(String searchTerm,
                                        Collection candidates,
                                        Collection<String> output,
                                        CollectionUtils.ISearchTextGetter textGetter) {
        PreCon.notNull(searchTerm);
        PreCon.notNull(candidates);
        PreCon.notNull(output);

        @SuppressWarnings("unchecked")
        List<Object> matching = CollectionUtils.textSearch(candidates, searchTerm, textGetter);

        if (output instanceof ArrayList)
            ((ArrayList) output).ensureCapacity(matching.size());

        for (Object match : matching) {

            @SuppressWarnings("unchecked")
            String text = textGetter.getText(match);

            output.add(text);
        }

        return output;
    }
}