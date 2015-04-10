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
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.future.FutureResultAgent;
import com.jcwhatever.nucleus.utils.observer.future.IFutureResult;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Static convenience functions for accessing the economy provider.
 */
public final class Economy {

    private Economy() {}

    /**
     * Create a new transaction to perform multiple operations on before executing them.
     *
     * <p>If the transaction fails, the economy operations performed in it are undone.</p>
     */
    public static IEconomyTransaction createTransaction() {
        return getProvider().createTransaction();
    }

    /**
     * Get the economy providers currency.
     */
    public static ICurrency getCurrency() {
        return getProvider().getCurrency();
    }

    /**
     * Get a players global account.
     *
     * @param playerId  The ID of the player.
     */
    public static IAccount getAccount(UUID playerId) {
        PreCon.notNull(playerId);

        return getProvider().getAccount(playerId);
    }

    /**
     * Get a players global account balance.
     *
     * <p>The currency of the balance is the economy providers default currency.</p>
     *
     * @param playerId  The ID of the player.
     */
    public static double getBalance(UUID playerId) {
        PreCon.notNull(playerId);

        IAccount account = getProvider().getAccount(playerId);
        if (account == null)
            return 0;

        return account.getBalance();
    }

    /**
     * Get a players global account balance.
     *
     * @param playerId  The ID of the player.
     * @param currency  The currency to return the amount in.
     */
    public static double getBalance(UUID playerId, ICurrency currency) {
        PreCon.notNull(playerId);

        IAccount account = getProvider().getAccount(playerId);
        if (account == null)
            return 0;

        return account.getBalance(currency);
    }

    /**
     * Transfer money between two players global accounts.
     *
     * @param giverPlayerId     The ID of the player who is giving money
     * @param receiverPlayerId  The ID of the player who is receiving money
     * @param amount            The amount of money to transfer.
     *
     * @return  A {@link IFutureResult} that returns the result of the transaction.
     */
    public static IFutureResult<IEconomyTransaction> transfer(
            UUID giverPlayerId, UUID receiverPlayerId, double amount) {

        return transfer(giverPlayerId, receiverPlayerId, amount, getCurrency());
    }

    /**
     * Transfer money between two players global accounts.
     *
     * @param giverPlayerId     The ID of the player who is giving money
     * @param receiverPlayerId  The ID of the player who is receiving money
     * @param amount            The amount of money to transfer.
     *
     * @return  A {@link IFutureResult} that returns the result of the transaction.
     */
    public static IFutureResult<IEconomyTransaction> transfer(
            UUID giverPlayerId, UUID receiverPlayerId, double amount, ICurrency currency) {

        PreCon.notNull(giverPlayerId);
        PreCon.notNull(receiverPlayerId);
        PreCon.positiveNumber(amount);
        PreCon.notNull(currency);

        IAccount fromAccount = getProvider().getAccount(giverPlayerId);
        if (fromAccount == null) {
            return new FutureResultAgent<IEconomyTransaction>()
                    .error(null, "Failed to find giving players account.");
        }

        IAccount toAccount = getProvider().getAccount(receiverPlayerId);
        if (toAccount == null) {
            return new FutureResultAgent<IEconomyTransaction>()
                    .error(null, "Failed to find receiving players account.");
        }

        return transfer(fromAccount, toAccount, amount, currency);
    }

    /**
     * Transfer money between two accounts.
     *
     * <p>The currency of the amount transferred is the economy providers default currency.</p>
     *
     * @param fromAccount  The account to transfer money out of.
     * @param toAccount    The account to transfer money into.
     * @param amount       The amount to transfer.
     *
     * @return  A {@link IFutureResult} that returns the result of the transaction.
     */
    public static IFutureResult<IEconomyTransaction> transfer(
            IAccount fromAccount, IAccount toAccount, double amount) {

        return transfer(fromAccount, toAccount, amount, getCurrency());
    }

