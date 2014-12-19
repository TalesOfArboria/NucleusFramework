/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.storage;

import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.plugin.Plugin;

import java.io.File;

public final class DataStorage {

    private DataStorage() {}

    /**
     * Remove data storage.
     *
     * @param plugin  The owning plugin.
     * @param path    Storage path.
     *
     * @return  True if successful.
     */
    public static boolean removeStorage(Plugin plugin, DataPath path) {
        PreCon.notNull(plugin);
        PreCon.notNull(path);

        File file = YamlDataStorage.convertStoragePathToFile(plugin, path);
        return file.exists() && file.delete();
    }

    /**
     * Get or create data storage.
     *
     * @param plugin  The owning plugin.
     * @param path    Storage path.
     */
    public static IDataNode getStorage(Plugin plugin, DataPath path) {
        PreCon.notNull(plugin);
        PreCon.notNull(path);

        return new YamlDataStorage(plugin, path);
    }

    /**
     * Determine if a data store exists.
     *
     * @param plugin  The owning plugin.
     * @param path    Storage path.
     */
    public static boolean hasStorage(Plugin plugin, DataPath path) {
        PreCon.notNull(plugin);
        PreCon.notNull(path);

        File file = YamlDataStorage.convertStoragePathToFile(plugin, path);
        return file.exists();
    }
}
