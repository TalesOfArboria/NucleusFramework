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

package com.jcwhatever.nucleus.commands.exceptions;

import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.UsageGenerator;
import com.jcwhatever.nucleus.commands.exceptions.InvalidCommandSenderException.CommandSenderType;
import com.jcwhatever.nucleus.commands.parameters.CommandParameter;
import com.jcwhatever.nucleus.commands.parameters.ParameterDescription;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Abstract implementation of an exception that is caught
 * when thrown while executing a command in order to display
 * an error message to the command sender.
 */
public abstract class CommandException extends Exception {

    @Localizable
    static final String _TOO_MANY_ARGS =
            "{RED}Too many arguments. Type '{0: usage}' for help.";

    @Localizable static final String _INVALID_COMMAND_SENDER =
            "{RED}Cannot execute command as {0: command sender type}.";

    @Localizable static final String _DUPLICATE_ARGUMENT =
            "{RED}Duplicate argument detected for parameter named '{0: parameter name}'.";

    @Localizable static final String _MISSING_REQUIRED_ARGUMENT =
            "{RED}Parameter '{0: parameter name}' is required. Type '{1: usage}' for help.";

    @Localizable static final String _INVALID_PARAMETER =
            "{RED}'{0: parameter name}' is not a valid parameter. Type '{1: usage} for help.";

    @Localizable static final String _INVALID_FLAG =
            "{RED}'{0: flag name}' is not a valid flag. Type '{1: usage}' for help.";

    @Localizable static final String _INVALID_ARGUMENT =
            "{RED}Invalid argument for parameter '{0: parameter name}'. " +
                    "Type '{1: usage}' for help.\n{WHITE}Parameter description: {GRAY}{2: description}";

    @Localizable static final String _INVALID_ARGUMENT_NO_DESCRIPTION =
            "{RED}Invalid argument for parameter '{0: parameter name}'. Type '{1: usage} for help.";

    /**
     * Throw an invalid argument exception.
     *
     * @param command               The command throwing the exception.
     * @param parameterDescription  The description of the parameter with an invalid argument.
     *
     * @throws InvalidArgumentException
     */
    public static void invalidArgument(
            AbstractCommand command, ParameterDescription parameterDescription)
            throws InvalidArgumentException {

        throw new InvalidArgumentException(
                NucLang.get(command.getPlugin(), _INVALID_ARGUMENT,
                        parameterDescription.getName(), getInlineUsage(command),
                        parameterDescription.getDescription())
        );
    }

    /**
     * Throw an invalid argument exception without a description.
     *
     * @param command        The command throwing the exception.
     * @param parameterName  The name of the parameter with an invalid argument.
     *
     * @throws InvalidArgumentException
     */
    public static void invalidArgument(
            AbstractCommand command, String parameterName)
            throws InvalidArgumentException {

        throw new InvalidArgumentException(
                NucLang.get(command.getPlugin(), _INVALID_ARGUMENT_NO_DESCRIPTION,
                        parameterName, getInlineUsage(command))
        );
    }

    /**
     * Throw an exception due to too many arguments provided.
     *
     * @param command  The command throwing the exception.
     *
     * @throws TooManyArgsException
     */
    public static void tooManyArgs(AbstractCommand command) throws TooManyArgsException {
        PreCon.notNull(command);

        throw new TooManyArgsException(
                NucLang.get(command.getPlugin(), _TOO_MANY_ARGS, getInlineUsage(command))
        );
    }

    /**
     * Throw an exception due to a required argument is missing.
     *
     * @param command    The command throwing the exception.
     * @param parameter  The command parameter with the missing argument.
     *
     * @throws MissingArgumentException
     */
    public static void missingRequiredArg(
            AbstractCommand command, CommandParameter parameter)
            throws MissingArgumentException {
        PreCon.notNull(command);

        throw new MissingArgumentException(
                NucLang.get(command.getPlugin(), _MISSING_REQUIRED_ARGUMENT,
                        parameter.getName(), getInlineUsage(command))
        );
    }

