package com.jcwhatever.bukkit.generic.storage.settings;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class ValueType {
	
	public static final Class<Boolean> BOOLEAN = Boolean.class;
	public static final Class<String> STRING = String.class;
	public static final Class<Integer> INTEGER = Integer.class;
	public static final Class<Double> DOUBLE = Double.class;
	public static final Class<ItemStack> ITEMSTACK = ItemStack.class;
	public static final Class<Location> LOCATION = Location.class;
	public static final Class<java.util.UUID> UUID = java.util.UUID.class;
}
