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

package com.jcwhatever.nucleus.utils.potions;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.materials.Materials;
import com.jcwhatever.nucleus.utils.nms.INmsPotionHandler;
import com.jcwhatever.nucleus.utils.nms.NmsUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import javax.annotation.Nullable;

/**
 * Potion utilities.
 */
public final class PotionUtils {

    private PotionUtils() {}

    private static final INmsPotionHandler _handler = NmsUtils.getPotionHandler();

    /**
     * Get a {@link org.bukkit.potion.PotionEffectType} from an object.
     *
     * <p>The object must be an instance of {@link org.bukkit.potion.PotionEffectType},
     * {@link org.bukkit.potion.PotionEffect} or the name of the type.</p>
     *
     * @param object  The potion effect type or name.
     *
     * @return  The potion effect type or null if it could not be converted..
     */
    @Nullable
    public static PotionEffectType getPotionEffectType(Object object) {

        if (object instanceof String) {
            String name = ((String)object).toUpperCase();

            PotionEffectType type = PotionEffectType.getByName(name);
            if (type == null)
                return null;

            return type;
        }
        else if (object instanceof PotionEffectType) {
            return (PotionEffectType)object;
        }
        else if (object instanceof PotionEffect) {
            PotionEffect effect = (PotionEffect)object;
            return effect.getType();
        }
        else {
            return null;
        }
    }

    public static ItemStack getPotionStack(PotionType type) {
        return getPotionStack(type, 1, false, false);
    }

    public static ItemStack getPotionStack(PotionType type, int level) {
        return getPotionStack(type, level, false, false);
    }

    public static ItemStack getPotionStack(PotionType type, int level, boolean isSplash, boolean isExtended) {
        PreCon.notNull(type);

        if (_handler == null)
            throw new UnsupportedOperationException("A potion NMS handler was not found.");

        return _handler.getPotionStack(type, level, isSplash, isExtended);
    }

    /**
     * Get an ItemStack using the specified potion ID.
     *
     * @param potionId  The potion ID.
     *
     * @return  The potion ItemStack or null if the potion ID is not valid.
     */
    @Nullable
    ItemStack getPotionStack(int potionId) {

        if (_handler == null)
            throw new UnsupportedOperationException("A potion NMS handler was not found.");

        return _handler.getPotionStack(potionId);
    }

    /**
     * Determine if an item stack can potentially be used as a potion ingredient.
     *
     * @param itemStack  The item stack to check.
     */
    public static boolean isPotionIngredient(ItemStack itemStack) {
        PreCon.notNull(itemStack);

        Material material = itemStack.getType();

        return Materials.isPotionIngredient(material)
                && (material != Material.RAW_FISH || itemStack.getData().getData() == 3);
    }

    /**
     * Get {@link Potion} for the result of the specified potion recipe.
     *
     * @param ingredient  The potion ingredient.
     * @param bottle      The potion target.
     *
     * @return  The potion or null if not a valid recipe.
     */
    @Nullable
    public static Potion getPotionFromRecipe(ItemStack ingredient, ItemStack bottle) {
        int potionId = getPotionIdFromRecipe(ingredient, bottle);
        if (potionId == -1)
            return null;

        return new Potion(potionId);
    }

    /**
     * Get the potion ID for the result of the specified potion recipe.
     *
     * @param ingredient  The potion ingredient.
     * @param bottle      The potion target.
     *
     * @return  The potion Id or -1 if not a valid recipe.
     */
    public static int getPotionIdFromRecipe(ItemStack ingredient, ItemStack bottle) {
        PreCon.notNull(ingredient);
        PreCon.notNull(bottle);

        if (_handler != null)
            return _handler.getPotionIdFromRecipe(ingredient, bottle);

        return -1;
    }
}
