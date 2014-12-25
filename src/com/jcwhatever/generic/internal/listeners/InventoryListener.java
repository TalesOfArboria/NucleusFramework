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


package com.jcwhatever.generic.internal.listeners;

import com.jcwhatever.generic.GenericsLib;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

public final class InventoryListener implements Listener {

    @EventHandler
    private void onBrew(BrewEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onCraftItem(CraftItemEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onFurnaceBurn(FurnaceBurnEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onFurnaceExtract(FurnaceExtractEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onFurnaceSmelt(FurnaceSmeltEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onInventoryCreative(InventoryCreativeEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onInventoryDrag(InventoryDragEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onInventoryInteract(InventoryInteractEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onInventoryMoveItem(InventoryMoveItemEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onInventoryOpen(InventoryOpenEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onInventoryPickupItem(InventoryPickupItemEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPrepareItemCraft(PrepareItemCraftEvent event) {

        GenericsLib.getEventManager().call(event);
    }

}
