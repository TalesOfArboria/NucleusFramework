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
import com.jcwhatever.bukkit.generic.commands.exceptions.DuplicateParameterException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidParameterException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.commands.exceptions.MissingCommandAnnotationException;
import com.jcwhatever.bukkit.generic.commands.exceptions.TooManyArgsException;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.permissions.Permissions;
import com.jcwhatever.bukkit.generic.utils.ArrayUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.text.TextUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Command Executor to handle commands
 */
public abstract class AbstractCommandHandler extends AbstractCommandUtils implements CommandExecutor {

    @Localizable static final String _TO_MANY_ARGS = "Too many arguments. Type '{0}' for help.";
    @Localizable static final String _MISSING_ARGS = "Missing arguments. Type '{0}' for help.";
    @Localizable static final String _ACCESS_DENIED = "Access denied.";
    @Localizable static final String _COMMAND_INCOMPLETE = "Command incomplete. Type '/{0} ?' for help.";
    @Localizable static final String _COMMAND_NOT_FOUND = "Command not found. Type '/{0} ?' for help.";
    @Localizable static final String _INVALID_PARAMETER = "Invalid parameter detected: {0}";
    @Localizable static final String _DUPLICATE_PARAMETER = "The parameter named '{0}' has a duplicate.";
    @Localizable static final String _CANT_EXECUTE_AS = "Cannot execute command as {0}.";
    @Localizable static final String _PARAMETER_DESCRIPTION = "{WHITE}Parameter description: {GRAY}{0}";
    @Localizable static final String _REASON = "Reason: {0}";


    private final Map<String, AbstractCommand> _masterCommands;
    private AbstractCommand _baseCommand;

    private List<AbstractCommand> _sortedCommands;

    /**
     * Constructor
     *
     * @param plugin   The plugin the command handler is for
     */
    public AbstractCommandHandler(Plugin plugin) {
        super(plugin);
        _masterCommands = new HashMap<String, AbstractCommand>(20);

        Permissions.runBatchOperation(true, new Runnable() {

            @Override
            public void run() {
                registerCommand(AboutCommand.class);
                registerCommands();
            }

        });

        registerHelpCommand();
    }

    /**
     * Get a collection of all registered top level commands.
     */
    public final Collection<AbstractCommand> getCommands() {

        if (_sortedCommands == null) {

            Set<AbstractCommand> commandSet = new HashSet<AbstractCommand>(_masterCommands.values());
            List<AbstractCommand> commands = new ArrayList<AbstractCommand>(commandSet);

            Collections.sort(commands);

            _sortedCommands = commands;
        }
        return new ArrayList<AbstractCommand>(_sortedCommands);
    }

