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

import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Rand;
import com.jcwhatever.nucleus.utils.Scheduler;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

/**
 * A collection of resource sounds that can be played
 * to players.
 */
public class PlayList implements IPluginOwned {

    // static references for use by events
    private static Map<Player, Set<PlayList>> _instances = new WeakHashMap<>(100);

    /**
     * Remove player from all play lists.
     *
     * @param player  The player to remove.
     */
    public static void clearQueue(Player player) {

        Set<PlayList> playLists = _instances.get(player);
        if (playLists == null)
            return;

        for (PlayList playList : playLists) {
            playList.removePlayer(player);
        }
    }

    /**
     * Get all {@code PlayList}'s the player is currently listening to.
     *
     * @param player  The player.
     *
     * @return  A new {@code List} of {@code PlayList}.
     */
    public static List<PlayList> getAll(Player player) {
        Set<PlayList> playLists = _instances.get(player);
        if (playLists == null)
            return new ArrayList<>(0);

        return new ArrayList<>(playLists);
    }

    private final Plugin _plugin;
    private final List<ResourceSound> _playList;
    private final Map<Player, PlayerSoundQueue> _playerQueues = new WeakHashMap<>(100);

    private boolean _isLoop;
    private boolean _isRandom;

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     */
    public PlayList(Plugin plugin) {
        PreCon.notNull(plugin);

        _plugin = plugin;
        _playList = new ArrayList<>(10);
    }

    /**
     * Constructor.
     *
     * @param plugin    The owning plugin.
     * @param playList  The collections of sounds for the playlist.
     */
    public PlayList(Plugin plugin, Collection<? extends ResourceSound> playList) {
        PreCon.notNull(plugin);
        PreCon.notNull(playList);

        _plugin = plugin;
        _playList = new ArrayList<>(playList);
    }

