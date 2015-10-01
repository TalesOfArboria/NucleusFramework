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
import com.jcwhatever.nucleus.providers.jail.IJailProvider;
import com.jcwhatever.nucleus.providers.kits.IKitProvider;
import com.jcwhatever.nucleus.providers.math.IFastMathProvider;
import com.jcwhatever.nucleus.providers.npc.INpcProvider;
import com.jcwhatever.nucleus.providers.permissions.IPermissionsProvider;
import com.jcwhatever.nucleus.providers.playerlookup.IPlayerLookupProvider;
import com.jcwhatever.nucleus.providers.regionselect.IRegionSelectProvider;
import com.jcwhatever.nucleus.providers.sql.ISqlProvider;
import com.jcwhatever.nucleus.providers.storage.IStorageProvider;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Interface for the provider manager.
 */
public interface IProviderManager {

    /**
     * Set the preferred provider implementation to use for the specified provider interface.
     *
     * <p>Allows setting which provider to use when multiple providers are available for
     * a specific API.</p>
     *
     * <p>A server restart is required for settings to take effect.</p>
     *
     * @param providerType  The service provider type.
     * @param providerName  The name of the provider. Null to clear preferred.
     *
     * @return  True if successful, otherwise false.
     */
    boolean setPreferred(ProviderType providerType, @Nullable String providerName);

    /**
     * Get the name of the preferred provider implementation for the specified
     * provider type.
     *
     * @param providerType  The service provider type.
     *
     * @return  The name of the provider or null if preferred not set.
     */
    @Nullable
    String getPreferred(ProviderType providerType);

    /**
     * Get the provider instance used for the specified service provider type.
     *
     * @param providerType  The service provider type.
     *
     * @param <T>  The provider type.
     *
     * @return  The provider or null if none.
     */
    @Nullable
    <T extends IProvider> T get(ProviderType providerType);

    /**
     * Get the API type of the specified service provider.
     *
     * @param name  The name of the service provider
     *
     * @return  The API type or null if not found.
     */
    @Nullable
    ProviderType getType(String name);

    /**
     * Get the player lookup provider.
     */
    IPlayerLookupProvider getPlayerLookup();

    /**
     * Get the friends provider.
     */
    IFriendsProvider getFriends();

    /**
     * Get the permissions provider.
     */
    IPermissionsProvider getPermissions();

    /**
     * Get the region selection provider.
     */
    IRegionSelectProvider getRegionSelection();

    /**
     * Get the bank item provider.
     */
    IBankItemsProvider getBankItems();

    /**
     * Get the economy provider.
     */
    IEconomyProvider getEconomy();

    /**
     * Get the Sql database provider.
     *
     * @return  The provider or null if not installed.
     */
    @Nullable
    ISqlProvider getSql();

    /**
     * Get the default data storage provider.
     */
    IStorageProvider getStorage();

    /**
     * Get the data storage provider for a plugin.
     *
     * @param plugin  The plugin.
     */
    IStorageProvider getStorage(Plugin plugin);

    /**
     * Set the data storage provider for a specific plugin.
     *
     * <p>Only sets the setting for the plugin. A restart of the server is required
     * in order for the setting to take effect.</p>
     *
     * @param plugin           The plugin.
     * @param storageProvider  The storage provider.
     */
    void setStorage(Plugin plugin, IStorageProvider storageProvider);

    /**
     * Get a storage provider by name.
     *
     * @param name  The name of the storage provider.
     *
     * @return Null if not found.
     */
    @Nullable
    IStorageProvider getStorage(String name);

    /**
     * Get all registered storage providers.
     */
    Collection<IStorageProvider> getStorageProviders();

    /**
     * Get all registered storage providers.
     *
     * @param output  The output collection to add results to.
     *
     * @return  The output collection.
     */
    <T extends Collection<IStorageProvider>> T getStorageProviders(T output);

    /**
     * Get the fast math provider.
     */
    IFastMathProvider getMath();

    /**
     * Get the NPC provider.
     *
     * @return  The NPC provider or null if there is none.
     */
    @Nullable
    INpcProvider getNpcs();

    /**
     * Get the jail provider.
     */
    IJailProvider getJails();

    /**
     * Get the equipment kit provider.
     */
    IKitProvider getKits();

    /**
     * Get the names of all providers that were loaded.
     *
     * <p>Includes the names of providers that were not used.</p>
     */
    Collection<String> getNames();

    /**
     * Get the names of all providers that were loaded.
     *
     * <p>Includes the names of providers that were not used.</p>
     *
     * @param output  The output collection to add results to.
     *
     * @return  The output collection.
     */
    <T extends Collection<String>> T getNames(T output);

    /**
     * Get the names of all providers that can be used for the specified
     * service provider API type.
     *
     * @param providerType  The provider API type.
     */
    Collection<String> getNames(ProviderType providerType);

    /**
     * Get the names of all providers that can be used for the specified
     * service provider API type.
     *
     * @param providerType  The provider API type.
     * @param output        The output collection to add results to.
     *
     * @return  The output collection.
     */
    <T extends Collection<String>> T getNames(ProviderType providerType, T output);
}
