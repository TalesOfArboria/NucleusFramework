package com.jcwhatever.bukkit.generic.warp;

import org.bukkit.Location;

import com.jcwhatever.bukkit.generic.storage.IDataNode;

public class Warp {
	private String _warpName;
	private String _warpSearchName;
	private Location _location;
	private IDataNode _settings;
			
	public Warp (String warpName, Location location, IDataNode settings) {
		_warpName = warpName;
		_warpSearchName = warpName.toLowerCase();		
		_location = location;
		_settings = settings;
	}
	
	public String getName() {
		return _warpName;
	}
	
	public String getSearchName() {
		return _warpSearchName;
	}
	
	public Location getLocation() {
		return _location;
	}
	
	public void setLocation(Location location) {
		_location = location;
		_settings.set(_warpName, location);
		_settings.saveAsync(null);
	}
}