    /**
     * Get the owning plugin.
     */
    @Override
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
    public void addSounds(Collection<? extends ResourceSound> sounds) {
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
     * Determine if the playlist should be played in random order.
     */
    public boolean isRandom() {
        return _isRandom;
    }

    /**
     * Set the playlist random order mode.
     *
     * @param isRandom  True to randomize order, false to play in order.
     */
    public void setRandom(boolean isRandom) {
        _isRandom = isRandom;
    }

    /**
     * Add a player to the playlist so they can listen to it.
     *
     * @param player    The player to add.
     * @param settings  The sound settings to use.
     *
     * @return  True if the player is added or already added. False if the
     * {@code PlayList} does not have any sounds.
     */
    public boolean addPlayer(Player player, SoundSettings settings) {
        PreCon.notNull(player);

        PlayerSoundQueue current = _playerQueues.get(player);
        if (current != null) {
            current._isRemoved = false;
            return true;
        }

        PlayerSoundQueue queue = new PlayerSoundQueue(player, settings);
        ResourceSound sound = queue.next();
        if (sound == null)
            return false;

        Set<PlayList> playLists = _instances.get(player);
        if (playLists == null) {
            playLists = new HashSet<>(10);
            _instances.put(player, playLists);
        }

        playLists.add(this);
        _playerQueues.put(player, queue);

        SoundManager.playSound(_plugin, player, sound, settings, null)
                .onFinish(new TrackChanger(player, queue));

        return true;
    }

    /**
     * Remove a player from the playlist.
     *
     * <p>Unless the player is in a different world than the playlist, the players
     * sound queue is marked for removal and removed when the currently playing
     * sound ends. Otherwise it is removed immediately.</p>
     *
     * @param player  The player to remove.
     */
    public boolean removePlayer(Player player) {
        return removePlayer(player, false);
    }

    /**
     * Remove a player from the playlist.
     *
     * <p>Unless forced or the player is in a different world, the players sound queue
     * is marked for removal and removed when the currently playing sound ends.</p>
     *
     * <p>Forcing the immediate removal of the players sound queue does not end the
     * sound on the client.</p>
     *
     * @param player  The player to remove.
     * @param force   True to force the immediate removal of the players sound queue.
     */
    public boolean removePlayer(Player player, boolean force) {
        PreCon.notNull(player);

        PlayerSoundQueue queue = _playerQueues.get(player);
        if (queue == null)
            return false;

        if (force)
            queue.removeNow();
        else
            queue.remove();

        return true;
    }

    /**
     * Get the players current sound queue from the playlist,
     * if any.
     *
     * @param player  The player to check.
     *
     * @return  Null if the player is not listening to the playlist.
     */
    @Nullable
    public PlayerSoundQueue getSoundQueue(Player player) {
        PreCon.notNull(player);

        return _playerQueues.get(player);
    }

    /**
     * A sound queue from a playlist for a player.
     */
    public class PlayerSoundQueue {

        private final WeakReference<Player> _player;
        private final LinkedList<ResourceSound> _queue = new LinkedList<>();
        private final World _world;
        private final SoundSettings _settings;
        private ResourceSound _current;
        private boolean _isRemoved;

        /**
         * Constructor.
         *
         * @param player  The player the sound queue is for.
         */
        PlayerSoundQueue(Player player, SoundSettings settings) {
            _player = new WeakReference<Player>(player);
            _settings = settings;
            _world = player.getWorld();
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
         * Get the world the playlist is playing in.
         */
        public World getWorld() {
            return _world;
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
         * Get the sound settings to use.
         */
        public SoundSettings getSettings() {
            return _settings;
        }

        /**
         * Mark the sound queue for removal.
         *
         * <p>If the player is still in a world where the sound is playing, the player
         * is not removed from the queue until after the current sound ends since there
         * is no way to stop the sound. If the player moves to a different world, the sound
         * is ended on the client and the player is removed from the queue immediately.</p>
         *
         * <p>Remove operation is performed after a 1 tick delay to ensure the {@code World} reported by
         * the {@code Player} object is up-to-date.</p>
         */
        void remove() {

            Scheduler.runTaskLater(getPlugin(), new Runnable() {
                @Override
                public void run() {

                    // check if player should be removed from queue immediately or
                    // wait for current song to end.
                    Player player = getPlayer();
                    boolean removeNow = player == null;
                    if (player != null) {

                        World world = player.getWorld();
                        removeNow = world == null || !world.equals(_world);
                    }

                    if (removeNow) {
                        removeNow();
                    }

                    _isRemoved = true;
                }
            });
        }

        /**
         * Remove the player sound queue.
         *
         * <p>Does not end sound on client.</p>
         */
        void removeNow() {

            Player player = getPlayer();
            if (player == null)
                return;

            _queue.clear();
            _playerQueues.remove(player);

            Set<PlayList> playLists = _instances.get(player);
            if (playLists != null) {
                playLists.remove(PlayList.this);
            }

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

            if (_queue.isEmpty())
                return _current = null;

            if (_isRandom) {
                int index = Rand.getInt(0, _queue.size() - 1);
                return _current = _queue.remove(index);
            }

            return _current = _queue.pollFirst();
        }
    }

    /**
     * Task to ensure the next song in the player queue is played.
     */
    private class TrackChanger implements Runnable {

        private final WeakReference<Player> _player;
        private final PlayerSoundQueue _soundQueue;

        TrackChanger(Player player, PlayerSoundQueue queue) {
            _player = new WeakReference<Player>(player);
            _soundQueue = queue;
        }

        @Override
        public void run() {

            Player player = _player.get();
            if (player == null)
                return;

            if (_soundQueue.isRemoved()) {
                removeNow(player);
                return;
            }

            ResourceSound sound = _soundQueue.next();
            if (sound == null) {
                removeNow(player);
                return;
            }

            SoundManager.playSound(_plugin, player, sound, _soundQueue.getSettings(), null).onFinish(this);
        }

        private void removeNow(Player player) {
            PlayerSoundQueue current = _playerQueues.get(player);
            if (current != _soundQueue)
                return;

            current.removeNow();
        }
    }
}
