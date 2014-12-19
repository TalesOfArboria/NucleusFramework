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
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.commands.exceptions.MissingCommandAnnotationException;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator;
import com.jcwhatever.bukkit.generic.permissions.IPermission;
import com.jcwhatever.bukkit.generic.permissions.Permissions;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.text.TextUtils;
import com.jcwhatever.bukkit.generic.utils.text.TextUtils.FormatTemplate;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import javax.annotation.Nullable;

/**
 * Base implementation of a command
 *
 * <p>The command implementation must have an {@code ICommandInfo} annotation.</p>
 */
public abstract class AbstractCommand extends AbstractCommandUtils implements Comparable<AbstractCommand> {

    private static final String _USAGE = "{GOLD}/{plugin-command} {GREEN}";
    private static final String _COMMAND_PAGINATOR_TITLE = "Commands";

    private final Map<String, AbstractCommand> _subCommands = new HashMap<String, AbstractCommand>(20);

    private CommandInfoContainer _info;
    private AbstractCommandHandler _commandHandler;

    private Set<Class<? extends AbstractCommand>> _subCommandQueue = new HashSet<Class<? extends AbstractCommand>>(20);
    private AbstractCommand _parent;
    private IPermission _permission;
    private List<AbstractCommand> _sortedSubCommands;
    private boolean _canExecute;

    /**
     * Constructor.
     *
     * <p>Only a parameter-less constructor should be used.</p>
     */
    public AbstractCommand () {
        super();
    }

    /**
     * Determine if the command can be executed.
     */
    public boolean canExecute() {
        return _canExecute;
    }

