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
import com.jcwhatever.nucleus.managed.teleport.ITeleportResult;
import com.jcwhatever.nucleus.managed.teleport.TeleportMode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.future.FutureResultAgent;
import com.jcwhatever.nucleus.utils.observer.future.FutureResultSubscriber;
import com.jcwhatever.nucleus.utils.observer.future.FutureSubscriber;
import com.jcwhatever.nucleus.utils.observer.future.IFuture.FutureStatus;
import com.jcwhatever.nucleus.utils.observer.future.IFutureResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import javax.annotation.Nullable;

/**
 * Implementation of {@link IScheduledTeleport}.
 */
class ScheduledTeleport extends TaskHandler implements IScheduledTeleport {

    private final InternalTeleportManager _manager;
    private final Player _player;
    private final int _delay;
    private final Location _location;
    private final FutureResultAgent<ITeleportResult> _agent;
    private final IFutureResult<ITeleportResult> _future;
    private final TeleportCause _cause;
    private final TeleportMode _mode;

    private boolean _isFinished;
    private boolean _isCancelled;

    /**
     * Constructor.
     *
     * @param manager   The owning manager instance.
     * @param player    The player.
     * @param delay     The scheduled delay in ticks.
     * @param location  The location to teleport the player.
     * @param cause     The teleport cause.
     */
    ScheduledTeleport(InternalTeleportManager manager, Player player, int delay,
                      Location location, TeleportCause cause, TeleportMode mode) {
        PreCon.notNull(manager);
        PreCon.notNull(player);
        PreCon.notNull(location);
        PreCon.notNull(cause);
        PreCon.notNull(mode);

        _manager = manager;
        _player = player;
        _delay = delay;
        _location = location;
        _agent = new FutureResultAgent<ITeleportResult>();
        _future = _agent.getFuture();
        _cause = cause;
        _mode = mode;
    }

    @Override
    public Player getPlayer() {
        return _player;
    }

    @Override
    public int getDelayTicks() {
        return _delay;
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
    public boolean isCancelled() {
        return _isCancelled;
    }

    @Override
    public void cancel() {
        cancelTask();
    }

    @Override
    public IFutureResult<ITeleportResult> onResult(FutureResultSubscriber<ITeleportResult> subscriber) {
        PreCon.notNull(subscriber);

        _future.onResult(subscriber);
        return this;
    }

    @Override
    public IFutureResult<ITeleportResult> onSuccess(FutureResultSubscriber<ITeleportResult> subscriber) {
        PreCon.notNull(subscriber);

        _future.onSuccess(subscriber);
        return this;
    }

    @Override
    public IFutureResult<ITeleportResult> onCancel(FutureResultSubscriber<ITeleportResult> subscriber) {
        PreCon.notNull(subscriber);

        _future.onCancel(subscriber);
        return this;
    }

    @Override
    public IFutureResult<ITeleportResult> onError(FutureResultSubscriber<ITeleportResult> subscriber) {
        PreCon.notNull(subscriber);

        _future.onError(subscriber);
        return this;
    }

    @Override
    public void run() {

        final ITeleportResult result = new TeleportHandler(_player, _cause, _mode).teleport(_location);

        result.getFuture()
                .onStatus(new FutureSubscriber() {
                    @Override
                    public void on(FutureStatus status, @Nullable CharSequence message) {

                        switch (status) {
                            case SUCCESS:
                                _agent.success(result);
                                break;
                            case CANCEL:
                                _agent.cancel(result);
                                break;
                            case ERROR:
                                _agent.error(result);
                                break;
                        }

                        _isFinished = true;
                        _manager.removeTask(_player.getUniqueId());
                    }
                });
    }

    @Override
    protected void onCancel() {

        if (!_isFinished)
            _agent.cancel();

        _isFinished = true;
        _isCancelled = true;

        _manager.removeTask(_player.getUniqueId());
    }
}
