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

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.IGenericsEventListener;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.views.IView;
import com.jcwhatever.bukkit.generic.views.ViewSession;
import com.jcwhatever.bukkit.generic.views.chest.ChestEventInfo.ItemStackSource;

import org.bukkit.entity.Entity;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;
import java.util.WeakHashMap;

/*
 * 
 */
class ChestEventListener implements IGenericsEventListener {

    private static ChestEventListener _instance;

    static void register(ChestView view) {

        if (_instance == null) {
            _instance = new ChestEventListener();
            GenericsLib.getEventManager().register(_instance);
        }

        _instance._chestSessionMap.put(view.getPlayer(), view.getViewSession());
    }

    static void unregister(ChestView view) {
        PreCon.notNull(view);

        _instance._chestSessionMap.remove(view.getPlayer());
    }

    private final Map<Entity, ViewSession> _chestSessionMap = new WeakHashMap<>(20);

    /*
     * Inventory Click Event
     */
    @GenericsEventHandler
    private void onInventoryClick(InventoryClickEvent event) {

        ViewSession session = _chestSessionMap.get(event.getWhoClicked());
        if (session == null)
            return;

        IView current = session.getCurrentView();

        if (!(current instanceof ChestView))
            return;

        ChestView view = (ChestView)current;

        ChestEventAction allow;
        InventoryItemAction action;
        InventoryItemAction secondaryAction =  InventoryItemAction.NONE;

        boolean isUpperInventory = event.getRawSlot() <
                event.getView().getTopInventory().getContents().length;

        // convert InventoryAction to InventoryItemAction
        switch (event.getAction()) {
            case PLACE_SOME:
            case PLACE_ALL:
            case PLACE_ONE:
            case SWAP_WITH_CURSOR:
                action = isUpperInventory
                        ? InventoryItemAction.PLACED_UPPER
                        : InventoryItemAction.PLACED_LOWER;
                break;

            case MOVE_TO_OTHER_INVENTORY:
                if (isUpperInventory) {
                    action = InventoryItemAction.PICKUP_UPPER;
                    secondaryAction = InventoryItemAction.PLACED_LOWER;
                } else {
                    action = InventoryItemAction.PICKUP_LOWER;
                    secondaryAction = InventoryItemAction.PLACED_UPPER;
                }
                break;

            case PICKUP_ALL:
            case PICKUP_HALF:
            case PICKUP_ONE:
            case PICKUP_SOME:
                action = isUpperInventory
                        ? InventoryItemAction.PICKUP_UPPER
                        : InventoryItemAction.PICKUP_LOWER;
                break;

            case DROP_ALL_CURSOR:
            case DROP_ONE_CURSOR:
                action = InventoryItemAction.DROPPED;
                break;

            default:
                return;
        }

        ItemStackSource itemStackSource = getRelevantStack(action, event);

        ChestEventInfo info = new ChestEventInfo();
        info.setInventoryAction(event.getAction());
        info.setAction(action);
        info.setSecondaryAction(secondaryAction);
        info.setInventory(event.getInventory());
        info.setItemStackSource(itemStackSource);
        info.setCursorStack(event.getCursor());
        info.setSlotStack(event.getCurrentItem());
        info.setItemStack(
                itemStackSource == ItemStackSource.SLOT
                        ? event.getCurrentItem()
                        : event.getCursor());

        info.setTopInventory(event.getView().getTopInventory());
        info.setBottomInventory(event.getView().getBottomInventory());
        info.setRawSlot(event.getRawSlot());
        info.setSlot(event.getSlot());

        // run appropriate method for the action being taken
        switch (action.getItemAction()) {
            case PLACED:
                allow = view.onItemsPlaced(info);
                break;
            case PICKUP:
                allow = view.onItemsPickup(info);
                break;
            case DROPPED:
                allow = view.onItemsDropped(info);
                break;
            default:
                throw new AssertionError();
        }

        // cancel event
        if (allow == ChestEventAction.DENY) {
            event.setCancelled(true);
        }
    }

    /*
     * Get the item stack that is relevant to the click event.
     */
    private ItemStackSource getRelevantStack(InventoryItemAction action, InventoryClickEvent event) {
        switch (action) {
            case PLACED_UPPER:
                // fall through
            case PLACED_LOWER:
                // fall through
            case DROPPED:
                if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                    return ItemStackSource.SLOT;
                }

                return ItemStackSource.CURSOR;
            default:
                return ItemStackSource.SLOT;
        }
    }

}
