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


package com.jcwhatever.bukkit.generic.views;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

/**
 * Handles click events on items within a menu view instance.
 */
public class InventoryActionInfoHandler {

    private InventoryClickEvent _clickEvent;
    private Inventory _inventory;
    private InventoryAction _inventoryAction;
    private int _rawSlot;
    private int _slot;

    private InventoryActionInfo _primaryInfo;
    private InventoryActionInfo _secondaryInfo;

    private ViewAction _primaryAction;
    private ViewAction _secondaryAction;

    private ItemStack _slotStack;
    private ItemStack _cursorStack;


    /**
     * Generic Action info about the process being performed.
     */
    public enum ViewAction {
        NONE,
        ITEMS_PLACED,
        ITEMS_PICKUP,
        ITEMS_DROPPED,
        LOWER_PLACED,
        LOWER_PICKUP
    }

    /**
     * Labels view action order/priority
     */
    public enum ViewActionOrder {
        PRIMARY,
        SECONDARY
    }

    /**
     * Constructor.
     *
     * <p>
     *     Compiles information from event.
     * </p>
     *
     * @param event  The parent InventoryClickEvent.
     */
    public InventoryActionInfoHandler(InventoryClickEvent event) {
        PreCon.notNull(event);

        _clickEvent = event;
        _inventory = event.getInventory();
        _inventoryAction = event.getAction();
        _rawSlot = event.getRawSlot();
        _slot = event.getSlot();
        _primaryAction = ViewAction.NONE;
        _secondaryAction = ViewAction.NONE;
        _slotStack = event.getCurrentItem();
        _cursorStack = event.getCursor().clone();

        boolean isUpperInventory = _rawSlot < event.getView().getTopInventory().getContents().length;

        switch (event.getAction()) {
            case PLACE_SOME:
            case PLACE_ALL:
            case PLACE_ONE:
            case SWAP_WITH_CURSOR:
                _primaryAction = isUpperInventory
                        ? ViewAction.ITEMS_PLACED
                        : ViewAction.LOWER_PLACED;
                break;

            case MOVE_TO_OTHER_INVENTORY:
                if (isUpperInventory) {
                    _primaryAction = ViewAction.ITEMS_PICKUP;
                    _secondaryAction = ViewAction.LOWER_PLACED;
                } else {
                    _primaryAction = ViewAction.LOWER_PICKUP;
                    _secondaryAction = ViewAction.ITEMS_PLACED;
                }
                break;

            case PICKUP_ALL:
            case PICKUP_HALF:
            case PICKUP_ONE:
            case PICKUP_SOME:
                _primaryAction = isUpperInventory
                        ? ViewAction.ITEMS_PICKUP
                        : ViewAction.LOWER_PICKUP;
                break;

            case DROP_ALL_CURSOR:
            case DROP_ONE_CURSOR:
                _primaryAction = ViewAction.ITEMS_DROPPED;
                break;

            default:
                _primaryAction = ViewAction.NONE;
                break;

        }

        ItemStack stack = getStack(_primaryAction, _slotStack, _cursorStack);

        if (_primaryAction != ViewAction.NONE) {
            _primaryInfo = new InventoryActionInfo(stack, _primaryAction, ViewActionOrder.PRIMARY);
        }

        if (_secondaryAction != ViewAction.NONE) {
            _secondaryInfo = new InventoryActionInfo(stack, _secondaryAction, ViewActionOrder.SECONDARY);
        }
    }

    /**
     * Get the primary action info. The primary action
     * is the action performed by the player during the event.
     * (i.e. The event is fired by the player dropping an item into
     * an inventory slot, the drop is primary, the pickup is secondary)
     */
    @Nullable
    public InventoryActionInfo getPrimaryInfo() {
        return _primaryInfo;
    }

    /**
     * Get the secondary action info. This is the action
     * that may have been taken before the player performed
     * the current action.
     */
    @Nullable
    public InventoryActionInfo getSecondaryInfo() {
        return _secondaryInfo;
    }

    /**
     * Get the primary view action.
     */
    public ViewAction getPrimaryViewAction() {
        return _primaryAction;
    }

    /**
     * Get the secondart view action.
     */
    public ViewAction getSecondaryViewAction() {
        return _secondaryAction;
    }

    /*
     * Get the stack relevant to the specified view action.
     */
    private ItemStack getStack(ViewAction viewAction, ItemStack slotStack, ItemStack cursorStack) {
        switch (viewAction) {
            case ITEMS_PLACED:
                // fall through
            case LOWER_PLACED:
                // fall through
            case ITEMS_DROPPED:
                if (_inventoryAction == InventoryAction.MOVE_TO_OTHER_INVENTORY)
                    return slotStack;

                return cursorStack;
            default:
                return slotStack;
        }
    }

    /**
     * Provides information about an inventory click event.
     */
    public class InventoryActionInfo {

        //private ItemStack _slotStack;
        //private ItemStack _cursorStack;
        private ItemStack _stack;
        private ViewAction _viewAction;
        private ViewActionOrder _actionOrder;

        /**
         * Constructor.
         *
         * @param stack        The item stack for the action.
         * @param viewAction   The view action.
         * @param actionOrder  The view action order of the info.
         */
        public InventoryActionInfo(ItemStack stack, ViewAction viewAction, ViewActionOrder actionOrder) {
            _stack = stack;
            _viewAction = viewAction;
            _actionOrder = actionOrder;
        }

        /**
         * Get the item stack from the slot clicked on
         * in the primary action event.
         */
        public ItemStack getSlotStack() {
            return _slotStack;
        }

        /**
         * Get the item stack from the cursor.
         */
        public ItemStack getCursorStack() {
            return _cursorStack;
        }

        /**
         * Get the item stack relevant to the event.
         */
        public ItemStack getStack() {
            return _stack;
        }

        /**
         * Get the event inventory.
         */
        public Inventory getInventory() {
            return _inventory;
        }

        /**
         * Get the event inventory action of
         * the primary action.
         */
        public InventoryAction getInventoryAction() {
            return _inventoryAction;
        }

        /**
         * Get the view action to be taken.
         */
        public ViewAction getViewAction() {
            return _viewAction;
        }

        /**
         * Determine if the action info instance
         * represents the primary event action or
         * the the secondary event action.
         */
        public ViewActionOrder getViewActionOrder() {
            return _actionOrder;
        }

        /**
         * Get the event raw slot for
         * the primary action.
         */
        public int getRawSlot() {
            return _rawSlot;
        }

        /**
         * Get the event slot for the
         * primary action.
         */
        public int getSlot() {
            return _slot;
        }

        /**
         * Get the inventory at the top of the view.
         */
        public Inventory getTopInventory() {
            return _clickEvent.getView().getTopInventory();
        }

        /**
         * Get the players inventory.
         */
        public Inventory getBottomInventory() {
            return _clickEvent.getView().getBottomInventory();
        }
    }
}
