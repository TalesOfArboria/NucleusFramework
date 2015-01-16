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


package com.jcwhatever.nucleus.sounds;

import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.LinkedList;

/**
 * Represents the currently playing song for a player.
 */
public final class Playing {

    private LinkedList<Runnable> _onFinish;

    private final Player _player;
    private final ResourceSound _sound;
    private final Location _location;
    private final float _volume;
    private final SoundFuture _future;

    private boolean _isFinished;


    /**
     * Constructor.
     *
     * @param player    The player.
     * @param sound     The sound the player hears.
     * @param location  The location of the sound.
     * @param volume    The volume of the sound.
     */
    Playing(Player player, ResourceSound sound, Location location, float volume) {
        _player = player;
        _sound = sound;
        _location = location;
        _volume = volume;
        _future = new SoundFuture();
    }

    /**
     * Get the player.
     */
    public Player getPlayer() {
        return _player;
    }

    /**
     * Get the resource sound the player hears.
     */
    public ResourceSound getResourceSound() {
        return _sound;
    }

    /**
     * Get the location of the sound.
     */
    public Location getLocation() {
        return _location;
    }

    /**
     * Get the volume of the sound.
     */
    public float getVolume() {
        return _volume;
    }

    /**
     * Determine if the sound is finished playing.
     */
    public boolean isFinished() {
        return _isFinished;
    }

    /**
     * Get a future used to run a callback
     * when the sound is finished.
     * @return
     */
    public SoundFuture getFuture() {
        return _future;
    }

    /**
     * Mark the sound as finished.
     */
    SoundFuture setFinished() {
        _isFinished = true;

        if (_onFinish == null)
            return _future;

        while (!_onFinish.isEmpty()) {
            _onFinish.removeFirst().run();
        }

        return _future;
    }

    /**
     * A future used to add callbacks that are
     * run when the sound is finished playing.
     */
    public class SoundFuture {

        /**
         * Add a callback to run when the sound is finished.
         *
         * @param callback  The callback to run.
         */
        public void onFinish(Runnable callback) {
            PreCon.notNull(callback);

            if (_isFinished) {
                callback.run();
                return;
            }

            if (_onFinish == null)
                _onFinish = new LinkedList<>();

            _onFinish.add(callback);
        }
    }
}
