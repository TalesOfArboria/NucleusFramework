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


package com.jcwhatever.nucleus.utils.pathing.astar;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.materials.Materials;
import com.jcwhatever.nucleus.utils.pathing.astar.AStar.DoorPathMode;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Openable;

public class AStarUtils {

    private AStarUtils() {}

    /**
     * Get the location of a node based on the path start location and its offset from the start location.
     *
     * @param start         The start location.
     * @param startOffsetX  The X axis offset from the start location.
     * @param startOffsetY  The Y axis offset from the start location.
     * @param startOffsetZ  The Z axis offset from the start location.
     */
    public static Location getNodeLocation(Location start, int startOffsetX, int startOffsetY, int startOffsetZ) {
        PreCon.notNull(start);

        return new Location(start.getWorld(),
                start.getBlockX() + startOffsetX,
                start.getBlockY() + startOffsetY,
                start.getBlockZ() + startOffsetZ);
    }

    /**
     * Determine if an entity can fit in the space above the
     * specified node.
     *
     * @param nodeLocation       The location of the node to check.
     * @param entityBlockHeight  The block height of the entity that must fit.
     * @param doorMode           The door check mode.
     */
    public static boolean hasRoomForEntity(Location nodeLocation, int entityBlockHeight, DoorPathMode doorMode) {
        PreCon.notNull(nodeLocation);
        PreCon.greaterThanZero(entityBlockHeight);
        PreCon.notNull(doorMode);

        Block block = nodeLocation.getBlock();

        // check head room
        for (int i=0; i < entityBlockHeight; i++) {

            Block above = block.getRelative(0, i + 1, 0);
            BlockState state = above.getState();

            if (doorMode != DoorPathMode.IGNORE_OPEN) {

                // check if block is an open doorway
                if (state.getData() instanceof Openable) {

                    Openable openable = (Openable) state.getData();

                    if (doorMode == DoorPathMode.IGNORE_CLOSED)
                        continue;

                    Block bottomDoor = block.getRelative(0, i, 0);
                    BlockState bottomState = bottomDoor.getState();

                    // check the lower door block instead
                    if (bottomState.getData() instanceof Openable) {
                        openable = (Openable) bottomState.getData();
                    }

                    if (openable.isOpen()) {
                        continue;
                    }
                }
            }

            // make sure block is transparent
            if (!Materials.isTransparent(above.getType())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Calculate a nodes G score.
     *
     * @param node  The node to check.
     */
    public static int getGScore(IPathNode node) {
        PreCon.notNull(node);

        int g = 0;
        IPathNode parent;
        IPathNode current = node;

        while ((parent = current.getParentNode()) != null) {

            int disX = Math.abs(current.getXStartOffset() - parent.getXStartOffset()),
                    disY = Math.abs(current.getYStartOffset() - parent.getYStartOffset()),
                    disZ = Math.abs(current.getZStartOffset() - parent.getZStartOffset());

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

    /**
     * Calculate a nodes H score.
     *
     * @param node  The node to check.
     */
    public static int getHScore(IPathNode node) {

        Location start = node.getStartLocation();
        Location end = node.getEndLocation();

        int startX = start.getBlockX();
        int startY = start.getBlockY();
        int startZ = start.getBlockZ();

        int endX = end.getBlockX();
        int endY = end.getBlockY();
        int endZ = end.getBlockZ();

        int disX = (startX + node.getXStartOffset()) - endX,
                disY = (startY + node.getYStartOffset()) - endY,
                disZ = (startZ + node.getZStartOffset()) - endZ;

        return (disX * disX) + (disY * disY) + (disZ * disZ);
    }

}
