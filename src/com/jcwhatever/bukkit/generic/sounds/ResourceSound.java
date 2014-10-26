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

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;

/**
 * Abstract implementation of a resource sound.
 */
public abstract class ResourceSound {

    private final String _soundName;
    private final String _displayName;
    private final String _credit;
    private final int _durationSeconds;
    private final int _durationTicks;

    /**
     * Constructor.
     *
     * @param dataNode  The resource sound data node.
     */
    ResourceSound (IDataNode dataNode) {
        PreCon.notNull(dataNode);

        // get the required sound name
        _soundName = loadName(dataNode);

        // get the required duration of the sound
        _durationSeconds = dataNode.getInteger("duration", -1);
        if (_durationSeconds < 0)
            throw new RuntimeException("Resource sounds file is missing required duration parameter for sound: " + _soundName);

        _displayName = dataNode.getString("display", _soundName);
        _credit = dataNode.getString("credit", "");
        _durationTicks = _durationSeconds * 20;
    }

    /**
     * Get the name of the sound.
     */
    public final String getName() {
        return _soundName;
    }

    /**
     * Get the display name of the sound.
     */
    public final String getDisplayName() {
        return _displayName;
    }

    /**
     * Get the name of the sound creator.
     */
    public final String getCredit() {
        return _credit;
    }

    /**
     * Get the duration of the sound in seconds.
     */
    public final int getDurationSeconds() {
        return _durationSeconds;
    }

    /**
     * Get the duration of the sound in ticks.
     */
    public final int getDurationTicks() {
        return _durationTicks;
    }

    @Override
    public String toString() {
        return _soundName;
    }

    /*
     *  load the name of the resource sound
     */
    protected String loadName(IDataNode dataNode) {
        String soundName = dataNode.getString("name");
        if (soundName == null)
            throw new RuntimeException("Required name parameter is missing in resource sounds file.");

        return soundName;
    }

}
