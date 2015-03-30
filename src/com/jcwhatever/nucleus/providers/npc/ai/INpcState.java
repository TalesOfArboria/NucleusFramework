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

package com.jcwhatever.nucleus.providers.npc.ai;

import com.jcwhatever.nucleus.providers.npc.ai.goals.INpcGoals;
import com.jcwhatever.nucleus.providers.npc.navigator.INpcNav;
import com.jcwhatever.nucleus.providers.npc.traits.INpcTraits;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;

/**
 * Used to store and retrieve state information about a {@link com.jcwhatever.nucleus.providers.npc.INpc}.
 */
public interface INpcState {

    /**
     * Get the NPC entity.
     *
     * @return  The {@link org.bukkit.entity.Entity} or null if not spawned.
     */
    @Nullable
    Entity getEntity();

    /**
     * Determine if the NPC is spawned.
     */
    boolean isSpawned();

    /**
     * Get the NPC entity location or the location the entity was add
     * when last despawned. If the entity was never spawned, returns null.
     */
    @Nullable
    Location getLocation();

    /**
     * Get the NPC entity location or the location the entity was add
     * when last despawned. If the entity was never spawned, returns null.
     *
     * <p>Copies the result location values into the provided location instance.</p>
     *
     * @param location  The location to copy result values into.
     *
     * @return  The location that was provided as an argument or null if the entity was never spawned.
     */
    @Nullable
    Location getLocation(Location location);

    /**
     * Get the NPC's navigator.
     */
    INpcNav getNavigator();

    /**
     * Get the NPC's goal manager.
     */
    INpcGoals getGoals();

    /**
     * Get the NPC's trait manager.
     */
    INpcTraits getTraits();

    /**
     * Get a stored meta state object.
     *
     * @param key  The meta key.
     */
    Object getMeta(String key);

    /**
     * Set a meta state object.
     *
     * @param key    The meta key.
     * @param value  The value.
     */
    void setMeta(String key, Object value);
}
