package com.jcwhatever.bukkit.generic.warp;

import com.jcwhatever.bukkit.generic.mixins.INamedLocation;
import com.jcwhatever.bukkit.generic.mixins.INamedLocationDistance;
import com.jcwhatever.bukkit.generic.mixins.implemented.NamedLocationDistance;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Location;

/**
 * Represents a warp location.
 */
public class Warp implements INamedLocation {

    private final String _warpName;
    private final String _warpSearchName;
    private final IDataNode _dataNode;
    private Location _location;

    /**
     * Constructor.
     *
     * @param warpName  The name of the warp.
     * @param location  The warp location.
     * @param dataNode  The warp data node.
     */
    public Warp (String warpName, Location location, IDataNode dataNode) {
        _warpName = warpName;
        _warpSearchName = warpName.toLowerCase();
        _location = location;
        _dataNode = dataNode;
    }

    /**
     * Get the name of the warp.
     */
    @Override
    public String getName() {
        return _warpName;
    }

    /**
     * Get the name of the warp in lowercase.
     */
    @Override
    public String getSearchName() {
        return _warpSearchName;
    }

    /**
     * Get the warp location.
     */
    @Override
    public Location getLocation() {
        return _location;
    }

    /**
     * Get the distance from the warp to the specified location.
     *
     * @param location  The location to check.
     */
    @Override
    public INamedLocationDistance getDistance(Location location) {
        return new NamedLocationDistance(this, location);
    }

    /**
     * Set the warp location.
     *
     * @param location  The warp location.
     */
    public void setLocation(Location location) {
        PreCon.notNull(location);

        _location = location;
        _dataNode.set(_warpName, location);
        _dataNode.saveAsync(null);
    }
}