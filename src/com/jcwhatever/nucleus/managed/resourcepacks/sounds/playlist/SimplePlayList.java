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


package com.jcwhatever.nucleus.managed.resourcepacks.sounds.playlist;

import com.jcwhatever.nucleus.managed.resourcepacks.sounds.types.IResourceSound;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A collection of resource sounds that can be played
 * to players.
 */
public class SimplePlayList extends PlayList {

    private final List<IResourceSound> _playList;

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     */
    public SimplePlayList(Plugin plugin) {
        super(plugin);

        _playList = new ArrayList<>(10);
    }

    /**
     * Constructor.
     *
     * @param plugin    The owning plugin.
     * @param playList  The collections of sounds for the playlist.
     */
    public SimplePlayList(Plugin plugin, Collection<? extends IResourceSound> playList) {
        super(plugin);
        PreCon.notNull(playList);

        _playList = new ArrayList<>(playList);
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
    public void addSound(IResourceSound sound) {
        _playList.add(sound);
    }

    /**
     * Remove a resource sound from the playlist.
     *
     * @param sound  The sound to remove.
     */
    public void removeSound(IResourceSound sound) {
        _playList.remove(sound);
    }

    /**
     * Add a collection of sounds to the playlist.
     *
     * @param sounds  The collection of sounds to add.
     */
    public void addSounds(Collection<? extends IResourceSound> sounds) {
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
    public List<IResourceSound> getSounds() {
        return new ArrayList<>(_playList);
    }

    @Override
    protected List<IResourceSound> getSounds(PlayerSoundQueue queue, int loopCount) {
        return _playList;
    }
}