    /**
     * Execute the command.
     *
     * <p>Intended to be overridden by implementation if needed.</p>
     */
    public void execute(CommandSender sender, CommandArguments args)
            throws InvalidValueException, InvalidCommandSenderException {
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
     *
     * @return A list of possible results. Empty list if no results.
     */
    public void onTabComplete(@SuppressWarnings("unused") CommandSender sender,
                              @SuppressWarnings("unused") String[] arguments,
                              @SuppressWarnings("unused") Collection<String> completions) {
        // do nothing
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
    public final void registerSubCommand(Class<? extends AbstractCommand> subCommandClass) {
        PreCon.notNull(subCommandClass);

        if (subCommandClass.equals(getClass())) {
            throw new IllegalStateException("Cannot register a command as a sub command of itself.");
        }

        // add sub command to registration queue if not ready to register it yet
        if (_info == null) {
            _subCommandQueue.add(subCommandClass);
            return;
        }

        // make sure sub command has required ICommandInfo annotation
        CommandInfo commandInfo = subCommandClass.getAnnotation(CommandInfo.class);
        if (commandInfo == null)
            throw new MissingCommandAnnotationException(subCommandClass);

        /**
         * Safety check. Make sure the parent specified by the command is this command
         */
        if (!commandInfo.parent().isEmpty() && !isCommandMatch(commandInfo.parent(), _info.getCommandNames())) {
            _msg.debug("Failed to register sub command. Registered with incorrect parent: "
                    + this.getClass().getName());
            return;
        }

        // instantiate command
        AbstractCommand instance;

        try {
            instance = subCommandClass.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        // set the instances parent command
        instance._parent = this;

        // set the instances command handler
        if (_commandHandler != null)
            instance.setCommandHandler(_commandHandler, _info.getMasterCommandName());

        onSubCommandInstantiated(instance);

        // split commands and add each command
        for (String cmd : commandInfo.command()) {
            _subCommands.put(cmd.trim().toLowerCase(), instance);
        }

        // clear sorted sub commands cache
        _sortedSubCommands = null;
    }

    /**
     * Unregister a sub command.
     *
     * @param commandClass  The commands implementation class.
     */
    public final boolean unregisterSubCommand(Class<? extends AbstractCommand> commandClass) {

        CommandInfo commandInfo = commandClass.getAnnotation(CommandInfo.class);
        if (commandInfo == null)
            throw new MissingCommandAnnotationException(commandClass);

        for (String commandName : commandInfo.command())
            _subCommands.remove(commandName.trim().toLowerCase());

        return true;
    }

    /**
     * Get the commands {@code ICommandInfo} annotation.
     */
    public final CommandInfoContainer getInfo() {
        return _info;
    }

    /**
     * Get the command handler
     */
    public final AbstractCommandHandler getCommandHandler() {
        return _commandHandler;
    }

    /**
     * Get the commands parent command, if any.
     */
    @Nullable
    public final AbstractCommand getParent() {
        return _parent;
    }

    /**
     * Get a direct sub command by name
     */
    @Nullable
    public final AbstractCommand getSubCommand(String subCommandName) {
        PreCon.notNullOrEmpty(subCommandName);

        return _subCommands.get(subCommandName.toLowerCase());
    }

    /**
     * Get the commands registered sub commands.
     */
    public final Collection<AbstractCommand> getSubCommands() {
        if (_sortedSubCommands == null) {
            List<AbstractCommand> subCommands = new ArrayList<AbstractCommand>(_subCommands.values());
            Collections.sort(subCommands);
            _sortedSubCommands = subCommands;
        }
        return new ArrayList<AbstractCommand>(_sortedSubCommands);
    }

    /**
     * Get the sub command names.
     */
    public final Collection<String> getSubCommandNames() {
        return new ArrayList<>(_subCommands.keySet());
    }

    /**
     * Get the commands permission object.
     */
    public final IPermission getPermission() {

        // lazy loaded
        if (_permission == null) {

            List<String> permissions = new ArrayList<String>(20);

            AbstractCommand parent = this;

            permissions.add(_info.getCommandName());

            while((parent = parent.getParent()) != null) {
                permissions.add(parent.getInfo().getCommandName());
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
     * Display the commands help info to the specified {@code CommandSender}
     */
    public void showHelp(final CommandSender sender, final int page) {
        PreCon.notNull(sender);
        PreCon.positiveNumber(page);

        if (!isHelpVisible(sender)) {
            return;
        }

        // batch operation to prevent each registered permission
        // from causing a permission recalculation
        Permissions.runBatchOperation(true, new Runnable() {

            @Override
            public void run () {

                final ChatPaginator pagin = new ChatPaginator(getPlugin(), 6,
                        Lang.get(getPlugin(), _COMMAND_PAGINATOR_TITLE));

                if (canExecute()) {
                    // add command to paginator
                    pagin.add(_info.getUsage(), _info.getDescription());
                }

                List<AbstractCommand> subCommands = new ArrayList<AbstractCommand>(20);

                for (AbstractCommand cmd : getSubCommands()) {

                    // Determine if the command has its own own sub commands 
                    // and put aside so it can be displayed at the end of the 
                    // help list
                    if (cmd.getSubCommands().size() > 0) {
                        subCommands.add(cmd);
                        continue;
                    }

                    if (!cmd.isHelpVisible(sender)) {
                        continue;
                    }

                    CommandInfoContainer info = cmd.getInfo();

                    // add command to paginator
                    pagin.add(info.getUsage(), info.getDescription());
                }

                // Add commands that were set aside because they have sub commands
                // and render differently
                for (AbstractCommand cmd : subCommands) {

                    if (!cmd.isHelpVisible(sender)) {
                        continue;
                    }

                    CommandInfoContainer info = cmd.getInfo();

                    // add info to get sub commands help to paginator
                    pagin.add(constructHelpUsage(cmd), info.getDescription());
                }

                // show paginator to CommandSender
                pagin.show(sender, page, FormatTemplate.CONSTANT_DEFINITION);
            }

        });
    }

    /**
     * Compare command names
     * Used for alphabetical sorting of commands.
     */
    @Override
    public int compareTo(AbstractCommand o) {
        return _info.getCommandName().compareTo(o._info.getCommandName());
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
     * Called after the command handler is set.
     *
     * <p>Provided as an optional implementation override.</p>
     *
     * @param commandHandler  The command handler that is set.
     */
    protected void onCommandHandlerSet(AbstractCommandHandler commandHandler) {
        PreCon.notNull(commandHandler);
    }

    /**
     * Determine if the supplied command name matches one of the
     * command names of the this command.
     *
     * @param parentName     The command name to match
     * @param possibleNames  A {@code String[]} of valid names
     */
    final boolean isCommandMatch(@Nullable String parentName, String[] possibleNames) {
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
     * @param parentNames    A {@code String[]} of possible names
     * @param possibleNames  A {@code String[]} of valid names
     */
    final boolean isCommandMatch(String[] parentNames, String[] possibleNames) {
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
     * Construct a string representing the command
     * a user should type to get help with this specific
     * command.
     */
    final String constructHelpUsage() {
        return constructHelpUsage(this);
    }

    /**
     * Set the commands command handler
     * Should only be called by the command handler or parent command
     */
    final void setCommandHandler(AbstractCommandHandler commandHandler, @Nullable String masterCommandName) {
        PreCon.notNull(commandHandler);

        if (_commandHandler != null)
            return;

        _commandHandler = commandHandler;
        setPlugin(commandHandler.getPlugin());

        CommandInfo info = this.getClass().getAnnotation(CommandInfo.class);
        _info = new CommandInfoContainer(getPlugin(), info, masterCommandName);

        // register queued sub commands
        for (Class<? extends AbstractCommand> commandClass : _subCommandQueue) {
            registerSubCommand(commandClass);
        }

        // remove queue
        _subCommandQueue = null;

        // set command handler in sub commands
        for (AbstractCommand subCommand : getSubCommands()) {
            if (subCommand.getCommandHandler() == null) {
                subCommand.setCommandHandler(_commandHandler, masterCommandName);
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

        onCommandHandlerSet(commandHandler);
    }


    /**
     * Construct a string representing the command
     * a user should type to get help with the specified
     * command.
     */
    static String constructHelpUsage(AbstractCommand command) {
        PreCon.notNull(command);

        Stack<AbstractCommand> commands = new Stack<AbstractCommand>();

        commands.add(command);

        AbstractCommand parent = command;
        while ((parent = parent.getParent()) != null) {
            commands.push(parent);
        }

        StringBuilder usage = new StringBuilder(32 * 3);
        usage.append(Lang.get(command.getPlugin(), _USAGE));

        while(!commands.isEmpty()) {
            usage.append(commands.pop()._info.getCommandName());
            usage.append(' ');
        }

        usage.append('?');

        // format plugin info into usage
        String result = TextUtils.formatPluginInfo(command.getPlugin(), usage.toString());

        // format colors and return
        return TextUtils.format(result);
    }

    /**
     * Determine if a command sender can see the command in help.
     */
    final boolean isHelpVisible(CommandSender sender) {

        // determine if the commands is visible in help
        if (!getInfo().isHelpVisible())
            return false;

        // determine if the CommandSender has permission to use the command
        if (sender instanceof Player &&
                !Permissions.has((Player)sender, getPermission().getName()))
            return false;

        return true;
    }
}
