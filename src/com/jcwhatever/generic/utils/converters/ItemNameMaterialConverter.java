/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.generic.utils.converters;

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
