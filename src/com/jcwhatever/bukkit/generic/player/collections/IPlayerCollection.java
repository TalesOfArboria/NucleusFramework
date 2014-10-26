package com.jcwhatever.bukkit.generic.player.collections;

import com.jcwhatever.bukkit.generic.mixins.IDisposable;
import org.bukkit.entity.Player;

public interface IPlayerCollection extends IDisposable {

	void removePlayer(Player p);
	
}
