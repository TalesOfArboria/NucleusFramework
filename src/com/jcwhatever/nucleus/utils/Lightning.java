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

import com.jcwhatever.nucleus.utils.nms.INmsEffectHandler;
import com.jcwhatever.nucleus.utils.nms.NmsUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Lightning strike utilities.
 */
public final class Lightning {

    private Lightning() {}

    private static INmsEffectHandler _handler;

    /**
     * Send a lightning strike to all players within a specified block radius.
     *
     * <p>If no NMS handler is available, a Bukkit lightning strike is used.</p>
     *
     * <p>Includes lightning strike sounds.</p>
     *
     * @param location  The location of the lightning strike.
     * @param radius    The radius.
     *
     * @return  The players that the lightning strike was sent to.
     */
    public static Collection<Player> strike(Location location, double radius) {
        return strike(location, radius, true);
    }

    /**
     * Send a lightning strike to all players within a specified block radius.
     *
     * @param location  The location of the lightning strike.
     * @param radius    The radius.
     * @param sound     True to add sound to the strike, otherwise false.
     *
     * @return  The players that the lightning strike was sent to.
     */
    public static Collection<Player> strike(Location location, double radius, boolean sound) {
        PreCon.notNull(location);
        PreCon.positiveNumber(radius);

        loadHandler();

        if (_handler == null) {
            location.getWorld().strikeLightningEffect(location);
            return location.getWorld().getPlayers();
        }
        else {
            return _handler.lightningStrike(location, radius, sound);
        }
    }

    /**
     * Send a lightning strike to all players within a specified block radius
     * and add the players to the specified output collection.
     *
     * <p>If no NMS handler is available, a Bukkit lightning strike is used.</p>
     *
     * <p>Includes lightning strike sounds.</p>
     *
     * @param location  The location of the lightning strike.
     * @param radius    The radius.
     * @param output    The output collection.
     *
     * @return  The output collection.
     */
    public static <T extends Collection<Player>> T strike(Location location, double radius, T output) {
        return strike(location, radius, true, output);
    }

    /**
     * Send a lightning strike to all players within a specified block radius
     * and add the players to the specified output collection.
     *
     * @param location  The location of the lightning strike.
     * @param radius    The radius.
     * @param output    The output collection.
     * @param sound     True to add sound to the strike, otherwise false.
     *
     * @return  The output collection.
     */
    public static <T extends Collection<Player>> T strike(Location location,
                                                          double radius, boolean sound, T output) {
        PreCon.notNull(location);
        PreCon.positiveNumber(radius);
        PreCon.notNull(output);

        loadHandler();

        if (_handler == null) {
            location.getWorld().strikeLightningEffect(location);
            output.addAll(location.getWorld().getPlayers());
        }
        else {
            _handler.lightningStrike(location, radius, sound, output);
        }

        return output;
    }

    /**
     * Send a lighting strike to a single player.
     *
     * <p>Includes lightning strike sounds.</p>
     *
     * @param player    The player to send the lightning strike to.
     * @param location  The location of the lightning strike.
     *
     * @return True if successful, false if no lightning NMS handler was present.
     */
    public static boolean strike(Player player, Location location) {
        return strike(player, location, true);
    }

    /**
     * Send a lighting strike to a single player.
     *
     * @param player    The player to send the lightning strike to.
     * @param location  The location of the lightning strike.
     * @param sound     True to add sound to the strike, otherwise false.
     *
     * @return True if successful, false if no lightning NMS handler was present.
     */
    public static boolean strike(Player player, Location location, boolean sound) {
        PreCon.notNull(player);
        PreCon.notNull(location);

        loadHandler();

        if (_handler != null) {
            _handler.lightningStrike(player, location, sound);
            return true;
        }

        return false;
    }

    /**
     * Send a lighting strike to a collection of players.
     *
     * <p>Includes lightning strike sounds.</p>
     *
     * @param players   The players to send the lightning strike to.
     * @param location  The location of the lightning strike.
     *
     * @return True if successful, false if no lightning NMS handler was present.
     */
    public static boolean strike(Collection<Player> players, Location location) {
        return strike(players, location, true);
    }

    /**
     * Send a lighting strike to a collection of players.
     *
     * @param players   The players to send the lightning strike to.
     * @param location  The location of the lightning strike.
     * @param sound     True to add sound to the strike, otherwise false.
     *
     * @return True if successful, false if no lightning NMS handler was present.
     */
    public static boolean strike(Collection<Player> players, Location location, boolean sound) {
        PreCon.notNull(players);
        PreCon.notNull(location);

        loadHandler();

        if (_handler != null) {
            _handler.lightningStrike(players, location, sound);
            return true;
        }
        return false;
    }

    static void loadHandler() {
        if (_handler == null)
            _handler = NmsUtils.getEffectHandler();
    }
}
