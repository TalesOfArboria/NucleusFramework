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
import com.jcwhatever.nucleus.events.teleport.PreTeleportEvent;
import com.jcwhatever.nucleus.events.teleport.TeleportEvent;
import com.jcwhatever.nucleus.managed.entity.meta.EntityMeta;
import com.jcwhatever.nucleus.managed.entity.meta.IEntityMetaContext;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.managed.teleport.ITeleportLeashPair;
import com.jcwhatever.nucleus.managed.teleport.ITeleportResult;
import com.jcwhatever.nucleus.managed.teleport.TeleportMode;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.LeashUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.utils.observer.future.FutureAgent;
import com.jcwhatever.nucleus.utils.observer.future.IFuture;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of {@link ITeleportResult} which also handles teleporting entities.
 */
class TeleportHandler implements ITeleportResult {

    private static final IEntityMetaContext META = EntityMeta.getContext(Nucleus.getPlugin());

    private final Entity _entity;
    private final List<Entity> _teleported = new ArrayList<>(3);
    private final TeleportCause _cause;
    private final TeleportMode _mode;
    private final Location _from = new Location(null, 0, 0, 0);
    private final Location _to = new Location(null, 0, 0, 0);

    // results
    private List<ITeleportLeashPair> _leashPairs;
    private Deque<Entity> _mounts;
    private List<Entity> _rejectedMounts;
    private List<ITeleportLeashPair> _rejectedLeashPairs;

    // result caches
    private List<Player> _players;
    private List<Entity> _rejected;

    private Status _status = Status.PENDING;
    private Status _result = Status.PENDING;
    private boolean _isMultiTick;
    private FutureAgent _agent;

    /**
     * Constructor.
     *
     * @param entity  The entity being teleported.
     * @param cause   The teleport cause.
     * @param mode    The teleport mode.
     */
    TeleportHandler(Entity entity, TeleportCause cause, TeleportMode mode) {

        _entity = entity;
        _cause = cause;
        _mode = mode;

        entity.getLocation(_from);

        // get all mounts
        Entity rootEntity = getRootEntity(entity);
        if (entity.equals(rootEntity)) {
            _teleported.add(entity);
        } else {
            _mounts = new ArrayDeque<>(3);
            Entity e = rootEntity;
            while (e != null) {

                boolean isEntity = e.equals(entity);

                if ((mode.isMountsTeleport() || isEntity) &&
                        (isEntity || !META.has(e, InternalTeleportManager.TELEPORT_DENY_META_NAME))) {
                    _teleported.add(e);
                    _mounts.add(e);
                }
                else {
                    if (_rejectedMounts == null)
                        _rejectedMounts = new ArrayList<>(3);

                    _rejectedMounts.add(e);
                }

                e = e.getPassenger();
            }
        }

        // find leashed entities
        for (Entity e : _teleported) {
            if (!(e instanceof Player))
                continue;

            Collection<Entity> leashed = LeashUtils.getLeashed((Player) e);

            for (Entity leash : leashed) {

                LeashPair pair = new LeashPair((LivingEntity) leash, (Player) e);
                if (mode.isLeashTeleport() &&
                        !META.has(leash, InternalTeleportManager.TELEPORT_DENY_META_NAME)) {

                    if (_leashPairs == null) {
                        _leashPairs = new ArrayList<>(3);
                    }
                    _leashPairs.add(pair);
                }
                else {
                    if (_rejectedLeashPairs == null)
                        _rejectedLeashPairs = new ArrayList<>(3);

                    _rejectedLeashPairs.add(pair);
                }
                pair.unleash();
            }
        }
        if (_leashPairs != null) {
            for (ITeleportLeashPair pair : _leashPairs)
                _teleported.add(pair.getLeashed());
        }
    }

