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


package com.jcwhatever.bukkit.generic.messaging;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.GenericsPlugin;
import com.jcwhatever.bukkit.generic.collections.TimedHashSet;
import com.jcwhatever.bukkit.generic.utils.PlayerUtils;
import com.jcwhatever.bukkit.generic.player.collections.PlayerMap;
import com.jcwhatever.bukkit.generic.storage.DataStorage;
import com.jcwhatever.bukkit.generic.storage.DataStorage.DataPath;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.TextUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

/**
 * Provide chat and console message utilities.
 */
public class Messenger {

    private Messenger() {}

    private static final Logger _log = Logger.getLogger("Minecraft");

    private static final int _maxLineLen = 60;
    private static IDataNode _importantData;
    private static Pattern returnPattern = Pattern.compile("\r");
    private static Map<UUID, TimedHashSet<String>> _noSpamCache = new PlayerMap<TimedHashSet<String>>(GenericsLib.getLib());

    /**
     * Specify if line wrapping is enabled.
     *
     * <p>
     *     When line wrapping is enabled, lines are
     *     forcefully wrapped and the plugin prefix appended
     *     to the beginning of each new line.
     * </p>
     *
     */
    public enum LineWrapping {
        ENABLED,
        DISABLED
    }

    /**
     * Tell a message to an {@code CommandSender} and cache it for 140 ticks.
     *
     * <p>
     *     The message will not be displayed again until the 140 ticks have elapsed.
     * </p>
     * <p>
     *     If the message is displayed before the 140 ticks have elapsed, the time is reset.
     * </p>
     *
     * @param plugin   The plugin to get a prefix from.
     * @param sender   The sender to display the message to.
     * @param message  The message to display.
     * @param params   Optional formatting parameters.
     *
     * @return  True if the message was displayed.
     */
    public static boolean tellNoSpam(@Nullable Plugin plugin, CommandSender sender, Object message, Object...params) {

        return tellNoSpam(LineWrapping.ENABLED, plugin, sender, 140, message, params);
    }

    /**
     * Tell a message to an {@code CommandSender} and cache it for the specified number of ticks.
     *
     * <p>
     *     The message will not be displayed again until the specified ticks have elapsed.
     * </p>
     * <p>
     *     If the message is displayed before the specified ticks have elapsed, the time is reset.
     * </p>
     *
     * @param plugin   The plugin to get a prefix from.
     * @param sender   The sender to display the message to.
     * @param ticks    The number of ticks before the message can be displayed again.
     * @param message  The message to display.
     * @param params   Optional formatting parameters.
     *
     * @return  True if the message was displayed.
     */
    public static boolean tellNoSpam(@Nullable Plugin plugin, CommandSender sender, int ticks, Object message, Object...params) {

        return tellNoSpam(LineWrapping.ENABLED, plugin, sender, ticks, message, params);
    }

    /**
     * Tell a message to an {@code CommandSender} and cache it for the specified number of ticks.
     *
     * <p>
     *     The message will not be displayed again until the specified ticks have elapsed.
     * </p>
     * <p>
     *     If the message is displayed before the specified ticks have elapsed, the time is reset.
     * </p>
     *
     * @param lineWrapping  Line wrapping option.
     * @param plugin        The plugin to get a prefix from.
     * @param sender        The sender to display the message to.
     * @param ticks         The number of ticks before the message can be displayed again.
     * @param message       The message to display.
     * @param params        Optional formatting parameters.
     *
     * @return  True if the message was displayed.
     */
    public static boolean tellNoSpam(LineWrapping lineWrapping, @Nullable Plugin plugin, CommandSender sender, int ticks, Object message, Object...params) {
        PreCon.notNull(sender);
        PreCon.notNull(lineWrapping);
        PreCon.positiveNumber(ticks);
        PreCon.notNull(message);
        PreCon.notNull(params);

        if (!(sender instanceof Player)) {
            return tell(plugin, sender, message, params);
        }

        Player p = (Player)sender;

        String msg = TextUtils.format(message, params);

        TimedHashSet<String> recent = _noSpamCache.get(p.getUniqueId());
        if (recent == null) {
            recent = new TimedHashSet<String>(20, 140);
            _noSpamCache.put(p.getUniqueId(), recent);
        }

        if (recent.contains(msg, 140))
            return false;

        recent.add(msg, ticks);

        return tell(lineWrapping, plugin, p, msg);
    }

