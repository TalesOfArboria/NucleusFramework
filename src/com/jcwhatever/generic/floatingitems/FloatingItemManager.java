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

package com.jcwhatever.generic.floatingitems;

import com.jcwhatever.generic.GenericsLib;
import com.jcwhatever.generic.internal.Msg;
import com.jcwhatever.generic.mixins.IPluginOwned;
import com.jcwhatever.generic.storage.IDataNode;
import com.jcwhatever.generic.utils.CollectionUtils;
import com.jcwhatever.generic.utils.PreCon;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/*
 * 
 */
public class FloatingItemManager implements IPluginOwned {

    private final Plugin _plugin;
    private final IDataNode _dataNode;

    private Map<String, IFloatingItem> _itemMap = new HashMap<>(50);

    public FloatingItemManager(Plugin plugin, IDataNode dataNode) {
        PreCon.notNull(plugin);
        PreCon.notNull(dataNode);

        _plugin = plugin;
        _dataNode = dataNode;

        loadSettings();
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    @Nullable
    public IFloatingItem add(String name, ItemStack itemStack) {
        return add(name, itemStack, null);
    }

    @Nullable
    public IFloatingItem add(String name, ItemStack itemStack, @Nullable Location location) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(itemStack);

        IFloatingItem item = _itemMap.get(name.toLowerCase());
        if (item != null) {
            return null;
        }

        IDataNode node = _dataNode.getNode(name);

        item = createFloatingItem(name, itemStack, location, node);

        node.set("location", location);
        node.set("item", itemStack);
        node.saveAsync(null);

        _itemMap.put(name.toLowerCase(), item);

        return item;
    }

    public boolean remove(String name) {
        PreCon.notNullOrEmpty(name);

        IFloatingItem item = _itemMap.remove(name.toLowerCase());
        if (item == null)
            return false;

        item.dispose();

        IDataNode node = item.getDataNode();
        if (node != null) {
            if (GenericsLib.getPlugin().isEnabled()) {
                node.remove();
                node.saveAsync(null);
            }
            else {
                node.set("dispose", true);
                node.save();
            }
        }

        return true;
    }

    public List<IFloatingItem> getItems() {
        return CollectionUtils.unmodifiableList(_itemMap.values());
    }

    @Nullable
    public IFloatingItem getItem(String name) {
        PreCon.notNullOrEmpty(name);

        return _itemMap.get(name.toLowerCase());
    }

    /**
     * Called after settings are loaded from the data node.
     *
     * @param dataNode  The data node.
     */
    protected void onLoadSettings(@SuppressWarnings("unused") IDataNode dataNode) {
        // do nothing
    }

    /**
     * Called after an item is loaded from the data node.
     *
     * @param item  The item.
     */
    protected void onItemLoaded(@SuppressWarnings("unused") IFloatingItem item) {
        // do nothing
    }

    private void loadSettings() {

        Set<String> itemNames = _dataNode.getSubNodeNames();

        for (String name : itemNames) {

            IDataNode node = _dataNode.getNode(name);

            Location location = node.getLocation("location");
            if (location == null) {
                Msg.debug(_plugin, "Location not found for floating item in data node.");
                continue;
            }

            boolean dispose = node.getBoolean("dispose");
            ItemStack[] itemStacks = node.getItemStacks("item");

            if (itemStacks == null || itemStacks.length == 0) {
                Msg.debug(_plugin, "Item stack not found for floating item in data node.");
                continue;
            }

            IFloatingItem item = createFloatingItem(name, itemStacks[0], location, node);

            // check if the item is marked for disposal
            if (dispose) {
                item.dispose();
                continue;
            }

            _itemMap.put(name.toLowerCase(), item);

            onItemLoaded(item);
        }

        onLoadSettings(_dataNode);
    }

    protected IFloatingItem createFloatingItem(String name, ItemStack item,
                                            @Nullable Location location, IDataNode dataNode) {
        return new FloatingItem(name, item, location, dataNode);
    }

}
