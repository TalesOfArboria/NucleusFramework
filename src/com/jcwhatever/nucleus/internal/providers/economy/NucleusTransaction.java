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

import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.providers.economy.IAccount;
import com.jcwhatever.nucleus.providers.economy.IEconomyTransaction;
import com.jcwhatever.nucleus.providers.economy.TransactionFailException;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * NucleusFrameworks {@code IEconomyTransaction} transaction implementation.
 */
public class NucleusTransaction implements IEconomyTransaction {

    private Map<IAccount, Double> _balanceDelta = new HashMap<>(5);
    private boolean _isExecuted;

    @Override
    public synchronized boolean isExecuted() {
        return _isExecuted;
    }

    @Override
    public synchronized double getBalance(IAccount account) {
        Double delta = _balanceDelta.get(account);
        if (delta == null)
            return account.getBalance();

        return account.getBalance() + delta;
    }

    @Override
    public synchronized boolean deposit(IAccount account, double amount) {
        PreCon.notNull(account);
        PreCon.positiveNumber(amount);

        Double delta = _balanceDelta.get(account);
        if (delta == null) {
            delta = 0.0D;
        }

        delta += amount;

        _balanceDelta.put(account, delta);

        return true;
    }

    @Override
    public synchronized boolean withdraw(IAccount account, double amount) {
        PreCon.notNull(account);
        PreCon.positiveNumber(amount);

        Double delta = _balanceDelta.get(account);
        if (delta == null) {
            delta = 0.0D;
        }

        delta -= amount;

        if (account.getBalance() + delta < 0.0D)
            return false;

        _balanceDelta.put(account, delta);

        return true;
    }

    @Override
    public synchronized void execute() throws TransactionFailException {
        execute(false);
    }

    @Override
    public synchronized void execute(boolean force) throws TransactionFailException {

        if (_isExecuted)
            throw new RuntimeException("Cannot execute an instance of a transaction more than once.");

        _isExecuted = true;

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
                    throw new TransactionFailException(account, "Account does not have sufficient funds.");
                }
            }
        }

        execute(deposit, withdraw);
    }

    private void execute(Map<IAccount, Double> deposits, Map<IAccount, Double> withdrawals)
            throws TransactionFailException {

        Map<IAccount, Double> deposited = new HashMap<>(3);
        Map<IAccount, Double> withdrawn = new HashMap<>(3);

        try {
            performOps(deposits, deposited, OperationType.DEPOSIT);
        }
        catch (TransactionFailException e) {
            undoOps(deposited, OperationType.DEPOSIT);
            throw e;
        }

        try {
            performOps(withdrawals, withdrawn, OperationType.WITHDRAW);
        }
        catch (TransactionFailException e) {
            undoOps(withdrawn, OperationType.WITHDRAW);
            undoOps(deposited, OperationType.DEPOSIT);
            throw e;
        }
    }

    private void performOps(Map<IAccount, Double> operations,
                         Map<IAccount, Double> performed, OperationType type)
            throws TransactionFailException {

        Set<Entry<IAccount, Double>> accounts = operations.entrySet();

        for (Entry<IAccount, Double> entry : accounts) {
            if (type == OperationType.DEPOSIT) {
                if (!entry.getKey().deposit(entry.getValue())) {
                    throw new TransactionFailException(entry.getKey(),
                            "Failed to deposit amount: " + entry.getValue());
                }
            }
            else {
                if (!entry.getKey().withdraw(entry.getValue())) {
                    throw new TransactionFailException(entry.getKey(),
                            "Failed to withdraw amount: " + entry.getValue());
                }
            }
            performed.put(entry.getKey(), entry.getValue());
        }
    }

    private void undoOps(Map<IAccount, Double> performed, OperationType type) {

        Set<Entry<IAccount, Double>> accounts = performed.entrySet();

        for (Entry<IAccount, Double> entry : accounts) {

            IAccount account = entry.getKey();
            Double amount = entry.getValue();

            if (type == OperationType.DEPOSIT) {
                if (!entry.getKey().withdraw(entry.getValue())) {

                    NucMsg.severe("Failed to undo transaction deposit. " +
                                    "Bank: {0}, Account: {1}, Deposit amount: {2}",
                            getBankName(account), account.getPlayerId(), amount);
                }
            }
            else {
                if (!entry.getKey().deposit(entry.getValue())) {

                    NucMsg.severe("Failed to undo transaction withdrawal. " +
                                    "Bank: {0}, Account: {1}, Withdrawal amount: {2}",
                            getBankName(account), account.getPlayerId(), amount);
                }
            }
        }
    }

    private String getBankName(IAccount account) {
        return account.getBank() != null
                ? account.getBank().getName()
                : "<global>";
    }

    private enum OperationType {
        DEPOSIT,
        WITHDRAW
    }
}