    public TeleportHandler teleport(Location destination) {

        LocationUtils.copy(destination, _to);

        PreTeleportEvent event = new PreTeleportEvent(_entity, this, _from, _to);

        if (META.has(_entity, InternalTeleportManager.TELEPORT_DENY_META_NAME))
            event.setCancelled(true);

        Nucleus.getEventManager().callBukkit(this, event);
        if (event.isCancelled()) {
            _result = Status.CANCELLED;
            return this;
        }

        if (_mounts != null) {
            for (Entity e : _mounts) {
                addTeleporting(e, _to);
                e.eject();
            }
        }
        else {
            addTeleporting(_entity, _to);
        }

        for (Entity e : _teleported) {
            boolean result = e.teleport(_to, _cause);
            if (e.equals(_entity)) {
                _result = result ? Status.SUCCESS : Status.CANCELLED;
            }
        }

        if (_result == Status.PENDING) {
            // main entity not teleported
            _result = Status.CANCELLED;
        }

        if (_result == Status.CANCELLED) {
            removeTeleporting();
            return this;
        }

        if (_mounts == null && _leashPairs == null) {
            removeTeleporting();
            callCompleteEvent();
            return this;
        }

        _isMultiTick = true;
        _agent = new FutureAgent();

        Scheduler.runTaskLater(Nucleus.getPlugin(), 2, new Runnable() {
            @Override
            public void run() {
                mountAll();
                leashAll();
                _agent.success();
                removeTeleporting();
                callCompleteEvent();
            }
        });

        return this;
    }

    @Override
    public boolean isMultiTick() {
        return _isMultiTick;
    }

    @Override
    public TeleportCause getCause() {
        return _cause;
    }

    @Override
    public Status getStatus() {
        return _status;
    }

    @Override
    public boolean isSuccess() {
        return _result == Status.SUCCESS;
    }

    @Override
    public boolean isCancelled() {
        return _result == Status.CANCELLED;
    }

    @Override
    public Location getFrom() {
        return LocationUtils.copy(_from);
    }

    @Override
    public Location getFrom(Location output) {
        PreCon.notNull(output);

        return LocationUtils.copy(_from, output);
    }

    @Override
    public Location getTo() {
        return LocationUtils.copy(_to);
    }

    @Override
    public Location getTo(Location output) {
        PreCon.notNull(output);

        return LocationUtils.copy(_to, output);
    }

    @Override
    public TeleportMode getMode() {
        return _mode;
    }

    @Override
    public Entity getEntity() {
        return _entity;
    }

    @Override
    public Collection<Player> getPlayers() {
        return getPlayers(new ArrayList<Player>(0));
    }

    @Override
    public <T extends Collection<Player>> T getPlayers(T output) {
        PreCon.notNull(output);

        if (_players == null) {
            _players = new ArrayList<>(3);
            for (Entity e : _teleported) {
                if (e instanceof Player) {
                    _players.add((Player)e);
                }
            }
        }

        output.addAll(_players);
        return output;
    }

    @Override
    public Collection<Entity> getMounts() {
        if (_mounts == null) {
            return CollectionUtils.unmodifiableList();
        }
        return Collections.unmodifiableList(getMounts(new ArrayList<Entity>(_mounts.size())));
    }

    @Override
    public <T extends Collection<Entity>> T getMounts(T output) {
        PreCon.notNull(output);

        if (_mounts != null)
            output.addAll(_mounts);

        return output;
    }

    @Override
    public Collection<ITeleportLeashPair> getLeashed() {
        if (_leashPairs == null) {
            return CollectionUtils.unmodifiableList();
        }
        return Collections.unmodifiableList(_leashPairs);
    }

    @Override
    public <T extends Collection<ITeleportLeashPair>> T getLeashed(T output) {
        PreCon.notNull(output);

        if (_leashPairs != null)
            output.addAll(_leashPairs);

        return output;
    }

    @Override
    public Collection<Entity> getTeleports() {
        return Collections.unmodifiableList(_teleported);
    }

    @Override
    public <T extends Collection<Entity>> T getTeleports(T output) {
        PreCon.notNull(output);

        output.addAll(_teleported);
        return output;
    }

    @Override
    public Collection<Entity> getRejectedMounts() {
        if (_rejectedMounts == null)
            return CollectionUtils.unmodifiableList();

        return Collections.unmodifiableList(_rejectedMounts);
    }

