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

import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.providers.economy.IAccount;
import com.jcwhatever.nucleus.providers.economy.ICurrency;
import com.jcwhatever.nucleus.providers.economy.IEconomyTransaction;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.future.FutureResultAgent;
import com.jcwhatever.nucleus.utils.observer.future.IFutureResult;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * NucleusFrameworks {@link IEconomyTransaction} transaction implementation.
 *
 * <p>Does not support multiple currency balances.</p>
 *
 * <p>Only works with {@link NucleusAccount} or {@link VaultAccount}.</p>
 */
class NucleusTransaction implements IEconomyTransaction {

    @Localizable static final String _INSUFFICIENT_FUNDS =
            "Account does not have sufficient funds.";

    @Localizable static final String _DEPOSIT_FAILED =
            "Failed to deposit amount: {0: amount} into account: {1: account}";

    @Localizable static final String _WITHDRAW_FAILED =
            "Failed to withdraw amount: {0: amount} into account: {1: account}";

    @Localizable static final String _TRANSACTION_SUCCESS =
            "Transaction success.";

    @Localizable static final String _GLOBAL_BANK =
            "<global>";

    private final Map<IAccount, Double> _balanceDelta = new HashMap<>(5);
    private boolean _isCommitted;
    private String _error;

    @Override
    public synchronized boolean isCommitted() {
        return _isCommitted;
    }

    @Override
    public double getDelta(IAccount account) {
        PreCon.notNull(account);

        Double delta = _balanceDelta.get(account);
        if (delta == null)
            return 0;

        return delta;
    }

    @Override
    public double getDelta(IAccount account, ICurrency currency) {
        PreCon.notNull(account);
        PreCon.notNull(currency);

        double delta = getDelta(account);

        return delta * currency.getConversionFactor();
    }

    @Override
    public synchronized double getBalance(IAccount account) {
        PreCon.notNull(account);

        Double delta = _balanceDelta.get(account);
        if (delta == null)
            return account.getBalance();

        return account.getBalance() + delta;
    }

    @Override
    public double getBalance(IAccount account, ICurrency currency) {
        return getBalance(account) * currency.getConversionFactor();
    }

    @Override
    public synchronized boolean deposit(IAccount account, double amount) {
        PreCon.notNull(account);
        PreCon.positiveNumber(amount);

        checkCommitted();

        Double delta = _balanceDelta.get(account);
        if (delta == null) {
            delta = 0.0D;
        }

        delta += amount;

        _balanceDelta.put(account, delta);

        return true;
    }

    @Override
    public boolean deposit(IAccount account, double amount, ICurrency currency) {
        return deposit(account, amount * currency.getConversionFactor());
    }

    @Override
    public synchronized boolean withdraw(IAccount account, double amount) {
        PreCon.notNull(account);
        PreCon.positiveNumber(amount);

        checkCommitted();

        Double delta = _balanceDelta.get(account);
        if (delta == null) {
            delta = 0.0D;
        }

        delta -= amount;

        if (account.getBalance() + delta < 0.0D) {
            if (_error != null)
                _error = NucLang.get(_INSUFFICIENT_FUNDS);
            return false;
        }

        _balanceDelta.put(account, delta);

        return true;
    }

    @Override
    public boolean withdraw(IAccount account, double amount, ICurrency currency) {
        return withdraw(account, amount * currency.getConversionFactor());
    }

    @Override
    public synchronized IFutureResult<IEconomyTransaction> commit() {
        return commit(false);
    }

    @Override
    public synchronized IFutureResult<IEconomyTransaction> commit(boolean force) {

        checkCommitted();

        _isCommitted = true;

        if (_error != null)
            return FutureResultAgent.errorResult((IEconomyTransaction)this, _error);

        Set<Entry<IAccount, Double>> accounts = _balanceDelta.entrySet();

        Iterator<Entry<IAccount, Double>> iterator = accounts.iterator();

        Map<IAccount, Double> deposit = new HashMap<>(3);
        Map<IAccount, Double> withdraw = new HashMap<>(3);

        while (iterator.hasNext()) {
            Entry<IAccount, Double> entry = iterator.next();

            if (Double.compare(entry.getValue(), 0.0D) == 0) {
                iterator.remove();
                continue;
            }

            IAccount account = entry.getKey();
            Double delta = entry.getValue();

            if (delta > 0) {
                deposit.put(account, delta);
            } else {
                withdraw.put(account, Math.abs(delta));

                if (!force && account.getBalance() + delta < 0) {
                    return FutureResultAgent
                            .errorResult((IEconomyTransaction)this,
                                    NucLang.get(_INSUFFICIENT_FUNDS));
                }
            }
        }

        return commit(deposit, withdraw);
    }

