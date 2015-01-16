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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.events.economy.EconDepositEvent;
import com.jcwhatever.nucleus.events.economy.EconWithdrawEvent;
import com.jcwhatever.nucleus.providers.economy.EconomyBankWrapper.BankWrapper;
import com.jcwhatever.nucleus.utils.PreCon;

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
public class EconomyWrapper implements IEconomyProvider {

    protected final IEconomyProvider _provider;

    public EconomyWrapper(IEconomyProvider provider) {
        PreCon.notNull(provider);

        _provider = provider;
    }

    @Override
    public ICurrency getCurrency() {
        return _provider.getCurrency();
    }

    @Override
    public IAccount getAccount(UUID playerId) {

        IAccount account = _provider.getAccount(playerId);
        if (account == null)
            return null;

        if (account instanceof AccountWrapper)
            return account;

        return new AccountWrapper(account);
    }

    @Override
    public IEconomyTransaction createTransaction() {
        return _provider.createTransaction();
    }

    @Override
    public IEconomyProvider getHandle() {
        return _provider;
    }

    public static class AccountWrapper implements IAccount {

        private final IAccount _account;
        private IBank _bank;

        public AccountWrapper(IAccount account) {
            PreCon.notNull(account);

            _account = account;
        }

        @Override
        public UUID getPlayerId() {
            return _account.getPlayerId();
        }

        @Nullable
        @Override
        public IBank getBank() {
            IBank bank = _account.getBank();
            if (bank == null)
                return null;

            if (bank instanceof BankWrapper)
                return bank;

            if (_bank != null && _bank.equals(bank))
                return _bank;

            return _bank = new BankWrapper(bank);
        }

        @Override
        public double getBalance() {
            return _account.getBalance();
        }

        @Override
        public double getBalance(ICurrency currency) {
            PreCon.notNull(currency);

            return getBalance() * currency.getConversionFactor();
        }

        @Override
        public boolean deposit(double amount) {

            EconDepositEvent event = onDeposit(_account, amount);
            return _account.deposit(event.getAmount());
        }

        @Override
        public boolean deposit(double amount, ICurrency currency) {
            PreCon.notNull(currency);

            return deposit(amount * currency.getConversionFactor());
        }

        @Override
        public boolean withdraw(double amount) {

            EconWithdrawEvent event = onWithdraw(_account, amount);
            return _account.withdraw(event.getAmount());
        }

        @Override
        public boolean withdraw(double amount, ICurrency currency) {
            PreCon.notNull(currency);

            return withdraw(amount * currency.getConversionFactor());
        }

        @Override
        public int hashCode() {
            return _account.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof AccountWrapper) {
                return ((AccountWrapper) obj)._account.equals(_account);
            }

            return _account.equals(obj);
        }

        @Override
        public IAccount getHandle() {
            return _account;
        }

        protected EconDepositEvent onDeposit(IAccount account, double amount) {
            EconDepositEvent event = new EconDepositEvent(account, amount);
            Nucleus.getEventManager().callBukkit(this, event);

            return event;
        }

        protected EconWithdrawEvent onWithdraw(IAccount account, double amount) {
            EconWithdrawEvent event = new EconWithdrawEvent(account, amount);
            Nucleus.getEventManager().callBukkit(this, event);

            return event;
        }
    }
}
