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

package com.jcwhatever.nucleus.views.menu;

import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.items.ItemStackMatcher;
import com.jcwhatever.nucleus.views.chest.ChestEventAction;
import com.jcwhatever.nucleus.views.chest.ChestEventInfo;
import com.jcwhatever.nucleus.views.chest.ChestView;
import com.jcwhatever.nucleus.views.chest.InventoryItemAction.InventoryPosition;

import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.List;
import javax.annotation.Nullable;

/**
 * Abstract implementation of a {@link ChestView} used as a menu.
 */
public abstract class MenuView extends ChestView {

    private MenuInventory _inventory;

    /**
     * Constructor.
     *
     * @param plugin     The owning plugin.
     * @param comparer   An item stack comparer.
     */
    protected MenuView(Plugin plugin, @Nullable ItemStackMatcher comparer) {
        super(plugin, comparer);
    }

    /**
     * Constructor.
     *
     * @param plugin     The owning plugin.
     * @param inventory  The menu inventory.
     * @param comparer   An item stack comparer.
     */
    protected MenuView(Plugin plugin, MenuInventory inventory,
                       @Nullable ItemStackMatcher comparer) {
        super(plugin, inventory, comparer);
    }

    /**
     * Get the currently registered {@link MenuItem}'s.
     */
    public List<MenuItem> getMenuItems() {

        if (_inventory == null)
            return CollectionUtils.unmodifiableList(MenuItem.class);

        return CollectionUtils.unmodifiableList(_inventory.getMenuItems());
    }

    /**
     * Get the registered {@link MenuItem} at the specified
     * slot index.
     *
     * @param slot  The slot index.
     *
     * @return  Null if not found.
     */
    @Nullable
    public MenuItem getMenuItem(int slot) {
        return _inventory.getMenuItem(slot);
    }

    /**
     * Remove a menu item from the view.
     *
     * @param menuItem  The menu item to remove.
     */
    public void removeMenuItem(MenuItem menuItem) {
        PreCon.notNull(menuItem);

        MenuItem item = _inventory.getMenuItem(menuItem.getSlot());
        if (!menuItem.equals(item))
            return;

        _inventory.setItem(menuItem.getSlot(), null);

        Inventory inventory = getInventory();
        if (inventory == null)
            return;

        menuItem.setVisible(this, false);
    }

    /**
     * Set a menu item into the view inventory and register it.
     *
     * @param menuItem  The menu item to set.
     */
    public void setMenuItem(MenuItem menuItem) {
        PreCon.notNull(menuItem);

        menuItem.setVisible(this, true);
    }

    /**
     * Create the inventory needed by the {@link ChestView} super type.
     */
    @Override
    protected Inventory createInventory() {

        List<MenuItem> menuItems = createMenuItems();

        if (menuItems.size() > MAX_SLOTS)
            throw new RuntimeException("The number of menu items cannot be more than " + MAX_SLOTS + '.');

        int maxSlots = getSlotsRequired(menuItems);

        _inventory = new MenuInventory(getPlayer(), maxSlots, getTitle());

        for (MenuItem item : menuItems) {
            //item.set(this);
            _inventory.setItem(item.getSlot(), item);
        }

        return _inventory;
    }

    /**
     * Deny placing items into the menu.
     */
    @Override
    protected ChestEventAction onItemsPlaced(ChestEventInfo eventInfo) {
        return ChestEventAction.DENY;
    }

    /**
     * Deny dropping items from the menu.
     */
    @Override
    protected ChestEventAction onItemsDropped(ChestEventInfo eventInfo) {
        return ChestEventAction.DENY;
    }

    /**
     * Deny picking up items from the menu. Detect clicks on menu items.
     */
    @Override
    protected ChestEventAction onItemsPickup(ChestEventInfo eventInfo) {

        if (eventInfo.getInventoryPosition() == InventoryPosition.TOP) {

            MenuItem menuItem = _inventory.getMenuItem(eventInfo.getSlot());
            if (menuItem != null && menuItem.isVisible(this)) {

                List<Runnable> clickCallbacks = menuItem.getOnClick();
                for (Runnable onClick : clickCallbacks) {
                    onClick.run();
                }

                onItemSelect(menuItem);
            }
        }

        return ChestEventAction.DENY;
    }

    /**
     * Get the number of slots needed for the {@link Inventory}
     * instance.
     */
    protected int getSlotsRequired(List<MenuItem> menuItems) {
        int maxSlot = menuItems.size();

        for (MenuItem menuItem: menuItems) {

            if (menuItem.getSlot() > maxSlot) {
                maxSlot = menuItem.getSlot();
            }
        }

        int rows = (int) Math.ceil((double)maxSlot / ROW_SIZE);
        return Math.max(rows * ROW_SIZE, ROW_SIZE);
    }

    /**
     * Called to get a list of {@link MenuItem}'s to initially register and
     * fill the {@link org.bukkit.inventory.Inventory} after it is created.
     */
    protected abstract List<MenuItem> createMenuItems();

    /**
     * Called when a menu item in the inventory view is clicked
     * by the player.
     *
     * @param menuItem  The clicked menu item.
     */
    protected abstract void onItemSelect(MenuItem menuItem);
}
