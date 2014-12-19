/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

package com.jcwhatever.bukkit.generic.utils.entity;

import com.jcwhatever.bukkit.generic.GenericsLib;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks entities and updates {@code TrackedEntity} objects.
 */
public final class EntityTracker implements Listener {

    private Map<UUID, TrackedEntity> _entities = new HashMap<>(50);

    EntityTracker() {
        Bukkit.getPluginManager().registerEvents(this, GenericsLib.getPlugin());
    }

    public TrackedEntity trackEntity(Entity entity) {

        if (entity instanceof Player) {
            // Tracking players is nice for player NPC's, but difficult. The NPC
            // plugin should handle this.
            throw new IllegalArgumentException("Player entities cannot be tracked.");
        }

        TrackedEntity tracked = _entities.get(entity.getUniqueId());
        if (tracked != null && !tracked.isDisposed())
            return tracked;

        tracked = new TrackedEntity(entity);
        _entities.put(entity.getUniqueId(), tracked);

        return tracked;
    }

    void untrackEntity(TrackedEntity tracked) {
        _entities.remove(tracked.getUniqueId());
    }

    @EventHandler
    private void onChunkLoad(ChunkLoadEvent event) {
        if (event.isNewChunk())
            return;

        Entity[] entities = event.getChunk().getEntities();

        for (Entity entity : entities) {
            TrackedEntity tracked = _entities.get(entity.getUniqueId());
            if (tracked == null || isDisposed(tracked))
                continue;

            tracked.setEntity(entity);
            tracked.onChunkLoad();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onChunkUnload(ChunkUnloadEvent event) {
        if (event.isCancelled())
            return;

        Entity[] entities = event.getChunk().getEntities();

        for (Entity entity : entities) {
            TrackedEntity tracked = _entities.get(entity.getUniqueId());
            if (tracked == null || isDisposed(tracked))
                continue;

            tracked.onChunkUnload();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onEntityTeleport(EntityTeleportEvent event) {
        if (event.isCancelled())
            return;

        TrackedEntity tracked = _entities.get(event.getEntity().getUniqueId());
        if (tracked == null || isDisposed(tracked))
            return;

        tracked.setWorld(event.getTo().getWorld());
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onEntityDestroy(EntityDeathEvent event) {

        // Make sure the event wasn't "cancelled"
        if (Double.compare(event.getEntity().getHealth(), 0.0D) != 0)
            return;

        TrackedEntity tracked = _entities.remove(event.getEntity().getUniqueId());
        if (tracked == null || tracked.isDisposed())
            return;

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