    /**
     * Called by Bukkit to begin processing a player command
     */
    @Override
    public boolean onCommand(CommandSender sender, Command bcmd, String baseCommandName, String[] args) {
        PreCon.notNull(sender);
        PreCon.notNull(bcmd);
        PreCon.notNull(baseCommandName);
        PreCon.notNull(args);

        // get the primary command from the first argument
        AbstractCommand command = null;

        if (args.length > 0) {
            command = _masterCommands.get(args[0]);
        }

        // command not found
        if (command == null) {

            // set default base command if none set
            if (_baseCommand == null) {
                setBaseCommand(AboutCommand.class);
            }

            // get arguments for base command
            CommandArguments baseCommandArgs = getCommandArguments(sender, _baseCommand, args, false);
            if (baseCommandArgs != null) {

                // execute base command and finish if successful.
                if (executeCommand(sender, _baseCommand, baseCommandArgs, false))
                    return true; // finished
            }

            // command not found
            tellError(sender, Lang.get(_COMMAND_NOT_FOUND, baseCommandName));
            return true;
        }

        // trim the first element from the array
        String[] argArray = ArrayUtils.reduceStart(1, args);

        // parse arguments and get command and command specific arguments
        CommandPackage commandPackage = getCommand(command, argArray, 1);
        if (commandPackage != null) {
            // update command and arguments
            command = commandPackage.command;
            argArray = commandPackage.arguments;
        }

        // Check if the player has permissions to run the command
        if (!Permissions.has(sender, command.getPermission().getName())) {
            tellError(sender, Lang.get(_ACCESS_DENIED));
            return true;
        }

        // handle command help, display if the command argument is '?' or 'help'
        if (argArray.length > 0 &&
                ((argArray[0].equals("?")) ||
                        argArray[0].equalsIgnoreCase("help"))) {

            int page = 1;

            if (argArray.length == 2) {
                try {
                    page = Integer.parseInt(argArray[1]);
                }
                catch (NumberFormatException ignored) {}
            }

            command.showHelp(sender, page);

            return true; // finished
        }

        // Determine if the command can execute or if it requires sub commands
        boolean canExecute = command.canExecute();

        if (!canExecute) {
            tellError(sender, Lang.get(_COMMAND_INCOMPLETE, baseCommandName));
            return true; // finished
        }

        // get arguments
        CommandArguments arguments = getCommandArguments(sender, command, argArray, true);
        if (arguments == null)
            return true; // finished

        // execute the command
        executeCommand(sender, command, arguments, true);


        return true;
    }

    /**
     * Called to modify a tab completion list.
     *
     * @param sender       The command sender.
     * @param args         The base command arguments.
     */
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        PreCon.notNull(sender);
        PreCon.notNull(args);

        // get the primary command from the first argument
        AbstractCommand masterCommand = _masterCommands.get(args[0]);

        if (masterCommand == null && !args[0].isEmpty()) {
            // get possible command matches
            return searchCommandNames(args[0], _masterCommands.keySet());
        }
        // check if the master command is the only argument
        else if (args.length == 1) {
            // return all commands
            return new ArrayList<>(_masterCommands.keySet());
        }

        // trim the first element from the array (the master command name)
        String[] argArray = ArrayUtils.reduceStart(1, args);

        List<String> result = new ArrayList<>(10);

        // parse arguments and get command and command specific arguments
        CommandPackage commandPackage = getCommand(masterCommand, argArray, 1);
        if (commandPackage != null) {

            AbstractCommand subCommand = commandPackage.command;
            String[] arguments = commandPackage.arguments;

            // add sub commands to list
            if (subCommand.getSubCommands().size() > 0 &&
                    (arguments.length ==0 || !arguments[0].equals("?"))) {


                // generate list of sub command names the player has permission to use
                Collection<String> names = subCommand.getSubCommandNames();
                Iterator<String> iterator = names.iterator();
                while (iterator.hasNext()) {

                    String name = iterator.next();
                    AbstractCommand command = subCommand.getSubCommand(name);
                    if (command == null)
                        continue;

                    if (!sender.hasPermission(command.getPermission().getName())) {
                        iterator.remove();
                    }
                }

                if (arguments.length == 1 &&
                        !arguments[0].isEmpty()) {
                    // search for command matches
                    result.addAll(searchCommandNames(arguments[0], names));
                }
                else {
                    // add all commands
                    result.addAll(names);
                }
            }

            // give the command the opportunity to modify the list
            subCommand.onTabComplete(
                    sender,
                    arguments,
                    result);

            // add the command help
            if (arguments.length == 0 ||
                    (arguments.length == 1 && arguments[0].isEmpty())) {
                result.add("?");
            }
        }

