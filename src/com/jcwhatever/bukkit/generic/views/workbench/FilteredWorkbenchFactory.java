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

package com.jcwhatever.bukkit.generic.views.workbench;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.events.manager.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.manager.GenericsEventListener;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.internal.Msg;
import com.jcwhatever.bukkit.generic.items.ItemFilterManager;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.utils.ItemStackUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.views.IView;
import com.jcwhatever.bukkit.generic.views.ViewFactory;
import com.jcwhatever.bukkit.generic.views.ViewSession;
import com.jcwhatever.bukkit.generic.views.data.ViewArguments;

import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

/**
 * Generates {@code FilteredWorkbenchView} instances.
 */
public class FilteredWorkbenchFactory extends ViewFactory {

    @Localizable static final String _NOT_CRAFTABLE_LORE = "{RED}Not craftable here.";
    @Localizable static final String _NOT_CRAFTABLE_CHAT = "{RED}You can't craft this item here.";

    private static EventListener _eventListener;
    private static Map<InventoryView, FilteredWorkbenchView> _viewMap = new WeakHashMap<>(10);

    private final ItemFilterManager _filterManager;


    /**
     * Constructor.
     *
     * @param plugin         The owning plugin.
     * @param name           The name of the instance.
     * @param filterManager  The default item filter manager.
     */
    public FilteredWorkbenchFactory(Plugin plugin, String name,
                                    @Nullable ItemFilterManager filterManager) {
        super(plugin, name);

        _filterManager = filterManager;

        if (_eventListener == null) {
            _eventListener = new EventListener(GenericsLib.getLib());
            GenericsLib.getEventManager().register(_eventListener);
        }
    }

    /**
     * Get the default filter manager used when
     * one is not specified.
     */
    @Nullable
    public ItemFilterManager getDefaultFilter() {
        return _filterManager;
    }

    @Override
    public IView create(@Nullable String title, ViewSession session, ViewArguments arguments) {
        PreCon.notNull(session);

        return new FilteredWorkbenchView(session, this, arguments, _filterManager);
    }

    /**
     * Create a new instance.
     *
     * @param title          The view title. (Workbench views cannot have their title set)
     * @param session        The players view session.
     * @param arguments      The view meta arguments.
     * @param filterManager  The filter manager to use.
     */
    public FilteredWorkbenchView create(@Nullable String title, ViewSession session, ViewArguments arguments,
                        ItemFilterManager filterManager) {
        PreCon.notNull(session);
        PreCon.notNull(arguments);
        PreCon.notNull(filterManager);

        return new FilteredWorkbenchView(session, this, arguments, filterManager);
    }

    /**
     * Register a view instance.
     *
     * @param view  The view to register.
     */
    void registerInventory(FilteredWorkbenchView view) {
        InventoryView inventory = view.getInventoryView();
        if (inventory == null)
            throw new AssertionError();

        _viewMap.put(inventory, view);
    }

    @Override
    protected void onDispose() {
        GenericsLib.getEventManager().unregister(_eventListener);
    }

    /**
     * Anvil event listener
     */
    private static class EventListener extends GenericsEventListener {

        public EventListener(Plugin plugin) {
            super(plugin);
        }

        @GenericsEventHandler
        private void onPrepareItemCraft(PrepareItemCraftEvent event) {

            FilteredWorkbenchView workbench = _viewMap.get(event.getView());
            if (workbench == null)
                return;

            ItemStack result = event.getRecipe().getResult();

            ItemFilterManager filter = workbench.getFilterManager();
            if (filter == null)
                return;

            if (!filter.isValidItem(result)) {
                InventoryView invView = event.getView();
                if (invView != null) {
                    ItemStack stack = result.clone();
                    ItemStackUtils.setLore(stack, Lang.get(workbench.getPlugin(), _NOT_CRAFTABLE_LORE));
                    invView.setItem(0, stack);
                }
            }
        }

        @GenericsEventHandler
        private void onCraftItem(CraftItemEvent event) {

            FilteredWorkbenchView workbench = _viewMap.get(event.getView());
            if (workbench == null)
                return;

            ItemFilterManager filter = workbench.getFilterManager();
            if (filter == null)
                return;

            ItemStack result = event.getRecipe().getResult();

            if (!filter.isValidItem(result)) {
                tellNoCraftMessage(workbench);
                event.setCancelled(true);
            }
        }

        @GenericsEventHandler
        private void onGenericsDisable(PluginDisableEvent event) {
            if (event.getPlugin() == GenericsLib.getLib())
                _eventListener = null;
        }

        private void tellNoCraftMessage(FilteredWorkbenchView view) {
            Msg.tellNoSpam(view.getPlugin(), view.getPlayer(),
                    Lang.get(view.getPlugin(), _NOT_CRAFTABLE_CHAT));
        }
    }
}
