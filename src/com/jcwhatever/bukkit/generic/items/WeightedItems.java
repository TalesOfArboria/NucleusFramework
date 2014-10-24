package com.jcwhatever.bukkit.generic.items;

import com.jcwhatever.bukkit.generic.collections.Weighted;
import com.jcwhatever.bukkit.generic.collections.WeightedList;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeightedItems extends WeightedList<ItemStack> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 910377756705998699L;
	private static Map<String, Integer> _weightMap = new HashMap<String, Integer>(350);
	
	public WeightedItems() {}
	
	public WeightedItems(Collection<ItemStack> items) {
		add(items);
	}
	
	public WeightedItems(ItemStack[] items) {
		add(items);
	}
	
	public WeightedItems add(String materialName) {
		ItemStack[] wrappers = ItemStackHelper.parse(materialName);
		this.add(wrappers);
		return this;
	}
	
	public void add(ItemStack wrapper) {
		add(wrapper, getWeight(wrapper));
	}
	
	public void add(Collection<ItemStack> items) {
		for (ItemStack item : items) {
			ItemStack stack =  new ItemStack(item);
			this.add(stack);
		}
	}

	public void add(ItemStack[] items) {
		for (ItemStack item : items) {
			ItemStack stack =  new ItemStack(item);
			this.add(stack);
		}
	}
	
	public List<ItemStack> getItemStacks() {
		List<ItemStack> items = new ArrayList<ItemStack>(this.size());
		
		for (Weighted<ItemStack> weighted : this) {
			items.add(weighted.getItem());
		}
		
		return items;
	}
	
	private static int getWeight(ItemStack wrapper) {
		Integer weight = _weightMap.get(wrapper.getType().name());
		if (weight != null)
			return weight;
		return 5;
	}
	
	static {
		new MapHelper()
		.add(9,  "APPLE")
		.add(10, "ARROW")
		.add(5,  "BAKED_POTATO")
		.add(5,  "BLAZE_POWDER")
		.add(5,  "BOAT")
		.add(8,  "BONE")
		.add(9,  "BOW")
		.add(2,  "BOWL")
		.add(8,  "BREAD")
		.add(10,  "BROWN_MUSHROOM")
		.add(1,  "CAKE")
		.add(9,  "CARROT")
		.add(6,  "CHAINMAIL_BOOTS")
		.add(4,  "CHAINMAIL_CHESTPLATE")
		.add(6,  "CHAINMAIL_HELMET")
		.add(5,  "CHAINMAIL_LEGGINGS")
		.add(6,  "CHAINMAIL_HELMET")
		.add(5,  "COAL")
		.add(1,  "COAL_BLOCK")
		.add(5,  "COCOA")
		.add(2,  "COOKED_BEEF")
		.add(2,  "COOKED_CHICKEN")
		.add(2,  "COOKED_FISH")
		.add(8,  "COOKIE")
		.add(6,  "CHAINMAIL_HELMET")
		.add(3,  "DIAMOND")
		.add(1,  "DIAMOND_AXE")
		.add(1,  "DIAMOND_BARDING")
		.add(1,  "DIAMOND_BLOCK")
		.add(1,  "DIAMOND_BOOTS")
		.add(1,  "DIAMOND_CHESTPLATE")
		.add(1,  "DIAMOND_HELMET")
		.add(1,  "DIAMOND_HOE")
		.add(1,  "DIAMOND_LEGGINGS")
		.add(1,  "DIAMOND_ORE")
		.add(1,  "DIAMOND_PICKAXE")
		.add(1,  "DIAMOND_SPADE")
		.add(1,  "DIAMOND_SWORD")
		.add(6,  "EGG")
		.add(5,  "ENDER_PEARL")
		.add(1,  "EXP_BOTTLE")
		.add(6,  "FEATHER")
		.add(5,  "FERMENTED_SPIDER_EYE")
		.add(4,  "FISHING_ROD")
		.add(5,  "FLINT")
		.add(3,  "FLINT_AND_STEEL")
		.add(1,  "GHAST_TEAR")
		.add(3,  "GLASS_BOTTLE")
		.add(3,  "GLOWSTONE_DUST")
		.add(6,  "GOLD_AXE")
		.add(6,  "GOLD_BARDING")
		.add(1,  "GOLD_BLOCK")
		.add(7,  "GOLD_BOOTS")
		.add(5,  "GOLD_CHESTPLATE")
		.add(7,  "GOLD_HELMET")
		.add(6,  "GOLD_HOE")
		.add(6,  "GOLD_INGOT")
		.add(4,  "GOLD_LEGGINGS")
		.add(6,  "GOLD_NUGGET")
		.add(6,  "GOLD_PICKAXE")
		.add(6,  "GOLD_AXE")
		.add(6,  "GOLD_SPADE")
		.add(6,  "GOLD_SWORD")
		.add(1,  "GOLDEN_APPLE")
		.add(1,  "GOLDEN_CARROT")
		.add(2,  "GRILLED_PORK")
		.add(6,  "GOLD_AXE")
		.add(2,  "INK_SACK")
		.add(4,  "IRON_AXE")
		.add(4,  "IRON_BARDING")
		.add(1,  "IRON_BLOCK")
		.add(5,  "IRON_BOOTS")
		.add(3,  "IRON_CHESTPLATE")
		.add(5,  "IRON_HELMET")
		.add(4,  "IRON_HOE")
		.add(4,  "IRON_INGOT")
		.add(4,  "IRON_LEGGINGS")
		.add(4,  "IRON_PICKAXE")
		.add(4,  "IRON_SPADE")
		.add(4,  "IRON_SWORD")
		.add(1,  "LAVA")
		.add(1,  "LAVA_BUCKET")
		.add(7,  "LEATHER")
		.add(7,  "LEATHER_BOOTS")
		.add(6,  "LEATHER_CHESTPLATE")
		.add(7,  "LEATHER_HELMET")
		.add(6,  "LEATHER_LEGGINGS")
		.add(2,  "MAGMA_CREAM")
		.add(4,  "MELON")
		.add(4,  "MILK_BUCKET")
		.add(3,  "NETHER_WARTS")
		.add(1,  "NETHER_STAR")
		.add(6,  "PORK")
		.add(7,  "POTATO")
		.add(7,  "POTATO_ITEM")
		.add(2,  "POTION")
		.add(5,  "PUMKIN_PIE")
		.add(5,  "RAW_BEEF")
		.add(6,  "RAW_CHICKEN")
		.add(7,  "RAW_FISH")
		.add(8,  "RED_MUSHROOM")
		.add(9,  "ROTTEN_FLESH")
		.add(4,  "SADDLE")
		.add(7,  "SHEARS")
		.add(7,  "SNOW_BALL")
		.add(4,  "SPECKLED_MELON")
		.add(6,  "SPIDER_EYE")
		.add(8,  "STICK")
		.add(7,  "STONE_AXE")
		.add(7,  "STONE_HOE")
		.add(7,  "STONE_PICKAXE")
		.add(7,  "STONE_SPADE")
		.add(8,  "STONE_SWORD")
		.add(6,  "STRING")
		.add(1,  "TNT")
		.add(7,  "WHEAT")
		.add(9,  "WOOD_AXE")
		.add(9,  "WOOD_HOE")
		.add(9,  "WOOD_PICKAXE")
		.add(9,  "WOOD_SPADE")
		.add(10,  "WOOD_SWORD")
		;
		
		
		
	}
	
	
	static class MapHelper {
		public MapHelper add(int weight, String materialName) {
			_weightMap.put(materialName, weight);
			return this;	
		}
	}

}
