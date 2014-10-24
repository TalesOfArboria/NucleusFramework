package com.jcwhatever.bukkit.generic.mixins.implemented;

import com.jcwhatever.bukkit.generic.mixins.INamedLocationDistance;
import com.jcwhatever.bukkit.generic.mixins.INamedLocation;
import org.bukkit.Location;

import com.jcwhatever.bukkit.generic.utils.PreCon;

/**
 * A basic named location implementation..
 */
public class NamedLocation implements INamedLocation {

	private String _name;
	private String _searchName;
	private Location _location;
	
	public NamedLocation(String name, Location location) {
		PreCon.notNullOrEmpty(name);
		PreCon.notNull(location);
		
		_name = name;
		_location = location;
	}
	
	@Override
	public String getName() {
		return _name;
	}
	@Override
	public String getSearchName() {
		if (_searchName == null)
			_searchName = _name.toLowerCase();
		
		return _searchName;
	}
	@Override
	public Location getLocation() {
		return _location;
	}
	
	@Override
	public INamedLocationDistance getDistance(Location location) {
		return new NamedLocationDistance(this, location);
	}
	
}
