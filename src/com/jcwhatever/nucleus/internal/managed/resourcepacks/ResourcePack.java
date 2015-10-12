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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.managed.resourcepacks.IResourcePack;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.YamlDataNode;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.io.File;

/**
 * Internal implementation of {@link IResourcePack}.
 */
class ResourcePack implements IResourcePack {

    private final String _name;
    private final String _searchName;
    private final String _url;
    private final InternalResourcePackManager _manager;
    private final ResourcePackSounds _sounds;

    ResourcePack(String name, String url, InternalResourcePackManager manager) {
        _name = name;
        _searchName = name.toLowerCase();
        _url = url;
        _manager = manager;

        ResourcePackSounds sounds = null;

        if (!Nucleus.getPlugin().isTesting()) {

            File folder = new File(Nucleus.getPlugin().getDataFolder(), "sounds");
            if (folder.exists() || (!folder.exists() && folder.mkdirs())) {

                File file = new File(folder, _name + ".yml");
                if (file.exists()) {
                    sounds = loadSounds(file);
                }
            }
        }

        _sounds = sounds != null ? sounds : new ResourcePackSounds(this, null);
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getSearchName() {
        return _searchName;
    }

    @Override
    public String getUrl() {
        return _url;
    }

    @Override
    public boolean apply(Player player) {
        return _manager.get(player).next(this);
    }

    @Override
    public boolean remove(Player player) {
        return _manager.get(player).remove(this);
    }

    @Override
    public ResourcePackSounds getSounds() {
        return _sounds;
    }

    @Nullable
    private ResourcePackSounds loadSounds(File file) {
        IDataNode dataNode = new YamlDataNode(Nucleus.getPlugin(), file);
        dataNode.load();

        return new ResourcePackSounds(this, dataNode);
    }
}
