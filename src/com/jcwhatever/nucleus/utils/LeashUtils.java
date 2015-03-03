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

import com.jcwhatever.nucleus.internal.InternalLeashTracker;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LeashHitch;
import org.bukkit.entity.Player;

import java.util.Collection;
import javax.annotation.Nullable;

/**
 * Leash (Lead) utilities.
 */
public class LeashUtils {

    private LeashUtils() {}

    /**
     * Determine if an entity is leashed to a player or hitch.
     *
     * @param entity  The {@link org.bukkit.entity.Entity} to check.
     */
    public static boolean isLeashed(Entity entity) {
        PreCon.notNull(entity);

        return InternalLeashTracker.isLeashed(entity);
    }

    /**
     * Determine if an entity is hitched.
     *
     * @param entity  The {@link org.bukkit.entity.Entity} to check.
     */
    public static boolean isHitched(Entity entity) {
        PreCon.notNull(entity);

        return InternalLeashTracker.isHitched(entity);
    }

    /**
     * Get entities that the player currently has a leash on.
     *
     * @param player  The player to check.
     */
    public static Collection<Entity> getLeashed(Player player) {
        PreCon.notNull(player);

        return InternalLeashTracker.getLeashed(player);
    }

    /**
     * Get the {@link org.bukkit.entity.LeashHitch} of a leashed entity.
     *
     * @param entity  The {@link org.bukkit.entity.Entity} to check.
     *
     * @return  The {@link org.bukkit.entity.LeashHitch} or null if the entity
     * is not leashed or is leashed to a player.
     */
    @Nullable
    public static LeashHitch getHitch(Entity entity) {
        PreCon.notNull(entity);

        return InternalLeashTracker.getHitch(entity);
    }

    /**
     * Get the player an {@link org.bukkit.entity.Entity} is leashed to.
     *
     * @param entity  The {@link org.bukkit.entity.Entity} to check.
     *
     * @return  The {@link org.bukkit.entity.Player} the entity is leashed to
     * or null if the entity is not leashed or is leashed to a hitch.
     */
    @Nullable
    public static Player getLeashedTo(Entity entity) {
        PreCon.notNull(entity);

        return InternalLeashTracker.getLeashedTo(entity);
    }
}
