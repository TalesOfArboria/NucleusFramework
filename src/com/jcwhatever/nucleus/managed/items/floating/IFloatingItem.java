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

package com.jcwhatever.nucleus.managed.items.floating;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.mixins.INamedInsensitive;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.utils.observer.update.IUpdateSubscriber;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * A dropped item.
 *
 * @see IFloatingItemManager
 * @see Nucleus#getFloatingItems
 */
public interface IFloatingItem extends IPluginOwned, INamedInsensitive, IDisposable {

    /**
     * Get the entities unique id.
     */
    UUID getUniqueId();

    /**
     * Get the floating {@link org.bukkit.inventory.ItemStack}.
     */
    ItemStack getItem();

    /**
     * Get the current entity.
     *
     * <p>Not guaranteed to be specific type of entity.</p>
     *
     * @return  The {@link Entity} or null if not spawned.
     */
    @Nullable
    Entity getEntity();

    /**
     * Determine if the item is spawned.
     */
    boolean isSpawned();

    /**
     * Get the location of the floating item.
     *
     * @return  The {@link Location} or null if a location isn't set yet.
     */
    @Nullable
    Location getLocation();

    /**
     * Copy the values of the items location to the output {@link Location}.
     *
     * @param output  The output {@link Location}.
     *
     * @return  The output {@link Location} or null if a location isn't set yet.
     */
    @Nullable
    Location getLocation(Location output);

    /**
     * Determine if the item can be picked up by players.
     */
    boolean canPickup();

    /**
     * Set if the item can be picked up by players.
     *
     * @param canPickup  True to allow players to pickup the item.
     */
    void setCanPickup(boolean canPickup);

    /**
     * Determine if pickup is simulated.
     *
     * <p>Simulated pickup is where the item appears to have been picked
     * up but does not end up in the players inventory.</p>
     *
     * <p>Value is ignored if {@link #canPickup} returns true.</p>
     */
    boolean isPickupSimulated();

    /**
     * Set pickup simulation.
     *
     * <p>Simulated pickup is where the item appears to have been picked
     * up but does not end up in the players inventory.</p>
     *
     * <p>Value is ignored if {@link #canPickup} returns true.</p>
     *
     * @param isPickupSimulated  True to simulate pickup, otherwise false.
     */
    void setPickupSimulated(boolean isPickupSimulated);

    /**
     * Determine if the item is spawned centered within the block at
     * the spawn location.
     */
    boolean isCentered();

    /**
     * Set item spawned centered within the block at the spawn location.
     *
     * @param isCentered  True to center, false to use exact location.
     */
    void setCentered(boolean isCentered);

    /**
     * Get the number of seconds before the item is respawned
     * after being picked up.
     */
    int getRespawnTimeSeconds();

    /**
     * Set the number of seconds before the item is respawned
     * after being picked up.
     *
     * @param seconds  The number of seconds.
     */
    void setRespawnTimeSeconds(int seconds);

    /**
     * Spawn the floating item entity at the current location.
     *
     * @return True if the item was spawned, false if failed or a location
     * is not set yet.
     */
    boolean spawn();

    /**
     * Spawn the floating item entity at a location.
     *
     * @param location  The location to spawn the item at.
     *
     * @return  True if the item was spawned, otherwise false.
     */
    boolean spawn(Location location);

    /**
     * Despawn the floating item entity.
     *
     * <p>The items spawn location is retained.</p>
     *
     * @return  True if the item was despawned, otherwise false.
     */
    boolean despawn();

    /**
     * Give a copy of the item to a player.
     *
     * @param player  The player.
     */
    boolean give(Player player);

    /**
     * Get updated when the item is spawned.
     *
     * @param subscriber  The update subscriber.
     *
     * @return  Self for chaining.
     */
    IFloatingItem onSpawn(IUpdateSubscriber<Entity> subscriber);

    /**
     * Get updated when the item is despawned.
     *
     * @param subscriber  The update subscriber.
     *
     * @return  Self for chaining.
     */
    IFloatingItem onDespawn(IUpdateSubscriber<Entity> subscriber);

    /**
     * Get updated when the item is picked up by a player.
     *
     * @param subscriber  The update subscriber. The subscriber will receive the player
     *                    that was detected picking up the item.
     *
     * @return  Self for chaining.
     */
    IFloatingItem onPickup(IUpdateSubscriber<Player> subscriber);

    /**
     * Get updated when an item pick up is attempted by a player.
     *
     * <p>Is called even if the item cannot be picked up.</p>
     *
     * @param subscriber  The update subscriber. The subscriber will receive the player
     *                    that was detected attempting to pick up the item.
     *
     * @return  Self for chaining.
     */
    IFloatingItem onTryPickup(IUpdateSubscriber<Player> subscriber);
}
