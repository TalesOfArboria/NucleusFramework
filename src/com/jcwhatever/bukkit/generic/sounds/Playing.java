package com.jcwhatever.bukkit.generic.sounds;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.LinkedList;

/**
 * Represents the currently playing song for a player.
 */
public class Playing {

    private LinkedList<Runnable> _onFinish;

    private final Player _player;
    private final ResourceSound _sound;
    private final Location _location;
    private final float _volume;
    private final Future _future;

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
        _future = new Future();
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
    public Future getFuture() {
        return _future;
    }

    /**
     * Mark the sound as finished.
     */
    Future setFinished() {
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
    public class Future {

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
