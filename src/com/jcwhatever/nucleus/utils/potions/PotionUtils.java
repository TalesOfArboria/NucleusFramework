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

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;

/**
 * Potion utilities.
 */
public final class PotionUtils {

    private PotionUtils() {}

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
}
