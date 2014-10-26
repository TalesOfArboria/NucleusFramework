/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.jcwhatever.bukkit.generic.pathing;

import com.jcwhatever.bukkit.generic.extended.MaterialExt;
import com.jcwhatever.bukkit.generic.pathing.astar.AStar;
import com.jcwhatever.bukkit.generic.pathing.astar.AStar.LocationAdjustment;
import com.jcwhatever.bukkit.generic.pathing.astar.AStarUtils;
import com.jcwhatever.bukkit.generic.pathing.astar.PathNode;
import com.jcwhatever.bukkit.generic.utils.LocationUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Gets all locations that can be pathed to from
 * the start point within the specified range.
 *
 * <p>
 *     Uses a provided AStar implementation for final validation
 *     of destinations.
 * </p>
 */
public class PathAreaFinder<T extends PathNode> {

    private final AStar<T> _pathValidator;

    private Set<Location> _invalidNodes;
    private Set<Location> _validNodes;
    private Location _start;

    /**
     * Constructor.
     *
     * @param pathValidator  AStar implementation which provides settings and path validation.
     */
    public PathAreaFinder(AStar<T> pathValidator) {
        PreCon.notNull(pathValidator);

        _pathValidator = pathValidator;
    }

    /**
     * Search for valid path destinations around the specified
     * path start point.
     *
     * @param start  The path start location.
     */
    public PathAreaResults search(Location start) {
        PreCon.notNull(start);

        start = LocationUtils.getBlockLocation(start);

        _start = start;

        init();

        // Add start node to open nodes
        _validNodes.add(start);

        // Add valid adjacent nodes to open list
        searchAdjacent(start);

        // validate paths against AStar implementation.
        Iterator<Location> iterator = _validNodes.iterator();
        while(iterator.hasNext()) {
            Location location = iterator.next();

            if (location.equals(start))
                continue;

            int pathDistance = _pathValidator.getPathDistance(start, location, LocationAdjustment.FIND_SURFACE);
            if (pathDistance == -1)
                iterator.remove();
        }

        return new PathAreaResults(_validNodes, _invalidNodes);
    }

    /*
     * Search for valid nodes adjacent to the specified node.
     */
    private void searchAdjacent(Location node) {
        // set of possible walk to locations adjacent to current tile

        byte dropHeight = (byte)(-_pathValidator.getMaxDropHeight());

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

                    // check if candidate is already checked
                    if (_invalidNodes.contains(candidate) || _validNodes.contains(candidate)) {
                        continue;
                    }

                    int xRange = Math.abs(_start.getBlockX() - node.getBlockX());
                    int yRange = Math.abs(_start.getBlockY() - node.getBlockY());
                    int zRange = Math.abs(_start.getBlockZ() - node.getBlockZ());

                    // check x & z range
                    if ((_pathValidator.getMaxRange() - xRange < 0) ||
                            (_pathValidator.getMaxRange() - zRange < 0)) {

                        columns[x + 1][z + 1] = false;
                        continue;
                    }

                    // check y range
                    if ((_pathValidator.getMaxRange() - yRange < 0)) {
                        continue;
                    }

                    // Check for diagonal obstruction
                    if (x != 0 && z != 0 && y >= 0) {
                        Location diagX = node.clone().add(x, y, (short)0),
                                 diagZ = node.clone().add((short)0, y, z);

                        if(!isValid(diagX) && !isValid(diagZ)) {
                            columns[x + 1][z + 1] = false;
                            continue;
                        }
                    }

                    // check candidate to see if its valid
                    if (!isValid(candidate)) {

                        // invalidate column if material is NOT transparent
                        if (!MaterialExt.isTransparent(candidate.getBlock().getType())) {
                            columns[x + 1][z + 1] = false;
                        }

                        continue;
                    }

                    _validNodes.add(candidate);

                    searchAdjacent(candidate);

                }
            }
        }
    }

    /*
     * Initialize hash sets
     */
    private void init() {
        int maxRange = _pathValidator.getMaxRange();
        _validNodes = new HashSet<Location>(maxRange * maxRange * maxRange);
        _invalidNodes = new HashSet<Location>(maxRange * maxRange * maxRange);
    }

    /*
     *  Determine if a node is a valid location.
     */
    private boolean isValid(Location loc) {
        Block block = loc.getBlock();
        Material material = block.getType();

        // check if block is a surface
        if (!MaterialExt.isSurface(material))
            return false;

        // check head room
        return AStarUtils.hasRoomForEntity(loc, _pathValidator.getEntityHeight(), _pathValidator.getDoorPathMode());
    }

    /**
     * Path area search results.
     */
    public static class PathAreaResults {
        private final Set<Location> _valid;
        private final Set<Location> _invalid;

        /**
         * Constructor.
         *
         * @param validDestinations    Valid destination locations.
         * @param invalidDestinations  Invalid destionation locations.
         */
        PathAreaResults (Set<Location> validDestinations, Set<Location> invalidDestinations) {
            _valid = validDestinations;
            _invalid = invalidDestinations;
        }

        /**
         * Get the valid path destinations found.
         */
        public Set<Location> getValid() {
            return _valid;
        }

        /**
         * Get invalid path destinations found.
         */
        public Set<Location> getInvalid() {
            return _invalid;
        }
    }

}