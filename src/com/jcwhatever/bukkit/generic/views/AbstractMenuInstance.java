package com.jcwhatever.bukkit.generic.views;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import com.jcwhatever.bukkit.generic.items.ItemStackHelper;
import com.jcwhatever.bukkit.generic.utils.Utils;
import com.jcwhatever.bukkit.generic.views.InventoryActionInfoHandler.InventoryActionInfo;
import com.jcwhatever.bukkit.generic.views.InventoryActionInfoHandler.ViewActionOrder;

/**
 * Abstract implementation of a menu view instance.
 */
public abstract class AbstractMenuInstance extends ViewInstance {

    /**
     * Constructor.
     *
     * @param view          The view the menu instance represents.
     * @param previous      The previous view instance the player was looking at.
     * @param p             The player the view instance is for.
     * @param sessionMeta   The view session meta.
     * @param instanceMeta  The instance meta.
     */
	public AbstractMenuInstance(IView view, ViewInstance previous, Player p, ViewMeta sessionMeta, ViewMeta instanceMeta) {
		super(view, previous, p, sessionMeta, instanceMeta);
	}

    /**
     * Called when the view is shown to the player.
     *
     * @param instanceMeta  The meta data for the instance.
     */
	@Override
	protected abstract InventoryView onShow(ViewMeta instanceMeta);

    /**
     * Called when the view is shown to the player after
     * closing another view instance.
     *
     * @param instanceMeta  The meta data for the instance.
     * @param result        The result meta data from the closing view instance.
     */
	@Override
	protected abstract InventoryView onShowAsPrev (ViewMeta instanceMeta, ViewResult result);

    /**
     * Called when the view instance is closed.
     *
     * @param reason  The reason the view was closed.
     */
	@Override
    protected abstract void onClose (ViewCloseReason reason);

    /**
     * Called when a menu item is needed for an inventory slot.
     *
     * @param slot  The slot to find the menu item from.
     */
	protected abstract MenuItem getMenuItem(int slot);

    /**
     * Called when a menu item is selected.
     *
     * @param menuItem  The selected menu item.
     */
	protected abstract void onItemSelect(MenuItem menuItem);

    /**
     *  Items in a menu cannot be dragged or moved.
     */
	@Override
	protected final boolean onItemsPlaced(InventoryActionInfo actionInfo, ViewActionOrder actionOrder) {
		return false; // cancel placing an item
	}

    /**
     * Called when an item is picked up. For the menu context,
     * it is used to determine when the player clicks a menu item
     * and the pickup is always cancelled.
     */
	@Override
	protected final boolean onItemsPickup(InventoryActionInfo actionInfo, ViewActionOrder actionOrder) {
		
		MenuItem item = getMenuItem(actionInfo.getRawSlot());
		if (item == null)
			return false;
		
		item.setSlot(actionInfo.getRawSlot());
		
		onItemSelect(item);
		
		
		String clickCommand = item.getClickCommand();

		if (clickCommand != null && !clickCommand.isEmpty())
			Utils.executeAsPlayer(getPlayer(), clickCommand);

		String clickViewName = item.getClickViewName();
		if (clickViewName != null && !clickViewName.isEmpty()) {

			getView().getViewManager().show(getPlayer(), clickViewName, getSourceBlock(), item);
		}
		
		return false;
	}

    /**
     * Items in menu view cannot be dropped out of the view.
     */
	@Override
	protected boolean onItemsDropped(InventoryActionInfo actionInfo, ViewActionOrder actionOrder) {
		return false; // cancel dropping items out of the view.
	}

    /**
     * Items in the menu view cannot be placed.
     */
    @Override
    protected boolean onLowerItemsPlaced (InventoryActionInfo actionInfo, ViewActionOrder actionOrder) {
        return false; // cancel placing items
    }

    /**
     * Items in the menu view cannot be picked up.
     */
    @Override
    protected boolean onLowerItemsPickup (InventoryActionInfo actionInfo, ViewActionOrder actionOrder) {
        return false; // cancel item pickup
    }

    /**
     * Temporarily remove a menu item from view or make it visible.
     *
     * @param slot       The slot index of the item to hide or show.
     * @param inventory  The menu view inventory.
     * @param visible    True for visible, false to hide.
     *
     * @return  True if successful.
     */
	protected boolean setItemVisible(int slot, Inventory inventory, boolean visible) {
        
        MenuItem menuItem = getMenuItem(slot);
        if (menuItem == null)
            return false;
        
        boolean isEmpty = isSlotEmpty(slot, inventory);
        
        if (visible && isEmpty) {
            inventory.setItem(slot, menuItem.getItemStack());
        }
        else if (!visible && !isEmpty) {
            inventory.setItem(slot, ItemStackHelper.AIR);
        }
        else {
            return false;
        }
        
        return true;
    }

    /**
     * Determine if a slot is empty.
     *
     * @param slot       The index of the slot to check.
     * @param inventory  The menu view inventory.
     *
     * @return  True if the slot is empty.
     */
    protected boolean isSlotEmpty(int slot, Inventory inventory) {
        
        ItemStack itemStack = inventory.getItem(slot);
        
        return itemStack == null || itemStack.getType() == Material.AIR;
    }
}
