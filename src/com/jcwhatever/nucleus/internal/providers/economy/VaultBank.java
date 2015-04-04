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

import com.jcwhatever.nucleus.providers.economy.IAccount;
import com.jcwhatever.nucleus.providers.economy.IBank;
import com.jcwhatever.nucleus.providers.economy.ICurrency;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Result;
import com.jcwhatever.nucleus.utils.observer.result.FutureResultAgent;
import com.jcwhatever.nucleus.utils.observer.result.FutureResultAgent.Future;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nullable;

class VaultBank implements IBank {

    private final String _name;
    private final Economy _economy;
    private final VaultEconomyProvider _provider;
    private Result<UUID> _ownerId;

    public VaultBank(VaultEconomyProvider provider, String name, Economy economy) {
        _provider = provider;
        _name = name;
        _economy = economy;
    }

    @Override
    public String getName() {
        return _name;
    }

    /**
     * Because vault does not support getting bank owner id,
     * Results may return null if the owner is not online.
     */
    @Nullable
    @Override
    public UUID getOwnerId() {

        if (_ownerId == null) {

            Collection<? extends Player> players = Bukkit.getOnlinePlayers();

            UUID ownerId = null;

            for (Player player : players) {
                EconomyResponse response = _economy.isBankOwner(_name, Bukkit.getOfflinePlayer(player.getUniqueId()));
                if (response.type == ResponseType.NOT_IMPLEMENTED)
                    break;

                if (response.type == ResponseType.SUCCESS) {
                    ownerId = player.getUniqueId();
                    break;
                }
            }

            _ownerId = new Result<UUID>(false, ownerId);
        }

        return _ownerId.getResult();
    }

    @Override
    public double getBalance() {
        EconomyResponse response = _economy.bankBalance(_name);
        if (response.type == ResponseType.NOT_IMPLEMENTED)
            throw new UnsupportedOperationException();

        return response.amount;
    }

    @Override
    public double getBalance(ICurrency currency) {
        PreCon.notNull(currency);

        double balance = getBalance();
        return balance * currency.getConversionFactor();
    }

    @Override
    public boolean hasAccount(UUID playerId) {
        PreCon.notNull(playerId);

        EconomyResponse response = _economy.isBankMember(_name, Bukkit.getOfflinePlayer(playerId));
        if (response.type == ResponseType.NOT_IMPLEMENTED)
            throw new UnsupportedOperationException();

        return response.type == ResponseType.SUCCESS;
    }

    @Override
    @Nullable
    public IAccount getAccount(UUID playerId) {

        if (!hasAccount(playerId))
            return null;

        return new VaultAccount(_provider, playerId, this, _economy);
    }

    @Override
    public IAccount createAccount(UUID playerId) {

        if (!_economy.createPlayerAccount(Bukkit.getOfflinePlayer(playerId), _name))
            return null;

        return new VaultAccount(_provider, playerId, this, _economy);
    }

    /**
     * Cannot delete a vault bank account.
     */
    @Override
    public Future<Void> deleteAccount(UUID playerId) {
        return new FutureResultAgent<Void>().error(null, "Vault accounts cannot be deleted.");
    }

    @Override
    public Object getHandle() {
        return _economy;
    }
}
