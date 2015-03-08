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

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Utility to filter and identify {@link org.bukkit.Material}'s by
 * property.
 */
public class Materials {

    Materials() {}

    private static Multimap<Material, MaterialProperty> _properties =
            MultimapBuilder.enumKeys(Material.class).hashSetValues().build();

    private static Multimap<MaterialProperty, Material> _materialsByProperty =
            MultimapBuilder.hashKeys().enumSetValues(Material.class).build();

    /**
     * Determine if a material has a property.
     *
     * @param material      The {@link org.bukkit.Material} to check.
     * @param propertyName  The name of the property to check.
     */
    public static boolean hasProperty(Material material, String propertyName) {
        PreCon.notNull(material);
        PreCon.notNull(propertyName);

        return hasProperty(material, new MaterialProperty(propertyName));
    }

    /**
     * Determine if a material has a property.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     * @param property  The {@link MaterialProperty} to check.
     */
    public static boolean hasProperty(Material material, MaterialProperty property) {
        PreCon.notNull(material);
        PreCon.notNull(property);

        return _properties.get(material).contains(property);
    }

    /**
     * Get all materials that match the specified properties.
     *
     * @param properties  The {@link MaterialProperty}'s to check for.
     */
    public static Set<Material> get(MaterialProperty... properties) {
        PreCon.notNull(properties);

        Set<Material> results = EnumSet.noneOf(Material.class);

        for (MaterialProperty property : properties) {
            Collection<Material> stored = _materialsByProperty.get(property);

            Iterator<Material> iterator = results.iterator();

            while (iterator.hasNext()) {
                Material material = iterator.next();

                if (!stored.contains(material))
                    iterator.remove();
            }

            results.addAll(stored);
        }

        return results;
    }

    /**
     * Get all {@link MaterialProperty}'s of the specified {@link org.bukkit.Material}.
     *
     * @param material  The material to check.
     */
    public static Set<MaterialProperty> getProperties(Material material) {
        PreCon.notNull(material);

        Collection<MaterialProperty> stored = _properties.get(material);

        return new HashSet<>(stored);
    }