    /**
     * Tell a message to the specified {@code CommandSender}.
     *
     * @param lineWrapping  Line wrapping option.
     * @param plugin        The plugin to get a prefix from.
     * @param sender        The sender to display the message to.
     * @param message       The message to display.
     * @param params        Optional formatting parameters.
     *
     * @return  True if the message was displayed.
     */
    public static boolean tell(LineWrapping lineWrapping, @Nullable Plugin plugin,
                               CommandSender sender, Object message, Object...params) {
        PreCon.notNull(lineWrapping);
        PreCon.notNull(plugin);

        return tell(lineWrapping == LineWrapping.ENABLED, plugin, sender, TextUtils.format(message, params));
    }

    /**
     * Tell a message to the specified {@code CommandSender}.
     *
     * @param plugin   The plugin to get a prefix from.
     * @param sender   The sender to display the message to.
     * @param message  The message to display.
     * @param params   Optional formatting parameters.
     *
     * @return  True if the message was displayed.
     */
    public static boolean tell(@Nullable Plugin plugin, CommandSender sender, Object message, Object...params) {

        return tell(true, plugin, sender, TextUtils.format(message, params));
    }

    /**
     * Tell a message to the specified {@code CommandSender}.
     *
     * @param sender   The sender to display the message to.
     * @param message  The message to display.
     * @param params   Optional formatting parameters.
     *
     * @return  True if the message was displayed.
     */
    public static boolean tell(CommandSender sender, Object message, Object...params) {

        return tell(false, null, sender, TextUtils.format(message, params));
    }

    /**
     * Tell an important message to the specified player. If the player is not
     * online, the message is cached and displayed to the player at the next log in.
     *
     * @param plugin    The plugin to get a prefix from.
     * @param playerId  The id of the player.
     * @param context   The message context. alphanumerics only.
     * @param message   The message to display.
     * @param params    Optional formatting parameters.
     */
    public static void tellImportant(@Nullable Plugin plugin, UUID playerId, String context, Object message, Object...params) {
        PreCon.notNull(playerId);
        PreCon.notNullOrEmpty(context);
        PreCon.notNull(message);
        PreCon.notNull(params);

        if (!TextUtils.isValidName(context, 64))
            throw new IllegalArgumentException("illegal characters in context argument or argument is too long.");

        Player p = PlayerUtils.getPlayer(playerId);
        if (p != null && p.isOnline()) {
            tell(plugin, p, message, params);
            return;
        }

        IDataNode data = getImportantData();

        data.set(playerId.toString() + '.' + context + ".message", TextUtils.format(message, params));
        data.set(playerId.toString() + '.' + context + ".plugin", plugin != null ? plugin.getName() : null);
        data.saveAsync(null);
    }

    /**
     * Called to display stored important messages for the
     * specified player to the specified player.
     *
     * @param p  The player.
     */
    public static void tellImportant(Player p) {
        PreCon.notNull(p);

        IDataNode data = getImportantData();

        IDataNode playerData = data.getNode(p.getUniqueId().toString());

        Set<String> contexts = playerData.getSubNodeNames();
        if (contexts == null)
            return;

        boolean save = false;

        for (String context : contexts) {
            IDataNode contextData = playerData.getNode(context);

            String pluginName = contextData.getString("plugin");
            if (pluginName == null)
                continue;

            Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
            if (plugin == null)
                continue;

            String message = contextData.getString("message", "");

            if (message != null) {
                tell(plugin, p, message);
            }

            contextData.clear();
            save = true;
        }

        if (save)
            data.saveAsync(null);
    }

    /**
     * Broadcast a message to all players on the server.
     *
     * @param plugin   The plugin to get a prefix from.
     * @param message  The message to display.
     * @param params   Optional formatting parameters.
     */
    public static void broadcast(@Nullable Plugin plugin, Object message, Object... params) {
        PreCon.notNull(message);
        PreCon.notNull(params);

        String formatted = TextUtils.format(message, params);

        for (Player p : Bukkit.getOnlinePlayers()) {
            tell(plugin, p, formatted);
        }
    }

