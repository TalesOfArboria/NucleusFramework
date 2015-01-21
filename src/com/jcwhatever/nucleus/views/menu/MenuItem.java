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

import com.jcwhatever.nucleus.mixins.IMeta;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.MetaKey;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils.DisplayNameResult;

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
public class MenuItem extends ItemStack implements IMeta {

    private final int _slot;
    private Map<Object, Object> _meta;
    private List<Runnable> _onClick;

    public MenuItem(int slot, ItemStack itemStack,
                         @Nullable Map<Object, Object> meta,
                         @Nullable List<Runnable> onClick) {
        super(itemStack);

        _slot = slot;
        _meta = meta;
        _onClick = onClick;
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
     * <p/>
     * <p>This is used as the item stacks display name.</p>
     *
     * @return Null if not set.
     */
    @Nullable
    public String getTitle() {
        return ItemStackUtils.getDisplayName(this, DisplayNameResult.OPTIONAL);
    }

    /**
     * Get the menu item description.
     * <p/>
     * <p>This is used as the item stacks lore.</p>
     */
    @Nullable
    public String getDescription() {
        List<String> lore = ItemStackUtils.getLore(this);
        if (lore == null || lore.isEmpty())
            return null;

        return lore.get(0);
    }

    /**
     * Set the menu item title.
     * <p/>
     * <p>This is used for the item stacks display name.</p>
     *
     * @param title The menu item title.
     */
    public void setTitle(@Nullable String title) {
        ItemStackUtils.setDisplayName(this, title);
    }

    /**
     * Set the menu item description.
     */
    public void setDescription(@Nullable String description) {

        List<String> lore = ItemStackUtils.getLore(this);
        lore = lore == null
                ? new ArrayList<String>(1)
                : new ArrayList<>(lore);

        if (lore.isEmpty())
            lore.add(description);
        else
            lore.set(0, description);

        ItemStackUtils.setLore(this, lore);
    }

    /**
     * Determine if the menu item is set in the
     * specified menu view.
     *
     * @param menuView The menu view.
     */
    public boolean isVisible(MenuView menuView) {
        PreCon.notNull(menuView);

        InventoryView inventory = menuView.getInventoryView();
        if (inventory == null)
            return false;

        ItemStack itemStack = inventory.getItem(_slot);

        return menuView.getItemStackMatcher().isMatch(itemStack, this);
    }

    /**
     * Set the items visibility in the specified menu view.
     *
     * @param menuView  The menu view.
     * @param isVisible True to set, False to remove.
     */
    public void setVisible(MenuView menuView, boolean isVisible) {
        PreCon.notNull(menuView);

        Inventory inventory = menuView.getInventory();
        if (inventory == null || isVisible(menuView) == isVisible)
            return;

        inventory.setItem(_slot, isVisible ? this : null);
    }

    /**
     * Set the {@code MenuItem} into the specified {@code MenuView}
     * inventory.
     * 
     * @param menuView  The menu view.
     */
    public void set(MenuView menuView) {
        setVisible(menuView, true);
    }

    /**
     * Get a new list of all click callbacks attached
     * to the menu item.
     */
    public List<Runnable> getOnClick() {
        if (_onClick == null)
            return CollectionUtils.unmodifiableList();

        return CollectionUtils.unmodifiableList(_onClick);
    }

    /**
     * Add a callback to be run when the menu item is
     * selected.
     *
     * @param runnable The callback to add.
     */
    public void onClick(Runnable runnable) {
        if (_onClick == null)
            _onClick = new ArrayList<>(3);

        _onClick.add(runnable);
    }

    /**
     * Remove a click event callback from the menu item.
     *
     * @param runnable The callback to remove.
     * @return True if found and removed.
     */
    public boolean removeOnClick(Runnable runnable) {
        return _onClick != null &&
                _onClick.remove(runnable);
    }

    /**
     * Get a meta value from the menu items meta store.
     *
     * @param key The meta key.
     * @param <T> The meta value type.
     * @return Null if not found.
     */
    @Nullable
    @Override
    public <T> T getMeta(MetaKey<T> key) {
        PreCon.notNull(key);

        @SuppressWarnings("unchecked")
        T value = (T) getMetaMap().get(key);

        return value;
    }

    /**
     * Get a meta value from the menu items meta store.
     *
     * @param key The meta key.
     * @return Null if not found.
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
     * @param key   The meta key.
     * @param value The meta value.
     * @param <T>   The meta value type.
     */
    @Override
    public <T> void setMeta(MetaKey<T> key, @Nullable T value) {
        PreCon.notNull(key);

        if (value == null)
            getMetaMap().remove(key);
        else
            getMetaMap().put(key, value);
    }

    @Override
    public MenuItem clone() {
        ItemStack cloneStack = super.clone();

        return new MenuItem(_slot, cloneStack,
                _meta != null ? new HashMap<>(_meta) : null,
                _onClick != null ? new ArrayList<>(_onClick) : null);
    }

    // get the meta map and instantiate if not already instantiated.
    private Map<Object, Object> getMetaMap() {
        if (_meta == null)
            _meta = new HashMap<Object, Object>(10);
        return _meta;
    }
}