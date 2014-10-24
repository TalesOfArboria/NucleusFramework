package com.jcwhatever.bukkit.generic.events.bukkit.sounds;

import com.jcwhatever.bukkit.generic.sounds.ResourceSound;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ResourceSoundEndEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private Player _player;
	private ResourceSound _sound;
	private Location _location;
	private float _volume;
		
	public ResourceSoundEndEvent(Player p, ResourceSound sound, Location location, float volume) {
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
	
	public Location getLocations() {
		return _location;
	}
	
	public float getVolume() {
		return _volume;
	}
	 
	@Override
    public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}

