package com.jcwhatever.bukkit.generic.converters;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

/**
 * Converts a material name to a bukkit material
 */
public class ItemNameMaterialConverter extends ValueConverter<Material, String> {

	ItemNameMaterialConverter() {}

    /**
     * Converts a string name of the the material constant name into a
     * Bukkit Material enum. Also accepts the Minecraft item id as a string.
     */
	@Override
	protected Material onConvert(Object value) {
		if (value instanceof Material) {
			return (Material)value;
		}
		else if (value instanceof String) {

			String name = ((String)value).toUpperCase();
			
			try {
				return Material.valueOf(name);
			}
			catch (Exception e) {
				
				// Sender check not needed:
				MaterialData data = callUnconvert(ValueConverters.ITEM_NAME_MATERIALDATA, name);
				
				if (data == null)
					return null;
				
				return data.getItemType();
			}
		}
		else {

			return callUnconvert(ValueConverters.ITEM_MATERIAL_ID, value);
		}
	}


    /**
     * Converts a Bukkit material enum into a string representation.
     */
	@Override
	protected String onUnconvert(Object value) {
		if (value instanceof Material) {
			return ((Material)value).name();
		}
		
		return null;
	}

}
