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

package com.jcwhatever.nucleus.providers;

import com.jcwhatever.nucleus.providers.bankitems.IBankItemsProvider;
import com.jcwhatever.nucleus.providers.economy.IEconomyProvider;
import com.jcwhatever.nucleus.providers.friends.IFriendsProvider;
import com.jcwhatever.nucleus.providers.npc.INpcProvider;
import com.jcwhatever.nucleus.providers.permissions.IPermissionsProvider;

import org.bukkit.plugin.Plugin;

import java.util.List;
import javax.annotation.Nullable;

/**
 * Interface for the provider manager.
 */
public interface IProviderManager {

    /**
     * Get the player lookup provider.
     */
    IPlayerLookupProvider getPlayerLookupProvider();

    /**
     * Get the friends provider.
     */
    IFriendsProvider getFriendsProvider();

    /**
     * Get the permissions provider.
     */
    IPermissionsProvider getPermissionsProvider();

    /**
     * Get the region selection provider.
     */
    IRegionSelectProvider getRegionSelectionProvider();

    /**
     * Get the bank item provider.
     */
    IBankItemsProvider getBankItemsProvider();

    /**
     * Get the economy provider.
     */
    IEconomyProvider getEconomyProvider();

    /**
     * Get the default data storage provider.
     */
    IStorageProvider getStorageProvider();

    /**
     * Get the data storage provider for a plugin.
     *
     * @param plugin  The plugin.
     */
    IStorageProvider getStorageProvider(Plugin plugin);

    /**
     * Set the data storage provider for a specific plugin.
     * Only sets the setting for the plugin. A restart of the
     * server is required in order for the setting to take effect.
     *
     * @param plugin           The plugin.
     * @param storageProvider  The storage provider.
     */
    void setStorageProvider(Plugin plugin, IStorageProvider storageProvider);

    /**
     * Get a storage provider by name.
     *
     * @param name  The name of the storage provider.
     *
     * @return Null if not found.
     */
    @Nullable
    IStorageProvider getStorageProvider(String name);

    /**
     * Get all registered storage providers.
     */
    List<IStorageProvider> getStorageProviders();

    /**
     * Get the NPC provider.
     *
     * @return  The NPC provider or null if there is none.
     */
    @Nullable
    INpcProvider getNpcProvider();
}
