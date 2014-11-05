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


package com.jcwhatever.bukkit.generic.items;

import com.jcwhatever.bukkit.generic.collections.WeightedList;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * A list of weighted {@code ItemStacks}. The weight of an item stack
 * is used to affect the outcome of randomly choosing from the list
 * using the {@code getRandom} method.
 */
public class WeightedItems extends WeightedList<ItemStack> {

    private static final Map<Material, Integer> _defaultWeightMap;

    static {

        _defaultWeightMap = new EnumMap<>(Material.class);

        new MapHelper()
                .add(9,   Material.APPLE)
                .add(10,  Material.ARROW)
                .add(5,   Material.BAKED_POTATO)
                .add(5,   Material.BLAZE_POWDER)
                .add(5,   Material.BOAT)
                .add(8,   Material.BONE)
                .add(9,   Material.BOW)
                .add(2,   Material.BOWL)
                .add(8,   Material.BREAD)
                .add(10,  Material.BROWN_MUSHROOM)
                .add(1,   Material.CAKE)
                .add(9,   Material.CARROT)
                .add(6,   Material.CHAINMAIL_BOOTS)
                .add(4,   Material.CHAINMAIL_CHESTPLATE)
                .add(6,   Material.CHAINMAIL_HELMET)
                .add(5,   Material.CHAINMAIL_LEGGINGS)
                .add(6,   Material.CHAINMAIL_HELMET)
                .add(5,   Material.COAL)
                .add(1,   Material.COAL_BLOCK)
                .add(5,   Material.COCOA)
                .add(2,   Material.COOKED_BEEF)
                .add(2,   Material.COOKED_CHICKEN)
                .add(2,   Material.COOKED_FISH)
                .add(8,   Material.COOKIE)
                .add(6,   Material.CHAINMAIL_HELMET)
                .add(3,   Material.DIAMOND)
                .add(1,   Material.DIAMOND_AXE)
                .add(1,   Material.DIAMOND_BARDING)
                .add(1,   Material.DIAMOND_BLOCK)
                .add(1,   Material.DIAMOND_BOOTS)
                .add(1,   Material.DIAMOND_CHESTPLATE)
                .add(1,   Material.DIAMOND_HELMET)
                .add(1,   Material.DIAMOND_HOE)
                .add(1,   Material.DIAMOND_LEGGINGS)
                .add(1,   Material.DIAMOND_ORE)
                .add(1,   Material.DIAMOND_PICKAXE)
                .add(1,   Material.DIAMOND_SPADE)
                .add(1,   Material.DIAMOND_SWORD)
                .add(6,   Material.EGG)
                .add(5,   Material.ENDER_PEARL)
                .add(1,   Material.EXP_BOTTLE)
                .add(6,   Material.FEATHER)
                .add(5,   Material.FERMENTED_SPIDER_EYE)
                .add(4,   Material.FISHING_ROD)
                .add(5,   Material.FLINT)
                .add(3,   Material.FLINT_AND_STEEL)
                .add(1,   Material.GHAST_TEAR)
                .add(3,   Material.GLASS_BOTTLE)
                .add(3,   Material.GLOWSTONE_DUST)
                .add(6,   Material.GOLD_AXE)
                .add(6,   Material.GOLD_BARDING)
                .add(1,   Material.GOLD_BLOCK)
                .add(7,   Material.GOLD_BOOTS)
                .add(5,   Material.GOLD_CHESTPLATE)
                .add(7,   Material.GOLD_HELMET)
                .add(6,   Material.GOLD_HOE)
                .add(6,   Material.GOLD_INGOT)
                .add(4,   Material.GOLD_LEGGINGS)
                .add(6,   Material.GOLD_NUGGET)
                .add(6,   Material.GOLD_PICKAXE)
                .add(6,   Material.GOLD_AXE)
                .add(6,   Material.GOLD_SPADE)
                .add(6,   Material.GOLD_SWORD)
                .add(1,   Material.GOLDEN_APPLE)
                .add(1,   Material.GOLDEN_CARROT)
                .add(2,   Material.GRILLED_PORK)
                .add(6,   Material.GOLD_AXE)
                .add(2,   Material.INK_SACK)
                .add(4,   Material.IRON_AXE)
                .add(4,   Material.IRON_BARDING)
                .add(1,   Material.IRON_BLOCK)
                .add(5,   Material.IRON_BOOTS)
                .add(3,   Material.IRON_CHESTPLATE)
                .add(5,   Material.IRON_HELMET)
                .add(4,   Material.IRON_HOE)
                .add(4,   Material.IRON_INGOT)
                .add(4,   Material.IRON_LEGGINGS)
                .add(4,   Material.IRON_PICKAXE)
                .add(4,   Material.IRON_SPADE)
                .add(4,   Material.IRON_SWORD)
                .add(1,   Material.LAVA)
                .add(1,   Material.LAVA_BUCKET)
                .add(7,   Material.LEATHER)
                .add(7,   Material.LEATHER_BOOTS)
                .add(6,   Material.LEATHER_CHESTPLATE)
                .add(7,   Material.LEATHER_HELMET)
                .add(6,   Material.LEATHER_LEGGINGS)
                .add(2,   Material.MAGMA_CREAM)
                .add(4,   Material.MELON)
                .add(4,   Material.MILK_BUCKET)
                .add(3,   Material.NETHER_WARTS)
                .add(1,   Material.NETHER_STAR)
                .add(6,   Material.PORK)
                .add(7,   Material.POTATO)
                .add(7,   Material.POTATO_ITEM)
                .add(2,   Material.POTION)
                .add(5,   Material.PUMPKIN_PIE)
                .add(5,   Material.RAW_BEEF)
                .add(6,   Material.RAW_CHICKEN)
                .add(7,   Material.RAW_FISH)
                .add(8,   Material.RED_MUSHROOM)
                .add(9,   Material.ROTTEN_FLESH)
                .add(4,   Material.SADDLE)
                .add(7,   Material.SHEARS)
                .add(7,   Material.SNOW_BALL)
                .add(4,   Material.SPECKLED_MELON)
                .add(6,   Material.SPIDER_EYE)
                .add(8,   Material.STICK)
                .add(7,   Material.STONE_AXE)
                .add(7,   Material.STONE_HOE)
                .add(7,   Material.STONE_PICKAXE)
                .add(7,   Material.STONE_SPADE)
                .add(8,   Material.STONE_SWORD)
                .add(6,   Material.STRING)
                .add(1,   Material.TNT)
                .add(7,   Material.WHEAT)
                .add(9,   Material.WOOD_AXE)
                .add(9,   Material.WOOD_HOE)
                .add(9,   Material.WOOD_PICKAXE)
                .add(9,   Material.WOOD_SPADE)
                .add(10,  Material.WOOD_SWORD)
        ;
    }

