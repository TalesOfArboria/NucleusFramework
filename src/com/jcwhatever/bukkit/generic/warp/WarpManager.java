package com.jcwhatever.bukkit.generic.warp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;

import com.jcwhatever.bukkit.generic.storage.IDataNode;

public class WarpManager {

	private Map<String, Warp> _warpMap = new HashMap<String, Warp>();
	private IDataNode _settings;
	
	public WarpManager (IDataNode settings) {
		_settings = settings;
		loadWarps();
	}
	
	public Warp getWarp(String name) {
		return _warpMap.get(name.toLowerCase());				
	}
	
	public List<Warp> getWarps() {
		return new ArrayList<Warp>(_warpMap.values());
	}
	
	public boolean setWarp(String name, Location location) {
		if (name == null || name.isEmpty() || location == null)
			return false;
		
		Warp warp = getWarp(name);
		
		if (warp != null) {
			warp.setLocation(location);
		}
		else {
			warp = new Warp(name, location, _settings);
			_settings.set(name, location);
			_settings.saveAsync(null);
			_warpMap.put(warp.getSearchName(), warp);
		}
		return true;
	}
	
	public boolean deleteWarp(String name) {
		Warp warp = getWarp(name);
		if (warp == null)
			return false;
		
		_warpMap.remove(warp.getSearchName());
		_settings.set(warp.getName(), null);
		_settings.saveAsync(null);
		return true;
	}
	
	private void loadWarps() {
		Set<String> warpNames = _settings.getSubNodeNames();
		if (warpNames == null)
			return;
		
		for (String warpName : warpNames) {
			Location location = _settings.getLocation(warpName);
			Warp warp = new Warp(warpName, location, _settings);
			_warpMap.put(warp.getSearchName(), warp);
		}
	}
	
}
