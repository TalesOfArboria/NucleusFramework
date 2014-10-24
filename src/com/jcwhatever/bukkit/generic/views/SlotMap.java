package com.jcwhatever.bukkit.generic.views;

import com.jcwhatever.bukkit.generic.utils.PreCon;

import java.util.HashMap;

/**
 * Maps MenuItems to an inventory slot index.
 */
public class SlotMap extends HashMap<Integer, MenuItem> {

	private static final long serialVersionUID = 1L;
	
	private int _slots;

    /**
     * Get number of slots in inventory.
     * <p>
     *     Value of total slots must be set by the implementation
     *     using the {@code SlotMap}.
     * </p>
     */
	public int getTotalSlots() {
		return _slots;
	}

    /**
     * Set the number of slots in the inventory
     * the slot map represents.
     *
     * @param slots  The number of slots.
     */
	public void setTotalSlots(int slots) {
        PreCon.positiveNumber(slots);

		_slots = slots;
	}

}
