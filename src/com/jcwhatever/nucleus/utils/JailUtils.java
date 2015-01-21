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

package com.jcwhatever.nucleus.utils;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.jail.Jail;
import com.jcwhatever.nucleus.jail.JailSession;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * Jail utilities.
 */
public final class JailUtils {

    private JailUtils() {}

    /**
     * Determine if a player is imprisoned in any registered jails.
     *
     * @param player  The player to check.
     */
    public static boolean isPrisoner(Player player) {
        PreCon.notNull(player);

        return isPrisoner(player.getUniqueId());
    }

    /**
     * Determine if a player is imprisoned in any registered jails.
     *
     * @param playerId  The ID of the player to check.
     */
    public static boolean isPrisoner(UUID playerId) {
        PreCon.notNull(playerId);

        return Nucleus.getJailManager().isPrisoner(playerId);
    }

    /**
     * Imprison the player in the server jail.
     *
     * @param player     The player to imprison.
     * @param duration   The number of minutes to imprison the player.
     * @param timeScale  The time scale of the specified duration.
     *                   Max resolution is implementation dependent.
     *
     * @return  Null if the player could not be imprisoned.
     */
    public static JailSession imprison(Player player, int duration, TimeScale timeScale) {
        PreCon.notNull(player);
        PreCon.greaterThanZero(duration);
        PreCon.notNull(timeScale);

        return Nucleus.getDefaultJail().imprison(player, duration, timeScale);
    }

    /**
     * Release a player from their current imprisonment session.
     *
     * @param player  The player to release.
     *
     * @return  True if released, false if release failed or player is not imprisoned.
     */
    public static boolean release(Player player) {
        PreCon.notNull(player);

        return release(player.getUniqueId());
    }

    /**
     * Release a player from their current imprisonment session.
     *
     * @param playerId  The ID of the player to release.
     *
     * @return  True if released, false if release failed or player is not imprisoned.
     */
    public static boolean release(UUID playerId) {
        PreCon.notNull(playerId);

        return Nucleus.getJailManager().release(playerId);
    }

    /**
     * Get a players current jail session.
     *
     * @param player  The player to check.
     *
     * @return  Null if the player is not imprisoned.
     */
    public static JailSession getSession(Player player) {
        PreCon.notNull(player);

        return getSession(player.getUniqueId());
    }

    /**
     * Get a players current jail session.
     *
     * @param playerId  The id of the player.
     *
     * @return  Null if the player is not imprisoned.
     */
    public static JailSession getSession(UUID playerId) {
        PreCon.notNull(playerId);

        return Nucleus.getJailManager().getSession(playerId);
    }

    /**
     * Get all registered {@code JailSession}'s.
     */
    public static List<JailSession> getSessions() {
        return Nucleus.getJailManager().getSessions();
    }

    /**
     * Get all registered jails.
     */
    List<Jail> getJails() {
        return Nucleus.getJailManager().getJails();
    }
}
