package com.jcwhatever.bukkit.generic.mixins;

import org.bukkit.Location;

/**
 * Mixin that defines an implementation as a {@code Location} wrapper that provides a name
 * to the encapsulated location.
 */
public interface INamedLocation {

    /**
     * Get the name of the location.
     */
	String getName();

    /**
     * Get the name of the location in
     * lowercase.
     */
	String getSearchName();

    /**
     * Get the location.
     */
	Location getLocation();

    /**
     * Get the distance to another location.
     *
     * @param location  The location to check.
     */
	INamedLocationDistance getDistance(Location location);
}
