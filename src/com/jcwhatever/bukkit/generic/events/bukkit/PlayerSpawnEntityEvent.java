package com.jcwhatever.bukkit.generic.events.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class PlayerSpawnEntityEvent extends Event {
	
	private static final HandlerList _handlers = new HandlerList();
	
	private CreatureSpawnEvent _parentEvent;
	private Player _player;
	
	PlayerSpawnEntityEvent(CreatureSpawnEvent event, Player player) {
		_parentEvent = event;
		_player = player;
	}
	
	public CreatureSpawnEvent getParentEvent() {
		return _parentEvent;
	}
	
	public Player getPlayer() {
		return _player;
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
	
	public static PlayerSpawnEntityEvent callEvent(CreatureSpawnEvent parentEvent, Player player) {
		PlayerSpawnEntityEvent event = new PlayerSpawnEntityEvent(parentEvent, player);
		
		if (hasListeners()) {
			Bukkit.getPluginManager().callEvent(event);
		}
		
		return event;
	}

	public static boolean hasListeners() {
		return _handlers.getRegisteredListeners().length > 0;
	}
}
