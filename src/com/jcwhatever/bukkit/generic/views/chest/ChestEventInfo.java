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

package com.jcwhatever.bukkit.generic.views.chest;

import com.jcwhatever.bukkit.generic.views.chest.InventoryItemAction.InventoryPosition;
import com.jcwhatever.bukkit.generic.views.chest.InventoryItemAction.ItemAction;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/*
 * 
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

    public InventoryAction getInventoryAction() {
        return _inventoryAction;
    }

    void setInventoryAction(InventoryAction action) {
        _inventoryAction = action;
    }

    public InventoryItemAction getAction() {
        return _action;
    }

    void setAction(InventoryItemAction action) {
        _action = action;
    }

    public InventoryItemAction getSecondaryAction() {
        return _secondaryAction;
    }

    void setSecondaryAction(InventoryItemAction action) {
        _secondaryAction = action;
    }

    public ItemAction getItemAction() {
        return _action.getItemAction();
    }

    public InventoryPosition getInventoryPosition() {
        return _action.getInventoryPosition();
    }

    public ItemStackSource getItemStackSource() {
        return _source;
    }

    void setItemStackSource(ItemStackSource source) {
        _source = source;
    }

    public ItemStack getItemStack() {
        return _itemStack;
    }

    void setItemStack(ItemStack itemStack) {
        _itemStack = itemStack;
    }

    public ItemStack getCursorStack() {
        return _cursorStack;
    }

    void setCursorStack(ItemStack cursorStack) {
        _cursorStack = cursorStack;
    }

    public ItemStack getSlotStack() {
        return _slotStack;
    }

    void setSlotStack(ItemStack slotStack) {
        _slotStack = slotStack;
    }


    public Inventory getInventory() {
        return _inventory;
    }

    void setInventory(Inventory inventory) {
        _inventory = inventory;
    }

    public Inventory getTopInventory() {
        return _topInventory;
    }

    void setTopInventory(Inventory inventory) {
        _topInventory = inventory;
    }

    public Inventory getBottomInventory() {
        return _bottomInventory;
    }

    void setBottomInventory(Inventory inventory) {
        _bottomInventory = inventory;
    }

    public int getRawSlot() {
        return _rawSlot;
    }

    void setRawSlot(int rawSlot) {
        _rawSlot = rawSlot;
    }

    public int getSlot() {
        return _slot;
    }

    void setSlot(int slot) {
        _slot = slot;
    }

    public enum ItemStackSource {
        SLOT,
        CURSOR
    }
}
