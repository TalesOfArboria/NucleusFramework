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

import com.jcwhatever.nucleus.providers.economy.IBank;
import com.jcwhatever.nucleus.providers.economy.IBankEconomyProvider;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

public class VaultEconomyBankProvider extends VaultEconomyProvider implements IBankEconomyProvider {

    public static boolean hasBankEconomy() {

        if (!VaultEconomyProvider.hasVaultEconomy())
            return false;

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        Economy economy = rsp.getProvider();

        return economy.hasBankSupport();
    }

    @Override
    public List<IBank> getBanks() {

        List<String> bankNames = getEconomy().getBanks();
        List<IBank> banks = new ArrayList<>(bankNames.size());

        for (String bankName : bankNames) {
            banks.add(new VaultBank(bankName, getEconomy()));
        }

        return Collections.unmodifiableList(banks);
    }

    @Nullable
    @Override
    public IBank getBank(String bankName) {
        PreCon.notNull(bankName);

        EconomyResponse response = getEconomy().bankBalance(bankName);
        if (response.type != ResponseType.SUCCESS)
            return null;

        return new VaultBank(bankName, getEconomy());
    }

    @Nullable
    @Override
    public IBank createBank(String bankName) {
        return createBank(bankName, null);
    }

    @Nullable
    @Override
    public IBank createBank(String bankName, UUID playerId) {
        PreCon.notNull(bankName);

        EconomyResponse response = getEconomy().createBank(bankName, playerId != null
                ? Bukkit.getOfflinePlayer(playerId)
                : null);

        if (response.type != ResponseType.SUCCESS)
            return null;

        return new VaultBank(bankName, getEconomy());
    }

    @Override
    public boolean deleteBank(String bankName) {
        PreCon.notNull(bankName);

        return getEconomy().deleteBank(bankName).type == ResponseType.SUCCESS;
    }
}
