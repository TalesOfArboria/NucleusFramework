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

package com.jcwhatever.nucleus.providers.npc;

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.providers.npc.ai.INpcState;
import com.jcwhatever.nucleus.storage.IDataNode;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;

/**
 * Interface for an NPC.
 */
public interface INpc extends INpcState, INpcScriptEvents, IDisposable {

    /**
     * Get the NPC's owning registry.
     */
    INpcRegistry getRegistry();

    /**
     * Get the name used to uniquely identify an NPC within its own
     * registry.
     */
    String getLookupName();

    /**
     * Get the NPC's display name.
     *
     * <p>This is the name seen above the NPC.</p>
     */
    String getDisplayName();

    /**
     * Set the display name of the NPC.
     *
     * @param name  The display name.
     *
     * @return  Self for chaining.
     */
    INpc setDisplayName(String name);

    /**
     * Determine if the NPC's name-plate is visible.
     */
    boolean isDisplayNameVisible();

    /**
     * Set the NPC's name-plate visibility.
     *
     * @param isVisible  True to make name-plate visible, otherwise false.
     *
     * @return  Self for chaining.
     */
    INpc setDisplayNameVisible(boolean isVisible);

    /**
     * Spawn the NPC in the specified location.
     *
     * <p>If the NPC is already spawned, the NPC is teleported to the
     * new location.</p>
     *
     * <p>If the NPC is teleported, no NPC spawn events are called.</p>
     *
     * @param location  The location to spawn or teleport the NPC.
     *
     * @return  True if the NPC was spawned or teleported, otherwise false.
     */
    boolean spawn(Location location);

    /**
     * Despawn the NPC.
     *
     * <p>If the NPC is already despawned, no action is taken.</p>
     *
     * @return  True if despawned, otherwise false.
     */
    boolean despawn();

    /**
     * Get the NPC vehicle the {@link INpc} is riding.
     *
     * @return  The {@link INpc} vehicle or null if there is no vehicle or the vehicle
     * is not an {@link INpc} of the provider.
     */
    @Nullable
    INpc getNPCVehicle();

    /**
     * Get the NPC passenger of the {@link INpc}.
     *
     * @return  The {@link INpc} passenger or null if there is no passenger or the the passenger
     * is not an {@link INpc} of the provider.
     */
    @Nullable
    INpc getNPCPassenger();

    /**
     * Mount the current {@link INpc} as a passenger of the specified {@link INpc}.
     *
     * @param vehicle  The vehicle to mount.
     *
     * @return  Self for chaining.
     */
    INpc mountNPC(INpc vehicle);

    /**
     * Position the NPC's head.
     *
     * @param yaw    The yaw angle.
     * @param pitch  The pitch angle.
     *
     * @return  Self for chaining.
     */
    INpc look(float yaw, float pitch);

    /**
     * Make the NPC face the specified {@link Entity}.
     *
     * <p>If the entity is not in the same world or not alive, no action is taken.</p>
     *
     * @param entity  The entity to look at.
     *
     * @return  Self for chaining.
     */
    INpc lookEntity(Entity entity);

    /**
     * Make the NPC face a {@link org.bukkit.Location}.
     *
     * <p>If the location is not in the same world, no action is taken.</p>
     *
     * @param location  The location to face.
     *
     * @return  Self for chaining.
     */
    INpc lookLocation(Location location);

    /**
     * Save the {@link INpc} to a data node.
     *
     * @param dataNode  The data node to save to.
     *
     * @return  True if the {@link INpc} was saved.
     */
    boolean save(IDataNode dataNode);
}
