package com.jcwhatever.bukkit.generic.events.bukkit.sounds;


import com.jcwhatever.bukkit.generic.sounds.ResourceSound;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayResourceSoundEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private Player _player;
	private ResourceSound _sound;
	private boolean _isCancelled;
	private Location _location;
	private float _volume;
	
	public PlayResourceSoundEvent(Player p, ResourceSound sound, Location location, float volume) {
		_player = p;
		_sound = sound;
		_location = location;
		_volume = volume;
	}
	
	public Player getPlayer() {
		return _player;
	}
	
	public ResourceSound getResourceSound() {
		return _sound;
	}
	
	public Location getLocation() {
		return _location;
	}
	
	public float getVolume() {
		return _volume;
	}
	
	public void setResourceSound(ResourceSound sound) {
		_sound = sound;
	}
	
	public void setLocation(Location location) {
		_location = location;
	}
	
	public void setVolume(float volume) {
		_volume = volume;
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
