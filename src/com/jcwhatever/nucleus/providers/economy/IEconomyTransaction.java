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

import com.jcwhatever.nucleus.utils.observer.result.FutureResultAgent.Future;

/**
 * Represents a type used to perform transactions.
 *
 * <p>The transaction makes no visible changes to any account or bank until the {@link #commit}
 * method is invoked. If the transaction fails during the execution phase, any changes were made
 * are undone.</p>
 */
public interface IEconomyTransaction {

    /**
     * Determine if the transaction has already been committed.
     *
     * <p>A transaction can only be committed once.</p>
     */
    boolean isCommitted();

    /**
     * Determine the difference in balance of the providers default currency in the
     * specified account.
     *
     * @param account  The account whose balance change is to be retrieved.
     *
     * @return  The balance delta for the transaction. Returns 0 if the account has not been modified.
     * If the transaction has not been committed yet, the value represents the current state of the
     * transaction, otherwise this is the delta change to the balance after commit.
     */
    double getDelta(IAccount account);

    /**
     * Determine the difference in balance of the specified currency in the specified
     * account.
     *
     * <p>The value returned depends on how the provider chooses to implement currency.
     * If the provider only stores the balance for a single currency, the returned value is
     * the balance of the default currency converted to the specified currency. The provider may
     * also store multiple currency balances in which case the returned value is the balance
     * for the specified currency.</p>
     *
     * @param account   The account whose balance change is to be retrieved.
     * @param currency  The currency to return the amount in.
     *
     * @return  The balance delta for the transaction. Returns 0 if the account has not been modified.
     * If the transaction has not been committed yet, the value represents the current state of the
     * transaction, otherwise this is the delta change to the balance after commit.
     */
    double getDelta(IAccount account, ICurrency currency);

    /**
     * Get the players balance.
     *
     * <p>The currency of the amount is the economy providers default currency.</p>
     *
     * @param account  The account to get the transaction balance for.
     *
     * @return  The balance current for the transaction. Does not represent the actual balance if changes
     * have been made until {@link #commit} is successfully invoked.
     */
    double getBalance(IAccount account);

    /**
     * Get the players balance in the specified currency.
     *
     * <p>The value returned depends on how the provider chooses to implement currency.
     * If the provider only stores the balance for a single currency, the returned value is
     * the balance of the default currency converted to the specified currency. The provider may
     * also store multiple currency balances in which case the returned value is the balance
     * for the specified currency.</p>
     *
     * <p>If the provider does not recognize the specified currency, it may choose to return 0 for
     * no balance or simply convert the default balance to the currency. If the provider creates
     * new balances for unrecognized currencies, the new balance will be tracked during the transaction
     * and created once the transaction is committed.</p>.
     *
     * @param account   The account to get the transaction balance for.
     * @param currency  The currency to return the amount in.
     *
     * @return  The balance current for the transaction. Does not represent the actual balance if changes
     * have been made until {@link #commit} is successfully invoked.
     */
    double getBalance(IAccount account, ICurrency currency);

    /**
     * Deposit into an account.
     *
     * <p>The currency of the amount is the economy providers default currency.</p>
     *
     * <p>The amount is not actually deposited until the {@link #commit} method is invoked.</p>
     *
     * @param account  The account to deposit into.
     * @param amount   The amount to deposit.
     *
     * @return  True if the virtual operation is successful.
     *
     * @throws IllegalStateException if the transaction has already been committed.
     */
    boolean deposit(IAccount account, double amount);

    /**
     * Deposit into an account an amount of the specified currency.
     *
     * <p>The amount deposited depends on how the provider chooses to implement currency.
     * If the provider only stores the balance for a single currency, the deposited amount is
     * converted to the default currency. The provider may also store multiple currency balances
     * in which case the amount is deposited into the balance for the specified currency.</p>
     *
     * <p>If the provider does not recognize the specified currency, it may choose to create a
     * new balance for it or simply convert the amount to the default currency.</p>.
     *
     * <p>The amount is not actually deposited until the {@link #commit} method is invoked.</p>
     *
     * @param account   The account to deposit into.
     * @param amount    The amount to deposit.
     * @param currency  The currency of the amount.
     *
     * @return  True if the virtual operation is successful.
     *
     * @throws IllegalStateException if the transaction has already been committed.
     */
    boolean deposit(IAccount account, double amount, ICurrency currency);

    /**
     * Withdraw from the account.
     *
     * <p>The currency of the amount is the economy providers default currency.</p>
     *
     * <p>The amount is not actually withdrawn until the {@link #commit} method is invoked.</p>
     *
     * @param account  The account to withdraw from.
     * @param amount   The amount to withdraw from the account.
     *
     * @return  True if the virtual operation is successful.
     *
     * @throws IllegalStateException if the transaction has already been committed.
     */
    boolean withdraw(IAccount account, double amount);

    /**
     * Withdraw from the account.
     *
     * <p>The amount withdrawn depends on how the provider chooses to implement currency.
     * If the provider only stores the balance for a single currency, the withdrawn amount is
     * converted to the default currency. The provider may also store multiple currency balances
     * in which case the amount is withdrawn from the balance for the specified currency.</p>
     *
     * <p>If the provider does not recognize the specified currency, it may choose to create a
     * new balance for it, or simply convert the amount to the default currency.</p>.
     *
     * <p>The amount is not actually withdrawn until the {@link #commit} method is invoked.</p>
     *
     * @param account   The account to withdraw from.
     * @param amount    The amount to withdraw from the account.
     * @param currency  The currency of the amount.
     *
     * @return  True if the virtual operation is successful.
     *
     * @throws IllegalStateException if the transaction has already been committed.
     */
    boolean withdraw(IAccount account, double amount, ICurrency currency);

    /**
     * Commit the transaction.
     *
     * <p>If the transaction fails, any operations that have been performed should be undone.</p>
     *
     * @return  A {@link Future} that returns the result of the transaction.
     *
     * @throws IllegalStateException if the transaction has already been committed.
     */
    Future<IEconomyTransaction> commit();

    /**
     * Commit the transaction.
     *
     * <p>If the transaction fails, any operations that have been performed should be undone.</p>
     *
     * @param force  If true, the transaction will attempt to take money from accounts with insufficient
     *               funds leaving them with a negative balance. This may still fail if the economy
     *               provider does not support or allow negative balances.
     *
     * @return  A {@link Future} that returns the result of the transaction.
     *
     * @throws IllegalStateException if the transaction has already been committed.
     */
    Future<IEconomyTransaction> commit(boolean force);
}
