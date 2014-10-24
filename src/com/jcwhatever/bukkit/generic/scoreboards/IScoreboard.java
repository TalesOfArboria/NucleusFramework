package com.jcwhatever.bukkit.generic.scoreboards;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;

public interface IScoreboard {

    void init(Plugin plugin, IScoreboardInfo typeInfo);
    
	String getType();
	
	Scoreboard getScoreboard();
	
	
	void apply(Player p);
    
	void cease(Player p);
	
	
	void dispose();
}
