package com.jcwhatever.bukkit.generic.events.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class AnvilItemRenameEvent extends Event {
	
	private static final HandlerList _handlers = new HandlerList();
	
	private Player _player;
	private AnvilInventory _anvilInventory;
	private ItemStack _item;
	private String _newName;
	private String _oldName;
	
	private boolean _isCancelled;
	
	
	AnvilItemRenameEvent(Player player, AnvilInventory anvilInventory, ItemStack item, String newName, String oldName) {
		_player = player;
		_anvilInventory = anvilInventory;
		_item = item;
		_newName = newName;
		_oldName = oldName;
	}
	
	public Player getPlayer() {
		return _player;
	}
	
	public AnvilInventory getAnvilInventory() {
		return _anvilInventory;
	}
	
	public ItemStack getRenamedItem() {
		return _item;
	}
	
	public String getNewName() {
		return _newName;
	}
	
	public String getOldName() {
		return _oldName;
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
	
	public static AnvilItemRenameEvent callEvent(Player player, AnvilInventory anvilInventory, ItemStack item, String newName, String oldName) {
		AnvilItemRenameEvent event = new AnvilItemRenameEvent(player, anvilInventory, item, newName, oldName);
		
		if (hasListeners()) {
			Bukkit.getPluginManager().callEvent(event);
		}
		
		return event;
	}

	public static boolean hasListeners() {
		return _handlers.getRegisteredListeners().length > 0;
	}
}
