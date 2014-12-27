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

package com.jcwhatever.nucleus.commands.arguments;

import com.jcwhatever.nucleus.collections.RetrievableSet;
import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.commands.parameters.CommandParameter;
import com.jcwhatever.nucleus.commands.parameters.FlagParameter;
import com.jcwhatever.nucleus.utils.ArrayUtils;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Parses command arguments.
 */
public class ArgumentParser {

    /**
     * Parse command arguments
     *
     * @param command  The command the arguments are for.
     * @param args     The arguments.
     */
    public ArgumentParseResults parse(AbstractCommand command, String[] args)
            throws CommandException {

        PreCon.notNull(command);
        PreCon.notNull(args);

        ArgumentParseResults results = new ArgumentParseResults(command);

        Deque<String> arguments = ArrayUtils.toDeque(args);
        Deque<CommandParameter> staticParameters = new LinkedList<>(
                command.getInfo().getStaticParams());

        // parse arguments for static parameters.
        parseStaticArgs(command, results, staticParameters, arguments);

        // check if there are floating parameters for the command.
        if (command.getInfo().getRawFloatingParams().length == 0) {

            // if there are no more parameters but there are still
            // arguments left, then there are too many arguments.
            if (!arguments.isEmpty()) {
                CommandException.tooManyArgs(command);
            }

            // nothing left to do
            return results;
        }

        // get ready to parse floating and flag parameters.
        RetrievableSet<CommandParameter> floating = new RetrievableSet<>(command.getInfo().getFloatingParams());
        RetrievableSet<FlagParameter> flags = new RetrievableSet<>(command.getInfo().getFlagParams());

        // parse arguments for non static parameters.
        parseNonStaticArgs(command, results, floating, flags, arguments);

        return results;

    }

    // parse arguments for static parameters
    private void parseStaticArgs(AbstractCommand command,
                                 ArgumentParseResults results,
                                 Deque<CommandParameter> parameterList,
                                 Deque<String> arguments)
            throws CommandException {

        while (!parameterList.isEmpty()) {

            CommandParameter parameter = parameterList.removeFirst();

            String name = parameter.getName();
            String value = null;

            if (!arguments.isEmpty()) {

                String arg = arguments.removeFirst();

                // Should not be any floating or flag parameters  before required arguments
                if (arg.startsWith("-")) {

                    // the last static parameter might be optional, in which
                    // case it should have a default value.

                    // If there are still more static parameters, it means this
                    // is not the last parameter and the value is incorrect.
                    if (!parameterList.isEmpty()) {
                        CommandException.invalidArgument(command, parameter.getName());
                    }

                    // No default value defined means a discreet value is expected.
                    else if (!parameter.hasDefaultValue()) {
                        CommandException.missingRequiredArg(command, parameter);
                    }

                    // re-insert floating argument so the other parsers
                    // will see it. Since this is the last static parameter,
                    // this is the end of the loop.
                    else {
                        arguments.addFirst(arg);
                    }
                }
                else {
                    // get parameter value
                    value = parseArgValue(arg, arguments);
                }
            }

            // add argument
            if (value != null || parameter.hasDefaultValue()) {
                CommandArgument commandArgument = new CommandArgument(parameter, value);

                if (results.getArgMap().containsKey(name)) {
                    CommandException.duplicateArg(command, parameter);
                }

                results.getStaticArgs().add(commandArgument);
                results.getArgMap().put(name, commandArgument);
            }
        }
    }

    // parse arguments for floating parameters and flags
    private void parseNonStaticArgs(AbstractCommand command,
                                    ArgumentParseResults results,
                                    RetrievableSet<CommandParameter> parameters,
                                    RetrievableSet<FlagParameter> flags,
                                    Deque<String> arguments)
            throws CommandException {

        while (!arguments.isEmpty()) {

            String paramName = arguments.removeFirst();

            if (!paramName.startsWith("-"))
                CommandException.invalidArgument(command, paramName);

            // check if floating parameter
            if (paramName.startsWith("--")) {

                paramName = paramName.substring(2);

                CommandParameter parameter = parameters.removeRetrieve(new CommandParameter(paramName, null));
                if (parameter == null) {
                    CommandException.invalidParam(command, paramName);
                }

                if (arguments.isEmpty()) {
                    CommandException.missingRequiredArg(command, parameter);
                }

                String arg = arguments.removeFirst();
                CommandArgument commandArgument = new CommandArgument(parameter, arg);

                if (results.getArgMap().containsKey(paramName)) {
                    CommandException.duplicateArg(command, parameter);
                }

                results.getFloatingArgs().add(commandArgument);
                results.getArgMap().put(paramName, commandArgument);
            }
            // the parameter is a flag
            else {

                paramName = paramName.substring(1);

                FlagParameter parameter = flags.removeRetrieve(new FlagParameter(paramName, -1));
                if (parameter == null) {
                    CommandException.invalidFlag(command, paramName);
                }

                results.setFlag(paramName, true);
            }
        }

        if (!parameters.isEmpty()) {

            for (CommandParameter param : parameters) {
                if (!param.hasDefaultValue()) {
                    CommandException.missingRequiredArg(command, param);
                }
            }
        }
    }

    /**
     * parses quotes if present or returns current argument.
     *
     * @param currentArg  The current argument
     * @param argsQueue   The queue of arguments words
     */
    private String parseArgValue(String currentArg, Deque<String> argsQueue) {

        // check to see if parsing a literal
        String quote = null;

        // detect double quote
        if (currentArg.startsWith("\"")) {
            quote = "\"";
        }
        // detect single quote
        else if (currentArg.startsWith("'")) {
            quote = "'";
        }

        // check for quoted literal
        if (quote != null) {


            String firstWord = currentArg.substring(1); // remove quotation

            // make sure the literal isn't closed on the same word
            if (firstWord.endsWith(quote)) {

                // remove end quote
                return firstWord.substring(0, firstWord.length() - 1);
            }
            // otherwise parse ahead until end of literal
            else {

                StringBuilder literal = new StringBuilder(currentArg.length() * argsQueue.size());
                literal.append(firstWord);

                while (!argsQueue.isEmpty()) {
                    String nextArg = argsQueue.removeFirst();

                    // check if this is the final word in the literal
                    if (nextArg.endsWith(quote)) {

                        //remove end quote
                        nextArg = nextArg.substring(0, nextArg.length() - 1);

                        literal.append(' ');
                        literal.append(nextArg);
                        break;
                    }

                    literal.append(' ');
                    literal.append(nextArg);
                }
                return literal.toString();
            }
        }
        // value is unquoted argument
        else {
            return currentArg;
        }
    }
}
