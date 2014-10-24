package com.jcwhatever.bukkit.generic.views;

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import org.bukkit.event.inventory.InventoryType;

/**
 * Abstract implementation of a menu view.
 */
public abstract class AbstractMenuView extends AbstractView {

	@Override
	protected final void onInit(String name, IDataNode dataNode, ViewManager viewManager) {
        // menu view does not store a name, data node, or view manager
		onInit();
	}

	@Override
	public final InventoryType getInventoryType() {
		return InventoryType.CHEST;
	}

	@Override
	public final ViewType getViewType() {
		return ViewType.MENU;
	}

	@Override
	public final void dispose() {
		onDispose();
	}
	
	protected abstract void onInit();
	protected abstract void buildInventory();
	protected void onDispose() {}
	
}








