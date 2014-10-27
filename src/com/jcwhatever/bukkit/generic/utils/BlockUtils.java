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


package com.jcwhatever.bukkit.generic.utils;

import com.jcwhatever.bukkit.generic.GenericsLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.FallingBlock;

public class BlockUtils {

    public static void dropRemoveBlock(Block block, int removeDelayTicks) {
        dropRemoveBlock(block.getLocation(), removeDelayTicks);
    }

    public static void dropRemoveBlock(final Location location, final int removeDelayTicks) {

        final BlockState startBlock = location.getBlock().getState();

        location.getBlock().setType(Material.AIR);

        Bukkit.getScheduler().scheduleSyncDelayedTask(GenericsLib.getPlugin(), new Runnable() {

            @Override
            public void run() {

                final FallingBlock fallBlock = location.getWorld().spawnFallingBlock(location, startBlock.getType(), startBlock.getData().getData());

                Bukkit.getScheduler().runTaskLater(GenericsLib.getPlugin(), new Runnable () {

                    @Override
                    public void run() {
                        if (fallBlock.isOnGround() || fallBlock.isDead()) {
                            Location landedLoc = findSolidBlockBelow(location);
                            if (landedLoc == null) {
                                return;
                            }

                            if (fallBlock.getFallDistance() == 0.0) {
                                Block landedBlock = landedLoc.getBlock();
                                if (landedBlock.getType() != startBlock.getType())
                                    return;

                                landedBlock.setType(Material.AIR);
                            }
                        }
                        else {
                            fallBlock.remove();
                        }

                    }

                }, removeDelayTicks);


            }

        }, 1);

    }



    public static Block getAdjacentBlock(Block current, BlockFace direction) {
        return current.getRelative(direction).getState().getBlock();
    }

    public static Location findSolidBlockBelow(Location searchLoc) {
        searchLoc = searchLoc.clone();
        searchLoc.setY(searchLoc.getY() - 1);
        Block current = searchLoc.getBlock();

        while (current.getType() == Material.AIR ||
                current.getType() == Material.WATER ||
                current.getType() == Material.LAVA) {
            searchLoc.setY(searchLoc.getY() - 1);
            current = searchLoc.getBlock();

            if (searchLoc.getY() < 0) {
                return null;
            }
        }

        return searchLoc;
    }

}
