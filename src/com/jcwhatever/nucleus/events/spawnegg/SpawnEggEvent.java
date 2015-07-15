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

import com.jcwhatever.nucleus.utils.PreCon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Called when a player uses a spawn egg, after the entity is spawned.
 */
public class SpawnEggEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private final ItemStack _spawnEgg;
    private final Entity _entity;

    /**
     * Constructor.
     *
     * @param player    The player spawning the entity.
     * @param spawnEgg  The spawn egg used to spawn the entity.
     * @param entity    The spawned entity.
     */
    public SpawnEggEvent(Player player, ItemStack spawnEgg, Entity entity) {
        super(player);

        PreCon.notNull(player);
        PreCon.notNull(spawnEgg);
        PreCon.notNull(entity);

        _spawnEgg = spawnEgg;
        _entity = entity;
    }

    /**
     * Get the spawn egg being used.
     */
    public ItemStack getSpawnEgg() {
        return _spawnEgg;
    }

    /**
     * Get the entity that was spawned.
     */
    public Entity getSpawnedEntity() {
        return _entity;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

