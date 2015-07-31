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
import com.jcwhatever.nucleus.managed.entity.mob.ISerializableMob;
import com.jcwhatever.nucleus.managed.entity.mob.Mobs;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.managed.teleport.TeleportMode;
import com.jcwhatever.nucleus.utils.LeashUtils;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.utils.player.PlayerStateSnapshot;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;


/*
 * Tracks passenger/vehicle relationships and restores them
 * after transport.
 */
class MountTeleporter {

    private static TeleportListener _listener;
    private static boolean _isSpawningNewEntity;
    private static Set<Chunk> _keepLoaded = new HashSet<>(10);

    private final Entity _entity;
    private final TeleportMode _mode;

    /**
     * Constructor.
     *
     * @param entity  The entity to teleport
     */
    MountTeleporter(Entity entity, TeleportMode mode) {

        _entity = entity;
        _mode = mode;

        if (_listener == null) {
            _listener = new TeleportListener();
            Bukkit.getPluginManager().registerEvents(_listener, Nucleus.getPlugin());
        }
    }

    boolean teleport(Location destination, PlayerTeleportEvent.TeleportCause cause) {

        destination = LocationUtils.copy(destination).add(0, 0.1, 0);

        _keepLoaded.add(destination.getChunk());
        _keepLoaded.add(_entity.getLocation().getChunk());
        destination.getChunk().load();

        Mounts mounts = dismountAll();

        while (!mounts.players.isEmpty()) {
            mounts.players.remove().teleport(destination);
        }

        if (_mode.isMountsTeleport() || _mode.isLeashTeleport()) {
            Scheduler.runTaskLater(Nucleus.getPlugin(), 5, new SpawnMobs(mounts, destination, cause));
        }
        else if (!(_entity instanceof Player)) {
            _entity.teleport(destination);
        }
        return true;
    }

    private class SpawnMobs implements Runnable {

        Mounts mounts;
        Location destination = new Location(null, 0, 0, 0);
        PlayerTeleportEvent.TeleportCause cause;

        SpawnMobs(Mounts mounts, Location destination, PlayerTeleportEvent.TeleportCause cause) {
            LocationUtils.copy(destination, this.destination);
            this.mounts = mounts;
            this.cause = cause;
        }

        @Override
        public void run() {

            _isSpawningNewEntity = true;

            for (int i=0; i < mounts.mounts.length; i++) {

                Object obj = mounts.mounts[i];

                if (obj instanceof ISerializableMob) {
                    mounts.mounts[i] = ((ISerializableMob) obj).spawn(destination);
                }
            }

            for (LeashPair pair : mounts.leashes) {
                pair.spawned = pair.leashed.spawn(destination);
                if (!(pair.spawned instanceof LivingEntity))
                    continue;

                ((LivingEntity) pair.spawned).setLeashHolder(pair.player);
            }

            _isSpawningNewEntity = false;

            Scheduler.runTaskLater(Nucleus.getPlugin(), 5, new DelayedMount(mounts, destination));
        }
    }

    private class DelayedMount implements Runnable {

        Mounts mounts;
        Location destination = new Location(null, 0, 0, 0);

        DelayedMount(Mounts mounts, Location destination) {
            LocationUtils.copy(destination, this.destination);
            this.mounts = mounts;
        }

        @Override
        public void run() {
            mountAll(mounts.mounts, destination);

            for (LeashPair pair : mounts.leashes) {
                Nucleus.getLeashTracker().registerLeash(pair.player, pair.spawned);
            }

            // preserve player game mode and flight
            while (!mounts.snapshots.isEmpty()) {
                PlayerStateSnapshot snapshot = mounts.snapshots.remove();
                Player player = PlayerUtils.getPlayer(snapshot.getPlayerId());
                if (player == null)
                    continue;

                player.setGameMode(snapshot.getGameMode());
                player.setFlying(snapshot.isFlying());
                player.setAllowFlight(snapshot.isFlightAllowed());
            }

            _keepLoaded.remove(destination.getChunk());
            _keepLoaded.remove(_entity.getLocation().getChunk());
        }
    }

    /*
     * Represents a leash from a player to an entity.
     */
    private static class LeashPair {
        Player player;
        ISerializableMob leashed;
        Entity spawned;

        LeashPair(Player player, ISerializableMob leashed) {
            this.player = player;
            this.leashed = leashed;
        }
    }

    private static class TeleportListener implements Listener {

        // prevent spawn restrictions from preventing a mounted mob from
        // being teleported with a player.
        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        private void onMountedEntitySpawn(EntitySpawnEvent event) {
            if ( _isSpawningNewEntity)
                event.setCancelled(false);
        }

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        private void onMountedEntityTeleport(EntityTeleportEvent event) {
            if ( _isSpawningNewEntity)
                event.setCancelled(false);
        }

        // prevent spawn restrictions from preventing a mounted mob from
        // being teleported with a player.
        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        private void onChunkUnload(ChunkUnloadEvent event) {
            if (_keepLoaded.contains(event.getChunk()))
                event.setCancelled(true);
        }
    }

    private static class Mounts {
        Object[] mounts;
        LinkedList<Player> players = new LinkedList<>();
        LinkedList<PlayerStateSnapshot> snapshots = new LinkedList<>();
        LinkedList<LeashPair> leashes = new LinkedList<>();
        LinkedList<ISerializableMob> mobs = new LinkedList<>();
    }


    private static void mountAll(Object[] mounts, Location mountLocation) {

        _isSpawningNewEntity = true;
        Entity entity = null;

        for (Object obj : mounts) {

            Entity passenger = null;

            if (obj instanceof Entity) {
                passenger = (Entity) obj;
            } else if (obj instanceof ISerializableMob) {

                passenger = ((ISerializableMob) obj).spawn(mountLocation);
            }

            if (entity != null) {
                entity.setPassenger(passenger);
            }

            entity = passenger;
        }

        _isSpawningNewEntity = false;
    }

    private Mounts dismountAll() {

        Mounts result = new Mounts();
        LinkedList<Object> mounts = new LinkedList<>();

        Entity entity = _entity;

        while (entity != null) {

            Entity passenger = entity.getPassenger();

            if (entity instanceof Player) {

                if (_mode.isMountsTeleport() || entity.equals(_entity)) {
                    result.players.add((Player) entity);
                    mounts.add(entity);
                    result.snapshots.add(new PlayerStateSnapshot((Player) entity));
                    entity.eject();

                    // get leashed entities
                    Collection<Entity> leashed = LeashUtils.getLeashed((Player) entity);

                    for (Entity leashEntity : leashed) {

                        if (_mode.isLeashTeleport()) {
                            ISerializableMob mob = Mobs.getSerializable(leashEntity);
                            if (mob == null)
                                continue;

                            result.leashes.add(new LeashPair((Player) entity, mob));
                            ((LivingEntity) leashEntity).setLeashHolder(null);
                            leashEntity.remove();
                        } else {
                            ((LivingEntity) leashEntity).setLeashHolder(null);
                        }
                    }
                }
            }
            else {

                if (_mode.isMountsTeleport() || entity.equals(_entity)) {
                    if (entity instanceof LivingEntity) {
                        ISerializableMob mob = Mobs.getSerializable(entity);
                        if (mob != null) {
                            mounts.add(mob);
                            result.mobs.add(mob);
                        }
                        entity.eject();
                        entity.remove();
                    } else {
                        mounts.add(entity);
                        entity.eject();
                    }
                }
                else {
                    entity.eject();
                }
            }

            entity = passenger;
        }

        result.mounts = mounts.toArray();
        return result;
    }
}
