package com.jcwhatever.bukkit.generic.percentbar;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.jcwhatever.bukkit.generic.player.collections.PlayerMap;

public class BarManager {

	static Map<UUID, Bar> _bars = new PlayerMap<Bar>();
	private static boolean _hasBarAPI = false;
	
	
	static {
		_hasBarAPI = Bukkit.getPluginManager().getPlugin("BarAPI") != null;
	}
	
	public static boolean hasBarAPI() {
		return _hasBarAPI;
	}
	
	public static Bar getNew(String message, float percent) {
		return new Bar(message, percent);
	}
	
	
	public static Bar setBar(Player p, String message, float percent) {
		
		Bar current = _bars.remove(p.getUniqueId());
		
		if (current != null)
			current.remove(p);
		
		Bar bar = new Bar(p, message, percent);
		
		_bars.put(p.getUniqueId(), bar);
		
		return bar;
	}
	
	public static Bar setBar(Collection<Player> players, String message, float percent) {
		return null;
	}
	
	public static Bar getBar(Player p) {
		return _bars.get(p);
	}
}
