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
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.internal.managed.commands.CommandCollection.ICommandContainerFactory;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.ICommand;
import com.jcwhatever.nucleus.managed.commands.IRegisteredCommand;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.mixins.IInitializableCommand;
import com.jcwhatever.nucleus.managed.commands.mixins.ITabCompletable;
import com.jcwhatever.nucleus.managed.commands.parameters.ICommandParameter;
import com.jcwhatever.nucleus.managed.commands.parameters.IFlagParameter;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.messaging.ChatPaginator;
import com.jcwhatever.nucleus.managed.messaging.IMessenger;
import com.jcwhatever.nucleus.providers.permissions.IPermission;
import com.jcwhatever.nucleus.providers.permissions.Permissions;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.nucleus.utils.text.TextUtils.FormatTemplate;

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
class RegisteredCommand implements IRegisteredCommand {

    @Localizable static final String _COMMAND_PAGINATOR_TITLE = "Commands";

    @Localizable static final String _STATIC_PARAMETER_HEADER = "Static Parameters";
    @Localizable static final String _STATIC_PARAMETER_ITEM_LINE1 = "{GOLD}<{0: parameter name}>";
    @Localizable static final String _STATIC_PARAMETER_ITEM_LINE2 = "{GRAY}{0: description}";

    @Localizable static final String _FLOATING_PARAMETER_HEADER = "Floating Parameters";
    @Localizable static final String _FLOATING_PARAMETER_ITEM_LINE1 = "{GOLD}--{0: parameter name} <value>";
    @Localizable static final String _FLOATING_PARAMETER_ITEM_LINE2 = "{GRAY}{0: description}";

    @Localizable static final String _FLAG_PARAMETER_HEADER = "Flags";
    @Localizable static final String _FLAG_PARAMETER_ITEM_LINE1 = "{GOLD}-{0: flag name}";
    @Localizable static final String _FLAG_PARAMETER_ITEM_LINE2 = "{GRAY}{0: description}";

    private final Plugin _plugin;
    private final ICommand _command;
    private final CommandCollection _subCommands;
    private final UsageGenerator _usageGenerator = new UsageGenerator();

    private RegisteredCommandInfo _info;
    private CommandDispatcher _dispatcher;

    private RegisteredCommand _parent;
    private IPermission _permission;
    protected IMessenger _msg;

    private Set<Class<? extends ICommand>> _subCommandQueue = new HashSet<>(20);

    /**
     * Constructor.
     */
    public RegisteredCommand(Plugin plugin, ICommand command, ICommandContainerFactory commandFactory) {
        super();

        PreCon.notNull(plugin);
        PreCon.notNull(command);
        PreCon.notNull(commandFactory);

        _plugin = plugin;
        _command = command;
        _subCommands = new CommandCollection(plugin, commandFactory);

        if (_command instanceof IInitializableCommand) {
            ((IInitializableCommand) _command).init(this);
        }
    }

