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

import com.jcwhatever.nucleus.managed.commands.ICommandOwner;
import com.jcwhatever.nucleus.utils.ArrayUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.nucleus.utils.validate.IValidator;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Parses command string arrays to find the intended command and arguments.
 */
class CommandParser {

    private final CommandContainer _rootCommand;

    /**
     * Constructor.
     *
     * @param rootCommand  The root command.
     */
    public CommandParser(CommandContainer rootCommand) {
        PreCon.notNull(rootCommand);

        _rootCommand = rootCommand;
    }

    /**
     * Parse for a command and its arguments.
     *
     * @param commandCollection  The collection of command candidates.
     * @param components         The components of the text command.
     *
     * @return  Null if a command is not found and no base command is set.
     */
    @Nullable
    public ParsedCommand parseCommand(CommandCollection commandCollection, String[] components) {
        return parse(commandCollection, components, false);
    }

    /**
     * Get a command instance using a string path.
     *
     * <p>The format of the path is the command names separated by periods.
     * i.e. "command.subcommand1.subcommand2"</p>
     *
     * <p>The command collection provided should be the commands at the root
     * of the intended command path. Sub commands are found by traversing the
     * root (master) command hierarchy.</p>
     *
     * @param commandCollection  The master command candidates.
     * @param commandPath        The path of the command.
     */
    @Nullable
    public CommandContainer parsePath(CommandCollection commandCollection, String commandPath) {
        PreCon.notNull(commandPath);

        String[] pathComp = TextUtils.PATTERN_DOT.split(commandPath);

        ParsedCommand parsed = parseCommand(commandCollection, pathComp);
        if (parsed == null)
            return null;

        // there shouldn't be any left over command path components
        if (parsed.getArguments().length != 0)
            return null;

        return parsed.getCommand();
    }

    /**
     * Get a list of commands for tab complete.
     *
     * @param commandOwner  The command the args are for.
     * @param sender        The command sender.
     * @param args          The base command arguments.
     */
    public ParsedTabComplete parseTabComplete(ICommandOwner commandOwner,
                                              CommandSender sender, String[] args) {
        PreCon.notNull(commandOwner);
        PreCon.notNull(sender);
        PreCon.notNull(args);

        // get the primary command from the first argument
        ParsedCommand parsed = parse(commandOwner, args, true);
        if (parsed == null || args.length == 1) {

            List<String> result;

            if (args[0].isEmpty()) {
                result = args.length == 1
                        // get all commands (that can be seen)
                        ? filterCommandNames(sender, "", commandOwner)
                        : new ArrayList<String>(5);
            } else {

                // get possible command matches
                result = filterCommandNames(sender, args[0], commandOwner);
            }

            return new ParsedTabComplete(result, null, ArrayUtils.EMPTY_STRING_ARRAY);
        }

        final CommandContainer command = parsed.getCommand();
        final String[] arguments = parsed.getArguments();
        ParsedTabComplete tabComplete;

        if (arguments.length == 1 &&
                command.getCommands().size() > 0) {

            // generate list of sub command names the player has permission to use
            List<String> names = filterCommandNames(sender, arguments[0], parsed.getCommand());

            tabComplete = new ParsedTabComplete(names, command, arguments);
        }
        else if (arguments.length == 0 && parsed.getCommand().getParent() != null) {

            // generate list of command names the player has permission to use
            List<String> names = filterCommandNames(sender, "", parsed.getCommand().getParent());

            tabComplete = new ParsedTabComplete(names, command, arguments);

        }
        else {
            tabComplete = new ParsedTabComplete(new ArrayList<String>(5), command, arguments);
        }

        return tabComplete;
    }

    /*
     * Filter a command owners command names based on a search name and if the
     * sub command is help visible.
     */
    private List<String> filterCommandNames(final CommandSender sender,
                                            String searchName,
                                            final ICommandOwner commandOwner) {

        final String caseSearchName = searchName.toLowerCase();

        Collection<String> commandNames = commandOwner.getCommandNames();

        return TextUtils.search(commandNames,
                new IValidator<String>() {
                    @Override
                    public boolean isValid(String element) {
                        CommandContainer subCommand = (CommandContainer)commandOwner.getCommand(element);
                        return subCommand != null && subCommand.isHelpVisible(sender) &&
                                element.toLowerCase().startsWith(caseSearchName);
                    }
                });
    }

    /*
     * internal command parser
     */
    private ParsedCommand parse(ICommandOwner commandCollection, String[] components, boolean isStrict) {
        PreCon.notNull(commandCollection);
        PreCon.notNull(components);

        // get the primary command from the first argument
        CommandContainer command = null;

        if (components.length > 0 && !components[0].isEmpty()) {
            command = (CommandContainer)commandCollection.getCommand(components[0]);
        }

        // primary command not found
        if (command == null) {

            if (!isStrict) {
                return new ParsedCommand(_rootCommand, components, 0);
            }

            return null;
        }

        // trim the first element from the array
        String[] args = ArrayUtils.reduceStart(1, components);

        // parse arguments and get command and command specific arguments
        return getCommand(command, args, 1);
    }

    /*
     * Recursively parses a String[] of arguments for the specified
     * parent command and return a ParsedCommand containing the
     * {@link AbstractCommand} implementation that should be used to execute the command
     * as well as the arguments to be used for the returned command.
     */
    private ParsedCommand getCommand(CommandContainer parentCommand, @Nullable String[] args, int depth) {
        PreCon.notNull(parentCommand);

        if (args == null || args.length == 0)
            return new ParsedCommand(parentCommand, new String[0], depth);

        String subCmd = args[0].toLowerCase();
        String[] params = ArrayUtils.reduceStart(1, args);

        CommandContainer subCommand = subCmd.isEmpty() ? null : parentCommand.getCommand(subCmd);
        if (subCommand == null)
            return new ParsedCommand(parentCommand, args, depth);
        else {
            ParsedCommand p = getCommand(subCommand, params, depth + 1);
            if (p == null)
                return new ParsedCommand(parentCommand, args, depth);

            return p;
        }
    }

    /**
     * A data object that holds results of parsing for a command.
     */
    public final static class ParsedCommand {

        private final CommandContainer _command;
        private final String[] _arguments;
        private final int _depth;

        ParsedCommand(CommandContainer command, String[] arguments, int depth) {
            _command = command;
            _arguments = arguments;
            _depth = depth;
        }

        /**
         * Get the command.
         */
        public CommandContainer getCommand() {
            return _command;
        }

        /**
         * Get the arguments intended for the command.
         */
        public String[] getArguments() {
            return _arguments;
        }

        /**
         * Get the command path depth.
         */
        public int getDepth() {
            return _depth;
        }
    }

    /**
     * A data object that holds the results of parsing for
     * tab completions.
     */
    public final static class ParsedTabComplete {

        private final CommandContainer _command;
        private final String[] _arguments;
        private final List<String> _matches;

        ParsedTabComplete(List<String> matches, @Nullable CommandContainer command, String[] arguments) {
            _command = command;
            _matches = matches;
            _arguments = arguments;
        }

        /**
         * Get the list of command name matches for
         * a tab completion.
         */
        public List<String> getMatches() {
            return _matches;
        }

        /**
         * Get the command that was parsed, if any.
         */
        @Nullable
        public CommandContainer getCommand() {
            return _command;
        }

        /**
         * Get the arguments for the command.
         */
        public String[] getArguments() {
            return _arguments;
        }
    }
}
