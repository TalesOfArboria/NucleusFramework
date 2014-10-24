package com.jcwhatever.bukkit.generic.events.bukkit;

import com.jcwhatever.bukkit.generic.GenericsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class EventManager {
	
	private GenericsPlugin _plugin;

	public EventManager(GenericsPlugin plugin) {
		_plugin = plugin;
	}

	public void addListener(Listener listener) {
		PluginManager pm = Bukkit.getServer().getPluginManager();
		
		pm.registerEvents(listener, _plugin);
	}
	
	public void removeListener(Listener listener) {
		HandlerList.unregisterAll(listener);
	}

}
