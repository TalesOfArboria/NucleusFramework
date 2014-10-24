package com.jcwhatever.bukkit.generic.pathing;

import java.util.Stack;

import javax.annotation.Nullable;

import com.jcwhatever.bukkit.generic.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.material.Gate;

import com.jcwhatever.bukkit.generic.utils.Utils;


/**
 * AStar pathing algorithm. Not intended for AI movement.
 * Primary use is to determine if a mob can reach a
 * destination (by walking) and measure approx. distance to
 * get there. Locations are automatically adjusted to 
 * path along the ground, regardless of the provided start and end 
 * location elevation.
 * 
 * @author JC The Pants
 *
 */
public final class GroundPathCheck {

	private final PathNodeMap _closedNodes;
	private final PathNodeMap _openNodes;

	private PathNode _startNode;
	private String _endId;

	private int _maxRange = 20;
	private byte _maxDropHeight = 1;
	private int _maxIterations = -1;

	private int _startX, _startY, _startZ, _endX, _endY, _endZ;
	private World _world;

	public GroundPathCheck() {
		_openNodes = new PathNodeMap();
		_closedNodes = new PathNodeMap();
	}

	private void init() {
		_openNodes.clear();
		_closedNodes.clear();
	}

	public int getMaxRange() {
		return _maxRange;
	}

	public void setMaxRange(int value) {
		_maxRange = value;
	}

	public byte getMaxDropHeight() {
		return _maxDropHeight; 
	}

	public void setMaxDropHeight(int height) {
		_maxDropHeight = (byte)height;
	}

	public int getMaxIterations() {
		return _maxIterations;
	}

	public void setMaxIterations(int iterations) {
		_maxIterations = iterations;
	}

	public int searchDistance(Location start, Location end) {
 		
		PathNode destination = searchOnly(start, end);
		if (destination == null)
			return -1;

		int totalNodes = 0;
		
		while(destination.parent != null) {
			destination = destination.parent;
			totalNodes++;
		}
		
		return totalNodes;
	}

	public Stack<PathNode> search(Location start, Location end) {

		PathNode destination = searchOnly(start, end);
		if (destination == null)
			return null;

		Stack<PathNode> paths = new Stack<PathNode>();

		paths.push(destination);
		destination.location = getLocation(destination, _world, _startX, _startY, _startZ);

		while(destination.parent != null) {
			paths.push(destination.parent);
			destination.parent.location = getLocation(destination.parent, _world, _startX, _startY, _startZ);
			destination = destination.parent;
		}

		return paths;
	}


	public static boolean isSurface(Material material) {
		if (material == Material.LAVA || 
				material == Material.STATIONARY_LAVA ||
				material == Material.FIRE ||
				material == Material.WHEAT ||
				material == Material.LADDER ||
				material == Material.FENCE ||
				material == Material.FENCE_GATE ||
				material == Material.NETHER_FENCE) {
			return false;
		}
		return !isTransparent(material);
	}

	public static boolean isTransparent(Material material) {
		return material == Material.AIR ||
				material == Material.STATIONARY_WATER ||
				material == Material.WATER ||
				material == Material.SAPLING ||
				material == Material.WEB ||
				material == Material.DOUBLE_PLANT ||
				material == Material.YELLOW_FLOWER ||
				material == Material.RED_ROSE ||
				material == Material.BROWN_MUSHROOM ||
				material == Material.RED_MUSHROOM ||
				material == Material.TORCH ||
				material == Material.SIGN ||
				material == Material.WALL_SIGN ||
				material == Material.SIGN_POST ||
				material == Material.REDSTONE_WIRE ||
				material == Material.REDSTONE_TORCH_ON ||
				material == Material.REDSTONE_TORCH_OFF ||
				material == Material.RAILS ||
				material == Material.LADDER;
	}


	public static Location findSolidBlockBelow(Location searchLoc) {
		searchLoc = LocationUtils.getBlockLocation(searchLoc);
		
		if (!isTransparent(searchLoc.getBlock().getType()))
			return searchLoc;

		searchLoc.add(0, -1, 0);
		Block current = searchLoc.getBlock();

		while (!isTransparent(current.getType()) && !isSurface(current.getType())) {
			searchLoc.add(0, -1, 0);
			current = searchLoc.getBlock();

			if (searchLoc.getY() < 0) {
				return null;
			}
		}
		return searchLoc;
	}


	private PathNode searchOnly(Location start, Location end) {
		
		start = GroundPathCheck.findSolidBlockBelow(start);
		end = GroundPathCheck.findSolidBlockBelow(end);

		if (!validateRange(start, end, _maxRange)) {
			return null;
		}

		init();

		_startNode = new PathNode((short)0, (short)0, (short)0, null);

		_startX = start.getBlockX();
		_startY = start.getBlockY();
		_startZ = start.getBlockZ();

		_endX = end.getBlockX();
		_endY = end.getBlockY();
		_endZ = end.getBlockZ();

		_world = start.getWorld();

		// get id of destination node for reference
		_endId = createId(_endX - _startX, _endY - _startY, _endZ - _startZ); 

		// Add start node to open nodes
		_openNodes.put(_startNode);

		// Add valid adjacent nodes to open list
		searchAdjacent(_startNode);



		PathNode current = null;

		// iterate until destination is found
		// or unable to continue

		int iterations = 0;
		while (canSearch()) {

			// get candidate/open node closest to destination
			current = getFNode();

			// search for more candidate nodes
			searchAdjacent(current);

			iterations ++;
			if (_maxIterations > 0 && iterations >= _maxIterations) {
				break;
			}
		}

		// no valid nodes found
		if (current == null) {
			return null;
		}

		// see if destination node was reached
		if (!_closedNodes.contains(_endId)) {
			return null;
		}

		return current;
	}


