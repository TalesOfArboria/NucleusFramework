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


package com.jcwhatever.bukkit.generic.views.old;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.items.ItemFilterManager;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.ItemStackUtils;
import com.jcwhatever.bukkit.generic.views.data.ViewArguments;
import com.jcwhatever.bukkit.generic.views.old.InventoryActionInfoHandler.InventoryActionInfo;
import com.jcwhatever.bukkit.generic.views.old.InventoryActionInfoHandler.ViewActionOrder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import javax.annotation.Nullable;

/**
 * Represents a crafting workbench GUI view.
 */
public class WorkbenchView extends AbstractView {

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
        return InventoryType.WORKBENCH;
    }

    @Override
    public ViewType getViewType() {
        return ViewType.WORKBENCH;
    }

    @Override
    public boolean isDisposed() {
        return false;
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
     * Get the filter manager used to determine which items
     * can be crafted in the workbench view.
     */
    public ItemFilterManager getFilterManager() {
        return _filterManager;
    }

    private static class EventListener implements Listener {

        @EventHandler(priority = EventPriority.HIGHEST)
        private void onPrepareItemCraft(PrepareItemCraftEvent event) {
            List<HumanEntity> viewers = event.getViewers();
            if (viewers.isEmpty())
                return;

            HumanEntity entity = viewers.get(0);
            if (!(entity instanceof Player))
                return;

            Player p = (Player)entity;

            ViewInstance instance = ViewManager.getCurrent(p);
            if (instance == null)
                return;

            IView view = instance.getView();

            if (view instanceof WorkbenchView) {

                WorkbenchView workbench = (WorkbenchView)view;
                ItemStack result = event.getRecipe().getResult();

                if (!workbench.getFilterManager().isValidItem(result)) {
                    InventoryView invView = instance.getInventoryView();
                    if (invView != null) {
                        ItemStack stack = result.clone();
                        ItemStackUtils.setLore(stack, ChatColor.RED + "Not craftable here.");
                        invView.setItem(0, stack);
                    }
                }
            }


        }

        @EventHandler(priority = EventPriority.HIGHEST)
        private void onCraftItem(CraftItemEvent event) {
            HumanEntity entity = event.getWhoClicked();
            if (!(entity instanceof Player))
                return;

            Player p = (Player)entity;

            ViewInstance instance = ViewManager.getCurrent(p);
            if (instance == null)
                return;

            IView view = instance.getView();

            if (view instanceof WorkbenchView) {

                WorkbenchView workbench = (WorkbenchView)view;
                ItemStack result = event.getRecipe().getResult();

                if (!workbench.getFilterManager().isValidItem(result)) {
                    tellNoCraftMessage(p, workbench.getViewManager());
                    event.setCancelled(true);
                }
            }

        }

        private void tellNoCraftMessage(Player p, ViewManager viewManager) {

        }
    }

    @Override
    protected ViewInstance onCreateInstance(Player p, @Nullable ViewInstance previous,
                                            ViewArguments sessionMeta, ViewArguments instanceMeta) {
        return new WorkbenchInstance(this, previous, p, sessionMeta, instanceMeta);
    }

    /**
     * The view instance for a specific player
     */
    public class WorkbenchInstance extends ViewInstance {

        /**
         * Constructor.
         *
         * @param view          The owning view.
         * @param previous      The previous view instance the player was looking at.
         * @param p             The player.
         * @param sessionMeta   The players session meta.
         * @param instanceMeta  The instance meta.
         */
        public WorkbenchInstance(IView view, @Nullable ViewInstance previous, Player p,
                                 ViewArguments sessionMeta, ViewArguments instanceMeta) {
            super(view, previous, p, sessionMeta, instanceMeta);
        }

        @Override
        @Nullable
        public ViewResult getResult() {
            return null; // does not return a result
        }

        @Override
        protected InventoryView onShow(ViewArguments meta) {
            return getPlayer().openWorkbench(
                    getSourceBlock() != null
                            ? getSourceBlock().getLocation()
                            : getPlayer().getLocation(), true);
        }

        @Override
        protected InventoryView onShowAsPrev(ViewArguments instanceMeta, ViewResult result) {
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
