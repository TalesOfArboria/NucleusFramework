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

import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.nucleus.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.nucleus.commands.parameters.CommandParameter;
import com.jcwhatever.nucleus.commands.parameters.FlagParameter;
import com.jcwhatever.nucleus.commands.parameters.ParameterDescription;
import com.jcwhatever.nucleus.commands.parameters.ParameterDescriptions;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.utils.language.Localizable;
import com.jcwhatever.nucleus.messaging.ChatPaginator;
import com.jcwhatever.nucleus.messaging.IMessenger;
import com.jcwhatever.nucleus.messaging.MessengerFactory;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.providers.permissions.IPermission;
import com.jcwhatever.nucleus.utils.Permissions;
import com.jcwhatever.nucleus.regions.selection.IRegionSelection;
import com.jcwhatever.nucleus.storage.settings.ISettingsManager;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.nucleus.utils.text.TextUtils.FormatTemplate;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Base implementation of a command
 *
 * <p>The command implementation must have an {@link CommandInfo} annotation.</p>
 */
public abstract class AbstractCommand
        implements Comparable<AbstractCommand>, ICommandOwner, IPluginOwned {

    @Localizable
    static final String _COMMAND_PAGINATOR_TITLE = "Commands";

    @Localizable static final String _STATIC_PARAMETER_HEADER = "Static Parameters";
    @Localizable static final String _STATIC_PARAMETER_ITEM_LINE1 = "{GOLD}<{0: parameter name}>";
    @Localizable static final String _STATIC_PARAMETER_ITEM_LINE2 = "{GRAY}{0: description}";

    @Localizable static final String _FLOATING_PARAMETER_HEADER = "Floating Parameters";
    @Localizable static final String _FLOATING_PARAMETER_ITEM_LINE1 = "{GOLD}--{0: parameter name} <value>";
    @Localizable static final String _FLOATING_PARAMETER_ITEM_LINE2 = "{GRAY}{0: description}";

    @Localizable static final String _FLAG_PARAMETER_HEADER = "Flags";
    @Localizable static final String _FLAG_PARAMETER_ITEM_LINE1 = "{GOLD}-{0: flag name}";
    @Localizable static final String _FLAG_PARAMETER_ITEM_LINE2 = "{GRAY}{0: description}";

    private final CommandCollection _subCommands = new CommandCollection();
    private final UsageGenerator _usageGenerator = new UsageGenerator();

    private CommandInfoContainer _info;
    private CommandDispatcher _dispatcher;

    private AbstractCommand _parent;
    private IPermission _permission;
    private boolean _canExecute;
    protected IMessenger _msg;

    private Set<Class<? extends AbstractCommand>> _subCommandQueue =
            new HashSet<Class<? extends AbstractCommand>>(20);

    /**
     * Constructor.
     *
     * <p>Only a parameter-less constructor should be used.</p>
     */
    public AbstractCommand () {
        super();
    }

    @Override
    public Plugin getPlugin() {
        if (_dispatcher == null)
            throw new RuntimeException("Command is not initialized.");

        return _dispatcher.getPlugin();
    }

    /**
     * Determine if the command can be executed.
     */
    public boolean canExecute() {
        return _canExecute;
    }

    /**
     * Get the commands {@link CommandInfoContainer}.
     */
    public CommandInfoContainer getInfo() {
        return _info;
    }

    /**
     * Get the command handler
     */
    public CommandDispatcher getDispatcher() {
        return _dispatcher;
    }

    /**
     * Get the commands parent command, if any.
     */
    @Nullable
    public AbstractCommand getParent() {
        return _parent;
    }

    /**
     * Execute the command.
     *
     * <p>Intended to be overridden by implementation if needed.</p>
     */
    public void execute(CommandSender sender, CommandArguments args)
            throws CommandException {
    }

    /**
     * Called to get a list of possible tab complete values from the command based
     * on the current text.
     *
     * <p>Intended to be overridden by implementation if needed.</p>
     *
     * @param sender      The command sender.
     * @param arguments   This command arguments currently entered by the command sender.
     *                    not including the command and command path.
     * @param completions The list of completions.
     */
    public void onTabComplete(@SuppressWarnings("unused") CommandSender sender,
                              @SuppressWarnings("unused") String[] arguments,
                              @SuppressWarnings("unused") Collection<String> completions) {
        // do nothing
    }

    /**
     * Get a direct sub command by name
     */
    @Override
    @Nullable
    public AbstractCommand getCommand(String subCommandName) {
        PreCon.notNullOrEmpty(subCommandName);

        return _subCommands.getCommand(subCommandName);
    }

    /**
     * Get the commands registered sub commands.
     */
    @Override
    public Collection<AbstractCommand> getCommands() {
        return _subCommands.getCommands();
    }

    /**
     * Get the sub command names.
     */
    @Override
    public Collection<String> getCommandNames() {
        return _subCommands.getCommandNames();
    }

    /**
     * Register a sub command.
     *
     * <p>Prefer registering sub commands in the implementations command constructor
     * to take advantage of the command handlers permission batch operation in order to improve
     * registration performance.</p>
     *
     * @param subCommandClass
     */
    @Override
    public boolean registerCommand(Class<? extends AbstractCommand> subCommandClass) {
        PreCon.notNull(subCommandClass);

        if (subCommandClass.equals(getClass())) {
            throw new IllegalStateException("Cannot register a command as a sub command of itself.");
        }

        // add sub command to registration queue if not ready to register it yet
        if (getInfo() == null) {
            _subCommandQueue.add(subCommandClass);
            return true;
        }

        String commandName = _subCommands.addCommand(subCommandClass);
        if (commandName == null) {
            _dispatcher.getUtils().debug("Failed to register command '{0}' as a sub command of '{1}' possibly because " +
                    "another command with the same name is already registered and no alternative " +
                    "command names were provided.", subCommandClass.getName(), getClass().getName());
            return false;
        }

        AbstractCommand command = _subCommands.getCommand(commandName);
        if (command == null)
            throw new AssertionError();

        // set the instance's parent command
        command._parent = this;

        // set the instance's command handler
        if (_dispatcher != null)
            command.setDispatcher(_dispatcher, getInfo().getRoot() != null ? getInfo().getRoot() : this);

        /**
         * Sanity check. Make sure the parent specified by the command is this command
         */
        if (!command.getInfo().getParentName().isEmpty() &&
                !isCommandMatch(command.getInfo().getParentName(), getInfo().getCommandNames())) {
            _dispatcher.getUtils().debug("Failed to register sub command '{0}'. Registered with incorrect parent: {1}",
                    command.getClass().getName(), this.getClass().getName());

            _subCommands.removeAll(command);
            return false;
        }

        onSubCommandInstantiated(command);

        return true;
    }

    /**
     * Unregister a sub command.
     *
     * @param commandClass  The commands implementation class.
     */
    @Override
    public boolean unregisterCommand(Class<? extends AbstractCommand> commandClass) {

        CommandInfo commandInfo = commandClass.getAnnotation(CommandInfo.class);
        if (commandInfo == null) {
            throw new RuntimeException(
                    "Could not find required CommandInfo annotation for command class: " + commandClass.getName());
        }

        for (String commandName : commandInfo.command())
            _subCommands.remove(commandName.trim().toLowerCase());

        return true;
    }

    /**
     * Get the commands permission object.
     */
    public IPermission getPermission() {

        // lazy loaded
        if (_permission == null) {

            List<String> permissions = new ArrayList<String>(20);

            AbstractCommand parent = this;

            permissions.add(getInfo().getName());

            while((parent = parent.getParent()) != null) {
                permissions.add(parent.getInfo().getName());
            }

            Collections.reverse(permissions);

            String permissionName = getPlugin().getName().toLowerCase() + ".commands." +
                    TextUtils.concat(permissions, ".");

            _permission = Permissions.register(permissionName, getInfo().getPermissionDefault());
            _permission.setDescription(getInfo().getDescription());
        }

        return _permission;
    }


    /**
     * Show help for a commands parameters.
     *
     * @param sender  The command sender to show the help to.
     * @param page    The help page.
     */
    public void showDetailedHelp(CommandSender sender, int page) {

        List<CommandParameter> staticParams = getInfo().getStaticParams();
        List<CommandParameter> floatingParams = getInfo().getFloatingParams();
        List<FlagParameter> flagParams = getInfo().getFlagParams();
        ParameterDescriptions paramDescriptions = getInfo().getParamDescriptions();

        ChatPaginator pagin = new ChatPaginator(getPlugin(), 6,
                NucLang.get(getPlugin(), _COMMAND_PAGINATOR_TITLE));

        String description = getInfo().getLongDescription().isEmpty()
                ? getInfo().getDescription()
                : getInfo().getLongDescription();

        String template = paramDescriptions.isEmpty()
                ? UsageGenerator.HELP_USAGE
                : UsageGenerator.PARAMETER_HELP;

        // add usage and description
        pagin.add(_usageGenerator.generate(this,
                getInfo().getRootSessionName(), template), description);

        if (!paramDescriptions.isEmpty()) {

            // add static parameter descriptions
            if (!staticParams.isEmpty()) {
                pagin.addFormatted(FormatTemplate.SUB_HEADER, NucLang.get(getPlugin(), _STATIC_PARAMETER_HEADER));

                for (CommandParameter parameter : staticParams) {

                    ParameterDescription paramDesc = paramDescriptions.get(parameter.getName());
                    if (paramDesc == null) {
                        _msg.debug("Missing static parameter description for '{0}' in command '{1}'",
                                parameter.getName(), getClass().getName());
                        continue;
                    }

                    pagin.addFormatted(NucLang.get(getPlugin(), _STATIC_PARAMETER_ITEM_LINE1,
                            paramDesc.getName(), paramDesc.getDescription()));

                    pagin.addFormatted(NucLang.get(getPlugin(), _STATIC_PARAMETER_ITEM_LINE2,
                            paramDesc.getDescription()));
                }
            }

            // add floating parameter descriptions
            if (!floatingParams.isEmpty()) {
                pagin.addFormatted(FormatTemplate.SUB_HEADER, NucLang.get(getPlugin(), _FLOATING_PARAMETER_HEADER));

                for (CommandParameter parameter : floatingParams) {

                    ParameterDescription paramDesc = paramDescriptions.get(parameter.getName());
                    if (paramDesc == null) {
                        _msg.debug("Missing floating parameter description for '{0}' in command '{1}'",
                                parameter.getName(), getClass().getName());
                        continue;
                    }

                    pagin.addFormatted(NucLang.get(getPlugin(), _FLOATING_PARAMETER_ITEM_LINE1,
                            paramDesc.getName(), paramDesc.getDescription()));

                    pagin.addFormatted(NucLang.get(getPlugin(), _FLOATING_PARAMETER_ITEM_LINE2,
                            paramDesc.getDescription()));
                }
            }

            // add flag parameter descriptions
            if (!flagParams.isEmpty()) {
                pagin.addFormatted(FormatTemplate.SUB_HEADER, NucLang.get(getPlugin(), _FLAG_PARAMETER_HEADER));

                for (FlagParameter parameter : flagParams) {

                    ParameterDescription paramDesc = paramDescriptions.get(parameter.getName());
                    if (paramDesc == null) {
                        _msg.debug("Missing flag description for '{0}' in command '{1}'",
                                parameter.getName(), getClass().getName());
                        continue;
                    }

                    pagin.addFormatted(NucLang.get(getPlugin(), _FLAG_PARAMETER_ITEM_LINE1,
                            paramDesc.getName(), paramDesc.getDescription()));

                    pagin.addFormatted(NucLang.get(getPlugin(), _FLAG_PARAMETER_ITEM_LINE2,
                            paramDesc.getDescription()));
                }
            }
        }

        pagin.show(sender, page, FormatTemplate.CONSTANT_DEFINITION);
    }

    /**
     * Display the commands help info in a paginated list that includes
     * the sub command help to the specified {@link CommandSender}
     */
    public void showHelp(CommandSender sender, int page) {
        PreCon.notNull(sender);
        PreCon.positiveNumber(page);

        ChatPaginator pagin = new ChatPaginator(getPlugin(), 6,
                NucLang.get(getPlugin(), _COMMAND_PAGINATOR_TITLE));

        if (canExecute() && isHelpVisible(sender)) {

            // add command to paginator
            pagin.add(_usageGenerator.generate(this), getInfo().getDescription());
        }

        List<AbstractCommand> subCommands = new ArrayList<AbstractCommand>(20);

        for (AbstractCommand cmd : getCommands()) {

            // Determine if the command has its own own sub commands
            // and put aside so it can be displayed at the end of the
            // help list
            if (cmd.getCommands().size() > 0) {
                subCommands.add(cmd);
                continue;
            }

            if (!cmd.isHelpVisible(sender)) {
                continue;
            }

            CommandInfoContainer info = cmd.getInfo();

            // add command to paginator
            pagin.add(_usageGenerator.generate(cmd), info.getDescription());
        }

        // Add commands that were set aside because they have sub commands
        // and render differently
        for (AbstractCommand cmd : subCommands) {

            if (!cmd.isHelpVisible(sender)) {
                continue;
            }

            CommandInfoContainer info = cmd.getInfo();

            // add info to get sub commands help to paginator
            pagin.add(_usageGenerator.generate(cmd), info.getDescription());
        }

        // show paginator to CommandSender
        pagin.show(sender, page, FormatTemplate.CONSTANT_DEFINITION);
    }

    /**
     * Compare command names
     * Used for alphabetical sorting of commands.
     */
    @Override
    public int compareTo(AbstractCommand o) {
        return getInfo().getName().compareTo(o.getInfo().getName());
    }

    /**
     * Called after the dispatcher is set.
     *
     * <p>Provided as an optional implementation override.</p>
     *
     * @param dispatcher  The commands dispatcher.
     */
    protected void onInitialized(CommandDispatcher dispatcher) {
        PreCon.notNull(dispatcher);
    }

    /**
     * Called after a sub command is registered and instantiated.
     *
     * <p>Provided as an optional implementation override.</p>
     *
     * @param command  The sub command instantiated.
     */
    protected void onSubCommandInstantiated(AbstractCommand command) {
        PreCon.notNull(command);
    }

    /**
     * Get the command collection.
     */
    protected CommandCollection getCommandCollection() {
        return _subCommands;
    }

    /**
     * Determine if the supplied command name matches one of the
     * command names of the this command.
     *
     * @param parentName     The command name to match
     * @param possibleNames  A {@link String[]} of valid names
     */
    protected boolean isCommandMatch(@Nullable String parentName, String[] possibleNames) {
        PreCon.notNull(possibleNames);

        if (parentName == null)
            return false;

        for (String possibleName : possibleNames) {
            if (parentName.equalsIgnoreCase(possibleName))
                return true;
        }
        return false;
    }

    /**
     * Determine if one of the supplied command names match any one of the
     * command names of the this command.
     *
     * @param parentNames    A {@link String[]} of possible names
     * @param possibleNames  A {@link String[]} of valid names
     */
    protected boolean isCommandMatch(String[] parentNames, String[] possibleNames) {
        PreCon.notNull(parentNames);
        PreCon.notNull(possibleNames);

        for (String possibleName : possibleNames) {
            for (String parentName : parentNames) {
                if (parentName.equalsIgnoreCase(possibleName))
                    return true;
            }

        }
        return false;
    }

    /**
     * Set the commands dispatcher
     * Should only be called by the dispatcher or parent command
     */
    final void setDispatcher(CommandDispatcher dispatcher, @Nullable AbstractCommand rootCommand) {
        PreCon.notNull(dispatcher);

        if (_dispatcher != null)
            return;

        _dispatcher = dispatcher;
        _msg = MessengerFactory.create(dispatcher.getPlugin());

        _info = new CommandInfoContainer(this, rootCommand);

        // register queued sub commands
        for (Class<? extends AbstractCommand> commandClass : _subCommandQueue) {
            registerCommand(commandClass);
        }

        // remove queue
        _subCommandQueue = null;

        // set command handler in sub commands
        for (AbstractCommand subCommand : getCommands()) {
            if (subCommand.getDispatcher() == null) {
                subCommand.setDispatcher(_dispatcher, rootCommand != null ? rootCommand : this);
            }
        }

        // determine if the command is executable
        try {
            this.getClass().getDeclaredMethod("execute", CommandSender.class, CommandArguments.class);
            _canExecute = true;
        } catch (NoSuchMethodException e) {
            _canExecute = false;
        }

        // register permission
        getPermission();

        onInitialized(dispatcher);
    }

    /**
     * Determine if a command sender can see the command in help.
     */
    protected boolean isHelpVisible(CommandSender sender) {

        // determine if the CommandSender has permission to use the command
        if (!Permissions.has(sender, getPermission().getName()))
            return false;

        // determine if the commands is visible in help
        return getInfo().isHelpVisible();
    }

    /**
     * Tell the {@link CommandSender} a generic message.
     */
    protected void tell(CommandSender sender, String msg, Object... params) {
        _dispatcher.getUtils().tell(sender, msg, params);
    }

    /**
     * Tell the {@link CommandSender} that something is enabled or disabled.
     *
     * <p>Use format code {e} to specify where to place the word Enabled or Disabled.</p>
     */
    protected void tellEnabled(CommandSender sender, String msg, boolean isEnabled, Object...params) {
        _dispatcher.getUtils().tellEnabled(sender, msg, isEnabled, params);
    }

    /**
     * Tell the {@link CommandSender} the command executed the request successfully.
     */
    protected void tellSuccess(CommandSender sender, String msg, Object... params) {
        _dispatcher.getUtils().tellSuccess(sender, msg, params);
    }

    /**
     * Tell the {@link CommandSender} the command failed to perform the requested task.
     */
    protected void tellError(CommandSender sender, String msg, Object... params) {
        _dispatcher.getUtils().tellError(sender, msg, params);
    }

    /**
     * Set the specified players region selection.
     * Handles error message if any.
     *
     * @param p   The player
     * @param p1  The first location of the selection.
     * @param p2  The second location of the selection.
     */
    protected boolean setRegionSelection(Player p, Location p1, Location p2) {
        return _dispatcher.getUtils().setRegionSelection(p, p1, p2);
    }

    /**
     * Get the specified players current region selection.
     * Handles error message if any.
     *
     * @param p  The player
     *
     * @return  {@link IRegionSelection} object that defines the selection.
     */
    @Nullable
    protected IRegionSelection getRegionSelection(Player p) {
        return _dispatcher.getUtils().getRegionSelection(p);
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
     * @throws com.jcwhatever.nucleus.commands.exceptions.InvalidArgumentException
     */
    protected void clearSetting(CommandSender sender, final ISettingsManager settings,
                                CommandArguments args, String propertyArgName) throws InvalidArgumentException {
        _dispatcher.getUtils().clearSetting(sender, settings, args, propertyArgName);
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
    protected void setSetting(CommandSender sender, final ISettingsManager settings,
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
    protected void setSetting(CommandSender sender, final ISettingsManager settings,
                              CommandArguments args, String propertyArgName,
                              String valueArgName, @Nullable final Runnable onSuccess)
            throws InvalidArgumentException, InvalidCommandSenderException {
        _dispatcher.getUtils().setSetting(sender, settings, args, propertyArgName, valueArgName, onSuccess);
    }
}
