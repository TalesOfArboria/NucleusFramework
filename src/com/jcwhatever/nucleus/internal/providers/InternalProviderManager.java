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
    private final Map<String, IStorageProvider> _pluginStorage = new HashMap<>(50);

    // keyed to provider name
    private final Map<String, IStorageProvider> _storageProviders = new HashMap<>(10);

    private boolean _isProvidersLoading;

    public InternalProviderManager() {
        _storageProviders.put(_yamlStorage.getInfo().getSearchName(), _yamlStorage);
    }

    public void enableProviders() {

        for (IProvider provider : _providers) {
            provider.registerTypes();
        }

        for (IProvider provider : _providers) {
            provider.enable();
        }
    }

    public boolean addProvider(IProvider provider) {
        PreCon.notNull(provider);
        PreCon.isValid(_isProvidersLoading, "Cannot set providers outside of provider load time.");

        boolean isAdded = false;

        if (provider instanceof IPlayerLookupProvider) {
            remove(_playerLookup);
            _playerLookup = add(provider);
            isAdded = true;
        }

        if (provider instanceof IFriendsProvider) {
            remove(_friends);
            _friends = add(provider);
            isAdded = true;
        }

        if (provider instanceof IPermissionsProvider) {
            remove(_permissions);
            _permissions = add(provider);
            isAdded = true;
        }

        if (provider instanceof IRegionSelectProvider) {
            remove(_regionSelect);
            _regionSelect = add(provider);
            isAdded = true;
        }

        if (provider instanceof IBankItemsProvider) {
            remove(_bankItems);
            _bankItems = add(_bankItems);
            isAdded = true;
        }

        if (provider instanceof IEconomyProvider) {
            remove(_economy);
            add(_economy);
            _economy = provider instanceof EconomyWrapper
                    ? (IEconomyProvider)provider
                    : provider instanceof IBankEconomyProvider
                    ? new EconomyBankWrapper((IBankEconomyProvider)provider)
                    : new EconomyWrapper((IEconomyProvider)provider);
            isAdded = true;
        }

        if (provider instanceof IStorageProvider) {
            remove(_defaultStorage);
            _defaultStorage = add(provider);
            registerStorageProvider((IStorageProvider) provider);
            isAdded = true;
        }

        if (provider instanceof INpcProvider) {
            remove(_npc);
            _npc = add(provider);
            isAdded = true;
        }

        if (provider instanceof IJailProvider) {
            remove(_jail);
            _jail = add(provider);
            isAdded = true;
        }

        return isAdded;
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

        IDataNode dataNode = getDataNode().getNode("storage");

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

    public void registerStorageProvider(IStorageProvider storageProvider) {
        PreCon.notNull(storageProvider);

        IStorageProvider previous = _storageProviders.put(
                storageProvider.getInfo().getSearchName(), storageProvider);

        if (previous instanceof IDisposable && previous != storageProvider) {
            ((IDisposable) previous).dispose();
        }

        IDataNode dataNode = getDataNode().getNode("storage");

        List<String> pluginNames = dataNode.getStringList(storageProvider.getInfo().getName(), null);
        if (pluginNames != null) {
            for (String pluginName : pluginNames) {
                _pluginStorage.put(pluginName.toLowerCase(), storageProvider);
            }
        }
    }

    void setLoading(boolean isLoading) {
        _isProvidersLoading = isLoading;

        // set default providers
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

    private IDataNode getDataNode() {
        if (_dataNode == null) {
            _dataNode = new YamlDataNode(Nucleus.getPlugin(), new DataPath("providers"));
        }
        return _dataNode;
    }

    private void remove(@Nullable IProvider provider) {
        if (provider != null) {
            _providers.remove(provider);
        }
    }

    private <T extends IProvider> T add(IProvider provider) {
        _providers.add(provider);

        @SuppressWarnings("unchecked")
        T cast = (T)provider;

        return cast;
    }
}
