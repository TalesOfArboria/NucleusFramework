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

import com.jcwhatever.nucleus.utils.ArrayUtils;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Sound playback settings.
 */
public class SoundSettings {

    private Set<Location> _locations = new HashSet<>(3);
    private float _volume = 1.0F;
    private float _pitch = 1.0F;

    /**
     * Constructor.
     *
     * <p>Initializes with no locations and a volume of 1.0F.</p>
     */
    public SoundSettings() {}

    /**
     * Constructor.
     *
     * <p>Clones settings from another {@link SoundSettings} instance.</p>
     *
     * @param original  The original to clone from.
     */
    public SoundSettings(SoundSettings original) {
        _locations.addAll(original._locations);
        _volume = original._volume;
    }

    /**
     * Constructor.
     *
     * @param volume     The volume of the sound.
     * @param locations  The locations to play the sound at.
     */
    public SoundSettings(float volume, Location... locations) {

        _locations.addAll(ArrayUtils.asList(locations));
        _volume = volume;
    }

    /**
     * Determine if there are locations set.
     *
     * @return  True if there are 1 or more locations.
     */
    public boolean hasLocations() {
        return !_locations.isEmpty();
    }

    /**
     * Clear all locations.
     *
     * @return  Self for chaining.
     */
    public SoundSettings clearLocations() {
        _locations.clear();

        return this;
    }

    /**
     * Get the locations of the sound.
     *
     * @return  A new list copy of the locations.
     */
    public List<Location> getLocations() {
        return new ArrayList<>(_locations);
    }

    /**
     * Add a collection of locations to play the sound at.
     *
     * @param locations  The locations to add.
     *
     * @return  Self for chaining.
     */
    public SoundSettings addLocations(Collection<Location> locations) {
        PreCon.notNull(locations);

        _locations.addAll(locations);

        return this;
    }

    /**
     * Add 1 or more locations to play the sound at.
     *
     * @param locations  Locations to add.
     *
     * @return  Self for chaining.
     */
    public SoundSettings addLocations(Location... locations) {
        PreCon.notNull(locations);

        if (locations.length > 0) {
            _locations.addAll(ArrayUtils.asList(locations));
        }

        return this;
    }

    /**
     * Remove 1 or more locations.
     *
     * @param locations  Locations to remove.
     *
     * @return  Self for chaining.
     */
    public SoundSettings removeLocations(Location... locations) {
        PreCon.notNull(locations);

        if (locations.length > 0) {
            _locations.removeAll(ArrayUtils.asList(locations));
        }

        return this;
    }

    /**
     * Remove a collection of locations.
     *
     * @param locations  The location to remove.
     *
     * @return  Self for chaining.
     */
    public SoundSettings removeLocations(Collection<Location> locations) {
        PreCon.notNull(locations);

        _locations.removeAll(locations);

        return this;
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
     *
     * @return  Self for chaining.
     */
    public SoundSettings setVolume(float volume) {
        _volume = volume;

        return this;
    }

    /**
     * Get the pitch of the sound.
     *
     * @return  Self for chaining.
     */
    public float getPitch() {
        return _pitch;
    }

    /**
     * Set the pitch of the sound.
     *
     * @param pitch  The pitch.
     *
     * @return  Self for chaining.
     */
    public SoundSettings setPitch(float pitch) {
        _pitch = pitch;

        return this;
    }
}
