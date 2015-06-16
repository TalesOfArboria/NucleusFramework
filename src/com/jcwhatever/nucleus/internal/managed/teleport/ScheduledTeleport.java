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

import com.jcwhatever.nucleus.managed.scheduler.TaskHandler;
import com.jcwhatever.nucleus.managed.teleport.IScheduledTeleport;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.future.FutureAgent;
import com.jcwhatever.nucleus.utils.observer.future.FutureSubscriber;
import com.jcwhatever.nucleus.utils.observer.future.IFuture;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Implementation of {@link IScheduledTeleport}.
 */
class ScheduledTeleport extends TaskHandler implements IScheduledTeleport {

    private final InternalTeleportManager _manager;
    private final Player _player;
    private final Location _location;
    private final FutureAgent _agent;
    private final IFuture _future;

    private boolean _isFinished;

    /**
     * Constructor.
     *
     * @param manager   The owning manager instance.
     * @param player    The player.
     * @param location  The location to teleport the player.
     */
    ScheduledTeleport(InternalTeleportManager manager, Player player, Location location) {
        PreCon.notNull(manager);
        PreCon.notNull(player);
        PreCon.notNull(location);

        _manager = manager;
        _player = player;
        _location = location;
        _agent = new FutureAgent();
        _future = _agent.getFuture();
    }

    @Override
    public Player getPlayer() {
        return _player;
    }

    @Override
    public Location getLocation() {
        return getLocation(new Location(null, 0, 0, 0));
    }

    @Override
    public Location getLocation(Location output) {
        PreCon.notNull(output);

        output.setWorld(_location.getWorld());
        output.setX(_location.getX());
        output.setY(_location.getY());
        output.setZ(_location.getZ());
        output.setYaw(_location.getYaw());
        output.setPitch(_location.getPitch());

        return output;
    }

    @Override
    public void cancel() {
        cancelTask();
    }

    @Override
    public IFuture onStatus(FutureSubscriber subscriber) {
        _future.onStatus(subscriber);
        return this;
    }

    @Override
    public IFuture onSuccess(FutureSubscriber subscriber) {
        _future.onSuccess(subscriber);
        return this;
    }

    @Override
    public IFuture onCancel(FutureSubscriber subscriber) {
        _future.onCancel(subscriber);
        return this;
    }

    @Override
    public IFuture onError(FutureSubscriber subscriber) {
        _future.onError(subscriber);
        return this;
    }

    @Override
    public void run() {
        if (_player.teleport(_location)) {
            _agent.success();
        }
        else {
            _agent.cancel();
        }
        _isFinished = true;
        _manager.removeTask(_player.getUniqueId());
    }

    @Override
    protected void onCancel() {

        if (!_isFinished)
            _agent.cancel();

        _isFinished = true;

        _manager.removeTask(_player.getUniqueId());
    }
}
