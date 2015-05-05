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

package com.jcwhatever.nucleus.managed.particles;

import com.jcwhatever.nucleus.utils.coords.ICoords3D;
import com.jcwhatever.nucleus.utils.coords.ICoords3Di;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;

/**
 * Mixin for a particle effect that can be spawned with a vector.
 */
public interface IVectorParticle {

    /**
     * Show the effect to a player.
     *
     * @param player    The player.
     * @param location  The location to spawn the effect at.
     * @param vector    The particle vector.
     *
     * @return  True if displayed, otherwise false.
     */
    boolean showTo(Player player, Location location, Vector vector);

    /**
     * Show the effect to a player.
     *
     * @param player  The player.
     * @param coords  The coordinates to spawn the effect at.
     * @param vector  The particle vector.
     *
     * @return  True if displayed, otherwise false.
     */
    boolean showTo(Player player, ICoords3D coords, Vector vector);

    /**
     * Show the effect to a player.
     *
     * @param player  The player.
     * @param coords  The coordinates to spawn the effect at.
     * @param vector  The particle vector.
     *
     * @return  True if displayed, otherwise false.
     */
    boolean showTo(Player player, ICoords3Di coords, Vector vector);

    /**
     * Show the effect to a collection of players.
     *
     * @param players   The players.
     * @param location  The location to spawn the effect at.
     * @param vector    The particle vector.
     *
     * @return  True if the effect was shown to at least one of the players,
     * otherwise false.
     */
    boolean showTo(Collection<? extends Player> players, Location location, Vector vector);

    /**
     * Show the effect to a collection of players.
     *
     * @param players  The players.
     * @param coords   The coordinates to spawn the effect at.
     * @param vector   The particle vector.
     *
     * @return  True if the effect was shown to at least one of the players,
     * otherwise false.
     */
    boolean showTo(Collection<? extends Player> players, ICoords3D coords, Vector vector);

    /**
     * Show the effect to a collection of players.
     *
     * @param players  The players.
     * @param coords   The coordinates to spawn the effect at.
     * @param vector   The particle vector.
     *
     * @return  True if the effect was shown to at least one of the players,
     * otherwise false.
     */
    boolean showTo(Collection<? extends Player> players, ICoords3Di coords, Vector vector);

    /**
     * Show the effect to all player within the effects visible radius.
     *
     * @param location  The location to spawn the effect.
     * @param vector    The particle vector.
     *
     * @return  True if the effect was shown to at least one of the players,
     * otherwise false.
     */
    boolean showFrom(Location location, Vector vector);
}
