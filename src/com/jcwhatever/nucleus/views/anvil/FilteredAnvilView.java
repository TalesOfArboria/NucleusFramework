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
import com.jcwhatever.nucleus.events.anvil.AnvilItemRenameEvent;
import com.jcwhatever.nucleus.events.anvil.AnvilItemRepairEvent;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.utils.items.ItemFilter;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.views.View;
import com.jcwhatever.nucleus.views.ViewCloseReason;
import com.jcwhatever.nucleus.views.ViewOpenReason;
import com.jcwhatever.nucleus.views.ViewSession;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

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
     * @param plugin         The owning plugin.
     * @param filterManager  The item filter manager.
     */
    public FilteredAnvilView(Plugin plugin, @Nullable ItemFilter filterManager) {
        super(plugin);

        _filterManager = filterManager;

        if (_eventListener == null) {
            _eventListener = new AnvilEventListener();
            Bukkit.getPluginManager().registerEvents(_eventListener, Nucleus.getPlugin());
        }
    }

    @Nullable
    public ItemFilter getFilterManager() {
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
        return NucLang.get(_NOT_REPAIRABLE);
    }

    /**
     * Anvil event listener.
     */
    static class AnvilEventListener implements Listener {

        @EventHandler(priority = EventPriority.MONITOR)
        private void onAnvilItemRepair(AnvilItemRepairEvent event) {
            check(event.getPlayer(), event.getItem());
        }

        @EventHandler(priority = EventPriority.MONITOR)
        private void onAnvilItemRename(AnvilItemRenameEvent event) {
            check(event.getPlayer(), event.getItem());
        }

        private void check(Player p, ItemStack repaired) {

            ViewSession session = _anvilMap.get(p);
            if (session == null)
                return;

            View current = session.getCurrent();
            if (current == null)
                return;

            if (!(current instanceof FilteredAnvilView))
                return;

            FilteredAnvilView view = (FilteredAnvilView)current;

            ItemFilter filter = view.getFilterManager();
            if (filter == null)
                return;

            if (!filter.isValid(repaired)) {
                InventoryView invView = current.getInventoryView();
                if (invView != null) {
                    ItemStack stack = repaired.clone();
                    ItemStackUtils.setLore(stack, view.getDenyMessage());
                    invView.setItem(0, stack);
                }
            }
        }
    }
}
