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

/**
 * Specifies the action taken in an inventory event.
 */
public enum InventoryItemAction {

    /**
     * No action specified.
     */
    NONE          (ItemAction.NONE,    InventoryPosition.NONE),
    /**
     * 1 or more items was placed in the views
     * top inventory.
     */
    PLACED_TOP    (ItemAction.PLACED,  InventoryPosition.TOP),
    /**
     * 1 or more items was placed in the views
     * bottom inventory.
     */
    PLACED_BOTTOM (ItemAction.PLACED,  InventoryPosition.BOTTOM),
    /**
     * 1 or more items was picked up from the views
     * top inventory.
     */
    PICKUP_TOP    (ItemAction.PICKUP,  InventoryPosition.TOP),
    /**
     * 1 or more items was picked up from the views
     * bottom inventory.
     */
    PICKUP_BOTTOM (ItemAction.PICKUP,  InventoryPosition.BOTTOM),
    /**
     * 1 or more items was dropped out of the view.
     */
    DROPPED       (ItemAction.DROPPED, InventoryPosition.NONE);

    /**
     * Specifies an action taken in an inventory event
     * less the specifics of inventory position.
     */
    public enum ItemAction {
        /**
         * No action specified.
         */
        NONE,
        /**
         * 1 or more items was placed into the inventory.
         */
        PLACED,
        /**
         * 1 or more items was picked up from the inventory.
         */
        PICKUP,
        /**
         * 1 or more items was dropped out of the inventory.
         */
        DROPPED
    }

    /**
     * Specifies which inventory in the inventory view is relevant.
     */
    public enum InventoryPosition {
        /**
         * Not specified.
         */
        NONE,
        /**
         * The top inventory.
         */
        TOP,
        /**
         * The bottom inventory.
         */
        BOTTOM
    }

    private final ItemAction _itemAction;
    private final InventoryPosition _inventoryAction;

    InventoryItemAction(ItemAction itemAction, InventoryPosition inventoryAction) {
        _itemAction = itemAction;
        _inventoryAction = inventoryAction;
    }

    /**
     * Get the item action less inventory position details.
     */
    public ItemAction getItemAction() {
        return _itemAction;
    }

    /**
     * Get which inventory in the view is relevant.
     */
    public InventoryPosition getInventoryPosition() {
        return _inventoryAction;
    }
}
