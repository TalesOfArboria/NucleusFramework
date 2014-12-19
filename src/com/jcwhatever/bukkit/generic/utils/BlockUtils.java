/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.utils;

import com.jcwhatever.bukkit.generic.GenericsLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.FallingBlock;

/**
 * {@code Block} utilities.
 */
public final class BlockUtils {

    private BlockUtils() {}

    /**
     * Converts the specified block to a falling block and
     * removes the fallen block after the specified delay.
     *
     * <p>
     *     If the falling block has not reached the ground and turned into
     *     a block after the delay has elapsed, the falling block is removed.
     * </p>
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
     * <p>
     *     If the falling block has not reached the ground and turned into
     *     a block after the delay has elapsed, the falling block is removed.
     * </p>
     *
     * @param location          The location of the block to drop.
     * @param removeDelayTicks  The delay in ticks before removing the fallen block.
     */
    public static void dropRemoveBlock(final Location location, final int removeDelayTicks) {

        final BlockState startBlock = location.getBlock().getState();

        location.getBlock().setType(Material.AIR);

        // schedule task so the block has a chance to turn into air
        Scheduler.runTaskLater(GenericsLib.getPlugin(), new Runnable() {

            @Override
            public void run() {

                // spawn a falling block
                final FallingBlock fallBlock = location.getWorld().spawnFallingBlock(
                        location, startBlock.getType(), startBlock.getData().getData());

                // schedule the removal of the block
                Scheduler.runTaskLater(GenericsLib.getPlugin(), removeDelayTicks, new Runnable () {

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
                        }
                        else {
                            // remove the falling block if it has not
                            // become a block yet.
                            fallBlock.remove();
                        }

                    }

                });
            }
        });
    }

    /**
     * Get the block adjacent to the specified block.
     *
     * @param block      The block to check.
     * @param direction  The direction to search for the adjacent block.
     */
    public static Block getAdjacentBlock(Block block, BlockFace direction) {
        return block.getRelative(direction).getState().getBlock();
    }
}
