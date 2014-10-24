package com.jcwhatever.bukkit.generic.mixins.implemented;

import com.jcwhatever.bukkit.generic.mixins.INamedLocationDistance;
import com.jcwhatever.bukkit.generic.mixins.INamedLocation;
import org.bukkit.Location;

import com.jcwhatever.bukkit.generic.utils.PreCon;

/**
 * A basic {@code INamedLocationDistance} implementation.
 */
public class NamedLocationDistance implements INamedLocationDistance {
	private INamedLocation _namedLocation;
	private Location _target;
	private Double _distance;
	private Double _distanceSquared;
	
	public NamedLocationDistance (INamedLocation namedLocation, Location target) {
		PreCon.notNull(namedLocation);
		PreCon.notNull(target);
		
		_namedLocation = namedLocation;
		_target = target;
	}
	
	@Override
    public INamedLocation getNamedLocation() {
		return _namedLocation;
	}
	
	@Override
    public Location getTarget() {
		return _target;
	}
	
	@Override
    public double getDistance() {
		if (_distance == null) {
			_distance = _namedLocation.getLocation().distance(_target);
		}
		return _distance;
	}
	
	@Override
    public double getDistanceSquared() {
		if (_distanceSquared == null) {
			_distanceSquared = _namedLocation.getLocation().distanceSquared(_target);
		}
		
		return _distanceSquared;
	}

	@Override
	public int compareTo(NamedLocationDistance distance) {
		return Double.compare(getDistanceSquared(), distance.getDistanceSquared());
	}

}
