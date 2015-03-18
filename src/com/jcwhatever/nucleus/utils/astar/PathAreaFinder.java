/*
 * This file is part of NucleusFramework for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.nucleus.utils.astar;

import com.jcwhatever.nucleus.utils.Coords3Di;
import com.jcwhatever.nucleus.utils.LocationUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.astar.AStarResult.AStarResultStatus;
import com.jcwhatever.nucleus.utils.astar.basic.AStarNodeContainer;
import com.jcwhatever.nucleus.utils.materials.Materials;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Set;

/**
 * Gets all locations that can be pathed to from the start point within
 * the specified range.
 *
 * <p>Meant to be used as a means of caching valid mob destination
 * locations from a fixed path start point to remove the need for using
 * A-Star pathing in real time for validation purposes.</p>
 *
 * <p> Uses a provided AStar implementation for final validation of destinations. </p>
 *
 * <p>Not intended for real-time use.</p>
 */
public class PathAreaFinder {

    /**
     * Search for valid path destinations around the specified
     * path start point.
     *
     * @param start  The path start location.
     */
    public PathAreaResults search(AStar astar, Location start) {
        PreCon.notNull(start);

        start = LocationUtils.getBlockLocation(start);
        LocationUtils.findSurfaceBelow(start, start);

        FinderContext context = new FinderContext(astar, start);

        // Add start node to open nodes
        context.validNodes.add(start);

        // Add valid adjacent nodes to open list
        searchAdjacent(context, start);

        return new PathAreaResults(context);
    }

    /*
     * Search for valid nodes adjacent to the specified node.
     */
    private void searchAdjacent(FinderContext context, Location node) {

        byte dropHeight = (byte)(-context.astar.getMaxDropHeight());

        // column validations, work from top down, skip columns that are false
        boolean[][] columns = new boolean[][] {
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
                    if (context.invalidNodes.contains(candidate) || context.validNodes.contains(candidate)) {
                        continue;
                    }

                    int xRange = Math.abs(context.start.getBlockX() - node.getBlockX());
                    int yRange = Math.abs(context.start.getBlockY() - node.getBlockY());
                    int zRange = Math.abs(context.start.getBlockZ() - node.getBlockZ());

                    // check x & z range
                    if ((context.astar.getRange() - xRange < 0) ||
                            (context.astar.getRange() - zRange < 0)) {

                        columns[x + 1][z + 1] = false;
                        continue;
                    }

                    // check y range
                    if ((context.astar.getRange() - yRange < 0)) {
                        continue;
                    }

                    // Check for diagonal obstruction
                    if (x != 0 && z != 0 && y >= 0) {
                        Location diagX = node.clone().add(x, y, (short)0),
                                diagZ = node.clone().add((short)0, y, z);

                        if(!isValid(context, diagX) && !isValid(context, diagZ)) {
                            columns[x + 1][z + 1] = false;
                            continue;
                        }
                    }

                    // check candidate to see if its valid
                    if (!isValid(context, candidate)) {

                        // invalidate column if material is NOT transparent
                        if (!Materials.isTransparent(candidate.getBlock().getType())) {
                            columns[x + 1][z + 1] = false;
                        }

                        continue;
                    }

                    context.validNodes.add(candidate);

                    searchAdjacent(context, candidate);

                }
            }
        }
    }

    /*
     *  Determine if a node is a valid location.
     */
    private boolean isValid(FinderContext context, Location loc) {

        Block block = loc.getBlock();
        Material material = block.getType();

        // check if block is a surface
        return Materials.isSurface(material) && context.search(loc).getStatus() == AStarResultStatus.RESOLVED;
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
         * @param context  The path search context.
         */
        PathAreaResults (FinderContext context) {
            _valid = context.validNodes;
            _invalid = context.invalidNodes;
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

    private static class FinderContext {
        AStar astar;
        Location start;
        Set<Location> invalidNodes;
        Set<Location> validNodes;

        FinderContext(AStar astar, Location start) {
            this.astar = astar;
            this.start = start;

            double range = astar.getRange();

            this.invalidNodes = new HashSet<Location>((int)(range * range * range));
            this.validNodes = new HashSet<Location>((int)(range * range * range));
        }

        AStarResult search(Location location) {
            return astar.search(Coords3Di.fromLocation(start), Coords3Di.fromLocation(location),
                    new AStarNodeContainer(astar.getExaminer()));
        }
    }

}