        return result;
    }

    /**
     * Get a command instance using a string path. The format of the
     * path is the command names separated by periods.
     * i.e. "command.subcommand1.subcommand2"
     *
     * @param commandPath  The path of the command.
     */
    @Nullable
    public final AbstractCommand getCommand(String commandPath) {
        PreCon.notNull(commandPath);

        if (commandPath.isEmpty())
            return _baseCommand;

        String[] pathComp = TextUtils.PATTERN_DOT.split(commandPath);

        // get the master command to search
        AbstractCommand masterCommand = _masterCommands.get(pathComp[0]);
        if (masterCommand == null)
            return null;

        // if only one component, we already have the command
        if (pathComp.length == 1)
            return masterCommand;

        CommandPackage commandPackage = getCommand(masterCommand, ArrayUtils.reduceStart(1, pathComp), 1);
        if (commandPackage == null)
            return null;

        // there shouldn't be any left over command path components
        if (commandPackage.arguments.length != 0)
            return null;

        return commandPackage.command;
    }

    /**
     * Registers a command with the command handler.
     *
     * @param commandClass  The commands implementation class
     */
    public final boolean registerCommand(Class<? extends AbstractCommand> commandClass) {
        PreCon.notNull(commandClass);

        return registerCommand(commandClass, false);
    }

    /**
     * Unregister a command from the command handler.
     *
     * @param commandClass  The commands implementation class.
     */
    public final boolean unregisterCommand(Class<? extends AbstractCommand> commandClass) {
        PreCon.notNull(commandClass);

        CommandInfo commandInfo = commandClass.getAnnotation(CommandInfo.class);
        if (commandInfo == null)
            throw new MissingCommandAnnotationException(commandClass);

        for (String commandName : commandInfo.command())
            _masterCommands.remove(commandName.trim().toLowerCase());

        return true;
    }

    /**
     * Called when the command handler is instantiated.
     * Implementation should register permanent commands here.
     *
     * <p>Called within a permissions batch operation to improve permissions performance.</p>
     */
    protected abstract void registerCommands();

    /**
     * Optional method called after a command is instantiated.
     *
     * <p>Provided as an optional implementation override.</p>
     *
     * @param instance  The instantiated command
     */
    protected void onMasterCommandInstantiated(AbstractCommand instance) {
        PreCon.notNull(instance);
    }

    /**
     * Register the help command
     */
    protected void registerHelpCommand() {
        this.registerCommand(HelpCommand.class);
    }

    /**
     * Set the command called when no arguments or sub classes are provided.
     * If not set, default is used.
     *
     * @param commandClass The commands implementation class.
     */
    protected final void setBaseCommand(Class<? extends AbstractCommand> commandClass) {
        PreCon.notNull(commandClass);

        this.registerCommand(commandClass, true);
    }

    /**
     * Recursively parses a String[] of arguments for the specified
     * parent command and return a {@code CommandPackage} containing the
     * {@code AbstractCommand} implementation that should be used to execute the command
     * as well as the arguments to be used for the returned command.
     *
     * @param parentCommand  The command the supplied arguments are for
     * @param args           The command arguments
     */
    private CommandPackage getCommand(AbstractCommand parentCommand, @Nullable String[] args, int depth) {
        PreCon.notNull(parentCommand);

        if (args == null || args.length == 0)
            return new CommandPackage(parentCommand, new String[0], depth);

        String subCmd = args[0].toLowerCase();
        String[] params = ArrayUtils.reduceStart(1, args);

        AbstractCommand subCommand = subCmd.isEmpty() ? null : parentCommand.getSubCommand(subCmd);
        if (subCommand == null)
            return new CommandPackage(parentCommand, args, depth);
        else {
            CommandPackage p = getCommand(subCommand, params, depth + 1);
            if (p == null)
                return new CommandPackage(parentCommand, args, depth);

            return p;
        }
    }

    @Nullable
    private CommandArguments getCommandArguments(CommandSender sender, AbstractCommand command, String[] argArray, boolean showMessages) {

        // Parse command arguments

        CommandArguments arguments;

        try {
            arguments = new CommandArguments(getPlugin(), command.getInfo(), argArray);

        } catch (TooManyArgsException e) {

            if (showMessages)
                tellError(sender, Lang.get(_TO_MANY_ARGS, command.constructHelpUsage()));

            return null; // finished

        } catch (InvalidValueException e) {

            if (showMessages && e.getMessage() != null) {
                tellError(sender, e.getMessage());

                if (e.getParameterDescription() != null) {
                    tell(sender, Lang.get(_PARAMETER_DESCRIPTION, e.getParameterDescription()));
                }
            }

            return null; // finished

        } catch (DuplicateParameterException e) {

            if (showMessages) {
                if (e.getMessage() != null) {
                    tellError(sender, e.getMessage());
                } else {
                    tellError(sender, Lang.get(_DUPLICATE_PARAMETER, e.getParameterName()));
                }
            }

            return null; // finished

        } catch (InvalidParameterException e) {

            if (showMessages)
                tellError(sender, Lang.get(_INVALID_PARAMETER, e.getParameterName()));

            return null; // finished
        }

        // Make sure the number of provided arguments match the expected amount
        if (arguments.staticSize() < arguments.expectedSize()) {

            if (showMessages)
                tellError(sender, Lang.get(_MISSING_ARGS, command.constructHelpUsage()));

            return null; // finished
        }

        return arguments;
    }

    private boolean executeCommand(CommandSender sender, AbstractCommand command, CommandArguments parameters, boolean showMessages) {
        // execute the command
        try {
            command.execute(sender, parameters);
        }
        // catch invalid argument values
        catch (InvalidValueException e) {

            if (showMessages && e.getMessage() != null) {
                tellError(sender, e.getMessage());

                if (e.getParameterDescription() != null) {
                    tell(sender, Lang.get(_PARAMETER_DESCRIPTION, e.getParameterDescription()));
                }
            }
            return false;
        }
        // catch invalid command senders
        catch (InvalidCommandSenderException e) {
            if (showMessages) {
                tellError(sender, Lang.get(_CANT_EXECUTE_AS, e.getSenderType().name()));

                if (e.getReason() != null) {
                    tellError(sender, Lang.get(_REASON, e.getReason()));
                }
            }
            return false;
        }
        return true;
    }

    private boolean registerCommand(Class<? extends AbstractCommand> commandClass, boolean isBaseCommand) {

        // make sure command has required command info annotation
        CommandInfo info = commandClass.getAnnotation(CommandInfo.class);
        if (info == null) {
            throw new MissingCommandAnnotationException(commandClass);
        }

        // instantiate command
        AbstractCommand instance;

        try {
            instance = commandClass.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }

        // split commands and add each command
        String[] commands = info.command();
        String commandName = null;
        for (String command : commands) {
            if (!_masterCommands.containsKey(command)) {

                if (commandName == null) {
                    commandName = command;
                }
                _masterCommands.put(command, instance);
            }
        }

        if (commandName == null && !isBaseCommand) {
            _msg.warning("Failed to register command '{0}' because another command with the same name " +
                    "is already registered and no alternative command names were provided.", info.command()[0]);
            return false;
        }

        // set the commands command handler
        instance.setCommandHandler(this, commandName);

        onMasterCommandInstantiated(instance);

        if (isBaseCommand) {
            _baseCommand = instance;
            return true; // finish
        }

        // clear sorted commands cache
        _sortedCommands = null;

        return true;
    }

    private List<String> searchCommandNames(String searchText, Collection<String> commandNames) {

        if (searchText.isEmpty())
            return new ArrayList<>(0);

        List<String> result = new ArrayList<>(commandNames.size());

        for (String command : commandNames) {
            if (command.toLowerCase().startsWith(searchText.toLowerCase()))
                result.add(command);
        }

        return result;
    }


    /**
     * A data object that holds an {@code AbstractCommand} implementation
     * as well as a {@code String[]} of arguments it should be
     * executed with.
     */
    private static class CommandPackage {
        AbstractCommand command;
        String[] arguments;
        int depth;

        CommandPackage (AbstractCommand command, String[] parameters, int depth) {
            this.command = command;
            this.arguments = parameters;
            this.depth = depth;
        }
    }

}