    /**
     * Throw an exception fue to an argument is provided twice for the same
     * parameter.
     *
     * @param command    The command throwing the exception.
     * @param parameter  The command parameter with the duplicate arguments.
     *
     * @throws DuplicateArgumentException
     */
    public static void duplicateArg(AbstractCommand command, CommandParameter parameter)
            throws DuplicateArgumentException {
        PreCon.notNull(command);
        PreCon.notNull(parameter);

        throw new DuplicateArgumentException(
                NucLang.get(command.getPlugin(), _DUPLICATE_ARGUMENT,
                        parameter.getName(), getInlineUsage(command))
        );
    }

    /**
     * Throw an exception due to a floating parameter has an invalid parameter name.
     *
     * @param command        The command throwing the exception.
     * @param parameterName  The invalid parameter name.
     *
     * @throws InvalidParameterException
     */
    public static void invalidParam(AbstractCommand command, String parameterName)
            throws InvalidParameterException {
        PreCon.notNull(command);
        PreCon.notNull(parameterName);

        throw new InvalidParameterException(
                NucLang.get(command.getPlugin(), _INVALID_PARAMETER,
                        parameterName, getInlineUsage(command))
        );
    }

    /**
     * Throw an exception due to a flag name is invalid.
     *
     * @param command   The command throwing the exception.
     * @param flagName  The invalid flag name.
     *
     * @throws InvalidParameterException
     */
    public static void invalidFlag(AbstractCommand command, String flagName)
            throws InvalidParameterException {
        PreCon.notNull(command);
        PreCon.notNull(flagName);

        throw new InvalidParameterException(
                NucLang.get(command.getPlugin(), _INVALID_FLAG, flagName, getInlineUsage(command))
        );
    }

    /**
     * Throws an exception if the provided {@link org.bukkit.command.CommandSender}
     * does not match the expected type.
     *
     * @param command   The command the command sender check is for.
     * @param sender    The command sender to check.
     * @param expected  The expected command sender type.
     *
     * @throws InvalidCommandSenderException
     */
    public static void checkCommandSender(
            AbstractCommand command, CommandSender sender, CommandSenderType expected)
            throws InvalidCommandSenderException {
        PreCon.notNull(command);
        PreCon.notNull(sender);
        PreCon.notNull(expected);

        switch (expected) {
            case CONSOLE:
                if (!(sender instanceof Player))
                    return;
                throw new InvalidCommandSenderException(
                        NucLang.get(command.getPlugin(), _INVALID_COMMAND_SENDER, "Console")
                );

            case PLAYER:
                if (sender instanceof Player)
                    return;
                throw new InvalidCommandSenderException(
                        NucLang.get(command.getPlugin(), _INVALID_COMMAND_SENDER, "Player")
                );
        }
    }

    /**
     * Throw an exception if the provided {@link org.bukkit.command.CommandSender}
     * is the console.
     *
     * @param command  The command the command sender check is for.
     * @param sender   The command sender to check.
     *
     * @throws InvalidCommandSenderException
     */
    public static void checkNotConsole(AbstractCommand command, CommandSender sender)
            throws InvalidCommandSenderException {
        PreCon.notNull(command);
        PreCon.notNull(sender);

        if (sender instanceof Player)
            return;

        throw new InvalidCommandSenderException(
                NucLang.get(command.getPlugin(), _INVALID_COMMAND_SENDER, "Player")
        );
    }

    // get inline command help usage
    private static String getInlineUsage(AbstractCommand command) {
        UsageGenerator usageGenerator = new UsageGenerator();
        return usageGenerator.generate(command, command.getInfo().getRootSessionName(),
                UsageGenerator.INLINE_HELP);
    }


    private String _message;

    /**
     * Constructor.
     *
     * @param message  Exception message.
     */
    public CommandException(String message) {
        _message = message;
    }

    @Override
    public String getMessage() {
        return _message;
    }
}
