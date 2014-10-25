package com.jcwhatever.bukkit.generic.items;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.enchantments.Enchantment;

/**
 * A wrapper to hold an Enchantment as well as its level.
 */
public class EnchantmentWrapper {
	
	private final Enchantment _enchantment;
	private final int _level;

    /**
     * Constructor.
     *
     * @param enchant  The enchantment.
     * @param level    The enchantment level.
     */
	public EnchantmentWrapper (Enchantment enchant, int level) {
        PreCon.notNull(enchant);

		_enchantment = enchant;
		_level = level;
	}

    /**
     * Get the enchantment.
     */
	public Enchantment getEnchantment() {
		return _enchantment;
	}

    /**
     * Get the enchantment level.
     */
	public int getLevel() {
		return _level;
	}

}
