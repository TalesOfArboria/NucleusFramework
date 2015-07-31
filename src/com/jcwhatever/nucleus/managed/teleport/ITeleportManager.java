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

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Interface for the global teleport manager.
 */
public interface ITeleportManager {

    /**
     * Get a players current scheduled teleport.
     *
     * @param player  The player.
     *
     * @return  The scheduled teleport or null if there is no teleport scheduled.
     */
    @Nullable
    IScheduledTeleport getScheduled(Player player);

    /**
     * Teleport a player to the specified location after a specified delay.
     *
     * <p>Teleport mode is {@link TeleportMode#MOUNTS_AND_LEASHED}.</p>
     *
     * @param plugin     The invoking plugin.
     * @param player     The player to teleport.
     * @param location   The location to teleport the player to.
     * @param tickDelay  The delay in ticks.
     *
     * @return  The scheduled teleport future.
     */
    IScheduledTeleport teleport(Plugin plugin, Player player, Location location, int tickDelay);

    /**
     * Teleport a player to the specified location after a specified delay.
     *
     * @param plugin     The invoking plugin.
     * @param player     The player to teleport.
     * @param location   The location to teleport the player to.
     * @param tickDelay  The delay in ticks.
     * @param mode       The teleport mode.
     *
     * @return  The scheduled teleport future.
     */
    IScheduledTeleport teleport(Plugin plugin, Player player,
                                Location location, int tickDelay, TeleportMode mode);

    /**
     * Teleports a player to the specified location.
     *
     * <p>Cancels delayed teleport for player, if any.</p>
     *
     * <p>Teleport mode is {@link TeleportMode#MOUNTS_AND_LEASHED}.</p>
     *
     * @param player    The player to teleport.
     * @param location  The location to teleport the player to.
     *
     * @return  True if the player was teleported, otherwise false.
     */
    boolean teleport(Player player, Location location);

    /**
     * Teleports a player to the specified location.
     *
     * <p>Cancels delayed teleport for player, if any.</p>
     *
     * @param player    The player to teleport.
     * @param location  The location to teleport the player to.
     * @param mode      The teleport mode.
     *
     * @return  True if the player was teleported, otherwise false.
     */
    boolean teleport(Player player, Location location, TeleportMode mode);

    /**
     * Teleports a player to the specified location.
     *
     * <p>Cancels delayed teleport for player, if any.</p>
     *
     * <p>Teleport mode is {@link TeleportMode#MOUNTS_AND_LEASHED}.</p>
     *
     * @param player    The player to teleport.
     * @param location  The location to teleport the player to.
     * @param cause     The cause of the player teleport.
     *
     * @return  True if the player was teleported, otherwise false.
     */
    boolean teleport(Player player, Location location, PlayerTeleportEvent.TeleportCause cause);

    /**
     * Teleports a player to the specified location.
     *
     * <p>Cancels delayed teleport for player, if any.</p>
     *
     * @param player    The player to teleport.
     * @param location  The location to teleport the player to.
     * @param cause     The cause of the player teleport.
     * @param mode      The teleport mode.
     *
     * @return  True if the player was teleported, otherwise false.
     */
    boolean teleport(Player player, Location location,
                     PlayerTeleportEvent.TeleportCause cause, TeleportMode mode);

    /**
     * Teleports a player to the specified entity.
     *
     * <p>Cancels delayed teleport for player, if any.</p>
     *
     * <p>Teleport mode is {@link TeleportMode#MOUNTS_AND_LEASHED}.</p>
     *
     * @param player  The player to teleport.
     * @param entity  The entity to teleport to.
     *
     * @return  True if the player was teleported, otherwise false.
     */
    boolean teleport(Player player, Entity entity);

    /**
     * Teleports a player to the specified entity.
     *
     * <p>Cancels delayed teleport for player, if any.</p>
     *
     * @param player  The player to teleport.
     * @param entity  The entity to teleport to.
     * @param mode    The teleport mode.
     *
     * @return  True if the player was teleported, otherwise false.
     */
    boolean teleport(Player player, Entity entity, TeleportMode mode);

    /**
     * Teleports a player to the specified entity.
     *
     * <p>Cancels delayed teleport for player, if any.</p>
     *
     * <p>Teleport mode is {@link TeleportMode#MOUNTS_AND_LEASHED}.</p>
     *
     * @param player  The player to teleport.
     * @param entity  The entity to teleport to.
     * @param cause   The cause of the player teleport.
     *
     * @return  True if the player was teleported, otherwise false.
     */
    boolean teleport(Player player, Entity entity, PlayerTeleportEvent.TeleportCause cause);

    /**
     * Teleports a player to the specified entity.
     *
     * <p>Cancels delayed teleport for player, if any.</p>
     *
     * @param player  The player to teleport.
     * @param entity  The entity to teleport to.
     * @param cause   The cause of the player teleport.
     * @param mode    The teleport mode.
     *
     * @return  True if the player was teleported, otherwise false.
     */
    boolean teleport(Player player, Entity entity,
                     PlayerTeleportEvent.TeleportCause cause, TeleportMode mode);

    /**
     * Teleports an entity to the specified location.
     *
     * <p>Teleport mode is {@link TeleportMode#MOUNTS_AND_LEASHED}.</p>
     *
     * @param entity    The entity to teleport.
     * @param location  The location to teleport to.
     *
     * @return  True if the entity was teleported, otherwise false.
     */
    boolean teleport(Entity entity, Location location);

    /**
     * Teleports an entity to the specified location.
     *
     * @param entity    The entity to teleport.
     * @param location  The location to teleport to.
     * @param mode      The teleport mode.
     *
     * @return  True if the entity was teleported, otherwise false.
     */
    boolean teleport(Entity entity, Location location, TeleportMode mode);
}
