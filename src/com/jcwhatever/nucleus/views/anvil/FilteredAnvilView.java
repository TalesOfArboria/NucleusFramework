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

package com.jcwhatever.nucleus.views.anvil;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.events.anvil.AnvilEnchantItemEvent;
import com.jcwhatever.nucleus.events.anvil.AnvilRenameItemEvent;
import com.jcwhatever.nucleus.events.anvil.AnvilRepairItemEvent;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.utils.items.ItemFilter;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import com.jcwhatever.nucleus.views.View;
import com.jcwhatever.nucleus.views.ViewCloseReason;
import com.jcwhatever.nucleus.views.ViewOpenReason;
import com.jcwhatever.nucleus.views.ViewSession;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Represents an anvil that can allow or disallow items.
 */
public class FilteredAnvilView extends AnvilView {

    @Localizable static final String _NOT_REPAIRABLE =
            "{RED}Not repairable here.";

    private static AnvilEventListener _eventListener;
    private static Map<Entity, ViewSession> _anvilMap = new WeakHashMap<>(20);

    private final ItemFilter _filterManager;

    /**
     * Constructor.
     *
     * @param plugin      The owning plugin.
     * @param itemFilter  The item filter manager.
     */
    public FilteredAnvilView(Plugin plugin, @Nullable ItemFilter itemFilter) {
        super(plugin);

        _filterManager = itemFilter;

        if (_eventListener != null)
            return;

        _eventListener = new AnvilEventListener();
        Bukkit.getPluginManager().registerEvents(_eventListener, Nucleus.getPlugin());
    }

    @Nullable
    public ItemFilter getItemFilter() {
        return _filterManager;
    }

    @Override
    protected boolean openView(ViewOpenReason reason) {
        if (super.openView(reason)) {
            _anvilMap.put(getPlayer(), getViewSession());
            return true;
        }
        return false;
    }

    @Override
    protected void onClose(ViewCloseReason reason) {
        super.onClose(reason);

        _anvilMap.remove(getPlayer());
    }

    /**
     * Invoked to get the craft deny message.
     */
    protected String getDenyMessage() {
        return NucLang.get(getPlugin(), _NOT_REPAIRABLE);
    }

    /**
     * Anvil event listener.
     */
    static class AnvilEventListener implements Listener {

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        private void onAnvilRepair(AnvilRepairItemEvent event) {
            if (!isValid(event.getPlayer(), event.getItem())) {
                event.setCancelled(true);
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        private void onAnvilEnchant(AnvilEnchantItemEvent event) {
            if (!isValid(event.getPlayer(), event.getItem())) {
                event.setCancelled(true);
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        private void onAnvilItemRename(AnvilRenameItemEvent event) {
            if (!isValid(event.getPlayer(), event.getItem())) {
                event.setCancelled(true);
            }
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        private void onAnvilClick(InventoryClickEvent event) {

            if (event.getView().getType() != InventoryType.ANVIL)
                return;

            final FilteredAnvilView view = getAnvil(event.getWhoClicked());
            if (view == null)
                return;

            ItemFilter filter = view.getItemFilter();
            if (filter == null)
                return;

            if (!event.getView().getTopInventory().equals(event.getClickedInventory()))
                return;

            ItemStack input = event.getView().getTopInventory().getItem(0);
            if (input == null || input.getType() == Material.AIR)
                return;

            if (!filter.isValid(input)) {
                final InventoryView invView = event.getView();
                if (invView != null) {

                    Scheduler.runTaskLater(Nucleus.getPlugin(), 2, new Runnable() {
                        @Override
                        public void run() {

                            ItemStack output = invView.getTopInventory().getItem(2);
                            if (output == null || output.getType() == Material.AIR)
                                return;

                            ItemStackUtils.setLore(output, view.getDenyMessage());
                            invView.setItem(2, output.clone());
                        }
                    });

                }
            }
        }

        @Nullable
        private FilteredAnvilView getAnvil(HumanEntity entity) {
            if (!(entity instanceof Player))
                return null;

            Player player = (Player)entity;

            ViewSession session = _anvilMap.get(player);
            if (session == null)
                return null;

            View current = session.getCurrent();
            if (current == null)
                return null;

            if (!(current instanceof FilteredAnvilView))
                return null;

            return (FilteredAnvilView)current;
        }

        private boolean isValid(Player p, ItemStack repaired) {

            FilteredAnvilView view = getAnvil(p);
            if (view == null)
                return true;

            ItemFilter filter = view.getItemFilter();
            return filter == null || filter.isValid(repaired);
        }
    }
}
