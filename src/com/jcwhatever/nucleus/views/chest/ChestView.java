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

package com.jcwhatever.nucleus.views.chest;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.items.ItemStackMatcher;
import com.jcwhatever.nucleus.views.View;
import com.jcwhatever.nucleus.views.ViewCloseReason;
import com.jcwhatever.nucleus.views.ViewOpenReason;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * An abstract implementation of a chest inventory view.
 */
public abstract class ChestView extends View {

    public static final int MAX_SLOTS = 6 * 9;
    public static final int ROW_SIZE = 9;

    private Inventory _inventory;
    private InventoryView _inventoryView;
    private ItemStackMatcher _comparer;

    /**
     * Constructor.
     *
     * @param plugin     The owning plugin.
     * @param comparer   The item stack comparer.
     */
    protected ChestView(Plugin plugin, @Nullable ItemStackMatcher comparer) {
        this(plugin, null, comparer);
    }

    /**
     * Constructor.
     *
     * @param plugin     The owning plugin.
     * @param inventory  The inventory.
     * @param comparer   The item stack comparer.
     */
    protected ChestView(Plugin plugin, @Nullable Inventory inventory, @Nullable ItemStackMatcher comparer) {
        super(plugin);

        _inventory = inventory;
        _comparer = comparer;

        if (_comparer == null)
            _comparer = ItemStackMatcher.getDefault();
    }

    /**
     * Get the inventory view title.
     */
    public abstract String getTitle();

    /**
     * Get the views {@code ItemStackMatcher}.
     */
    public ItemStackMatcher getItemStackMatcher() {
        return _comparer;
    }

    @Override
    public InventoryType getInventoryType() {
        return InventoryType.CHEST;
    }

    @Nullable
    @Override
    public InventoryView getInventoryView() {
        return _inventoryView;
    }

    @Override
    @Nullable
    public Inventory getInventory() {
        return _inventory;
    }

    @Override
    public boolean isInventoryViewable() {
        return true;
    }

    @Override
    protected final boolean openView(ViewOpenReason reason) {
        ChestEventListener.register(this);

        if (!onPreShow(reason))
            return false;

        if (_inventory == null)
            _inventory = createInventory();

        if (_inventory.getType() != getInventoryType())
            throw new RuntimeException("Incorrect inventory type.");

        _inventoryView = getPlayer().openInventory(_inventory);

        onShow(reason);

        return true;
    }

    @Override
    protected void onClose(ViewCloseReason reason) {
        PreCon.notNull(reason);

        if (reason != ViewCloseReason.REFRESH) {
            ChestEventListener.unregister(this);
        }
    }

    /**
     * Called when the view is being opened but before the inventory
     * is created.
     *
     * @param reason  The reason the view is being opened.
     *
     * @return True to allow showing the view.
     */
    protected boolean onPreShow(ViewOpenReason reason) { return true; }

    /**
     * Called after the view is opened.
     *
     * @param reason  The view open reason.
     */
    protected abstract void onShow(ViewOpenReason reason);

    /**
     * Called to get an {@code Inventory} instance used to
     * open an inventory view to the player. Only called
     * if the {@code Inventory} is not set in the constructor.
     */
    protected abstract Inventory createInventory();

    /**
     * Called when an item is placed in an chest slot.
     */
    protected abstract ChestEventAction onItemsPlaced (ChestEventInfo eventInfo);

    /**
     * Called when an item is picked up from an chest slot.
     */
    protected abstract ChestEventAction onItemsPickup (ChestEventInfo eventInfo);

    /**
     * Called when an item is dropped outside the chest view.
     */
    protected abstract ChestEventAction onItemsDropped (ChestEventInfo eventInfo);

}
