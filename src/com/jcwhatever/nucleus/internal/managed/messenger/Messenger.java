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


package com.jcwhatever.nucleus.internal.managed.messenger;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.NucleusPlugin;
import com.jcwhatever.nucleus.collections.players.PlayerMap;
import com.jcwhatever.nucleus.collections.timed.TimedHashSet;
import com.jcwhatever.nucleus.managed.messaging.IMessenger;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.TimeScale;
import com.jcwhatever.nucleus.utils.nms.NmsUtils;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.nucleus.utils.text.components.SimpleChatMessage;
import com.jcwhatever.nucleus.utils.text.components.IChatMessage;
import com.jcwhatever.nucleus.utils.text.format.TextFormatter;
import com.jcwhatever.nucleus.utils.text.format.TextFormatterResult;
import com.jcwhatever.nucleus.utils.text.format.TextFormatterSettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Provide chat and console message utilities.
 */

class Messenger implements IMessenger {

    private static Map<UUID, TimedHashSet<String>> _noSpamCache =
            new PlayerMap<TimedHashSet<String>>(Nucleus.getPlugin());

    private TextFormatterSettings TEXT_SETTING = new TextFormatterSettings();
    private TextFormatter TEXT_FORMATTER = new TextFormatter(TEXT_SETTING);

    private final Plugin _plugin;
    private final String _consolePrefix;
    private final IDataNode _importantData;
    private final Logger _logger;

    private int _maxLineLen = 60;
    private int _spamDelay = 140;
    private LineWrapping _lineWrap = LineWrapping.ENABLED;
    private final Object _prefixSource;
    private IChatMessage _chatPrefix;

