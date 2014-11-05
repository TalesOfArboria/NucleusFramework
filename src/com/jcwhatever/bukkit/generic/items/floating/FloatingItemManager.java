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


package com.jcwhatever.bukkit.generic.items.floating;

import com.jcwhatever.bukkit.generic.messaging.Messenger;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FloatingItemManager {

    private final Plugin _plugin;
    private final IDataNode _dataNode;

    private Map<String, FloatingItem> _itemMap = new HashMap<>(50);

    public FloatingItemManager(Plugin plugin, IDataNode dataNode) {
        PreCon.notNull(plugin);
        PreCon.notNull(dataNode);

        _plugin = plugin;
        _dataNode = dataNode;

        loadSettings();
    }

    public Plugin getPlugin() {
        return _plugin;
    }

    public FloatingItem add(String name, Location location, ItemStack itemStack) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(location);
        PreCon.notNull(itemStack);

        IDataNode node = _dataNode.getNode(name);

        FloatingItem item = new FloatingItem(name, location, itemStack, node);

        node.set("location", location);
        node.set("item", itemStack);
        node.saveAsync(null);

        _itemMap.put(name.toLowerCase(), item);

        return item;
    }

    public boolean remove(String name) {
        PreCon.notNullOrEmpty(name);

        FloatingItem item = _itemMap.remove(name.toLowerCase());
        if (item == null)
            return false;

        item.dispose();

        IDataNode node = item.getDataNode();
        if (node != null) {
            node.remove();
            node.saveAsync(null);
        }

        return true;
    }

    public List<FloatingItem> getItems() {
        return new ArrayList<>(_itemMap.values());
    }

    @Nullable
    public FloatingItem getItem(String name) {
        PreCon.notNullOrEmpty(name);

        return _itemMap.get(name.toLowerCase());
    }

    private void loadSettings() {

        Set<String> itemNames = _dataNode.getSubNodeNames();

        for (String name : itemNames) {

            IDataNode node = _dataNode.getNode(name);

            Location location = node.getLocation("location");
            if (location == null) {
                Messenger.debug(_plugin, "Location not found for floating item in data node.");
                continue;
            }

            ItemStack[] itemStacks = node.getItemStacks("item");

            if (itemStacks == null || itemStacks.length == 0) {
                Messenger.debug(_plugin, "Item stack not found for floating item in data node.");
                continue;
            }

            FloatingItem item = new FloatingItem(name, location, itemStacks[0], node);

            _itemMap.put(name.toLowerCase(), item);

        }

    }

}
