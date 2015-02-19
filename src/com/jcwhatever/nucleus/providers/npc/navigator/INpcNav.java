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

package com.jcwhatever.nucleus.providers.npc.navigator;

import com.jcwhatever.nucleus.providers.npc.INpc;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;

/**
 * Interface for an NPC's pathing manager.
 */
public interface INpcNav extends INpcNavScriptEvents {

    /**
     * Get the owning NPC.
     */
    INpc getNpc();

    /**
     * Get the navigator settings applied to all navigation targets.
     */
    INpcNavSettings getSettings();

    /**
     * Get the navigator settings for the current pathing target.
     *
     * <p>When a target is set, the current settings are the settings
     * available from the method {@link #getSettings}. Changes to the
     * current settings only last until the current navigation ends.</p>
     */
    INpcNavSettings getCurrentSettings();

    /**
     * Determine if the navigator is currently running,
     * that is, that the NPC is currently pathing towards
     * a destination.
     */
    boolean isRunning();

    /**
     * Determine if the intention towards the current
     * pathing target is hostile.
     */
    boolean isHostile();

    /**
     * Determine if navigation targets are send to the
     * NPC vehicle, if any.
     */
    boolean isVehicleProxy();

    /**
     * Set the flag for sending navigation targets to the
     * NPC vehicle, if any.
     *
     * @param isProxy  True to send nav targets to NPC vehicle, otherwise false.
     *
     * @return  Self for chaining.
     */
    INpcNav setVehicleProxy(boolean isProxy);

    /**
     * Determine if the current pathing target is a location.
     */
    boolean isTargetingLocation();

    /**
     * Determine if the current pathing target is an entity.
     */
    boolean isTargetingEntity();

    /**
     * Get the current target as a location.
     *
     * @return  The current target location or null if there is no target.
     */
    @Nullable
    Location getTargetLocation();

    /**
     * Get the current target entity.
     *
     * @return  The current target entity or null if there is no target
     * or the target is not an entity.
     */
    @Nullable
    Entity getTargetEntity();

    /**
     * Start navigating using the current settings and target.
     *
     * @return  Self for chaining.
     */
    INpcNav start();

    /**
     * Pause the navigator. The navigator will resume when {@link #start} is invoked.
     *
     * @return  Self for chaining.
     */
    INpcNav pause();

    /**
     * Stop the navigator and clear all path state.
     *
     * @return  Self for chaining.
     */
    INpcNav cancel();

    /**
     * Set the current target location.
     *
     * @param location  The location.
     *
     * @return  Self for chaining.
     */
    INpcNav setTarget(Location location);

    /**
     * Set the current target entity.
     *
     * @param entity  The entity.
     *
     * @return  Self for chaining.
     */
    INpcNav setTarget(Entity entity);

    /**
     * Set the current hostile intention towards the target.
     *
     * @param isHostile  True to attack, False to simply move towards.
     *
     * @return  Self for chaining.
     */
    INpcNav setHostile(boolean isHostile);
}
