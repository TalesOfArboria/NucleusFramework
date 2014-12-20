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

import com.jcwhatever.bukkit.generic.utils.ArrayUtils;
import com.jcwhatever.bukkit.generic.utils.EntryValidator;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.text.TextUtils;
import com.jcwhatever.bukkit.generic.utils.text.TextUtils.CaseSensitivity;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Parses command strings to find the intended command
 * and arguments.
 */
public class CommandParser {

    private AbstractCommand _baseCommand;

    /**
     * Get the base command used when no commands found.
     */
    @Nullable
    public AbstractCommand getBaseCommand() {
        return _baseCommand;
    }

    /**
     * Set the base command used when no commands are found.
     * @param command
     */
    public void setBaseCommand(AbstractCommand command) {
        _baseCommand = command;
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
     * Get a command instance using a string path. The format of the
     * path is the command names separated by periods.
     * i.e. "command.subcommand1.subcommand2"
     *
     * <p>The command collection provided should be the commands at the root
     * of the intended command path. Sub commands are found by traversing the
     * root (master) command hierarchy.</p>
     *
     * @param commandCollection  The master command candidates.
     * @param commandPath        The path of the command.
     */
    @Nullable
    public AbstractCommand parsePath(CommandCollection commandCollection, String commandPath) {
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
     * @param sender       The command sender.
     * @param args         The base command arguments.
     */
    public ParsedTabComplete parseTabComplete(CommandCollection commandCollection,
                                              CommandSender sender, String[] args) {
        PreCon.notNull(commandCollection);
        PreCon.notNull(sender);
        PreCon.notNull(args);

        // get the primary command from the first argument
        ParsedCommand parsed = parse(commandCollection, args, true);
        if (parsed == null || args.length == 1) {

            if (!args[0].isEmpty()) {

                // get possible command matches
                List<String> result = TextUtils.startsWith(
                        args[0], commandCollection.getCommandNames(), CaseSensitivity.IGNORE_CASE);

                return new ParsedTabComplete(result, null, ArrayUtils.EMPTY_STRING_ARRAY);
            }
            else if (args.length == 1) {

                // return all commands
                return new ParsedTabComplete(
                        commandCollection.getCommandNames(), null, ArrayUtils.EMPTY_STRING_ARRAY);
            }

            return new ParsedTabComplete(new ArrayList<String>(5), null, ArrayUtils.EMPTY_STRING_ARRAY);
        }

        final AbstractCommand command = parsed.getCommand();
        final String[] arguments = parsed.getArguments();
        ParsedTabComplete tabComplete;

        if (arguments.length == 1 &&
                command.getSubCommands().size() > 0) {

            // generate list of sub command names the player has permission to use
            List<String> names = filterSubCommandNames(sender, arguments[0], parsed.getCommand());

            tabComplete = new ParsedTabComplete(names, command, arguments);
        }
        else if (arguments.length == 0 && parsed.getCommand().getParent() != null) {

            // generate list of command names the player has permission to use
            List<String> names = filterSubCommandNames(sender, arguments[0], parsed.getCommand().getParent());

            tabComplete = new ParsedTabComplete(names, command, arguments);

        }
        else {
            tabComplete = new ParsedTabComplete(new ArrayList<String>(5), command, arguments);
        }

        return tabComplete;
    }

    // Filter a commands sub command names based on a suffix and if the
    // sub command is help visible.
    private List<String> filterSubCommandNames(final CommandSender sender,
                                               String suffix,
                                               final AbstractCommand parentCommand) {

        final String caseSuffix = suffix.toLowerCase();

        Collection<String> commandNames = parentCommand.getSubCommandNames();

        return TextUtils.search(commandNames,
                new EntryValidator<String>() {
                    @Override
                    public boolean isValid(String entry) {
                        AbstractCommand subCommand = parentCommand.getSubCommand(entry);
                        return subCommand != null && subCommand.isHelpVisible(sender) &&
                                entry.toLowerCase().startsWith(caseSuffix);
                    }
                });
    }

    // internal command parser
    private ParsedCommand parse(CommandCollection commandCollection, String[] components, boolean isStrict) {
        PreCon.notNull(commandCollection);
        PreCon.notNull(components);

        // get the primary command from the first argument
        AbstractCommand command = null;

        if (components.length > 0) {
            command = commandCollection.fromFirst(components);
        }

        // primary command not found
        if (command == null) {

            if (_baseCommand != null && !isStrict) {
                return new ParsedCommand(_baseCommand, components, 0);
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
     * parent command and return a {@code ParsedCommand} containing the
     * {@code AbstractCommand} implementation that should be used to execute the command
     * as well as the arguments to be used for the returned command.
     */
    private ParsedCommand getCommand(AbstractCommand parentCommand, @Nullable String[] args, int depth) {
        PreCon.notNull(parentCommand);

        if (args == null || args.length == 0)
            return new ParsedCommand(parentCommand, new String[0], depth);

        String subCmd = args[0].toLowerCase();
        String[] params = ArrayUtils.reduceStart(1, args);

        AbstractCommand subCommand = subCmd.isEmpty() ? null : parentCommand.getSubCommand(subCmd);
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

        private final AbstractCommand _command;
        private final String[] _arguments;
        private final int _depth;

        ParsedCommand(AbstractCommand command, String[] arguments, int depth) {
            _command = command;
            _arguments = arguments;
            _depth = depth;
        }

        /**
         * Get the command.
         */
        public AbstractCommand getCommand() {
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

        private final AbstractCommand _command;
        private final String[] _arguments;
        private final List<String> _matches;

        ParsedTabComplete(List<String> matches, @Nullable AbstractCommand command, String[] arguments) {
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
        public AbstractCommand getCommand() {
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
