package com.jcwhatever.bukkit.generic.extended;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import com.jcwhatever.bukkit.generic.utils.PreCon;


/**
 * Armor type enumeration. Maps Armor items to one of the following:
 * Helmet (head piece), Chestplate (torso), Leggings (pants), Boots, or
 * Horse Armor.
 *
 * @author JC The Pants
 *
 */
public enum ArmorType {
	HELMET(0,
			ArmorPiece.n(0.04F, Material.LEATHER_HELMET), 
			ArmorPiece.n(0.08F, Material.CHAINMAIL_HELMET),
			ArmorPiece.n(0.08F, Material.IRON_HELMET),
			ArmorPiece.n(0.08F, Material.GOLD_HELMET),
			ArmorPiece.n(0.12F, Material.DIAMOND_HELMET)
	),

	CHESTPLATE(1,
			ArmorPiece.n(0.12F, Material.LEATHER_CHESTPLATE),
			ArmorPiece.n(0.20F, Material.CHAINMAIL_CHESTPLATE),
			ArmorPiece.n(0.20F, Material.GOLD_CHESTPLATE),
			ArmorPiece.n(0.24F, Material.IRON_CHESTPLATE),
			ArmorPiece.n(0.32F, Material.DIAMOND_CHESTPLATE)
			
	),

	LEGGINGS(2,
			ArmorPiece.n(0.08F, Material.LEATHER_LEGGINGS),
			ArmorPiece.n(0.12F, Material.GOLD_LEGGINGS),
			ArmorPiece.n(0.16F, Material.CHAINMAIL_LEGGINGS),
			ArmorPiece.n(0.20F, Material.IRON_LEGGINGS),
			ArmorPiece.n(0.24F, Material.DIAMOND_LEGGINGS)
			
	),
	BOOTS(3,
			ArmorPiece.n(0.04F, Material.LEATHER_BOOTS),
			ArmorPiece.n(0.04F, Material.CHAINMAIL_BOOTS),
			ArmorPiece.n(0.04F, Material.GOLD_BOOTS),
			ArmorPiece.n(0.08F, Material.IRON_BOOTS),
			ArmorPiece.n(0.12F, Material.DIAMOND_BOOTS)
			
	),
	HORSE_ARMOR(-1,
		ArmorPiece.n(0.0F, Material.IRON_BARDING),
		ArmorPiece.n(0.0F, Material.GOLD_BARDING),
		ArmorPiece.n(0.0F, Material.DIAMOND_BARDING)
	),
	NOT_ARMOR(-1);

	private final ArmorPiece[] _types;
	private final int _slot;

	private ArmorType(int slot, ArmorPiece... types) {
		_types = types;
		_slot = slot;
	}

    /**
     * Get the slot index the armor is typically placed in.
     *
     * @return  -1 if no result.
     */
	public int getArmorSlot() {
		return _slot;
	}

    /**
     * Get the type of armor an {@code ItemStack} is.
     *
     * @param stack  The {@code ItemStack} to check.
     */
	public static ArmorType getType(ItemStack stack) {
		Material stackType = stack == null ? Material.AIR : stack.getType();
		return getType(stackType);
	}

    /**
     * Get the type of armor a material is.
     *
     * @param material  The {@code Material} to check.
     */
	public static ArmorType getType(Material material) {
		for (ArmorType armorType : ArmorType.values()) {
			for (ArmorPiece ap : armorType._types) {
				if (material != ap.material) continue;
					return armorType;
			}
		}
		return ArmorType.NOT_ARMOR;
	}

    /**
     * Get the sum of defense values of specified {@code ItemStack}'s.
     *
     * @param armor  The {@code ItemStack}'s to check.
     */
	public static float getDefense(ItemStack... armor) {
		
		if (armor == null || armor.length == 0)
			return 0.0F;
		
		float result = 0.0F;
		
		for (ItemStack item : armor) {
			result += getDefense(item.getType());
		}
		
		return result;
	}

    /**
     * Get the defense value for an armor material.
     *
     * <p>Returns 0.0F if the material is not armor.</p>
     *
     * @param material  The material to check.
     */
	public static float getDefense(Material material) {
        PreCon.notNull(material);
		
		for (ArmorType armorType : ArmorType.values()) {
			for (ArmorPiece ap : armorType._types) {
				if (material != ap.material) 
					continue;
				
				return ap.defense;
			}
		}
		
		return 0.0F;
	}
	
	/**
	 * Estimate damage after player/living entity armor absorbs it.
	 * Does not calculate for enchantment.
	 * @param entity
	 * @param damage
	 * @return
	 */
	public static double getEstimatedDamage(LivingEntity entity, double damage) {
		ItemStack[] armor = entity.getEquipment().getArmorContents();
		float defense = getDefense(armor);
		
		if (defense == 0.0F)
			return damage;
		
		return damage - (damage * defense);
	}

    /**
     * Represents information about an Armor item.
     */
	private static class ArmorPiece {
		Material material;
		float defense;
		
		private static ArmorPiece n(float defense, Material material) {
			ArmorPiece ap = new ArmorPiece();
			ap.material = material;
			ap.defense = defense;
			
			return ap;
		}
	}
} 