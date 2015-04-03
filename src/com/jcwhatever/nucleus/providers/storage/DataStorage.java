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


package com.jcwhatever.nucleus.providers.storage;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.storage.DataPath;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.MemoryDataNode;

import org.bukkit.plugin.Plugin;

/**
 * Static convenience methods for accessing the storage provider.
 */
public final class DataStorage {

    private DataStorage() {}

    private static boolean _isTransientOnly;

    /**
     * Determine if a data store exists.
     *
     * @param plugin  The owning plugin.
     * @param path    Storage path.
     */
    public static boolean has(Plugin plugin, DataPath path) {

        if (_isTransientOnly)
            return false;

        IStorageProvider provider = Nucleus.getProviders().getStorage(plugin);
        return provider.has(plugin, path);
    }

    /**
     * Get or create data storage.
     *
     * @param plugin  The owning plugin.
     * @param path    Storage path.
     */
    public static IDataNode get(Plugin plugin, DataPath path) {

        if (_isTransientOnly)
            return new MemoryDataNode(plugin);

        IStorageProvider provider = Nucleus.getProviders().getStorage(plugin);
        return provider.get(plugin, path);
    }

    /**
     * Remove data storage.
     *
     * @param plugin  The owning plugin.
     * @param path    Storage path.
     *
     * @return  True if successful.
     */
    public static boolean remove(Plugin plugin, DataPath path) {

        if (_isTransientOnly)
            return true;

        IStorageProvider provider = Nucleus.getProviders().getStorage(plugin);
        return provider.remove(plugin, path);
    }

    /**
     * When true, Ensures only transient data nodes are returned.
     *
     * <p>Used for testing.</p>
     *
     * @param transientOnly  True to only return transient memory nodes.
     */
    static void setTransientOnly(boolean transientOnly) {
        _isTransientOnly = transientOnly;
    }
}
