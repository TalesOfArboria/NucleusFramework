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


package com.jcwhatever.nucleus.internal.managed.scripting.api;

import com.jcwhatever.nucleus.managed.resourcepacks.sounds.playlist.SimplePlayList;
import com.jcwhatever.nucleus.managed.resourcepacks.sounds.types.IResourceSound;
import com.jcwhatever.nucleus.managed.sounds.SoundSettings;
import com.jcwhatever.nucleus.managed.sounds.Sounds;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class SAPI_Sounds implements IDisposable {

    private final Plugin _plugin;
    private boolean _isDisposed;

    public SAPI_Sounds(Plugin plugin) {
        _plugin = plugin;
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        _isDisposed = true;
    }

    /**
     * Get a {@link IResourceSound} by name.
     *
     * @param soundName  The name of the sound.
     */
    public IResourceSound get(String soundName) {
        PreCon.notNullOrEmpty(soundName, "soundName");

        IResourceSound sound = Sounds.get(soundName);
        PreCon.isValid(sound != null, "Sound not found.");

        return sound;
    }

    /**
     * Play a resource sound to a player.
     *
     * @param player     The player.
     * @param soundName  The name of the resource sound.
     */
    public void play(Object player, String soundName) {
        PreCon.notNull(player);
        PreCon.notNullOrEmpty(soundName);

        Player p = PlayerUtils.getPlayer(player);
        PreCon.notNull(p);

        IResourceSound sound = Sounds.get(soundName);
        if (sound == null) {
            Sounds.playEffect(soundName, p);
            return;
        }

        Sounds.playSound(_plugin, p, sound, new SoundSettings(500.0f, p.getLocation()));
    }

    /**
     * Play a resource sound to a player at the specified coordinates
     * and with the specified volume.
     *
     * @param player     The player.
     * @param soundName  The name of the resource sound.
     * @param x          The X coordinates.
     * @param y          The Y coordinates.
     * @param z          The Z coordinates.
     * @param volume     The volume.
     */
    public void playAt(Object player, String soundName, int x, int y, int z, double volume) {
        PreCon.notNull(player);
        PreCon.notNullOrEmpty(soundName);

        Player p = PlayerUtils.getPlayer(player);
        PreCon.notNull(p);

        Location location = new Location(p.getWorld(), x, y, z);

        IResourceSound sound = Sounds.get(soundName);
        if (sound == null) {
            Sounds.playEffect(soundName, p, location, (float)volume, 1.0f);
            return;
        }

        Sounds.playSound(_plugin, p, sound, new SoundSettings((float) volume, location));
    }

    /**
     * Play a resource sound to a player at the specified coordinates
     * and with the specified volume.
     *
     * @param player     The player.
     * @param soundName  The name of the resource sound.
     * @param location   The location to play at.
     * @param volume     The volume.
     */
    public void playLocation(Object player, String soundName, Location location, double volume) {
        PreCon.notNull(player);
        PreCon.notNullOrEmpty(soundName);
        PreCon.notNull(location);

        Player p = PlayerUtils.getPlayer(player);
        PreCon.notNull(p);

        IResourceSound sound = Sounds.get(soundName);
        if (sound == null) {
            Sounds.playEffect(soundName, p, location, (float)volume, 1.0f);
            return;
        }

        Sounds.playSound(_plugin, p, sound, new SoundSettings((float) volume, location));
    }

    /**
     * Create a new playlist.
     *
     * @param soundNames  The names of the resource sounds to play. The resource pack the sound
     *                    is in precedes the sound name and is separated with a colon.
     *                    i.e. "resourcePackName.soundName"
     */
    public SimplePlayList createPlayList(String... soundNames) {

        List<IResourceSound> sounds = new ArrayList<>(soundNames.length);

        for (String soundName : soundNames) {

            IResourceSound sound = Sounds.get(soundName);
            if (sound == null)
                throw new RuntimeException("The sound " + soundName + " was not found.");

            sounds.add(sound);
        }

        SimplePlayList playList = new SimplePlayList(_plugin);
        playList.addSounds(sounds);
        return playList;
    }

    /**
     * Create a new sound settings object.
     *
     * @param volume     The sound volume.
     * @param pitch      The sound pitch.
     * @param locations  The locations to play the sound at.
     */
    public SoundSettings createSoundSettings(float volume, float pitch, Location... locations) {
        SoundSettings settings = new SoundSettings();

        settings.setVolume(volume).setPitch(pitch).addLocations(locations);

        return settings;
    }
}
