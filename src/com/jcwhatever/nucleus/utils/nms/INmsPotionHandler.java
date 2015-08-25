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

package com.jcwhatever.nucleus.utils.nms;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import javax.annotation.Nullable;

/**
 * Interface for NucleusFramework's Minecraft Potion handler.
 *
 * @see NmsUtils
 */
public interface INmsPotionHandler extends INmsHandler {

    /**
     * Get the potion ID for the specified potion parameters.
     *
     * @param type        The potion type.
     * @param level       The potion level.
     * @param isSplash    True for splash potions, otherwise false.
     * @param isExtended  True for extended duration potions, otherwise false.
     */
    int getPotionId(PotionType type, int level, boolean isSplash, boolean isExtended);

    /**
     * Get an ItemStack using the specified potion parameters.
     *
     * @param type        The potion type.
     * @param level       The potion level.
     * @param isSplash    True for splash potions, otherwise false.
     * @param isExtended  True for extended duration potions, otherwise false.
     */
    ItemStack getPotionStack(PotionType type, int level, boolean isSplash, boolean isExtended);

    /**
     * Get an ItemStack using the specified potion ID.
     *
     * @param potionId  The potion ID.
     *
     * @return  The potion ItemStack or null if the potion ID is not valid.
     */
    @Nullable
    ItemStack getPotionStack(int potionId);

    /**
     * Get the potion ID for the result of the specified potion recipe.
     *
     * @param ingredient  The potion ingredient.
     * @param bottle      The potion target.
     *
     * @return  The potion Id or -1 if not a valid recipe.
     */
    int getPotionIdFromRecipe(ItemStack ingredient, ItemStack bottle);

    /**
     * Determine if a potion ID specifies a splash potion.
     *
     * @param potionId  The ID of the potion.
     */
    boolean isSplash(int potionId);

    /**
     * Get the level of a potion specified by the potion ID.
     *
     * @param potionId   The ID of the potion.
     */
    int getLevel(int potionId);

    /**
     * Determine if a potion ID specifies an extended duration.
     *
     * @param potionId  The ID of the potion.
     */
    boolean isExtendedDuration(int potionId);
}
