/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

import com.sun.istack.internal.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.views.InventoryActionInfoHandler.InventoryActionInfo;
import com.jcwhatever.bukkit.generic.views.InventoryActionInfoHandler.ViewActionOrder;

/**
 * Represents a Chest inventory GUI view.
 */
public class ChestView extends AbstractView {

    private static final int MAX_ROWS = 6;
    private int _rows = 6;

    public int getRows() {
        return _rows;
    }

    public void setRows(int rows) {
        _rows = Math.min(rows, MAX_ROWS);
    }

    @Override
    protected void onInit(String name, IDataNode dataNode, ViewManager viewManager) {
        // do nothing
    }

    @Override
    public InventoryType getInventoryType() {
        return InventoryType.CHEST;
    }

    @Override
    public ViewType getViewType() {
        return ViewType.INVENTORY;
    }

    @Override
    public void dispose() {
        // do nothing

    }

    @Override
    protected void onLoadSettings(IDataNode dataNode) {
        // do nothing
    }

    @Override
    protected ViewInstance onCreateInstance(Player p, @Nullable ViewInstance previous, ViewMeta sessionMeta, ViewMeta instanceMeta) {
        return new ChestInstance(this, previous, p, sessionMeta, instanceMeta);
    }

    /**
     * Instance of a Chest GUI view shown to a player.
     */
    public class ChestInstance extends ViewInstance {

        /**
         * Constructor.
         *
         * @param view          The owning view.
         * @param previous      The view the player was previously looking at, if any.
         * @param p             The player to show the view to.
         * @param sessionMeta   The players session meta.
         * @param instanceMeta  The instance meta.
         */
        public ChestInstance(IView view, @Nullable ViewInstance previous, Player p, ViewMeta sessionMeta, ViewMeta instanceMeta) {
            super(view, previous, p, sessionMeta, instanceMeta);
        }

        @Override
        @Nullable
        public ViewResult getResult() {
            return null; // no result returned
        }

        @Override
        protected InventoryView onShow(ViewMeta meta) {
            Inventory inventory = Bukkit.createInventory(getPlayer(), _rows * 9);
            return getPlayer().openInventory(inventory);
        }

        @Override
        protected InventoryView onShowAsPrev(ViewMeta instanceMeta, ViewResult result) {
            return onShow(instanceMeta);
        }

        @Override
        protected void onClose(ViewCloseReason reason) {
            // do nothing
        }

        @Override
        protected boolean onItemsPlaced(InventoryActionInfo actionInfo, ViewActionOrder actionOrder) {
            return true;
        }

        @Override
        protected boolean onItemsPickup(InventoryActionInfo actionInfo, ViewActionOrder actionOrder) {
            return true;
        }

        @Override
        protected boolean onItemsDropped(InventoryActionInfo actionInfo, ViewActionOrder actionOrder) {
            return true;
        }

        @Override
        protected boolean onLowerItemsPlaced (InventoryActionInfo actionInfo, ViewActionOrder actionOrder) {
            return true;
        }

        @Override
        protected boolean onLowerItemsPickup (InventoryActionInfo actionInfo, ViewActionOrder actionOrder) {
            return true;
        }
    }



}