    /**
     * Get the encapsulated command handler.
     */
    @Override
    public ICommand getCommand() {
        return _command;
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public RegisteredCommandInfo getInfo() {
        return _info;
    }

    @Override
    public CommandDispatcher getDispatcher() {
        return _dispatcher;
    }

    @Override
    @Nullable
    public RegisteredCommand getParent() {
        return _parent;
    }

    /**
     * Execute the command.
     */
    public void execute(CommandSender sender, Arguments args)
            throws CommandException {

        if (_command instanceof IExecutableCommand)
            ((IExecutableCommand)_command).execute(sender, args);
    }

    /**
     * Invoked to get a list of possible tab complete values from the command based
     * on the current text.
     *
     * <p>Intended to be overridden by implementation if needed.</p>
     *
     * @param sender      The command sender.
     * @param arguments   This command arguments currently entered by the command sender.
     *                    not including the command and command path.
     * @param completions The list of completions.
     */
    public void onTabComplete(
            CommandSender sender, String[] arguments, Collection<String> completions) {

        if (_command instanceof ITabCompletable)
            ((ITabCompletable)_command).onTabComplete(sender, arguments, completions);
    }

    @Override
    @Nullable
    public RegisteredCommand getCommand(String subCommandName) {
        PreCon.notNullOrEmpty(subCommandName);

        return _subCommands.getCommand(subCommandName);
    }

    @Override
    public Collection<IRegisteredCommand> getCommands() {
        return _subCommands.getCommands();
    }

    @Override
    public <T extends Collection<IRegisteredCommand>> T getCommands(T output) {
        return _subCommands.getCommands(output);
    }

    @Override
    public Collection<String> getCommandNames() {
        return _subCommands.getCommandNames();
    }

    @Override
    public <T extends Collection<String>> T getCommandNames(T output) {
        return _subCommands.getCommandNames(output);
    }

    @Override
    public boolean registerCommand(Class<? extends ICommand> subCommandClass) {
        PreCon.notNull(subCommandClass);

        if (subCommandClass.equals(_command.getClass())) {
            throw new IllegalStateException("Cannot register a command as a sub command of itself.");
        }

        // add sub command to registration queue if not ready to register it yet
        if (getInfo() == null) {
            _subCommandQueue.add(subCommandClass);
            return true;
        }

        String commandName = _subCommands.addCommand(subCommandClass);
        if (commandName == null) {
            NucMsg.debug(getPlugin(),
                    "Failed to register command '{0}' as a sub command of '{1}' possibly because " +
                            "another command with the same name is already registered and no alternative " +
                            "command names were provided.", subCommandClass.getName(), getClass().getName());
            return false;
        }

        RegisteredCommand command = _subCommands.getCommand(commandName);
        if (command == null)
            throw new AssertionError();

        // set the instance's parent command
        command._parent = this;

        // set the instance's command handler
        if (_dispatcher != null)
            command.setDispatcher(_dispatcher, getInfo().getRoot() != null ? getInfo().getRoot() : this);

        // Sanity check. Make sure the parent specified by the command is this command
        if (!command.getInfo().getParentName().isEmpty() &&
                !isCommandMatch(command.getInfo().getParentName(), getInfo().getCommandNames())) {

            NucMsg.debug(getPlugin(),
                    "Failed to register sub command '{0}'. Registered with incorrect parent: {1}",
                    command.getClass().getName(), this.getClass().getName());

            _subCommands.removeAll(command);
            return false;
        }

        return true;
    }

    @Override
    public boolean unregisterCommand(Class<? extends ICommand> commandClass) {

        CommandInfo commandInfo = commandClass.getAnnotation(CommandInfo.class);
        if (commandInfo == null) {
            throw new RuntimeException(
                    "Could not find required CommandInfo annotation for command class: " +
                            commandClass.getName());
        }

        for (String commandName : commandInfo.command())
            _subCommands.remove(commandName.trim().toLowerCase());

        return true;
    }

    @Override
    public IPermission getPermission() {

        // lazy loaded
        if (_permission == null) {

            List<String> permissions = new ArrayList<String>(20);

            RegisteredCommand parent = this;

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

        List<ICommandParameter> staticParams = getInfo().getStaticParams();
        List<ICommandParameter> floatingParams = getInfo().getFloatingParams();
        List<IFlagParameter> flagParams = getInfo().getFlagParams();
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
                getInfo().getRootAliasName(), template), description);

        if (!paramDescriptions.isEmpty()) {

            // add static parameter descriptions
            if (!staticParams.isEmpty()) {
                pagin.addFormatted(FormatTemplate.SUB_HEADER, NucLang.get(getPlugin(), _STATIC_PARAMETER_HEADER));

                for (ICommandParameter parameter : staticParams) {

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

                for (ICommandParameter parameter : floatingParams) {

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

                for (IFlagParameter parameter : flagParams) {

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

        if (_command instanceof IExecutableCommand && isHelpVisible(sender)) {

            // add command to paginator
            pagin.add(_usageGenerator.generate(this), getInfo().getDescription());
        }

        List<RegisteredCommand> subCommands = new ArrayList<>(20);

        for (IRegisteredCommand regcmd : getCommands()) {

            RegisteredCommand cmd = (RegisteredCommand)regcmd;

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

            RegisteredCommandInfo info = cmd.getInfo();

            // add command to paginator
            pagin.add(_usageGenerator.generate(cmd), info.getDescription());
        }

        // Add commands that were set aside because they have sub commands
        // and render differently
        for (RegisteredCommand cmd : subCommands) {

            if (!cmd.isHelpVisible(sender)) {
                continue;
            }

            RegisteredCommandInfo info = cmd.getInfo();

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
    public int compareTo(IRegisteredCommand o) {
        return getInfo().getName().compareTo(o.getInfo().getName());
    }

    /**
     * Get the command collection.
     */
    protected CommandCollection getCommandCollection() {
        return _subCommands;
    }

    /*
     * Determine if the supplied command name matches one of the
     * command names of the this command.
     */
    private boolean isCommandMatch(@Nullable String parentName, String[] possibleNames) {
        PreCon.notNull(possibleNames);

        if (parentName == null)
            return false;

        for (String possibleName : possibleNames) {
            if (parentName.equalsIgnoreCase(possibleName))
                return true;
        }
        return false;
    }

    /*
     * Set the commands dispatcher. Initializes command.
     */
    final void setDispatcher(CommandDispatcher dispatcher, @Nullable RegisteredCommand rootCommand) {
        PreCon.notNull(dispatcher);

        if (_dispatcher != null)
            return;

        _dispatcher = dispatcher;
        _msg = Nucleus.getMessengerFactory().create(dispatcher.getPlugin());

        _info = new RegisteredCommandInfo(this, rootCommand);

        // register queued sub commands
        for (Class<? extends ICommand> commandClass : _subCommandQueue) {
            registerCommand(commandClass);
        }

        // remove queue
        _subCommandQueue = null;

        // set command handler in sub commands
        for (IRegisteredCommand subCommand : getCommands()) {
            if (subCommand.getDispatcher() == null) {
                ((RegisteredCommand)subCommand)
                        .setDispatcher(_dispatcher, rootCommand != null ? rootCommand : this);
            }
        }

        // register permission
        getPermission();
    }

    /**
     * Determine if a command sender can see the command in help.
     */
    protected boolean isHelpVisible(CommandSender sender) {

        // determine if the CommandSender has permission to use the command
        if (sender instanceof Player && !Permissions.has((Player)sender, getPermission().getName()))
            return false;

        // determine if the commands is visible in help
        return getInfo().isHelpVisible();
    }
}