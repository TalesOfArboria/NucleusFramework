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

package com.jcwhatever.nucleus.utils.enchantment;

import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import javax.annotation.Nullable;

/*
 * Enchantment utilities.
 */
public final class EnchantUtils {

    private EnchantUtils() {}

    /**
     * Add enchantments to an {@link ItemStack}.
     *
     * @param stack         The {@link ItemStack} to add enchantments to.
     * @param enchantments  Enchantments to add
     */
    public static void addEnchantments(
            ItemStack stack, Collection<? extends IEnchantmentLevel> enchantments) {

        PreCon.notNull(stack);
        PreCon.notNull(enchantments);

        ItemMeta meta = stack.getItemMeta();

        // check for enchantment storage items such as enchanted books
        if (meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta storage = (EnchantmentStorageMeta)meta;

            for (IEnchantmentLevel enchant : enchantments) {
                storage.addStoredEnchant(enchant.getEnchantment(), enchant.getLevel(), true);
            }

            stack.setItemMeta(storage);
        }
        else {
            for (IEnchantmentLevel enchant : enchantments) {
                stack.addUnsafeEnchantment(enchant.getEnchantment(), enchant.getLevel());
            }
        }
    }

    /**
     * Add an enchantment to an {@link ItemStack}.
     *
     * @param stack    The {@link ItemStack}.
     * @param enchant  The {@link IEnchantmentLevel} containing enchantment info.
     */
    public static void addEnchantment(ItemStack stack, IEnchantmentLevel enchant) {
        PreCon.notNull(stack);
        PreCon.notNull(enchant);

        addEnchantment(stack, enchant.getEnchantment(), enchant.getLevel());
    }

    /**
     * Add an enchantment to an {@link ItemStack}.
     *
     * @param stack        The {@link ItemStack}.
     * @param enchantName  The enchantment to add.
     * @param level        The enchantment level.
     *
     * @return  True if the enchantName was found and applied, otherwise false.
     */
    public static boolean addEnchantment(ItemStack stack, String enchantName, int level) {
        PreCon.notNull(stack);
        PreCon.notNullOrEmpty(enchantName);
        PreCon.positiveNumber(level);

        Enchantment enchantment = Enchantment.getByName(enchantName);
        if (enchantment == null)
            return false;

        addEnchantment(stack, enchantment, level);

        return true;
    }

    /**
     * Add an enchantment to an {@link ItemStack}.
     *
     * @param stack    The item stack.
     * @param enchant  The enchantment to add.
     * @param level    The enchantment level.
     */
    public static void addEnchantment(ItemStack stack, Enchantment enchant, int level) {
        PreCon.notNull(stack);
        PreCon.notNull(enchant);
        PreCon.positiveNumber(level);

        ItemMeta meta = stack.getItemMeta();

        // check for enchantment storage items such as enchanted books
        if (meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta storage = (EnchantmentStorageMeta)meta;
            storage.addStoredEnchant(enchant, level, true);
            stack.setItemMeta(storage);
        }
        else {
            stack.addUnsafeEnchantment(enchant, level);
        }
    }

    /**
     * Removes an enchantment from an {@link ItemStack} and returns an
     * {@link IEnchantmentLevel} containing the enchantment and enchantment
     * level before it was removed.
     *
     * @param stack            The item stack.
     * @param enchantmentName  The name of the enchantment to remove.
     *
     * @return  Null if the enchantment name is not found or the item did not have
     * the enchantment.
     */
    @Nullable
    public static IEnchantmentLevel removeEnchantment(ItemStack stack, String enchantmentName) {
        PreCon.notNull(stack);
        PreCon.notNullOrEmpty(enchantmentName);

        Enchantment enchantment = Enchantment.getByName(enchantmentName);
        if (enchantment == null)
            return null;

        return removeEnchantment(stack, enchantment);
    }

    /**
     * Removes an enchantment from an {@link ItemStack} and returns an
     * {@link IEnchantmentLevel} containing the enchantment and enchantment
     * level before it was removed.
     *
     * @param stack        The item stack.
     * @param enchantment  The enchantment to remove.
     *
     * @return  Null if the item did not have the enchantment
     */
    @Nullable
    public static IEnchantmentLevel removeEnchantment(ItemStack stack, Enchantment enchantment) {
        PreCon.notNull(stack);
        PreCon.notNull(enchantment);

        if (!stack.getEnchantments().containsKey(enchantment))
            return null;

        int level = stack.removeEnchantment(enchantment);

        return EnchantUtils.wrap(level, enchantment);
    }

    /**
     * Wrap an enchantment and the enchantment level.
     *
     * @param level        The enchantment level.
     * @param enchantment  The enchantment.
     */
    public static IEnchantmentLevel wrap(int level, Enchantment enchantment) {
        PreCon.notNull(enchantment);

        return new EnchantmentLevel(level, enchantment);
    }

    /**
     * A wrapper to hold an Enchantment as well as its level.
     */
   private static class EnchantmentLevel implements IEnchantmentLevel {

        private final int _level;
        private final Enchantment _enchantment;

        /**
         * Constructor.
         *
         * @param enchant  The enchantment.
         * @param level    The enchantment level.
         */
        public EnchantmentLevel(int level, Enchantment enchant) {
            PreCon.notNull(enchant);

            _enchantment = enchant;
            _level = level;
        }

        @Override
        public Enchantment getEnchantment() {
            return _enchantment;
        }

        @Override
        public int getLevel() {
            return _level;
        }
    }
}