    /**
     * Removes {@link org.bukkit.Material}'s from the provided {@link java.util.Collection} that
     * have the specified {@link MaterialProperty}'s.
     *
     * <p>Requires the provided collections iterator to implement the remove method.</p>
     *
     * @param materials   The collection of materials to modify.
     * @param properties  The properties to remove from the collection.
     */
    public static void remove(Collection<Material> materials,
                                               MaterialProperty... properties) {
        PreCon.notNull(materials);
        PreCon.notNull(properties);

        Iterator<Material> iterator = materials.iterator();

        while (iterator.hasNext()) {
            Material material = iterator.next();

            for (MaterialProperty property : properties) {
                if (hasProperty(material, property)) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    /**
     * Determine if a material is transparent.
     *
     * <p>This is the transparency that allows the player to walk through
     * a block, not the visual transparency as in glass.</p>
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see MaterialProperty#TRANSPARENT
     */
    public static boolean isTransparent(Material material) {
        return hasProperty(material, MaterialProperty.TRANSPARENT);
    }

    /**
     * Determine if a material is a surface.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#SURFACE
     */
    public static boolean isSurface(Material material) {
        return hasProperty(material, MaterialProperty.SURFACE);
    }

    /**
     * Determine if a material is an openable boundary.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#OPENABLE_BOUNDARY
     */
    public static boolean isOpenable(Material material) {
        return hasProperty(material, MaterialProperty.OPENABLE_BOUNDARY);
    }

    /**
     * Determine if a material is a door.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#DOOR
     */
    public static boolean isDoor(Material material) {
        return hasProperty(material, MaterialProperty.DOOR);
    }

    /**
     * Determine if a material is a fence gate.
     *
     * @param material The {@link org.bukkit.Material} to check.
     * @see MaterialProperty#FENCE_GATE
     */
    public static boolean isFenceGate(Material material) {
        return hasProperty(material, MaterialProperty.FENCE_GATE);
    }

    /**
     * Determine if a material is a trap door.
     *
     * @param material The {@link org.bukkit.Material} to check.
     * @see MaterialProperty#TRAPDOOR
     */
    public static boolean isTrapDoor(Material material) {
        return hasProperty(material, MaterialProperty.TRAPDOOR);
    }

    /**
     * Determine if a material is redstone compatible.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#REDSTONE
     */
    public static boolean isRedstoneCompatible(Material material) {
        return hasProperty(material, MaterialProperty.REDSTONE);
    }

    /**
     * Determine if a material activates a redstone current when interacted
     * with by a player.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#REDSTONE_SWITCH
     */
    public static boolean isRedstoneSwitch(Material material) {
        return hasProperty(material, MaterialProperty.REDSTONE_SWITCH);
    }

    /**
     * Determine if a material is a button.
     *
     * @param material The {@link org.bukkit.Material} to check.
     * @see MaterialProperty#BUTTON
     */
    public static boolean isButton(Material material) {
        return hasProperty(material, MaterialProperty.BUTTON);
    }

    /**
     * Determine if a material is a pressure plate.
     *
     * @param material The {@link org.bukkit.Material} to check.
     * @see MaterialProperty#PRESSURE_PLATE
     */
    public static boolean isPressurePlate(Material material) {
        return hasProperty(material, MaterialProperty.PRESSURE_PLATE);
    }

    /**
     * Determine if a material is wearable by a player.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#WEARABLE
     */
    public static boolean isWearable(Material material) {
        return hasProperty(material, MaterialProperty.WEARABLE);
    }

    /**
     * Determine if a material is player armor.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#ARMOR
     */
    public static boolean isArmor(Material material) {
        return hasProperty(material, MaterialProperty.ARMOR);
    }

    /**
     * Determine if a material is a helmet.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#HELMET
     */
    public static boolean isHelmet(Material material) {
        return hasProperty(material, MaterialProperty.HELMET);
    }

    /**
     * Determine if a material is a chestplate.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#CHESTPLATE
     */
    public static boolean isChestplate(Material material) {
        return hasProperty(material, MaterialProperty.CHESTPLATE);
    }

    /**
     * Determine if a material is leggings.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#LEGGINGS
     */
    public static boolean isLeggings(Material material) {
        return hasProperty(material, MaterialProperty.LEGGINGS);
    }

    /**
     * Determine if a material is boots.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#BOOTS
     */
    public static boolean isBoots(Material material) {
        return hasProperty(material, MaterialProperty.BOOTS);
    }

    /**
     * Determine if a material is a weapon.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#WEAPON
     */
    public static boolean isWeapon(Material material) {
        return hasProperty(material, MaterialProperty.WEAPON);
    }

    /**
     * Determine if a material is a tool.
     *
     * @param material The {@link org.bukkit.Material} to check.
     * @see MaterialProperty#TOOL
     */
    public static boolean isTool(Material material) {
        return hasProperty(material, MaterialProperty.TOOL);
    }

    /**
     * Determine if a material is a mining tool.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#MINING_TOOL
     */
    public static boolean isMiningTool(Material material) {
        return hasProperty(material, MaterialProperty.MINING_TOOL);
    }

    /**
     * Determine if a material is a shovel.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#SHOVEL
     */
    public static boolean isShovel(Material material) {
        return hasProperty(material, MaterialProperty.SHOVEL);
    }

    /**
     * Determine if a material is a hoe.
     *
     * @param material The {@link org.bukkit.Material} to check.
     * @see MaterialProperty#HOE
     */
    public static boolean isHoe(Material material) {
        return hasProperty(material, MaterialProperty.HOE);
    }

    /**
     * Determine if a material is an axe.
     *
     * @param material The {@link org.bukkit.Material} to check.
     * @see MaterialProperty#AXE
     */
    public static boolean isAxe(Material material) {
        return hasProperty(material, MaterialProperty.AXE);
    }

    /**
     * Determine if a material is a pickaxe.
     *
     * @param material The {@link org.bukkit.Material} to check.
     * @see MaterialProperty#PICKAXE
     */
    public static boolean isPickaxe(Material material) {
        return hasProperty(material, MaterialProperty.PICKAXE);
    }

    /**
     * Determine if a material is based on wood.
     *
     * @param material The {@link org.bukkit.Material} to check.
     * @see MaterialProperty#WOOD_BASED
     */
    public static boolean isWoodBased(Material material) {
        return hasProperty(material, MaterialProperty.WOOD_BASED);
    }

    /**
     * Determine if a material is based on stone.
     *
     * @param material The {@link org.bukkit.Material} to check.
     * @see MaterialProperty#STONE_BASED
     */
    public static boolean isStoneBased(Material material) {
        return hasProperty(material, MaterialProperty.STONE_BASED);
    }

    /**
     * Determine if a material is based on leather.
     *
     * @param material The {@link org.bukkit.Material} to check.
     * @see MaterialProperty#LEATHER_BASED
     */
    public static boolean isLeatherBased(Material material) {
        return hasProperty(material, MaterialProperty.LEATHER_BASED);
    }

    /**
     * Determine if a material is based on iron.
     *
     * @param material The {@link org.bukkit.Material} to check.
     * @see MaterialProperty#IRON_BASED
     */
    public static boolean isIronBased(Material material) {
        return hasProperty(material, MaterialProperty.IRON_BASED);
    }

    /**
     * Determine if a material is based on gold.
     *
     * @param material The {@link org.bukkit.Material} to check.
     * @see MaterialProperty#GOLD_BASED
     */
    public static boolean isGoldBased(Material material) {
        return hasProperty(material, MaterialProperty.GOLD_BASED);
    }

    /**
     * Determine if a material is based on diamond.
     *
     * @param material The {@link org.bukkit.Material} to check.
     * @see MaterialProperty#DIAMOND_BASED
     */
    public static boolean isDiamondBased(Material material) {
        return hasProperty(material, MaterialProperty.DIAMOND_BASED);
    }

    /**
     * Determine if a material is based on quartz.
     *
     * @param material The {@link org.bukkit.Material} to check.
     * @see MaterialProperty#QUARTZ_BASED
     */
    public static boolean isQuartzBased(Material material) {
        return hasProperty(material, MaterialProperty.QUARTZ_BASED);
    }

    /**
     * Determine if a material can be placed by player.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#PLACEABLE
     */
    public static boolean isPlaceable(Material material) {
        return hasProperty(material, MaterialProperty.PLACEABLE);
    }

    /**
     * Determine if a material can be crafted or created by cooking
     * by a player.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#CRAFTABLE
     */
    public static boolean isCraftable(Material material) {
        return hasProperty(material, MaterialProperty.CRAFTABLE);
    }

    /**
     * Determine if a material is a block.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#BLOCK
     */
    public static boolean isBlock(Material material) {
        return hasProperty(material, MaterialProperty.BLOCK);
    }

    /**
     * Determine if a material is a multi-block.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#MULTI_BLOCK
     */
    public static boolean isMultiBlock(Material material) {
        return hasProperty(material, MaterialProperty.MULTI_BLOCK);
    }

    /**
     * Determine if a material is stairs.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#STAIRS
     */
    public static boolean isStairs(Material material) {
        return hasProperty(material, MaterialProperty.STAIRS);
    }

    /**
     * Determine if a material is an ore block.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#ORE
     */
    public static boolean isOre(Material material) {
        return hasProperty(material, MaterialProperty.ORE);
    }

    /**
     * Determine if a material is repairable/damageable.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#REPAIRABLE
     */
    public static boolean isRepairable(Material material) {
        return hasProperty(material, MaterialProperty.REPAIRABLE);
    }

    /**
     * Determine if a material can be thrown by a player.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#THROWABLE
     */
    public static boolean isThrowable(Material material) {
        return hasProperty(material, MaterialProperty.THROWABLE);
    }

    /**
     * Determine if a material is consumable by a player.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#FOOD
     */
    public static boolean isFood(Material material) {
        return hasProperty(material, MaterialProperty.FOOD);
    }

    /**
     * Determine if a material is a block that is affected
     * by gravity.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#GRAVITY
     */
    public static boolean hasGravity(Material material) {
        return hasProperty(material, MaterialProperty.GRAVITY);
    }

    /**
     * Determine if a material has a GUI interface.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#GUI
     */
    public static boolean hasGUI(Material material) {
        return hasProperty(material, MaterialProperty.GUI);
    }

    /**
     * Determine if a material has an inventory used to store items
     * for players.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#INVENTORY
     */
    public static boolean hasInventory(Material material) {
        return hasProperty(material, MaterialProperty.INVENTORY);
    }

    /**
     * Determine if a materials byte meta data is used for color.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#MULTI_COLOR_DATA
     */
    public static boolean hasColorData(Material material) {
        return hasProperty(material, MaterialProperty.MULTI_COLOR_DATA);
    }

    /**
     * Determine if a materials byte meta data is used to determine
     * the direction it faces.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#DIRECTIONAL_DATA
     */
    public static boolean hasDirectionalData(Material material) {
        return hasProperty(material, MaterialProperty.DIRECTIONAL_DATA);
    }

    /**
     * Determine if a materials byte meta data is used to specify
     * a sub material.
     *
     * @param material  The {@link org.bukkit.Material} to check.
     *
     * @see  MaterialProperty#SUB_MATERIAL_DATA
     */
    public static boolean hasSubMaterialData(Material material) {
        return hasProperty(material, MaterialProperty.SUB_MATERIAL_DATA);
    }

    private static void add(Material material, MaterialProperty... properties) {
        _properties.putAll(material, Arrays.asList(properties));
        for (MaterialProperty property : properties) {
            _materialsByProperty.put(property, material);
        }
    }

    static {
        add(Material.ACACIA_DOOR,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.OPENABLE_BOUNDARY,
                MaterialProperty.DOOR,
                MaterialProperty.MULTI_BLOCK,
                MaterialProperty.REDSTONE);

        add(Material.ACACIA_DOOR_ITEM,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.OPENABLE_BOUNDARY,
                MaterialProperty.DOOR,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.REDSTONE);

        add(Material.ACACIA_FENCE,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE);

        add(Material.ACACIA_FENCE_GATE,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.OPENABLE_BOUNDARY,
                MaterialProperty.FENCE_GATE,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.REDSTONE);

        add(Material.ACACIA_STAIRS,
                MaterialProperty.STAIRS,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.DIRECTIONAL_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.ACTIVATOR_RAIL,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.PLACEABLE,
                MaterialProperty.REDSTONE,
                MaterialProperty.CRAFTABLE);

        add(Material.AIR,
                MaterialProperty.TRANSPARENT);

        add(Material.ANVIL,
                MaterialProperty.PLACEABLE,
                MaterialProperty.GUI,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE,
                MaterialProperty.REPAIRABLE);

        add(Material.APPLE,
                MaterialProperty.FOOD);

        add(Material.ARMOR_STAND,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.PLACEABLE); // item

        add(Material.ARROW,
                MaterialProperty.THROWABLE,
                MaterialProperty.CRAFTABLE);

        add(Material.BAKED_POTATO,
                MaterialProperty.CRAFTABLE);

        add(Material.BANNER,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.PLACEABLE); // item

        add(Material.BEACON,
                MaterialProperty.PLACEABLE,
                MaterialProperty.GUI,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.BED,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE);

        add(Material.BED_BLOCK,
                MaterialProperty.SURFACE);

        add(Material.BEDROCK,
                MaterialProperty.PLACEABLE,
                MaterialProperty.SURFACE);

        add(Material.BIRCH_DOOR,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.OPENABLE_BOUNDARY,
                MaterialProperty.DOOR,
                MaterialProperty.MULTI_BLOCK,
                MaterialProperty.REDSTONE);

        add(Material.BIRCH_DOOR_ITEM,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.OPENABLE_BOUNDARY,
                MaterialProperty.DOOR,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.REDSTONE);

        add(Material.BIRCH_FENCE,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE);

        add(Material.BIRCH_FENCE_GATE,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.OPENABLE_BOUNDARY,
                MaterialProperty.FENCE_GATE,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.REDSTONE);

        add(Material.BIRCH_WOOD_STAIRS,
                MaterialProperty.STAIRS,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.DIRECTIONAL_DATA,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.BLAZE_POWDER,
                MaterialProperty.CRAFTABLE);

        add(Material.BLAZE_POWDER);

        add(Material.BOAT,
                MaterialProperty.CRAFTABLE);

        add(Material.BONE);

        add(Material.BOOK,
                MaterialProperty.CRAFTABLE);

        add(Material.BOOK_AND_QUILL,
                MaterialProperty.CRAFTABLE);

        add(Material.BOOKSHELF,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.BOW,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEAPON,
                MaterialProperty.CRAFTABLE);

        add(Material.BOWL,
                MaterialProperty.CRAFTABLE);

        add(Material.BREAD,
                MaterialProperty.CRAFTABLE);

        add(Material.BREWING_STAND,
                MaterialProperty.GUI);

        add(Material.BREWING_STAND_ITEM,
                MaterialProperty.PLACEABLE);

        add(Material.BRICK,
                MaterialProperty.CRAFTABLE);

        add(Material.BRICK_STAIRS,
                MaterialProperty.STAIRS,
                MaterialProperty.DIRECTIONAL_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.BROWN_MUSHROOM,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.PLACEABLE);

        add(Material.BUCKET,
                MaterialProperty.CRAFTABLE);

        add(Material.BURNING_FURNACE,
                MaterialProperty.DIRECTIONAL_DATA,
                MaterialProperty.SURFACE);

        add(Material.CACTUS,
                MaterialProperty.PLACEABLE);

        add(Material.CAKE,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE);

        add(Material.CAKE_BLOCK,
                MaterialProperty.BLOCK);

        add(Material.CARPET,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.MULTI_COLOR_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE);

        add(Material.CARROT,
                MaterialProperty.BLOCK);

        add(Material.CARROT_ITEM,
                MaterialProperty.PLACEABLE);

        add(Material.CARROT_STICK,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.TOOL,
                MaterialProperty.CRAFTABLE);

        add(Material.CAULDRON,
                MaterialProperty.BLOCK);

        add(Material.CAULDRON_ITEM,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE);

        add(Material.CHAINMAIL_BOOTS,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEARABLE,
                MaterialProperty.ARMOR,
                MaterialProperty.BOOTS);

        add(Material.CHAINMAIL_CHESTPLATE,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEARABLE,
                MaterialProperty.ARMOR,
                MaterialProperty.CHESTPLATE);

        add(Material.CHAINMAIL_HELMET,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEARABLE,
                MaterialProperty.ARMOR,
                MaterialProperty.HELMET);

        add(Material.CHAINMAIL_LEGGINGS,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEARABLE,
                MaterialProperty.ARMOR,
                MaterialProperty.LEGGINGS);

        add(Material.CHEST,
                MaterialProperty.DIRECTIONAL_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.INVENTORY,
                MaterialProperty.GUI,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.CLAY,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK);

        add(Material.CLAY_BALL,
                MaterialProperty.CRAFTABLE);

        add(Material.CLAY_BRICK,
                MaterialProperty.CRAFTABLE);

        add(Material.COAL,
                MaterialProperty.SUB_MATERIAL_DATA);

        add(Material.COAL_BLOCK,
                MaterialProperty.BLOCK,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.COAL_ORE,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.ORE,
                MaterialProperty.SURFACE);

        add(Material.COBBLE_WALL,
                MaterialProperty.STONE_BASED,
                MaterialProperty.SUB_MATERIAL_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE);

        add(Material.COBBLESTONE,
                MaterialProperty.STONE_BASED,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.SURFACE);

        add(Material.COBBLESTONE_STAIRS,
                MaterialProperty.STAIRS,
                MaterialProperty.STONE_BASED,
                MaterialProperty.DIRECTIONAL_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.COCOA);

        add(Material.COMMAND,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.GUI,
                MaterialProperty.SURFACE);

        add(Material.COMMAND_MINECART);

        add(Material.COMPASS,
                MaterialProperty.CRAFTABLE);

        add(Material.COOKED_BEEF,
                MaterialProperty.FOOD,
                MaterialProperty.CRAFTABLE);

        add(Material.COOKED_CHICKEN,
                MaterialProperty.FOOD,
                MaterialProperty.CRAFTABLE);

        add(Material.COOKED_FISH,
                MaterialProperty.FOOD,
                MaterialProperty.CRAFTABLE);

        add(Material.COOKED_MUTTON,
                MaterialProperty.FOOD,
                MaterialProperty.CRAFTABLE);

        add(Material.COOKED_RABBIT,
                MaterialProperty.FOOD,
                MaterialProperty.CRAFTABLE);

        add(Material.COOKIE,
                MaterialProperty.FOOD,
                MaterialProperty.CRAFTABLE);

        add(Material.CROPS,
                MaterialProperty.BLOCK,
                MaterialProperty.SURFACE);

        add(Material.DARK_OAK_DOOR,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.OPENABLE_BOUNDARY,
                MaterialProperty.DOOR,
                MaterialProperty.MULTI_BLOCK,
                MaterialProperty.REDSTONE);

        add(Material.DARK_OAK_DOOR_ITEM,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.OPENABLE_BOUNDARY,
                MaterialProperty.DOOR,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.REDSTONE);

        add(Material.DARK_OAK_FENCE,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE);

        add(Material.DARK_OAK_FENCE_GATE,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.OPENABLE_BOUNDARY,
                MaterialProperty.FENCE_GATE,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.REDSTONE);

        add(Material.DARK_OAK_STAIRS,
                MaterialProperty.STAIRS,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.DIRECTIONAL_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.DAYLIGHT_DETECTOR,
                MaterialProperty.REDSTONE,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE);

        add(Material.DAYLIGHT_DETECTOR_INVERTED,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.REDSTONE);

        add(Material.DEAD_BUSH,
                MaterialProperty.BLOCK,
                MaterialProperty.TRANSPARENT);

        add(Material.DETECTOR_RAIL,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.REDSTONE,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE);

        add(Material.DIAMOND,
                MaterialProperty.DIAMOND_BASED);

        add(Material.DIAMOND_AXE,
                MaterialProperty.DIAMOND_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.TOOL,
                MaterialProperty.AXE,
                MaterialProperty.CRAFTABLE);

        add(Material.DIAMOND_BARDING,
                MaterialProperty.DIAMOND_BASED,
                MaterialProperty.HORSE_ARMOR);

        add(Material.DIAMOND_BLOCK,
                MaterialProperty.DIAMOND_BASED,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE);

        add(Material.DIAMOND_BOOTS,
                MaterialProperty.DIAMOND_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEARABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.ARMOR,
                MaterialProperty.BOOTS);

        add(Material.DIAMOND_CHESTPLATE,
                MaterialProperty.DIAMOND_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEARABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.ARMOR,
                MaterialProperty.CHESTPLATE);

        add(Material.DIAMOND_HELMET,
                MaterialProperty.DIAMOND_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEARABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.ARMOR,
                MaterialProperty.HELMET);

        add(Material.DIAMOND_HOE,
                MaterialProperty.DIAMOND_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.TOOL,
                MaterialProperty.HOE,
                MaterialProperty.CRAFTABLE);

        add(Material.DIAMOND_LEGGINGS,
                MaterialProperty.DIAMOND_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEARABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.ARMOR,
                MaterialProperty.LEGGINGS);

        add(Material.DIAMOND_ORE,
                MaterialProperty.DIAMOND_BASED,
                MaterialProperty.PLACEABLE,
                MaterialProperty.ORE);

        add(Material.DIAMOND_PICKAXE,
                MaterialProperty.DIAMOND_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.TOOL,
                MaterialProperty.MINING_TOOL,
                MaterialProperty.PICKAXE,
                MaterialProperty.CRAFTABLE);

        add(Material.DIAMOND_SPADE,
                MaterialProperty.DIAMOND_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.TOOL,
                MaterialProperty.MINING_TOOL,
                MaterialProperty.SHOVEL,
                MaterialProperty.CRAFTABLE);

        add(Material.DIAMOND_SWORD,
                MaterialProperty.DIAMOND_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEAPON,
                MaterialProperty.CRAFTABLE);

        add(Material.DIODE,
                MaterialProperty.REDSTONE,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE);

        add(Material.DIODE_BLOCK_OFF,
                MaterialProperty.DIRECTIONAL_DATA,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.REDSTONE);

        add(Material.DIODE_BLOCK_ON,
                MaterialProperty.DIRECTIONAL_DATA,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.REDSTONE);

        add(Material.DIRT,
                MaterialProperty.SUB_MATERIAL_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.SURFACE);

        add(Material.DISPENSER,
                MaterialProperty.DIRECTIONAL_DATA,
                MaterialProperty.INVENTORY,
                MaterialProperty.GUI,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.REDSTONE,
                MaterialProperty.SURFACE);

        add(Material.DOUBLE_PLANT,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.MULTI_BLOCK,
                MaterialProperty.SUB_MATERIAL_DATA);

        add(Material.DOUBLE_STEP,
                MaterialProperty.SUB_MATERIAL_DATA,
                MaterialProperty.SURFACE);

        add(Material.DRAGON_EGG);

        add(Material.DROPPER,
                MaterialProperty.DIRECTIONAL_DATA,
                MaterialProperty.INVENTORY,
                MaterialProperty.GUI,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.REDSTONE,
                MaterialProperty.SURFACE);

        add(Material.EGG,
                MaterialProperty.THROWABLE);

        add(Material.EMERALD);

        add(Material.EMERALD_BLOCK,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.EMERALD_ORE,
                MaterialProperty.PLACEABLE,
                MaterialProperty.ORE,
                MaterialProperty.SURFACE);

        add(Material.EMPTY_MAP);

        add(Material.ENCHANTED_BOOK);

        add(Material.ENCHANTMENT_TABLE,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.GUI);

        add(Material.ENDER_CHEST,
                MaterialProperty.DIRECTIONAL_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.INVENTORY,
                MaterialProperty.GUI,
                MaterialProperty.SURFACE);

        add(Material.ENDER_PEARL,
                MaterialProperty.THROWABLE);

        add(Material.ENDER_PORTAL,
                MaterialProperty.BLOCK,
                MaterialProperty.TRANSPARENT);

        add(Material.ENDER_PORTAL_FRAME,
                MaterialProperty.SURFACE);

        add(Material.ENDER_STONE);

        add(Material.EXP_BOTTLE,
                MaterialProperty.THROWABLE);

        add(Material.EXPLOSIVE_MINECART);

        add(Material.EYE_OF_ENDER);

        add(Material.FEATHER);

        add(Material.FENCE,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE);

        add(Material.FENCE_GATE,
                MaterialProperty.OPENABLE_BOUNDARY,
                MaterialProperty.FENCE_GATE,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE);

        add(Material.FERMENTED_SPIDER_EYE);

        add(Material.FIRE);

        add(Material.FIREBALL,
                MaterialProperty.THROWABLE);

        add(Material.FIREWORK,
                MaterialProperty.CRAFTABLE);

        add(Material.FIREWORK_CHARGE,
                MaterialProperty.CRAFTABLE);

        add(Material.FISHING_ROD,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.TOOL,
                MaterialProperty.CRAFTABLE);

        add(Material.FLINT);

        add(Material.FLINT_AND_STEEL,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.TOOL,
                MaterialProperty.CRAFTABLE);

        add(Material.FLOWER_POT,
                MaterialProperty.BLOCK);

        add(Material.FLOWER_POT_ITEM,
                MaterialProperty.PLACEABLE);

        add(Material.FURNACE,
                MaterialProperty.DIRECTIONAL_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.GUI,
                MaterialProperty.SURFACE);

        add(Material.GHAST_TEAR);

        add(Material.GLASS,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE);

        add(Material.GLASS_BOTTLE,
                MaterialProperty.CRAFTABLE);

        add(Material.GLOWING_REDSTONE_ORE,
                MaterialProperty.BLOCK,
                MaterialProperty.SURFACE);

        add(Material.GLOWSTONE,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK);

        add(Material.GLOWSTONE_DUST);

        add(Material.GOLD_AXE,
                MaterialProperty.GOLD_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.TOOL,
                MaterialProperty.AXE,
                MaterialProperty.CRAFTABLE);

        add(Material.GOLD_BARDING,
                MaterialProperty.GOLD_BASED,
                MaterialProperty.HORSE_ARMOR);

        add(Material.GOLD_BLOCK,
                MaterialProperty.GOLD_BASED,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE);

        add(Material.GOLD_BOOTS,
                MaterialProperty.GOLD_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEARABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.ARMOR,
                MaterialProperty.BOOTS);

        add(Material.GOLD_CHESTPLATE,
                MaterialProperty.GOLD_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEARABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.ARMOR,
                MaterialProperty.CHESTPLATE);

        add(Material.GOLD_HELMET,
                MaterialProperty.GOLD_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEARABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.ARMOR,
                MaterialProperty.HELMET);

        add(Material.GOLD_HOE,
                MaterialProperty.GOLD_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.TOOL,
                MaterialProperty.HOE,
                MaterialProperty.CRAFTABLE);

        add(Material.GOLD_INGOT,
                MaterialProperty.GOLD_BASED,
                MaterialProperty.CRAFTABLE);

        add(Material.GOLD_LEGGINGS,
                MaterialProperty.GOLD_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEARABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.ARMOR,
                MaterialProperty.LEGGINGS);

        add(Material.GOLD_NUGGET,
                MaterialProperty.GOLD_BASED,
                MaterialProperty.CRAFTABLE);

        add(Material.GOLD_ORE,
                MaterialProperty.GOLD_BASED,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.ORE);

        add(Material.GOLD_PICKAXE,
                MaterialProperty.GOLD_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.TOOL,
                MaterialProperty.MINING_TOOL,
                MaterialProperty.PICKAXE,
                MaterialProperty.CRAFTABLE);

        add(Material.GOLD_PLATE,
                MaterialProperty.GOLD_BASED,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.REDSTONE,
                MaterialProperty.REDSTONE_SWITCH,
                MaterialProperty.PRESSURE_PLATE,
                MaterialProperty.CRAFTABLE);

        add(Material.GOLD_RECORD);

        add(Material.GOLD_SPADE,
                MaterialProperty.GOLD_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.TOOL,
                MaterialProperty.MINING_TOOL,
                MaterialProperty.SHOVEL,
                MaterialProperty.CRAFTABLE);

        add(Material.GOLD_SWORD,
                MaterialProperty.GOLD_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEAPON,
                MaterialProperty.CRAFTABLE);

        add(Material.GOLDEN_APPLE,
                MaterialProperty.GOLD_BASED,
                MaterialProperty.FOOD,
                MaterialProperty.SUB_MATERIAL_DATA,
                MaterialProperty.CRAFTABLE);

        add(Material.GOLDEN_CARROT,
                MaterialProperty.GOLD_BASED,
                MaterialProperty.FOOD,
                MaterialProperty.CRAFTABLE);

        add(Material.GRASS,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.SURFACE);

        add(Material.GRAVEL,
                MaterialProperty.BLOCK,
                MaterialProperty.GRAVITY,
                MaterialProperty.PLACEABLE,
                MaterialProperty.SURFACE);

        add(Material.GREEN_RECORD);

        add(Material.GRILLED_PORK,
                MaterialProperty.FOOD,
                MaterialProperty.CRAFTABLE);

        add(Material.HARD_CLAY,
                MaterialProperty.BLOCK,
                MaterialProperty.PLACEABLE);

        add(Material.HAY_BLOCK,
                MaterialProperty.BLOCK,
                MaterialProperty.PLACEABLE,
                MaterialProperty.SURFACE);

        add(Material.HOPPER,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.REDSTONE,
                MaterialProperty.INVENTORY,
                MaterialProperty.GUI,
                MaterialProperty.CRAFTABLE);

        add(Material.HOPPER_MINECART);

        add(Material.HUGE_MUSHROOM_1,
                MaterialProperty.BLOCK,
                MaterialProperty.SURFACE);

        add(Material.HUGE_MUSHROOM_2,
                MaterialProperty.BLOCK,
                MaterialProperty.SURFACE);

        add(Material.ICE,
                MaterialProperty.PLACEABLE,
                MaterialProperty.SURFACE);

        add(Material.INK_SACK,
                MaterialProperty.MULTI_COLOR_DATA,
                MaterialProperty.SUB_MATERIAL_DATA);

        add(Material.IRON_AXE,
                MaterialProperty.IRON_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.TOOL,
                MaterialProperty.AXE,
                MaterialProperty.CRAFTABLE);

        add(Material.IRON_BARDING,
                MaterialProperty.IRON_BASED,
                MaterialProperty.HORSE_ARMOR);

        add(Material.IRON_BLOCK,
                MaterialProperty.IRON_BASED,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE);

        add(Material.IRON_BOOTS,
                MaterialProperty.IRON_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEARABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.ARMOR,
                MaterialProperty.BOOTS);

        add(Material.IRON_CHESTPLATE,
                MaterialProperty.IRON_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEARABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.ARMOR,
                MaterialProperty.CHESTPLATE);

        add(Material.IRON_DOOR,
                MaterialProperty.IRON_BASED,
                MaterialProperty.OPENABLE_BOUNDARY,
                MaterialProperty.DOOR,
                MaterialProperty.MULTI_BLOCK,
                MaterialProperty.PLACEABLE,
                MaterialProperty.REDSTONE,
                MaterialProperty.CRAFTABLE);

        add(Material.IRON_DOOR_BLOCK,
                MaterialProperty.IRON_BASED,
                MaterialProperty.OPENABLE_BOUNDARY,
                MaterialProperty.DOOR,
                MaterialProperty.BLOCK,
                MaterialProperty.REDSTONE);

        add(Material.IRON_FENCE,
                MaterialProperty.IRON_BASED,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE);

        add(Material.IRON_HELMET,
                MaterialProperty.IRON_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEARABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.ARMOR,
                MaterialProperty.HELMET);

        add(Material.IRON_HOE,
                MaterialProperty.IRON_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.TOOL,
                MaterialProperty.HOE,
                MaterialProperty.CRAFTABLE);

        add(Material.IRON_INGOT,
                MaterialProperty.IRON_BASED,
                MaterialProperty.CRAFTABLE);

        add(Material.IRON_LEGGINGS,
                MaterialProperty.IRON_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEARABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.ARMOR,
                MaterialProperty.LEGGINGS);

        add(Material.IRON_ORE,
                MaterialProperty.IRON_BASED,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.ORE);

        add(Material.IRON_PICKAXE,
                MaterialProperty.IRON_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.TOOL,
                MaterialProperty.MINING_TOOL,
                MaterialProperty.PICKAXE,
                MaterialProperty.CRAFTABLE);

        add(Material.IRON_PLATE,
                MaterialProperty.IRON_BASED,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.REDSTONE,
                MaterialProperty.REDSTONE_SWITCH,
                MaterialProperty.PRESSURE_PLATE,
                MaterialProperty.CRAFTABLE);

        add(Material.IRON_SPADE,
                MaterialProperty.IRON_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.TOOL,
                MaterialProperty.MINING_TOOL,
                MaterialProperty.SHOVEL,
                MaterialProperty.CRAFTABLE);

        add(Material.IRON_SWORD,
                MaterialProperty.IRON_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEAPON,
                MaterialProperty.CRAFTABLE);

        add(Material.IRON_TRAPDOOR,
                MaterialProperty.IRON_BASED,
                MaterialProperty.OPENABLE_BOUNDARY,
                MaterialProperty.TRAPDOOR,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.REDSTONE);

        add(Material.ITEM_FRAME,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE);

        add(Material.JACK_O_LANTERN,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.WEARABLE,
                MaterialProperty.SURFACE);

        add(Material.JUKEBOX,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.JUNGLE_DOOR,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.OPENABLE_BOUNDARY,
                MaterialProperty.DOOR,
                MaterialProperty.MULTI_BLOCK,
                MaterialProperty.REDSTONE);

        add(Material.JUNGLE_DOOR_ITEM,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.OPENABLE_BOUNDARY,
                MaterialProperty.DOOR,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.REDSTONE);

        add(Material.JUNGLE_FENCE,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE);

        add(Material.JUNGLE_FENCE_GATE,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.OPENABLE_BOUNDARY,
                MaterialProperty.FENCE_GATE,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.REDSTONE);

        add(Material.JUNGLE_WOOD_STAIRS,
                MaterialProperty.STAIRS,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.DIRECTIONAL_DATA,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.LADDER,
                MaterialProperty.DIRECTIONAL_DATA,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE);

        add(Material.LAPIS_BLOCK,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.LAPIS_ORE,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.ORE,
                MaterialProperty.SURFACE);

        add(Material.LAVA,
                MaterialProperty.BLOCK,
                MaterialProperty.PLACEABLE);

        add(Material.LAVA_BUCKET);

        add(Material.LEASH);

        add(Material.LEATHER,
                MaterialProperty.LEATHER_BASED);

        add(Material.LEATHER_BOOTS,
                MaterialProperty.LEATHER_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEARABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.ARMOR,
                MaterialProperty.BOOTS);

        add(Material.LEATHER_CHESTPLATE,
                MaterialProperty.LEATHER_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEARABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.ARMOR,
                MaterialProperty.CHESTPLATE);

        add(Material.LEATHER_HELMET,
                MaterialProperty.LEATHER_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEARABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.ARMOR,
                MaterialProperty.HELMET);

        add(Material.LEATHER_LEGGINGS,
                MaterialProperty.LEATHER_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEARABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.ARMOR,
                MaterialProperty.LEGGINGS);

        add(Material.LEAVES,
                MaterialProperty.SUB_MATERIAL_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.SURFACE);

        add(Material.LEAVES_2,
                MaterialProperty.SUB_MATERIAL_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.SURFACE);

        add(Material.LEVER,
                MaterialProperty.DIRECTIONAL_DATA,
                MaterialProperty.REDSTONE_SWITCH,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.REDSTONE,
                MaterialProperty.CRAFTABLE);

        add(Material.LOG,
                MaterialProperty.SUB_MATERIAL_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.SURFACE);

        add(Material.LOG_2,
                MaterialProperty.SUB_MATERIAL_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.SURFACE);

        add(Material.LONG_GRASS,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.SUB_MATERIAL_DATA);

        add(Material.MAGMA_CREAM);

        add(Material.MAP);

        add(Material.MELON);

        add(Material.MELON_BLOCK,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.SURFACE);

        add(Material.MELON_SEEDS);

        add(Material.MELON_STEM);

        add(Material.MILK_BUCKET);

        add(Material.MINECART,
                MaterialProperty.CRAFTABLE);

        add(Material.MOB_SPAWNER);

        add(Material.MONSTER_EGG);

        add(Material.MONSTER_EGGS);

        add(Material.MOSSY_COBBLESTONE,
                MaterialProperty.STONE_BASED,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.SURFACE);

        add(Material.MUSHROOM_SOUP,
                MaterialProperty.FOOD,
                MaterialProperty.CRAFTABLE);

        add(Material.MUTTON,
                MaterialProperty.FOOD);

        add(Material.MYCEL,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.SURFACE);

        add(Material.NAME_TAG);

        add(Material.NETHER_BRICK,
                MaterialProperty.BLOCK,
                MaterialProperty.SURFACE);

        add(Material.NETHER_BRICK_ITEM,
                MaterialProperty.PLACEABLE);

        add(Material.NETHER_BRICK_STAIRS,
                MaterialProperty.STAIRS,
                MaterialProperty.DIRECTIONAL_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.NETHER_FENCE,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE);

        add(Material.NETHER_STALK); // Nether wart item

        add(Material.NETHER_STAR);

        add(Material.NETHER_WARTS,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK);

        add(Material.NETHERRACK,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.SURFACE);

        add(Material.NOTE_BLOCK,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.OBSIDIAN,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.SURFACE);

        add(Material.PACKED_ICE,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.SURFACE);

        add(Material.PAINTING,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE);

        add(Material.PAPER,
                MaterialProperty.CRAFTABLE);

        add(Material.PISTON_BASE,
                MaterialProperty.BLOCK,
                MaterialProperty.SURFACE);

        add(Material.PISTON_EXTENSION);

        add(Material.PISTON_MOVING_PIECE);

        add(Material.PISTON_STICKY_BASE,
                MaterialProperty.SURFACE);

        add(Material.POISONOUS_POTATO,
                MaterialProperty.FOOD,
                MaterialProperty.CRAFTABLE);

        add(Material.PORK,
                MaterialProperty.FOOD);

        add(Material.PORTAL,
                MaterialProperty.BLOCK,
                MaterialProperty.TRANSPARENT);

        add(Material.POTATO,
                MaterialProperty.BLOCK);

        add(Material.POTATO_ITEM,
                MaterialProperty.FOOD);

        add(Material.POTION);

        add(Material.POWERED_MINECART);

        add(Material.POWERED_RAIL,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.REDSTONE,
                MaterialProperty.CRAFTABLE);

        add(Material.PRISMARINE);

        add(Material.PRISMARINE_CRYSTALS);

        add(Material.PRISMARINE_SHARD);

        add(Material.PUMPKIN,
                MaterialProperty.PLACEABLE);

        add(Material.PUMPKIN_PIE,
                MaterialProperty.CRAFTABLE);

        add(Material.PUMPKIN_SEEDS);

        add(Material.PUMPKIN_STEM);

        add(Material.QUARTZ,
                MaterialProperty.QUARTZ_BASED);

        add(Material.QUARTZ_BLOCK,
                MaterialProperty.QUARTZ_BASED,
                MaterialProperty.SUB_MATERIAL_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.QUARTZ_ORE,
                MaterialProperty.QUARTZ_BASED,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.ORE,
                MaterialProperty.SURFACE);

        add(Material.QUARTZ_STAIRS,
                MaterialProperty.STAIRS,
                MaterialProperty.QUARTZ_BASED,
                MaterialProperty.DIRECTIONAL_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.RABBIT);

        add(Material.RABBIT_FOOT);

        add(Material.RABBIT_HIDE);

        add(Material.RABBIT_STEW,
                MaterialProperty.FOOD,
                MaterialProperty.CRAFTABLE);

        add(Material.RAILS,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE);

        add(Material.RAW_BEEF,
                MaterialProperty.FOOD);

        add(Material.RAW_CHICKEN,
                MaterialProperty.FOOD);

        add(Material.RAW_FISH,
                MaterialProperty.FOOD,
                MaterialProperty.SUB_MATERIAL_DATA);

        add(Material.RECORD_10);

        add(Material.RECORD_11);

        add(Material.RECORD_12);

        add(Material.RECORD_3);

        add(Material.RECORD_4);

        add(Material.RECORD_5);

        add(Material.RECORD_6);

        add(Material.RECORD_7);

        add(Material.RECORD_8);

        add(Material.RECORD_9);

        add(Material.RED_MUSHROOM,
                MaterialProperty.BLOCK,
                MaterialProperty.TRANSPARENT);

        add(Material.RED_ROSE,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.SUB_MATERIAL_DATA);

        add(Material.RED_SANDSTONE,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.RED_SANDSTONE_STAIRS,
                MaterialProperty.STAIRS,
                MaterialProperty.DIRECTIONAL_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.REDSTONE,
                MaterialProperty.PLACEABLE);

        add(Material.REDSTONE_BLOCK,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.REDSTONE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.REDSTONE_COMPARATOR,
                MaterialProperty.PLACEABLE,
                MaterialProperty.REDSTONE,
                MaterialProperty.CRAFTABLE);

        add(Material.REDSTONE_COMPARATOR_OFF,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.BLOCK,
                MaterialProperty.REDSTONE);

        add(Material.REDSTONE_COMPARATOR_ON,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.BLOCK,
                MaterialProperty.REDSTONE);

        add(Material.REDSTONE_LAMP_OFF,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.REDSTONE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.REDSTONE_LAMP_ON,
                MaterialProperty.REDSTONE,
                MaterialProperty.BLOCK,
                MaterialProperty.SURFACE);

        add(Material.REDSTONE_ORE,
                MaterialProperty.BLOCK,
                MaterialProperty.PLACEABLE);

        add(Material.REDSTONE_TORCH_OFF,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.REDSTONE,
                MaterialProperty.CRAFTABLE);

        add(Material.REDSTONE_TORCH_ON,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.BLOCK,
                MaterialProperty.REDSTONE);

        add(Material.REDSTONE_WIRE,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.BLOCK,
                MaterialProperty.REDSTONE);

        add(Material.ROTTEN_FLESH,
                MaterialProperty.FOOD);

        add(Material.SADDLE);

        add(Material.SAND,
                MaterialProperty.SUB_MATERIAL_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.GRAVITY,
                MaterialProperty.SURFACE);

        add(Material.SANDSTONE,
                MaterialProperty.SUB_MATERIAL_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.SANDSTONE_STAIRS,
                MaterialProperty.STAIRS,
                MaterialProperty.DIRECTIONAL_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.SAPLING,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.SUB_MATERIAL_DATA,
                MaterialProperty.PLACEABLE);

        add(Material.SEA_LANTERN,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.SEEDS,
                MaterialProperty.SUB_MATERIAL_DATA,
                MaterialProperty.PLACEABLE);

        add(Material.SHEARS,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.TOOL,
                MaterialProperty.CRAFTABLE);

        add(Material.SIGN,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE);

        add(Material.SIGN_POST,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.BLOCK);

        add(Material.SKULL,
                MaterialProperty.BLOCK);

        add(Material.SKULL_ITEM,
                MaterialProperty.PLACEABLE,
                MaterialProperty.WEARABLE);

        add(Material.SLIME_BALL);

        add(Material.SMOOTH_BRICK,
                MaterialProperty.SUB_MATERIAL_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.SMOOTH_STAIRS,
                MaterialProperty.STAIRS,
                MaterialProperty.DIRECTIONAL_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.SNOW,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.BLOCK,
                MaterialProperty.PLACEABLE);

        add(Material.SNOW_BALL,
                MaterialProperty.THROWABLE);

        add(Material.SNOW_BLOCK,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.SOIL,
                MaterialProperty.BLOCK,
                MaterialProperty.SURFACE);

        add(Material.SOUL_SAND,
                MaterialProperty.BLOCK,
                MaterialProperty.PLACEABLE);

        add(Material.SPECKLED_MELON); // Glistering Melon

        add(Material.SPIDER_EYE);

        add(Material.SPONGE,
                MaterialProperty.SUB_MATERIAL_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.SURFACE);

        add(Material.SPRUCE_DOOR,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.OPENABLE_BOUNDARY,
                MaterialProperty.DOOR,
                MaterialProperty.MULTI_BLOCK,
                MaterialProperty.REDSTONE);

        add(Material.SPRUCE_DOOR_ITEM,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.OPENABLE_BOUNDARY,
                MaterialProperty.DOOR,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.REDSTONE);

        add(Material.SPRUCE_FENCE,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE);

        add(Material.SPRUCE_FENCE_GATE,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.OPENABLE_BOUNDARY,
                MaterialProperty.FENCE_GATE,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.REDSTONE);

        add(Material.SPRUCE_WOOD_STAIRS,
                MaterialProperty.STAIRS,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.DIRECTIONAL_DATA,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.STAINED_CLAY,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.MULTI_COLOR_DATA,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.STAINED_GLASS,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.MULTI_COLOR_DATA,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.STAINED_GLASS_PANE,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.MULTI_COLOR_DATA,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.STANDING_BANNER,
                MaterialProperty.BLOCK);

        add(Material.STATIONARY_LAVA,
                MaterialProperty.BLOCK);

        add(Material.STATIONARY_WATER,
                MaterialProperty.BLOCK,
                MaterialProperty.TRANSPARENT);

        add(Material.STEP,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.SUB_MATERIAL_DATA,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.STICK);

        add(Material.STONE,
                MaterialProperty.STONE_BASED,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.SUB_MATERIAL_DATA,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.STONE_AXE,
                MaterialProperty.STONE_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.TOOL,
                MaterialProperty.AXE,
                MaterialProperty.CRAFTABLE);

        add(Material.STONE_BUTTON,
                MaterialProperty.STONE_BASED,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.REDSTONE,
                MaterialProperty.REDSTONE_SWITCH,
                MaterialProperty.BUTTON,
                MaterialProperty.CRAFTABLE);

        add(Material.STONE_HOE,
                MaterialProperty.STONE_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.TOOL,
                MaterialProperty.HOE,
                MaterialProperty.CRAFTABLE);

        add(Material.STONE_PICKAXE,
                MaterialProperty.STONE_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.TOOL,
                MaterialProperty.MINING_TOOL,
                MaterialProperty.PICKAXE,
                MaterialProperty.CRAFTABLE);

        add(Material.STONE_PLATE,
                MaterialProperty.STONE_BASED,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.REDSTONE,
                MaterialProperty.REDSTONE_SWITCH,
                MaterialProperty.PRESSURE_PLATE,
                MaterialProperty.CRAFTABLE);

        add(Material.STONE_SLAB2,
                MaterialProperty.STONE_BASED,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.STONE_SPADE,
                MaterialProperty.STONE_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.TOOL,
                MaterialProperty.MINING_TOOL,
                MaterialProperty.SHOVEL,
                MaterialProperty.CRAFTABLE);

        add(Material.STONE_SWORD,
                MaterialProperty.STONE_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEAPON,
                MaterialProperty.CRAFTABLE);

        add(Material.STORAGE_MINECART);

        add(Material.STRING);

        add(Material.SUGAR);

        add(Material.SUGAR_CANE,
                MaterialProperty.PLACEABLE);

        add(Material.SUGAR_CANE_BLOCK,
                MaterialProperty.BLOCK);

        add(Material.SULPHUR); // gunpowder

        add(Material.THIN_GLASS,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.TNT,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE);

        add(Material.TORCH,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE);

        add(Material.TRAP_DOOR,
                MaterialProperty.OPENABLE_BOUNDARY,
                MaterialProperty.TRAPDOOR,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE);

        add(Material.TRAPPED_CHEST,
                MaterialProperty.DIRECTIONAL_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.REDSTONE,
                MaterialProperty.REDSTONE_SWITCH,
                MaterialProperty.INVENTORY,
                MaterialProperty.GUI,
                MaterialProperty.CRAFTABLE);

        add(Material.TRIPWIRE,
                MaterialProperty.BLOCK,
                MaterialProperty.TRANSPARENT);

        add(Material.TRIPWIRE_HOOK,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.REDSTONE,
                MaterialProperty.CRAFTABLE);

        add(Material.VINE,
                MaterialProperty.PLACEABLE);

        add(Material.WALL_BANNER,
                MaterialProperty.BLOCK);

        add(Material.WALL_SIGN,
                MaterialProperty.BLOCK,
                MaterialProperty.TRANSPARENT);

        add(Material.WATCH,
                MaterialProperty.CRAFTABLE);

        add(Material.WATER,
                MaterialProperty.BLOCK,
                MaterialProperty.TRANSPARENT);

        add(Material.WATER_BUCKET);

        add(Material.WATER_LILY,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.BLOCK,
                MaterialProperty.SURFACE,
                MaterialProperty.PLACEABLE);

        add(Material.WEB,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.BLOCK,
                MaterialProperty.PLACEABLE);

        add(Material.WHEAT,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK);

        add(Material.WOOD,
                MaterialProperty.SUB_MATERIAL_DATA,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.WOOD_AXE,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.TOOL,
                MaterialProperty.AXE,
                MaterialProperty.CRAFTABLE);

        add(Material.WOOD_BUTTON,
                MaterialProperty.PLACEABLE,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.REDSTONE,
                MaterialProperty.REDSTONE_SWITCH,
                MaterialProperty.BUTTON,
                MaterialProperty.CRAFTABLE);

        add(Material.WOOD_DOOR,
                MaterialProperty.OPENABLE_BOUNDARY,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.DOOR,
                MaterialProperty.PLACEABLE,
                MaterialProperty.REDSTONE,
                MaterialProperty.CRAFTABLE);

        add(Material.WOOD_DOUBLE_STEP,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.BLOCK,
                MaterialProperty.SUB_MATERIAL_DATA,
                MaterialProperty.SURFACE);

        add(Material.WOOD_HOE,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.TOOL,
                MaterialProperty.HOE,
                MaterialProperty.CRAFTABLE);

        add(Material.WOOD_PICKAXE,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.TOOL,
                MaterialProperty.MINING_TOOL,
                MaterialProperty.PICKAXE,
                MaterialProperty.CRAFTABLE);

        add(Material.WOOD_PLATE,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.TRANSPARENT,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.REDSTONE,
                MaterialProperty.REDSTONE_SWITCH,
                MaterialProperty.PRESSURE_PLATE,
                MaterialProperty.CRAFTABLE);

        add(Material.WOOD_SPADE,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.TOOL,
                MaterialProperty.MINING_TOOL,
                MaterialProperty.SHOVEL,
                MaterialProperty.CRAFTABLE);

        add(Material.WOOD_STAIRS,
                MaterialProperty.STAIRS,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.DIRECTIONAL_DATA,
                MaterialProperty.PLACEABLE,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.SURFACE);

        add(Material.WOOD_STEP,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.SUB_MATERIAL_DATA,
                MaterialProperty.BLOCK,
                MaterialProperty.SURFACE);

        add(Material.WOOD_SWORD,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.REPAIRABLE,
                MaterialProperty.WEAPON,
                MaterialProperty.CRAFTABLE);

        add(Material.WOODEN_DOOR,
                MaterialProperty.WOOD_BASED,
                MaterialProperty.OPENABLE_BOUNDARY,
                MaterialProperty.DOOR,
                MaterialProperty.MULTI_BLOCK,
                MaterialProperty.REDSTONE);// block

        add(Material.WOOL,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.MULTI_COLOR_DATA,
                MaterialProperty.CRAFTABLE);

        add(Material.WORKBENCH,
                MaterialProperty.PLACEABLE,
                MaterialProperty.BLOCK,
                MaterialProperty.CRAFTABLE,
                MaterialProperty.GUI,
                MaterialProperty.SURFACE);

        add(Material.WRITTEN_BOOK,
                MaterialProperty.GUI);

        add(Material.YELLOW_FLOWER,
                MaterialProperty.BLOCK,
                MaterialProperty.TRANSPARENT);
    }
}
