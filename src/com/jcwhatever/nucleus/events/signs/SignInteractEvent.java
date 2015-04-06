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


package com.jcwhatever.nucleus.events.signs;

import com.jcwhatever.nucleus.mixins.ICancellable;
import com.jcwhatever.nucleus.mixins.IPlayerReference;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Called when a player interacts with a sign.
 */
public class SignInteractEvent extends Event
		implements Cancellable, ICancellable, IPlayerReference {
	
	private static final HandlerList handlers = new HandlerList();
	
	private final PlayerInteractEvent _parentEvent;
	private final Sign _sign;

	/**
	 * Constructor.
	 *
	 * @param event  The parent event.
	 * @param sign   The interacted sign.
	 */
	public SignInteractEvent(PlayerInteractEvent event, Sign sign) {
		PreCon.notNull(event);
		PreCon.notNull(sign);

		_parentEvent = event;
		_sign = sign;
	}

	/**
	 * Get the interacted sign.
	 */
	public Sign getSign() {
		return _sign;
	}

	/**
	 * Get the player that interacted with the sign.
	 */
	@Override
	public Player getPlayer() {
		return _parentEvent.getPlayer();
	}

	/**
	 * Get the event action.
	 */
	public Action getAction() {
		return _parentEvent.getAction();
	}

	/**
	 * Get the block face.
	 */
	public BlockFace getBlockFace() {
		return _parentEvent.getBlockFace();
	}

	/**
	 * Get the clicked sign block.
	 */
	public Block getClickedBlock() {
		return _parentEvent.getClickedBlock();
	}

	/**
	 * Get the item in the players hand.
	 */
	public ItemStack getItem() {
		return _parentEvent.getItem();
	}

	/**
	 * Determine if the player will be able to use the item in hand.
	 */
	public Result useItemInHand() {
		return _parentEvent.useItemInHand();
	}

	/**
	 * Set if the player will be able to use the item in hand.
	 */
	public void setUseItemInHand(Result useItemInHand) {
		_parentEvent.setUseItemInHand(useItemInHand);
	}
	
	@Override
	public boolean isCancelled() {
		return _parentEvent.isCancelled();
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		_parentEvent.setCancelled(isCancelled);
	}

	@Override
    public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
