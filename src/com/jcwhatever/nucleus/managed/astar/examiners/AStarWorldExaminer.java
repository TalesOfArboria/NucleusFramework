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

package com.jcwhatever.nucleus.managed.astar.examiners;

import com.jcwhatever.nucleus.managed.astar.nodes.IAStarNode;
import com.jcwhatever.nucleus.managed.astar.IAStarNodeContainer;
import com.jcwhatever.nucleus.managed.astar.score.AStarScore;
import com.jcwhatever.nucleus.managed.astar.score.IAStarScore;
import com.jcwhatever.nucleus.managed.astar.score.IAStarScoreProvider;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.materials.Materials;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Openable;

import javax.annotation.Nullable;

/**
 * Node examiner for world coordinates.
 */
public class AStarWorldExaminer<N extends IAStarNode<N>> implements IAStarNodeExaminer<N> {

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

    private final World _world;
    private final IAStarScoreProvider<N> _scoreProvider;

    private double _entityHeight = 2;
    private DoorPathMode _doorPathMode = DoorPathMode.OPEN;

    /**
     * Constructor.
     *
     * @param world  The world to examine.
     */
    public AStarWorldExaminer(World world) {
        PreCon.notNull(world);

        _world = world;
        _scoreProvider = AStarScore.getCoordsProvider();
    }

    /**
     * Constructor.
     *
     * @param world          The world to examine.
     * @param scoreProvider  The score instance provider.
     */
    public AStarWorldExaminer(World world, IAStarScoreProvider<N> scoreProvider) {
        PreCon.notNull(world);

        _world = world;
        _scoreProvider = scoreProvider;
    }

    /**
     * Get the world being examined.
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
    public boolean isDestination(N node) {
        PreCon.notNull(node);

        return node.equals(node.getContext().getDestination());
    }

    @Override
    public PathableResult isPathable(N from, N to) {
        PreCon.notNull(from);
        PreCon.notNull(to);
        PreCon.isValid(from.isAdjacent(to), "'to' node should be adjacent to 'from' node");

        IAStarNodeContainer<N> container = from.getContext().getNodeContainer();

        // check if candidate is already closed
        if (container.isClosed(to))
            return PathableResult.INVALID_POINT;


        int x = to.getX() - from.getX();
        int y = to.getY() - from.getY();
        int z = to.getZ() - from.getZ();

        Material material = _world
                .getBlockAt(to.getX(), to.getY(), to.getZ())
                .getType();

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
            IAStarNode diagX = from.getRelative(x, y, 0);
            IAStarNode diagZ = from.getRelative(0, y, z);

            boolean isXValid = hasRoomForEntity(diagX, getDoorPathMode());
            boolean isZValid = hasRoomForEntity(diagZ, getDoorPathMode());

            // prevent walking through diagonal walls
            if(y == 0 && !isXValid && !isZValid ) {
                return PathableResult.INVALID_COLUMN;
            }

            // prevent walking through corners
            if (!isXValid || !isZValid) {
                return PathableResult.INVALID_COLUMN;
            }
        }

        return PathableResult.VALID;
    }

    @Override
    public IAStarScore<N> getScore(@Nullable N parent, N node) {
        return _scoreProvider.getScore(parent, node);
    }

    /**
     * Determine if there is enough room for an entity above a specified node.
     */
    protected boolean hasRoomForEntity(IAStarNode node, DoorPathMode doorMode) {

        Block block = _world
                .getBlockAt(node.getX(), node.getY(), node.getZ());

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
