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
