/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.events.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SignInteractEvent extends Event {
	
	private static final HandlerList _handlers = new HandlerList();
	
	private PlayerInteractEvent _parentEvent;
	private Sign _sign;
	
	SignInteractEvent(PlayerInteractEvent event, Sign sign) {
		_parentEvent = event;
		_sign = sign;
	}
	
	public Sign getSign() {
		return _sign;
	}
	
	public Player getPlayer() {
		return _parentEvent.getPlayer();
	}
	
	public Action getAction() {
		return _parentEvent.getAction();
	}
	
	public BlockFace getBlockFace() {
		return _parentEvent.getBlockFace();
	}
	
	public Block getClickedBlock() {
		return _parentEvent.getClickedBlock();
	}
	
	public ItemStack getItem() {
		return _parentEvent.getItem();
	}
	
	public Material getMaterial() {
		return _parentEvent.getMaterial();
	}
	
	public boolean hasBlock() {
		return _parentEvent.hasBlock();
	}
	
	public Result useInteractedBlock() {
		return _parentEvent.useInteractedBlock();
	}
	
	public Result useItemInHand() {
		return _parentEvent.useItemInHand();
	}
	
	public boolean isBlockInHand() {
		return _parentEvent.isBlockInHand();
	}
	
	public void setUseInteractedBlock(Result useInteractedBlock) {
		_parentEvent.setUseInteractedBlock(useInteractedBlock);
	}
	
	public void setUseItemInHand(Result useItemInHand) {
		_parentEvent.setUseItemInHand(useItemInHand);
	}
	
	public boolean isCancelled() {
		return _parentEvent.isCancelled();
	}
	
	public void setIsCancelled(boolean isCancelled) {
		_parentEvent.setCancelled(isCancelled);
	}
	
	@Override
    public HandlerList getHandlers() {
	    return _handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return _handlers;
	}
	
	public static SignInteractEvent callEvent(PlayerInteractEvent parentEvent, Sign sign) {
		SignInteractEvent event = new SignInteractEvent(parentEvent, sign);
		
		if (hasListeners()) {
			Bukkit.getPluginManager().callEvent(event);
		}
		
		return event;
	}

	public static boolean hasListeners() {
		return _handlers.getRegisteredListeners().length > 0;
	}
}
