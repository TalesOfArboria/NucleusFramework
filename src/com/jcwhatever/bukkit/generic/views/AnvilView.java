/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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
import com.jcwhatever.bukkit.generic.items.ItemStackHelper;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AnvilView extends AbstractView {

    private static EventListener _eventListener;

    private ItemFilterManager _filterManager;



    @Override
    protected void onInit(String name, IDataNode dataNode, ViewManager viewManager) {

        _filterManager = new ItemFilterManager(viewManager.getPlugin(), dataNode.getNode("item-filter"));

        if (_eventListener == null) {
            _eventListener = new EventListener();
            Bukkit.getPluginManager().registerEvents(_eventListener, GenericsLib.getPlugin());
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


    public ItemFilterManager getFilterManager() {
        return _filterManager;
    }

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
                        ItemStackHelper.setLore(stack, ChatColor.RED + "Not repairable here.");
                        invView.setItem(0, stack);
                    }
                }
            }
        }
    }

    @Override
    protected ViewInstance onCreateInstance(Player p, ViewInstance previous, ViewMeta sessionMeta, ViewMeta meta) {
        AnvilInstance instance = new AnvilInstance(this, previous, p, sessionMeta, meta);
        return instance;
    }



    public class AnvilInstance extends ViewInstance {

        public AnvilInstance(IView view, ViewInstance previous, Player p, ViewMeta sessionMeta, ViewMeta initialMeta) {
            super(view, previous, p, sessionMeta, initialMeta);
        }

        @Override
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
            catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            catch (SecurityException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public ViewResult getResult() {
            return null;
        }

        @Override
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



}
