package com.jcwhatever.bukkit.generic.commands.arguments;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Use as a callback to retrieve a location.
 */
public abstract class LocationResponse {
    
    public abstract void onLocationRetrieved(Player p, Location result);
    
}
