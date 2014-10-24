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
