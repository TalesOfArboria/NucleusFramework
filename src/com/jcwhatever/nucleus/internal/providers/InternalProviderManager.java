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
import com.jcwhatever.nucleus.internal.providers.economy.NucleusEconomyProvider;
import com.jcwhatever.nucleus.internal.providers.economy.VaultEconomyProvider;
import com.jcwhatever.nucleus.internal.providers.permissions.BukkitProvider;
import com.jcwhatever.nucleus.internal.providers.permissions.VaultProvider;
import com.jcwhatever.nucleus.internal.providers.selection.NucleusSelectionProvider;
import com.jcwhatever.nucleus.internal.providers.selection.WorldEditSelectionProvider;
import com.jcwhatever.nucleus.internal.providers.storage.YamlStorageProvider;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.providers.IPlayerLookupProvider;
import com.jcwhatever.nucleus.providers.IProviderManager;
import com.jcwhatever.nucleus.providers.IRegionSelectProvider;
import com.jcwhatever.nucleus.providers.IStorageProvider;
import com.jcwhatever.nucleus.providers.economy.EconomyBankWrapper;
import com.jcwhatever.nucleus.providers.economy.EconomyWrapper;
import com.jcwhatever.nucleus.providers.economy.IBankEconomyProvider;
import com.jcwhatever.nucleus.providers.economy.IEconomyProvider;
import com.jcwhatever.nucleus.providers.permissions.IPermissionsProvider;
import com.jcwhatever.nucleus.storage.DataPath;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.YamlDataStorage;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Internal provider manager implementation.
 */
public final class InternalProviderManager implements IProviderManager {

    private IDataNode _dataNode;

    private volatile IPlayerLookupProvider _playerLookup;
    private volatile IPermissionsProvider _permissions;
    private volatile IRegionSelectProvider _regionSelect;
    private volatile IEconomyProvider _economy;

    private volatile IStorageProvider _defaultStorage;

    private final YamlStorageProvider _yamlStorage = new YamlStorageProvider();

    // keyed to plugin name
    private final Map<String, IStorageProvider> _pluginStorage = new HashMap<>(50);

    // keyed to provider name
    private final Map<String, IStorageProvider> _storageProviders = new HashMap<>(10);

    boolean _isProvidersLoading;

    public InternalProviderManager() {
        _storageProviders.put(_yamlStorage.getName().toLowerCase(), _yamlStorage);

        _regionSelect = WorldEditSelectionProvider.isWorldEditInstalled()
                ? new WorldEditSelectionProvider()
                : new NucleusSelectionProvider();

        _economy = VaultEconomyProvider.hasVaultEconomy()
                ? new VaultEconomyProvider()
                : new NucleusEconomyProvider(Nucleus.getPlugin());

        _playerLookup = new InternalPlayerLookupProvider(Nucleus.getPlugin());
    }

    @Override
    public IPlayerLookupProvider getPlayerLookupProvider() {
        return _playerLookup;
    }

    @Override
    public void setPlayerLookupProvider(IPlayerLookupProvider lookupProvider) {
        PreCon.notNull(lookupProvider);

        if (_playerLookup instanceof IDisposable) {
            ((IDisposable) _playerLookup).dispose();
        }
        _playerLookup = lookupProvider;
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
    public void setPermissionsProvider(IPermissionsProvider permissionsProvider) {
        PreCon.notNull(permissionsProvider);
        PreCon.isValid(_isProvidersLoading, "Cannot set providers outside of provider load time.");

        _permissions = permissionsProvider;
    }

    @Override
    public IRegionSelectProvider getRegionSelectionProvider() {
        return _regionSelect;
    }

    @Override
    public void setRegionSelectionProvider(IRegionSelectProvider provider) {
        PreCon.notNull(provider);

        if (_regionSelect instanceof IDisposable && provider != _regionSelect) {
            ((IDisposable)_regionSelect).dispose();
        }

        _regionSelect = provider;
    }

    @Override
    public IEconomyProvider getEconomyProvider() {
        return _economy;
    }

    @Override
    public void setEconomyProvider(IEconomyProvider provider) {
        PreCon.notNull(provider);
        PreCon.isValid(_isProvidersLoading, "Cannot register providers outside of provider load time.");

        if (_economy instanceof IDisposable && provider != _economy) {
            ((IDisposable)_economy).dispose();
        }

        IDisposable disposable = (IDisposable) (provider instanceof EconomyWrapper
                ? ((EconomyWrapper) provider).getHandle()
                : provider);

        if (disposable != null)
            disposable.dispose();

        _economy = provider instanceof EconomyWrapper
                ? provider
                : provider instanceof IBankEconomyProvider
                ? new EconomyBankWrapper((IBankEconomyProvider)provider)
                : new EconomyWrapper(provider);
    }

    @Override
    public IStorageProvider getStorageProvider() {
        return _defaultStorage != null ? _defaultStorage : _yamlStorage;
    }

    @Override
    public void setStorageProvider(IStorageProvider storageProvider) {
        PreCon.notNull(storageProvider);
        PreCon.isValid(_isProvidersLoading, "Cannot set providers outside of provider load time.");

        if (_defaultStorage instanceof IDisposable && storageProvider != _defaultStorage) {
            ((IDisposable) _defaultStorage).dispose();
        }

        _defaultStorage = storageProvider;
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

            List<String> pluginNames = dataNode.getStringList(storageProvider.getName(),
                    new ArrayList<String>(5));

            assert pluginNames != null;

            pluginNames.add(plugin.getName());
            dataNode.set(storageProvider.getName(), pluginNames);
        }

        dataNode.saveAsync(null);
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
    public void registerStorageProvider(IStorageProvider storageProvider) {
        PreCon.notNull(storageProvider);
        PreCon.isValid(_isProvidersLoading, "Cannot register providers outside of provider load time.");

        IStorageProvider previous = _storageProviders.put(storageProvider.getName().toLowerCase(), storageProvider);
        if (previous instanceof IDisposable && previous != storageProvider) {
            ((IDisposable) previous).dispose();
        }

        IDataNode dataNode = getDataNode().getNode("storage");

        List<String> pluginNames = dataNode.getStringList(storageProvider.getName(), null);
        if (pluginNames != null) {
            for (String pluginName : pluginNames) {
                _pluginStorage.put(pluginName.toLowerCase(), storageProvider);
            }
        }
    }

    private IDataNode getDataNode() {
        if (_dataNode == null) {
            _dataNode = new YamlDataStorage(Nucleus.getPlugin(), new DataPath("providers"));
        }
        return _dataNode;
    }
}
