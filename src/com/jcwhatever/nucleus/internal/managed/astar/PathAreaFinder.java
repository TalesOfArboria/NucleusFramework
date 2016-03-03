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


package com.jcwhatever.nucleus.internal.managed.astar;

import com.jcwhatever.nucleus.managed.astar.IAStarSettings;
import com.jcwhatever.nucleus.managed.astar.IAStarResult;
import com.jcwhatever.nucleus.managed.astar.IAStarResult.ResultStatus;
import com.jcwhatever.nucleus.managed.astar.area.IPathAreaFinder;
import com.jcwhatever.nucleus.managed.astar.area.IPathAreaResult;
import com.jcwhatever.nucleus.managed.astar.examiners.AStarWorldExaminer;
import com.jcwhatever.nucleus.managed.astar.examiners.IAStarNodeExaminer;
import com.jcwhatever.nucleus.managed.astar.nodes.AStarNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.Coords3Di;
import com.jcwhatever.nucleus.utils.coords.ICoords3Di;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.utils.coords.MutableCoords3Di;
import com.jcwhatever.nucleus.utils.materials.Materials;
import org.bukkit.Location;
import org.bukkit.World;
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
 * <p>Uses AStar for final validation of destinations. </p>
 *
 * <p>Not intended for real-time use.</p>
 */
class PathAreaFinder implements IPathAreaFinder {

    private static final PathAreaFinder INSTANCE = new PathAreaFinder();

    static PathAreaFinder get() {
        return INSTANCE;
    }

    @Override
    public IPathAreaResult search(Location start, IAStarSettings settings) {
        return search(settings, new AStarWorldExaminer<AStarNode>(start.getWorld()), start);
    }

    /**
     * Search for valid path destinations around the specified
     * path start point.
     *
     * @param start  The path start location.
     */
    PathAreaResults search(IAStarSettings settings,
                                  IAStarNodeExaminer<AStarNode> examiner,
                                  Location start) {
        PreCon.notNull(start);

        start = LocationUtils.getBlockLocation(start);
        LocationUtils.findSurfaceBelow(start, start);

        Coords3Di startCoords = Coords3Di.fromLocation(start);
        FinderContext context = new FinderContext(settings, examiner, startCoords, start.getWorld());

        // Add start node to open nodes
        context.validNodes.add(startCoords);

        // Add valid adjacent nodes to open list
        searchAdjacent(context, startCoords);

        Iterator<ICoords3Di> iterator = context.validNodes.iterator();
        while (iterator.hasNext()) {
            ICoords3Di coord = iterator.next();
            if (context.search(coord).getStatus() != ResultStatus.RESOLVED) {
                iterator.remove();
                context.invalidNodes.add(coord);
            }
        }

        return new PathAreaResults(context);
    }

