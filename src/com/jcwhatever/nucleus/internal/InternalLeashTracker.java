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
import com.jcwhatever.nucleus.utils.CollectionUtils;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

/**
 * Tracks entities leashed to players.
 */
public class InternalLeashTracker implements Listener {

    private static final Map<UUID, Set<Entity>> _playerMap = new HashMap<>(35);
    private static final Map<Entity, UUID> _entityMap = new WeakHashMap<>(55);

    /**
     * Get all entities currently leashed to the player.
     *
     * @param player  The player to check.
     */
    public static Collection<Entity> getLeashed(Player player) {
        PreCon.notNull(player);

        synchronized (_playerMap) {
            Set<Entity> entities = _playerMap.get(player.getUniqueId());
            if (entities == null)
                return CollectionUtils.unmodifiableList(Entity.class);

            Set<Entity> result = new HashSet<>(entities.size());

            Iterator<Entity> iterator = entities.iterator();
            while (iterator.hasNext()) {

                Entity entity = iterator.next();

                if (entity.isDead() || !entity.isValid()) {
                    iterator.remove();
                }
                else {
                    result.add(entity);
                }
            }

            return CollectionUtils.unmodifiableSet(result);
        }
    }

    /**
     * Get the player an {@link org.bukkit.entity.Entity} is leashed to.
     *
     * @param entity  The {@link org.bukkit.entity.Entity} to check.
     *
     * @return  The {@link org.bukkit.entity.Player} the entity is leashed to
     * or null if the entity is not leashed or is leashed to a hitch.
     */
    @Nullable
    public static Player getLeashedTo(Entity entity) {
        UUID playerId = _entityMap.get(entity);

        return PlayerUtils.getPlayer(playerId);
    }

    /**
     * Perform initial Bukkit listener registration.
     */
    public static void registerListener() {
        _playerMap.clear();
        _entityMap.clear();
        Bukkit.getPluginManager().registerEvents(new InternalLeashTracker(), Nucleus.getPlugin());
    }

    private InternalLeashTracker() {}

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerQuit(PlayerQuitEvent event) {

        Set<Entity> set = _playerMap.remove(event.getPlayer().getUniqueId());
        if (set == null)
            return;

        for (Entity entity : set) {
            _entityMap.remove(entity);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerLeash(PlayerLeashEntityEvent event) {

        UUID playerId = event.getPlayer().getUniqueId();
        Entity leashHolder = event.getLeashHolder();

        if (event.getPlayer().equals(leashHolder))
            addPlayerLeash(playerId, event.getEntity());
        else {
            removePlayerLeash(playerId, event.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerUnleash(PlayerUnleashEntityEvent event) {
        removePlayerLeash(event.getPlayer().getUniqueId(), event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDeath(EntityDeathEvent event) {

        UUID playerId = _entityMap.remove(event.getEntity());
        if (playerId == null)
            return;

        _playerMap.remove(playerId);
    }

    @EventHandler(priority = EventPriority.MONITOR)
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
