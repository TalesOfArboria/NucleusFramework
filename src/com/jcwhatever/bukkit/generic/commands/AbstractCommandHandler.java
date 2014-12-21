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

import com.jcwhatever.bukkit.generic.commands.CommandParser.ParsedCommand;
import com.jcwhatever.bukkit.generic.commands.CommandParser.ParsedTabComplete;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.DuplicateParameterException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidParameterException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.bukkit.generic.commands.exceptions.MissingArgumentException;
import com.jcwhatever.bukkit.generic.commands.exceptions.TooManyArgsException;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.permissions.Permissions;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.List;
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


    private final CommandCollection _masterCommands = new CommandCollection();
    private final CommandParser _parser = new CommandParser();

    /**
     * Constructor
     *
     * @param plugin   The plugin the command handler is for
     */
    public AbstractCommandHandler(Plugin plugin) {
        super(plugin);

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
    public Collection<AbstractCommand> getCommands() {
        return _masterCommands.getCommands();
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

        initBaseCommand();

        ParsedCommand parsed = _parser.parseCommand(_masterCommands, args);

        if (parsed == null) {
            // command not found
            tellError(sender, Lang.get(_COMMAND_NOT_FOUND, baseCommandName));
            return true; // finish
        }

        AbstractCommand command = parsed.getCommand();
        String[] rawArguments = parsed.getArguments();

        // Check if the player has permissions to run the command
        if (!Permissions.has(sender, command.getPermission().getName())) {
            tellError(sender, Lang.get(_ACCESS_DENIED));
            return true;
        }

        // handle command help, display if the command argument is '?' or 'help'
        if (isCommandHelp(rawArguments)) {

            int page = 1;

            if (rawArguments.length == 2) {
                try {
                    page = Integer.parseInt(rawArguments[1]);
                }
                catch (NumberFormatException ignored) {}
            }

            command.showHelp(sender, page);

            return true; // finished
        }

        // Determine if the command can execute or if it requires sub commands
        if (!command.canExecute()) {
            tellError(sender, Lang.get(_COMMAND_INCOMPLETE, baseCommandName));
            return true; // finished
        }

        // get arguments
        CommandArguments arguments = getCommandArguments(sender, command, rawArguments, true);
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

        ParsedTabComplete parsed = _parser.parseTabComplete(_masterCommands, sender, args);

        AbstractCommand command = parsed.getCommand();
        String[] arguments = parsed.getArguments();
        List<String> matches = parsed.getMatches();

        if (command != null) {

            // give the command the opportunity to modify the list
            command.onTabComplete(
                    sender,
                    arguments,
                    matches);
        }

        // add the command help
        if (command != null && matches.size() > 1 &&
                (arguments.length == 0 ||
                (arguments.length == 1 && arguments[0].isEmpty()))) {
            parsed.getMatches().add("?");
        }

        return parsed.getMatches();
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
            return _parser.getBaseCommand();

        return _parser.parsePath(_masterCommands, commandPath);
    }

    /**
     * Registers a command with the command handler.
     *
     * @param commandClass  The commands implementation class
     */
    @Nullable
    public final <T extends AbstractCommand> T registerCommand(Class<T> commandClass) {
        PreCon.notNull(commandClass);

        String commandName = _masterCommands.add(commandClass);

        if (commandName == null) {
            _msg.debug("Failed to register command '{0}' possibly because another command with the " +
                            "same name is already registered and no alternative command names were provided.",
                    commandClass.getName());
            return null;
        }

        AbstractCommand command = _masterCommands.getCommand(commandName);
        if (command == null)
            throw new AssertionError();

        // set the commands command handler
        command.setCommandHandler(this, commandName);

        onMasterCommandInstantiated(command);

        @SuppressWarnings("unchecked")
        T result = (T)command;

        return result;
    }

    /**
     * Unregister a command from the command handler.
     *
     * @param commandClass  The commands implementation class.
     */
    public final boolean unregisterCommand(Class<? extends AbstractCommand> commandClass) {
        PreCon.notNull(commandClass);

        return _masterCommands.removeAll(commandClass);
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

        AbstractCommand command = _masterCommands.getCommand(commandClass);

        if (command == null) {
            String name = _masterCommands.add(commandClass);
            if (name == null) {
                _msg.debug("Failed to register based command '{0}'.", commandClass.getName());
                return;
            }

            command = _masterCommands.getCommand(name);
            if (command == null)
                throw new AssertionError();
        }

        _parser.setBaseCommand(command);
    }

    @Nullable
    private CommandArguments getCommandArguments(CommandSender sender,
                                                 AbstractCommand command,
                                                 String[] argArray,
                                                 boolean showMessages) {
        // Parse command arguments
        CommandArguments arguments;

        try {
            arguments = new CommandArguments(getPlugin(), command, argArray);

        } catch (TooManyArgsException e) {

            if (showMessages)
                tellError(sender, Lang.get(_TO_MANY_ARGS, command.constructHelpUsage()));

            return null; // finished

        } catch (InvalidArgumentException e) {

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
        } catch (MissingArgumentException e) {
            e.printStackTrace();

            if (showMessages)
                tellError(sender, Lang.get(_MISSING_ARGS, command.constructHelpUsage()));

            return null;
        }

        return arguments;
    }

    private boolean executeCommand(CommandSender sender, AbstractCommand command, CommandArguments parameters, boolean showMessages) {
        // execute the command
        try {
            command.execute(sender, parameters);
        }
        // catch invalid argument values
        catch (InvalidArgumentException e) {

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

    private void initBaseCommand() {
        if (_parser.getBaseCommand() != null)
            return;

        AbstractCommand aboutCommand = _masterCommands.getCommand("about");
        if (aboutCommand == null && registerCommand(AboutCommand.class) == null) {
            return;
        }

        _parser.setBaseCommand(_masterCommands.getCommand("about"));
    }

    private boolean isCommandHelp(String[] args) {
        return args.length > 0 &&
                ((args[0].equals("?")) || args[0].equalsIgnoreCase("help"));
    }

}