    /*
     * Search for valid nodes adjacent to the specified node.
     */
    private void searchAdjacent(FinderContext context, ICoords3Di node) {

        LinkedList<StackItem> stack = new LinkedList<>();
        stack.push(new StackItem(node));
        StackItem curr = stack.peek();

        byte dropHeight = (byte)(-context.settings.getMaxDropHeight());

        while (!stack.isEmpty()) {

            start:
            {
                for (; curr.y >= dropHeight; curr.y--) {
                    for (; curr.x <= 1; curr.x++) {
                        for (; curr.z <= 1; curr.z++) {

                            if (!curr.columns[curr.x + 1][curr.z + 1])
                                continue;

                            // get instance of candidate node
                            Coords3Di candidate = new Coords3Di(node, curr.x, curr.y, curr.z);

                            // check if candidate is already checked
                            if (context.invalidNodes.contains(candidate)) {
                                continue;
                            }

                            if (context.validNodes.contains(candidate)) {
                                curr.columns[curr.x + 1][curr.z + 1] = false;
                                continue;
                            }

                            int xRange = Math.abs(context.start.getX() - candidate.getX());
                            int yRange = Math.abs(context.start.getY() - candidate.getY());
                            int zRange = Math.abs(context.start.getZ() - candidate.getZ());

                            // check x & z range
                            if ((context.settings.getRange() - xRange < 0) ||
                                    (context.settings.getRange() - zRange < 0)) {

                                curr.columns[curr.x + 1][curr.z + 1] = false;
                                continue;
                            }

                            // check y range
                            if ((context.settings.getRange() - yRange < 0)) {
                                continue;
                            }

                            // Check for diagonal obstruction
                            if (curr.x != 0 && curr.z != 0 && curr.y >= 0) {
                                Coords3Di diagX = tempNode(node, curr.x, curr.y, (short) 0, context.diagX),
                                        diagZ = tempNode(node, (short) 0, curr.y, curr.z, context.diagZ);

                                if (!isValid(diagX, context) && !isValid(diagZ, context)) {
                                    curr.columns[curr.x + 1][curr.z + 1] = false;
                                    continue;
                                }
                            }

                            // check candidate to see if its valid
                            if (!isValid(candidate, context)) {

                                // invalidate column if material is NOT transparent
                                if (!Materials.isTransparent(candidate.getBlock(context.world).getType())) {
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
    private boolean isValid(Coords3Di loc, FinderContext context) {

        Block block = loc.getBlock(context.world);

        // check if block is a surface
        if (!Materials.isSurface(block.getType()))
            return false;

        Block above = block.getRelative(0, 1, 0);
        if (!Materials.isTransparent(above.getType()))
            return false;

        Block above1 = block.getRelative(0, 2, 0);
        return Materials.isTransparent(above1.getType());
    }

    private Coords3Di tempNode(ICoords3Di parent, int deltaX, int deltaY, int deltaZ, MutableCoords3Di output) {
        output.setX(parent.getX() + deltaX);
        output.setY(parent.getY() + deltaY);
        output.setZ(parent.getZ() + deltaZ);
        return output;
    }

    /**
     * Path area search results.
     */
    public static class PathAreaResults implements IPathAreaResult {

        final World world;
        final Set<ICoords3Di> valid;
        final Set<ICoords3Di> invalid;

        /**
         * Constructor.
         *
         * @param context  The path search context.
         */
        PathAreaResults (FinderContext context) {
            world = context.world;
            valid = context.validNodes;
            invalid = context.invalidNodes;
        }

        @Override
        public World getWorld() {
            return world;
        }

        @Override
        public Set<ICoords3Di> getValid() {
            return valid;
        }

        @Override
        public Set<ICoords3Di> getInvalid() {
            return invalid;
        }
    }

    private static class FinderContext {
        final World world;
        final IAStarSettings settings;
        final IAStarNodeExaminer<AStarNode> examiner;
        final ICoords3Di start;
        final Set<ICoords3Di> invalidNodes;
        final Set<ICoords3Di> validNodes;

        final MutableCoords3Di diagX = new MutableCoords3Di();
        final MutableCoords3Di diagZ = new MutableCoords3Di();

        FinderContext(IAStarSettings settings, IAStarNodeExaminer<AStarNode> examiner,
                      ICoords3Di start, World world) {
            this.world = world;
            this.settings = settings;
            this.examiner = examiner;
            this.start = start;

            double range = settings.getRange();

            this.invalidNodes = new HashSet<>((int)(range * range * range));
            this.validNodes = new HashSet<>((int)(range * range * range));
        }

        IAStarResult<AStarNode> search(ICoords3Di coords) {

            AStarNode startNode = new AStarNode(start);
            AStarNode destNode = new AStarNode(coords);

            AStarContext<AStarNode> context = new AStarContext<AStarNode>(
                    startNode, destNode, examiner, settings);

            return AStarCoordsSearch.<AStarNode>get().search(context);
        }
    }

    private static class StackItem {
        final ICoords3Di node;
        byte x = -1;
        byte y = 1;
        byte z = -1;
        // column validations, work from top down, skip columns that are false
        boolean[][] columns = new boolean[][] {
                { true, true,  true },
                { true, false, true },
                { true, true,  true }
        };

        StackItem(ICoords3Di node) {
            this.node = node;
        }
    }
}