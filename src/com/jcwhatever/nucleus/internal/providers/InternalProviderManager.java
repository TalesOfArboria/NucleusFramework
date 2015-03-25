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

package com.jcwhatever.nucleus.internal.providers;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.providers.bankitems.BankItemsProvider;
import com.jcwhatever.nucleus.internal.providers.economy.NucleusEconomyProvider;
import com.jcwhatever.nucleus.internal.providers.economy.VaultEconomyBankProvider;
import com.jcwhatever.nucleus.internal.providers.economy.VaultEconomyProvider;
import com.jcwhatever.nucleus.internal.providers.friends.NucleusFriendsProvider;
import com.jcwhatever.nucleus.internal.providers.jail.NucleusJailProvider;
import com.jcwhatever.nucleus.internal.providers.permissions.BukkitProvider;
import com.jcwhatever.nucleus.internal.providers.permissions.VaultProvider;
import com.jcwhatever.nucleus.internal.providers.selection.NucleusSelectionProvider;
import com.jcwhatever.nucleus.internal.providers.selection.WorldEditSelectionProvider;
import com.jcwhatever.nucleus.internal.providers.storage.YamlStorageProvider;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.providers.IPlayerLookupProvider;
import com.jcwhatever.nucleus.providers.IProvider;
import com.jcwhatever.nucleus.providers.IProviderManager;
import com.jcwhatever.nucleus.providers.IRegionSelectProvider;
import com.jcwhatever.nucleus.providers.IStorageProvider;
import com.jcwhatever.nucleus.providers.bankitems.IBankItemsProvider;
import com.jcwhatever.nucleus.providers.economy.EconomyBankWrapper;
import com.jcwhatever.nucleus.providers.economy.EconomyWrapper;
import com.jcwhatever.nucleus.providers.economy.IBankEconomyProvider;
import com.jcwhatever.nucleus.providers.economy.IEconomyProvider;
import com.jcwhatever.nucleus.providers.friends.IFriendsProvider;
import com.jcwhatever.nucleus.providers.jail.IJailProvider;
import com.jcwhatever.nucleus.providers.npc.INpcProvider;
import com.jcwhatever.nucleus.providers.permissions.IPermissionsProvider;
import com.jcwhatever.nucleus.storage.DataPath;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.YamlDataNode;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Internal provider manager implementation.
 */
public final class InternalProviderManager implements IProviderManager {

    private IDataNode _dataNode;

    // names of all providers that were found
    private Map<String, String> _providerNames = new HashMap<>(25);
    private Multimap<String, String> _providerNamesByApi =
            MultimapBuilder.hashKeys().hashSetValues().build();

    // primary providers in use
    private Set<IProvider> _providers = new HashSet<>(25);

    private volatile IPlayerLookupProvider _playerLookup;
    private volatile IFriendsProvider _friends;
    private volatile IPermissionsProvider _permissions;
    private volatile IRegionSelectProvider _regionSelect;
    private volatile IEconomyProvider _economy;
    private volatile IBankItemsProvider _bankItems;
    private volatile INpcProvider _npc;
    private volatile IJailProvider _jail;
    private volatile IStorageProvider _defaultStorage;

    private final YamlStorageProvider _yamlStorage = new YamlStorageProvider();

    // keyed to plugin name
    private final Map<String, IStorageProvider> _pluginStorage = new HashMap<>(35);

    // keyed to provider name
    private final Map<String, IStorageProvider> _storageProviders = new HashMap<>(10);

    private boolean _isProvidersLoading;

