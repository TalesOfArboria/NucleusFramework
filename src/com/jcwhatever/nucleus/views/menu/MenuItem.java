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
import com.jcwhatever.nucleus.utils.MetaStore;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils.DisplayNameOption;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Represents an item in a {@link MenuView}.
 */
public class MenuItem extends ItemStack implements IMeta {

    private final int _slot;
    private final MetaStore _meta = new MetaStore();
    private List<Runnable> _onClick;

    /**
     * Constructor.
     *
     * @param slot       The inventory slot the items belongs in.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack}.
     */
    public MenuItem(int slot, ItemStack itemStack) {
        super(itemStack);

        _slot = slot;
    }

    /**
     * Constructor.
     *
     * <p>Copies an existing menu item.</p>
     *
     * @param menuItem  The menu item to copy.
     */
    public MenuItem(MenuItem menuItem) {
        super(menuItem.clone());

        _slot = menuItem._slot;
        _onClick = menuItem._onClick;
        _meta.copyAll(menuItem);
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
        return ItemStackUtils.getDisplayName(this, DisplayNameOption.OPTIONAL);
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
     * Set the {@link MenuItem} into the specified {@link MenuView}
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
     *
     * @return True if found and removed.
     */
    public boolean removeOnClick(Runnable runnable) {
        return _onClick != null &&
                _onClick.remove(runnable);
    }

    /**
     * Clear all click event callbacks.
     */
    public void clearOnClick() {
        if (_onClick == null)
            return;

        _onClick.clear();
    }

    @Override
    public MetaStore getMeta() {
        return _meta;
    }

    @Override
    public MenuItem clone() {
        return new MenuItem(this);
    }
}