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


package com.jcwhatever.nucleus.utils.materials;

import com.jcwhatever.nucleus.collections.MultiBiMap;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import javax.annotation.Nullable;

/**
 * Get {@link org.bukkit.material.MaterialData} based on name of the {@link org.bukkit.Material}
 * or alternate name.
 *
 * <p>Where Minecraft 1.8 names conflict with {@link org.bukkit.Material} names,
 * the 1.8 name is used.</p>
 */
public final class NamedMaterialData {

    private NamedMaterialData(){}

    private static MultiBiMap<String, MaterialData> _alternateNames =
            new MultiBiMap<String, MaterialData>();

    /**
     * Get {@link org.bukkit.material.MaterialData} using the
     * material name.
     *
     * <p>Checks alternate names first. If an alternate name is not found, the
     * name is checked against {@link org.bukkit.Material} names.</p>
     *
     * @param materialName  The non-case sensitive name of the {@link org.bukkit.Material}
     *                      or alternate name.
     *
     * @return  The {@link org.bukkit.material.MaterialData} or null if not found.
     */
    @Nullable
    public static MaterialData get(String materialName) {
        PreCon.notNull(materialName);

        String str = materialName.toUpperCase();

        if (str.startsWith("MINECRAFT:"))
            str = str.substring(10);

        // check for alternate value
        MaterialData data = _alternateNames.getValue(str);
        if (data == null) {

            // check for enum name
            try {
                Material material = Material.valueOf(str);
                if (material != null)
                    return new MaterialData(material);
            }
            catch (Exception ignore) {}
            return null;
        }
        return new MaterialData(data.getItemType(), data.getData());
    }

    /**
     * Get the name of the specified {@link org.bukkit.material.MaterialData} using
     * the most descriptive available name.
     *
     * @param materialData  The material data to get a name for.
     */
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

