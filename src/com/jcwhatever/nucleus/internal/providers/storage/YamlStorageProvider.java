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

package com.jcwhatever.nucleus.internal.providers.storage;

import com.jcwhatever.nucleus.internal.providers.InternalProviderInfo;
import com.jcwhatever.nucleus.providers.IStorageProvider;
import com.jcwhatever.nucleus.providers.Provider;
import com.jcwhatever.nucleus.storage.DataPath;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.YamlDataNode;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.plugin.Plugin;

import java.io.File;

/**
 * Yaml file data storage.
 */
public final class YamlStorageProvider extends Provider implements IStorageProvider {

    public YamlStorageProvider() {
        setInfo(new InternalProviderInfo(this.getClass(),
                "NucleusYaml", "Default YAML based storage provider."));
    }

    @Override
    public boolean remove(Plugin plugin, DataPath path) {
        PreCon.notNull(plugin);
        PreCon.notNull(path);

        File file = YamlDataNode.dataPathToFile(plugin, path);
        return file.exists() && file.delete();
    }

    @Override
    public IDataNode get(Plugin plugin, DataPath path) {
        PreCon.notNull(plugin);
        PreCon.notNull(path);

        return new YamlDataNode(plugin, path);
    }

    @Override
    public boolean has(Plugin plugin, DataPath path) {
        PreCon.notNull(plugin);
        PreCon.notNull(path);

        File file = YamlDataNode.dataPathToFile(plugin, path);
        return file.exists();
    }
}
