package com.jcwhatever.bukkit.generic.pathing;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.jcwhatever.bukkit.generic.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.jcwhatever.bukkit.generic.utils.Utils;

/**
 * Gets all locations that can be pathed to from
 * the start point within the specified range.
 * @author JC The Pants
 *
 */
public class PathAreaFinder {
	private Set<Location> _invalidNodes;
	private Set<Location> _validNodes;

	private Location _start;

	private int _maxRange = 16;
	private byte _maxDropHeight = 1;
	private int _maxIterations = -1;

	private int _startX, _startY, _startZ;
	private World _world;

	private void init() {
		_validNodes = new HashSet<Location>(_maxRange * _maxRange * _maxRange);
		_invalidNodes = new HashSet<Location>(_maxRange * _maxRange * _maxRange);
	}

	public byte getMaxDropHeight() {
		return _maxDropHeight; 
	}

	public void setMaxDropHeight(byte value) {
		_maxDropHeight = value;
	}

	public int getMaxIterations() {
		return _maxIterations;
	}

	public void setMaxIterations(int iterations) {
		_maxIterations = iterations;
	}


	public PathAreaResults searchPossibleDestinations(Location start, int maxRange, int maxTravelDistance) {

		start = LocationUtils.getBlockLocation(start);

		_maxRange = maxRange;
		_start = start;

		_startX = start.getBlockX();
		_startY = start.getBlockY();
		_startZ = start.getBlockZ();

		_world = start.getWorld();

		init();

		// Add start node to open nodes
		_validNodes.add(start);

		// Add valid adjacent nodes to open list
		searchAdjacent(start);

		// validate path distances
		if (maxTravelDistance > -1) {
			GroundPathCheck astar = new GroundPathCheck();
			astar.setMaxDropHeight(_maxDropHeight);
			astar.setMaxRange(maxRange);

			Iterator<Location> iterator = _validNodes.iterator();
			
			while(iterator.hasNext()) {
				Location location = iterator.next();

				if (location.equals(start))
					continue;

				int pathDistance = astar.searchDistance(start, location);
				if (pathDistance == -1 || pathDistance > maxTravelDistance)
					iterator.remove();
			}
		}


		return new PathAreaResults(_validNodes, _invalidNodes);
	}

	private void searchAdjacent(Location node) {
		// set of possible walk to locations adjacent to current tile

		byte dropHeight = (byte)(-_maxDropHeight);

		// column validations, work from top down, skip columns that are false
		Boolean[][] columns = new Boolean[][] {
				{ true, true,  true },
				{ true, false, true },
				{ true, true,  true }
		};

		for (byte y = 1; y >= dropHeight; y--) {
			for (byte x = -1; x <= 1; x++) {
				for (byte z = -1; z <= 1; z++) {

					if (!columns[x + 1][z + 1])
						continue;

					// get instance of candidate node
					Location candidate = node.clone().add(x, y, z);

					// check if candidate is already closed
					if (isClosed(candidate) || _validNodes.contains(candidate)) {
						continue;
					}

					int xRange = Math.abs(_start.getBlockX() - node.getBlockX());
					int yRange = Math.abs(_start.getBlockY() - node.getBlockY());
					int zRange = Math.abs(_start.getBlockZ() - node.getBlockZ());

					// check x & z range
					if ((_maxRange - xRange < 0) || 
							(_maxRange - zRange < 0)) {

						invColumn(columns, x, z);
						continue;
					}

					// check y range
					if ((_maxRange - yRange < 0)) {
						continue;
					}

					// Check for diagonal obstruction
					if (x != 0 && z != 0 && y >= 0) {
						Location diagX = node.clone().add(x, y, (short)0),
								diagZ = node.clone().add((short)0, y, z);

						if(!isValid(diagX, _world, _startX, _startY, _startZ) && 
								!isValid(diagZ, _world, _startX, _startY, _startZ)) {
							invColumn(columns, x, z);
							continue;
						}
					}

					// check candidate to see if its valid
					if (!isValid(candidate, _world, _startX, _startY, _startZ)) {

						// invalidate column if material is NOT transparent
						if (!GroundPathCheck.isTransparent(candidate.getBlock().getType())) {
							invColumn(columns, x, z);
						}

						continue;
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

	static boolean isValid(Location loc, World world, int startX, int startY, int startZ) {
		Block block = loc.getBlock();
		Material material = block.getType();

		// check if block is a surface
		if (!GroundPathCheck.isSurface(material))
			return false;

		// check head room
		Material headMaterial = block.getRelative(0, 2, 0).getType();
		if (!GroundPathCheck.isTransparent(headMaterial)) {
			return false;
		}

		// block above current
		Block above = block.getRelative(0, 1, 0);

		// check if block above is transparent
		return GroundPathCheck.isTransparent(above.getType());

	}


	public static class PathAreaResults {
		private final Set<Location> _valid;
		private final Set<Location> _invalid;

		public PathAreaResults (Set<Location> validDestinations, Set<Location> invalidDestinations) {
			_valid = validDestinations;
			_invalid = invalidDestinations;
		}

		public Set<Location> getValid() {
			return _valid;
		}

		public Set<Location> getInvalid() {
			return _invalid;
		}
	}

}