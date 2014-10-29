/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.commands.exceptions;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * Thrown if the {@CommandSender} cannot run the command.
 */
public class InvalidCommandSenderException extends Exception {

    private static final long serialVersionUID = 1L;
    private CommandSenderType _senderType;
    private CommandSenderType _expectedType;
    private String _reason;

    /**
     * Check the command sender and throw exception if the 
     * sender is not the expected type.
     *
     * @param sender    The command sender.
     * @param expected  The expected sender type.
     *
     * @throws InvalidCommandSenderException
     */
    public static void check(CommandSender sender, CommandSenderType expected)
            throws InvalidCommandSenderException {

        check(sender, expected, null);
    }

    /**
     * Check the command sender and throw exception if the
     * sender is not the expected type.
     *
     * @param sender    The command sender.
     * @param expected  The expected sender type.
     * @param reason    The reason the command sender type is invalid.
     *
     * @throws InvalidCommandSenderException
     */
    public static void check(CommandSender sender, CommandSenderType expected, @Nullable String reason)
            throws InvalidCommandSenderException {

        switch (expected) {
            case CONSOLE:
                if (!(sender instanceof Player))
                    return;
                throw new InvalidCommandSenderException(CommandSenderType.PLAYER, CommandSenderType.CONSOLE, reason);

            case PLAYER:
                if (sender instanceof Player)
                    return;
                throw new InvalidCommandSenderException(CommandSenderType.CONSOLE, CommandSenderType.PLAYER, reason);
        }
    }

    /**
     * Defines command sender types
     */
    public enum CommandSenderType {
        CONSOLE ("Console"),
        PLAYER  ("Player");

        private final String _displayName;

        CommandSenderType(String displayName) {
            _displayName = displayName;
        }

        public String getDisplayName() {
            return _displayName;
        }
    }

    /**
     * Constructor.
     *
     * @param commandSenderType  The type of command sender
     * @param expectedType       The expected type of the command sender
     */
    public InvalidCommandSenderException(CommandSenderType commandSenderType,
                                         CommandSenderType expectedType) {
        this(commandSenderType, expectedType, null);
    }

    /**
     * Constructor.
     *
     * @param commandSenderType  The type of command sender
     * @param expectedType       The expected type of the command sender
     * @param reason             The reason the command sender cannot be used
     */
    public InvalidCommandSenderException(CommandSenderType commandSenderType,
                                         CommandSenderType expectedType, @Nullable String reason) {
        PreCon.notNull(commandSenderType);
        PreCon.notNull(commandSenderType);

        _senderType = commandSenderType;
        _expectedType = expectedType;
        _reason = reason;
    }

    /**
     * Get the type of the command sender.
     */
    public CommandSenderType getSenderType() {
        return _senderType;
    }

    /**
     * Get the expected command sender type.
     */
    public CommandSenderType getExpectedType() {
        return _expectedType;
    }

    /**
     * Get the reason the command sender cannot
     * be accepted.
     */
    @Nullable
    public String getReason() {
        return _reason;
    }

}
