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

package com.jcwhatever.nucleus.internal.providers.economy;

import com.google.common.collect.MapMaker;
import com.jcwhatever.nucleus.internal.providers.InternalProviderInfo;
import com.jcwhatever.nucleus.providers.Provider;
import com.jcwhatever.nucleus.providers.economy.IAccount;
import com.jcwhatever.nucleus.providers.economy.ICurrency;
import com.jcwhatever.nucleus.providers.economy.IEconomyProvider;
import com.jcwhatever.nucleus.providers.economy.IEconomyTransaction;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

import java.util.Map;
import java.util.UUID;

/**
 * Vault economy provider.
 */
public class VaultEconomyProvider extends Provider implements IEconomyProvider {

    public static final String NAME = "VaultEconomy";

    public static boolean hasVaultEconomy() {

        Plugin vault = Bukkit.getPluginManager().getPlugin("Vault");
        if (vault == null)
            return false;

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        return rsp != null && rsp.getProvider() != null && rsp.getProvider().isEnabled();
    }

    private final Map<UUID, VaultAccount> _accounts =
            new MapMaker().weakValues().concurrencyLevel(1).initialCapacity(100).makeMap();

    private Economy _economy;
    private VaultCurrency _currency;

    public VaultEconomyProvider() {
        setInfo(new InternalProviderInfo(this.getClass(),
                NAME, "Vault economy provider."));
    }

    @Override
    public ICurrency getCurrency() {
        if (_currency == null)
            getEconomy(); // load economy and currency

        return _currency;
    }

    @Override
    public IAccount getAccount(UUID playerId) {
        PreCon.notNull(playerId);

        VaultAccount account = _accounts.get(playerId);
        if (account == null) {
            account = new VaultAccount(playerId, null, getEconomy());
            _accounts.put(playerId, account);
        }

        return account;
    }

    @Override
    public IEconomyTransaction createTransaction() {
        return new NucleusTransaction();
    }

    @Override
    public Object getHandle() {
        return getEconomy();
    }

    public Economy getEconomy() {
        if (_economy == null) {
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
            _economy = rsp.getProvider();
            _currency = new VaultCurrency(_economy);
        }

        return _economy;
    }
}
