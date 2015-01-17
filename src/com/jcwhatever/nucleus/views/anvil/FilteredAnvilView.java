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
import com.jcwhatever.nucleus.events.manager.EventListener;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.utils.language.Localizable;
import com.jcwhatever.nucleus.utils.items.ItemFilterManager;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import com.jcwhatever.nucleus.views.View;
import com.jcwhatever.nucleus.views.ViewSession;
import com.jcwhatever.nucleus.views.ViewCloseReason;
import com.jcwhatever.nucleus.views.ViewOpenReason;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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

    @Localizable static final String _NOT_REPAIRABLE = "{RED}Not repairable here.";

    private static AnvilEventListener _eventListener;
    private static Map<Entity, ViewSession> _anvilMap = new WeakHashMap<>(20);

    private final ItemFilterManager _filterManager;

    /**
     * Constructor.
     *
     * @param plugin         The owning plugin.
     * @param filterManager  The item filter manager.
     */
    protected FilteredAnvilView(Plugin plugin, @Nullable ItemFilterManager filterManager) {
        super(plugin);

        _filterManager = filterManager;

        if (_eventListener == null) {
            _eventListener = new AnvilEventListener(Nucleus.getPlugin());
            Nucleus.getEventManager().register(_eventListener);
        }
    }

    @Nullable
    public ItemFilterManager getFilterManager() {
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
     * Anvil event listener.
     */
    static class AnvilEventListener extends EventListener {

        public AnvilEventListener(Plugin plugin) {
            super(plugin);
        }

        private void onAnvilItemRepair(AnvilItemRepairEvent event) {
            check(event.getPlayer(), event.getItem());
        }

        private void onAnvilItemRename(AnvilItemRenameEvent event) {
            check(event.getPlayer(), event.getItem());
        }

        private void check(Player p, ItemStack repaired) {

            ViewSession session = _anvilMap.get(p);
            if (session == null)
                return;

            View current = session.getCurrentView();
            if (current == null)
                return;

            if (!(current instanceof FilteredAnvilView))
                return;

            FilteredAnvilView view = (FilteredAnvilView)current;

            ItemFilterManager filter = view.getFilterManager();
            if (filter == null)
                return;

            if (!filter.isValidItem(repaired)) {
                InventoryView invView = current.getInventoryView();
                if (invView != null) {
                    ItemStack stack = repaired.clone();
                    ItemStackUtils.setLore(stack, NucLang.get(_NOT_REPAIRABLE));
                    invView.setItem(0, stack);
                }
            }
        }

    }
}