    /**
     * Constructor.
     */
    public WeightedItems() {
        super();
    }

    /**
     * Constructor.
     *
     * @param size  The initial capacity.
     */
    public WeightedItems(int size) {
        super(size);
    }

    /**
     * Constructor.
     *
     * @param items  Initial items to add using default weights.
     */
    public WeightedItems(Collection<ItemStack> items) {
        PreCon.notNull(items);

        addAll(items);
    }

    /**
     * Constructor.
     *
     * @param items  Initial items to add using default weights.
     */
    public WeightedItems(ItemStack[] items) {
        PreCon.notNull(items);

        add(items);
    }

    /**
     * Add an {@code ItemStack} using the default weight.
     *
     * @param item  The item to add.
     */
    @Override
    public boolean add(ItemStack item) {
        PreCon.notNull(item);

        return add(item, getDefaultWeight(item));
    }

    /**
     * Add a collection of {@code ItemStack}'s using the
     * default weights.
     *
     * @param items  The items to add.
     */
    @Override
    public boolean addAll(Collection<? extends ItemStack> items) {
        PreCon.notNull(items);

        for (ItemStack item : items) {
            add(item);
        }
        return true;
    }

    /**
     * Add an array of {@code ItemStacks} using the default
     * weights.
     *
     * @param items  The items to add.
     */
    public void add(ItemStack[] items) {
        PreCon.notNull(items);

        for (ItemStack item : items) {
            add(item);
        }
    }

    /**
     * Get the default weight value for the specified
     * {@code ItemStack}.
     *
     * @param item  The item to check.
     */
    public int getDefaultWeight(ItemStack item) {
        PreCon.notNull(item);

        Integer weight = _defaultWeightMap.get(item.getType());
        if (weight != null)
            return weight;
        return 5;
    }

    static class MapHelper {
        public MapHelper add(int weight, Material material) {
            _defaultWeightMap.put(material, weight);
            return this;
        }
    }

}
