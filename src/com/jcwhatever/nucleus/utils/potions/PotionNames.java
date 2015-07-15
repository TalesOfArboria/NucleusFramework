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

import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.ThreadSingletons;
import com.jcwhatever.nucleus.utils.nms.INmsPotionHandler;
import com.jcwhatever.nucleus.utils.nms.NmsUtils;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

/**
 * Stores potion display names.
 */
public class PotionNames {

    private PotionNames() {}

    @Localizable static final String _AWKWARD = "Awkward Potion";
    @Localizable static final String _THICK = "Thick Potion";
    @Localizable static final String _MUNDANE = "Mundane Potion";

    @Localizable static final String _WATER = "Water Bottle";
    @Localizable static final String _REGEN = "Potion of Regeneration";
    @Localizable static final String _SPEED = "Potion of Swiftness";
    @Localizable static final String _FIRE_RESISTANCE = "Potion of Fire Resistance";
    @Localizable static final String _POISON = "Potion of Poison";
    @Localizable static final String _INSTANT_HEAL ="Potion of Healing";
    @Localizable static final String _NIGHT_VISION = "Potion of Night Vision";
    @Localizable static final String _WEAKNESS = "Potion of Weakness";
    @Localizable static final String _STRENGTH = "Potion of Strength";
    @Localizable static final String _SLOWNESS = "Potion of Slowness";
    @Localizable static final String _JUMP = "Potion of Leaping";
    @Localizable static final String _INSTANT_DAMAGE = "Potion of Harming";
    @Localizable static final String _WATER_BREATHING = "Potion of Water Breathing";
    @Localizable static final String _INVISIBILITY = "Potion of Invisibility";

    @Localizable static final String _SPLASH = "Splash";
    @Localizable static final String _LEVEL2 = "II";
    @Localizable static final String _EXTENDED_DURATION = "(ext)";

    private static final ThreadSingletons<StringBuilder> BUFFERS = new ThreadSingletons<>(
            new ThreadSingletons.ISingletonFactory<StringBuilder>() {
                @Override
                public StringBuilder create(Thread thread) {
                    return new StringBuilder(45);
                }
            });

    private static final INmsPotionHandler _handler = NmsUtils.getPotionHandler();

    /**
     * Get a simple potion name.
     *
     * @param potion  The potion.
     */
    public static String getSimple(Potion potion) {
        PreCon.notNull(potion);

        return getSimple(potion.getType());
    }

    /**
     * Get a simple potion name.
     *
     * @param type  The potion type.
     */
    public static String getSimple(PotionType type) {
        PreCon.notNull(type);

        StringBuilder buffer = buffer();
        appendSimple(buffer, type);
        return buffer.toString();
    }

    /**
     * Get a full potion name including characteristics.
     *
     * @param potion  The potion ID.
     */
    public static String getFull(Potion potion) {
        PreCon.notNull(potion);

        int potionId = _handler.getPotionId(
                potion.getType(), potion.getLevel(), potion.isSplash(), potion.hasExtendedDuration());

        return getFull(potionId);
    }

    /**
     * Get a full potion name including characteristics.
     *
     * @param potionId  The potion ID.
     */
    public static String getFull(int potionId) {

        if (potionId <= 64) {

            switch (potionId) {
                case 0:
                    return NucLang.get(_WATER);
                case 16:
                    return NucLang.get(_AWKWARD);
                case 32:
                    return NucLang.get(_THICK);
                case 64:
                    return NucLang.get(_MUNDANE);
                default:
                    throw new IllegalArgumentException(
                            "Failed to get PotionType for Potion name Id: " +  potionId);
            }
        }

        PotionType type = PotionType.getByDamageValue(potionId & 15);

        StringBuilder buffer = buffer();

        if ((potionId & 16384) == 16384) {
            append(buffer, _SPLASH);
            buffer.append(' ');
        }

        appendSimple(buffer, type);

        if ((potionId & 32) == 32) {
            buffer.append(' ');
            append(buffer, _LEVEL2);
        }

        if ((potionId & 64) == 64) {
            buffer.append(' ');
            append(buffer, _EXTENDED_DURATION);
        }

        return buffer.toString();
    }

    private static void appendSimple(StringBuilder buffer, PotionType type) {
        switch (type) {
            case WATER:
                append(buffer, _WATER);
                break;
            case REGEN:
                append(buffer, _REGEN);
                break;
            case SPEED:
                append(buffer, _SPEED);
                break;
            case FIRE_RESISTANCE:
                append(buffer, _FIRE_RESISTANCE);
                break;
            case POISON:
                append(buffer, _POISON);
                break;
            case INSTANT_HEAL:
                append(buffer, _INSTANT_HEAL);
                break;
            case NIGHT_VISION:
                append(buffer, _NIGHT_VISION);
                break;
            case WEAKNESS:
                append(buffer, _WEAKNESS);
                break;
            case STRENGTH:
                append(buffer, _STRENGTH);
                break;
            case SLOWNESS:
                append(buffer, _SLOWNESS);
                break;
            case JUMP:
                append(buffer, _JUMP);
                break;
            case INSTANT_DAMAGE:
                append(buffer, _INSTANT_DAMAGE);
                break;
            case WATER_BREATHING:
                append(buffer, _WATER_BREATHING);
                break;
            case INVISIBILITY:
                append(buffer, _INVISIBILITY);
                break;
        }
    }

    private static void append(StringBuilder buffer, String text) {
        buffer.append(NucLang.get(text));
    }

    private static StringBuilder buffer() {
        StringBuilder buffer = BUFFERS.get();
        buffer.setLength(0);
        return buffer;
    }
}
