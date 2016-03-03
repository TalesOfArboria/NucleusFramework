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

import com.jcwhatever.nucleus.managed.astar.interior.IInteriorFinder;
import com.jcwhatever.nucleus.managed.astar.interior.IInteriorFinderResult;
import com.jcwhatever.nucleus.providers.regionselect.IRegionSelection;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.Coords3Di;
import com.jcwhatever.nucleus.utils.coords.ICoords3Di;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.utils.coords.MutableCoords3Di;
import com.jcwhatever.nucleus.utils.materials.Materials;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;

/**
 * Gets air block locations inside an enclosed space.
 */
class InteriorFinder implements IInteriorFinder {

    private static final InteriorFinder INSTANCE = new InteriorFinder();

    static InteriorFinder get() {
        return INSTANCE;
    }

    @Override
    public IInteriorFinderResult search(Location start, IRegionSelection boundaries) {
        PreCon.notNull(start);
        PreCon.notNull(boundaries);

        start = LocationUtils.getBlockLocation(start);

        FinderContext context = new FinderContext(start, boundaries);

        Coords3Di startCoords = Coords3Di.fromLocation(start);

        // Add start node to valid nodes
        context.validNodes.add(startCoords);

        // Add valid adjacent nodes to valid nodes list
        searchAdjacent(context, startCoords);

        return new InteriorResults(context);
    }

    /*
     * Search adjacent locations around the specified location
     * and add valid and invalid locations to their respective map.
     */
    private void searchAdjacent(FinderContext context, Coords3Di node) {

        // column validations, work from top down, skip columns that are false
        boolean[][] columns = new boolean[][] {
                { true, true,  true },
                { true, true, true },
                { true, true,  true }
        };

        boolean isBelowStart = node.getY() <= context.start.getBlockY();

        byte yStart =  (byte)(isBelowStart ? 1 : -1);

        for (byte y = yStart; isBelowStart ? y >= -1 : y <= 1; y += (isBelowStart ? -1 : 1)) {
            for (byte x = -1; x <= 1; x++) {
                for (byte z = -1; z <= 1; z++) {

                    if (x == 0 && z == 0 && y == 0)
                        continue;

                    // get instance of candidate node
                    Coords3Di candidate = new Coords3Di(node, x, y, z);

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
                    if (!context.boundaries.contains(candidate.getX(), candidate.getY(), candidate.getZ())) {
                        continue;
                    }

                    // make sure candidate is air
                    if (candidate.getBlock(context.world).getType() != Material.AIR) {
                        context.invalidNodes.add(candidate);
                        columns[x + 1][z + 1] = false;
                        continue;
                    }

                    // Check for diagonal obstruction
                    if (x != 0 && z != 0) {
                        Coords3Di diagX = tempNode(node, x, y, 0, context.diagX),
                                  diagZ = tempNode(node, 0, y, z, context.diagZ);

                        if(!Materials.isTransparent(diagX.getBlock(context.world).getType()) &&
                                !Materials.isTransparent(diagZ.getBlock(context.world).getType())) {

                            context.invalidNodes.add(candidate);
                            columns[x + 1][z + 1] = false;
                            continue;
                        }
                    }

                    // check for adjacent obstruction
                    if (y != 0) {
                        Coords3Di middle = tempNode(node, 0, y, 0, context.middle),
                                  below = tempNode(node, x, 0, z, context.below);

                        if (!Materials.isTransparent(middle.getBlock(context.world).getType()) &&
                                !Materials.isTransparent(below.getBlock(context.world).getType())) {
                            continue;
                        }
                    }

                    // check for corner obstruction
                    if (x != 0 && y != 0 && z != 0) {
                        Coords3Di adjac1 = tempNode(node, x, 0, 0, context.adjac1),
                                  adjac2 = tempNode(node, 0, 0, z, context.adjac2),
                                  middle = tempNode(node, 0, y, 0, context.middle);

                        if (!Materials.isTransparent(adjac1.getBlock(context.world).getType()) &&
                                !Materials.isTransparent(adjac2.getBlock(context.world).getType()) &&
                                !Materials.isTransparent(middle.getBlock(context.world).getType())) {
                            continue;
                        }
                    }

                    context.validNodes.add(candidate);

                    searchAdjacent(context, candidate);
                }
            }
        }
    }

    private Coords3Di tempNode(ICoords3Di parent, int deltaX, int deltaY, int deltaZ, MutableCoords3Di output) {
        output.setX(parent.getX() + deltaX);
        output.setY(parent.getY() + deltaY);
        output.setZ(parent.getZ() + deltaZ);
        return output;
    }

    /**
     * Stores a set of locations that represent the
     * interior volume of a searched location.
     */
    public static class InteriorResults implements IInteriorFinderResult {

        private final World world;
        private final Set<ICoords3Di> air;

        /**
         * Constructor.
         */
        InteriorResults (FinderContext context) {
            air = context.validNodes;
            this.world = context.world;
        }

        @Override
        public World getWorld() {
            return this.world;
        }

        @Override
        public Set<ICoords3Di> getInterior() {
            return air;
        }

        @Override
        public int getVolume() {
            return air.size();
        }
    }

    private static class FinderContext {

        final World world;
        final Location start;
        final IRegionSelection boundaries;
        final Set<ICoords3Di> invalidNodes;
        final Set<ICoords3Di> validNodes;

        final MutableCoords3Di diagX = new MutableCoords3Di();
        final MutableCoords3Di diagZ = new MutableCoords3Di();
        final MutableCoords3Di middle = new MutableCoords3Di();
        final MutableCoords3Di below = new MutableCoords3Di();
        final MutableCoords3Di adjac1 = new MutableCoords3Di();
        final MutableCoords3Di adjac2 = new MutableCoords3Di();

        FinderContext(Location start, IRegionSelection boundaries) {
            this.start = start;
            this.world = start.getWorld();
            this.boundaries = boundaries;
            this.validNodes = new HashSet<>((int)boundaries.getVolume());
            this.invalidNodes = new HashSet<>((int)boundaries.getVolume());
        }
    }
}
