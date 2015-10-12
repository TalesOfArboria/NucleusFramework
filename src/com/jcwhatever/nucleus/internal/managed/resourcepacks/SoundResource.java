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

import com.jcwhatever.nucleus.managed.resourcepacks.IResourcePack;
import com.jcwhatever.nucleus.managed.resourcepacks.sounds.types.IResourceSound;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;

/**
 * Abstract implementation of {@link IResourceSound}.
 */
abstract class SoundResource implements IResourceSound {

    private final IResourcePack _resourcePack;
    private final String _soundName;
    private final String _clientName;
    private final String _title;
    private final String _credit;
    private final int _durationSeconds;
    private final int _durationTicks;

    /**
     * Constructor.
     *
     * @param resourcePack  The resource pack the sound belongs to.
     * @param dataNode      The resource sound data node.
     */
    SoundResource(IResourcePack resourcePack, IDataNode dataNode) {
        PreCon.notNull(resourcePack);
        PreCon.notNull(dataNode);

        _resourcePack = resourcePack;

        // get the required sound name
        _soundName = loadName(dataNode);

        // get the required duration of the sound
        _durationSeconds = dataNode.getInteger("duration", -1);
        if (_durationSeconds < 0) {
            throw new RuntimeException("Resource sounds file is missing required duration "
                    + "parameter for sound: " + _soundName);
        }

        _clientName = dataNode.getString("client-name", _soundName);
        _title = dataNode.getString("title", _soundName);
        _credit = dataNode.getString("credit", "");
        _durationTicks = _durationSeconds * 20;
    }

    @Override
    public IResourcePack getResourcePack() {
        return _resourcePack;
    }

    @Override
    public final String getName() {
        return _soundName;
    }

    @Override
    public final String getClientName() {
        return _clientName;
    }

    @Override
    public final String getTitle() {
        return _title;
    }

    @Override
    public final String getCredit() {
        return _credit;
    }

    @Override
    public final int getDurationSeconds() {
        return _durationSeconds;
    }

    @Override
    public final int getDurationTicks() {
        return _durationTicks;
    }

    @Override
    public String toString() {
        return _soundName;
    }

    /*
     *  load the name of the resource sound.
     */
    protected String loadName(IDataNode dataNode) {
        return dataNode.getName();
    }
}
