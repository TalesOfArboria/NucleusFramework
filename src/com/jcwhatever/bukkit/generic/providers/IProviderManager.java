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

package com.jcwhatever.bukkit.generic.providers;

import org.bukkit.plugin.Plugin;

/**
 * Interface for the provider manager.
 */
public interface IProviderManager {

    IPermissionsProvider getPermissionsProvider();

    /**
     * Get the default data storage provider.
     */
    IStorageProvider getStorageProvider();

    /**
     * Set the default data storage provider.
     *
     * <p>Can only be set while GenericsLib is loading providers.</p>
     *
     * @param storageProvider  The storage provider.
     */
    void setStorageProvider(IStorageProvider storageProvider);

    /**
     * Get the data storage provider for a plugin.
     *
     * @param plugin  The plugin.
     */
    IStorageProvider getStorageProvider(Plugin plugin);

    /**
     * Set the data storage provider for a specific plugin.
     *
     * <p>Can only be set while GenericsLib is loading providers.</p>
     *
     * @param plugin           The plugin.
     * @param storageProvider  The storage provider.
     */
    void setStorageProvider(Plugin plugin, IStorageProvider storageProvider);

    /**
     * Register a storage provider.
     *
     * <p>Can only register while GenericsLib is loading providers.</p>
     *
     * @param storageProvider  The storage provider.
     */
    void registerStorageProvider(IStorageProvider storageProvider);
}
