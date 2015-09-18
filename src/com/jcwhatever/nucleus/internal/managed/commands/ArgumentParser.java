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

import com.jcwhatever.nucleus.collections.RetrievableSet;
import com.jcwhatever.nucleus.managed.commands.IRegisteredCommand;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.parameters.ICommandParameter;
import com.jcwhatever.nucleus.managed.commands.parameters.IFlagParameter;
import com.jcwhatever.nucleus.utils.ArrayUtils;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Parses command arguments.
 */
class ArgumentParser {

    // common floating argument prefix, all floating arguments begin with this
    public static final String COMMON_PREFIX = "-";
    // floating argument prefix
    public static final String FLOATING_PREFIX = COMMON_PREFIX;
    // flag prefix
    public static final String FLAG_PREFIX = COMMON_PREFIX + '-';

    /**
     * Parse command arguments
     *
     * @param command  The command the arguments are for.
     * @param args     The arguments.
     */
    public ArgumentParseResults parse(IRegisteredCommand command, String[] args)
            throws CommandException {

        PreCon.notNull(command);
        PreCon.notNull(args);

        ArgumentParseResults results = new ArgumentParseResults(command);

        Deque<String> arguments = ArrayUtils.toDeque(args);
        Deque<ICommandParameter> staticParameters = new ArrayDeque<>(
                command.getInfo().getStaticParams());

        // parse arguments for static parameters.
        parseStaticArgs(command, results, staticParameters, arguments);

        // check if there are floating parameters for the command.
        if (command.getInfo().getRawFloatingParams().length == 0 &&
                command.getInfo().getRawFlagParams().length == 0) {

            // if there are no more parameters but there are still
            // arguments left, then there are too many arguments.
            if (!arguments.isEmpty())
                throw CommandException.tooManyArgs(command);


            // nothing left to do
            return results;
        }

        // get ready to parse floating and flag parameters.
        RetrievableSet<ICommandParameter> floating = new RetrievableSet<>(command.getInfo().getFloatingParams());
        RetrievableSet<IFlagParameter> flags = new RetrievableSet<>(command.getInfo().getFlagParams());

        // parse arguments for non static parameters.
        parseNonStaticArgs(command, results, floating, flags, arguments);

        return results;

    }

    // parse arguments for static parameters
    private void parseStaticArgs(IRegisteredCommand command,
                                 ArgumentParseResults results,
                                 Deque<ICommandParameter> staticParameters,
                                 Deque<String> arguments)
            throws CommandException {

        while (!staticParameters.isEmpty()) {

            ICommandParameter parameter = staticParameters.removeFirst();

            String name = parameter.getName();
            String value = null;

            if (!arguments.isEmpty()) {

                String arg = arguments.removeFirst();

                // Should not be any floating or flag parameters  before required arguments
                if (arg.startsWith(COMMON_PREFIX)) {

                    // the last static parameter might be optional, in which
                    // case it should have a default value.

                    // If there are still more static parameters, it means this
                    // is not the last parameter and the value is incorrect.
                    if (!staticParameters.isEmpty()) {
                        throw CommandException.invalidArgument(command, parameter.getName());
                    }

                    // No default value defined means a discreet value is expected.
                    else if (!parameter.hasDefaultValue()) {
                        throw CommandException.missingRequiredArg(command, parameter);
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
                Argument argument = new Argument(parameter, value);

                if (results.getArgMap().containsKey(name))
                    throw CommandException.duplicateArg(command, parameter);


                results.getStaticArgs().add(argument);
                results.getArgMap().put(name, argument);
            }
            else {
                throw CommandException.missingRequiredArg(command, parameter);
            }
        }
    }

    // parse arguments for floating parameters and flags
    private void parseNonStaticArgs(IRegisteredCommand command,
                                    ArgumentParseResults results,
                                    RetrievableSet<ICommandParameter> parameters,
                                    RetrievableSet<IFlagParameter> flags,
                                    Deque<String> arguments)
            throws CommandException {

        while (!arguments.isEmpty()) {

            String paramName = arguments.removeFirst();

            if (!paramName.startsWith(COMMON_PREFIX))
                throw CommandException.invalidArgument(command, paramName);

            // check for flag
            if (paramName.startsWith(FLAG_PREFIX)) {

                paramName = paramName.substring(FLAG_PREFIX.length());

                IFlagParameter parameter = flags.removeRetrieve(new Flag(paramName, -1));
                if (parameter == null)
                    throw CommandException.invalidFlag(command, paramName);


                results.setFlag(paramName, true);
            }
            // check if floating parameter
            else if (paramName.startsWith(FLOATING_PREFIX)) {

                paramName = paramName.substring(FLOATING_PREFIX.length());

                ICommandParameter parameter = parameters.removeRetrieve(new Parameter(paramName, null));
                if (parameter == null)
                    throw CommandException.invalidParam(command, paramName);

                if (arguments.isEmpty())
                    throw CommandException.missingRequiredArg(command, parameter);

                String arg = parseArgValue(arguments.removeFirst(), arguments);
                Argument commandArgument = new Argument(parameter, arg);

                if (results.getArgMap().containsKey(paramName))
                    throw CommandException.duplicateArg(command, parameter);


                results.getFloatingArgs().add(commandArgument);
                results.getArgMap().put(paramName, commandArgument);
            }
        }

        if (!parameters.isEmpty()) {

            for (ICommandParameter param : parameters) {
                if (param.hasDefaultValue()) {
                    Argument commandArgument = new Argument(param, null);
                    results.getFloatingArgs().add(commandArgument);
                    results.getArgMap().put(param.getName(), commandArgument);
                } else {
                    throw CommandException.missingRequiredArg(command, param);
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
