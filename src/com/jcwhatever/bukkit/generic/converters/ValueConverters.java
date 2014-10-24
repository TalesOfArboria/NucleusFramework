package com.jcwhatever.bukkit.generic.converters;

public class ValueConverters {

    /**
     * Converts between a Potion and potion id.
     */
	public static final PotionIDConverter POTION_ID = new PotionIDConverter();

    /**
     * Convert between Minecraft material ID's and Bukkit Material enum.
     */
	public static final ItemMaterialIDConverter ITEM_MATERIAL_ID = new ItemMaterialIDConverter();

    /**
     * Convert between chat color codes that use the '&' character and valid chat color codes.
     */
	public static final AlternativeChatColorConverter ALT_CHAT_COLOR = new AlternativeChatColorConverter();

    /**
     * Converts a material name to a bukkit material
     */
	public static final ItemNameMaterialConverter ITEM_NAME_MATERIAL = new ItemNameMaterialConverter();

    /**
     * Converts between a Bukkit Material enum constant name as a string to MaterialData.
     * Also includes non Bukkit Material enum names.
     */
	public static final ItemNameMaterialDataConverter ITEM_NAME_MATERIALDATA = new ItemNameMaterialDataConverter();
}
