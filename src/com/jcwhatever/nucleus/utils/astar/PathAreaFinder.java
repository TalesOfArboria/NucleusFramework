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

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.astar.basic.AStarNodeContainer;
import com.jcwhatever.nucleus.utils.coords.Coords3Di;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.utils.materials.Materials;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
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

        Iterator<Location> iterator = context.validNodes.iterator();
        while (iterator.hasNext()) {
            Location location = iterator.next();
            if (context.search(location).getStatus() != AStarResult.AStarResultStatus.RESOLVED) {
                iterator.remove();
                context.invalidNodes.add(location);
            }
        }

        return new PathAreaResults(context);
    }

    /*
     * Search for valid nodes adjacent to the specified node.
     */
    private void searchAdjacent(FinderContext context, Location node) {

        LinkedList<StackItem> stack = new LinkedList<>();
        stack.push(new StackItem(node));
        StackItem curr = stack.peek();

        byte dropHeight = (byte)(-context.astar.getMaxDropHeight());

        while (!stack.isEmpty()) {

            start:
            {
                for (; curr.y >= dropHeight; curr.y--) {
                    for (; curr.x <= 1; curr.x++) {
                        for (; curr.z <= 1; curr.z++) {

                            if (!curr.columns[curr.x + 1][curr.z + 1])
                                continue;

                            // get instance of candidate node
                            Location candidate = curr.node.clone().add(curr.x, curr.y, curr.z);

                            // check if candidate is already checked
                            if (context.invalidNodes.contains(candidate)) {
                                continue;
                            }

                            if (context.validNodes.contains(candidate)) {
                                curr.columns[curr.x + 1][curr.z + 1] = false;
                                continue;
                            }

                            int xRange = Math.abs(context.start.getBlockX() - candidate.getBlockX());
                            int yRange = Math.abs(context.start.getBlockY() - candidate.getBlockY());
                            int zRange = Math.abs(context.start.getBlockZ() - candidate.getBlockZ());

                            // check x & z range
                            if ((context.astar.getRange() - xRange < 0) ||
                                    (context.astar.getRange() - zRange < 0)) {

                                curr.columns[curr.x + 1][curr.z + 1] = false;
                                continue;
                            }

                            // check y range
                            if ((context.astar.getRange() - yRange < 0)) {
                                continue;
                            }

                            // Check for diagonal obstruction
                            if (curr.x != 0 && curr.z != 0 && curr.y >= 0) {
                                Location diagX = curr.node.clone().add(curr.x, curr.y, (short) 0),
                                        diagZ = curr.node.clone().add((short) 0, curr.y, curr.z);

                                if (!isValid(diagX) && !isValid(diagZ)) {
                                    curr.columns[curr.x + 1][curr.z + 1] = false;
                                    continue;
                                }
                            }

                            // check candidate to see if its valid
                            if (!isValid(candidate)) {

                                // invalidate column if material is NOT transparent
                                if (!Materials.isTransparent(candidate.getBlock().getType())) {
                                    curr.columns[curr.x + 1][curr.z + 1] = false;
                                }

                                context.invalidNodes.add(candidate);
                                continue;
                            }

                            context.validNodes.add(candidate);

                            //searchAdjacent(context, candidate);
                            curr.z++;
                            stack.push(new StackItem(candidate));
                            curr = stack.peek();


                            break start;
                        }
                        curr.z = -1;
                    }
                    curr.x = -1;
                }

                stack.pop();
                if (stack.isEmpty()) {
                    return;
                }
                else {
                    curr = stack.peek();
                }
            }

            // "start" break location
        }
    }

    /*
     *  Determine if a node is a valid location.
     */
    private boolean isValid(Location loc) {

        Block block = loc.getBlock();

        // check if block is a surface
        if (!Materials.isSurface(block.getType()))
            return false;

        Block above = block.getRelative(0, 1, 0);
        if (!Materials.isTransparent(above.getType()))
            return false;

        Block above1 = block.getRelative(0, 2, 0);
        return Materials.isTransparent(above1.getType());
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
                    new AStarNodeContainer());
        }
    }

    private static class StackItem {
        final Location node;
        byte x = -1;
        byte y = 1;
        byte z = -1;
        // column validations, work from top down, skip columns that are false
        boolean[][] columns = new boolean[][] {
                { true, true,  true },
                { true, false, true },
                { true, true,  true }
        };

        StackItem(Location node) {
            this.node = node;
        }
    }
}