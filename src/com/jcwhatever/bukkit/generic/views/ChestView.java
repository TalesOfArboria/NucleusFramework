package com.jcwhatever.bukkit.generic.views;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.views.InventoryActionInfoHandler.InventoryActionInfo;
import com.jcwhatever.bukkit.generic.views.InventoryActionInfoHandler.ViewActionOrder;

public class ChestView extends AbstractView {

	private static final int MAX_ROWS = 6;
	private int _rows = 6;
	
	
	public int getRows() {
		return _rows;
	}
	
	public void setRows(int rows) {
		_rows = Math.min(rows, MAX_ROWS);
	}
	
	@Override
	protected void onInit(String name, IDataNode dataNode, ViewManager viewManager) {
		// do nothing
	}
	
	@Override
	public InventoryType getInventoryType() {
		return InventoryType.CHEST;
	}

	@Override
	public ViewType getViewType() {
		return ViewType.INVENTORY;
	}

	@Override
	public void dispose() {
		// do nothing
		
	}

	@Override
	protected void onLoadSettings(IDataNode dataNode) {
		// do nothing		
	}
	
	@Override
	protected ViewInstance onCreateInstance(Player p, ViewInstance previous, ViewMeta sessionMeta, ViewMeta instanceMeta) {
		ChestInstance instance = new ChestInstance(this, previous, p, sessionMeta, instanceMeta);
		return instance;
	}

	
	public class ChestInstance extends ViewInstance {
 
		public ChestInstance(IView view, ViewInstance previous, Player p, ViewMeta sessionMeta, ViewMeta instanceMeta) {
			super(view, previous, p, sessionMeta, instanceMeta);
		}
		
		@Override
		public ViewResult getResult() {
			return null;
		}
		
		@Override
		protected InventoryView onShow(ViewMeta meta) {
			Inventory inventory = Bukkit.createInventory(getPlayer(), _rows * 9);
			return getPlayer().openInventory(inventory);
		}
		
		@Override
		protected InventoryView onShowAsPrev(ViewMeta instanceMeta, ViewResult result) {
			return onShow(instanceMeta);
		}

		@Override
		protected void onClose(ViewCloseReason reason) {
			// do nothing
		}

		@Override
		protected boolean onItemsPlaced(InventoryActionInfo actionInfo, ViewActionOrder actionOrder) {
			return true;
		}

		@Override
		protected boolean onItemsPickup(InventoryActionInfo actionInfo, ViewActionOrder actionOrder) {
			return true;
		}

		@Override
		protected boolean onItemsDropped(InventoryActionInfo actionInfo, ViewActionOrder actionOrder) {
			return true;
		}
		
        @Override
        protected boolean onLowerItemsPlaced (InventoryActionInfo actionInfo, ViewActionOrder actionOrder) {
            return true;
        }

        @Override
        protected boolean onLowerItemsPickup (InventoryActionInfo actionInfo, ViewActionOrder actionOrder) {
            return true;
        }
	}

	

}
