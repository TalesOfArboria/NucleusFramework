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

package com.jcwhatever.nucleus.internal.managed.items.floating;

import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.managed.items.floating.IFloatingItem;
import com.jcwhatever.nucleus.utils.managers.NamedInsensitiveDataManager;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Floating item context.
 */
public class FloatingItemContext extends NamedInsensitiveDataManager<IFloatingItem> {

    private Plugin _plugin;

    /**
     * Constructor.
     *
     * @param plugin    The owning plugin.
     * @param dataNode  The data node.
     */
    protected FloatingItemContext(Plugin plugin, IDataNode dataNode) {
        super(dataNode, false);

        _plugin = plugin;

        load();
    }

    @Nullable
    public IFloatingItem add(String name, ItemStack itemStack) {
        return add(name, itemStack, null);
    }

    @Nullable
    public IFloatingItem add(String name, ItemStack itemStack,
                             @Nullable Location location) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(itemStack);

        if (contains(name))
            return null;

        IDataNode node = getNode(name);

        IFloatingItem item = createFloatingItem(name, itemStack, location, node);
        node.set("location", location);
        node.set("item", itemStack);
        node.save();

        add(item);

        return item;
    }

    @Nullable
    @Override
    protected IFloatingItem load(String name, IDataNode node) {

        Location location = node.getLocation("location");
        if (location == null) {
            NucMsg.debug(_plugin, "Location not found for floating item in data node.");
            return null;
        }

        boolean dispose = node.getBoolean("dispose");
        ItemStack[] itemStacks = node.getItemStacks("item");

        if (itemStacks == null || itemStacks.length == 0) {
            NucMsg.debug(_plugin, "Item stack not found for floating item in data node.");
            return null;
        }

        IFloatingItem item = createFloatingItem(name, itemStacks[0], location, node);

        // check if the item is marked for disposal
        if (dispose) {
            item.dispose();
            node.remove();
            return null;
        }

        return item;
    }

    @Nullable
    @Override
    protected void save(IFloatingItem item, IDataNode itemNode) {
        // do nothing
    }

    @Override
    protected void onRemove(IFloatingItem removed) {

        if (_plugin.isEnabled()) {
            removed.dispose();
            super.onRemove(removed);
        }
        else {
            IDataNode node = getNode(removed.getSearchName());
            node.set("dispose", true);
            node.saveSync();
        }
    }

    /*
     * Invoked to create a new instance of a floating item.
     */
    private IFloatingItem createFloatingItem(String name, ItemStack item,
                                             @Nullable Location location, IDataNode dataNode) {
        return new FloatingItem(_plugin, name, item, location, dataNode);
    }
}
