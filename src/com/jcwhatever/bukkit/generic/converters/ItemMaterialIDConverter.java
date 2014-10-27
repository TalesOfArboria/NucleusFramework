/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


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
