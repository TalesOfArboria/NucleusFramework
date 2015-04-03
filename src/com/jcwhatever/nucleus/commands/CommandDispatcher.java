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
import com.jcwhatever.nucleus.commands.CommandParser.ParsedCommand;
import com.jcwhatever.nucleus.commands.CommandParser.ParsedTabComplete;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.managed.messaging.IMessenger;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.utils.ArrayUtils;
import com.jcwhatever.nucleus.providers.permissions.Permissions;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Dispatches Bukkit commands to the proper {@link AbstractCommand} for execution.
 *
 * <p>Also handles tab completion.</p>
 */
public class CommandDispatcher implements
        CommandExecutor, TabCompleter, ICommandOwner, IPluginOwned {

    @Localizable static final String _ACCESS_DENIED = "{RED}Access denied.";

    @Localizable static final String _COMMAND_INCOMPLETE =
            "{RED}Command incomplete. Type '{0: usage}' for help.";

    @Localizable static final String _COMMAND_NOT_FOUND =
            "{RED}Command not found. Type '{0: usage}' for help.";


    private final Plugin _plugin;
    private AboutCommand _defaultRoot;
    private CommandCollection _rootCommands;
    private final IMessenger _msg;
    private final Set<String> _pluginCommands;
    private final CommandUtils _utils;
    private final UsageGenerator _usageGenerator;

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     */
    public CommandDispatcher(Plugin plugin) {
        PreCon.notNull(plugin);

        _plugin = plugin;

        _rootCommands = new CommandCollection();
        _msg = Nucleus.getMessengerFactory().create(plugin);
        _utils = new CommandUtils(plugin);
        _usageGenerator = new UsageGenerator();

        if (plugin.getDescription().getCommands() == null) {
            NucMsg.warning(plugin, "Plugin has no commands registered in its plugin.yml file.");
            _pluginCommands = new HashSet<>(2);
        }
        else {
            _pluginCommands = new HashSet<>(plugin.getDescription().getCommands().keySet());
        }

        _defaultRoot = new AboutCommand();
        _defaultRoot.setDispatcher(this, null);

        registerCommands();
    }

    /**
     * Get the owning plugin.
     */
    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the dispatchers command utils.
     */
    public CommandUtils getUtils() {
        return _utils;
    }

    /**
     * Called by Bukkit when a command is executed.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String rootName, String[] rootArguments) {

        AbstractCommand rootCommand = _rootCommands.getCommand(cmd.getName());
        if (rootCommand == null) {
            rootCommand = _defaultRoot;
        }

        rootCommand.getInfo().setSessionRootName(rootName);

        CommandParser parser = new CommandParser(rootCommand);
        ParsedCommand parsed = parser.parseCommand(rootCommand.getCommandCollection(), rootArguments);

        if (parsed == null) {
            // command not found
            _utils.tellError(sender, NucLang.get(_COMMAND_NOT_FOUND, rootName));
            return true; // finish
        }

        AbstractCommand command = parsed.getCommand();
        String[] rawArguments = parsed.getArguments();

        // Check if the player has permissions to run the command
        if (sender instanceof Player && !Permissions.has((Player)sender, command.getPermission().getName())) {
            _utils.tellError(sender, NucLang.get(_ACCESS_DENIED));
            return true;
        }

        // handle command help, display if the command argument is '?' or 'help'
        if (isCommandHelp(rawArguments)) {

            int page = TextUtils.parseInt(
                    ArrayUtils.get(rawArguments, 1, null), 1);

            command.showHelp(sender, page);

            return true; // finished
        }

        if (isDetailedHelp(rawArguments)) {

            int page = TextUtils.parseInt(
                    ArrayUtils.get(rawArguments, 1, null), 1);

            command.showDetailedHelp(sender, page);

            return true; // finished
        }

        // Determine if the command can execute or if it requires sub commands
        if (!command.canExecute()) {
            _utils.tellError(sender,
                    NucLang.get(_COMMAND_INCOMPLETE,
                            _usageGenerator.generate(command, rootName, UsageGenerator.INLINE_HELP)));
            return true; // finished
        }

        // Parse command arguments
        CommandArguments arguments;

        try {
            arguments = new CommandArguments(command, rawArguments);

        } catch (CommandException e) {
            _utils.tellError(sender, e.getMessage());
            return true; // finished
        }

        // execute the command
        try {
            command.execute(sender, arguments);
        }
        catch (CommandException e) {
            _utils.tellError(sender, e.getMessage());
        }

        return true;
    }

    /**
     * Called by bukkit when a command is tab completed.
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {

        if (args.length == 0)
            return new ArrayList<>(0);

        AbstractCommand rootCommand = _rootCommands.getCommand(args[0]);
        if (rootCommand == null) {
            rootCommand = _defaultRoot;
        }

        CommandParser _parser = new CommandParser(rootCommand);
        ParsedTabComplete parsed = _parser.parseTabComplete(rootCommand, sender, args);

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
     * Register and instantiate a root command.
     */
    @Override
    public boolean registerCommand(Class<? extends AbstractCommand> commandClass) {
        PreCon.notNull(commandClass);

        String rootName = _rootCommands.addCommand(commandClass);
        if (rootName == null) {
            _msg.debug("Failed to register command '{0}' possibly because another command with the " +
                            "same name is already registered and no alternative command names were provided.",
                    commandClass.getName());
            return false;
        }

        AbstractCommand command = _rootCommands.getCommand(rootName);
        if (command == null)
            throw new AssertionError();

        if (!_pluginCommands.contains(rootName)) {
            _rootCommands.removeAll(command);
            if (!_defaultRoot.registerCommand(commandClass))
                return false;

            command = _defaultRoot.getCommandCollection().getCommand(commandClass);
            if (command == null)
                throw new AssertionError();
        }

        command.setDispatcher(this, null);
        return true;
    }

    /**
     * Unregister and remove a root command.
     */
    @Override
    public boolean unregisterCommand(Class<? extends AbstractCommand> commandClass) {

        if (!_rootCommands.unregisterCommand(commandClass) &&
                !_defaultRoot.unregisterCommand(commandClass)) {
            return false;
        }
        return true;
    }

    /**
     * Get a root command by name.
     */
    @Nullable
    @Override
    public AbstractCommand getCommand(String commandName) {

        AbstractCommand result = _rootCommands.getCommand(commandName);
        if (result == null) {
            result = _defaultRoot.getCommand(commandName);
        }

        return result;
    }

    /**
     * Get all root commands.
     */
    @Override
    public Collection<AbstractCommand> getCommands() {
        Collection<AbstractCommand> commands = _rootCommands.getCommands();
        commands.add(_defaultRoot);
        return commands;
    }

    /**
     * Get the names of the root commands.
     */
    @Override
    public Collection<String> getCommandNames() {
        return _rootCommands.getCommandNames();
    }

    /**
     * Called after initialization when the command dispatcher
     * is ready to accept command registrations.
     *
     * <p>Intended to be overridden by a class that extends
     * {@link CommandDispatcher}.</p>
     *
     * <p>Used for convenience. Commands can still be registered
     * outside of the dispatcher any time after instantiating it.</p>
     */
    protected void registerCommands () {
        // do nothing
    }

    // determine if the arguments provided are
    // to view a commands help.
    private boolean isCommandHelp(String[] args) {
        return args.length > 0 &&
                ((args[0].equals("?")) || args[0].equalsIgnoreCase("help"));
    }

    // determine if the arguments provided are
    // to view a commands detailed help.
    private boolean isDetailedHelp(String[] args) {
        return args.length > 0 &&
                ((args[0].equals("??")));
    }
}