    public InternalProviderManager() {
        _storageProviders.put(_yamlStorage.getInfo().getSearchName(), _yamlStorage);

        // register names of default providers
        addName(BankItemsProvider.NAME, IBankItemsProvider.class);
        addName(NucleusEconomyProvider.NAME, IEconomyProvider.class);
        addName(VaultEconomyProvider.NAME, IEconomyProvider.class);
        addName(NucleusFriendsProvider.NAME, IFriendsProvider.class);
        addName(NucleusJailProvider.NAME, IJailProvider.class);
        addName(BukkitProvider.NAME, IPermissionsProvider.class);
        addName(VaultProvider.NAME, IPermissionsProvider.class);
        addName(NucleusSelectionProvider.NAME, IRegionSelectProvider.class);
        addName(WorldEditSelectionProvider.NAME, IRegionSelectProvider.class);
        addName(YamlStorageProvider.NAME, IStorageProvider.class);

        _dataNode = new YamlDataNode(Nucleus.getPlugin(), new DataPath("providers"));

        // setup preferred internal bank items
        String prefBankItems = getPreferred(IBankItemsProvider.class);
        if (BankItemsProvider.NAME.equalsIgnoreCase(prefBankItems)) {
            _bankItems = add(new BankItemsProvider());
        }

        // setup preferred internal economy
        String prefEcon = getPreferred(IEconomyProvider.class);
        if (NucleusEconomyProvider.NAME.equalsIgnoreCase(prefEcon)) {
            _economy = add(new NucleusEconomyProvider(Nucleus.getPlugin()));
        }
        else if (VaultEconomyProvider.NAME.equalsIgnoreCase(prefEcon) &&
                VaultEconomyProvider.hasVaultEconomy()) {
            _economy = add(VaultEconomyBankProvider.hasBankEconomy()
                    ? new VaultEconomyBankProvider()
                    : new VaultEconomyProvider());
        }

        // setup preferred internal friends
        String prefFriends = getPreferred(IFriendsProvider.class);
        if (NucleusFriendsProvider.NAME.equalsIgnoreCase(prefFriends)) {
            _friends = add(new NucleusFriendsProvider());
        }

        // setup preferred internal jail
        String prefJail = getPreferred(IJailProvider.class);
        if (NucleusJailProvider.NAME.equalsIgnoreCase(prefJail)) {
            _jail = add(new NucleusJailProvider());
        }

        // setup preferred internal permissions
        String prefPerm = getPreferred(IPermissionsProvider.class);
        if (BukkitProvider.NAME.equalsIgnoreCase(prefPerm)) {
            _permissions = add(new BukkitProvider());
        }
        else if (VaultProvider.NAME.equalsIgnoreCase(prefPerm)) {
            _permissions = add(new VaultProvider());
        }

        // setup preferred internal region selection
        String prefSelect = getPreferred(IRegionSelectProvider.class);
        if (NucleusSelectionProvider.NAME.equalsIgnoreCase(prefSelect) &&
                !WorldEditSelectionProvider.isWorldEditInstalled()) {
            _regionSelect = add(new NucleusSelectionProvider());
        }
        else if (WorldEditSelectionProvider.NAME.equalsIgnoreCase(prefSelect) &&
                WorldEditSelectionProvider.isWorldEditInstalled()) {
            _regionSelect = add(new WorldEditSelectionProvider());
        }
    }

    /**
     * Enable all providers.
     */
    public void enableProviders() {

        for (IProvider provider : _providers) {
            provider.registerTypes();
        }

        for (IProvider provider : _providers) {
            provider.enable();
        }
    }

