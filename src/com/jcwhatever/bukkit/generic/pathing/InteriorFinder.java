package com.jcwhatever.bukkit.generic.pathing;

import com.jcwhatever.bukkit.generic.regions.Region;
import com.jcwhatever.bukkit.generic.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

/**
 * Gets air blocks inside an enclosed space
 * @author JC The Pants
 *
 */
public class InteriorFinder {
	
	private Set<Location> _invalidNodes;
	private Set<Location> _validNodes;
	
	private Location _start;
	private Region _boundaries;
		
	private void init(Region boundaries) {
		_validNodes = new HashSet<Location>((int)boundaries.getVolume() + 10);
		_invalidNodes = new HashSet<Location>((int)boundaries.getVolume() + 10);
	}
	
	public InteriorResults searchInterior(Location start, Region boundaries) {
		
		start = LocationUtils.getBlockLocation(start);
		
		_start = start;
		_boundaries = boundaries;
		
		init(boundaries);
		
		// Add start node to open nodes
		_validNodes.add(start);

		// Add valid adjacent nodes to open list
		searchAdjacent(start);

		return new InteriorResults(_validNodes);
	}
	
	private void searchAdjacent(Location node) {
		// set of possible walk to locations adjacent to current tile
		
		// column validations, work from top down, skip columns that are false
		Boolean[][] columns = new Boolean[][] {
			{ true, true,  true },
			{ true, true, true },
			{ true, true,  true }
		};
		
		boolean isBelowStart = node.getBlockY() <= _start.getBlockY(); 
		
		byte yStart =  (byte)(isBelowStart ? 1 : -1);
				
		for (byte y = yStart; isBelowStart ? y >= -1 : y <= 1; y += (isBelowStart ? -1 : 1)) {
			for (byte x = -1; x <= 1; x++) {
				for (byte z = -1; z <= 1; z++) {

					if (x == 0 && z == 0 && y == 0)
						continue;
					
					// get instance of candidate node
					Location candidate = node.clone().add(x, y, z);
					
					// check if candidate is already considered
					if (isClosed(candidate)) {
						invColumn(columns, x, z);
						continue;
					}
					
					if (_validNodes.contains(candidate)) {
						continue;
					}

					
					if (!columns[x + 1][z + 1]) {
						//_invalidNodes.add(candidate);
						continue;
					}
					
					// make sure candidate is within boundaries
					if (!_boundaries.contains(candidate)) {
						_invalidNodes.add(candidate);
						continue;
					}
					
					// make sure candidate is air
					if (candidate.getBlock().getType() != Material.AIR) {
						_invalidNodes.add(candidate);
						invColumn(columns, x, z);
						continue;
					}
										
					// Check for diagonal obstruction
					if (x != 0 && z != 0) {
						Location diagX = node.clone().add(x, y, 0),
								 diagZ = node.clone().add(0, y, z);

						if(!GroundPathCheck.isTransparent(diagX.getBlock().getType()) &&
						   !GroundPathCheck.isTransparent(diagZ.getBlock().getType())) {
							_invalidNodes.add(candidate);
							invColumn(columns, x, z);
							continue;
						}
					}
					
					// check for adjacent obstruction
					if (y != 0) {
						Location middle = node.clone().add(0, y, 0),
								 below = node.clone().add(x, 0, z);

						if (!GroundPathCheck.isTransparent(middle.getBlock().getType()) &&
							!GroundPathCheck.isTransparent(below.getBlock().getType())) {
							continue;
						}
					}
					
					// check for corner obstruction
					if (x != 0 && y != 0 && z != 0) {
						Location adjac1 = node.clone().add(x, 0, 0),
								 adjac2 = node.clone().add(0, 0, z),
								 middle = node.clone().add(0, y, 0);
						
						if (!GroundPathCheck.isTransparent(adjac1.getBlock().getType()) &&
								!GroundPathCheck.isTransparent(adjac2.getBlock().getType()) &&
								!GroundPathCheck.isTransparent(middle.getBlock().getType())) {
							continue;
						}
					}
					
					_validNodes.add(candidate);
					
					searchAdjacent(candidate);

				}
			}
		}
		
		
	}

	
	private static void invColumn(Boolean[][] columns, int x, int z) {
		columns[x + 1][z + 1] = false;
	}

	private boolean isClosed(Location node) {
		return _invalidNodes.contains(node);
	}
	
	public static class InteriorResults {
		private final Set<Location> _air;
				
		public InteriorResults (Set<Location> air) {
			_air = air;
		}
		
		public Set<Location> getNodes() {
			return _air;
		}
		
		public int getVolume() {
			return _air.size();
		}
		
	}
}
