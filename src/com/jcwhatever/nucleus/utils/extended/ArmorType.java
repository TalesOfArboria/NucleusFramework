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


package com.jcwhatever.nucleus.utils.extended;

import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;


/**
 * Armor type enumeration. Maps Armor items to one of the following:
 * Helmet (head piece), Chestplate (torso), Leggings (pants), Boots, or
 * Horse Armor.
 */
public enum ArmorType {
    HELMET(0,
            ArmorPiece.n(0.04F, Material.LEATHER_HELMET),
            ArmorPiece.n(0.08F, Material.CHAINMAIL_HELMET),
            ArmorPiece.n(0.08F, Material.IRON_HELMET),
            ArmorPiece.n(0.08F, Material.GOLD_HELMET),
            ArmorPiece.n(0.12F, Material.DIAMOND_HELMET)
    ),

    CHESTPLATE(1,
            ArmorPiece.n(0.12F, Material.LEATHER_CHESTPLATE),
            ArmorPiece.n(0.20F, Material.CHAINMAIL_CHESTPLATE),
            ArmorPiece.n(0.20F, Material.GOLD_CHESTPLATE),
            ArmorPiece.n(0.24F, Material.IRON_CHESTPLATE),
            ArmorPiece.n(0.32F, Material.DIAMOND_CHESTPLATE)

    ),

    LEGGINGS(2,
            ArmorPiece.n(0.08F, Material.LEATHER_LEGGINGS),
            ArmorPiece.n(0.12F, Material.GOLD_LEGGINGS),
            ArmorPiece.n(0.16F, Material.CHAINMAIL_LEGGINGS),
            ArmorPiece.n(0.20F, Material.IRON_LEGGINGS),
            ArmorPiece.n(0.24F, Material.DIAMOND_LEGGINGS)

    ),
    BOOTS(3,
            ArmorPiece.n(0.04F, Material.LEATHER_BOOTS),
            ArmorPiece.n(0.04F, Material.CHAINMAIL_BOOTS),
            ArmorPiece.n(0.04F, Material.GOLD_BOOTS),
            ArmorPiece.n(0.08F, Material.IRON_BOOTS),
            ArmorPiece.n(0.12F, Material.DIAMOND_BOOTS)

    ),
    HORSE_ARMOR(-1,
            ArmorPiece.n(0.0F, Material.IRON_BARDING),
            ArmorPiece.n(0.0F, Material.GOLD_BARDING),
            ArmorPiece.n(0.0F, Material.DIAMOND_BARDING)
    ),
    NOT_ARMOR(-1);

    private final ArmorPiece[] _types;
    private final int _slot;

    private ArmorType(int slot, ArmorPiece... types) {
        _types = types;
        _slot = slot;
    }

    /**
     * Get the slot index the armor is typically placed in.
     *
     * @return  -1 if no result.
     */
    public int getArmorSlot() {
        return _slot;
    }

    /**
     * Get the type of armor an {@link org.bukkit.inventory.ItemStack} is.
     *
     * @param stack  The {@link org.bukkit.inventory.ItemStack} to check.
     */
    public static ArmorType getType(@Nullable ItemStack stack) {
        Material stackType = stack == null ? Material.AIR : stack.getType();
        return getType(stackType);
    }

    /**
     * Get the type of armor a material is.
     *
     * @param material  The {@link Material} to check.
     */
    public static ArmorType getType(Material material) {
        PreCon.notNull(material);

        for (ArmorType armorType : ArmorType.values()) {
            for (ArmorPiece ap : armorType._types) {
                if (material != ap.material)
                    continue;

                return armorType;
            }
        }
        return ArmorType.NOT_ARMOR;
    }

    /**
     * Get the sum of defense values of specified {@link org.bukkit.inventory.ItemStack}'s.
     *
     * @param armor  The {@link org.bukkit.inventory.ItemStack}'s to check.
     */
    public static float getDefense(ItemStack... armor) {
        PreCon.notNull(armor);
        PreCon.greaterThanZero(armor.length);

        float result = 0.0F;

        for (ItemStack item : armor) {
            result += getDefense(item.getType());
        }

        return result;
    }

    /**
     * Get the defense value for an armor material.
     *
     * <p>Returns 0.0F if the material is not armor.</p>
     *
     * @param material  The material to check.
     */
    public static float getDefense(Material material) {
        PreCon.notNull(material);

        for (ArmorType armorType : ArmorType.values()) {
            for (ArmorPiece ap : armorType._types) {
                if (material != ap.material)
                    continue;

                return ap.defense;
            }
        }

        return 0.0F;
    }

    /**
     * Estimate damage after player/living entity armor absorbs it.
     * Does not calculate for enchantment.
     *
     * @param entity  The living entity.
     * @param damage  The damage to check.
     */
    public static double getEstimatedDamage(LivingEntity entity, double damage) {
        PreCon.notNull(entity);
        PreCon.positiveNumber(damage);

        ItemStack[] armor = entity.getEquipment().getArmorContents();
        float defense = getDefense(armor);

        if (Float.compare(defense, 0.0F) == 0)
            return damage;

        return damage - (damage * defense);
    }

    /**
     * Represents information about an Armor item.
     */
    private static class ArmorPiece {
        Material material;
        float defense;

        private static ArmorPiece n(float defense, Material material) {
            ArmorPiece ap = new ArmorPiece();
            ap.material = material;
            ap.defense = defense;

            return ap;
        }
    }
} 