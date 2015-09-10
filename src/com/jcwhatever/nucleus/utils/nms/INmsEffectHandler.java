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

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Interface for NucleusFramework's Minecraft Misc. Effect handler.
 *
 * @see NmsUtils
 */
public interface INmsEffectHandler extends INmsHandler {

    /**
     * Send a lightning strike to all players within a specified block radius.
     *
     * @param location  The location of the lightning strike.
     * @param radius    The radius.
     * @param sound     True to add sound to the strike, otherwise false.
     *
     * @return  The players that the lightning strike was sent to.
     */
    Collection<Player> lightningStrike(Location location, double radius, boolean sound);

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
    <T extends Collection<Player>> T lightningStrike(Location location, double radius, boolean sound, T output);

    /**
     * Send a lighting strike to a single player.
     *
     * @param player    The player to send the lightning strike to.
     * @param location  The location of the lightning strike.
     * @param sound     True to add sound to the strike, otherwise false.
     */
    void lightningStrike(Player player, Location location, boolean sound);

    /**
     * Send a lighting strike to a collection of players.
     *
     * @param players   The players to send the lightning strike to.
     * @param location  The location of the lightning strike.
     * @param sound     True to add sound to the strike, otherwise false.
     */
    void lightningStrike(Collection<Player> players, Location location, boolean sound);
}
