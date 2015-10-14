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

package com.jcwhatever.nucleus.events.teleport;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.events.HandlerListExt;
import com.jcwhatever.nucleus.managed.teleport.ITeleportInfo;
import com.jcwhatever.nucleus.managed.teleport.ITeleportLeashPair;
import com.jcwhatever.nucleus.managed.teleport.TeleportMode;
import com.jcwhatever.nucleus.mixins.ICancellable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Collection;

/**
 * Called when an entity is teleported via Nucleus.
 *
 * @see com.jcwhatever.nucleus.managed.teleport.Teleporter
 * @see com.jcwhatever.nucleus.managed.teleport.ITeleportManager
 */
public class PreTeleportEvent extends EntityEvent implements ITeleportInfo, ICancellable, Cancellable {

    private static final HandlerList handlers = new HandlerListExt(
            Nucleus.getPlugin(), PreTeleportEvent.class);

    private final ITeleportInfo _info;
    private final Location _from;
    private final Location _to;
    private boolean _isCancelled;

    /**
     * Constructor.
     *
     * @param teleported    The entity teleported.
     * @param teleportInfo  The entity teleport information.
     * @param from          The location the entity is being teleported from.
     * @param to            The location the entity is being teleported to.
     */
    public PreTeleportEvent(Entity teleported, ITeleportInfo teleportInfo, Location from, Location to) {
        super(teleported);
        PreCon.notNull(teleported);
        PreCon.notNull(teleportInfo);
        PreCon.notNull(from);
        PreCon.notNull(to);

        _info = teleportInfo;
        _from = from;
        _to = to;
    }

    /**
     * Get the location the entity is being teleported from.
     */
    public Location getFrom() {
        return _from.clone();
    }

    /**
     * Copy the location the entity is being teleported from to the
     * specified output location.
     *
     * @param output  The output location.
     *
     * @return  The output location.
     */
    public Location getFrom(Location output) {
        PreCon.notNull(output);

        return LocationUtils.copy(_from, output);
    }

    /**
     * Get a direct reference to the location the entity is being teleported to.
     */
    public Location getTo() {
        return _to;
    }

    @Override
    public boolean isCancelled() {
        return _isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        _isCancelled = isCancelled;
    }

    @Override
    public boolean isMultiTick() {
        return _info.isMultiTick();
    }

    @Override
    public TeleportCause getCause() {
        return _info.getCause();
    }

    @Override
    public TeleportMode getMode() {
        return _info.getMode();
    }

    @Override
    public Collection<Player> getPlayers() {
        return _info.getPlayers();
    }

    @Override
    public <T extends Collection<Player>> T getPlayers(T output) {
        return _info.getPlayers(output);
    }

    @Override
    public Collection<Entity> getMounts() {
        return _info.getMounts();
    }

    @Override
    public <T extends Collection<Entity>> T getMounts(T output) {
        return _info.getMounts(output);
    }

    @Override
    public Collection<ITeleportLeashPair> getLeashed() {
        return _info.getLeashed();
    }

    @Override
    public <T extends Collection<ITeleportLeashPair>> T getLeashed(T output) {
        return _info.getLeashed(output);
    }

    @Override
    public Collection<Entity> getTeleports() {
        return _info.getTeleports();
    }

    @Override
    public <T extends Collection<Entity>> T getTeleports(T output) {
        return _info.getTeleports(output);
    }

    @Override
    public Collection<Entity> getRejectedMounts() {
        return _info.getRejectedMounts();
    }

    @Override
    public <T extends Collection<Entity>> T getRejectedMounts(T output) {
        return _info.getRejectedMounts(output);
    }

    @Override
    public Collection<ITeleportLeashPair> getRejectedLeashed() {
        return _info.getRejectedLeashed();
    }

    @Override
    public <T extends Collection<ITeleportLeashPair>> T getRejectedLeashed(T output) {
        return _info.getRejectedLeashed(output);
    }

    @Override
    public Collection<Entity> getRejected() {
        return _info.getRejected();
    }

    @Override
    public <T extends Collection<Entity>> T getRejected(T output) {
        return _info.getRejected(output);
    }

    @Override
    public Collection<Entity> getAll() {
        return _info.getAll();
    }

    @Override
    public <T extends Collection<Entity>> T getAll(T output) {
        return _info.getAll(output);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
