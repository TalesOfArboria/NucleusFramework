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

import com.jcwhatever.bukkit.generic.collections.MultiBiMap;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import javax.annotation.Nullable;

/**
 * Get MaterialData based on string name of material or alternate name.
 */
public final class NamedMaterialData {

    private NamedMaterialData(){}

    private static MultiBiMap<String, MaterialData> _alternateNames = new MultiBiMap<String, MaterialData>();

    static {
        _alternateNames

                .add("COBBLE", new MaterialData(Material.COBBLESTONE))

                .add("STONE_BRICK", new MaterialData(Material.SMOOTH_BRICK, (byte) 0))
                .add("MOSSY_BRICK", new MaterialData(Material.SMOOTH_BRICK, (byte) 1))
                .add("CRACKED_BRICK", new MaterialData(Material.SMOOTH_BRICK, (byte) 2))
                .add("CHISELED_BRICK", new MaterialData(Material.SMOOTH_BRICK, (byte) 3))

                .add("GRANITE", new MaterialData(Material.STONE, (byte) 1))
                .add("POLISHED_GRANITE", new MaterialData(Material.STONE, (byte) 2))
                .add("SMOOTH_GRANITE", new MaterialData(Material.STONE, (byte) 2))
                .add("DIORITE", new MaterialData(Material.STONE, (byte) 3))
                .add("POLISHED_DIORITE", new MaterialData(Material.STONE, (byte) 4))
                .add("SMOOTH_DIORITE", new MaterialData(Material.STONE, (byte) 4))
                .add("ANDESITE", new MaterialData(Material.STONE, (byte) 5))
                .add("POLISHED_ANDESITE", new MaterialData(Material.STONE, (byte) 6))
                .add("SMOOTH_ANDESITE", new MaterialData(Material.STONE, (byte) 6))

                .add("OAK_PLANK", new MaterialData(Material.WOOD, (byte) 0))
                .add("SPRUCE_PLANK", new MaterialData(Material.WOOD, (byte) 1))
                .add("BIRCH_PLANK", new MaterialData(Material.WOOD, (byte) 2))
                .add("JUNGLE_PLANK", new MaterialData(Material.WOOD, (byte) 3))
                .add("ACACIA_PLANK", new MaterialData(Material.WOOD, (byte) 4))
                .add("DARK_OAK_PLANK", new MaterialData(Material.WOOD, (byte) 5))

                .add("OAK_SAPPLING", new MaterialData(Material.SAPLING, (byte) 0))
                .add("SPRUCE_SAPPLING", new MaterialData(Material.SAPLING, (byte) 1))
                .add("BIRCH_SAPPLING", new MaterialData(Material.SAPLING, (byte) 2))
                .add("JUNGLE_SAPPLING", new MaterialData(Material.SAPLING, (byte) 3))
                .add("ACACIA_SAPPLING", new MaterialData(Material.SAPLING, (byte) 4))
                .add("DARK_OAK_SAPPLING", new MaterialData(Material.SAPLING, (byte) 5))

                .add("OAK_LOG", new MaterialData(Material.LOG, (byte) 0))
                .add("SPRUCE_LOG", new MaterialData(Material.LOG, (byte) 1))
                .add("BIRCH_LOG", new MaterialData(Material.LOG, (byte) 2))
                .add("JUNGLE_LOG", new MaterialData(Material.LOG, (byte) 3))

                .add("ACACIA_LOG", new MaterialData(Material.LOG_2))
                .add("DARK_OAK_LOG", new MaterialData(Material.LOG_2, (byte) 1))

                .add("OAK_LEAVES", new MaterialData(Material.LEAVES, (byte) 0))
                .add("SPRUCE_LEAVES", new MaterialData(Material.LEAVES, (byte) 1))
                .add("BIRCH_LEAVES", new MaterialData(Material.LEAVES, (byte) 2))
                .add("JUNGLE_LEAVES", new MaterialData(Material.LEAVES, (byte) 3))

                .add("ACACIA_LEAVES", new MaterialData(Material.LEAVES_2))
                .add("DARK_OAK_LEAVES", new MaterialData(Material.LEAVES_2, (byte) 1))

                .add("CHISELED_SANDSTONE", new MaterialData(Material.SANDSTONE, (byte) 1))
                .add("SMOOTH_SANDSTONE", new MaterialData(Material.LEAVES, (byte) 2))

                .add("WHITE_WOOL", new MaterialData(Material.WOOL, (byte) 0))
                .add("ORANGE_WOOL", new MaterialData(Material.WOOL, (byte) 1))
                .add("MAGENTA_WOOL", new MaterialData(Material.WOOL, (byte) 2))
                .add("LIGHT_BLUE_WOOL", new MaterialData(Material.WOOL, (byte) 3))
                .add("YELLOW_WOOL", new MaterialData(Material.WOOL, (byte) 4))
                .add("LIME_WOOL", new MaterialData(Material.WOOL, (byte) 5))
                .add("PINK_WOOL", new MaterialData(Material.WOOL, (byte) 6))
                .add("GRAY_WOOL", new MaterialData(Material.WOOL, (byte) 7))
                .add("GREY_WOOL", new MaterialData(Material.WOOL, (byte) 7))
                .add("LIGHT_GRAY_WOOL", new MaterialData(Material.WOOL, (byte) 8))
                .add("LIGHT_GREY_WOOL", new MaterialData(Material.WOOL, (byte) 8))
                .add("CYAN_WOOL", new MaterialData(Material.WOOL, (byte) 9))
                .add("PURPLE_WOOL", new MaterialData(Material.WOOL, (byte) 10))
                .add("BLUE_WOOL", new MaterialData(Material.WOOL, (byte) 11))
                .add("BROWN_WOOL", new MaterialData(Material.WOOL, (byte) 12))
                .add("GREEN_WOOL", new MaterialData(Material.WOOL, (byte) 13))
                .add("RED_WOOL", new MaterialData(Material.WOOL, (byte) 14))
                .add("BLACK_WOOL", new MaterialData(Material.WOOL, (byte) 15))

                .add("WHITE_CARPET", new MaterialData(Material.CARPET, (byte) 0))
                .add("ORANGE_CARPET", new MaterialData(Material.CARPET, (byte) 1))
                .add("MAGENTA_CARPET", new MaterialData(Material.CARPET, (byte) 2))
                .add("LIGHT_BLUE_CARPET", new MaterialData(Material.CARPET, (byte) 3))
                .add("YELLOW_CARPET", new MaterialData(Material.CARPET, (byte) 4))
                .add("LIME_CARPET", new MaterialData(Material.CARPET, (byte) 5))
                .add("PINK_CARPET", new MaterialData(Material.CARPET, (byte) 6))
                .add("GRAY_CARPET", new MaterialData(Material.CARPET, (byte) 7))
                .add("GREY_CARPET", new MaterialData(Material.CARPET, (byte) 7))
                .add("LIGHT_GRAY_CARPET", new MaterialData(Material.CARPET, (byte) 8))
                .add("LIGHT_GREY_CARPET", new MaterialData(Material.CARPET, (byte) 8))
                .add("CYAN_CARPET", new MaterialData(Material.CARPET, (byte) 9))
                .add("PURPLE_CARPET", new MaterialData(Material.CARPET, (byte) 10))
                .add("BLUE_CARPET", new MaterialData(Material.CARPET, (byte) 11))
                .add("BROWN_CARPET", new MaterialData(Material.CARPET, (byte) 12))
                .add("GREEN_CARPET", new MaterialData(Material.CARPET, (byte) 13))
                .add("RED_CARPET", new MaterialData(Material.CARPET, (byte) 14))
                .add("BLACK_CARPET", new MaterialData(Material.CARPET, (byte) 15))

                .add("BLACK_DYE", new MaterialData(Material.INK_SACK, (byte) 0))
                .add("RED_DYE", new MaterialData(Material.INK_SACK, (byte) 1))
                .add("GREEN_DYE", new MaterialData(Material.INK_SACK, (byte) 2))
                .add("BROWN_DYE", new MaterialData(Material.INK_SACK, (byte) 3))
                .add("COCOA_BEANS", new MaterialData(Material.INK_SACK, (byte) 3))
                .add("BLUE_DYE", new MaterialData(Material.INK_SACK, (byte) 4))
                .add("LAPIS", new MaterialData(Material.INK_SACK, (byte) 4))
                .add("LAPIS_LAZULI", new MaterialData(Material.INK_SACK, (byte) 4))
                .add("PURPLE_DYE", new MaterialData(Material.INK_SACK, (byte) 5))
                .add("CYAN_DYE", new MaterialData(Material.INK_SACK, (byte) 6))
                .add("LIGHT_GRAY_DYE", new MaterialData(Material.INK_SACK, (byte) 7))
                .add("LIGHT_GREY_DYE", new MaterialData(Material.INK_SACK, (byte) 7))
                .add("GRAY_DYE", new MaterialData(Material.INK_SACK, (byte) 8))
                .add("GREY_DYE", new MaterialData(Material.INK_SACK, (byte) 8))
                .add("PINK_DYE", new MaterialData(Material.INK_SACK, (byte) 9))
                .add("LIME_DYE", new MaterialData(Material.INK_SACK, (byte) 10))
                .add("YELLOW_DYE", new MaterialData(Material.INK_SACK, (byte) 11))
                .add("LIGHT_BLUE_DYE", new MaterialData(Material.INK_SACK, (byte) 12))
                .add("MAGENTA_DYE", new MaterialData(Material.INK_SACK, (byte) 13))
                .add("ORANGE_DYE", new MaterialData(Material.INK_SACK, (byte) 14))
                .add("WHITE_DYE", new MaterialData(Material.INK_SACK, (byte) 15))
                .add("BONE_MEAL", new MaterialData(Material.INK_SACK, (byte) 15));
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
