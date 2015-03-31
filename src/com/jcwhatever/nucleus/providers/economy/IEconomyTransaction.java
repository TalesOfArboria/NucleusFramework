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

/**
 * Represents a type used to perform transactions.
 *
 * <p>The transaction makes no visible changes to any account or bank until the {@link #execute}
 * method is invoked. If the transaction fails during the execution phase, any changes were made
 * are undone.</p>
 */
public interface IEconomyTransaction {

    /**
     * Determine if the transaction has already been executed.
     *
     * <p>A transaction can only be executed once.</p>
     */
    boolean isExecuted();

    /**
     * Get the players balance.
     *
     * <p>The currency of the amount is the economy providers default currency.</p>
     *
     * @param account  The account to get the transaction balance for.
     *
     * @return  The balance current for the transaction. Does not represent the actual balance until
     * {@link #execute} is invoked.
     *
     * @throws IllegalStateException if the transaction has already been executed.
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
     * and created once the transaction is executed.</p>.
     *
     * @param account   The account to get the transaction balance for.
     * @param currency  The currency to return the amount in.
     *
     * @return  The balance current for the transaction. Does not
     * represent the actual balance until {@link #execute} is invoked.
     *
     * @throws IllegalStateException if the transaction has already been executed.
     */
    double getBalance(IAccount account, ICurrency currency);

    /**
     * Deposit into an account.
     *
     * <p>The currency of the amount is the economy providers default currency.</p>
     *
     * <p>The amount is not actually deposited until the {@link #execute} method is invoked.</p>
     *
     * @param amount  The amount to deposit.
     *
     * @return  True if the operation is successful.
     *
     * @throws IllegalStateException if the transaction has already been executed.
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
     * <p>The amount is not actually deposited until the {@link #execute} method is invoked.</p>
     *
     * @param amount    The amount to deposit.
     * @param currency  The currency of the amount.
     *
     * @return  True if the operation is successful.
     *
     * @throws IllegalStateException if the transaction has already been executed.
     */
    boolean deposit(IAccount account, double amount, ICurrency currency);

    /**
     * Withdraw from the account.
     *
     * <p>The currency of the amount is the economy providers default currency.</p>
     *
     * <p>The amount is not actually withdrawn until the {@link #execute} method is invoked.</p>
     *
     * @param amount  The amount to withdraw from the account.
     *
     * @return  True if the operation is successful.
     *
     * @throws IllegalStateException if the transaction has already been executed.
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
     * <p>The amount is not actually withdrawn until the {@link #execute} method is invoked.</p>
     *
     * @param amount    The amount to withdraw from the account.
     * @param currency  The currency of the amount.
     *
     * @return  True if the operation is successful.
     *
     * @throws IllegalStateException if the transaction has already been executed.
     */
    boolean withdraw(IAccount account, double amount, ICurrency currency);

    /**
     * Execute the transaction.
     *
     * <p>If the transaction fails, any operations that have been performed should be undone.</p>
     *
     * @throws IllegalStateException if the transaction has already been executed.
     * @throws TransactionFailException if the transaction fails.
     */
    void execute() throws TransactionFailException;

    /**
     * Execute the transaction.
     *
     * <p>If the transaction fails, any operations that have been performed should be undone.</p>
     *
     * @param force  If true, the transaction will attempt to take money from accounts with insufficient
     *               funds leaving them with a negative balance. This may still fail if the economy
     *               provider does not support or allow negative balances.
     *
     * @throws IllegalStateException if the transaction has already been executed.
     * @throws TransactionFailException if the transaction fails.
     */
    void execute(boolean force) throws TransactionFailException;

}
