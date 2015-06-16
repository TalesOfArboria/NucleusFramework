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

package com.jcwhatever.nucleus.managed.teleport;

import com.jcwhatever.nucleus.Nucleus;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Static convenience methods for accessing the global teleport manager.
 */
public final class Teleporter {

    private Teleporter() {}

    /**
     * Get a players current scheduled teleport.
     *
     * @param player  The player.
     *
     * @return  The scheduled teleport or null if there is no teleport scheduled.
     */
    @Nullable
    public static IScheduledTeleport getScheduled(Player player) {
        return manager().getScheduled(player);
    }

    /**
     * Teleport a player to the specified location after a specified delay.
     *
     * @param plugin     The invoking plugin.
     * @param player     The player to teleport.
     * @param location   The location to teleport the player to.
     * @param tickDelay  The delay in ticks.
     *
     * @return  The scheduled teleport future.
     */
    public static IScheduledTeleport teleport(
            Plugin plugin, Player player, Location location, int tickDelay) {
        return manager().teleport(plugin, player, location, tickDelay);
    }

    /**
     * Teleports a player to the specified location.
     *
     * <p>Cancels delayed teleport for player, if any.</p>
     *
     * @param player    The player to teleport.
     * @param location  The location to teleport the player to.
     *
     * @return  True if the player was teleported, otherwise false.
     */
    public static boolean teleport(Player player, Location location) {
        return manager().teleport(player, location);
    }

    /**
     * Teleports a player to the specified location.
     *
     * <p>Cancels delayed teleport for player, if any.</p>
     *
     * @param player    The player to teleport.
     * @param location  The location to teleport the player to.
     * @param cause     The cause of the player teleport.
     *
     * @return  True if the player was teleported, otherwise false.
     */
    public static boolean teleport(Player player, Location location,
                                   PlayerTeleportEvent.TeleportCause cause) {
        return manager().teleport(player, location, cause);
    }

    /**
     * Teleports a player to the specified entity.
     *
     * <p>Cancels delayed teleport for player, if any.</p>
     *
     * @param player  The player to teleport.
     * @param entity  The entity to teleport to.
     *
     * @return  True if the player was teleported, otherwise false.
     */
    public static boolean teleport(Player player, Entity entity) {
        return manager().teleport(player, entity);
    }

    /**
     * Teleports a player to the specified entity.
     *
     * <p>Cancels delayed teleport for player, if any.</p>
     *
     * @param player  The player to teleport.
     * @param entity  The entity to teleport to.
     * @param cause   The cause of the player teleport.
     *
     * @return  True if the player was teleported, otherwise false.
     */
    public static boolean teleport(Player player, Entity entity,
                                   PlayerTeleportEvent.TeleportCause cause) {
        return manager().teleport(player, entity, cause);
    }

    private static ITeleportManager manager() {
        return Nucleus.getTeleportManager();
    }
}