    static {
        _alternateNames

                .add("GRANITE", new MaterialData(Material.STONE, (byte) 1))
                .add("POLISHED_GRANITE", new MaterialData(Material.STONE, (byte) 2))
                .add("SMOOTH_GRANITE", new MaterialData(Material.STONE, (byte) 2))
                .add("DIORITE", new MaterialData(Material.STONE, (byte) 3))
                .add("POLISHED_DIORITE", new MaterialData(Material.STONE, (byte) 4))
                .add("SMOOTH_DIORITE", new MaterialData(Material.STONE, (byte) 4))
                .add("ANDESITE", new MaterialData(Material.STONE, (byte) 5))
                .add("POLISHED_ANDESITE", new MaterialData(Material.STONE, (byte) 6))
                .add("SMOOTH_ANDESITE", new MaterialData(Material.STONE, (byte) 6))

                .add("COARSE_DIRT", new MaterialData(Material.DIRT, (byte) 1))
                .add("PODZOL", new MaterialData(Material.DIRT, (byte) 2))

                .add("COBBLE", new MaterialData(Material.COBBLESTONE))
                .add("MOSSY_COBBLE", new MaterialData(Material.MOSSY_COBBLESTONE))

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

                .add("FLOWING_WATER", new MaterialData(Material.WATER))
                .add("WATER", new MaterialData(Material.STATIONARY_WATER))

                .add("FLOWING_LAVA", new MaterialData(Material.LAVA))
                .add("LAVA", new MaterialData(Material.STATIONARY_LAVA))

                .add("RED_SAND", new MaterialData(Material.SAND, (byte) 1))

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

                .add("WET_SPONGE", new MaterialData(Material.SPONGE, (byte) 1))

                .add("CHISELED_SANDSTONE", new MaterialData(Material.SANDSTONE, (byte) 1))
                .add("SMOOTH_SANDSTONE", new MaterialData(Material.LEAVES, (byte) 2))

                .add("NOTEBLOCK", new MaterialData(Material.NOTE_BLOCK))

                .add("GOLDEN_RAIL", new MaterialData(Material.POWERED_RAIL))

                .add("STICKY_PISTON", new MaterialData(Material.PISTON_STICKY_BASE))
                .add("PISTON", new MaterialData(Material.PISTON_BASE))

                .add("COBWEB", new MaterialData(Material.WEB))

                .add("TALLGRASS", new MaterialData(Material.LONG_GRASS))
                .add("TALL_GRASS", new MaterialData(Material.LONG_GRASS))

                .add("FERN", new MaterialData(Material.LONG_GRASS, (byte) 2))

                .add("DEADBUSH", new MaterialData(Material.DEAD_BUSH))

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

                .add("DANDELION", new MaterialData(Material.YELLOW_FLOWER))

                .add("RED_FLOWER", new MaterialData(Material.RED_ROSE))
                .add("POPPY", new MaterialData(Material.RED_ROSE))
                .add("BLUE_ORCHID", new MaterialData(Material.RED_ROSE, (byte) 1))
                .add("ALLIUM", new MaterialData(Material.RED_ROSE, (byte) 2))
                .add("AZURE_BLUET", new MaterialData(Material.RED_ROSE, (byte)3))
                .add("RED_TULIP", new MaterialData(Material.RED_ROSE, (byte)4))
                .add("ORANGE_TULIP", new MaterialData(Material.RED_ROSE, (byte)5))
                .add("WHITE_TULIP", new MaterialData(Material.RED_ROSE, (byte) 6))
                .add("PINK_TULIP", new MaterialData(Material.RED_ROSE, (byte) 7))
                .add("OXEYE_DAISY", new MaterialData(Material.RED_ROSE, (byte) 8))

                .add("DOUBLE_STONE_SLAB", new MaterialData(Material.DOUBLE_STEP))
                .add("DOUBLE_SANDSTONE_SLAB", new MaterialData(Material.DOUBLE_STEP, (byte) 1))

                .add("DOUBLE_COBBLE_SLAB", new MaterialData(Material.DOUBLE_STEP, (byte) 3))
                .add("DOUBLE_COBBLESTONE_SLAB", new MaterialData(Material.DOUBLE_STEP, (byte) 3))
                .add("DOUBLE_BRICK_SLAB", new MaterialData(Material.DOUBLE_STEP, (byte) 4))
                .add("DOUBLE_STONEBRICK_SLAB", new MaterialData(Material.DOUBLE_STEP, (byte) 5))
                .add("DOUBLE_NETHERBRICK_SLAB", new MaterialData(Material.STEP, (byte) 6))
                .add("DOUBLE_QUARTZ_SLAB", new MaterialData(Material.DOUBLE_STEP, (byte) 7))
                .add("DOUBLE_SMOOTH_STONE_SLAB", new MaterialData(Material.DOUBLE_STEP, (byte) 8))
                .add("DOUBLE_SMOOTH_SANDSTONE_SLAB", new MaterialData(Material.DOUBLE_STEP, (byte) 9))

                .add("DOUBLE_WOODEN_SLAB", new MaterialData(Material.WOOD_DOUBLE_STEP))
                .add("DOUBLE_WOOD_SLAB", new MaterialData(Material.WOOD_DOUBLE_STEP))
                .add("DOUBLE_OAK_SLAB", new MaterialData(Material.WOOD_DOUBLE_STEP))
                .add("DOUBLE_SPRUCE_SLAB", new MaterialData(Material.WOOD_DOUBLE_STEP, (byte) 1))
                .add("DOUBLE_BIRCH_SLAB", new MaterialData(Material.WOOD_DOUBLE_STEP, (byte)2))
                .add("DOUBLE_JUNGLE_SLAB", new MaterialData(Material.WOOD_DOUBLE_STEP, (byte)3))
                .add("DOUBLE_ACACIA_SLAB", new MaterialData(Material.WOOD_DOUBLE_STEP, (byte) 4))
                .add("DOUBLE_DARK_OAK_SLAB", new MaterialData(Material.WOOD_DOUBLE_STEP, (byte) 5))

                .add("WOODEN_SLAB", new MaterialData(Material.WOOD_STEP))
                .add("WOOD_SLAB", new MaterialData(Material.WOOD_STEP))
                .add("OAK_SLAB", new MaterialData(Material.WOOD_STEP))
                .add("SPRUCE_SLAB", new MaterialData(Material.WOOD_STEP, (byte)1))
                .add("BIRCH_SLAB", new MaterialData(Material.WOOD_STEP, (byte)2))
                .add("JUNGLE_SLAB", new MaterialData(Material.WOOD_STEP, (byte)3))
                .add("ACACIA_SLAB", new MaterialData(Material.WOOD_STEP, (byte) 4))
                .add("DARK_OAK_SLAB", new MaterialData(Material.WOOD_STEP, (byte) 5))

                .add("STONE_SLAB", new MaterialData(Material.STEP))
                .add("SANDSTONE_SLAB", new MaterialData(Material.STEP, (byte) 1))
                .add("WOOD_SLAB", new MaterialData(Material.STEP, (byte) 2))
                .add("WOODEN_SLAB", new MaterialData(Material.STEP, (byte) 2))
                .add("COBBLE_SLAB", new MaterialData(Material.STEP, (byte) 3))
                .add("COBBLESTONE_SLAB", new MaterialData(Material.STEP, (byte) 3))
                .add("BRICK_SLAB", new MaterialData(Material.STEP, (byte) 4))
                .add("STONEBRICK_SLAB", new MaterialData(Material.STEP, (byte) 5))
                .add("NETHERBRICK_SLAB", new MaterialData(Material.STEP, (byte) 6))
                .add("QUARTZ_SLAB", new MaterialData(Material.STEP, (byte) 7))
                .add("SANDSTONE_SLAB", new MaterialData(Material.STEP, (byte) 1))

                .add("BRICK_BLOCK", new MaterialData(Material.BRICK))

                .add("OAK_STAIRS", new MaterialData(Material.WOOD_STAIRS))
                .add("STONE_STAIRS", new MaterialData(Material.COBBLESTONE_STAIRS))
                .add("COBBLE_STAIRS", new MaterialData(Material.COBBLESTONE_STAIRS))
                .add("COBBLESTONE_STAIRS", new MaterialData(Material.COBBLESTONE_STAIRS))
                .add("STONEBRICK_STAIRS", new MaterialData(Material.SMOOTH_STAIRS))
                .add("STONE_BRICK_STAIRS", new MaterialData(Material.SMOOTH_STAIRS))
                .add("NETHERBRICK_STAIRS", new MaterialData(Material.NETHER_BRICK_STAIRS))
                .add("SPRUCE_STAIRS", new MaterialData(Material.SPRUCE_WOOD_STAIRS))
                .add("BIRCH_STAIRS", new MaterialData(Material.BIRCH_WOOD_STAIRS))
                .add("JUNGLE_STAIRS", new MaterialData(Material.JUNGLE_WOOD_STAIRS))

                .add("CRAFTING_TABLE", new MaterialData(Material.WORKBENCH))

                .add("FARMLAND", new MaterialData(Material.SOIL))

                .add("STANDING_SIGN", new MaterialData(Material.SIGN_POST))

                .add("SNOW_LAYER", new MaterialData(Material.SNOW))
                .add("SNOW", new MaterialData(Material.SNOW_BLOCK))

                .add("REEDS", new MaterialData(Material.SUGAR_CANE_BLOCK))

                .add("LIT_PUMPKIN", new MaterialData(Material.JACK_O_LANTERN))

                .add("WHITE_STAINED_GLASS", new MaterialData(Material.STAINED_GLASS))
                .add("WHITE_GLASS", new MaterialData(Material.STAINED_GLASS))
                .add("ORANGE_STAINED_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 1))
                .add("ORANGE_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 1))
                .add("MAGENTA_STAINED_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 2))
                .add("MAGENTA_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 2))
                .add("LIGHT_BLUE_STAINED_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 3))
                .add("LIGHT_BLUE_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 3))
                .add("YELLOW_STAINED_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 4))
                .add("YELLOW_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 4))
                .add("LIME_STAINED_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 5))
                .add("LIME_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 5))
                .add("PINK_STAINED_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 6))
                .add("PINK_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 6))
                .add("GRAY_STAINED_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 7))
                .add("GRAY_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 7))
                .add("LIGHT_GRAY_STAINED_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 8))
                .add("LIGHT_GRAY_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 8))
                .add("CYAN_STAINED_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 9))
                .add("CYAN_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 9))
                .add("PURPLE_STAINED_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 10))
                .add("PURPLE_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 10))
                .add("BLUE_STAINED_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 11))
                .add("BLUE_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 11))
                .add("BROWN_STAINED_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 12))
                .add("BROWN_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 12))
                .add("GREEN_STAINED_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 13))
                .add("GREEN_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 13))
                .add("RED_STAINED_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 14))
                .add("RED_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 14))
                .add("BLACK_STAINED_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 15))
                .add("BLACK_GLASS", new MaterialData(Material.STAINED_GLASS, (byte) 15))

                .add("WHITE_STAINED_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 0))
                .add("WHITE_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 0))
                .add("ORANGE_STAINED_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 1))
                .add("ORANGE_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 1))
                .add("MAGENTA_STAINED_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 2))
                .add("MAGENTA_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 2))
                .add("LIGHT_BLUE_STAINED_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 3))
                .add("LIGHT_BLUE_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 3))
                .add("YELLOW_STAINED_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 4))
                .add("YELLOW_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 4))
                .add("LIME_STAINED_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 5))
                .add("LIME_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 5))
                .add("PINK_STAINED_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 6))
                .add("PINK_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 6))
                .add("GRAY_STAINED_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7))
                .add("GRAY_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7))
                .add("LIGHT_GRAY_STAINED_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 8))
                .add("LIGHT_GRAY_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 8))
                .add("CYAN_STAINED_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 9))
                .add("CYAN_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 9))
                .add("PURPLE_STAINED_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 10))
                .add("PURPLE_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 10))
                .add("BLUE_STAINED_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 11))
                .add("BLUE_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 11))
                .add("BROWN_STAINED_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 12))
                .add("BROWN_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 12))
                .add("GREEN_STAINED_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 13))
                .add("GREEN_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 13))
                .add("RED_STAINED_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 14))
                .add("RED_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 14))
                .add("BLACK_STAINED_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 15))
                .add("BLACK_GLASS_PANE", new MaterialData(Material.STAINED_GLASS_PANE, (byte) 15))

                .add("MONSTER_EGG", new MaterialData(Material.MONSTER_EGGS))
                .add("STONE_MONSTER_EGG", new MaterialData(Material.MONSTER_EGGS))
                .add("COBBLE_MONSTER_EGG", new MaterialData(Material.STAINED_GLASS, (byte) 1))
                .add("COBBLESTONE_MONSTER_EGG", new MaterialData(Material.STAINED_GLASS, (byte) 1))
                .add("STONEBRICK_MONSTER_EGG", new MaterialData(Material.STAINED_GLASS, (byte) 2))
                .add("MOSSY_STONEBRICK_MONSTER_EGG", new MaterialData(Material.STAINED_GLASS, (byte) 3))
                .add("CRACKED_STONEBRICK_MONSTER_EGG", new MaterialData(Material.STAINED_GLASS, (byte) 4))
                .add("CHISELED_STONEBRICK_MONSTER_EGG", new MaterialData(Material.STAINED_GLASS, (byte) 5))

                .add("BROWN_MUSHROOM_BLOCK", new MaterialData(Material.HUGE_MUSHROOM_1))
                .add("RED_MUSHROOM_BLOCK", new MaterialData(Material.HUGE_MUSHROOM_2))

                .add("IRON_BARS", new MaterialData(Material.IRON_FENCE))

                .add("GLASS_PANE", new MaterialData(Material.THIN_GLASS))

                .add("WATERLILY", new MaterialData(Material.WATER_LILY))
                .add("LILY_PAD", new MaterialData(Material.WATER_LILY))

                .add("NETHER_BRICK", new MaterialData(Material.NETHER_BRICK_ITEM))
                .add("NETHERBRICK", new MaterialData(Material.NETHER_BRICK_ITEM))

                .add("NETHER_BRICK_FENCE", new MaterialData(Material.NETHER_FENCE))
                .add("NETHERBRICK_FENCE", new MaterialData(Material.NETHER_FENCE))

                .add("NETHER_WART", new MaterialData(Material.NETHER_WARTS))

                .add("ENCHANTING_TABLE", new MaterialData(Material.ENCHANTMENT_TABLE))

                .add("END_PORTAL", new MaterialData(Material.ENDER_PORTAL))
                .add("END_PORTAL_FRAME", new MaterialData(Material.ENDER_PORTAL_FRAME))
                .add("END_STONE", new MaterialData(Material.ENDER_STONE))

                .add("REDSTONE_LAMP", new MaterialData(Material.REDSTONE_LAMP_OFF))
                .add("LIT_REDSTONE_LAMP", new MaterialData(Material.REDSTONE_LAMP_ON))

                .add("COMMAND_BLOCK", new MaterialData(Material.COMMAND))

                .add("COBBLESTONE_WALL", new MaterialData(Material.COBBLE_WALL))
                .add("MOSSY_COBBLESTONE_WALL", new MaterialData(Material.COBBLE_WALL, (byte) 1))
                .add("MOSSY_COBBLE_WALL", new MaterialData(Material.COBBLE_WALL, (byte) 1))

                .add("CHISELED_QUARTZ_BLOCK", new MaterialData(Material.QUARTZ_BLOCK, (byte) 1))
                .add("PILLAR_QUARTZ_BLOCK", new MaterialData(Material.QUARTZ_BLOCK, (byte) 2))

                .add("STAINED_HARDENED_CLAY", new MaterialData(Material.STAINED_CLAY))
                .add("WHITE_STAINED_HARDENED_CLAY", new MaterialData(Material.STAINED_CLAY))
                .add("WHITE_CLAY", new MaterialData(Material.STAINED_CLAY))
                .add("ORANGE_CLAY", new MaterialData(Material.STAINED_CLAY, (byte) 1))
                .add("MAGENTA_CLAY", new MaterialData(Material.STAINED_CLAY, (byte) 2))
                .add("LIGHT_BLUE_CLAY", new MaterialData(Material.STAINED_CLAY, (byte) 3))
                .add("YELLOW_CLAY", new MaterialData(Material.STAINED_CLAY, (byte) 4))
                .add("LIME_CLAY", new MaterialData(Material.STAINED_CLAY, (byte) 5))
                .add("PINK_CLAY", new MaterialData(Material.STAINED_CLAY, (byte) 6))
                .add("GRAY_CLAY", new MaterialData(Material.STAINED_CLAY, (byte) 7))
                .add("LIGHT_GRAY_CLAY", new MaterialData(Material.STAINED_CLAY, (byte) 8))
                .add("CYAN_CLAY", new MaterialData(Material.STAINED_CLAY, (byte) 9))
                .add("PURPLE_CLAY", new MaterialData(Material.STAINED_CLAY, (byte) 10))
                .add("BLUE_CLAY", new MaterialData(Material.STAINED_CLAY, (byte) 11))
                .add("BROWN_CLAY", new MaterialData(Material.STAINED_CLAY, (byte) 12))
                .add("GREEN_CLAY", new MaterialData(Material.STAINED_CLAY, (byte) 13))
                .add("RED_CLAY", new MaterialData(Material.STAINED_CLAY, (byte) 14))
                .add("BLACK_CLAY", new MaterialData(Material.STAINED_CLAY, (byte) 15))

                .add("SLIME", new MaterialData(Material.SLIME_BLOCK))

                .add("PRISMARINE_BRICK", new MaterialData(Material.PRISMARINE, (byte) 1))
                .add("DARK_PRISMARINE", new MaterialData(Material.PRISMARINE, (byte) 2))

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

                .add("HARDENED_CLAY", new MaterialData(Material.HARD_CLAY))

                .add("SUNFLOWER", new MaterialData(Material.DOUBLE_PLANT))
                .add("LILAC", new MaterialData(Material.DOUBLE_PLANT, (byte) 1))
                .add("DOUBLE_TALLGRASS", new MaterialData(Material.DOUBLE_PLANT, (byte) 2))
                .add("DOUBLE_TALL_GRASS", new MaterialData(Material.DOUBLE_PLANT, (byte) 2))
                .add("LARGE_FERN", new MaterialData(Material.DOUBLE_PLANT, (byte) 3))
                .add("ROSE_BUSH", new MaterialData(Material.DOUBLE_PLANT, (byte) 4))
                .add("PEONY", new MaterialData(Material.DOUBLE_PLANT, (byte) 5))

                .add("CHISELED_RED_SANDSTONE", new MaterialData(Material.RED_SANDSTONE, (byte) 1))
                .add("SMOOTH_RED_SANDSTONE", new MaterialData(Material.RED_SANDSTONE, (byte) 2))

                .add("ACACIA_DOOR", new MaterialData(Material.ACACIA_DOOR_ITEM))
                .add("SPRUCE_DOOR", new MaterialData(Material.SPRUCE_DOOR_ITEM))
                .add("BIRCH_DOOR", new MaterialData(Material.BIRCH_DOOR_ITEM))
                .add("JUNGLE_DOOR", new MaterialData(Material.JUNGLE_DOOR_ITEM))
                .add("DARK_OAK_DOOR", new MaterialData(Material.DARK_OAK_DOOR_ITEM))

                .add("STONE_SHOVEL", new MaterialData(Material.STONE_SPADE))
                .add("WOOD_SHOVEL", new MaterialData(Material.WOOD_SPADE))
                .add("WOODEN_SHOVEL", new MaterialData(Material.WOOD_SPADE))
                .add("IRON_SHOVEL", new MaterialData(Material.IRON_SPADE))
                .add("GOLD_SHOVEL", new MaterialData(Material.GOLD_SPADE))
                .add("GOLDEN_SHOVEL", new MaterialData(Material.GOLD_SPADE))
                .add("DIAMOND_SHOVEL", new MaterialData(Material.DIAMOND_SPADE))


                .add("STONE_PRESSURE_PLATE", new MaterialData(Material.STONE_PLATE))
                .add("WOOD_PRESSURE_PLATE", new MaterialData(Material.WOOD_PLATE))
                .add("WOODEN_PRESSURE_PLATE", new MaterialData(Material.WOOD_PLATE))
                .add("GOLD_PRESSURE_PLATE", new MaterialData(Material.GOLD_PLATE))
                .add("GOLDEN_PRESSURE_PLATE", new MaterialData(Material.GOLD_PLATE))
                .add("LIGHT_WEIGHTED_PRESSURE_PLATE", new MaterialData(Material.GOLD_PLATE))
                .add("IRON_PRESSURE_PLATE", new MaterialData(Material.IRON_PLATE))
                .add("HEAVY_WEIGHTED_PRESSURE_PLATE", new MaterialData(Material.IRON_PLATE))

                .add("FILLED_MAP", new MaterialData(Material.MAP))

                .add("SPAWN_EGG", new MaterialData(Material.MONSTER_EGG, (byte) 1))

                .add("EXPERIENCE_BOTTLE", new MaterialData(Material.EXP_BOTTLE))

                .add("WRITABLE_BOOK", new MaterialData(Material.BOOK_AND_QUILL))

                .add("FLOWER_POT", new MaterialData(Material.FLOWER_POT_ITEM))

                .add("SKULL", new MaterialData(Material.SKULL_ITEM))

                .add("CARROT_ON_A_STICK", new MaterialData(Material.CARROT_STICK))

                .add("FIREWORKS", new MaterialData(Material.FIREWORK))

                .add("COMPARATOR", new MaterialData(Material.REDSTONE_COMPARATOR))

                .add("NETHERBRICK", new MaterialData(Material.NETHER_BRICK_ITEM))

                .add("TNT_MINECART", new MaterialData(Material.EXPLOSIVE_MINECART))
                .add("COMMAND_BLOCK_MINECART", new MaterialData(Material.COMMAND_MINECART))

                .add("IRON_HORSE_ARMOR", new MaterialData(Material.IRON_BARDING))
                .add("GOLD_HORSE_ARMOR", new MaterialData(Material.GOLD_BARDING))
                .add("DIAMOND_HORSE_ARMOR", new MaterialData(Material.DIAMOND_BARDING))

                .add("LEAD", new MaterialData(Material.LEASH))

                .add("STONEBRICK", new MaterialData(Material.SMOOTH_BRICK, (byte) 0))
                .add("MOSSY_STONEBRICK", new MaterialData(Material.SMOOTH_BRICK, (byte) 1))
                .add("CRACKED_STONEBRICK", new MaterialData(Material.SMOOTH_BRICK, (byte) 2))
                .add("CHISELED_STONEBRICK", new MaterialData(Material.SMOOTH_BRICK, (byte) 3))

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
                .add("BONE_MEAL", new MaterialData(Material.INK_SACK, (byte) 15))
        ;
    }
}
