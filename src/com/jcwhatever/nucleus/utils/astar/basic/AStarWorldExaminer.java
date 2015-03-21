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

package com.jcwhatever.nucleus.utils.astar.basic;

import com.jcwhatever.nucleus.utils.coords.Coords3Di;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.astar.AStarContext;
import com.jcwhatever.nucleus.utils.astar.AStarNode;
import com.jcwhatever.nucleus.utils.astar.IAStarExaminer;
import com.jcwhatever.nucleus.utils.astar.IAStarNodeContainer;
import com.jcwhatever.nucleus.utils.materials.Materials;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Openable;

import javax.annotation.Nullable;

/**
 * Basic implementation of {@link IAStarExaminer}.
 */
public class AStarWorldExaminer implements IAStarExaminer {

    private final World _world;
    private double _entityHeight = 2;
    private DoorPathMode _doorPathMode = DoorPathMode.OPEN;

    /**
     * Specifies how doorways are handled.
     */
    public enum DoorPathMode  {
        /**
         * Path through open doorways
         */
        OPEN,

        /**
         * Always path through doorways even if they
         * are closed.
         */
        IGNORE_CLOSED,

        /**
         * Never path through doorways even if they
         * are open.
         */
        IGNORE_OPEN,
    }

    /**
     * Constructor.
     *
     * @param world  The {@link org.bukkit.World} the examiner will examine.
     */
    public AStarWorldExaminer(World world) {
        _world = world;
    }

    /**
     * Get the examiners {@link org.bukkit.World}.
     */
    public World getWorld() {
        return _world;
    }

    /**
     * Get the height of the pathing entity.
     */
    public double getEntityHeight() {
        return _entityHeight;
    }

    /**
     * Set the height of the pathing entity.
     *
     * @param height  The entity height.
     */
    public void setEntityHeight(double height) {
        _entityHeight = height;
    }

    /**
     * Get the door pathing mode.
     */
    public DoorPathMode getDoorPathMode() {
        return _doorPathMode;
    }

    /**
     * Set the door pathing mode.
     *
     * @param mode  The door path mode.
     */
    public void setDoorPathMode(DoorPathMode mode) {
        PreCon.notNull(mode);

        _doorPathMode = mode;
    }

    @Override
    public boolean isDestination(AStarNode node) {
        PreCon.notNull(node);

        return node.equals(node.getContext().getDestination());
    }

    @Override
    public boolean canSearch(AStarContext context) {
        PreCon.notNull(context);

        IAStarNodeContainer container = context.getContainer();
        AStarNode destination = context.getDestination();

        return container.openSize() != 0 && !container.isClosed(destination);
    }

    @Override
    public AStarScore getScore(@Nullable AStarNode parent, AStarNode node) {
        PreCon.notNull(node);

        return new AStarScore(parent, node);
    }

    @Override
    public PathableResult isPathable(AStarNode from, AStarNode to) {
        PreCon.notNull(from);
        PreCon.notNull(to);
        PreCon.isValid(from.isAdjacent(to), "'to' node should be adjacent to 'from' node");

        IAStarNodeContainer container = from.getContext().getContainer();

        // check if candidate is already closed
        if (container.isClosed(to))
            return PathableResult.INVALID_POINT;

        Coords3Di parent = from.getCoords();
        Coords3Di candidate = to.getCoords();

        Coords3Di delta = candidate.getDelta(parent);
        int x = delta.getX();
        int y = delta.getY();
        int z = delta.getZ();

        Material material = candidate.getBlock(_world).getType();

        // check candidate to see if its valid for the entity to stand on
        if (!Materials.isSurface(material) ||
                !hasRoomForEntity(to, getDoorPathMode())) {

            // invalidate column if material is NOT transparent
            if (!Materials.isTransparent(material)) {
                return PathableResult.INVALID_COLUMN;
            }

            return PathableResult.INVALID_POINT;
        }

        // Check for diagonal obstruction
        if (x != 0 && z != 0 && y >= 0) {
            AStarNode diagX = from.getRelative(x, y, 0);
            AStarNode diagZ = from.getRelative(0, y, z);

            boolean isXValid = hasRoomForEntity(diagX, getDoorPathMode());
            boolean isZValid = hasRoomForEntity(diagZ, getDoorPathMode());

            // prevent walking through diagonal walls
            if(y == 0 && !isXValid && !isZValid ) {
                return PathableResult.INVALID_COLUMN;
            }

            // prevent walking through corners
            if (!isXValid || !isZValid) {
                return PathableResult.INVALID_POINT;
            }
        }

        return PathableResult.VALID;
    }

    /**
     * Determine if there is enough room for an entity above a specified node.
     */
    protected boolean hasRoomForEntity(AStarNode node, DoorPathMode doorMode) {

        Block block = node.getCoords().getBlock(_world);

        int height = (int)Math.ceil(getEntityHeight());

        // check head room
        for (int i=0; i < height; i++) {

            Block above = block.getRelative(0, i + 1, 0);

            if (doorMode != DoorPathMode.IGNORE_OPEN) {

                BlockState state = above.getState();

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
}
