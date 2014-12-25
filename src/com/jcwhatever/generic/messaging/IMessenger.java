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

package com.jcwhatever.generic.messaging;

import com.jcwhatever.generic.mixins.IPluginOwned;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

/**
 * Interface for a chat and console messenger.
 */
public interface IMessenger extends IPluginOwned {

    public enum LineWrapping {
        ENABLED,
        DISABLED
    }

    /**
     * Get the number of characters allowed in a line
     * for line wrapped messages.
     */
    int getLineWrapCount();

    /**
     * Set the number of characters allowed in a line
     * for line wrapped messages.
     *
     * @param characterCount  The number of characters.
     */
    void setLineWrapCount(int characterCount);

    /**
     * Get the default line wrapping mode.
     */
    LineWrapping getDefaultLineWrap();

    /**
     * Set the default line wrapping mode.
     *
     * @param lineWrapping  The line wrap mode.
     */
    void setDefaultLineWrap(LineWrapping lineWrapping);

    /**
     * Get the time in ticks that are delayed before the
     * same no-spam message can be displayed again.
     */
    int getDefaultNoSpamDelay();

    /**
     * Set the time in ticks that are delayed before the
     * same no-spam message can be displayed again.
     *
     * @param delay  The delay in ticks.
     */
    void setDefaultNoSpamDelay(int delay);

    /**
     * Tell a message to a {@code CommandSender} and cache it for the default spam delay.
     *
     * <p>The message will not be displayed again until the delay time has elapsed.</p>
     *
     * <p>If the message is displayed before the delay time has elapsed, the delay is reset.</p>
     *
     * @param sender        The sender to display the message to.
     * @param lineWrapping  Line wrapping option.
     * @param ticks         The number of ticks before the message can be displayed again.
     * @param message       The message to display.
     * @param params        Optional formatting parameters.
     *
     * @return  True if the message was displayed.
     */
    boolean tellNoSpam(CommandSender sender, Integer ticks, LineWrapping lineWrapping, Object message, Object...params);

    /**
     * Tell a message to a {@code CommandSender} and cache it for the default spam delay.
     *
     * <p>The message will not be displayed again until the delay time has elapsed.</p>
     *
     * <p>If the message is displayed before the delay time has elapsed, the delay is reset.</p>
     *
     * @param sender   The sender to display the message to.
     * @param ticks    The number of ticks before the message can be displayed again.
     * @param message  The message to display.
     * @param params   Optional formatting parameters.
     *
     * @return  True if the message was displayed.
     */
    boolean tellNoSpam(CommandSender sender, Integer ticks, Object message, Object...params);

    /**
     * Tell a message to a {@code CommandSender} and cache it the default spam delay.
     *
     * <p>The message will not be displayed again until the delay time has elapsed.</p>
     *
     * <p>If the message is displayed before the delay time has elapsed, the delay is reset.</p>
     *
     * @param sender   The sender to display the message to.
     * @param message  The message to display.
     * @param params   Optional formatting parameters.
     *
     * @return  True if the message was displayed.
     */
    boolean tellNoSpam(CommandSender sender, Object message, Object...params);

    /**
     * Tell a message to the specified {@code CommandSender}.
     *
     * @param sender   The sender to display the message to.
     * @param message  The message to display.
     * @param params   Optional formatting parameters.
     *
     * @return  True if the message was displayed.
     */
    boolean tell(CommandSender sender, Object message, Object...params);

    /**
     * Tell a message to the specified {@code CommandSender}.
     *
     * @param sender        The sender to display the message to.
     * @param lineWrapping  Line wrapping option.
     * @param message       The message to display.
     * @param params        Optional formatting parameters.
     *
     * @return  True if the message was displayed.
     */
    boolean tell(CommandSender sender, LineWrapping lineWrapping, Object message, Object...params);

    /**
     * Tell an important message to the specified player. If the player is not
     * online, the message is cached and displayed to the player at the next log in.
     *
     * @param player    The player.
     * @param context   The message context. alphanumerics only.
     * @param message   The message to display.
     * @param params    Optional formatting parameters.
     */
    void tellImportant(Player player, String context, Object message, Object...params);

    /**
     * Tell an important message to the specified player. If the player is not
     * online, the message is cached and displayed to the player at the next log in.
     *
     * @param playerId  The id of the player.
     * @param context   The message context. alphanumerics only.
     * @param message   The message to display.
     * @param params    Optional formatting parameters.
     */
    void tellImportant(UUID playerId, String context, Object message, Object...params);

    /**
     * Broadcast a message to all players on the server.
     *
     * @param message  The message to display.
     * @param params   Optional formatting parameters.
     */
    void broadcast(Object message, Object... params);

    /**
     * Broadcast a message to all players on the server, excluding
     * players from a specified collection of players.
     *
     * @param exclude  The players to exclude from the broadcast.
     * @param message  The message to display.
     * @param params   Optional formatting parameters.
     */
    void broadcast(Collection<Player> exclude, Object message, Object...params);

    /**
     * Broadcast a message to all players on the server.
     *
     * @param lineWrapping  The wrapping option.
     * @param message       The message to display.
     * @param params        Optional formatting parameters.
     */
    void broadcast(LineWrapping lineWrapping, Object message, Object... params);

    /**
     * Broadcast a message to all players on the server.
     *
     * @param exclude       The players to exclude from the broadcast.
     * @param lineWrapping  The wrapping option.
     * @param message       The message to display.
     * @param params        Optional formatting parameters.
     */
    void broadcast(Collection<Player> exclude, LineWrapping lineWrapping, Object message, Object... params);

    /**
     * Display an information message in the console.
     *
     * @param message  The message to display.
     * @param params   Optional formatting parameters.
     */
    void info(Object message, Object... params);

    /**
     * Display a debug message in the console.
     *
     * @param message  The message to display.
     * @param params   Optional formatting parameters.
     */
    void debug(Object message, Object... params);

    /**
     * Display a warning in the console.
     *
     * @param message  The message to display.
     * @param params   Optional formatting parameters.
     */
    void warning(Object message, Object... params);

    /**
     * Display a severe error in the console.
     *
     * @param message  The message to display.
     * @param params   Optional formatting parameters.
     */
    void severe(Object message, Object... params);
}
