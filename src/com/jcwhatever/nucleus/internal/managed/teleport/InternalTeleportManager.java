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

package com.jcwhatever.nucleus.internal.managed.teleport;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.players.PlayerMap;
import com.jcwhatever.nucleus.events.teleport.TeleportScheduledEvent;
import com.jcwhatever.nucleus.managed.entity.meta.EntityMeta;
import com.jcwhatever.nucleus.managed.entity.meta.IEntityMetaContext;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.managed.teleport.IScheduledTeleport;
import com.jcwhatever.nucleus.managed.teleport.ITeleportManager;
import com.jcwhatever.nucleus.managed.teleport.ITeleportResult;
import com.jcwhatever.nucleus.managed.teleport.TeleportMode;
import com.jcwhatever.nucleus.utils.LeashUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Implementation of {@link ITeleportManager}.
 */
public final class InternalTeleportManager implements ITeleportManager {

    static final Map<Entity, Void> CROSS_WORLD_TELEPORTS = new WeakHashMap<>(10);
    static final Map<Entity, Void> TELEPORTS = new WeakHashMap<>(10);
    static final String TELEPORT_DENY_META_NAME =
            InternalTeleportManager.class.getName() + ":CanTeleport";
    private static final Object TELEPORT_DENY_META = new Object();
    private static final IEntityMetaContext META = EntityMeta.getContext(Nucleus.getPlugin());

    private static BukkitListener LISTENER;

    private final Map<UUID, ScheduledTeleport> _scheduled = new PlayerMap<>(Nucleus.getPlugin());

    public InternalTeleportManager() {
        if (LISTENER == null) {
            LISTENER = new BukkitListener();
            Bukkit.getPluginManager().registerEvents(LISTENER, Nucleus.getPlugin());
        }
    }

    @Override
    public boolean isTeleporting(Entity entity) {
        PreCon.notNull(entity);

        return TELEPORTS.containsKey(entity);
    }

    @Override
    public boolean isCrossWorldTeleporting(Entity entity) {
        PreCon.notNull(entity);

        return CROSS_WORLD_TELEPORTS.containsKey(entity);
    }

    @Nullable
    @Override
    public IScheduledTeleport getScheduled(Player player) {
        PreCon.notNull(player);

        return _scheduled.get(player.getUniqueId());
    }

    @Override
    public IScheduledTeleport teleport(Plugin plugin, Player player, Location location, int tickDelay) {
        return teleport(plugin, player, location, tickDelay, TeleportMode.MOUNTS_AND_LEASHED);
    }

    @Override
    public IScheduledTeleport teleport(Plugin plugin, Player player,
                                       Location location, int tickDelay, TeleportMode mode) {
        PreCon.notNull(plugin);
        PreCon.notNull(player);
        PreCon.notNull(location);
        PreCon.positiveNumber(tickDelay);
        PreCon.notNull(mode);

        ScheduledTeleport scheduled = _scheduled.get(player.getUniqueId());
        if (scheduled != null)
            scheduled.cancel();

        location = LocationUtils.copy(location);

        TeleportScheduledEvent event = new TeleportScheduledEvent(player, location, tickDelay);
        if (META.has(player, TELEPORT_DENY_META_NAME)) {
            event.setCancelled(true);
        }

        Nucleus.getEventManager().callBukkit(this, event);

        scheduled = new ScheduledTeleport(this, player, event.getDelayTicks(), location,
                TeleportCause.PLUGIN, mode);

        if (event.isCancelled()) {
            scheduled.cancel();
            return scheduled;
        }

        _scheduled.put(player.getUniqueId(), scheduled);
        Scheduler.runTaskLater(plugin, scheduled.getDelayTicks(), scheduled);

        return scheduled;
    }

    @Override
    public ITeleportResult teleport(Player player, Location location) {
        return teleport(player, location, TeleportCause.PLUGIN,
                TeleportMode.MOUNTS_AND_LEASHED);
    }

    @Override
    public ITeleportResult teleport(Player player, Location location, TeleportMode mode) {
        return teleport(player, location, TeleportCause.PLUGIN, mode);
    }

    @Override
    public ITeleportResult teleport(Player player, Location location,
                            TeleportCause cause) {
        return teleport(player, location, cause, TeleportMode.MOUNTS_AND_LEASHED);
    }

    @Override
    public ITeleportResult teleport(Player player, Location location,
                            TeleportCause cause, TeleportMode mode) {
        PreCon.notNull(player);
        PreCon.notNull(location);
        PreCon.notNull(cause);
        PreCon.notNull(mode);

        if (META.has(player, TELEPORT_DENY_META_NAME))
            return new TeleportHandler(player, cause, mode);

        ScheduledTeleport scheduled = _scheduled.remove(player.getUniqueId());
        if (scheduled != null)
            scheduled.cancel();

        return new TeleportHandler(player, cause, mode).teleport(location);
    }

    @Override
    public ITeleportResult teleport(Player player, Entity entity) {
        return teleport(player, entity.getLocation(), TeleportCause.PLUGIN,
                TeleportMode.MOUNTS_AND_LEASHED);
    }

    @Override
    public ITeleportResult teleport(Player player, Entity entity, TeleportMode mode) {
        return teleport(player, entity.getLocation(), TeleportCause.PLUGIN,
                TeleportMode.MOUNTS_AND_LEASHED);
    }

    @Override
    public ITeleportResult teleport(Player player, Entity entity,
                            TeleportCause cause) {
        return teleport(player, entity.getLocation(), cause, TeleportMode.MOUNTS_AND_LEASHED);
    }

    @Override
    public ITeleportResult teleport(Player player, Entity entity,
                            TeleportCause cause, TeleportMode mode) {
        return teleport(player, entity.getLocation(), cause, mode);
    }

    @Override
    public ITeleportResult teleport(Entity entity, Location location) {
        return teleport(entity, location, TeleportMode.MOUNTS_AND_LEASHED);
    }

    @Override
    public ITeleportResult teleport(Entity entity, Location location, TeleportMode mode) {
        PreCon.notNull(entity);
        PreCon.notNull(location);

        if (entity instanceof Player) {
            return teleport((Player)entity, location);
        }

        return new TeleportHandler(entity, TeleportCause.PLUGIN, mode);
    }

    @Override
    public boolean canTeleport(Entity entity) {
        PreCon.notNull(entity);

        return !META.has(entity, TELEPORT_DENY_META_NAME);
    }

    @Override
    public void setCanTeleport(Entity entity, boolean canTeleport) {
        PreCon.notNull(entity);

        META.set(entity, TELEPORT_DENY_META_NAME, canTeleport ? null : TELEPORT_DENY_META);
    }

    void removeTask(UUID playerId) {
        _scheduled.remove(playerId);
    }

    static boolean isSingleTeleport(Entity entity) {

        return !(entity instanceof Player &&
                !LeashUtils.getLeashed((Player) entity).isEmpty())
                && entity.getVehicle() == null && entity.getPassenger() == null;
    }

    private static class BukkitListener implements Listener {

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        private void onTeleport(EntityTeleportEvent event) {
            Entity entity = event.getEntity();
            if (META.has(entity, TELEPORT_DENY_META_NAME))
                event.setCancelled(true);
        }
    }
}
