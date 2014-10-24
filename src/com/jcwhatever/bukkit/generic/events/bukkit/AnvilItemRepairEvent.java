package com.jcwhatever.bukkit.generic.events.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class AnvilItemRepairEvent extends Event {
	
	private static final HandlerList _handlers = new HandlerList();
	
	private Player _player;
	private AnvilInventory _anvilInventory;
	private ItemStack _item;
	
	private boolean _isCancelled;
	
	AnvilItemRepairEvent(Player player, AnvilInventory anvilInventory, ItemStack item) {
		_player = player;
		_anvilInventory = anvilInventory;
		_item = item;
	}
	
	public Player getPlayer() {
		return _player;
	}
	
	public AnvilInventory getAnvilInventory() {
		return _anvilInventory;
	}
	
	public ItemStack getRepairedItem() {
		return _item;
	}
	
	public boolean isCancelled() {
		return _isCancelled;
	}
	
	public void setIsCancelled(boolean isCancelled) {
		_isCancelled = isCancelled;
	}
	
	public HandlerList getHandlers() {
	    return _handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return _handlers;
	}
	
	public static AnvilItemRepairEvent callEvent(Player player, AnvilInventory anvilInventory, ItemStack item) {
		AnvilItemRepairEvent event = new AnvilItemRepairEvent(player, anvilInventory, item);
		
		if (hasListeners()) {
			Bukkit.getPluginManager().callEvent(event);
		}
		
		return event;
	}

	public static boolean hasListeners() {
		return _handlers.getRegisteredListeners().length > 0;
	}
}
