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
import com.jcwhatever.nucleus.managed.resourcepacks.sounds.IResourcePackSounds;
import com.jcwhatever.nucleus.managed.resourcepacks.sounds.types.IResourceSound;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Internal implementation of {@link IResourcePackSounds}.
 */
public class ResourcePackSounds implements IResourcePackSounds {

    private final ResourcePack _resourcePack;
    private Map<String, IResourceSound> _sounds;

    /**
     * Constructor.
     *
     * @param resourcePack  The owning resource pack.
     * @param dataNode      The data node containing sound information.
     */
    ResourcePackSounds(ResourcePack resourcePack, @Nullable IDataNode dataNode) {
        _resourcePack = resourcePack;

        if (dataNode != null) {
            load(dataNode);
        }
        else {
            _sounds = new HashMap<>(0);
        }
    }

    @Override
    public IResourcePack getResourcePack() {
        return _resourcePack;
    }

    @Override
    @Nullable
    public IResourceSound get(String name) {
        PreCon.notNull(name);

        return _sounds.get(name.toLowerCase());
    }

    @Override
    public Collection<IResourceSound> getAll() {
        return new ArrayList<>(_sounds.values());
    }

    @Override
    public <T extends Collection<IResourceSound>> T getAll(T output) {
        PreCon.notNull(output);

        output.addAll(_sounds.values());
        return output;
    }

    @Override
    public <T extends IResourceSound> Collection<T> getTypes(Class<T> type) {
        return getTypes(type, new ArrayList<T>(_sounds.size()));
    }

    @Override
    public <T extends IResourceSound, E extends Collection<T>> E getTypes(Class<T> type, E output) {
        PreCon.notNull(type);
        PreCon.notNull(output);

        for (IResourceSound sound : _sounds.values()) {

            if (type.isInstance(sound)) {

                @SuppressWarnings("unchecked")
                T result = (T) sound;

                output.add(result);
            }
        }

        return output;
    }

    public void load(IDataNode dataNode) {

        _sounds = new HashMap<>(dataNode.size());

        for (IDataNode soundNode : dataNode) {

            IResourceSound sound = getNewSound(soundNode);
            _sounds.put(sound.getName().toLowerCase(), sound);
        }
    }

    /*
     * Create a new resource sound instance
     */
    private IResourceSound getNewSound(IDataNode node) {

        String type = node.getString("type");

        if (type == null || type.isEmpty()) {
            int diskId = node.getInteger("disk-id", -1);
            type = diskId == -1
                    ? "music"
                    : "disc";
        }

        switch(type.toLowerCase()) {

            case "music":
                return new SoundMusic(_resourcePack, node);

            case "disc":
                return new SoundMusicDisc(_resourcePack, node);

            case "effect":
                return new SoundEffect(_resourcePack, node);

            case "voice":
                return new SoundVoice(_resourcePack, node);

            default:
                throw new RuntimeException("Invalid sound type in resource sounds: " + type);
        }
    }
}
