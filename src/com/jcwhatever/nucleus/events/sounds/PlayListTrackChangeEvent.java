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
import com.jcwhatever.nucleus.sounds.ResourceSound;
import com.jcwhatever.nucleus.sounds.playlist.PlayList;
import com.jcwhatever.nucleus.sounds.playlist.PlayList.PlayerSoundQueue;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;

/**
 * Called when a playlist track changes
 */
public class PlayListTrackChangeEvent extends Event implements IPlayerReference, Cancellable, ICancellable {

    private static final HandlerList handlers = new HandlerList();

    private final PlayList _playList;
    private final PlayerSoundQueue _soundQueue;
    private final ResourceSound _prev;

    private ResourceSound _next;
    private boolean _isCancelled;

    /**
     * Constructor.
     *
     * @param playList    The {@code PlayList} the event is for.
     * @param soundQueue  The {@code PlayerSoundQueue} the event is for.
     * @param prev        The previous song that was playing, if any.
     * @param next        The next song to be played.
     */
    public PlayListTrackChangeEvent(PlayList playList, PlayerSoundQueue soundQueue,
                                    @Nullable ResourceSound prev, ResourceSound next) {
        PreCon.notNull(playList);
        PreCon.notNull(soundQueue);
        PreCon.notNull(next);

        _playList = playList;
        _soundQueue = soundQueue;
        _prev = prev;
        _next = next;
    }

    @Override
    public Player getPlayer() {
        return _soundQueue.getPlayer();
    }

    /**
     * Get the {@code PlayList} the event is for.
     */
    public PlayList getPlayList() {
        return _playList;
    }

    /**
     * Get the {@code PlayerSoundQueue} the event is for.
     */
    public PlayerSoundQueue getSoundQueue() {
        return _soundQueue;
    }

    /**
     * Get the previous sound that was playing.
     *
     * @return The previous sound or null if there was no previous sound.
     */
    @Nullable
    public ResourceSound getPreviousSound() {
        return _prev;
    }

    /**
     * Get the next sound to be played.
     */
    public ResourceSound getNextSound() {
        return _next;
    }

    /**
     * Set the next sound to be played.
     *
     * @param sound  The next sound.
     */
    public void setNextSound(ResourceSound sound) {
        PreCon.notNull(sound);

        _next = sound;
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
