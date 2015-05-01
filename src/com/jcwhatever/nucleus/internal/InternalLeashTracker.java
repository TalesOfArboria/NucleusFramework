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

package com.jcwhatever.nucleus.internal;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.WeakHashSet;
import com.jcwhatever.nucleus.managed.leash.ILeashTracker;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

/**
 * Internal implementation of {@link ILeashTracker}.
 */
public class InternalLeashTracker implements ILeashTracker, Listener {

    private final Map<UUID, Set<Entity>> _playerMap = new HashMap<>(35);
    private final Map<Entity, UUID> _entityMap = new WeakHashMap<>(55);

    public InternalLeashTracker() {
        Bukkit.getPluginManager().registerEvents(this, Nucleus.getPlugin());
    }

    @Override
    public Collection<Entity> getLeashed(Player player) {
        return getLeashed(player, new ArrayList<Entity>(10));
    }

    @Override
    public <T extends Collection<Entity>> T getLeashed(Player player, T output) {
        PreCon.notNull(player);
        PreCon.notNull(output);

        synchronized (_playerMap) {
            Set<Entity> entities = _playerMap.get(player.getUniqueId());
            if (entities == null)
                return output;

            Iterator<Entity> iterator = entities.iterator();
            while (iterator.hasNext()) {

                Entity entity = iterator.next();

                if (entity.isDead() || !entity.isValid()) {
                    iterator.remove();
                }
                else {
                    output.add(entity);
                }
            }
        }

        return output;
    }

    @Override
    @Nullable
    public Player getLeashedTo(Entity entity) {
        UUID playerId = _entityMap.get(entity);

        return PlayerUtils.getPlayer(playerId);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerQuit(PlayerQuitEvent event) {

        Set<Entity> set = _playerMap.remove(event.getPlayer().getUniqueId());
        if (set == null)
            return;

        for (Entity entity : set) {
            _entityMap.remove(entity);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerLeash(PlayerLeashEntityEvent event) {

        UUID playerId = event.getPlayer().getUniqueId();
        Entity leashHolder = event.getLeashHolder();

        if (event.getPlayer().equals(leashHolder))
            addPlayerLeash(playerId, event.getEntity());
        else {
            removePlayerLeash(playerId, event.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerUnleash(PlayerUnleashEntityEvent event) {
        removePlayerLeash(event.getPlayer().getUniqueId(), event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDeath(EntityDeathEvent event) {

        // check for "cancelled" event
        if (event.getEntity().getHealth() > 0)
            return;

        UUID playerId = _entityMap.remove(event.getEntity());
        if (playerId == null)
            return;

        _playerMap.remove(playerId);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onCreatureSpawn(CreatureSpawnEvent event) {

        if (!event.getEntity().isLeashed())
            return;

        Entity leashHolder = event.getEntity().getLeashHolder();

        if (leashHolder instanceof Player) {
            addPlayerLeash(leashHolder.getUniqueId(), event.getEntity());
        }
    }

    private void addPlayerLeash(UUID playerId, Entity leashed) {

        synchronized (_playerMap) {
            Set<Entity> set = _playerMap.get(playerId);
            if (set == null) {
                set = new WeakHashSet<>(5);
                _playerMap.put(playerId, set);
            }

            set.add(leashed);
        }
    }

    private void removePlayerLeash(UUID playerId, Entity unleashed) {

        synchronized (_playerMap) {
            Set<Entity> set = _playerMap.get(playerId);
            if (set == null)
                return;

            set.remove(unleashed);
        }
    }
}
