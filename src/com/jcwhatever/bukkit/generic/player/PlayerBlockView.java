package com.jcwhatever.bukkit.generic.player;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.performance.SingleCache;
import com.jcwhatever.bukkit.generic.player.collections.PlayerMap;
import com.jcwhatever.bukkit.generic.regions.Region;
import com.jcwhatever.bukkit.generic.regions.RegionBlockIterator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Ensures a player sees a region as it describes itself
 */
public class PlayerBlockView {
	
	private static Map<UUID, PlayerView> _playerViews = new PlayerMap<PlayerView>();
	private static SingleCache<UUID, PlayerView> _viewCache = new SingleCache<UUID, PlayerView>();
	
	
	public static void registerPlayerView(Player p, Region region) {
		PlayerView views = _playerViews.get(p.getUniqueId());
		if (views == null) {
			views = new PlayerView();
			_playerViews.put(p.getUniqueId(), views);
		}
		
		if (!views.hasRegion(region))
			setRegionView(p, region);
		
		views.addRegion(region);
	}
	
	public static boolean unregisterPlayerView(Player p, Region region) {
		PlayerView views = _playerViews.get(p.getUniqueId());
		if (views == null) {
			return false;
		}
		
		if (views.removeRegion(region)) {
			resetRegionView(p, region);
			return true;
		}
		return false;
	}
	
	public static List<Region> getPlayerViews(Player p) {
		List<Region> regions = new ArrayList<Region>(10);
		PlayerView views = _playerViews.get(p.getUniqueId());
		if (views == null) {
			return regions;
		}
		
		regions.addAll(views.getRegions());
		
		return regions;
	}
	
	public static void setPlayerView(final Player p, final Location loc) {
		UUID id = p.getUniqueId();
		PlayerView view;
		if (_viewCache.keyEquals(id)) {
			view = _viewCache.getValue();
		} else {
			view = _playerViews.get(id);
			_viewCache.set(id, view);
		}
		
		if (view == null)
			return;
		
		
		Set<Region> regions = view.getRegions();
		
		for (final Region region : regions) {
			if (region.contains(loc)) {
				Bukkit.getScheduler().runTaskLater(GenericsLib.getPlugin(), new Runnable() {

					@Override
					public void run() {

                        ItemStack stack = loc.getBlock().getState().getData().toItemStack();
						setClientBlock(p, loc, stack);
					}
					
				}, 1);
				
				return;
			}
		}
	}
	
	private static class PlayerView {
		
		private Set<Region> regions = new HashSet<Region>(10);
		
		
		public boolean addRegion(Region region) {
            return !this.regions.contains(region) && this.regions.add(region);

        }
		
		public boolean removeRegion(Region region) {
			return this.regions.remove(region);
		}
		
		public Set<Region> getRegions() {
			return regions;
		}
		
		public boolean hasRegion(Region region) {
			return this.regions.contains(region);
		}
	}
	
	
	private static void setRegionView(Player p, Region region) {
		RegionBlockIterator iterator = new RegionBlockIterator(region);
		
		while (iterator.hasNext()) {
			Location loc = iterator.next().getLocation();

            ItemStack stack = loc.getBlock().getState().getData().toItemStack();
			setClientBlock(p, loc, stack);
		}
	}
	
	private static void resetRegionView(Player p, Region region) {

		RegionBlockIterator iterator = new RegionBlockIterator(region);
		
		while (iterator.hasNext()) {
			
			Location loc = iterator.next().getLocation();
			resetClientBlock(p, loc);
		}
	}

	public static boolean isAlternateViewed(Player p, World world, int x,	int y, int z) {
		
		UUID id = p.getUniqueId();
		PlayerView view;
		
		if (_viewCache.keyEquals(id)) {
			view = _viewCache.getValue();
		} else {
			view = _playerViews.get(id);
			_viewCache.set(id, view);
		}
		
		if (view == null)
			return false;
		
		Set<Region> regions = view.getRegions();
		
		Location loc = new Location (world, x, y, z);
		
		for (final Region region : regions) {
			if (region.contains(loc)) {
				return true;
			}
		}
		
		return false;
	}

    private static void setClientBlock(Player p, Location location, ItemStack stackMaterial) {
        p.sendBlockChange(location, stackMaterial.getType(), stackMaterial.getData().getData());
    }

    private static void resetClientBlock(Player p, Location location) {
        Block block = location.getBlock();
        p.sendBlockChange(location, block.getType(), block.getData());
    }
}
