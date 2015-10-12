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


package com.jcwhatever.nucleus.events.anvil;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.events.HandlerListExt;
import com.jcwhatever.nucleus.mixins.ICancellable;
import com.jcwhatever.nucleus.mixins.IPlayerReference;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

/**
 * Called when an item is renamed on an anvil.
 */
public class AnvilRenameItemEvent extends Event implements
		Cancellable, ICancellable, IPlayerReference {
	
	private static final HandlerList handlers = new HandlerListExt(Nucleus.getPlugin(), AnvilRenameItemEvent.class);
	
	private final Player _player;
	private final AnvilInventory _anvilInventory;
	private final ItemStack _item;
	private final String _oldName;

	private String _newName;

	private boolean _isCancelled;

	/**
	 * Constructor.
	 *
	 * @param player          The player.
	 * @param anvilInventory  The anvil inventory.
	 * @param item            The item being renamed.
	 * @param newName         The new name.
	 * @param oldName         The old name.
	 */
	public AnvilRenameItemEvent(Player player, AnvilInventory anvilInventory,
								ItemStack item, String newName, @Nullable String oldName) {

		_player = player;
		_anvilInventory = anvilInventory;
		_item = item;
		_newName = newName;
		_oldName = oldName;
	}

	/**
	 * Get the player.
	 */
	@Override
	public Player getPlayer() {
		return _player;
	}

	/**
	 * Get the anvil inventory.
	 */
	public AnvilInventory getAnvilInventory() {
		return _anvilInventory;
	}

	/**
	 * Get the item that is being renamed.
	 */
	public ItemStack getItem() {
		return _item;
	}

	/**
	 * Get the items new name.
	 */
	public String getNewName() {
		return _newName;
	}

	/**
	 * Set the items new name.
	 */
	public void setNewName(@Nullable String newName) {
		_newName = newName;
	}

	/**
	 * Get the items old name.
	 */
	public String getOldName() {
		return _oldName;
	}
	
	@Override
	public boolean isCancelled() {
		return _isCancelled;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		_isCancelled = isCancelled;
	}

	@Override
    public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
