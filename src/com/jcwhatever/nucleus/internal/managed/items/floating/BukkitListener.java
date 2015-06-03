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


package com.jcwhatever.nucleus.internal.managed.items.floating;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.CircularQueue;
import com.jcwhatever.nucleus.events.floatingitems.FloatingItemPickUpEvent;
import com.jcwhatever.nucleus.managed.items.floating.IFloatingItem;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.ChunkCoords;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class BukkitListener implements Listener {

    private Map<UUID, FloatingItem> _floatingItems = new HashMap<>(100);

    private Multimap<ChunkCoords, FloatingItem> _chunkMap =
            MultimapBuilder.hashKeys(100).hashSetValues(5).build();

    private Respawner _respawner = new Respawner();

    BukkitListener() {
        Scheduler.runTaskRepeat(Nucleus.getPlugin(), 1, 1, _respawner);
    }

    void register(FloatingItem item) {
        PreCon.notNull(item);

        _floatingItems.put(item.getUniqueId(), item);
    }

    void unregister(FloatingItem item) {
        PreCon.notNull(item);

        _floatingItems.remove(item.getUniqueId());
    }

    void registerPendingSpawn(FloatingItem item) {
        PreCon.notNull(item);
        PreCon.notNull(item.getLocation());

        _chunkMap.put(new ChunkCoords(item.getLocation().getChunk()), item);
    }

    void unregisterPendingSpawn(FloatingItem item) {
        PreCon.notNull(item);

        CollectionUtils.removeValue(_chunkMap, item);
    }

    @EventHandler
    private void onChunkLoad(ChunkLoadEvent event) {

        Collection<FloatingItem> items =
                _chunkMap.removeAll(new ChunkCoords(event.getChunk()));

        for (FloatingItem item : items) {
            if (item.getLocation() != null)
                item.spawn(item.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlayerTryPickup(final PlayerPickupItemEvent event) {

        final FloatingItem item = _floatingItems.get(event.getItem().getUniqueId());
        if (item == null)
            return;

        ItemStack itemToGive = event.getItem().getItemStack();

        // restore display name
        String displayName = item.getItem().getItemMeta().getDisplayName();
        ItemMeta meta = itemToGive.getItemMeta();
        meta.setDisplayName(displayName);
        itemToGive.setItemMeta(meta);

        // prevent or allow pickup
        if (!item.canPickup() && !item.isPickupSimulated()) {
            event.setCancelled(true);
        }

        FloatingItemPickUpEvent fiEvent = new FloatingItemPickUpEvent(item, event.getPlayer());
        fiEvent.setCancelled(event.isCancelled());

        Nucleus.getEventManager().callBukkit(this, fiEvent);

        event.setCancelled(fiEvent.isCancelled());

        item.onTryPickup(event.getPlayer());

        // check for cancelled event.
        if (event.isCancelled()) {

            // set meta back to uuid to prevent merging
            meta.setDisplayName(UUID.randomUUID().toString());
            itemToGive.setItemMeta(meta);
            return;
        }
        else if (item.isPickupSimulated()) {
            event.setCancelled(true);
            item.despawn();
        }

        final Location location = item.getLocation();

        if (location != null) {
            _respawner.queue.add(new RespawnEntry(item,
                    System.currentTimeMillis() + (item.getRespawnTimeSeconds() * 1000)));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerPickup(PlayerPickupItemEvent event) {

        final FloatingItem item = _floatingItems.get(event.getItem().getUniqueId());
        if (item == null)
            return;

        // call items pickup event callback handlers
        item.onPickup(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onItemDespawn(ItemDespawnEvent event) {

        final FloatingItem item = _floatingItems.get(event.getEntity().getUniqueId());
        if (item == null)
            return;

        if (item.isSpawned())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onItemDestroyed(EntityDamageEvent event) {
        if (event.getEntity().getType() != EntityType.DROPPED_ITEM)
            return;

        final FloatingItem item = _floatingItems.get(event.getEntity().getUniqueId());
        if (item == null)
            return;

        if (item.isSpawned()) {
            event.setDamage(0.0D);
            event.setCancelled(true);
        }
    }

    private static class Respawner implements Runnable {

        CircularQueue<RespawnEntry> queue = new CircularQueue<>();

        @Override
        public void run() {

            int size = queue.size();

            for (int i=0; i < size; i++) {

                RespawnEntry entry = queue.peekFirst();
                assert entry != null;

                if (entry.item.isDisposed() || entry.time <= System.currentTimeMillis()) {
                    queue.removeFirst();

                    if (!entry.item.isDisposed()) {
                        entry.item.spawn();
                    }
                }
                else {
                    queue.next();
                }
            }
        }
    }

    private static class RespawnEntry {
        long time;
        IFloatingItem item;
        RespawnEntry(IFloatingItem item, long time) {
            this.item = item;
            this.time = time;
        }
    }
}
