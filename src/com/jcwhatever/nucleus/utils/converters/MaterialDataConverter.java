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
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import javax.annotation.Nullable;

/**
 * Converts {@link org.bukkit.inventory.ItemStack} or {@link java.lang.String} representation of a
 * material ID or material name to {@link org.bukkit.material.MaterialData}.
 */
public class MaterialDataConverter extends Converter<MaterialData> {

    protected MaterialDataConverter() {}

    @Nullable
    @Override
    protected MaterialData onConvert(@Nullable Object value) {

        if (value instanceof MaterialData) {
            return (MaterialData)value;
        }
        else if (value instanceof String) {

            int id = TextUtils.parseInt((String)value, Integer.MIN_VALUE);
            if (id != Integer.MIN_VALUE) {
                Material material = Material.getMaterial(id);
                return new MaterialData(material);
            }

            return NamedMaterialData.get((String) value);
        }
        else if (value instanceof ItemStack) {
            return ((ItemStack)value).getData();
        }
        return null;
    }
}
