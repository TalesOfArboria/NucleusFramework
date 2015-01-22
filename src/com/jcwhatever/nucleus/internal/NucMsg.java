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

package com.jcwhatever.nucleus.internal;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.messaging.IMessenger;
import com.jcwhatever.nucleus.messaging.IMessenger.LineWrapping;
import com.jcwhatever.nucleus.messaging.MessengerFactory;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.UUID;

/* 
 * NucleusFramework's internal messenger.
 */
public final class NucMsg {

    private NucMsg() {}

    public static int getLineWrapCount() {
        return msg().getLineWrapCount();
    }

    public static void setLineWrapCount(int characterCount) {
        msg().setLineWrapCount(characterCount);
    }

    public static LineWrapping getDefaultLineWrap() {
        return msg().getDefaultLineWrap();
    }

    public static void setDefaultLineWrap(LineWrapping lineWrapping) {
        msg().setDefaultLineWrap(lineWrapping);
    }

    public static int getDefaultNoSpamDelay() {
        return msg().getDefaultNoSpamDelay();
    }

    public static void setDefaultNoSpamDelay(int delay) {
        msg().setDefaultNoSpamDelay(delay);
    }

    public static boolean tellNoSpam(CommandSender sender, Object message, Object... params) {
        return msg().tellNoSpam(sender, message, params);
    }

    public static boolean tellNoSpam(Plugin plugin, CommandSender sender, Object message, Object... params) {
        return msg(plugin).tellNoSpam(sender, message, params);
    }

    public static boolean tellNoSpam(CommandSender sender, Integer ticks, Object message, Object... params) {
        return msg().tellNoSpam(sender, ticks, message, params);
    }

    public static boolean tellNoSpam(CommandSender sender, Integer ticks, LineWrapping lineWrapping, Object message, Object... params) {
        return msg().tellNoSpam(sender, ticks, lineWrapping, message, params);
    }

    public static boolean tell(CommandSender sender, Object message, Object... params) {
        return msg().tell(sender, message, params);
    }

    public static boolean tell(Plugin plugin, CommandSender sender, Object message, Object... params) {
        return msg(plugin).tell(sender, message, params);
    }

    public static boolean tell(CommandSender sender, LineWrapping lineWrapping, Object message, Object... params) {
        return msg().tell(sender, lineWrapping, message, params);
    }

    public static boolean tell(Plugin plugin, CommandSender sender, LineWrapping lineWrapping,
                               Object message, Object... params) {
        return msg(plugin).tell(sender, lineWrapping, message, params);
    }

    public static boolean tellAnon(CommandSender sender, Object message, Object... params) {
        return anonMsg().tell(sender, message, params);
    }

    public static void tellImportant(Player player, String context, Object message, Object... params) {
        msg().tellImportant(player, context, message, params);
    }

    public static void tellImportant(UUID playerId, String context, Object message, Object... params) {
        msg().tellImportant(playerId, context, message, params);
    }

    public static void broadcast(Object message, Object... params) {
        msg().broadcast(message, params);
    }

    public static void broadcast(Collection<Player> exclude, Object message, Object... params) {
        msg().broadcast(exclude, message, params);
    }

    public static void info(Object message, Object... params) {
        msg().info(message, params);
    }

    public static void debug(Object message, Object... params) {
        msg().debug(message, params);
    }

    public static void debug(Plugin plugin, Object message, Object... params) {
        msg(plugin).debug(message, params);
    }

    public static void warning(Object message, Object... params) {
        msg().warning(message, params);
    }

    public static void warning(Plugin plugin, Object message, Object... params) {
        msg(plugin).warning(message, params);
    }

    public static void severe(Object message, Object... params) {
        msg().severe(message, params);
    }

    public static void severe(Plugin plugin, Object message, Object... params) {
        msg(plugin).severe(message, params);
    }

    private static IMessenger msg() {
        return Nucleus.getPlugin().getMessenger();
    }

    private static IMessenger msg(Plugin plugin) {
        return MessengerFactory.get(plugin);
    }

    private static IMessenger anonMsg() {
        return Nucleus.getPlugin().getAnonMessenger();
    }
}
