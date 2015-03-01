/*
 * This file is part of NucleusFramework for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.nucleus.utils.converters;

import com.jcwhatever.nucleus.utils.materials.NamedMaterialData;

import org.bukkit.material.MaterialData;

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
