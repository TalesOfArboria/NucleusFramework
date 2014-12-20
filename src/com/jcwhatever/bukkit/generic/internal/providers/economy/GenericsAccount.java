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
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import java.util.UUID;
import javax.annotation.Nullable;

/**
 * GenericsLib economy account
 */
public final class GenericsAccount implements IAccount {

    private final UUID _playerId;
    private final GenericsBank _bank;
    private final IDataNode _dataNode;

    private double _balance;

    GenericsAccount(UUID playerId, @Nullable GenericsBank bank, IDataNode dataNode) {
        _playerId = playerId;
        _bank = bank;
        _dataNode = dataNode;

        _balance = _dataNode.getDouble("balance", 0);
    }

    @Override
    public UUID getPlayerId() {
        return _playerId;
    }

    @Nullable
    @Override
    public IBank getBank() {
        return _bank;
    }

    @Override
    public double getBalance() {
        return _balance;
    }

    @Override
    public boolean deposit(double amount) {
        PreCon.positiveNumber(amount);

        if (amount == 0)
            return true;

        _balance += amount;

        if (_bank != null) {
            _bank._balance += amount;
        }

        _dataNode.set("balance", _balance);
        _dataNode.saveAsync(null);

        return true;
    }

    @Override
    public boolean withdraw(double amount) {
        PreCon.positiveNumber(amount);

        if (amount == 0)
            return true;

        if (_balance < amount)
            return false;

        _balance -= amount;

        if (_bank != null) {
            _bank._balance -= amount;
        }

        _dataNode.set("balance", _balance);
        _dataNode.saveAsync(null);
        return false;
    }

    @Override
    public int hashCode() {
        return _playerId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GenericsAccount) {
            GenericsAccount account = (GenericsAccount)obj;

            return account._playerId.equals(_playerId) &&
                    account._bank == _bank;
        }

        return false;
    }
}
