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

package com.jcwhatever.nucleus.events.spawnegg;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.events.HandlerListExt;
import com.jcwhatever.nucleus.mixins.ICancellable;
import com.jcwhatever.nucleus.utils.PreCon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.SpawnEgg;

/**
 * Called when a spawn egg is used, before the entity is spawned.
 */
public class PreSpawnEggEvent extends PlayerEvent implements Cancellable, ICancellable {

    private static final HandlerList handlers = new HandlerListExt(
            Nucleus.getPlugin(), PreSpawnEggEvent.class);

    private final ItemStack _spawnEgg;

    private boolean _isCancelled;

    /**
     * Constructor.
     *
     * @param player      The player that is using the spawn egg.
     * @param spawnEgg    The spawn egg item stack the player is using.
     */
    public PreSpawnEggEvent(Player player, ItemStack spawnEgg) {
        super(player);

        PreCon.notNull(player);
        PreCon.notNull(spawnEgg);

        _spawnEgg = spawnEgg;
    }

    /**
     * Get the spawn egg item stack being used.
     */
    public ItemStack getSpawnEgg() {
        return _spawnEgg;
    }

    /**
     * Get the type of entity being spawned.
     */
    public EntityType getEntityType() {
        ItemMeta meta = _spawnEgg.getItemMeta();
        if (meta instanceof SpawnEgg) {
            return ((SpawnEgg) meta).getSpawnedType();
        }
        return EntityType.UNKNOWN;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return _isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        _isCancelled = isCancelled;
    }
}