    /**
     * Transfer money between two accounts.
     *
     * @param fromAccount  The account to transfer money out of.
     * @param toAccount    The account to transfer money into.
     * @param amount       The amount to transfer.
     * @param currency     The currency of the amount.
     *
     * @return  A {@link IFutureResult} that returns the result of the transaction.
     */
    public static IFutureResult<IEconomyTransaction> transfer(
            IAccount fromAccount, IAccount toAccount, double amount, ICurrency currency) {

        PreCon.notNull(fromAccount);
        PreCon.notNull(toAccount);
        PreCon.positiveNumber(amount);
        PreCon.notNull(currency);

        IEconomyTransaction transaction = createTransaction();

        transaction.withdraw(fromAccount, amount, currency);
        transaction.deposit(toAccount, amount, currency);
        return transaction.commit();
    }

    /**
     * Deposit money into a players global account.
     *
     * <p>The currency of the amount is the economy providers default currency.</p>
     *
     * @param playerId  The ID of the player to give money to.
     * @param amount    The amount to give the player.
     *
     * @return  A {@link IFutureResult} to retrieve the result of the transaction.
     */
    public static IFutureResult<IEconomyTransaction> deposit(UUID playerId, double amount) {
        return deposit(playerId, amount, getCurrency());
    }

    /**
     * Deposit money into a players global account.
     *
     * @param playerId  The ID of the player to give money to.
     * @param amount    The amount to give the player.
     * @param currency  The currency of the amount.
     *
     * @return  A {@link IFutureResult} to retrieve the result of the transaction.
     */
    public static IFutureResult<IEconomyTransaction> deposit(UUID playerId, double amount, ICurrency currency) {
        PreCon.notNull(playerId);
        PreCon.positiveNumber(amount);
        PreCon.notNull(currency);

        IAccount account = getProvider().getAccount(playerId);
        if (account == null) {
            return new FutureResultAgent<IEconomyTransaction>()
                    .error(null, "Failed to retrieve account " + playerId);
        }

        IEconomyTransaction transaction = getProvider().createTransaction();
        transaction.deposit(account, amount, currency);

        return transaction.commit();
    }

    /**
     * Withdraw money from a players global account.
     *
     * <p>The currency of the amount is the economy providers default currency.</p>
     *
     * @param playerId  The ID of the player to take money from.
     * @param amount    The amount to take.
     *
     * @return  A {@link IFutureResult} to retrieve the result of the transaction.
     */
    public static IFutureResult<IEconomyTransaction> withdraw(UUID playerId, double amount) {
        return withdraw(playerId, amount, getCurrency());
    }

    /**
     * Withdraw money from a players global account.
     *
     * @param playerId  The ID of the player to take money from.
     * @param amount    The amount to take.
     * @param currency  The currency of the amount.
     *
     * @return  A {@link IFutureResult} to retrieve the result of the transaction.
     */
    public static IFutureResult<IEconomyTransaction> withdraw(UUID playerId, double amount, ICurrency currency) {
        PreCon.notNull(playerId);
        PreCon.positiveNumber(amount);
        PreCon.notNull(currency);

        IAccount account = getProvider().getAccount(playerId);
        if (account == null) {
            return new FutureResultAgent<IEconomyTransaction>()
                    .error(null, "Failed to retrieve account for " + playerId);
        }

        IEconomyTransaction transaction = getProvider().createTransaction();

        transaction.withdraw(account, amount, currency);

        return transaction.commit();
    }

    /**
     * Deposit or withdraw money from a players global account depending on the value provided.
     *
     * <p>The currency of the amount is the economy providers default currency.</p>
     *
     * <p>Positive values are deposited, negative values are withdrawn.</p>
     *
     * @param playerId  The ID of the player.
     * @param amount    The amount to deposit or withdraw.
     *
     * @return  A {@link IFutureResult} to retrieve the result of the transaction.
     */
    public static IFutureResult<IEconomyTransaction> depositOrWithdraw(UUID playerId, double amount) {
        PreCon.notNull(playerId);

        IAccount account = getProvider().getAccount(playerId);
        if (account == null) {
            return new FutureResultAgent<IEconomyTransaction>()
                    .error(null, "Failed to find account for " + playerId);
        }

        return depositOrWithdraw(account, amount, getCurrency());
    }

    /**
     * Deposit or withdraw money from a players global account depending on the value provided.
     *
     * <p>Positive values are deposited, negative values are withdrawn.</p>
     *
     * @param playerId  The ID of the player.
     * @param amount    The amount to deposit or withdraw.
     *
     * @return  A {@link IFutureResult} to retrieve the result of the transaction.
     */
    public static IFutureResult<IEconomyTransaction> depositOrWithdraw(
            UUID playerId, double amount, ICurrency currency) {

        PreCon.notNull(playerId);
        PreCon.notNull(currency);

        IAccount account = getProvider().getAccount(playerId);
        if (account == null) {
            return new FutureResultAgent<IEconomyTransaction>()
                    .error(null, "Failed to find account for " + playerId);
        }

        return depositOrWithdraw(account, amount, currency);
    }

