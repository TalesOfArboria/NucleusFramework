package com.jcwhatever.bukkit.generic.converters;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Convert between Minecraft material ID's and Bukkit Material enum.
 */
public class ItemMaterialIDConverter extends ValueConverter<Integer, Material> {

	ItemMaterialIDConverter() {}

    /**
     * Convert Bukkit Material enum to Minecraft item ID.
     * @param value Valid types are Material, ItemStack, or a String that can be parsed into an item stack.
     */
	@Override
	protected Integer onConvert(Object value) {
		Material material;

		if (value instanceof Material) {
            material = (Material) value;
        }
        else if (value instanceof String) {
			material = callConvert(ValueConverters.ITEM_NAME_MATERIAL, value);
		}
		else {
			return null;
		}

		if (material == null)
			return null;

		//return _idLookup.get(material);
		ItemStack temp = new ItemStack(material);
		return temp.getTypeId();
	}


    /**
     * Convert a number that represents a Minecraft item ID into
     * its Bukkit Material enum equivalent.
     *
     * @param value  Valid types are number values, or a String with the material name or item id
     * @return
     */
	@Override
	protected Material onUnconvert(Object value) {
		Integer id = 0;

		if (value instanceof Byte) {
			id = (int)(byte)value;
		}
		else if (value instanceof Short) {
			id = (int)(short)value;
		}
		else if (value instanceof Integer) {
			id = (int)value;
		}
		else if (value instanceof String) {

            String str = (String)value;

            try {
                id = Integer.parseInt(str);
            }
            catch (NumberFormatException nfe) {
                nfe.printStackTrace();
                return null;
            }
        }

		//return _materialLookup.get(id);
		return Material.getMaterial(id);
	}


}
