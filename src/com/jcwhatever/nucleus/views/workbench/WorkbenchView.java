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

import com.jcwhatever.nucleus.views.View;
import com.jcwhatever.nucleus.views.ViewCloseReason;
import com.jcwhatever.nucleus.views.ViewOpenReason;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * A workbench view.
 */
public class WorkbenchView extends View {

    private InventoryView _inventoryView;

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     */
    protected WorkbenchView(Plugin plugin) {
        super(plugin);
    }

    @Override
    public InventoryType getInventoryType() {
        return InventoryType.CRAFTING;
    }

    @Override
    public InventoryView getInventoryView() {
        return _inventoryView;
    }

    @Nullable
    @Override
    public Inventory getInventory() {
        return null;
    }

    @Override
    public boolean isInventoryViewable() {
        return true;
    }

    @Override
    protected boolean openView(ViewOpenReason reason) {
        _inventoryView = getPlayer().openWorkbench(getPlayer().getLocation(), true);
        return true;
    }

    @Override
    protected void onClose(ViewCloseReason reason) {
        // do nothing
    }
}
