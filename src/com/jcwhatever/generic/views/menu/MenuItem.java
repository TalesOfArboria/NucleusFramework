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

package com.jcwhatever.generic.views.menu;

import com.jcwhatever.generic.mixins.ICancellable;
import com.jcwhatever.generic.mixins.IMeta;
import com.jcwhatever.generic.utils.items.ItemStackUtils;
import com.jcwhatever.generic.utils.MetaKey;
import com.jcwhatever.generic.utils.PreCon;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Represents an item in a {@code MenuView}.
 */
public class MenuItem implements IMeta, ICancellable {

    private final int _slot;

    private Map<Object, Object> _metaMap;
    private List<Runnable> _onClick;
    private String _title;
    private String _description;
    private ItemStack _baseItemStack;
    private ItemStack _menuItemStack;

    private boolean _isCancelled;

    /**
     * Constructor.
     *
     * @param slot  The inventory slot index the item is assigned to.
     */
    public MenuItem(int slot) {
        _slot = slot;
    }

    /**
     * Get the inventory slot index the item is
     * assigned to.
     */
    public int getSlot() {
        return _slot;
    }

    /**
     * Get the menu item title.
     *
     * <p>This is used as the item stacks display name.</p>
     *
     * @return  Null if not set.
     */
    @Nullable
    public String getTitle() {
        return _title;
    }

    /**
     * Get the menu item description.
     *
     * <p>This is used as the item stacks lore.</p>
     */
    @Nullable
    public String getDescription() {
        return _description;
    }

    /**
     * Get the item stack used for the menu inventory.
     */
    public ItemStack getItemStack() {
        if (_menuItemStack == null) {
            _menuItemStack = generateItemStack(_baseItemStack);
        }
        return _menuItemStack;
    }

    /**
     * Set the menu item title.
     *
     * <p>This is used for the item stacks display name.</p>
     *
     * @param title  The menu item title.
     *
     * @return  Self for chaining.
     */
    public MenuItem setTitle(@Nullable String title) {
        _title = title;
        _menuItemStack = null;

        return this;
    }

    /**
     * Set the menu item description.
     *
     * <p>This is used for the item stacks lore.</p>
     *
     * @param description  The menu item description.
     *
     * @return  Self for chaining.
     */
    public MenuItem setDescription(@Nullable String description) {
        _description = description;
        _menuItemStack = null;

        return this;
    }

    /**
     * Set the item stack to use.
     *
     * @param itemStack  The item stack.
     *
     * @return  Self for chaining.
     */
    public MenuItem setItemStack(ItemStack itemStack) {
        PreCon.notNull(itemStack);

        _baseItemStack = itemStack;
        _menuItemStack = null;

        return this;
    }

    /**
     * Set the item into the inventory of the
     * specified menu view.
     *
     * @param menuView  The menu view.
     *
     * @return  True if successful.
     */
    public boolean set(MenuView menuView) {
        PreCon.notNull(menuView);

        Inventory inventory = menuView.getInventory();
        if (inventory == null)
            return false;

        inventory.setItem(_slot, getItemStack());

        return true;
    }

    /**
     * Determine if the menu item is set in the
     * specified menu view.
     *
     * @param menuView  The menu view.
     */
    public boolean isVisible(MenuView menuView) {
        PreCon.notNull(menuView);

        Inventory inventory = menuView.getInventory();
        if (inventory == null)
            return false;

        ItemStack itemStack = inventory.getItem(_slot);

        return menuView.getItemStackComparer().isSame(itemStack, getItemStack());
    }

    /**
     * Set the items visibility in the specified menu view.
     *
     * @param menuView   The menu view.
     * @param isVisible  True to set, False to remove.
     */
    public void setVisible(MenuView menuView, boolean isVisible) {
        PreCon.notNull(menuView);

        Inventory inventory = menuView.getInventory();
        if (inventory == null || isVisible(menuView) == isVisible)
            return;

        if (isVisible) {
            set(menuView);
        }
        else {

            InventoryView view = menuView.getInventoryView();

            if (view != null) {
                view.setItem(_slot, new ItemStack(Material.AIR));
            }
            else {
                throw new AssertionError();
            }
        }
    }

    /**
     * Get a new list of all click callbacks attached
     * to the menu item.
     */
    public List<Runnable> getOnClick() {
        if (_onClick == null)
            return new ArrayList<>(0);

        return new ArrayList<>(_onClick);
    }

    /**
     * Add a callback to be run when the menu item is
     * selected.
     *
     * @param runnable  The callback to add.
     */
    public void onClick(Runnable runnable) {
        if (_onClick == null)
            _onClick = new ArrayList<>(5);

        _onClick.add(runnable);
    }

    /**
     * Remove a click event callback from the menu item.
     *
     * @param runnable  The callback to remove.
     *
     * @return  True if found and removed.
     */
    public boolean removeOnClick(Runnable runnable) {
        return _onClick != null &&
                _onClick.remove(runnable);
    }

    /**
     * Get a meta value from the menu items meta store.
     *
     * @param key  The meta key.
     *
     * @param <T>  The meta value type.
     *
     * @return  Null if not found.
     */
    @Nullable
    @Override
    public <T> T getMeta(MetaKey<T> key) {
        PreCon.notNull(key);

        @SuppressWarnings("unchecked")
        T value = (T)getMetaMap().get(key);

        return value;
    }

    /**
     * Get a meta value from the menu items meta store.
     *
     * @param key  The meta key.
     *
     * @return  Null if not found.
     */
    @Nullable
    @Override
    public Object getMetaObject(Object key) {
        PreCon.notNull(key);

        return getMetaMap().get(key);
    }

    /**
     * Set a meta value in the menu items meta store.
     *
     * @param key    The meta key.
     * @param value  The meta value.
     *
     * @param <T>  The meta value type.
     */
    @Override
    public <T> void setMeta(MetaKey<T> key, @Nullable T value) {
        PreCon.notNull(key);

        if (value == null)
            getMetaMap().remove(key);
        else
            getMetaMap().put(key, value);
    }

    /**
     * Determine if the menu item click event is cancelled.
     */
    @Override
    public boolean isCancelled() {
        return _isCancelled;
    }

    /**
     * Set the cancelled state of the menu item.
     *
     * @param isCancelled  True to cancel click event.
     */
    @Override
    public void setCancelled(boolean isCancelled) {
        _isCancelled = isCancelled;
    }

    /**
     * Called to generate an item stack from the base
     * item stack and set properties such as title and
     * description on the new item stack.
     */
    protected ItemStack generateItemStack(ItemStack baseItemStack) {
        if (baseItemStack == null)
            baseItemStack = new ItemStack(Material.WOOD);

        _menuItemStack = baseItemStack.clone();

        if (_title != null)
            ItemStackUtils.setDisplayName(_menuItemStack, _title);

        if (_description != null)
            ItemStackUtils.setLore(_menuItemStack, _description);

        return _menuItemStack;
    }

    // get the meta map and instantiate if not already instantiated.
    private Map<Object, Object> getMetaMap() {
        if (_metaMap == null)
            _metaMap = new HashMap<Object, Object>(10);
        return _metaMap;
    }
}
