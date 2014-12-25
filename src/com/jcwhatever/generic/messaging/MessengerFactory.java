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

import com.jcwhatever.generic.GenericsLib;
import com.jcwhatever.generic.GenericsPlugin;
import com.jcwhatever.generic.storage.DataPath;
import com.jcwhatever.generic.storage.DataStorage;
import com.jcwhatever.generic.storage.IDataNode;
import com.jcwhatever.generic.utils.PreCon;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Logger;
import javax.annotation.Nullable;

/**
 * Generates {@code IMessenger} implementation instances.
 */
public class MessengerFactory {

    protected MessengerFactory(Plugin plugin) {
        if (plugin != GenericsLib.getPlugin())
            throw new RuntimeException("MessengerFactory should only be instantiated by GenericsLib.");
    }

    protected final Logger _log = Logger.getLogger("Minecraft");

    private Map<Plugin, IDataNode> _nodeCache = new WeakHashMap<>(25);
    private Map<Plugin, IMessenger> _messengers = new WeakHashMap<>(25);
    private Map<Plugin, IMessenger> _anonMessengers = new WeakHashMap<>(25);

    /**
     * Gets or creates a singleton instance of a messenger
     * for the specified plugin.
     *
     * @param plugin  The plugin.
     */
    public static IMessenger get(Plugin plugin) {

        if (plugin instanceof GenericsPlugin) {
            GenericsPlugin gp = (GenericsPlugin)plugin;
            if (gp.getMessenger() != null) {
                return gp.getMessenger();
            }
        }

        MessengerFactory factory = GenericsLib.getMessengerFactory();

        IMessenger messenger = factory._messengers.get(plugin);
        if (messenger == null) {
            messenger = new Messenger(plugin, plugin);
            factory._messengers.put(plugin, messenger);
        }

        return messenger;
    }

    /**
     * Get a singleton messenger that has no chat prefix.
     */
    public static IMessenger getAnon(Plugin plugin) {

        if (plugin instanceof GenericsPlugin) {
            GenericsPlugin gp = (GenericsPlugin)plugin;
            if (gp.getAnonMessenger() != null) {
                return gp.getAnonMessenger();
            }
        }

        MessengerFactory factory = GenericsLib.getMessengerFactory();

        IMessenger messenger = factory._anonMessengers.get(plugin);
        if (messenger == null) {
            messenger = new AnonMessenger(plugin);
            factory._anonMessengers.put(plugin, messenger);
        }

        return messenger;
    }

    /**
     * Create a new messenger instance.
     *
     * @param plugin  The owning plugin.
     */
    public static IMessenger create(Plugin plugin) {
        return create(plugin, plugin);
    }

    /**
     * Create a new messenger instance.
     *
     * @param plugin        The owning plugin.
     * @param prefixObject  The object to create a prefix from.
     */
    public static IMessenger create(Plugin plugin, @Nullable Object prefixObject) {

        return new Messenger(plugin, prefixObject);
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
     * Get the minecraft logger.
     */
    public Logger getLogger() {
        return _log;
    }

    /**
     * Get the data node where important messages are stored
     * for the specified plugin.
     */
    public IDataNode getImportantData(Plugin plugin) {

        IDataNode dataNode = _nodeCache.get(plugin);
        if (dataNode != null)
            return dataNode;

        dataNode = DataStorage.getStorage(plugin, new DataPath("important-messages"));
        dataNode.load();

        _nodeCache.put(plugin, dataNode);

        return dataNode;
    }

    /**
     * Called to display stored important messages for the
     * specified player to the specified player.
     *
     * @param p  The player.
     */
    public void tellImportant(Player p) {
        PreCon.notNull(p);

        List<IDataNode> dataNodes = new ArrayList<>(_nodeCache.values());

        for (IDataNode data : dataNodes) {

            IDataNode playerData = data.getNode(p.getUniqueId().toString());

            Set<String> contexts = playerData.getSubNodeNames();
            if (contexts == null)
                return;

            boolean save = false;

            for (String context : contexts) {
                IDataNode contextData = playerData.getNode(context);

                String prefix = contextData.getString("prefix", "");
                String message = contextData.getString("message", "");

                assert prefix != null;
                assert message != null;

                if (!message.isEmpty()) {
                    tellMissed(p, prefix + message);
                }

                contextData.remove();
                save = true;
            }

            if (save)
                data.saveAsync(null);
        }
    }

    private static void tellMissed(Player p, String message) {
        p.sendRawMessage(ChatColor.GOLD + "[Missed] " + ChatColor.RESET + message);
    }
}
