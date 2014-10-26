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
import com.jcwhatever.bukkit.generic.regions.Region;
import com.jcwhatever.bukkit.generic.utils.LocationUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

/**
 * Gets air block locations inside an enclosed space
 */
public class InteriorFinder {

    private Set<Location> _invalidNodes;
    private Set<Location> _validNodes;

    private Location _start;
    private Region _boundaries;

    /**
     * Search for air blocks within a region without moving
     * outside of structural boundaries.
     *
     * @param start       The location to start the search from.
     * @param boundaries  The region boundaries to prevent searching forever.
     */
    public InteriorResults searchInterior(Location start, Region boundaries) {
        PreCon.notNull(start);
        PreCon.notNull(boundaries);

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

    /*
     *  Initialize hash sets using the volume of the boundaries region.
     */
    private void init(Region boundaries) {
        _validNodes = new HashSet<Location>((int)boundaries.getVolume());
        _invalidNodes = new HashSet<Location>((int)boundaries.getVolume());
    }

    /*
     * Search adjacent locations around the specified location
     * and add valid and invalid locations to their respective map.
     */
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
                    if (_invalidNodes.contains(candidate)) {
                        columns[x + 1][z + 1] = false;
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
                        columns[x + 1][z + 1] = false;
                        continue;
                    }

                    // Check for diagonal obstruction
                    if (x != 0 && z != 0) {
                        Location diagX = node.clone().add(x, y, 0),
                                diagZ = node.clone().add(0, y, z);

                        if(!MaterialExt.isTransparent(diagX.getBlock().getType()) &&
                                !MaterialExt.isTransparent(diagZ.getBlock().getType())) {
                            _invalidNodes.add(candidate);
                            columns[x + 1][z + 1] = false;
                            continue;
                        }
                    }

                    // check for adjacent obstruction
                    if (y != 0) {
                        Location middle = node.clone().add(0, y, 0),
                                below = node.clone().add(x, 0, z);

                        if (!MaterialExt.isTransparent(middle.getBlock().getType()) &&
                                !MaterialExt.isTransparent(below.getBlock().getType())) {
                            continue;
                        }
                    }

                    // check for corner obstruction
                    if (x != 0 && y != 0 && z != 0) {
                        Location adjac1 = node.clone().add(x, 0, 0),
                                 adjac2 = node.clone().add(0, 0, z),
                                 middle = node.clone().add(0, y, 0);

                        if (!MaterialExt.isTransparent(adjac1.getBlock().getType()) &&
                                !MaterialExt.isTransparent(adjac2.getBlock().getType()) &&
                                !MaterialExt.isTransparent(middle.getBlock().getType())) {
                            continue;
                        }
                    }

                    _validNodes.add(candidate);

                    searchAdjacent(candidate);

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
         *
         * @param air  Air locations found.
         */
        InteriorResults (Set<Location> air) {
            _air = air;
        }

        /**
         * Get the location results.
         */
        public Set<Location> getNodes() {
            return _air;
        }

        /**
         * Get the volume of the result.
         */
        public int getVolume() {
            return _air.size();
        }

    }
}
