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

package com.jcwhatever.nucleus.internal.managed.resourcepacks;

import com.jcwhatever.nucleus.managed.resourcepacks.IPlayerResourcePacks;
import com.jcwhatever.nucleus.managed.resourcepacks.IResourcePack;
import com.jcwhatever.nucleus.managed.resourcepacks.ResourcePackStatus;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.future.FutureResultAgent;
import com.jcwhatever.nucleus.utils.observer.future.IFutureResult;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

/**
 * Implementation of {@link IPlayerResourcePacks}.
 */
class PlayerResourcePacks implements IPlayerResourcePacks {

    private final Player _player;
    private final Deque<IResourcePack> _packs = new ArrayDeque<IResourcePack>(3);


    private ResourcePackStatus _status = ResourcePackStatus.NO_RESOURCE;
    private FutureResultAgent<IPlayerResourcePacks> _nextStatusAgent;
    private FutureResultAgent<IPlayerResourcePacks> _finalStatusAgent;

    PlayerResourcePacks(Player player, @Nullable IResourcePack current) {
        _player = player;

        if (current != null)
            _packs.addLast(current);
    }

    @Override
    public Player getPlayer() {
        return _player;
    }

    @Nullable
    @Override
    public IResourcePack getCurrent() {
        return _packs.peekLast();
    }

    @Override
    public boolean prev() {

        if (_packs.size() <= 1 || _status == ResourcePackStatus.PENDING)
            return false;

        _packs.pollLast();

        IResourcePack current = _packs.peekLast();
        _player.setResourcePack(current.getUrl());
        _status = ResourcePackStatus.PENDING;
        return true;
    }

    @Override
    public boolean next(IResourcePack resourcePack) {
        PreCon.notNull(resourcePack);

        if (_status == ResourcePackStatus.PENDING)
            return false;

        _packs.offerLast(resourcePack);
        _player.setResourcePack(resourcePack.getUrl());
        _status = ResourcePackStatus.PENDING;
        return true;
    }

    @Override
    public boolean next(IResourcePack resourcePack, IResourcePack... remove) {
        PreCon.notNull(resourcePack);
        PreCon.notNull(remove);

        for (IResourcePack toRemove : remove) {
            _packs.removeLastOccurrence(toRemove);
        }

        return next(resourcePack);
    }

    @Override
    public boolean remove(IResourcePack resourcePack) {
        PreCon.notNull(resourcePack);

        IResourcePack current = _packs.peekLast();

        if (resourcePack.equals(current))
            return prev();

        return _packs.removeLastOccurrence(resourcePack);
    }

    @Override
    public ResourcePackStatus getStatus() {
        return _status;
    }

    @Override
    public IFutureResult<IPlayerResourcePacks> getNextStatus() {

        if (_status.isFinal()) {
            return FutureResultAgent.<IPlayerResourcePacks>successResult(this);
        }

        if (_nextStatusAgent == null)
            _nextStatusAgent = new FutureResultAgent<>();

        return _nextStatusAgent.getFuture();
    }

    @Override
    public IFutureResult<IPlayerResourcePacks> getFinalStatus() {

        if (_status.isFinal()) {
            return FutureResultAgent.<IPlayerResourcePacks>successResult(this);
        }

        if (_finalStatusAgent == null)
            _finalStatusAgent = new FutureResultAgent<>();

        return _finalStatusAgent.getFuture();
    }

    @Override
    public List<IResourcePack> getStack() {
        return new ArrayList<>(_packs);
    }

    @Override
    public <T extends Collection<IResourcePack>> T getStack(T output) {
        PreCon.notNull(output);

        if (output instanceof ArrayList)
            ((ArrayList) output).ensureCapacity(_packs.size() + output.size());

        output.addAll(_packs);
        return output;
    }

    void setStatus(ResourcePackStatus status) {
        _status = status;

        if (_nextStatusAgent != null) {
            _nextStatusAgent.success(this);
            _nextStatusAgent = null;
        }

        if (_finalStatusAgent != null && _status.isFinal()) {
            _finalStatusAgent.success(this);
            _finalStatusAgent = null;
        }
    }
}
