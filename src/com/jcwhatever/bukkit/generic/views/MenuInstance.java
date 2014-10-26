/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.views;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

/**
 * Represents an instance of a menu view as seen
 * by a player.
 */
public class MenuInstance extends AbstractMenuInstance {
	
	private MenuView _view;
	
	public MenuInstance(MenuView view, ViewInstance previous, Player p, ViewMeta sessionMeta, ViewMeta instanceMeta) {
		super(view, previous, p, sessionMeta, instanceMeta);
		_view = view;
	}
	
	@Override
	public ViewResult getResult() {
		return null;
	}

	@Override
	protected InventoryView onShow(ViewMeta meta) {
		return getPlayer().openInventory(_view._menu);
	}
	
	@Override
	protected InventoryView onShowAsPrev(ViewMeta instanceMeta, ViewResult result) {
		return onShow(instanceMeta);
	}

	@Override
	protected MenuItem getMenuItem(int slot) {
		return _view._slotMap.get(slot);
	}

	@Override
	protected void onItemSelect(MenuItem menuItem) {
		// do nothing		
	}

	@Override
	protected void onClose(ViewCloseReason reason) {
		// do nothing		
	}

}
