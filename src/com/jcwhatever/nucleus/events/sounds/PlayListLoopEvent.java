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

package com.jcwhatever.nucleus.events.sounds;

import com.jcwhatever.nucleus.mixins.ICancellable;
import com.jcwhatever.nucleus.mixins.IPlayerReference;
import com.jcwhatever.nucleus.managed.sounds.types.ResourceSound;
import com.jcwhatever.nucleus.managed.sounds.playlist.PlayList;
import com.jcwhatever.nucleus.managed.sounds.playlist.PlayList.PlayerSoundQueue;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

/**
 * Called when a {@link PlayList.PlayerSoundQueue} finishes and is ready to loop.
 */
public class PlayListLoopEvent extends Event implements IPlayerReference, Cancellable, ICancellable {

    private static final HandlerList handlers = new HandlerList();

    private final PlayList _playList;
    private final PlayerSoundQueue _soundQueue;
    private final List<ResourceSound> _sounds;
    private final int _loopCount;

    private boolean _isCancelled;

    /**
     * Constructor.
     *
     * @param soundQueue  The {@link PlayerSoundQueue} the event is for.
     * @param sounds      The sounds that will be played in the next loop.
     * @param loopCount   The number of times the {@link PlayerSoundQueue} has looped.
     */
    public PlayListLoopEvent(PlayList playList, PlayerSoundQueue soundQueue,
                             List<ResourceSound> sounds, int loopCount) {
        PreCon.notNull(playList);
        PreCon.notNull(soundQueue);
        PreCon.notNull(sounds);

        _playList = playList;
        _soundQueue = soundQueue;
        _sounds = sounds;
        _loopCount = loopCount;
    }

    @Override
    public Player getPlayer() {
        return _soundQueue.getPlayer();
    }

    /**
     * Get the {@link PlayList} the event is for.
     */
    public PlayList getPlayList() {
        return _playList;
    }

    /**
     * Get the {@link PlayerSoundQueue} the event is for.
     */
    public PlayerSoundQueue getSoundQueue() {
        return _soundQueue;
    }

    /**
     * Get the sounds that will be played in the next loop.
     */
    public List<ResourceSound> getSounds() {
        return _sounds;
    }

    /**
     * Get the number of times the {@link PlayerSoundQueue} has looped.
     */
    public int getLoopCount() {
        return _loopCount;
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
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
