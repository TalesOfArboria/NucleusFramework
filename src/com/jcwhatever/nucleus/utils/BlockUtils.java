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


package com.jcwhatever.nucleus.utils;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.utils.materials.MaterialProperty;
import com.jcwhatever.nucleus.utils.materials.Materials;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.FallingBlock;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * {@link org.bukkit.block.Block} related utilities.
 */
public final class BlockUtils {

    private static Location BLOCK_LOCATION = new Location(null, 0, 0, 0);
    private static BlockFace[] CARDINAL_DIRECTIONS = new BlockFace[] {
            BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST
    };
    private static BlockFace[] DIRECTIONS_3D = new BlockFace[] {
            BlockFace.UP, BlockFace.DOWN,
            BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST
    };

    private BlockUtils() {}

    /**
     * Get an immutable list of block faces representing the 4
     * cardinal directions.
     */
    public static List<BlockFace> getCardinalFaces() {
        return Arrays.asList(CARDINAL_DIRECTIONS);
    }

    /**
     * Get an immutable list of block faces representing the 4
     * cardinal directions as well as up and down.
     */
    public static List<BlockFace> get3DFaces() {
        return Arrays.asList(DIRECTIONS_3D);
    }

    /**
     * Get all adjacent blocks that match all of the specified properties.
     *
     * <p>If no properties are specified, all adjacent blocks are returned.</p>
     *
     * @param block       The block to check.
     * @param properties  The properties to match.
     */
    public static List<Block> getAdjacent(Block block, MaterialProperty... properties) {
        return getAdjacent(block, new ArrayList<Block>(6), properties);
    }

    /**
     * Get all adjacent blocks that match all of the specified properties and add them
     * to the specified output collection.
     *
     * <p>If no properties are specified, all adjacent blocks are returned.</p>
     *
     * @param block       The block to check.
     * @param output      The output collection.
     * @param properties  The properties to match.
     *
     * @return  The output collection.
     */
    public static <T extends Collection<Block>> T getAdjacent(Block block, T output,
                                                              MaterialProperty... properties) {
        PreCon.notNull(block);

        if (output instanceof ArrayList)
            ((ArrayList) output).ensureCapacity(output.size() + 6);

        for (BlockFace face : DIRECTIONS_3D) {
            if (block.getY() == 254 && face == BlockFace.UP)
                continue;

            if (block.getY() == 0 && face == BlockFace.DOWN)
                continue;

            Block adjacent = block.getRelative(face);

            if (properties.length > 0) {
                boolean isValid = true;
                for (MaterialProperty property : properties) {
                    if (!Materials.hasProperty(adjacent.getType(), property)) {
                        isValid = false;
                        break;
                    }
                }

                if (!isValid)
                    continue;
            }

            output.add(adjacent);
        }
        return output;
    }

    /**
     * Get all adjacent blocks that match one of the specified materials.
     *
     * <p>If no properties are specified, all adjacent blocks are returned.</p>
     *
     * @param block      The block to check.
     * @param materials  The materials to match.
     */
    public static List<Block> getAdjacent(Block block, Material... materials) {
        return getAdjacent(block, new ArrayList<Block>(6), materials);
    }

    /**
     * Get all adjacent blocks that match one of the specified materials and add them
     * to the specified output collection.
     *
     * <p>If no properties are specified, all adjacent blocks are returned.</p>
     *
     * @param block      The block to check.
     * @param output     The output collection.
     * @param materials  The materials to match.
     *
     * @return  The output collection.
     */
    public static <T extends Collection<Block>> T getAdjacent(Block block, T output,
                                                              Material... materials) {
        PreCon.notNull(block);

        if (output instanceof ArrayList)
            ((ArrayList) output).ensureCapacity(output.size() + 6);

        for (BlockFace face : DIRECTIONS_3D) {
            if (block.getY() == 254 && face == BlockFace.UP)
                continue;

            if (block.getY() == 0 && face == BlockFace.DOWN)
                continue;

            Block adjacent = block.getRelative(face);

            if (materials.length > 0) {
                boolean isValid = false;
                for (Material material : materials) {
                    if (adjacent.getType() == material) {
                        isValid = true;
                        break;
                    }
                }

                if (!isValid)
                    continue;
            }

            output.add(adjacent);
        }
        return output;
    }