    /**
     * Add a provider.
     *
     * @param provider  The provider to add.
     *
     * @return  True if the provider was added. Otherwise false.
     */
    public boolean addProvider(IProvider provider) {
        PreCon.notNull(provider);
        PreCon.isValid(_isProvidersLoading, "Cannot set providers outside of provider load time.");

        boolean isAdded = false;

        if (provider instanceof IPlayerLookupProvider) {
            addName(provider, IPlayerLookupProvider.class);
            if (remove(_playerLookup, IPlayerLookupProvider.class)) {
                _playerLookup = add(provider);
                isAdded = true;
            }
        }

        if (provider instanceof IFriendsProvider) {
            addName(provider, IFriendsProvider.class);
            if (remove(_friends, IFriendsProvider.class)) {
                _friends = add(provider);
                isAdded = true;
            }
        }

        if (provider instanceof IPermissionsProvider) {
            addName(provider, IPermissionsProvider.class);
            if (remove(_permissions, IPermissionsProvider.class)) {
                _permissions = add(provider);
                isAdded = true;
            }
        }

        if (provider instanceof IRegionSelectProvider) {
            addName(provider, IRegionSelectProvider.class);
            if (remove(_regionSelect, IRegionSelectProvider.class)) {
                _regionSelect = add(provider);
                isAdded = true;
            }
        }

        if (provider instanceof IBankItemsProvider) {
            addName(provider, IBankItemsProvider.class);
            if (remove(_bankItems, IBankItemsProvider.class)) {
                _bankItems = add(_bankItems);
                isAdded = true;
            }
        }

        if (provider instanceof IEconomyProvider) {
            addName(provider, IEconomyProvider.class);
            if (remove(_economy, IEconomyProvider.class)) {
                add(_economy);
                _economy = provider instanceof EconomyWrapper
                        ? (IEconomyProvider) provider
                        : provider instanceof IBankEconomyProvider
                        ? new EconomyBankWrapper((IBankEconomyProvider) provider)
                        : new EconomyWrapper((IEconomyProvider) provider);
                isAdded = true;
            }
        }

        if (provider instanceof IStorageProvider) {
            addName(provider, IStorageProvider.class);
            if (remove(_defaultStorage, IStorageProvider.class)) {
                _defaultStorage = add(provider);
            }
            registerStorageProvider((IStorageProvider) provider);
            isAdded = true;
        }

        if (provider instanceof INpcProvider) {
            addName(provider, INpcProvider.class);
            if (remove(_npc, INpcProvider.class)) {
                _npc = add(provider);
                isAdded = true;
            }
        }

        if (provider instanceof IJailProvider) {
            addName(provider, IJailProvider.class);
            if (remove(_jail, IJailProvider.class)) {
                _jail = add(provider);
                isAdded = true;
            }
        }

        return isAdded;
    }

    @Override
    public boolean setPreferred(Class<? extends IProvider> apiType, @Nullable String providerName) {
        PreCon.notNull(apiType);
        PreCon.isValid(isProviderInterface(apiType),
                "Invalid service provider API class specified for parameter 'apiType'.");

        _dataNode.set("preferred." + apiType.getSimpleName(), providerName);
        _dataNode.save();

        return true;
    }

    @Nullable
    @Override
    public String getPreferred(Class<? extends IProvider> apiType) {
        PreCon.notNull(apiType);
        PreCon.isValid(isProviderInterface(apiType),
                "Invalid service provider API class specified for parameter 'apiType'.");

        return _dataNode.getString("preferred." + apiType.getSimpleName());
    }

    @Override
    public IPlayerLookupProvider getPlayerLookupProvider() {
        return _playerLookup;
    }

    @Override
    public IFriendsProvider getFriendsProvider() {
        if (_friends == null)
            _friends = new NucleusFriendsProvider();

        return _friends;
    }

    @Override
    public IPermissionsProvider getPermissionsProvider() {
        if (_permissions == null) {
            _permissions = VaultProvider.hasVaultPermissions()
                    ? VaultProvider.getVaultProvider()
                    : new BukkitProvider();
        }
        return _permissions;
    }

    @Override
    public IRegionSelectProvider getRegionSelectionProvider() {
        return _regionSelect;
    }

    @Override
    public IBankItemsProvider getBankItemsProvider() {

        if (_bankItems == null)
            _bankItems = new BankItemsProvider();

        return _bankItems;
    }

    @Override
    public IEconomyProvider getEconomyProvider() {
        return _economy;
    }

    @Nullable
    @Override
    public INpcProvider getNpcProvider() {
        return _npc;
    }

    @Override
    public IJailProvider getJailProvider() {
        return _jail;
    }

    @Override
    public IStorageProvider getStorageProvider() {
        return _defaultStorage != null ? _defaultStorage : _yamlStorage;
    }

    @Override
    public IStorageProvider getStorageProvider(Plugin plugin) {
        PreCon.notNull(plugin);

        synchronized (_pluginStorage) {

            IStorageProvider pluginProvider = _pluginStorage.get(plugin.getName().toLowerCase());
            return pluginProvider != null ? pluginProvider : getStorageProvider();
        }
    }

    @Override
    public void setStorageProvider(Plugin plugin, IStorageProvider storageProvider) {
        PreCon.notNull(plugin);
        PreCon.notNull(storageProvider);

        IDataNode dataNode = _dataNode.getNode("storage");

        synchronized (_pluginStorage) {

            List<String> pluginNames = dataNode.getStringList(storageProvider.getInfo().getName(),
                    new ArrayList<String>(5));

            assert pluginNames != null;

            pluginNames.add(plugin.getName());
            dataNode.set(storageProvider.getInfo().getName(), pluginNames);
        }

        dataNode.save();
    }

