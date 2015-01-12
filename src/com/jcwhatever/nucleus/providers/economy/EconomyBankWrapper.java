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

package com.jcwhatever.nucleus.providers.economy;

import com.jcwhatever.nucleus.utils.PreCon;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Wraps economy interfaces to provide economy based events.
 *
 * <p>Used by NucleusFramework to wrap the provider.</p>
 *
 * <p>If the provider wraps its own results, then results are not
 * re-wrapped.</p>
 *
 */
public class EconomyBankWrapper extends EconomyWrapper implements IBankEconomyProvider {

    protected final IBankEconomyProvider _provider;

    public EconomyBankWrapper(IBankEconomyProvider provider) {
        super(provider);

        _provider = provider;
    }

    @Override
    public List<IBank> getBanks() {
        List<IBank> banks = _provider.getBanks();
        List<IBank> wrapped = new ArrayList<>(banks.size());

        for (IBank bank : banks) {
            if (bank instanceof BankWrapper) {
                wrapped.add(bank);
            }
            else {
                wrapped.add(new BankWrapper(bank));
            }
        }

        return wrapped;
    }

    @Nullable
    @Override
    public IBank getBank(String bankName) {

        IBank bank = _provider.getBank(bankName);
        if (bank == null)
            return null;

        if (bank instanceof BankWrapper)
            return bank;

        return new BankWrapper(bank);
    }

    @Nullable
    @Override
    public IBank createBank(String bankName) {

        IBank bank = _provider.createBank(bankName);
        if (bank == null)
            return null;

        if (bank instanceof BankWrapper)
            return bank;

        return new BankWrapper(bank);
    }

    @Nullable
    @Override
    public IBank createBank(String bankName, UUID playerId) {

        IBank bank = _provider.createBank(bankName, playerId);
        if (bank == null)
            return null;

        if (bank instanceof BankWrapper)
            return bank;

        return new BankWrapper(bank);
    }

    @Override
    public boolean deleteBank(String bankName) {
        return _provider.deleteBank(bankName);
    }

    public static class BankWrapper implements IBank {

        private final IBank _bank;

        public BankWrapper (IBank bank) {
            PreCon.notNull(bank);

            _bank = bank;
        }

        @Nullable
        @Override
        public UUID getOwnerId() {
            return _bank.getOwnerId();
        }

        @Override
        public double getBalance() {
            return _bank.getBalance();
        }

        @Override
        public double getBalance(ICurrency currency) {
            return getBalance() * currency.getConversionFactor();
        }

        @Override
        public boolean hasAccount(UUID playerId) {
            return _bank.hasAccount(playerId);
        }

        @Nullable
        @Override
        public IAccount getAccount(UUID playerId) {

            IAccount account = _bank.getAccount(playerId);
            if (account == null)
                return null;

            if (account instanceof AccountWrapper)
                return account;

            return new AccountWrapper(account);
        }

        @Override
        public List<IAccount> getAccounts() {

            List<IAccount> accounts = _bank.getAccounts();
            List<IAccount> wrapped = new ArrayList<>(accounts.size());

            for (IAccount account : accounts) {
                if (account instanceof AccountWrapper)
                    wrapped.add(account);
                else
                    wrapped.add(new AccountWrapper(account));
            }

            return wrapped;
        }

        @Nullable
        @Override
        public IAccount createAccount(UUID playerId) {

            IAccount account = _bank.createAccount(playerId);
            if (account == null)
                return null;

            if (account instanceof AccountWrapper)
                return account;

            return new AccountWrapper(account);
        }

        @Override
        public boolean deleteAccount(UUID playerId) {
            return _bank.deleteAccount(playerId);
        }

        @Override
        public String getName() {
            return _bank.getName();
        }

        @Override
        public int hashCode() {
            return _bank.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof BankWrapper) {
                return ((BankWrapper) obj)._bank.equals(_bank);
            }

            return _bank.equals(obj);
        }

        @Override
        public IBank getHandle() {
            return _bank;
        }
    }
}
