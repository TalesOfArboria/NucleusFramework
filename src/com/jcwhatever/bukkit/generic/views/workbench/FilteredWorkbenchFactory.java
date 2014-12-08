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
import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.IGenericsEventListener;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.items.ItemFilterManager;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.messaging.Messenger;
import com.jcwhatever.bukkit.generic.utils.ItemStackUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.views.IView;
import com.jcwhatever.bukkit.generic.views.IViewSession;
import com.jcwhatever.bukkit.generic.views.ViewFactory;
import com.jcwhatever.bukkit.generic.views.data.ViewArguments;
import com.jcwhatever.bukkit.generic.views.data.ViewOpenReason;

import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

/*
 * 
 */
public class FilteredWorkbenchFactory extends ViewFactory<FilteredWorkbenchView> {

    @Localizable static final String _NOT_CRAFTABLE_LORE = "{RED}Not craftable here.";
    @Localizable static final String _NOT_CRAFTABLE_CHAT = "{RED}You can't craft this item here.";

    private final String _name;
    private final String _searchName;
    private final ItemFilterManager _filterManager;
    private final Map<InventoryView, FilteredWorkbenchView> _viewMap = new WeakHashMap<>(10);
    private final EventListener _eventListener;

    public FilteredWorkbenchFactory(String name, ItemFilterManager filterManager) {
        super(name, FilteredWorkbenchView.class);

        PreCon.notNull(filterManager);

        _name = name;
        _searchName = name.toLowerCase();

        _filterManager = filterManager;

        _eventListener = new EventListener();
        GenericsLib.getEventManager().register(_eventListener);
    }

    public ItemFilterManager getFilterManager() {
        return _filterManager;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getSearchName() {
        return _searchName;
    }

    @Override
    public IView create(@Nullable String title, IViewSession session, ViewArguments arguments) {
        PreCon.notNull(session);

        return new FilteredWorkbenchView(session, this, arguments, _filterManager);
    }

    @Override
    protected boolean onOpen(ViewOpenReason reason, FilteredWorkbenchView view) {

        view.show();

        InventoryView inventory = view.getInventoryView();
        if (inventory == null)
            return false;

        _viewMap.put(inventory, view);

        return true;
    }

    @Override
    protected void onDispose() {
        GenericsLib.getEventManager().unregister(_eventListener);
    }

    private class EventListener implements IGenericsEventListener {

        @GenericsEventHandler
        private void onPrepareItemCraft(PrepareItemCraftEvent event) {

            FilteredWorkbenchView workbench = _viewMap.get(event.getView());
            if (workbench == null)
                return;

            ItemStack result = event.getRecipe().getResult();

            if (!workbench.getFilterManager().isValidItem(result)) {
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

            ItemStack result = event.getRecipe().getResult();

            if (!workbench.getFilterManager().isValidItem(result)) {
                tellNoCraftMessage(workbench);
                event.setCancelled(true);
            }
        }

        private void tellNoCraftMessage(FilteredWorkbenchView view) {
            Messenger.tellNoSpam(view.getPlugin(), view.getPlayer(),
                    Lang.get(view.getPlugin(), _NOT_CRAFTABLE_CHAT));
        }
    }
}
