package com.jcwhatever.bukkit.generic.pathing.astar;

import com.sun.istack.internal.Nullable;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * Stores information about a path node location.
 */
public interface PathNode extends Comparable<PathNode> {

    /**
     * Get the parent node.
     */
    @Nullable
    public PathNode getParentNode();

    /**
     * Get the X axis offset from the parent location X axis.
     */
    public int getXParentOffset();

    /**
     * Get the Y axis offset from the parent location Y axis.
     */
    public int getYParentOffset();

    /**
     * Get the Z axis offset from the parent location Z axis.
     */
    public int getZParentOffset();

    /**
     * Get the X axis offset from the start location X axis.
     */
    public int getXStartOffset();

    /**
     * Get the Y axis offset from the start location Y axis.
     */
    public int getYStartOffset();

    /**
     * Get the Z axis offset from the start location Z axis.
     */
    public int getZStartOffset();

    /**
     * Get the start location of the path.
     */
    public Location getStartLocation();

    /**
     * Get the end location of the path.
     */
    public Location getEndLocation();

    /**
     * Get the node location.
     */
    public Location getLocation();

    /**
     * Get the G score.
     */
    public int getGScore();

    /**
     * Get the H score.
     */
    public long getHScore();

    /**
     * Get the F score.
     */
    public long getFScore();

    /**
     * Get the material of the block at the
     * location the path node represents.
     */
    public Material getMaterial();

    /**
     * Determine if the location is a transparent
     * block that entities can move through.
     */
    public boolean isTransparent();

    /**
     * Determine if the location is a block
     * that entities can walk on.
     */
    public boolean isSurface();
}
