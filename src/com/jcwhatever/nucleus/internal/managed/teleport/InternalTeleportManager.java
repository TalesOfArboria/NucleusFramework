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
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.managed.teleport.IScheduledTeleport;
import com.jcwhatever.nucleus.managed.teleport.ITeleportManager;
import com.jcwhatever.nucleus.managed.teleport.TeleportMode;
import com.jcwhatever.nucleus.utils.LeashUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.entity.EntityUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of {@link ITeleportManager}.
 */
public final class InternalTeleportManager implements ITeleportManager {

    private Map<UUID, ScheduledTeleport> _scheduled = new PlayerMap<>(Nucleus.getPlugin());

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

        scheduled = new ScheduledTeleport(this, player, location,
                PlayerTeleportEvent.TeleportCause.PLUGIN, mode);

        _scheduled.put(player.getUniqueId(), scheduled);

        Scheduler.runTaskLater(plugin, tickDelay, scheduled);

        return scheduled;
    }

    @Override
    public boolean teleport(Player player, Location location) {
        return teleport(player, location, PlayerTeleportEvent.TeleportCause.PLUGIN,
                TeleportMode.MOUNTS_AND_LEASHED);
    }

    @Override
    public boolean teleport(Player player, Location location, TeleportMode mode) {
        return teleport(player, location, PlayerTeleportEvent.TeleportCause.PLUGIN,
                TeleportMode.MOUNTS_AND_LEASHED);
    }

    @Override
    public boolean teleport(Player player, Location location,
                            PlayerTeleportEvent.TeleportCause cause) {
        return teleport(player, location, cause, TeleportMode.MOUNTS_AND_LEASHED);
    }

    @Override
    public boolean teleport(Player player, Location location,
                            PlayerTeleportEvent.TeleportCause cause, TeleportMode mode) {
        PreCon.notNull(player);
        PreCon.notNull(location);
        PreCon.notNull(cause);
        PreCon.notNull(mode);

        ScheduledTeleport scheduled = _scheduled.remove(player.getUniqueId());
        if (scheduled != null)
            scheduled.cancel();

        return isSingleTeleport(player)
                ? player.teleport(location, cause)
                : mountedTeleport(player, location, cause, mode);
    }

    @Override
    public boolean teleport(Player player, Entity entity) {
        return teleport(player, entity.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN,
                TeleportMode.MOUNTS_AND_LEASHED);
    }

    @Override
    public boolean teleport(Player player, Entity entity, TeleportMode mode) {
        return teleport(player, entity.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN,
                TeleportMode.MOUNTS_AND_LEASHED);
    }

    @Override
    public boolean teleport(Player player, Entity entity,
                            PlayerTeleportEvent.TeleportCause cause) {
        return teleport(player, entity.getLocation(), cause, TeleportMode.MOUNTS_AND_LEASHED);
    }

    @Override
    public boolean teleport(Player player, Entity entity,
                            PlayerTeleportEvent.TeleportCause cause, TeleportMode mode) {
        return teleport(player, entity.getLocation(), cause, mode);
    }

    @Override
    public boolean teleport(Entity entity, Location location) {
        return teleport(entity, location, TeleportMode.MOUNTS_AND_LEASHED);
    }

    @Override
    public boolean teleport(Entity entity, Location location, TeleportMode mode) {
        PreCon.notNull(entity);
        PreCon.notNull(location);

        if (entity instanceof Player) {
            return teleport((Player)entity, location);
        }

        return isSingleTeleport(entity)
                ? entity.teleport(location)
                : mountedTeleport(entity, location, PlayerTeleportEvent.TeleportCause.PLUGIN, mode);
    }

    void removeTask(UUID playerId) {
        _scheduled.remove(playerId);
    }

    static boolean mountedTeleport(Entity entity, Location destination,
                                   PlayerTeleportEvent.TeleportCause cause, TeleportMode mode) {

        Entity rootVehicle = EntityUtils.getRootVehicle(entity);

        if (!mode.isLeashTeleport() && !mode.isMountsTeleport()) {
            entity.eject();
            Entity vehicle = entity.getVehicle();
            if (vehicle != null)
                vehicle.eject();

            entity.teleport(destination, cause);
        }

        return new MountTeleporter(rootVehicle, mode)
                .teleport(destination, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    static boolean isSingleTeleport(Entity entity) {

        return !(entity instanceof Player &&
                !LeashUtils.getLeashed((Player) entity).isEmpty())
                && entity.getVehicle() == null && entity.getPassenger() == null;
    }
}
