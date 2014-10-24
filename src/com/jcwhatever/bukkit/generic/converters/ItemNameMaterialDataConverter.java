package com.jcwhatever.bukkit.generic.converters;

import com.jcwhatever.bukkit.generic.extended.NamedMaterialData;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.jcwhatever.bukkit.generic.collections.MultiValueMap;
import com.jcwhatever.bukkit.generic.items.ItemStackHelper;

/**
 * Converts between a Bukkit Material enum constant name as a string to MaterialData.
 * Also includes non Bukkit Material enum names.
 */
public class ItemNameMaterialDataConverter extends ValueConverter<String, MaterialData> {

    ItemNameMaterialDataConverter() {}

    /**
     * Convert MaterialData, ItemStack, BlockState, or Block into
     * a Material Name string.
     */
    @Override
    protected String onConvert(Object value) {
        MaterialData data;
        if (value instanceof MaterialData) {
            data = (MaterialData)value;
        }
        else {
            return null;
        }

        return NamedMaterialData.get(data);
    }

    /**
     * Gets MaterialData from a Material constant name string.
     */
    @Override
    protected MaterialData onUnconvert(Object value) {

        if (value instanceof String) {

            return NamedMaterialData.get((String)value);
        }

        return null;
    }

}
