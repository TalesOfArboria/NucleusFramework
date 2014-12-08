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

package com.jcwhatever.bukkit.generic.views.anvil;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.events.IGenericsEventListener;
import com.jcwhatever.bukkit.generic.events.bukkit.AnvilItemRenameEvent;
import com.jcwhatever.bukkit.generic.events.bukkit.AnvilItemRepairEvent;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.items.ItemFilterManager;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.utils.ItemStackUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.views.IView;
import com.jcwhatever.bukkit.generic.views.IViewFactory;
import com.jcwhatever.bukkit.generic.views.ViewSession;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.WeakHashMap;

/*
 * 
 */
public class FilteredAnvilViewFactory extends AnvilViewFactory {

    @Localizable static final String _NOT_REPAIRABLE = "{RED}Not repairable here.";

    private static EventListener _listener;
    private static Map<Entity, ViewSession> _anvilMap = new WeakHashMap<>(20);

    private final ItemFilterManager _filterManager;

    protected FilteredAnvilViewFactory(Plugin plugin, String name, ItemFilterManager filterManager) {
        super(plugin, name);

        PreCon.notNull(filterManager);

        _filterManager = filterManager;
    }

    public ItemFilterManager getFilterManager() {
        return _filterManager;
    }

    @Override
    protected void onDispose() {
        super.onDispose();
    }

    static void register(FilteredAnvilView view) {
        if (_listener == null) {
            _listener = new EventListener();
            GenericsLib.getEventManager().register(_listener);
        }

        _anvilMap.put(view.getPlayer(), view.getViewSession());
    }

    static void unregister(FilteredAnvilView view) {
        _anvilMap.remove(view.getPlayer());
    }

    static class EventListener implements IGenericsEventListener {

        private void onAnvilItemRepair(AnvilItemRepairEvent event) {
            check(event.getPlayer(), event.getRepairedItem());
        }

        private void onAnvilItemRename(AnvilItemRenameEvent event) {
            check(event.getPlayer(), event.getRenamedItem());
        }

        private void check(Player p, ItemStack repaired) {

            ViewSession session = _anvilMap.get(p);
            if (session == null)
                return;

            IView current = session.getCurrentView();
            if (current == null)
                return;

            IViewFactory currentFactory = current.getFactory();

            if (!(currentFactory instanceof FilteredAnvilViewFactory))
                return;

            FilteredAnvilViewFactory factory = (FilteredAnvilViewFactory)currentFactory;

            if (!factory.getFilterManager().isValidItem(repaired)) {
                InventoryView invView = current.getInventoryView();
                if (invView != null) {
                    ItemStack stack = repaired.clone();
                    ItemStackUtils.setLore(stack, Lang.get(_NOT_REPAIRABLE));
                    invView.setItem(0, stack);
                }
            }
        }

    }
}
