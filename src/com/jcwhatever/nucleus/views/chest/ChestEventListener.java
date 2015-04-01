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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.views.View;
import com.jcwhatever.nucleus.views.ViewSession;
import com.jcwhatever.nucleus.views.chest.ChestEventInfo.ItemStackSource;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.server.PluginDisableEvent;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Listens to events related to the {@link ChestView}.
 */
class ChestEventListener implements Listener {

    private static ChestEventListener _instance;

    /**
     * Register a {@link ChestView} instance so its events can be handled.
     */
    static void register(ChestView view) {

        if (_instance == null) {
            _instance = new ChestEventListener();
            Bukkit.getPluginManager().registerEvents(_instance, Nucleus.getPlugin());
        }

        _instance._chestSessionMap.put(view.getPlayer(), view.getViewSession());
    }

    /**
     * Unregister a {@link ChestView} instance.
     */
    static void unregister(ChestView view) {
        PreCon.notNull(view);

        _instance._chestSessionMap.remove(view.getPlayer());
    }

    private final Map<Entity, ViewSession> _chestSessionMap = new WeakHashMap<>(20);

    /*
     * Inventory Click Event
     */
    @EventMethod
    private void onInventoryClick(InventoryClickEvent event) {

        ViewSession session = _chestSessionMap.get(event.getWhoClicked());
        if (session == null)
            return;

        // get current chest view instance
        View current = session.getCurrent();
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
                // fall through
            case PLACE_ALL:
                // fall through
            case PLACE_ONE:
                // fall through
            case SWAP_WITH_CURSOR:
                action = isUpperInventory
                        ? InventoryItemAction.PLACED_TOP
                        : InventoryItemAction.PLACED_BOTTOM;
                break;

            case MOVE_TO_OTHER_INVENTORY:
                if (isUpperInventory) {
                    action = InventoryItemAction.PICKUP_TOP;
                    secondaryAction = InventoryItemAction.PLACED_BOTTOM;
                } else {
                    action = InventoryItemAction.PICKUP_BOTTOM;
                    secondaryAction = InventoryItemAction.PLACED_TOP;
                }
                break;

            case PICKUP_ALL:
                // fall through
            case PICKUP_HALF:
                // fall through
            case PICKUP_ONE:
                // fall through
            case PICKUP_SOME:
                action = isUpperInventory
                        ? InventoryItemAction.PICKUP_TOP
                        : InventoryItemAction.PICKUP_BOTTOM;
                break;

            case DROP_ALL_CURSOR:
                // fall through
            case DROP_ONE_CURSOR:
                action = InventoryItemAction.DROPPED;
                break;

            default:
                return;
        }

        ItemStackSource itemStackSource = getRelevantStack(action, event);

        // generate event info object
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
     * Reset listener instance if NucleusFramework is disabled (i.e Server reset)
     */
    @EventMethod
    private void onNucleusDisabled(PluginDisableEvent event) {
        if (event.getPlugin() == Nucleus.getPlugin())
            _instance = null;
    }

    /*
     * Get the item stack that is relevant to the click event.
     */
    private ItemStackSource getRelevantStack(InventoryItemAction action, InventoryClickEvent event) {
        switch (action) {
            case PLACED_TOP:
                // fall through
            case PLACED_BOTTOM:
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
