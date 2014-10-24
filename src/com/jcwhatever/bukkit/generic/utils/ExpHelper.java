package com.jcwhatever.bukkit.generic.utils;

import org.bukkit.entity.Player;

public class ExpHelper {

	
	public static int getTotalExp(Player p) {
		//int level = p.getLevel();
		return p.getTotalExperience();// + getExpFromLevel(level);
	}
	
	public static int getExpFromLevel(int level) {
		if (level >= 30) {
	        return 62 + (level - 30) * 7;
	    } else if (level >= 15) {
	        return 17 + (level - 15) * 3;
	    } else {
	        return 17;
	    }
	}
	
	public static void setExp(Player p, int exp) {
		p.setTotalExperience(0);
		p.setLevel(0);
		p.setExp(0);
		p.giveExp(exp);
	}
	
	public static void incrementExp(Player p, int amount) {
		p.giveExp(amount);
	}
	
}
