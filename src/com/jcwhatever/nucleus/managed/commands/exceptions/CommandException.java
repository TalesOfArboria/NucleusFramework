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

package com.jcwhatever.nucleus.managed.commands.exceptions;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.commands.ICommand;
import com.jcwhatever.nucleus.managed.commands.IRegisteredCommand;
import com.jcwhatever.nucleus.managed.commands.exceptions.InvalidCommandSenderException.CommandSenderType;
import com.jcwhatever.nucleus.managed.commands.parameters.ICommandParameter;
import com.jcwhatever.nucleus.managed.commands.parameters.IParameterDescription;
import com.jcwhatever.nucleus.managed.commands.utils.ICommandUsageGenerator;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * An exception that is caught when thrown while executing a command in order
 * to display an error message to the command sender.
 */
public class CommandException extends Exception {

    @Localizable static final String _TOO_MANY_ARGS =
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
     * Get an {@link InvalidArgumentException} to throw.
     *
     * @param command               The command throwing the exception.
     * @param parameterDescription  The description of the parameter with an invalid argument.
     */
    public static InvalidArgumentException invalidArgument(
            IRegisteredCommand command, IParameterDescription parameterDescription) {

        PreCon.notNull(command);
        PreCon.notNull(parameterDescription);

        return new InvalidArgumentException(
                NucLang.get(command.getPlugin(), _INVALID_ARGUMENT,
                        parameterDescription.getName(), getInlineUsage(command),
                        parameterDescription.getDescription())
        );
    }

    /**
     * Get a {@link InvalidArgumentException} exception without a parameter description.
     *
     * @param command        The command throwing the exception.
     * @param parameterName  The name of the parameter with an invalid argument.
     */
    public static InvalidArgumentException invalidArgument(
            IRegisteredCommand command, String parameterName) {

        PreCon.notNull(command);
        PreCon.notNull(parameterName);

        return new InvalidArgumentException(
                NucLang.get(command.getPlugin(), _INVALID_ARGUMENT_NO_DESCRIPTION,
                        parameterName, getInlineUsage(command))
        );
    }

    /**
     * Get a {@link TooManyArgsException} exception to throw due to too many
     * arguments provided.
     *
     * @param command  The command throwing the exception.
     */
    public static TooManyArgsException tooManyArgs(IRegisteredCommand command) {
        PreCon.notNull(command);

        return new TooManyArgsException(
                NucLang.get(command.getPlugin(), _TOO_MANY_ARGS, getInlineUsage(command))
        );
    }

    /**
     * Get a {@link MissingArgumentException} exception to throw due to a required
     * argument is missing.
     *
     * @param command    The command throwing the exception.
     * @param parameter  The command parameter with the missing argument.
     */
    public static MissingArgumentException missingRequiredArg(
            IRegisteredCommand command, ICommandParameter parameter) {

        PreCon.notNull(command);
        PreCon.notNull(parameter);

        return new MissingArgumentException(
                NucLang.get(command.getPlugin(), _MISSING_REQUIRED_ARGUMENT,
                        parameter.getName(), getInlineUsage(command))
        );
    }

    /**
     * Get a {@link DuplicateArgumentException} exception to throw due to an argument
     * that is provided twice for the same parameter.
     *
     * @param command    The command throwing the exception.
     * @param parameter  The command parameter with the duplicate arguments.
     */
    public static DuplicateArgumentException duplicateArg(
            IRegisteredCommand command, ICommandParameter parameter) {

        PreCon.notNull(command);
        PreCon.notNull(parameter);

        return new DuplicateArgumentException(
                NucLang.get(command.getPlugin(), _DUPLICATE_ARGUMENT,
                        parameter.getName(), getInlineUsage(command))
        );
    }

    /**
     * Get an {@link InvalidParameterException} exception to throw due to a floating
     * parameter that has an invalid parameter name.
     *
     * @param command        The command throwing the exception.
     * @param parameterName  The invalid parameter name.
     */
    public static InvalidParameterException invalidParam(
            IRegisteredCommand command, String parameterName) {

        PreCon.notNull(command);
        PreCon.notNull(parameterName);

        return new InvalidParameterException(
                NucLang.get(command.getPlugin(), _INVALID_PARAMETER,
                        parameterName, getInlineUsage(command))
        );
    }

    /**
     * Get an {@link InvalidParameterException} exception to throw due to a flag
     * whose name is invalid.
     *
     * @param command   The command throwing the exception.
     * @param flagName  The invalid flag name.
     */
    public static InvalidParameterException invalidFlag(IRegisteredCommand command, String flagName) {
        PreCon.notNull(command);
        PreCon.notNull(flagName);

        return new InvalidParameterException(
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
            IRegisteredCommand command, CommandSender sender, CommandSenderType expected)
            throws InvalidCommandSenderException {
        PreCon.notNull(command);
        PreCon.notNull(sender);
        PreCon.notNull(expected);

        switch (expected) {
            case CONSOLE:
                if (!(sender instanceof Player))
                    return;
                throw new InvalidCommandSenderException(
                        NucLang.get(command.getPlugin(), _INVALID_COMMAND_SENDER, "Player")
                );

            case PLAYER:
                if (sender instanceof Player)
                    return;
                throw new InvalidCommandSenderException(
                        NucLang.get(command.getPlugin(), _INVALID_COMMAND_SENDER, "Console")
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
    public static void checkNotConsole(IRegisteredCommand command, CommandSender sender)
            throws InvalidCommandSenderException {
        checkNotConsole(command.getPlugin(), command.getCommand(), sender);
    }

    /**
     * Throw an exception if the provided {@link org.bukkit.command.CommandSender}
     * is the console.
     *
     * @param plugin   The commands owning plugin.
     * @param command  The command the command sender check is for.
     * @param sender   The command sender to check.
     *
     * @throws InvalidCommandSenderException
     */
    public static void checkNotConsole(Plugin plugin, ICommand command, CommandSender sender)
            throws InvalidCommandSenderException {
        PreCon.notNull(command);
        PreCon.notNull(sender);

        if (sender instanceof Player)
            return;

        throw new InvalidCommandSenderException(
                NucLang.get(plugin, _INVALID_COMMAND_SENDER, "Player")
        );
    }

    // get inline command help usage
    private static String getInlineUsage(IRegisteredCommand command) {
        ICommandUsageGenerator usageGenerator = Nucleus.getCommandManager().getUsageGenerator();
        return usageGenerator.generate(command, ICommandUsageGenerator.INLINE_HELP);
    }

    private String _message;

    /**
     * Constructor.
     *
     * @param message  Exception message.
     * @param args     Optional format arguments.
     */
    public CommandException(String message, Object... args) {
        _message = TextUtils.format(message, args);
    }

    @Override
    public String getMessage() {
        return _message;
    }
}
