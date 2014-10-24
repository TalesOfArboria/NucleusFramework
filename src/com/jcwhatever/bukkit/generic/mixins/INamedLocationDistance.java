package com.jcwhatever.bukkit.generic.mixins;

import com.jcwhatever.bukkit.generic.mixins.implemented.NamedLocationDistance;
import org.bukkit.Location;

/**
 * Defines an implementation that represents a distance to a named location.
 */
public interface INamedLocationDistance extends Comparable<NamedLocationDistance>{

    /**
     * The named location the distance is for.
     */
	INamedLocation getNamedLocation();

    /**
     * The target location.
     */
	Location getTarget();

    /**
     * Get the distance between the named location
     * and the target location.
     */
	double getDistance();

    /**
     * Get the distance between the named location
     * and the target location squared.
     *
     * <p>Should generally provide a faster implementation than
     * using {@code getDistance}.</p>
     */
	double getDistanceSquared();
	
}