    /**
     * Get the first adjacent block that matches the specified properties.
     *
     * <p>Checks block in the order that block faces are returned from {@link #get3DFaces()}.</p>
     *
     * <p>If no properties are specified, all adjacent blocks are considered.</p>
     *
     * @param block       The block to check.
     * @param properties  The properties to match.
     *
     * @return  The matching adjacent block or null if none found.
     */
    @Nullable
    public static Block getFirstAdjacent(Block block, MaterialProperty... properties) {
        PreCon.notNull(block);

        for (BlockFace face : DIRECTIONS_3D) {
            if (block.getY() == 254 && face == BlockFace.UP)
                continue;

            if (block.getY() == 0 && face == BlockFace.DOWN)
                continue;

            Block adjacent = block.getRelative(face);

            if (properties.length > 0) {

                boolean isValid = true;
                for (MaterialProperty property : properties) {
                    if (!Materials.hasProperty(adjacent.getType(), property)) {
                        isValid = false;
                        break;
                    }
                }

                if (!isValid)
                    continue;
            }

            return adjacent;
        }

        return null;
    }

    /**
     * Get the first adjacent block that matches one of the specified materials.
     *
     * <p>Checks block in the order that block faces are returned from {@link #get3DFaces()}.</p>
     *
     * <p>If no properties are specified, all adjacent blocks are considered.</p>
     *
     * @param block      The block to check.
     * @param materials  The properties to match.
     *
     * @return  The matching adjacent block or null if none found.
     */
    @Nullable
    public static Block getFirstAdjacent(Block block, Material... materials) {
        PreCon.notNull(block);

        for (BlockFace face : DIRECTIONS_3D) {
            if (block.getY() == 254 && face == BlockFace.UP)
                continue;

            if (block.getY() == 0 && face == BlockFace.DOWN)
                continue;

            Block adjacent = block.getRelative(face);

            if (materials.length > 0) {

                boolean isValid = false;
                for (Material material : materials) {
                    if (adjacent.getType() == material) {
                        isValid = true;
                        break;
                    }
                }

                if (!isValid)
                    continue;
            }

            return adjacent;
        }

        return null;
    }

    /**
     * Converts the specified block to a falling block.
     *
     * @param block  The block to drop.
     */
    public static FallingBlock dropBlock(Block block) {
        PreCon.notNull(block);

        Location location = Bukkit.isPrimaryThread()
                ? block.getLocation(BLOCK_LOCATION)
                : block.getLocation();

        return dropBlock(location);
    }

    /**
     * Converts the block at the specified location to a falling block.
     *
     * @param location  The location of the block to drop.
     */
    public static FallingBlock dropBlock(Location location) {
        PreCon.notNull(location);

        Block block = location.getBlock();

        Material material = block.getType();
        byte data = block.getData();

        block.setType(Material.AIR);

        // spawn a falling block
        return block.getWorld().spawnFallingBlock(
                location, material, data);
    }

    /**
     * Converts the specified block to a falling block and removes the fallen
     * block after the specified delay.
     *
     * <p>If the falling block has not reached the ground and turned into
     * a block after the delay has elapsed, the falling block is removed.</p>
     *
     * @param block             The block to drop.
     * @param removeDelayTicks  The delay in ticks before removing the fallen block.
     */
    public static void dropRemoveBlock(Block block, int removeDelayTicks) {
        dropRemoveBlock(block.getLocation(), removeDelayTicks);
    }

    /**
     * Converts the block at the specified location to a falling block and
     * removes the fallen block after the specified delay.
     *
     * <p>If the falling block has not reached the ground and turned into
     * a block after the delay has elapsed, the falling block is removed.</p>
     *
     * @param location          The location of the block to drop.
     * @param removeDelayTicks  The delay in ticks before removing the fallen block.
     */
    public static void dropRemoveBlock(final Location location, final int removeDelayTicks) {
        PreCon.notNull(location);

        final BlockState startBlock = location.getBlock().getState();

        location.getBlock().setType(Material.AIR);

        // schedule task so the block has a chance to turn into air
        Scheduler.runTaskLater(Nucleus.getPlugin(), new Runnable() {

            @Override
            public void run() {

                // spawn a falling block
                final FallingBlock fallBlock = location.getWorld().spawnFallingBlock(
                        location, startBlock.getType(), startBlock.getData().getData());

                // schedule the removal of the block
                Scheduler.runTaskLater(Nucleus.getPlugin(), removeDelayTicks, new Runnable() {

                    @Override
                    public void run() {
                        if (fallBlock.isOnGround() || fallBlock.isDead()) {

                            // find the fallen block
                            Location landedLoc = LocationUtils.findSurfaceBelow(location);
                            if (landedLoc == null)
                                return;

                            if (fallBlock.getFallDistance() == 0.0) {

                                Block landedBlock = landedLoc.getBlock();
                                if (landedBlock.getType() != startBlock.getType())
                                    return;

                                landedBlock.setType(Material.AIR);
                            }
                        } else {
                            // remove the falling block if it has not
                            // become a block yet.
                            fallBlock.remove();
                        }

                    }

                });
            }
        });
    }
}
