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

package com.jcwhatever.bukkit.generic.views.chest;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.views.IViewFactory;
import com.jcwhatever.bukkit.generic.views.View;
import com.jcwhatever.bukkit.generic.views.ViewSession;
import com.jcwhatever.bukkit.generic.views.data.ViewArguments;
import com.jcwhatever.bukkit.generic.views.data.ViewCloseReason;
import com.jcwhatever.bukkit.generic.views.data.ViewOpenReason;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import javax.annotation.Nullable;

/*
 * 
 */
public abstract class ChestView extends View {

    private InventoryView _inventoryView;

    protected ChestView(@Nullable String title, ViewSession session, IViewFactory factory, ViewArguments arguments) {
        super(title, session, factory, arguments);

        PreCon.notNull(title);
    }

    @Override
    public InventoryType getInventoryType() {
        return InventoryType.CHEST;
    }

    @Nullable
    @Override
    public InventoryView getInventoryView() {
        return _inventoryView;
    }

    @Override
    public boolean isInventoryViewable() {
        return true;
    }

    public final void show(ViewOpenReason reason) {
        ChestEventListener.register(this);

        Inventory inventory = createInventory();
        if (_inventoryView.getType() != getInventoryType())
            throw new RuntimeException("Incorrect inventory type.");

        _inventoryView = getPlayer().openInventory(inventory);

        onShow(reason);
    }

    @Override
    public final boolean close(ViewCloseReason reason) {
        PreCon.notNull(reason);

        if (super.close(reason)) {
            ChestEventListener.unregister(this);
            return true;
        }

        return false;
    }

    protected abstract void onShow(ViewOpenReason reason);

    protected abstract Inventory createInventory();

    /**
     * Called when an item is placed in an chest slot.
     */
    protected abstract ChestEventAction onItemsPlaced (ChestEventInfo eventInfo);

    /**
     * Called when an item is picked up from an chest slot.
     */
    protected abstract ChestEventAction onItemsPickup (ChestEventInfo eventInfo);

    /**
     * Called when an item is dropped outside the chest view.
     */
    protected abstract ChestEventAction onItemsDropped (ChestEventInfo eventInfo);

}
