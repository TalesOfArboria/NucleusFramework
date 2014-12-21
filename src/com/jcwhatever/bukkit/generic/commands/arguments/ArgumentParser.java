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

package com.jcwhatever.bukkit.generic.commands.arguments;

import com.jcwhatever.bukkit.generic.collections.RetrievableSet;
import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.exceptions.DuplicateParameterException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidParameterException;
import com.jcwhatever.bukkit.generic.commands.exceptions.MissingArgumentException;
import com.jcwhatever.bukkit.generic.commands.exceptions.TooManyArgsException;
import com.jcwhatever.bukkit.generic.commands.parameters.CommandParameter;
import com.jcwhatever.bukkit.generic.commands.parameters.FlagParameter;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.utils.ArrayUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import java.util.LinkedList;

/**
 * Parses command arguments.
 */
public class ArgumentParser {

    @Localizable static final String _DUPLICATE_ARGUMENT =
            "Duplicate argument detected for parameter named '{0: parameter name}'.";

    @Localizable static final String _INVALID_PARAMETER =
            "'{0: parameter name}' is not a valid parameter.";

    @Localizable static final String _INVALID_FLAG =
            "'{0: flag name}' is not a valid flag.";

    @Localizable static final String _MISSING_FLOATING_ARGUMENT =
            "Parameter '{0: parameter name}' is missing an argument.";

    @Localizable static final String _MISSING_REQUIRED_ARGUMENT =
            "Parameter '{0: parameter name}' is required.";

    /**
     * Parse command arguments
     *
     * @param command  The command the arguments are for.
     * @param args     The arguments.
     */
    public ArgumentParseResults parse(AbstractCommand command, String[] args)
            throws InvalidArgumentException, DuplicateParameterException,
            InvalidParameterException, TooManyArgsException, MissingArgumentException {

        PreCon.notNull(command);
        PreCon.notNull(args);

        ArgumentParseResults results = new ArgumentParseResults(command);

        LinkedList<String> arguments = ArrayUtils.asLinkedList(args);
        LinkedList<CommandParameter> staticParameters = new LinkedList<>(
                command.getInfo().getStaticParams());

        // parse arguments for static parameters.
        parseStaticArgs(results, staticParameters, arguments);

        // check if there are floating parameters for the command.
        if (command.getInfo().getRawFloatingParams().length == 0) {

            // if there are no more parameters but there are still
            // arguments left, then there are too many arguments.
            if (!arguments.isEmpty()) {
                throw new TooManyArgsException();
            }

            // nothing left to do
            return results;
        }

        // get ready to parse floating and flag parameters.
        RetrievableSet<CommandParameter> floating = new RetrievableSet<>(command.getInfo().getFloatingParams());
        RetrievableSet<FlagParameter> flags = new RetrievableSet<>(command.getInfo().getFlagParams());

        // parse arguments for non static parameters.
        parseNonStaticArgs(results, floating, flags, arguments);

        return results;

    }

    // parse arguments for static parameters
    private void parseStaticArgs(ArgumentParseResults results,
                                 LinkedList<CommandParameter> parameterList,
                                 LinkedList<String> arguments)
            throws InvalidArgumentException, DuplicateParameterException, MissingArgumentException {

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
                        throw new InvalidArgumentException(parameter.getName());
                    }

                    // No default value defined means a discreet value is expected.
                    else if (!parameter.hasDefaultValue()) {
                        throw new MissingArgumentException(
                                name, Lang.get(_MISSING_REQUIRED_ARGUMENT, parameter.getName()));
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
                    throw new DuplicateParameterException(
                            name, Lang.get(_DUPLICATE_ARGUMENT, name));
                }

                results.getStaticArgs().add(commandArgument);
                results.getArgMap().put(name, commandArgument);
            }
        }
    }

    // parse arguments for floating parameters and flags
    private void parseNonStaticArgs(ArgumentParseResults results,
                                    RetrievableSet<CommandParameter> parameters,
                                    RetrievableSet<FlagParameter> flags,
                                    LinkedList<String> arguments)
            throws InvalidArgumentException, InvalidParameterException,
            MissingArgumentException, DuplicateParameterException {

        while (!arguments.isEmpty()) {

            String paramName = arguments.removeFirst();

            if (!paramName.startsWith("-"))
                throw new InvalidArgumentException(paramName);

            // check if floating parameter
            if (paramName.startsWith("--")) {

                paramName = paramName.substring(2);

                CommandParameter parameter = parameters.removeRetrieve(new CommandParameter(paramName, null));
                if (parameter == null) {
                    throw new InvalidParameterException(
                            paramName, Lang.get(_INVALID_PARAMETER, paramName));
                }

                if (arguments.isEmpty()) {
                    throw new MissingArgumentException(
                            paramName, Lang.get(_MISSING_FLOATING_ARGUMENT, paramName));
                }

                String arg = arguments.removeFirst();
                CommandArgument commandArgument = new CommandArgument(parameter, arg);

                if (results.getArgMap().containsKey(paramName)) {
                    throw new DuplicateParameterException(
                            paramName, Lang.get(_DUPLICATE_ARGUMENT, paramName));
                }

                results.getFloatingArgs().add(commandArgument);
                results.getArgMap().put(paramName, commandArgument);
            }
            // the parameter is a flag
            else {

                paramName = paramName.substring(1);

                FlagParameter parameter = flags.removeRetrieve(new FlagParameter(paramName, -1));
                if (parameter == null) {
                    throw new InvalidParameterException(
                            paramName, Lang.get(_INVALID_FLAG, paramName));
                }

                results.setFlag(paramName, true);
            }
        }

        if (!parameters.isEmpty()) {

            for (CommandParameter param : parameters) {
                if (!param.hasDefaultValue()) {
                    throw new MissingArgumentException(
                            param.getName(), Lang.get(_MISSING_REQUIRED_ARGUMENT, param.getName()));
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
    private String parseArgValue(String currentArg, LinkedList<String> argsQueue) {

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
