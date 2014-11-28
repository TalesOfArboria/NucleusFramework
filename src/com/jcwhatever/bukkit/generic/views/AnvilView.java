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

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.events.bukkit.AnvilItemRenameEvent;
import com.jcwhatever.bukkit.generic.items.ItemFilterManager;
import com.jcwhatever.bukkit.generic.utils.ItemStackUtils;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.views.InventoryActionInfoHandler.InventoryActionInfo;
import com.jcwhatever.bukkit.generic.views.InventoryActionInfoHandler.ViewActionOrder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Represents an Anvil GUI view.
 */
public class AnvilView extends AbstractView {

    private static EventListener _eventListener;

    private ItemFilterManager _filterManager;

    @Override
    protected void onInit(String name, IDataNode dataNode, ViewManager viewManager) {

        _filterManager = new ItemFilterManager(viewManager.getPlugin(), dataNode.getNode("item-filter"));

        if (_eventListener == null) {
            _eventListener = new EventListener();
            Bukkit.getPluginManager().registerEvents(_eventListener, GenericsLib.getLib());
        }
    }

    @Override
    public InventoryType getInventoryType() {
        return InventoryType.ANVIL;
    }

    @Override
    public ViewType getViewType() {
        return ViewType.ANVIL;
    }

    @Override
    public void dispose() {
        // do nothing
    }

    @Override
    protected void onLoadSettings(IDataNode dataNode) {
        // do nothing
    }

    /**
     * Get the filter manager that specifies what can be created
     * or repaired on the anvil view.
     */
    public ItemFilterManager getFilterManager() {
        return _filterManager;
    }


    @Override
    protected ViewInstance onCreateInstance(Player p, @Nullable ViewInstance previous, ViewMeta sessionMeta, ViewMeta meta) {
        return new AnvilInstance(this, previous, p, sessionMeta, meta);
    }

    /**
     * Instance of an anvil GUI view shown to a player.
     */
    public class AnvilInstance extends ViewInstance {

        /**
         * Constructor.
         *
         * @param view         The owning view.
         * @param previous     The previous view the player was looking at.
         * @param p            The player.
         * @param sessionMeta  The sessionMeta.
         * @param initialMeta  The initial instance meta.
         */
        public AnvilInstance(IView view, @Nullable ViewInstance previous, Player p, ViewMeta sessionMeta, ViewMeta initialMeta) {
            super(view, previous, p, sessionMeta, initialMeta);
        }

        @Override
        @Nullable
        protected InventoryView onShow(ViewMeta meta) {

            if (getSourceBlock() == null)
                return null;

            Location loc = getSourceBlock().getLocation();
            try {

                Player p = getPlayer();

                Method getHandle = p.getClass().getDeclaredMethod("getHandle");

                Object entityHuman = getHandle.invoke(p);

                Method openAnvil = entityHuman.getClass().getDeclaredMethod("openAnvil", int.class, int.class, int.class);

                openAnvil.invoke(entityHuman, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

            }
            catch (NoSuchMethodException | SecurityException | IllegalAccessException |
                    IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        @Nullable
        public ViewResult getResult() {
            return null; // no result is returned
        }

        @Override
        @Nullable
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


    /*
     * Global Bukkit event listener for AnvilView
     */
    private static class EventListener implements Listener {

        @EventHandler(priority = EventPriority.HIGHEST)
        private void onAnvilItemRepair(AnvilItemRenameEvent event) {
            Player p = event.getPlayer();

            ViewInstance current = ViewManager.getCurrent(p);

            if (current instanceof AnvilInstance) {

                AnvilView view = (AnvilView)current.getView();
                ItemStack result = event.getRenamedItem();

                if (!view.getFilterManager().isValidItem(result)) {
                    InventoryView invView = current.getInventoryView();
                    if (invView != null) {
                        ItemStack stack = result.clone();
                        ItemStackUtils.setLore(stack, ChatColor.RED + "Not repairable here.");
                        invView.setItem(0, stack);
                    }
                }
            }
        }
    }



}
