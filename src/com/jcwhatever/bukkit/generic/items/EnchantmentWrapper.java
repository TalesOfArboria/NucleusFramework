package com.jcwhatever.bukkit.generic.items;

import org.bukkit.enchantments.Enchantment;

/**
 * A class to hold an Enchantment as well as its level.
 * 
 * @author JC The Pants
 *
 */
public class EnchantmentWrapper {
	
	private Enchantment _enchantment;
	private int _level;
	
	public EnchantmentWrapper (Enchantment enchant, int level) {
		_enchantment = enchant;
		_level = level;
	}

	public Enchantment getEnchantment() {
		return _enchantment;
	}
	
	public int getLevel() {
		return _level;
	}

}