    private IFutureResult<IEconomyTransaction> commit(
            Map<IAccount, Double> deposits, final Map<IAccount, Double> withdrawals) {

        final Map<IAccount, Double> deposited = new HashMap<>(7);
        final Map<IAccount, Double> withdrawn = new HashMap<>(7);

        final FutureResultAgent<IEconomyTransaction> agent = new FutureResultAgent<>();

        if (!performOps(deposits, deposited, OperationType.DEPOSIT, agent)) {
            undoOps(deposited, OperationType.DEPOSIT);
            return agent.getFuture();
        }

        if (!performOps(withdrawals, withdrawn, OperationType.WITHDRAW, agent)) {
            undoOps(withdrawn, OperationType.WITHDRAW);
            undoOps(deposited, OperationType.DEPOSIT);
            return agent.getFuture();
        }

        return agent.success(this, NucLang.get(_TRANSACTION_SUCCESS));
    }

    private boolean performOps(Map<IAccount, Double> operations,
                         Map<IAccount, Double> performed, OperationType type,
                            FutureResultAgent<IEconomyTransaction> agent) {

        Set<Entry<IAccount, Double>> accounts = operations.entrySet();

        for (Entry<IAccount, Double> entry : accounts) {
            if (type == OperationType.DEPOSIT) {

                if (accountDeposit(entry.getKey(), entry.getValue()) == null) {
                    agent.error(this, NucLang.get(_DEPOSIT_FAILED, entry.getValue(), entry.getKey()));
                    return false;
                }
            }
            else {

                if (accountWithdraw(entry.getKey(), entry.getValue()) == null) {
                    agent.error(this, NucLang.get(_WITHDRAW_FAILED, entry.getValue(), entry.getKey()));
                    return false;
                }
            }
            performed.put(entry.getKey(), entry.getValue());
        }

        return true;
    }

    private void undoOps(Map<IAccount, Double> performed, OperationType type) {

        Set<Entry<IAccount, Double>> accounts = performed.entrySet();

        for (Entry<IAccount, Double> entry : accounts) {

            IAccount account = entry.getKey();
            Double amount = entry.getValue();

            if (type == OperationType.DEPOSIT) {
                if (accountWithdraw(entry.getKey(), entry.getValue()) == null) {

                    NucMsg.severe("Failed to undo transaction deposit. " +
                                    "Bank: {0}, Account: {1}, Deposit amount: {2}",
                            getBankName(account), account.getPlayerId(), amount);
                }
            }
            else {
                if (accountDeposit(entry.getKey(), entry.getValue()) == null) {

                    NucMsg.severe("Failed to undo transaction withdrawal. " +
                                    "Bank: {0}, Account: {1}, Withdrawal amount: {2}",
                            getBankName(account), account.getPlayerId(), amount);
                }
            }
        }
    }

    private Double accountWithdraw(IAccount account, double amount) {
        if (account instanceof NucleusAccount)
            return ((NucleusAccount) account).withdraw(amount);
        else if (account instanceof VaultAccount)
            return ((VaultAccount) account).withdraw(amount);
        else
            throw new IllegalArgumentException("account must be an instance of NucleusAccount or VaultAccount.");
    }

    private Double accountDeposit(IAccount account, double amount) {
        if (account instanceof NucleusAccount)
            return ((NucleusAccount) account).deposit(amount);
        else if (account instanceof VaultAccount)
            return ((VaultAccount) account).deposit(amount);
        else
            throw new IllegalArgumentException("account must be an instance of NucleusAccount or VaultAccount.");
    }

    private String getBankName(IAccount account) {
        return account.getBank() != null
                ? account.getBank().getName()
                : NucLang.get(_GLOBAL_BANK);
    }

    private void checkCommitted() {
        if (_isCommitted)
            throw new IllegalStateException("Cannot use a transaction after it's been committed.");
    }

    private enum OperationType {
        DEPOSIT,
        WITHDRAW
    }
}
