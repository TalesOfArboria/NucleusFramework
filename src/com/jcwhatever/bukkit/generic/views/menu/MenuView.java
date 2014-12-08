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

package com.jcwhatever.bukkit.generic.views.menu;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.views.IViewFactory;
import com.jcwhatever.bukkit.generic.views.ViewSession;
import com.jcwhatever.bukkit.generic.views.chest.ChestEventAction;
import com.jcwhatever.bukkit.generic.views.chest.ChestEventInfo;
import com.jcwhatever.bukkit.generic.views.chest.ChestView;
import com.jcwhatever.bukkit.generic.views.chest.InventoryItemAction.InventoryPosition;
import com.jcwhatever.bukkit.generic.views.data.ViewArguments;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/*
 * 
 */
public abstract class MenuView extends ChestView {

    public static final int MAX_SLOTS = 6 * 9;
    public static final int ROW_SIZE = 9;

    private final Map<Integer, MenuItem> _menuItems = new HashMap<>(MAX_SLOTS);

    protected MenuView(@Nullable String title, ViewSession session, IViewFactory factory, ViewArguments arguments) {
        super(title, session, factory, arguments);

        List<MenuItem> menuItems = createMenuItems();
        for (MenuItem menuItem : menuItems) {
            menuItem.setMenuView(this);
            _menuItems.put(menuItem.getSlot(), menuItem);
        }
    }

    @Nullable
    public MenuItem getMenuItem(int slot) {
        return _menuItems.get(slot);
    }

    public void setMenuItem(MenuItem menuItem) {
        PreCon.notNull(menuItem);

        menuItem.setMenuView(this);

        _menuItems.put(menuItem.getSlot(), menuItem);

        InventoryView inventoryView = getInventoryView();
        if (inventoryView == null)
            return;

        inventoryView.getTopInventory().setItem(menuItem.getSlot(), menuItem.getItemStack());
    }

    protected abstract List<MenuItem> createMenuItems();

    protected abstract void onItemSelect(MenuItem menuItem);

    @Override
    protected Inventory createInventory() {

        if (_menuItems.size() > MAX_SLOTS)
            throw new RuntimeException("The number of menu items cannot be more than " + MAX_SLOTS + '.');

        int maxSlot = _menuItems.size();

        for (MenuItem menuItem: _menuItems.values()) {

            if (menuItem.getSlot() > maxSlot) {
                maxSlot = menuItem.getSlot();
            }
        }


        int rows = (int) Math.ceil((double)maxSlot / ROW_SIZE);
        int slots = rows * ROW_SIZE;

        Inventory inventory =  getTitle() != null
                ? Bukkit.createInventory(getPlayer(), slots, getTitle())
                : Bukkit.createInventory(getPlayer(), slots);

        for (MenuItem item : _menuItems.values()) {
            inventory.setItem(item.getSlot(), item.getItemStack());
        }

        return inventory;
    }

    @Override
    protected ChestEventAction onItemsPlaced(ChestEventInfo eventInfo) {
        return ChestEventAction.DENY;
    }

    @Override
    protected ChestEventAction onItemsDropped(ChestEventInfo eventInfo) {
        return ChestEventAction.DENY;
    }

    @Override
    protected ChestEventAction onItemsPickup(ChestEventInfo eventInfo) {

        if (eventInfo.getInventoryPosition() == InventoryPosition.UPPER) {

            MenuItem menuItem = _menuItems.get(eventInfo.getSlot());
            if (menuItem != null && menuItem.isVisible()) {

                Runnable onClick = menuItem.getOnClick();
                if (onClick != null)
                    onClick.run();

                if (menuItem.isCancelled()) {
                    menuItem.setCancelled(false);
                }
                else {
                    onItemSelect(menuItem);
                }
            }
        }

        return ChestEventAction.DENY;
    }
}
