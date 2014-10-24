package com.jcwhatever.bukkit.generic.events.bukkit.economy;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.jcwhatever.bukkit.generic.player.PlayerHelper;

public class EconGiveEvent extends Event {
	
	private static final HandlerList _handlers = new HandlerList();
	
	private UUID _playerId;
	private Player _player;
	private double _amount;
	
	EconGiveEvent(UUID playerId, double amount) {
		_playerId = playerId;
		_amount = amount;
	}
	
	public UUID getPlayerId() {
		return _playerId;
	}
	
	public double getAmount() {
		return _amount;
	}
	
	public void setAmount(double amount) {
		_amount = amount;
	}
	
	public Player getPlayer() {
		if (_player == null) {
			_player = PlayerHelper.getPlayer(_playerId);
		}
		return _player;
	}
	
	public HandlerList getHandlers() {
	    return _handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return _handlers;
	}
	
	public static EconGiveEvent callEvent(UUID playerId, double amount) {
		EconGiveEvent event = new EconGiveEvent(playerId, amount);
		
		if (hasListeners()) {
			Bukkit.getPluginManager().callEvent(event);
		}
		
		return event;
	}

	public static boolean hasListeners() {
		return _handlers.getRegisteredListeners().length > 0;
	}
}