    @Nullable
    @Override
    public IStorageProvider getStorageProvider(String name) {
        PreCon.notNullOrEmpty(name);

        return _storageProviders.get(name.toLowerCase());
    }

    @Override
    public List<IStorageProvider> getStorageProviders() {
        return new ArrayList<>(_storageProviders.values());
    }

    @Override
    public Collection<String> getAllProviderNames() {
        return new ArrayList<>(_providerNames.values());
    }

    @Override
    public Collection<String> getProviderNames(Class<? extends IProvider> apiType) {
        PreCon.notNull(apiType);
        PreCon.isValid(isProviderInterface(apiType),
                "Invalid service provider API class specified for parameter 'apiType'.");

        return new HashSet<>(_providerNamesByApi.get(apiType.getSimpleName()));
    }

    public void registerStorageProvider(IStorageProvider storageProvider) {
        PreCon.notNull(storageProvider);

        IStorageProvider previous = _storageProviders.put(
                storageProvider.getInfo().getSearchName(), storageProvider);

        if (previous instanceof IDisposable && previous != storageProvider) {
            ((IDisposable) previous).dispose();
        }

        IDataNode dataNode = _dataNode.getNode("storage");

        List<String> pluginNames = dataNode.getStringList(storageProvider.getInfo().getName(), null);
        if (pluginNames != null) {
            for (String pluginName : pluginNames) {
                _pluginStorage.put(pluginName.toLowerCase(), storageProvider);
            }
        }
    }

    void setLoading(boolean isLoading) {
        _isProvidersLoading = isLoading;

        // add default providers
        if (!isLoading) {
            if (_jail == null) {
                _jail = new NucleusJailProvider();
            }

            if (_regionSelect == null) {
                _regionSelect = WorldEditSelectionProvider.isWorldEditInstalled()
                        ? new WorldEditSelectionProvider()
                        : new NucleusSelectionProvider();
            }

            if (_economy == null) {
                _economy = VaultEconomyProvider.hasVaultEconomy()
                        ? VaultEconomyBankProvider.hasBankEconomy()
                        ? new VaultEconomyBankProvider()
                        : new VaultEconomyProvider()
                        : new NucleusEconomyProvider(Nucleus.getPlugin());
            }

            if (_playerLookup == null)
                _playerLookup = new InternalPlayerLookupProvider(Nucleus.getPlugin());
        }
    }

    // remove provider from primary list, returns true unless specified provider is the
    // preferred provider, in which case the provider is not removed.
    private boolean remove(@Nullable IProvider provider, Class<? extends IProvider> apiType) {
        if (provider == null)
            return true;

        String preferred = _dataNode.getString("preferred." + apiType.getSimpleName());
        if (preferred != null && provider.getInfo().getSearchName().equals(preferred))
            return false;

        _providers.remove(provider);
        return true;
    }

    private <T extends IProvider> T add(IProvider provider) {
        _providers.add(provider);

        @SuppressWarnings("unchecked")
        T cast = (T)provider;

        return cast;
    }

    private void addName(IProvider provider, Class<? extends IProvider> api) {
        _providerNames.put(provider.getInfo().getSearchName(), provider.getInfo().getName());
        _providerNamesByApi.put(api.getSimpleName(), provider.getInfo().getName());
    }

    private void addName(String name, Class<?extends IProvider> api) {
        _providerNames.put(name.toLowerCase(), name);
        _providerNamesByApi.put(api.getSimpleName(), name);
    }

    private boolean isProviderInterface(Class<?> providerInterface) {
        return providerInterface.equals(IPlayerLookupProvider.class) ||
                providerInterface.equals(IFriendsProvider.class) ||
                providerInterface.equals(IPermissionsProvider.class) ||
                providerInterface.equals(IRegionSelectProvider.class) ||
                providerInterface.equals(IBankItemsProvider.class) ||
                providerInterface.equals(IEconomyProvider.class) ||
                providerInterface.equals(IStorageProvider.class) ||
                providerInterface.equals(INpcProvider.class) ||
                providerInterface.equals(IJailProvider.class);
    }
}
