package com.jcwhatever.bukkit.generic.events.bukkit.sounds;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ClearSoundsEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private Player _player;
	private boolean _isCancelled;
	
	public ClearSoundsEvent(Player p) {
		_player = p;
	}
	
	public Player getPlayer() {
		return _player;
	}
	
	public boolean isCancelled() {
		return _isCancelled;
	}
	
	public void setIsCancelled(boolean isCancelled) {
		_isCancelled = isCancelled;
	}
	 
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
