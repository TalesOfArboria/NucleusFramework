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

import com.jcwhatever.nucleus.views.chest.InventoryItemAction.InventoryPosition;
import com.jcwhatever.nucleus.views.chest.InventoryItemAction.ItemAction;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Provides information and objects from a chest
 * inventory event.
 */
public class ChestEventInfo {

    private InventoryAction _inventoryAction;
    private InventoryItemAction _action;
    private InventoryItemAction _secondaryAction;
    private ItemStackSource _source;
    private ItemStack _itemStack;
    private ItemStack _cursorStack;
    private ItemStack _slotStack;
    private Inventory _inventory;
    private Inventory _topInventory;
    private Inventory _bottomInventory;
    private int _rawSlot;
    private int _slot;

    ChestEventInfo() {}

    /**
     * Get the inventory action of the event.
     */
    public InventoryAction getInventoryAction() {
        return _inventoryAction;
    }

    void setInventoryAction(InventoryAction action) {
        _inventoryAction = action;
    }

    /**
     * Get the item action of the event.
     */
    public InventoryItemAction getAction() {
        return _action;
    }

    void setAction(InventoryItemAction action) {
        _action = action;
    }

    /**
     * Get the secondary action. The secondary action
     * is the action that was taken in order to cause
     * the event.
     */
    public InventoryItemAction getSecondaryAction() {
        return _secondaryAction;
    }

    void setSecondaryAction(InventoryItemAction action) {
        _secondaryAction = action;
    }

    /**
     * Get the item action. This is a more generalized
     * version of an inventory item action.
     */
    public ItemAction getItemAction() {
        return _action.getItemAction();
    }

    /**
     * Get the inventory position the event takes place in.
     * (top or bottom inventory)
     */
    public InventoryPosition getInventoryPosition() {
        return _action.getInventoryPosition();
    }

    /**
     * Get the source of the item stack most relevant to the event.
     */
    public ItemStackSource getItemStackSource() {
        return _source;
    }

    void setItemStackSource(ItemStackSource source) {
        _source = source;
    }

    /**
     * Get the item stack most relevant to the event.
     */
    public ItemStack getItemStack() {
        return _itemStack;
    }

    void setItemStack(ItemStack itemStack) {
        _itemStack = itemStack;
    }

    /**
     * Get the item stack in the players cursor.
     */
    public ItemStack getCursorStack() {
        return _cursorStack;
    }

    void setCursorStack(ItemStack cursorStack) {
        _cursorStack = cursorStack;
    }

    /**
     * Get the item stack in from the relevant inventory slot.
     */
    public ItemStack getSlotStack() {
        return _slotStack;
    }

    void setSlotStack(ItemStack slotStack) {
        _slotStack = slotStack;
    }

    /**
     * Get the inventory.
     */
    public Inventory getInventory() {
        return _inventory;
    }

    void setInventory(Inventory inventory) {
        _inventory = inventory;
    }

    /**
     * Get the top inventory.
     */
    public Inventory getTopInventory() {
        return _topInventory;
    }

    void setTopInventory(Inventory inventory) {
        _topInventory = inventory;
    }

    /**
     * Get the bottom inventory.
     */
    public Inventory getBottomInventory() {
        return _bottomInventory;
    }

    void setBottomInventory(Inventory inventory) {
        _bottomInventory = inventory;
    }

    /**
     * Get the raw slot index.
     */
    public int getRawSlot() {
        return _rawSlot;
    }

    void setRawSlot(int rawSlot) {
        _rawSlot = rawSlot;
    }

    /**
     * Get the slot index.
     */
    public int getSlot() {
        return _slot;
    }

    void setSlot(int slot) {
        _slot = slot;
    }

    /**
     * Specifies the source of an item stack
     * from the inventory event.
     */
    public enum ItemStackSource {
        /**
         * The item stack is from the inventory slot.
         */
        SLOT,

        /**
         * The item stack is from the players cursor.
         */
        CURSOR
    }
}
