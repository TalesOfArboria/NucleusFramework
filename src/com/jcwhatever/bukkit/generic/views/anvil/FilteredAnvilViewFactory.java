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
import com.jcwhatever.bukkit.generic.events.anvil.AnvilItemRenameEvent;
import com.jcwhatever.bukkit.generic.events.anvil.AnvilItemRepairEvent;
import com.jcwhatever.bukkit.generic.events.manager.GenericsEventListener;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.items.ItemFilterManager;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.utils.ItemStackUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.views.IView;
import com.jcwhatever.bukkit.generic.views.ViewFactory;
import com.jcwhatever.bukkit.generic.views.ViewSession;
import com.jcwhatever.bukkit.generic.views.data.ViewArguments;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

/**
 * Generates {@link FilteredAnvilView} instances.
 */
public class FilteredAnvilViewFactory extends ViewFactory {

    @Localizable static final String _NOT_REPAIRABLE = "{RED}Not repairable here.";

    private static EventListener _eventListener;
    private static Map<Entity, ViewSession> _anvilMap = new WeakHashMap<>(20);

    private final ItemFilterManager _filterManager;

    /**
     * Constructor.
     *
     * @param plugin         The owning plugin.
     * @param name           The factory instance name.
     * @param filterManager  The default filter manager.
     */
    protected FilteredAnvilViewFactory(Plugin plugin, String name,
                                       @Nullable ItemFilterManager filterManager) {
        super(plugin, name);

        PreCon.notNull(filterManager);

        _filterManager = filterManager;
    }

    /**
     * Get the default filter manager.
     */
    public ItemFilterManager getDefaultFilter() {
        return _filterManager;
    }

    @Override
    public IView create(@Nullable String title, ViewSession session, ViewArguments arguments) {
        PreCon.notNull(session);
        PreCon.notNull(arguments);

        return new FilteredAnvilView(title, session, this, arguments, getDefaultFilter());
    }

    /**
     * Create a new view instance using the specified filter manager.
     *
     * @param title          The view title. (Anvil views can't have titles set)
     * @param session        The players view session.
     * @param arguments      The view meta arguments.
     * @param filterManager  The filter manager.
     */
    public FilteredAnvilView create(@Nullable String title, ViewSession session, ViewArguments arguments,
                        ItemFilterManager filterManager) {
        PreCon.notNull(session);
        PreCon.notNull(arguments);

        return new FilteredAnvilView(title, session, this, arguments, filterManager);
    }

    /**
     * Register a filtered anvil view instance.
     *
     * @param view  The view to register
     */
    static void register(FilteredAnvilView view) {
        if (_eventListener == null) {
            _eventListener = new EventListener(GenericsLib.getPlugin());
            GenericsLib.getEventManager().register(_eventListener);
        }

        _anvilMap.put(view.getPlayer(), view.getViewSession());
    }

    /**
     * Unregister a filtered anvil view instance.
     *
     * @param view  The view to unregister.
     */
    static void unregister(FilteredAnvilView view) {
        _anvilMap.remove(view.getPlayer());
    }

    @Override
    protected void onDispose() {
        // do nothing
    }

    /**
     * Anvil event listener.
     */
    static class EventListener extends GenericsEventListener {

        public EventListener(Plugin plugin) {
            super(plugin);
        }

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
                    ItemStackUtils.setLore(stack, Lang.get(_NOT_REPAIRABLE));
                    invView.setItem(0, stack);
                }
            }
        }

    }
}
