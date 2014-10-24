package com.jcwhatever.bukkit.generic.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.RegionSelector;

public class WorldEditUtils {
	
	private static Boolean _isWorldEditInstalled = null;
	private static Plugin _wePlugin;
	
	public static boolean isWorldEditInstalled() {
		if (_isWorldEditInstalled == null) {

			// Check that World Edit is installed
			_isWorldEditInstalled = ((_wePlugin = Bukkit.getPluginManager().getPlugin("WorldEdit")) != null && _wePlugin instanceof WorldEditPlugin);
		}
		
		return _isWorldEditInstalled;
	}
	
	public static WorldEditPlugin getWorldEdit() {
		if (!isWorldEditInstalled())
			return null;
		
		return (WorldEditPlugin)_wePlugin;
	}

	
	public static Selection getWorldEditSelection(Player p) {
		// Check that World Edit is installed
		if (!isWorldEditInstalled()) {
			return null;
		}

		WorldEditPlugin plugin = (WorldEditPlugin)_wePlugin;
		Selection sel;

		// Check for World Edit selection
		if ((sel = plugin.getSelection(p)) == null) {
			return null;
		}

		// Make sure both points are selected
		if (sel.getMaximumPoint() == null || sel.getMinimumPoint() == null) {
			return null;
		}

		if (!sel.getMaximumPoint().getWorld().equals(sel.getMinimumPoint().getWorld())) {
			return null;
		}

		return sel;
	}
	
	
	public static boolean setWorldEditSelection(Player p, Location p1, Location p2) {
		PreCon.notNull(p);
		PreCon.notNull(p1);
		PreCon.notNull(p2);
		
		// Check that World Edit is installed
		if (!isWorldEditInstalled())
			return false;
		
		if (!p1.getWorld().equals(p2.getWorld())) {
			return false;
		}
		
		WorldEditPlugin plugin = (WorldEditPlugin)_wePlugin;
		RegionSelector selector = plugin.getSession(p).getRegionSelector(plugin.wrapPlayer(p).getWorld());
		
		Vector p1Vector = new Vector(p1.toVector().getX(), p1.toVector().getY(), p1.toVector().getZ());
		Vector p2Vector = new Vector(p2.toVector().getX(), p2.toVector().getY(), p2.toVector().getZ());
		
		selector.selectPrimary(p1Vector);
		selector.selectSecondary(p2Vector);
		
		return true;
	}

}
