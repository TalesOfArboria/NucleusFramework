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

package com.jcwhatever.nucleus.utils.entity;

import com.google.common.collect.MapMaker;
import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.regions.data.ChunkInfo;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.Map;
import java.util.UUID;

/**
 * Tracks entities and updates {@code TrackedEntity} objects.
 */
public final class EntityTracker implements Listener {

    private Map<UUID, TrackedEntity> _entities = new MapMaker().concurrencyLevel(3).initialCapacity(25).makeMap();

    /**
     * Constructor.
     */
    EntityTracker() {

        Bukkit.getPluginManager().registerEvents(this, Nucleus.getPlugin());
    }

    /**
     * Track a non-player entity.
     *
     * @param entity  The entity to track.
     *
     * @return  A new or cached {@code TrackedEntity} instance.
     */
    public TrackedEntity trackEntity(Entity entity) {

        if (entity instanceof Player)
            throw new IllegalArgumentException("Player entities cannot be tracked.");

        TrackedEntity tracked = _entities.get(entity.getUniqueId());
        if (tracked != null && !tracked.isDisposed())
            return tracked;

        tracked = new TrackedEntity(entity);
        _entities.put(entity.getUniqueId(), tracked);

        return tracked;
    }

    /**
     * Called when a {@code TrackedEntity} is disposed so it can
     * be removed from the entity map.
     */
    void disposeEntity(TrackedEntity tracked) {
        _entities.remove(tracked.getUniqueId());
    }

    /**
     * Handle chunk load
     */
    @EventHandler
    private void onChunkLoad(ChunkLoadEvent event) {

        // new chunks wont have any tracked entities.
        if (event.isNewChunk())
            return;

        Entity[] entities = event.getChunk().getEntities();

        for (Entity entity : entities) {

            TrackedEntity tracked = _entities.get(entity.getUniqueId());
            if (tracked == null || isDisposed(tracked))
                continue;

            // update entity instance to the latest
            tracked.updateEntity(entity);
        }
    }

    /**
     * Handle chunk unload
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void onChunkUnload(ChunkUnloadEvent event) {

        Entity[] entities = event.getChunk().getEntities();

        for (Entity entity : entities) {

            TrackedEntity tracked = _entities.get(entity.getUniqueId());
            if (tracked == null || isDisposed(tracked))
                continue;

            tracked.notifyChunkUnload(new ChunkInfo(event.getChunk()));
        }
    }

    /**
     * Handle entity removed.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void onEntityDestroy(EntityDeathEvent event) {

        // Make sure the event wasn't "cancelled"
        if (Double.compare(event.getEntity().getHealth(), 0.0D) != 0)
            return;

        TrackedEntity tracked = _entities.remove(event.getEntity().getUniqueId());
        if (tracked == null || tracked.isDisposed())
            return;

        // dispose tracked entity
        tracked.dispose();
    }

    private boolean isDisposed(TrackedEntity tracked) {
        if (tracked.isDisposed()) {
            _entities.remove(tracked.getUniqueId());
            return true;
        }
        return false;
    }
}