    /**
     * Broadcast a message to all players on the server, excluding
     * players from a specified collection of players.
     *
     * @param plugin   The plugin to get a prefix from.
     * @param message  The message to display.
     * @param exclude  The players to exclude from the broadcast.
     * @param params   Optional formatting parameters.
     */
    public static void broadcast(@Nullable Plugin plugin, Object message, Collection<Player> exclude, Object...params) {
        PreCon.notNull(message);
        PreCon.notNull(exclude);
        PreCon.notNull(params);

        String formatted = TextUtils.format(message, params);

        for (Player p : Bukkit.getOnlinePlayers()) {

            if (exclude.contains(p))
                continue;

            tell(plugin, p, formatted);
        }
    }

    /**
     * Display an information message in the console.
     *
     * @param plugin   The plugin to get a prefix from.
     * @param message  The message to display.
     * @param params   Optional formatting parameters.
     */
    public static void info(@Nullable Plugin plugin, Object message, Object... params) {
        PreCon.notNull(message);
        PreCon.notNull(params);

        _log.info(getConsolePrefix(plugin) + TextUtils.format(message, params));
    }

    /**
     * Display a debug message in the console.
     *
     * @param plugin   The plugin to get a prefix from.
     * @param message  The message to display.
     * @param params   Optional formatting parameters.
     */
    public static void debug(@Nullable Plugin plugin, Object message, Object... params) {
        PreCon.notNull(message);
        PreCon.notNull(params);

        if (plugin instanceof GenericsPlugin && !((GenericsPlugin) plugin).isDebugging())
            return;

        ConsoleCommandSender e = Bukkit.getConsoleSender();

        if (e != null) {
            tell(false, plugin, e, ChatColor.GOLD + "[debug] " + TextUtils.format(message, params));
        }
        else {
            info(plugin, "[debug] " + TextUtils.format(message, params));
        }
    }

    /**
     * Display a warning in the console.
     *
     * @param plugin   The plugin to get a prefix from.
     * @param message  The message to display.
     * @param params   Optional formatting parameters.
     */
    public static void warning(@Nullable Plugin plugin, Object message, Object... params) {
        PreCon.notNull(message);
        PreCon.notNull(params);

        _log.warning(getConsolePrefix(plugin) + TextUtils.format(message, params));
    }

    /**
     * Display a severe error in the console.
     *
     * @param plugin   The plugin to get a prefix from.
     * @param message  The message to display.
     * @param params   Optional formatting parameters.
     */
    public static void severe(@Nullable Plugin plugin, Object message, Object... params) {
        PreCon.notNull(message);
        PreCon.notNull(params);

        _log.severe(getConsolePrefix(plugin) + TextUtils.format(message, params));
    }

    /*
     *  Get the chat prefix of a plugin.
     */
    private static String getChatPrefix(@Nullable Plugin plugin) {
        if (plugin instanceof GenericsPlugin) {
            return ((GenericsPlugin) plugin).getChatPrefix();
        }

        if (plugin != null) {
            return '[' + plugin.getName() + ']';
        }

        return "";
    }

    /*
     * Get the console prefix of a plugin.
     */
    private static String getConsolePrefix(@Nullable Plugin plugin) {
        if (plugin instanceof GenericsPlugin) {
            return ((GenericsPlugin) plugin).getConsolePrefix();
        }

        if (plugin != null) {
            return '[' + plugin.getName() + ']';
        }

        return "";

    }

    /*
     * Get the data node where important messages are stored.
     */
    private static IDataNode getImportantData() {
        if (_importantData == null) {

            _importantData = DataStorage.getStorage(GenericsLib.getLib(), new DataPath("important-messages"));
            _importantData.loadAsync();
        }

        return _importantData;
    }


    /*
     * Tell a message to the specified {@code CommandSender}
     */
    private static boolean tell(boolean cutLines, @Nullable Plugin plugin, CommandSender sender, String message) {
        PreCon.notNull(sender);
        PreCon.notNull(message);

        String chatPrefix = getChatPrefix(plugin);

        cutLines = cutLines && plugin != null;

        // if lines don't need to be cut, simply send the raw message
        if (!cutLines) {
            sender.sendMessage(chatPrefix + message);
            return true;
        }

        String[] lines = returnPattern.split(message);

        for (String line : lines) {

            line = chatPrefix + line;
            String testLine = ChatColor.stripColor(line);

            if (testLine.length() > _maxLineLen) {
                List<String> moreLines = TextUtils.paginateString(line, chatPrefix, _maxLineLen, true);
                for (String mLine : moreLines) {
                    sender.sendMessage(mLine);
                }
            } else {
                sender.sendMessage(line);
            }
        }
        return true;
    }
}

