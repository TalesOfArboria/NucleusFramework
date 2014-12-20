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

package com.jcwhatever.bukkit.generic.internal.providers.economy;

import com.jcwhatever.bukkit.generic.providers.economy.IAccount;
import com.jcwhatever.bukkit.generic.providers.economy.IBank;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import java.util.UUID;
import javax.annotation.Nullable;

/**
 * A Vault bank account
 */
public final class VaultAccount implements IAccount {

    private final UUID _playerId;
    private final Economy _economy;

    VaultAccount(UUID ownerId, Economy economy) {
        _playerId = ownerId;
        _economy = economy;
    }

    @Override
    public UUID getPlayerId() {
        return _playerId;
    }

    @Nullable
    @Override
    public IBank getBank() {
        return null;
    }

    @Override
    public double getBalance() {
        return _economy.getBalance(_playerId.toString());
    }

    @Override
    public boolean deposit(double amount) {
        PreCon.positiveNumber(amount);

        EconomyResponse response = _economy.depositPlayer(_playerId.toString(), amount);

        return response.transactionSuccess();
    }

    @Override
    public boolean withdraw(double amount) {
        PreCon.positiveNumber(amount);

        EconomyResponse response = _economy.withdrawPlayer(_playerId.toString(), amount);

        return response.transactionSuccess();
    }

    @Override
    public int hashCode() {
        return _playerId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VaultAccount) {
            VaultAccount account = (VaultAccount) obj;

            return account._playerId.equals(_playerId);
        }

        return false;
    }
}
