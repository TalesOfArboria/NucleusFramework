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
import com.jcwhatever.nucleus.utils.PreCon;
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
        PreCon.notNull(plugin);
        PreCon.notNull(player);
        PreCon.notNull(location);
        PreCon.positiveNumber(tickDelay);

        ScheduledTeleport scheduled = _scheduled.get(player.getUniqueId());
        if (scheduled != null)
            scheduled.cancel();

        scheduled = new ScheduledTeleport(this, player, location);
        _scheduled.put(player.getUniqueId(), scheduled);

        Scheduler.runTaskLater(plugin, tickDelay, scheduled);

        return scheduled;
    }

    @Override
    public boolean teleport(Player player, Location location) {
        return teleport(player, location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @Override
    public boolean teleport(Player player, Location location,
                            PlayerTeleportEvent.TeleportCause cause) {
        PreCon.notNull(player);
        PreCon.notNull(location);
        PreCon.notNull(cause);

        ScheduledTeleport scheduled = _scheduled.remove(player.getUniqueId());
        if (scheduled != null)
            scheduled.cancel();

        return player.teleport(location, cause);
    }

    @Override
    public boolean teleport(Player player, Entity entity) {
        return teleport(player, entity, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @Override
    public boolean teleport(Player player, Entity entity,
                            PlayerTeleportEvent.TeleportCause cause) {
        PreCon.notNull(player);
        PreCon.notNull(entity);
        PreCon.notNull(cause);

        ScheduledTeleport scheduled = _scheduled.remove(player.getUniqueId());
        if (scheduled != null)
            scheduled.cancel();

        return player.teleport(entity, cause);
    }

    void removeTask(UUID playerId) {
        _scheduled.remove(playerId);
    }
}