	private boolean canSearch() {
		// check if open list is empty, if it is no path has been found
		if (_openNodes.size() == 0)
			return false;

		return !_closedNodes.contains(_endId);
	}

	/**
	 * Get open node with the lowest F score (closest to destination)
	 * @return
	 */
	@Nullable
	private PathNode getFNode() {

		double f = 0;
		PathNode fNode = null;

		for (PathNode candidate : _openNodes.values()) {

			double candidateF = candidate.f;

			if (f == 0) {
				fNode = candidate;
				f = candidateF;
				continue;
			} 

			if (candidateF < f) {
				fNode = candidate;
				f = candidateF;
			}
		}

		if (fNode != null) {
		    _openNodes.remove(fNode.id);

		    _closedNodes.put(fNode);
		}

		return fNode;
	}


	private void searchAdjacent(PathNode node) {
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
					PathNode candidate = createChild(node, x, y, z);

					// check if candidate is already closed
					if (isClosed(candidate)) {
						continue;
					}

					// check x & z range
					if ((_maxRange - Math.abs(node.x) < 0) || 
							(_maxRange - Math.abs(node.z) < 0)) {

						invColumn(columns, x, z);
						continue;
					}

					// check y range
					if ((_maxRange - Math.abs(node.y) < 0)) {
						continue;
					}

					// Check for diagonal obstruction
					if (x != 0 && z != 0 && y >= 0) {
						PathNode diagX = createChild(node, x, y, (short)0),
								diagZ = createChild(node, (short)0, y, z);

						if(!isValid(diagX, _world, _startX, _startY, _startZ) && 
								!isValid(diagZ, _world, _startX, _startY, _startZ)) {
							invColumn(columns, x, z);
							continue;
						}
					}
					
					// check candidate to see if its valid
					if (!isValid(candidate, _world, _startX, _startY, _startZ)) {

						// invalidate column if material is NOT transparent
						if (!isTransparent(candidate.material)) {
							invColumn(columns, x, z);
						}

						continue;
					}

					candidate.g = calculateG(candidate);
					candidate.h = calculateH(candidate, _startX, _startY, _startZ, _endX, _endY, _endZ);
					candidate.f = candidate.g + candidate.h;

					PathNode open = _openNodes.get(candidate.id);

					if (open != null && candidate.g < open.g) {
						open.parent = node;
						candidate.g = calculateG(candidate);
						candidate.f = candidate.g + candidate.h;

					} else {
						_openNodes.put(candidate);
					}

				}
			}
		}
	}

	private static void invColumn(Boolean[][] columns, int x, int z) {
		columns[x + 1][z + 1] = false;
	}

	private boolean isClosed(PathNode node) {
		return _closedNodes.contains(node.id);
	}


	static int calculateG(PathNode node) {

		int g = 0;
		PathNode parent;
		PathNode current = node;

		while ((parent = current.parent) != null) {

			int disX = Math.abs(current.x - parent.x), 
					disY = Math.abs(current.y - parent.y), 
					disZ = Math.abs(current.z - parent.z);

			if (disX == 1 && disY == 1 && disZ == 1) {
				g += 1.7;
			} else if (((disX == 1 || disZ == 1) && disY == 1) || 
					((disX == 1 || disZ == 1) && disY == 0)) {
				g += 1.4;
			} else {
				g += 1.0;
			}

			// move backwards a tile
			current = parent;
		}
		return g;
	}

	static double calculateH(PathNode node, int startX, int startY, int startZ, int endX, int endY, int endZ) {

		int disX = (startX + node.x) - endX, 
				disY = (startY + node.y) - endY, 
				disZ = (startZ + node.z) - endZ;

		return (disX * disX) + (disY * disY) + (disZ * disZ);
	}

	static PathNode createChild(PathNode parent, short offsetX, short offsetY, short offsetZ) {
		PathNode child = new PathNode((short)(parent.x + offsetX), (short)(parent.y + offsetY), (short)(parent.z + offsetZ), parent);
		return child;
	}

	static boolean isValid(PathNode node, World world, int startX, int startY, int startZ) {
		Location loc = getLocation(node, world, startX, startY, startZ);
		Block block = loc.getBlock();
		Material material = block.getType();
		node.material = material;

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

		// check for open gate
		if (above.getType() == Material.FENCE_GATE) {
			Gate gate = new Gate(above.getData());
			return gate.isOpen();
		}

		// check if block above is transparent
		return isTransparent(above.getType());

	}

	static Location getLocation(PathNode node, World world, int startX, int startY, int startZ) {
		return new Location(world, startX + node.x, startY + node.y, startZ + node.z);
	}

	static String createId(int offsetX, int offsetY, int offsetZ) {
		return offsetX + "," + offsetY + "," + offsetZ;
	}

	public static boolean validateRange(Location start, Location end, int maxRange) {
		return start.distance(end) <= maxRange;
	}





}