    @Override
    public <T extends Collection<Entity>> T getRejectedMounts(T output) {
        PreCon.notNull(output);

        if (_rejectedMounts != null)
            output.addAll(_rejectedMounts);

        return output;
    }

    @Override
    public Collection<ITeleportLeashPair> getRejectedLeashed() {
        if (_rejectedLeashPairs == null)
            return CollectionUtils.unmodifiableList();

        return Collections.unmodifiableList(_rejectedLeashPairs);
    }

    @Override
    public <T extends Collection<ITeleportLeashPair>> T getRejectedLeashed(T output) {
        PreCon.notNull(output);

        if (_rejectedLeashPairs != null)
            output.addAll(_rejectedLeashPairs);

        return output;
    }

    @Override
    public Collection<Entity> getRejected() {
        return getRejected(new ArrayList<Entity>(0));
    }

    @Override
    public <T extends Collection<Entity>> T getRejected(T output) {
        PreCon.notNull(output);

        if (_rejected == null) {
            int size = (_rejectedMounts == null ? 0 : _rejectedMounts.size())
                    + (_rejectedLeashPairs == null ? 0 : _rejectedLeashPairs.size());

            _rejected = new ArrayList<>(size);

            if (_rejectedMounts != null)
                _rejected.addAll(_rejectedMounts);

            if (_rejectedLeashPairs != null) {
                for (ITeleportLeashPair pair : _rejectedLeashPairs)
                    _rejected.add(pair.getLeashed());
            }
        }

        output.addAll(_rejected);
        return output;
    }

    @Override
    public Collection<Entity> getAll() {
        return getAll(new ArrayList<Entity>(3));
    }

    @Override
    public <T extends Collection<Entity>> T getAll(T output) {
        PreCon.notNull(output);

        getTeleports(output);
        getRejected(output);
        return output;
    }

    @Override
    public IFuture getFuture() {
        if (_agent != null) {
            return _agent.getFuture();
        }

        switch (_status) {
            case SUCCESS:
                return new FutureAgent().success();
            case CANCELLED:
                return new FutureAgent().cancel();
            default:
                throw new AssertionError("Invalid status");
        }
    }

    private void callCompleteEvent() {
        TeleportEvent event = new TeleportEvent(_entity, this);
        Nucleus.getEventManager().callBukkit(this, event);
    }

    private void mountAll() {
        if (_mounts == null)
            return;

        Iterator<Entity> iterator = _mounts.descendingIterator();
        Entity previous = null;

        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            if (previous != null) {
                entity.setPassenger(previous);
            }
            previous = entity;
        }
    }

    private void leashAll() {
        if (_leashPairs == null)
            return;

        for (ITeleportLeashPair pair : _leashPairs) {
            ((LeashPair)pair).leash();
        }
    }

    private void addTeleporting(Entity entity, Location destination) {
        InternalTeleportManager.TELEPORTS.put(entity, null);
        if (!entity.getWorld().equals(destination.getWorld()))
            InternalTeleportManager.CROSS_WORLD_TELEPORTS.put(entity, null);
    }

    private void removeTeleporting() {
        for (Entity entity : _teleported) {
            InternalTeleportManager.TELEPORTS.remove(entity);
            InternalTeleportManager.CROSS_WORLD_TELEPORTS.remove(entity);
        }
        _status = _result;
    }

    private static Entity getRootEntity(Entity entity) {
        while (entity.getVehicle() != null) {
            entity = entity.getVehicle();
        }
        return entity;
    }

    private static class LeashPair implements ITeleportLeashPair {
        LivingEntity leashed;
        Player owner;
        LeashPair(LivingEntity leashed, Player owner) {
            this.leashed = leashed;
            this.owner = owner;
        }
        void leash() {
            leashed.setLeashHolder(owner);
        }
        void unleash() {
            leashed.setLeashHolder(null);
        }

        @Override
        public Entity getLeashed() {
            return leashed;
        }

        @Override
        public Entity getLeashHolder() {
            return owner;
        }
    }
}
