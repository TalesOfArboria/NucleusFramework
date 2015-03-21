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

import com.jcwhatever.nucleus.regions.selection.IRegionSelection;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.materials.Materials;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

/**
 * Gets air block locations inside an enclosed space.
 */
public class InteriorFinder {

    /**
     * Search for air blocks within a region without moving
     * outside of structural boundaries.
     *
     * <p>The structure must be completely enclosed with no block open to the exterior.</p>
     *
     * <p>Does not search through doors even if they are open.</p>
     *
     * @param start       The location to start the search from.
     * @param boundaries  The region boundaries to prevent searching endlessly into the world.
     */
    public InteriorResults searchInterior(Location start, IRegionSelection boundaries) {
        PreCon.notNull(start);
        PreCon.notNull(boundaries);

        start = LocationUtils.getBlockLocation(start);

        FinderContext context = new FinderContext(start, boundaries);

        // Add start node to valid nodes
        context.validNodes.add(start);

        // Add valid adjacent nodes to valid nodes list
        searchAdjacent(context, start);

        return new InteriorResults(context);
    }

    /*
     * Search adjacent locations around the specified location
     * and add valid and invalid locations to their respective map.
     */
    protected void searchAdjacent(FinderContext context, Location node) {

        // column validations, work from top down, skip columns that are false
        boolean[][] columns = new boolean[][] {
                { true, true,  true },
                { true, true, true },
                { true, true,  true }
        };

        boolean isBelowStart = node.getBlockY() <= context.start.getBlockY();

        byte yStart =  (byte)(isBelowStart ? 1 : -1);

        for (byte y = yStart; isBelowStart ? y >= -1 : y <= 1; y += (isBelowStart ? -1 : 1)) {
            for (byte x = -1; x <= 1; x++) {
                for (byte z = -1; z <= 1; z++) {

                    if (x == 0 && z == 0 && y == 0)
                        continue;

                    // get instance of candidate node
                    Location candidate = node.clone().add(x, y, z);

                    // check if candidate is already considered
                    if (context.invalidNodes.contains(candidate)) {
                        columns[x + 1][z + 1] = false;
                        continue;
                    }

                    if (context.validNodes.contains(candidate)) {
                        continue;
                    }

                    if (!columns[x + 1][z + 1]) {
                        continue;
                    }

                    // make sure candidate is within boundaries
                    if (!context.boundaries.contains(candidate)) {
                        continue;
                    }

                    // make sure candidate is air
                    if (candidate.getBlock().getType() != Material.AIR) {
                        context.invalidNodes.add(candidate);
                        columns[x + 1][z + 1] = false;
                        continue;
                    }

                    // Check for diagonal obstruction
                    if (x != 0 && z != 0) {
                        Location diagX = node.clone().add(x, y, 0),
                                diagZ = node.clone().add(0, y, z);

                        if(!Materials.isTransparent(diagX.getBlock().getType()) &&
                                !Materials.isTransparent(diagZ.getBlock().getType())) {

                            context.invalidNodes.add(candidate);
                            columns[x + 1][z + 1] = false;
                            continue;
                        }
                    }

                    // check for adjacent obstruction
                    if (y != 0) {
                        Location middle = node.clone().add(0, y, 0),
                                below = node.clone().add(x, 0, z);

                        if (!Materials.isTransparent(middle.getBlock().getType()) &&
                                !Materials.isTransparent(below.getBlock().getType())) {
                            continue;
                        }
                    }

                    // check for corner obstruction
                    if (x != 0 && y != 0 && z != 0) {
                        Location adjac1 = node.clone().add(x, 0, 0),
                                adjac2 = node.clone().add(0, 0, z),
                                middle = node.clone().add(0, y, 0);

                        if (!Materials.isTransparent(adjac1.getBlock().getType()) &&
                                !Materials.isTransparent(adjac2.getBlock().getType()) &&
                                !Materials.isTransparent(middle.getBlock().getType())) {
                            continue;
                        }
                    }

                    context.validNodes.add(candidate);

                    searchAdjacent(context, candidate);

                }
            }
        }
    }

    /**
     * Stores a set of locations that represent the
     * interior volume of a searched location.
     */
    public static class InteriorResults {

        private final Set<Location> _air;

        /**
         * Constructor.
         */
        InteriorResults (FinderContext context) {
            _air = context.validNodes;
        }

        /**
         * Get the location results.
         */
        public Set<Location> getInterior() {
            return _air;
        }

        /**
         * Get the volume of the result.
         */
        public int getVolume() {
            return _air.size();
        }
    }

    private static class FinderContext {

        final Location start;
        final IRegionSelection boundaries;
        final Set<Location> invalidNodes;
        final Set<Location> validNodes;

        FinderContext(Location start, IRegionSelection boundaries) {
            this.start = start;
            this.boundaries = boundaries;
            this.validNodes = new HashSet<Location>((int)boundaries.getVolume());
            this.invalidNodes = new HashSet<Location>((int)boundaries.getVolume());
        }
    }
}
