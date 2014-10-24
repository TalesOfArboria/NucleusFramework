package com.jcwhatever.bukkit.generic.pathing;

import org.bukkit.Location;
import org.bukkit.Material;

/**
 * Stores information about a path node (location)
 * @author JC The Pants
 *
 */
public final class PathNode implements Comparable<PathNode>{

	public final short  x, y, z;
	public final String id;
	public Location     location;
	public Material     material;
	
	double      g = -1, h = -1, f = -1;
	PathNode    parent;
		
	PathNode(short x, short y, short z, PathNode parent) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = GroundPathCheck.createId(x, y, z);
		this.parent = parent;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PathNode) {
			PathNode node = (PathNode)obj;

			return node.x == x && 
				   node.y == y && 
				   node.z == z;
		}
		return false;
	}

	@Override
	public int compareTo(PathNode other) {
		if (other.f < f)
			return 1;
		if (other.f > f)
			return -1;
		
		return 0;
	}
	
		
}
