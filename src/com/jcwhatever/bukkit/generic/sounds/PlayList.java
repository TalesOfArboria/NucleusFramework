/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.sounds;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * A collection of resource sounds that can be played
 * to players.
 */
public class PlayList {

    // static references for use by events
    private static Map<Player, Set<PlayList>> _instances = new WeakHashMap<>(100);

    /**
     * Remove player from all play lists.
     *
     * @param p  The player to remove.
     */
    public static void clearQueue(Player p) {

        Set<PlayList> playLists = _instances.get(p);
        if (playLists == null)
            return;

        for (PlayList playList : playLists) {
            playList.removePlayer(p);
        }
    }

    private final Plugin _plugin;
    private final List<ResourceSound> _playList;
    private final Map<Player, PlayerSoundQueue> _playerQueues = new WeakHashMap<>(100);

    private boolean _isLoop;
    private Location _location;
    private float _volume = 1.0F;

    /**
     * Constructor.
     *
     * @param plugin    The owning plugin.
     * @param playList  The collections of sounds for the playlist.
     */
    public PlayList(Plugin plugin, Collection<ResourceSound> playList) {
        _plugin = plugin;
        _playList = new ArrayList<>(playList);
    }

    /**
     * Get the owning plugin.
     */
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the number of songs in the playlist.
     */
    public int size() {
        return _playList.size();
    }

    /**
     * Determine if the play list is being
     * run in a loop.
     */
    public boolean isLoop() {
        return _isLoop;
    }

    /**
     * Set the play lists looping mode.
     *
     * @param isLoop  True to enable looping.
     */
    public void setLoop(boolean isLoop) {
        _isLoop = isLoop;
    }

    /**
     * Get the location of the sound. If the sound is null
     * the sound is played wherever the player is at.
     */
    @Nullable
    public Location getLocation() {
        return _location;
    }

    /**
     * Set the location sounds are played at. Null to play at
     * the players location.
     *
     * @param location  The location to play at.
     */
    public void setLocation(@Nullable Location location) {
        _location = location;
    }

    /**
     * Get the volume of the sound.
     */
    public float getVolume() {
        return _volume;
    }

    /**
     * Set the volume of the sound.
     *
     * @param volume  The volume.
     */
    public void setVolume(float volume) {
        _volume = volume;
    }

    /**
     * Add a resource sound to the playlist.
     *
     * @param sound  The sound to add.
     */
    public void addSound(ResourceSound sound) {
        _playList.add(sound);
    }

    /**
     * Remove a resource sound from the playlist.
     *
     * @param sound  The sound to remove.
     */
    public void removeSound(ResourceSound sound) {
        _playList.remove(sound);
    }

    /**
     * Add a collection of sounds to the playlist.
     *
     * @param sounds  The collection of sounds to add.
     */
    public void addSounds(Collection<ResourceSound> sounds) {
        _playList.addAll(sounds);
    }

    /**
     * Clear all sounds from the playlist.
     */
    public void clearSounds() {
        _playList.clear();
    }

    /**
     * Get all sounds in the playlist.
     */
    public List<ResourceSound> getSounds() {
        return new ArrayList<>(_playList);
    }

    /**
     * Add a player to the playlist so they can listen to it.
     *
     * @param p  The player to add.
     */
    public boolean addPlayer(Player p) {
        PreCon.notNull(p);

        if (_playerQueues.containsKey(p))
            return false;

        final PlayerSoundQueue queue = new PlayerSoundQueue(p);

        ResourceSound sound = queue.next();
        if (sound == null)
            return false;

        Set<PlayList> playLists = _instances.get(p);
        if (playLists == null) {
            playLists = new HashSet<>(10);
            _instances.put(p, playLists);
        }

        playLists.add(this);
        _playerQueues.put(p, queue);

        SoundManager.playSound(_plugin, p, sound, _location, _volume, null)
                .onFinish(new TrackChanger(p));

        return true;
    }

    /**
     * Remove a player from the playlist.
     *
     * @param p  The player to remove.
     */
    public boolean removePlayer(Player p) {
        PreCon.notNull(p);

        PlayerSoundQueue queue = _playerQueues.get(p);
        if (queue == null)
            return false;

        queue.remove();

        Set<PlayList> playLists = _instances.get(p);
        if (playLists != null) {
            playLists.remove(this);
        }

        return true;
    }

    /**
     * Get the players current sound queue from the playlist,
     * if any.
     *
     * @param p  The player to check.
     *
     * @return  Null if the player is not listening to the playlist.
     */
    @Nullable
    public PlayerSoundQueue getSoundQueue(Player p) {
        PreCon.notNull(p);

        return _playerQueues.get(p);
    }


    /**
     * A sound queue from a playlist for a player.
     */
    public class PlayerSoundQueue {

        private final WeakReference<Player> _player;
        private final LinkedList<ResourceSound> _queue = new LinkedList<>();
        private ResourceSound _current;
        private boolean _isRemoved;

        /**
         * Constructor.
         *
         * @param player  The player the sound queue is for.
         */
        PlayerSoundQueue(Player player) {
            _player = new WeakReference<Player>(player);
            _queue.addAll(_playList);
        }

        /**
         * Get the player the sound queue is for.
         *
         * <p>Player is held by a weak reference, may return null.</p>
         */
        @Nullable
        public Player getPlayer() {
            return _player.get();
        }

        /**
         * Get the current sound being played to the player.
         */
        @Nullable
        public ResourceSound getCurrent() {
            return _current;
        }

        /**
         * Determine if the sound queue is marked for removal
         * after the current sound completes.
         */
        public boolean isRemoved() {
            return _isRemoved;
        }

        /**
         * Mark the sound queue for removal.
         */
        void remove() {
            _queue.clear();
            _isRemoved = true;
        }

        /**
         * Get the next sound in the queue.
         *
         * @return  Null if the playlist is finished.
         */
        @Nullable
        ResourceSound next() {
            if (_isRemoved ||
                    (_queue.isEmpty() && !_isLoop) ||
                    getPlayer() == null) {

                _current = null;
                return null;
            }
            else if (_queue.isEmpty()) {
                _queue.addAll(_playList);
            }

            return _current = _queue.removeFirst();
        }
    }

    /**
     * Task to ensure the next song in the player queue is played.
     */
    private class TrackChanger implements Runnable {

        private final WeakReference<Player> _player;

        TrackChanger(Player p) {
            _player = new WeakReference<Player>(p);
        }

        @Override
        public void run() {

            if (_player.get() == null)
                return;

            PlayerSoundQueue queue = _playerQueues.get(_player.get());
            if (queue == null)
                return;

            if (queue.isRemoved()) {
                _playerQueues.remove(_player.get());
                return;
            }

            ResourceSound sound = queue.next();
            if (sound == null)
                return;

            SoundManager.playSound(_plugin, _player.get(), sound, _location, _volume, null).onFinish(this);
        }
    }

}
