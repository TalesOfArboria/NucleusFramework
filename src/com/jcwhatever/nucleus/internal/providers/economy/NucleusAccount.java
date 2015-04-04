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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.providers.economy.IAccount;
import com.jcwhatever.nucleus.providers.economy.IBank;
import com.jcwhatever.nucleus.providers.economy.ICurrency;
import com.jcwhatever.nucleus.providers.economy.events.EconDepositEvent;
import com.jcwhatever.nucleus.providers.economy.events.EconWithdrawEvent;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.result.FutureResultAgent;
import com.jcwhatever.nucleus.utils.observer.result.FutureResultAgent.Future;

import java.util.UUID;
import javax.annotation.Nullable;

/**
 * NucleusFramework economy account
 */
class NucleusAccount implements IAccount {

    private final UUID _playerId;
    private final NucleusBank _bank;
    private final NucleusEconomyProvider _provider;
    private final IDataNode _dataNode;

    private double _balance;

    NucleusAccount(NucleusEconomyProvider provider,
                   UUID playerId, @Nullable NucleusBank bank, IDataNode dataNode) {
        _provider = provider;
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
    public synchronized double getBalance() {
        return _balance;
    }

    @Override
    public double getBalance(ICurrency currency) {
        return _balance * currency.getConversionFactor();
    }

    @Override
    public Future<Double> getLatestBalance() {
        return new FutureResultAgent<Double>().success(getBalance());
    }

    @Override
    public Future<Double> getLatestBalance(ICurrency currency) {
        return new FutureResultAgent<Double>().success(getBalance(currency));
    }

    @Nullable
    public Double deposit(double amount) {
        return deposit(amount, _provider.getCurrency());
    }

    @Nullable
    public synchronized Double deposit(double amount, ICurrency currency) {
        PreCon.positiveNumber(amount);
        PreCon.notNull(currency);

        EconDepositEvent event = new EconDepositEvent(this, amount, currency);
        Nucleus.getEventManager().callBukkit(this, event);

        amount = event.getAmount() * currency.getConversionFactor();

        if (amount == 0)
            return amount;

        _balance += event.getAmount();

        if (_bank != null) {
            _bank.incrementBalance(amount);
        }

        _dataNode.set("balance", _balance);
        _dataNode.save();

        return amount;
    }

    @Nullable
    public Double withdraw(double amount) {
        PreCon.positiveNumber(amount);

        return withdraw(amount, _provider.getCurrency());
    }

    @Nullable
    public synchronized Double withdraw(double amount, ICurrency currency) {
        PreCon.positiveNumber(amount);
        PreCon.notNull(currency);

        EconWithdrawEvent event = new EconWithdrawEvent(this, amount, currency);
        Nucleus.getEventManager().callBukkit(this, event);

        amount = event.getConvertedAmount();

        if (amount == 0)
            return amount;

        _balance -= amount;

        if (_bank != null) {
            _bank.incrementBalance(-amount);
        }

        _dataNode.set("balance", _balance);
        _dataNode.save();

        return amount;
    }

    @Override
    public Object getHandle() {
        return this;
    }

    @Override
    public int hashCode() {
        return _playerId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NucleusAccount) {
            NucleusAccount account = (NucleusAccount)obj;

            return account._playerId.equals(_playerId) &&
                    account._bank == _bank;
        }

        return false;
    }
}
