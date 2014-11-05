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


package com.jcwhatever.bukkit.generic.views;

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a Menu GUI view, an inventory view with
 * functionality of a menu.
 */
public class MenuView extends AbstractMenuView {

    protected Inventory _menu;

    protected Map<String, MenuItem> _itemMap = new HashMap<String, MenuItem>(36);
    protected Map<Integer, MenuItem> _slotMap = new HashMap<Integer, MenuItem>(36);


    @Override
    protected void onInit() {
        //do nothing
    }

    /**
     * Get a menu item by name.
     *
     * @param name  The name of the menu item.
     */
    @Nullable
    public MenuItem getMenuItem(String name) {
        PreCon.notNullOrEmpty(name);

        name = name.toLowerCase();

        return _itemMap.get(name);
    }

    /**
     * Get a list of menu items.
     */
    public List<MenuItem> getMenuItems() {
        return new ArrayList<MenuItem>(_itemMap.values());
    }

    /**
     * Add a menu item.
     *
     * @param name         The name of the menu item.
     * @param item         The {@code ItemStack} used to represent the item.
     * @param title        The menu item title.
     * @param description  The menu item description.
     *
     * @return  True if the item was added.
     */
    public boolean addMenuItem(String name, ItemStack item, String title, String description) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(item);

        name = name.toLowerCase();

        MenuItem menuItem = _itemMap.get(name);
        if (menuItem != null)
            return false;

        IDataNode node = null;

        if (_dataNode != null) {
            node = _dataNode.getNode("items." + name);
            node.set("item", item);
            node.set("title", title);
            node.set("description", description);
            node.saveAsync(null);
        }

        menuItem = new MenuItem(-1, name, this, node);

        _itemMap.put(name, menuItem);

        buildInventory();

        return true;
    }

    /**
     * Remove a menu item by name.
     *
     * @param name  The name of the menu item.
     *
     * @return  True if the item was removed.
     */
    public boolean removeMenuItem(String name) {
        PreCon.notNullOrEmpty(name);

        name = name.toLowerCase();

        MenuItem menuItem = _itemMap.remove(name);
        if (menuItem == null)
            return false;

        if (_dataNode != null) {
            _dataNode.remove("items." + name);
            _dataNode.saveAsync(null);
        }

        buildInventory();

        return true;
    }

    @Override
    protected void onLoadSettings(IDataNode menuNode) {

        Set<String> itemNames = menuNode.getSubNodeNames("items");
        if (itemNames != null && !itemNames.isEmpty()) {

            for (String itemName : itemNames) {
                IDataNode itemNode = menuNode.getNode("items." + itemName);
                MenuItem item = new MenuItem(-1, itemName, this, itemNode);
                _itemMap.put(itemName.toLowerCase(), item);
            }
        }

        buildInventory();
    }

    @Override
    protected void buildInventory() {

        List<MenuItem> menuItems = new ArrayList<MenuItem>(_itemMap.values());

        double itemSize = menuItems.size();
        int rows = (int)Math.ceil(itemSize / 9);

        int slots = rows * 9;

        _menu = Bukkit.createInventory(null, slots, getDefaultTitle());

        int size = Math.min(menuItems.size(), slots);


        _slotMap.clear();
        for (int i=0; i < size; i++) {
            menuItems.get(i).setSlot(i);
            _menu.setItem(i, menuItems.get(i).getItemStack());
            _slotMap.put(i, menuItems.get(i));
        }
    }

    @Override
    protected ViewInstance onCreateInstance(Player p, ViewInstance previous, ViewMeta sessionMeta, ViewMeta instanceMeta) {
        return new MenuInstance(this, previous, p, sessionMeta, instanceMeta);
    }

}