    protected Messenger(InternalMessengerFactory factory, Plugin plugin, @Nullable Object prefixSource) {
        _plugin = plugin;
        _prefixSource = prefixSource;
        _consolePrefix = InternalMessengerFactory.getConsolePrefix(prefixSource);
        _logger = InternalMessengerFactory.LOGGER;
        _importantData = factory.getImportantData(plugin);
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public int getLineWrapCount() {
        return _maxLineLen;
    }

    @Override
    public void setLineWrapCount(int characterCount) {
        _maxLineLen = characterCount;
    }

    @Override
    public LineWrapping getDefaultLineWrap() {
        return _lineWrap;
    }

    @Override
    public void setDefaultLineWrap(LineWrapping lineWrapping) {
        _lineWrap = lineWrapping;
    }

    @Override
    public int getDefaultNoSpamDelay() {
        return _spamDelay;
    }

    @Override
    public void setDefaultNoSpamDelay(int delay) {
        _spamDelay = delay;
    }

    @Override
    public boolean tellNoSpam(CommandSender sender, Object message, Object... params) {
        return tellNoSpam(sender, _spamDelay, _lineWrap, message, params);
    }

    @Override
    public boolean tellNoSpam(CommandSender sender, Integer ticks, Object message, Object... params) {
        return tellNoSpam(sender, ticks, _lineWrap, message, params);
    }

    @Override
    public boolean tellNoSpam(CommandSender sender, Integer ticks, LineWrapping lineWrapping,
                              Object message, Object... params) {
        PreCon.notNull(sender);
        PreCon.positiveNumber(ticks);
        PreCon.notNull(lineWrapping);
        PreCon.notNull(message);
        PreCon.notNull(params);

        if (!(sender instanceof Player)) {
            return tell(sender, message, params);
        }

        Player player = (Player)sender;

        String msg = TextUtils.format(message, params);

        TimedHashSet<String> recent = _noSpamCache.get(player.getUniqueId());
        if (recent == null) {
            recent = new TimedHashSet<String>(_plugin, 20, 140);
            _noSpamCache.put(player.getUniqueId(), recent);
        }

        if (recent.contains(msg, ticks, TimeScale.TICKS))
            return false;

        recent.add(msg, ticks, TimeScale.TICKS);

        return tell(player, lineWrapping, msg);
    }

    @Override
    public boolean tell(CommandSender sender, Object message, Object... params) {
        return tell(sender, _lineWrap, message, params);
    }

    @Override
    public boolean tell(CommandSender sender, LineWrapping lineWrapping, Object messageObject, Object... params) {
        PreCon.notNull(sender);
        PreCon.notNull(lineWrapping);
        PreCon.notNull(messageObject);
        PreCon.notNull(params);

        loadChatPrefix();

        boolean cutLines = lineWrapping == LineWrapping.ENABLED && sender instanceof Player;

        TEXT_SETTING.setMaxLineLen(cutLines ? _maxLineLen : -1);

        TextFormatterResult result = TEXT_FORMATTER.format(TEXT_SETTING, messageObject.toString(), params);

        sendMessage(sender, result);

        /*
        // if lines don't need to be cut, simply send the raw message
        if (!cutLines) {
            sender.sendMessage(_chatPrefix + message);
            return true;
        }

        String[] lines = TextUtils.PATTERN_NEW_LINE.split(message);

        for (String line : lines) {

            line = _chatPrefix + line;
            String testLine = ChatColor.stripColor(line);

            if (testLine.length() > _maxLineLen) {
                List<String> moreLines = TextUtils.paginateString(line, _chatPrefix, _maxLineLen, true);
                for (String mLine : moreLines) {
                    sender.sendMessage(mLine);
                }
            } else {
                sender.sendMessage(line);
            }
        }
        */
        return true;
    }

    private void sendMessage(CommandSender sender, TextFormatterResult result) {
        if (sender instanceof Player) {
            NmsUtils.getChatHandler().send((Player)sender, result);
        }
        else {
            sender.sendMessage(result.toString());
        }
    }

    @Override
    public void tellImportant(Player player, String context, Object message, Object... params) {
        tellImportant(player.getUniqueId(), context, message, params);
    }

    @Override
    public void tellImportant(UUID playerId, String context, Object message, Object... params) {
        PreCon.notNull(playerId);
        PreCon.notNullOrEmpty(context);
        PreCon.notNull(message);
        PreCon.notNull(params);

        if (!TextUtils.isValidName(context, 64))
            throw new IllegalArgumentException("Illegal characters in context argument or argument is too long.");

        Player p = PlayerUtils.getPlayer(playerId);
        if (p != null && p.isOnline()) {
            tell(p, message, params);
            return;
        }

        IDataNode data = _importantData;

        data.set(playerId.toString() + '.' + context + ".message", TextUtils.format(message, params));
        data.set(playerId.toString() + '.' + context + ".prefix", loadChatPrefix().toString());
        data.set(playerId.toString() + '.' + context + ".lineLen", _maxLineLen);
        data.save();
    }

    @Override
    public void broadcast(Object message, Object... params) {
        broadcast(_lineWrap, message, params);
    }

    @Override
    public void broadcast(Collection<? extends Player> exclude, Object message, Object... params) {
        broadcast(exclude, _lineWrap, message, params);
    }

    @Override
    public void broadcast(LineWrapping lineWrapping, Object message, Object... params) {
        PreCon.notNull(message);
        PreCon.notNull(params);

        String formatted = TextUtils.format(message, params);

        for (Player p : Bukkit.getOnlinePlayers()) {
            tell(p, lineWrapping, formatted);
        }
    }

    @Override
    public void broadcast(Collection<? extends Player> exclude,
                          LineWrapping lineWrapping, Object message, Object... params) {
        PreCon.notNull(lineWrapping);
        PreCon.notNull(exclude);
        PreCon.notNull(message);
        PreCon.notNull(params);

        String formatted = TextUtils.format(message, params);

        for (Player p : Bukkit.getOnlinePlayers()) {

            if (exclude.contains(p))
                continue;

            tell(p, lineWrapping, formatted);
        }
    }

    @Override
    public void info(Object message, Object... params) {
        PreCon.notNull(message);
        PreCon.notNull(params);

        _logger.info(_consolePrefix + TextUtils.format(message, params));
    }

    @Override
    public void debug(Object message, Object... params) {
        PreCon.notNull(message);
        PreCon.notNull(params);

        if (_plugin instanceof NucleusPlugin && !((NucleusPlugin) _plugin).isDebugging())
            return;

        ConsoleCommandSender e = Bukkit.getConsoleSender();

        if (e != null) {
            tell(e, LineWrapping.DISABLED, ChatColor.GOLD + "[debug] " + TextUtils.format(message, params));
        }
        else {
            info("[debug] " + TextUtils.format(message, params));
        }
    }

    @Override
    public void warning(Object message, Object... params) {
        PreCon.notNull(message);
        PreCon.notNull(params);

        _logger.warning(_consolePrefix + TextUtils.format(message, params));
    }

    @Override
    public void severe(Object message, Object... params) {
        PreCon.notNull(message);
        PreCon.notNull(params);

        _logger.severe(_consolePrefix + TextUtils.format(message, params));
    }

    private IChatMessage loadChatPrefix() {
        if (_chatPrefix == null) {
            _chatPrefix = _prefixSource != null
                    ? TEXT_FORMATTER.format(InternalMessengerFactory.getChatPrefix(_prefixSource))
                    : new SimpleChatMessage();
            TEXT_SETTING.setLinePrepend(_prefixSource == null ? null :_chatPrefix);
        }
        return _chatPrefix;
    }
}

