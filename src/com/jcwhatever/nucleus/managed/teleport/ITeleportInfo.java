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

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Collection;

/**
 * Interface to provide information about a teleport.
 */
public interface ITeleportInfo {

    /**
     * Determine if the teleport requires multiple ticks to complete.
     */
    boolean isMultiTick();

    /**
     * Get the teleport cause.
     */
    TeleportCause getCause();

    /**
     * Get the teleport mode used.
     */
    TeleportMode getMode();

    /**
     * Get the entity that was teleported.
     */
    Entity getEntity();

    /**
     * Get a list of all players that are teleported.
     */
    Collection<Player> getPlayers();

    /**
     * Add all players that are teleported to the specified output collection.
     *
     * @param output  The output collection.
     *
     * @return  The output collection.
     */
    <T extends Collection<Player>> T getPlayers(T output);

    /**
     * Get all entities that are teleported as part of a mount (passenger/vehicle) relationship.
     */
    Collection<Entity> getMounts();

    /**
     * Add all entities that are teleported as part of a mount (passenger/vehicle) relationship
     * to the specified output collection.
     *
     * @param output  The output collection.
     *
     * @return  The output collection.
     */
    <T extends Collection<Entity>> T getMounts(T output);

    /**
     * Get all entities that are teleported because they were leashed to a teleported entity.
     */
    Collection<ITeleportLeashPair> getLeashed();

    /**
     * Add all entities that are teleported because they were leashed to a teleported entity
     * to the specified output collection.
     *
     * @param output  The output collection.
     *
     * @return  The output collection.
     */
    <T extends Collection<ITeleportLeashPair>> T getLeashed(T output);

    /**
     * Get all entities teleported.
     */
    Collection<Entity> getTeleports();

    /**
     * Add all entities teleported to the specified output collection.
     *
     * @param output  The output collection.
     *
     * @return  The output collection.
     */
    <T extends Collection<Entity>> T getTeleports(T output);

    /**
     * Get entities that were part of a mount but not teleported.
     */
    Collection<Entity> getRejectedMounts();

    /**
     * Add entities to the specified output collection that were part of a mount but not
     * teleported.
     *
     * @param output  The output collection.
     *
     * @return  The output collection.
     */
    <T extends Collection<Entity>> T getRejectedMounts(T output);

    /**
     * Get entities that were leashed but not teleported.
     */
    Collection<ITeleportLeashPair> getRejectedLeashed();

    /**
     * Add entities to the specified output collection that were leashed but not teleported.
     *
     * @param output  The output collection.
     *
     * @return  The output collection.
     */
    <T extends Collection<ITeleportLeashPair>> T getRejectedLeashed(T output);

    /**
     * Get all rejected entities.
     */
    Collection<Entity> getRejected();

    /**
     * Add all rejected entities to the specified output collection.
     *
     * @param output  The output collection.
     *
     * @return  The output collection.
     */
    <T extends Collection<Entity>> T getRejected(T output);

    /**
     * Get all entities processed.
     */
    Collection<Entity> getAll();

    /**
     * Add all entities processed to the specified output collection.
     *
     * @param output  The output collection.
     *
     * @return  The output collection.
     */
    <T extends Collection<Entity>> T getAll(T output);
}