    /**
     * Deposit or withdraw money from an account depending on the value provided.
     *
     * <p>The currency of the amount is the economy providers default currency.</p>
     *
     * <p>Positive values are deposited, negative values are withdrawn.</p>
     *
     * @param account  The account.
     * @param amount   The amount to deposit or withdraw.
     *
     * @return  A {@link IFutureResult} to retrieve the result of the transaction.
     */
    public static IFutureResult<IEconomyTransaction> depositOrWithdraw(IAccount account, double amount) {
        return depositOrWithdraw(account, amount, getCurrency());
    }

    /**
     * Deposit or withdraw money from an account depending on the value provided.
     *
     * <p>Positive values are deposited, negative values are withdrawn.</p>
     *
     * @param account   The account.
     * @param amount    The amount to deposit or withdraw.
     * @param currency  The currency of the amount.
     *
     * @return  A {@link IFutureResult} to retrieve the result of the transaction.
     */
    public static IFutureResult<IEconomyTransaction> depositOrWithdraw(
            IAccount account, double amount, ICurrency currency) {

        PreCon.notNull(account);
        PreCon.notNull(currency);

        IEconomyTransaction transaction = getProvider().createTransaction();

        if (amount >= 0) {
            transaction.deposit(account, amount, currency);
        } else if (amount < 0) {
            transaction.withdraw(account, -amount, currency);
        }

        return transaction.commit();
    }

    /**
     * Determine if the economy provider has bank support.
     */
    public static boolean hasBankSupport() {
        return getProvider() instanceof IBankEconomyProvider;
    }

    /**
     * Get a bank by name.
     *
     * @param bankName  The name of the bank.
     *
     * @return  Null if the bank was not found.
     *
     * @throws java.lang.UnsupportedOperationException if {@link #hasBankSupport} returns false.
     */
    @Nullable
    public static IBank getBank(String bankName) {
        return getBankProvider().getBank(bankName);
    }

    /**
     * Get a list of banks.
     *
     * @throws java.lang.UnsupportedOperationException if {@link #hasBankSupport} returns false.
     */
    public static List<IBank> getBanks() {
        return getBankProvider().getBanks();
    }

    /**
     * Create a new bank account.
     *
     * @param bankName  The name of the bank.
     *
     * @return  Null if the bank was not created.
     *
     * @throws java.lang.UnsupportedOperationException if {@link #hasBankSupport} returns false.
     */
    @Nullable
    public static IBank createBank(String bankName) {
        return getBankProvider().createBank(bankName);
    }

    /**
     * Create a new bank account with the specified player as the owner.
     *
     * @param bankName  The name of the bank.
     * @param ownerId   The ID of the bank owner.
     *
     * @return  Null if the bank was not created.
     *
     * @throws java.lang.UnsupportedOperationException if {@link #hasBankSupport} returns false.
     */
    @Nullable
    public static IBank createBank(String bankName, UUID ownerId) {
        return getBankProvider().createBank(bankName, ownerId);
    }

    /**
     * Delete a bank.
     *
     * @param bankName  The name of the bank.
     *
     * @return  True if the bank was found and deleted.
     *
     * @throws java.lang.UnsupportedOperationException if {@link #hasBankSupport} returns false.
     */
    public static boolean deleteBank(String bankName) {
        return getBankProvider().deleteBank(bankName);
    }

    /**
     * Get the economy provider.
     */
    public static IEconomyProvider getProvider() {
        return Nucleus.getProviders().getEconomy();
    }

    /**
     * Get the bank interface for the economy provider.
     *
     * <p>Check {@link #hasBankSupport} method first before calling.</p>
     *
     * @throws java.lang.UnsupportedOperationException
     */
    public static IBankEconomyProvider getBankProvider() {
        IBankEconomyProvider provider = (IBankEconomyProvider)getProvider();
        if (provider == null)
            throw new UnsupportedOperationException();

        return provider;
    }
}
