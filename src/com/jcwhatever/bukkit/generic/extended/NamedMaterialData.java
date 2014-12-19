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


package com.jcwhatever.bukkit.generic.extended;

import com.jcwhatever.bukkit.generic.collections.MultiValueBiMap;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import javax.annotation.Nullable;

/**
 * Get MaterialData based on string name of material or alternate name.
 */
public final class NamedMaterialData {

    private NamedMaterialData(){}

    private static MultiValueBiMap<String, MaterialData> _alternateNames = new MultiValueBiMap<String, MaterialData>();

    static {
        _alternateNames

                .put("COBBLE", new MaterialData(Material.COBBLESTONE))

                .put("STONE_BRICK", new MaterialData(Material.SMOOTH_BRICK, (byte) 0))
                .put("MOSSY_BRICK", new MaterialData(Material.SMOOTH_BRICK, (byte) 1))
                .put("CRACKED_BRICK", new MaterialData(Material.SMOOTH_BRICK, (byte) 2))
                .put("CHISELED_BRICK", new MaterialData(Material.SMOOTH_BRICK, (byte) 3))

                .put("GRANITE", new MaterialData(Material.STONE, (byte) 1))
                .put("POLISHED_GRANITE", new MaterialData(Material.STONE, (byte) 2))
                .put("SMOOTH_GRANITE", new MaterialData(Material.STONE, (byte)2))
                .put("DIORITE", new MaterialData(Material.STONE, (byte) 3))
                .put("POLISHED_DIORITE", new MaterialData(Material.STONE, (byte) 4))
                .put("SMOOTH_DIORITE", new MaterialData(Material.STONE, (byte) 4))
                .put("ANDESITE", new MaterialData(Material.STONE, (byte) 5))
                .put("POLISHED_ANDESITE", new MaterialData(Material.STONE, (byte) 6))
                .put("SMOOTH_ANDESITE", new MaterialData(Material.STONE, (byte) 6))

                .put("OAK_PLANK", new MaterialData(Material.WOOD, (byte) 0))
                .put("SPRUCE_PLANK", new MaterialData(Material.WOOD, (byte) 1))
                .put("BIRCH_PLANK", new MaterialData(Material.WOOD, (byte) 2))
                .put("JUNGLE_PLANK", new MaterialData(Material.WOOD, (byte) 3))
                .put("ACACIA_PLANK", new MaterialData(Material.WOOD, (byte) 4))
                .put("DARK_OAK_PLANK", new MaterialData(Material.WOOD, (byte) 5))

                .put("OAK_SAPPLING", new MaterialData(Material.SAPLING, (byte) 0))
                .put("SPRUCE_SAPPLING", new MaterialData(Material.SAPLING, (byte) 1))
                .put("BIRCH_SAPPLING", new MaterialData(Material.SAPLING, (byte) 2))
                .put("JUNGLE_SAPPLING", new MaterialData(Material.SAPLING, (byte) 3))
                .put("ACACIA_SAPPLING", new MaterialData(Material.SAPLING, (byte) 4))
                .put("DARK_OAK_SAPPLING", new MaterialData(Material.SAPLING, (byte) 5))

                .put("OAK_LOG", new MaterialData(Material.LOG, (byte) 0))
                .put("SPRUCE_LOG", new MaterialData(Material.LOG, (byte) 1))
                .put("BIRCH_LOG", new MaterialData(Material.LOG, (byte) 2))
                .put("JUNGLE_LOG", new MaterialData(Material.LOG, (byte) 3))

                .put("ACACIA_LOG", new MaterialData(Material.LOG_2))
                .put("DARK_OAK_LOG", new MaterialData(Material.LOG_2, (byte)1))

                .put("OAK_LEAVES", new MaterialData(Material.LEAVES, (byte) 0))
                .put("SPRUCE_LEAVES", new MaterialData(Material.LEAVES, (byte) 1))
                .put("BIRCH_LEAVES", new MaterialData(Material.LEAVES, (byte) 2))
                .put("JUNGLE_LEAVES", new MaterialData(Material.LEAVES, (byte) 3))

                .put("ACACIA_LEAVES", new MaterialData(Material.LEAVES_2))
                .put("DARK_OAK_LEAVES", new MaterialData(Material.LEAVES_2, (byte) 1))

                .put("CHISELED_SANDSTONE", new MaterialData(Material.SANDSTONE, (byte) 1))
                .put("SMOOTH_SANDSTONE", new MaterialData(Material.LEAVES, (byte) 2))

                .put("WHITE_WOOL", new MaterialData(Material.WOOL, (byte) 0))
                .put("ORANGE_WOOL", new MaterialData(Material.WOOL, (byte) 1))
                .put("MAGENTA_WOOL", new MaterialData(Material.WOOL, (byte) 2))
                .put("LIGHT_BLUE_WOOL", new MaterialData(Material.WOOL, (byte) 3))
                .put("YELLOW_WOOL", new MaterialData(Material.WOOL, (byte) 4))
                .put("LIME_WOOL", new MaterialData(Material.WOOL, (byte) 5))
                .put("PINK_WOOL", new MaterialData(Material.WOOL, (byte) 6))
                .put("GRAY_WOOL", new MaterialData(Material.WOOL, (byte) 7))
                .put("GREY_WOOL", new MaterialData(Material.WOOL, (byte) 7))
                .put("LIGHT_GRAY_WOOL", new MaterialData(Material.WOOL, (byte) 8))
                .put("LIGHT_GREY_WOOL", new MaterialData(Material.WOOL, (byte) 8))
                .put("CYAN_WOOL", new MaterialData(Material.WOOL, (byte) 9))
                .put("PURPLE_WOOL", new MaterialData(Material.WOOL, (byte) 10))
                .put("BLUE_WOOL", new MaterialData(Material.WOOL, (byte) 11))
                .put("BROWN_WOOL", new MaterialData(Material.WOOL, (byte) 12))
                .put("GREEN_WOOL", new MaterialData(Material.WOOL, (byte) 13))
                .put("RED_WOOL", new MaterialData(Material.WOOL, (byte) 14))
                .put("BLACK_WOOL", new MaterialData(Material.WOOL, (byte) 15))

                .put("WHITE_CARPET", new MaterialData(Material.CARPET, (byte) 0))
                .put("ORANGE_CARPET", new MaterialData(Material.CARPET, (byte) 1))
                .put("MAGENTA_CARPET", new MaterialData(Material.CARPET, (byte) 2))
                .put("LIGHT_BLUE_CARPET", new MaterialData(Material.CARPET, (byte) 3))
                .put("YELLOW_CARPET", new MaterialData(Material.CARPET, (byte) 4))
                .put("LIME_CARPET", new MaterialData(Material.CARPET, (byte) 5))
                .put("PINK_CARPET", new MaterialData(Material.CARPET, (byte) 6))
                .put("GRAY_CARPET", new MaterialData(Material.CARPET, (byte) 7))
                .put("GREY_CARPET", new MaterialData(Material.CARPET, (byte) 7))
                .put("LIGHT_GRAY_CARPET", new MaterialData(Material.CARPET, (byte) 8))
                .put("LIGHT_GREY_CARPET", new MaterialData(Material.CARPET, (byte) 8))
                .put("CYAN_CARPET", new MaterialData(Material.CARPET, (byte) 9))
                .put("PURPLE_CARPET", new MaterialData(Material.CARPET, (byte) 10))
                .put("BLUE_CARPET", new MaterialData(Material.CARPET, (byte) 11))
                .put("BROWN_CARPET", new MaterialData(Material.CARPET, (byte) 12))
                .put("GREEN_CARPET", new MaterialData(Material.CARPET, (byte) 13))
                .put("RED_CARPET", new MaterialData(Material.CARPET, (byte) 14))
                .put("BLACK_CARPET", new MaterialData(Material.CARPET, (byte) 15))

                .put("BLACK_DYE", new MaterialData(Material.INK_SACK, (byte) 0))
                .put("RED_DYE", new MaterialData(Material.INK_SACK, (byte) 1))
                .put("GREEN_DYE", new MaterialData(Material.INK_SACK, (byte) 2))
                .put("BROWN_DYE", new MaterialData(Material.INK_SACK, (byte) 3))
                .put("COCOA_BEANS", new MaterialData(Material.INK_SACK, (byte) 3))
                .put("BLUE_DYE", new MaterialData(Material.INK_SACK, (byte) 4))
                .put("LAPIS", new MaterialData(Material.INK_SACK, (byte) 4))
                .put("LAPIS_LAZULI", new MaterialData(Material.INK_SACK, (byte) 4))
                .put("PURPLE_DYE", new MaterialData(Material.INK_SACK, (byte) 5))
                .put("CYAN_DYE", new MaterialData(Material.INK_SACK, (byte) 6))
                .put("LIGHT_GRAY_DYE", new MaterialData(Material.INK_SACK, (byte) 7))
                .put("LIGHT_GREY_DYE", new MaterialData(Material.INK_SACK, (byte) 7))
                .put("GRAY_DYE", new MaterialData(Material.INK_SACK, (byte) 8))
                .put("GREY_DYE", new MaterialData(Material.INK_SACK, (byte) 8))
                .put("PINK_DYE", new MaterialData(Material.INK_SACK, (byte) 9))
                .put("LIME_DYE", new MaterialData(Material.INK_SACK, (byte) 10))
                .put("YELLOW_DYE", new MaterialData(Material.INK_SACK, (byte) 11))
                .put("LIGHT_BLUE_DYE", new MaterialData(Material.INK_SACK, (byte) 12))
                .put("MAGENTA_DYE", new MaterialData(Material.INK_SACK, (byte) 13))
                .put("ORANGE_DYE", new MaterialData(Material.INK_SACK, (byte) 14))
                .put("WHITE_DYE", new MaterialData(Material.INK_SACK, (byte) 15))
                .put("BONE_MEAL", new MaterialData(Material.INK_SACK, (byte) 15));
    }

    @Nullable
    public static MaterialData get(String materialName) {
        PreCon.notNull(materialName);

        String str = materialName.toUpperCase();

        if (str.startsWith("MINECRAFT:"))
            str = str.substring(10);

        // check for standard enum value
        try {
            Material material = Material.valueOf(str);
            if (material != null)
                return new MaterialData(material);
        }
        catch (Exception ignore) {}

        // check for alternate value
        MaterialData data = _alternateNames.getValue(str);
        if (data == null) {
            return null;
        }
        return data.clone();
    }

    public static String get(MaterialData materialData) {
        PreCon.notNull(materialData);

        String name = _alternateNames.getKey(materialData);

        if (name == null) {
            String result = materialData.getItemType().name();
            if (materialData.getData() != 0)
                result += ":" + materialData.getData();
            return result;
        } else {
            return name;
        }
    }
}
