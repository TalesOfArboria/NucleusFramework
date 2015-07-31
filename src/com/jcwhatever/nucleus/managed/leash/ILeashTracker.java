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

package com.jcwhatever.nucleus.managed.leash;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Interface for the global leash tracker.
 */
public interface ILeashTracker {

    /**
     * Get all entities currently leashed to a player.
     *
     * @param player  The player to check.
     */
    Collection<Entity> getLeashed(Player player);

    /**
     * Get all entities currently leashed to a player.
     *
     * @param player  The player to check.
     * @param output  The output collection to put results into.
     *
     * @return  The output collection.
     */
    <T extends Collection<Entity>> T getLeashed(Player player, T output);

    /**
     * Get the player an {@link org.bukkit.entity.Entity} is leashed to.
     *
     * @param entity  The {@link org.bukkit.entity.Entity} to check.
     *
     * @return  The {@link org.bukkit.entity.Player} the entity is leashed to
     * or null if the entity is not leashed or is leashed to a hitch.
     */
    @Nullable
    Player getLeashedTo(Entity entity);

    /**
     * Allows manually registering a leashed entity to a player.
     *
     * <p>The entity must already be leashed to the player.</p>
     *
     * <p>Use when setting leashes in a manner that does not call Bukkit leash events.</p>
     *
     * @param player   The player that holds the leash.
     * @param leashed  The leashed entity.
     *
     * @return  True if registered, false if the entity is not leashed to the player.
     */
    boolean registerLeash(Player player, Entity leashed);
}
