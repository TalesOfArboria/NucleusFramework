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

package com.jcwhatever.nucleus.utils.player;

import com.jcwhatever.nucleus.utils.ArrayUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Static player exp utilities.
 */
public final class Exp {

    private Exp() {}

    private static final int MAX_LEVELS = 21863;
    private static final int[] EXP_LEVEL_LOOKUP = new int[MAX_LEVELS / 4];

    static {

        for (int i=0; i < EXP_LEVEL_LOOKUP.length; i++) {
            EXP_LEVEL_LOOKUP[i] = calculateExpInLevel(i);
        }
    }

    /**
     * Get a players total exp.
     *
     * @param player  The player.
     */
    public static double get(Player player) {
        PreCon.notNull(player);

        int level = player.getLevel();
        float exp = player.getExp();

        return get(level) + exp;
    }

    /**
     * Set a players total experience.
     *
     * @param player  The player.
     * @param exp     The experience.
     */
    public static void set(Player player, double exp) {
        PreCon.notNull(player);
        PreCon.positiveNumber(exp);

        double current = get(player);

        int level = getLevel(exp);
        double lvlExp = get(level);

        player.setLevel(level);

        if (exp > current) {
            player.setTotalExperience((int) (player.getTotalExperience() + (exp - current)));
        }
        else {
            // causes client update
            player.setTotalExperience(player.getTotalExperience());
        }

        double capacity = getBarCapacity(level);
        double expPercent = (exp - lvlExp) / capacity;
        player.setExp((float) expPercent);
    }

    /**
     * Increment a players exp by the specified amount.
     *
     * <p>Negative numbers are allowed.</p>
     *
     * @param player  The player.
     * @param exp     The amount of experience to add or subtract.
     */
    public static void increment(Player player, double exp) {
        PreCon.notNull(player);

        if (Double.compare(exp, 0.0D) == 0)
            return;

        double current = get(player);
        current += exp;

        set(player, Math.max(0.0D, current));
    }

    /**
     * Get the total Exp required to reach the given level.
     *
     * @param level  The exp level.
     */
    public static double get(int level) {
        PreCon.positiveNumber(level);

        if (level >= EXP_LEVEL_LOOKUP.length)
            return calculateExpInLevel(level);

        return EXP_LEVEL_LOOKUP[level];
    }

    /**
     * Get the exp level of the specified amount of experience.
     *
     * @param exp  The experience.
     */
    public static int getLevel(double exp) {
        PreCon.positiveNumber(exp);

        if (Double.compare(exp, 0.0D) == 0)
            return 0;

        if (exp > ArrayUtils.last(EXP_LEVEL_LOOKUP)) {
            return calculateLevel((int)exp);
        }

        int insertion = Arrays.binarySearch(EXP_LEVEL_LOOKUP, (int) exp);
        return insertion < 0 ? -(insertion) - 2 : insertion;
    }

    /**
     * Set a players experience to the amount required for the
     * specified level.
     *
     * @param player  The player.
     * @param level   The level.
     */
    public static void setLevel(Player player, int level) {
        PreCon.notNull(player);
        PreCon.positiveNumber(level);

        double exp = get(level);
        set(player, exp);
    }

    /**
     * Increment a players level by the specified amount.
     *
     * <p>Negative numbers are allowed.</p>
     *
     * @param player  The player.
     * @param amount  The amount to add or subtract.
     */
    public static void incrementLevel(Player player, int amount) {
        PreCon.notNull(player);

        if (amount == 0)
            return;

        int level = player.getLevel();
        double currLevelExp = get(level);

        level += amount;
        level = Math.max(0, level);

        double newLevelExp = get(level);

        double deltaExp = newLevelExp - currLevelExp;

        increment(player, deltaExp);
    }

    /**
     * Get the amount of Exp that can be held in the players experience
     * bar at the given level.
     *
     * @param level  The exp level.
     */
    public static double getBarCapacity(int level) {
        PreCon.positiveNumber(level);

        // algorithm found at NMS: EntityHuman.getExpToLevel()
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        }
        else if (level >= 15) {
            return 37 + (level - 15) * 5;
        }
        else {
            return 7 + level * 2;
        }
    }

    private static int calculateExpInLevel(int level) {
        int current = 0;
        int exp = 0;

        while (current < level) {
            exp += getBarCapacity(current);
            current++;
        }

        return exp;
    }

    private static int calculateLevel(int exp) {
        int level = 0;
        int current = getLevel(1);

        while (current <= exp) {
            level++;
            current = getLevel(level);
        }

        return level;
    }
}
