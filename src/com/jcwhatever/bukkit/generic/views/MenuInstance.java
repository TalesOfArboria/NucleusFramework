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
