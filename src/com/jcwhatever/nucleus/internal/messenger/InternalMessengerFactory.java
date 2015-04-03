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

package com.jcwhatever.nucleus.internal.messenger;

import com.jcwhatever.nucleus.NucleusPlugin;
import com.jcwhatever.nucleus.messaging.IChatPrefixed;
import com.jcwhatever.nucleus.messaging.IMessenger;
import com.jcwhatever.nucleus.messaging.IMessengerFactory;
import com.jcwhatever.nucleus.storage.DataPath;
import com.jcwhatever.nucleus.providers.storage.DataStorage;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Logger;
import javax.annotation.Nullable;

/**
 * Internal messenger factory
 */
public final class InternalMessengerFactory implements IMessengerFactory {

    public static final Logger LOGGER = Logger.getLogger("Minecraft");

    private Map<Plugin, IDataNode> _nodeCache = new WeakHashMap<>(25);
    private Map<Plugin, IMessenger> _messengers = new WeakHashMap<>(25);
    private Map<Plugin, IMessenger> _anonMessengers = new WeakHashMap<>(25);

    @Override
    public IMessenger get(Plugin plugin) {
        PreCon.notNull(plugin);

        if (plugin instanceof NucleusPlugin) {
            NucleusPlugin gp = (NucleusPlugin)plugin;
            if (gp.getMessenger() != null) {
                return gp.getMessenger();
            }
        }

        IMessenger messenger = _messengers.get(plugin);
        if (messenger == null) {
            messenger = new InternalMessenger(this, plugin, plugin);
            _messengers.put(plugin, messenger);
        }

        return messenger;
    }

    @Override
    public IMessenger getAnon(Plugin plugin) {
        if (plugin instanceof NucleusPlugin) {
            NucleusPlugin gp = (NucleusPlugin)plugin;
            if (gp.getAnonMessenger() != null) {
                return gp.getAnonMessenger();
            }
        }

        IMessenger messenger = _anonMessengers.get(plugin);
        if (messenger == null) {
            messenger = new InternalAnonMessenger(this, plugin);
            _anonMessengers.put(plugin, messenger);
        }

        return messenger;
    }

    @Override
    public IMessenger create(Plugin plugin) {
        return create(plugin, plugin);
    }

    @Override
    public IMessenger create(Plugin plugin, @Nullable Object prefixObject) {
        return new InternalMessenger(this, plugin, prefixObject);
    }

    @Override
    public void tellImportant(Player player, boolean clearMessages) {
        PreCon.notNull(player);

        List<IDataNode> dataNodes = new ArrayList<>(_nodeCache.values());

        for (IDataNode data : dataNodes) {

            IDataNode playerData = data.getNode(player.getUniqueId().toString());

            boolean save = false;

            for (IDataNode contextData : playerData) {

                String prefix = contextData.getString("prefix", "");
                String message = contextData.getString("message", "");

                assert prefix != null;
                assert message != null;

                if (!message.isEmpty()) {
                    tellMissed(player, prefix + message);
                }

                if (clearMessages) {
                    contextData.remove();
                    save = true;
                }
            }

            if (save)
                data.save();
        }
    }

    /**
     * Get a chat prefix from the supplied object.
     */
    public static String getChatPrefix(@Nullable Object source) {
        if (source instanceof IChatPrefixed) {
            return ((IChatPrefixed) source).getChatPrefix();
        }
        else if (source instanceof Plugin) {
            return '[' + ((Plugin)source).getName() + ']';
        }
        else if (source instanceof String) {
            return ((String)source);
        }
        else
        {
            return source != null
                    ? source.toString()
                    : "";
        }
    }

    /**
     * Get a console prefix from the supplied object.
     */
    public static String getConsolePrefix(@Nullable Object source) {
        return source instanceof IChatPrefixed
                ? ((IChatPrefixed) source).getConsolePrefix()
                : getChatPrefix(source);
    }

    /**
     * Convert an object into a string.
     */
    public static String getString(Object object) {
        return object.toString();
    }

    /**
     * Get the data node where important messages are stored
     * for the specified plugin.
     */
    public IDataNode getImportantData(Plugin plugin) {

        IDataNode dataNode = _nodeCache.get(plugin);
        if (dataNode != null)
            return dataNode;


        dataNode = DataStorage.get(plugin, new DataPath("important-messages"));
        dataNode.load();

        _nodeCache.put(plugin, dataNode);

        return dataNode;
    }

    private static void tellMissed(Player p, String message) {
        p.sendRawMessage(ChatColor.GOLD + "[Missed] " + ChatColor.RESET + message);
    }
}
