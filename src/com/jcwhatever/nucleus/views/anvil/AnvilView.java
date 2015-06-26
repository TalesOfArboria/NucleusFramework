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

import com.jcwhatever.nucleus.utils.nms.INmsAnvilViewHandler;
import com.jcwhatever.nucleus.utils.nms.NmsUtils;
import com.jcwhatever.nucleus.views.View;
import com.jcwhatever.nucleus.views.ViewCloseReason;
import com.jcwhatever.nucleus.views.ViewOpenReason;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * An implementation of an anvil view.
 */
public class AnvilView extends View {

    private Inventory _inventory;
    private InventoryView _inventoryView;

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     */
    public AnvilView(Plugin plugin) {
        super(plugin);
    }

    @Override
    protected boolean openView(ViewOpenReason reason) {

        Player player = getPlayer();

        INmsAnvilViewHandler handler = NmsUtils.getAnvilViewHandler();
        if (handler == null) {
            _inventory = Bukkit.createInventory(player, InventoryType.ANVIL);
            _inventoryView = player.openInventory(_inventory);
            if (_inventoryView == null)
                return false;
        }
        else {

            _inventoryView = handler.open(player, getViewSession().getSessionBlock());
            if (_inventoryView == null)
                return false;

            _inventory = _inventoryView.getTopInventory();
        }

        return true;
    }

    @Override
    protected void onClose(ViewCloseReason reason) {
        // do nothing
    }

    @Override
    public InventoryType getInventoryType() {
        return InventoryType.ANVIL;
    }

    @Nullable
    @Override
    public InventoryView getInventoryView() {
        return _inventoryView;
    }

    @Nullable
    @Override
    public Inventory getInventory() {
        return _inventory;
    }
}
