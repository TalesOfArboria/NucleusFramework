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

/* 
 * 
 */
public enum InventoryItemAction {

    NONE         (ItemAction.NONE,    InventoryPosition.NONE),
    PLACED_UPPER (ItemAction.PLACED,  InventoryPosition.UPPER),
    PLACED_LOWER (ItemAction.PLACED,  InventoryPosition.LOWER),
    PICKUP_UPPER (ItemAction.PICKUP,  InventoryPosition.UPPER),
    PICKUP_LOWER (ItemAction.PICKUP,  InventoryPosition.LOWER),
    DROPPED      (ItemAction.DROPPED, InventoryPosition.NONE);

    public enum ItemAction {
        NONE,
        PLACED,
        PICKUP,
        DROPPED
    }

    public enum InventoryPosition {
        NONE,
        UPPER,
        LOWER
    }

    private final ItemAction _itemAction;
    private final InventoryPosition _inventoryAction;

    InventoryItemAction(ItemAction itemAction, InventoryPosition inventoryAction) {
        _itemAction = itemAction;
        _inventoryAction = inventoryAction;
    }

    public ItemAction getItemAction() {
        return _itemAction;
    }

    public InventoryPosition getInventoryPosition() {
        return _inventoryAction;
    }
}
