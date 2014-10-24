package com.jcwhatever.bukkit.generic.regions;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Location;

/**
 * Contains 2 location points used to
 * designate a cuboid area of a world.
 */
public class RegionSelection {
    
    private Location _p1;
    private Location _p2;

    /**
     * Constructor.
     *
     * @param p1  The first point of the cuboid area.
     * @param p2  The seconds point of the cuboid area.
     */
    public RegionSelection(Location p1, Location p2) {
        PreCon.notNull(p1);
        PreCon.notNull(p2);

        _p1 = p1;
        _p2 = p2;
    }

    /**
     * Get the first point of the cuboid area.
     */
    public Location getP1() {
        return _p1;
    }

    /**
     * Get the second point of the cuboid area.
     */
    public Location getP2() {
        return _p2;
    }

}
