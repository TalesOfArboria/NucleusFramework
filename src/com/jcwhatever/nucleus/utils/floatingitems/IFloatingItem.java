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

package com.jcwhatever.nucleus.utils.floatingitems;

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.mixins.INamedInsensitive;
import com.jcwhatever.nucleus.storage.IDataNode;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import javax.annotation.Nullable;

/**
 * A dropped item.
 */
public interface IFloatingItem extends INamedInsensitive, IDisposable {

    /**
     * Get the floating {@code ItemStack}.
     */
    public ItemStack getItem();

    /**
     * Get the entities unique id.
     */
    public UUID getUniqueId();

    /**
     * Get the current item entity.
     *
     * @return  Null if not spawned.
     */
    @Nullable
    public Entity getEntity();

    /**
     * Get the floating items data node, if any.
     */
    @Nullable
    public IDataNode getDataNode();

    /**
     * Determine if the item is spawned as an entity.
     */
    public boolean isSpawned();

    /**
     * Get the location of the floating item.
     *
     * @return  Null if no location is set yet.
     */
    @Nullable
    public Location getLocation();

    /**
     * Determine if the item can be picked up.
     */
    public boolean canPickup();

    /**
     * Set if the item can be picked up.
     *
     * @param canPickup  True to allow players to pickup the item.
     */
    public void setCanPickup(boolean canPickup);

    /**
     * Determine if the item is spawned centered within
     * the block at the spawn location.
     */
    public boolean isCentered();

    /**
     * Set item spawned centered within the block
     * at the spawn location.
     *
     * @param isCentered  True to center.
     */
    public void setCentered(boolean isCentered);

    /**
     * Get the number of seconds before the item is respawned
     * after being picked up.
     */
    public int getRespawnTimeSeconds();

    /**
     * Set the number of seconds before the item is respawned
     * after being picked up.
     *
     * @param seconds  The number of seconds.
     */
    public void setRespawnTimeSeconds(int seconds);

    /**
     * Spawn the floating item entity.
     */
    public boolean spawn();

    /**
     * Spawn the floating item entity.
     */
    public boolean spawn(Location location);

    /**
     * Despawn the floating item entity.
     */
    public boolean despawn();

    /**
     * Give a copy of the item to a player.
     *
     * @param p  The player.
     */
    public boolean give(Player p);
}
