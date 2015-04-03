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

package com.jcwhatever.nucleus.views.workbench;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.utils.items.ItemFilter;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.views.ViewOpenReason;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
 * A workbench view that can allow or deny specific items from being crafted.
 */
public class FilteredWorkbenchView extends WorkbenchView {

    @Localizable static final String _NOT_CRAFTABLE_LORE =
            "{RED}Not craftable here.";

    @Localizable static final String _NOT_CRAFTABLE_CHAT =
            "{RED}You can't craft this item here.";

    private static AnvilEventListener _eventListener;
    private static Map<InventoryView, FilteredWorkbenchView> _viewMap = new WeakHashMap<>(10);

    private final ItemFilter _filter;

    /**
     * Constructor.
     *
     * @param plugin         The views owning plugin.
     * @param filterManager  The filter manager used to allow or deny specific items.
     */
    public FilteredWorkbenchView(Plugin plugin, ItemFilter filterManager) {
        super(plugin);

        _filter = filterManager;
    }

    /**
     * Get the views item filter manager.
     */
    @Nullable
    public ItemFilter getFilterManager() {
        return _filter;
    }

    @Override
    protected boolean openView(ViewOpenReason reason) {
        if (super.openView(reason)) {

            if (_eventListener == null) {
                _eventListener = new AnvilEventListener();
                Bukkit.getPluginManager().registerEvents(_eventListener, Nucleus.getPlugin());
            }

            InventoryView inventory = getInventoryView();
            if (inventory == null)
                throw new AssertionError();

            _viewMap.put(inventory, this);

            return true;
        }
        return false;
    }

    /**
     * Invoked to get the craft deny message displayed in item lore.
     */
    protected String getDenyLore() {
        return NucLang.get(_NOT_CRAFTABLE_LORE);
    }

    /**
     * Invoked to get the craft deny message displayed in chat.
     *
     * @return  The message or null to show no message.
     */
    @Nullable
    protected String getDenyChat() {
        return NucLang.get(_NOT_CRAFTABLE_CHAT);
    }

    /**
     * Anvil event listener
     */
    private static class AnvilEventListener implements Listener {

        @EventHandler
        private void onPrepareItemCraft(PrepareItemCraftEvent event) {

            FilteredWorkbenchView workbench = _viewMap.get(event.getView());
            if (workbench == null)
                return;

            ItemStack result = event.getRecipe().getResult();

            ItemFilter filter = workbench.getFilterManager();
            if (filter == null)
                return;

            if (!filter.isValid(result)) {
                InventoryView invView = event.getView();
                if (invView != null) {
                    ItemStack stack = result.clone();
                    ItemStackUtils.setLore(stack, workbench.getDenyLore());
                    invView.setItem(0, stack);
                }
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        private void onCraftItem(CraftItemEvent event) {

            FilteredWorkbenchView workbench = _viewMap.get(event.getView());
            if (workbench == null)
                return;

            ItemFilter filter = workbench.getFilterManager();
            if (filter == null)
                return;

            ItemStack result = event.getRecipe().getResult();

            if (!filter.isValid(result)) {
                tellNoCraftMessage(workbench);
                event.setCancelled(true);
            }
        }

        @EventHandler
        private void onNucleusDisable(PluginDisableEvent event) {
            if (event.getPlugin() == Nucleus.getPlugin())
                _eventListener = null;
        }

        private void tellNoCraftMessage(FilteredWorkbenchView view) {

            String message = view.getDenyChat();
            if (message == null || message.isEmpty())
                return;

            NucMsg.tellNoSpam(view.getPlugin(), view.getPlayer(), message);
        }
    }
}
