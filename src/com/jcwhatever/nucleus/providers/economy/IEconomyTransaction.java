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
     * @param account  The account to get the transaction balance for.
     *
     * @return  The balance current for the transaction. Does not
     * represent the actual balance until {@code #execute} is invoked.
     */
    double getBalance(IAccount account);

    /**
     * Deposit into an account.
     *
     * <p>The amount is not actually deposited until the
     * {@code #execute} method is invoked.</p>
     *
     * @param amount  The amount to deposit.
     *
     * @return  True if the operation is successful.
     */
    boolean deposit(IAccount account, double amount);

    /**
     * Withdraw from the account.
     *
     * <p>The amount is not actually withdrawn until the
     * {@code #execute} method is invoked.</p>
     *
     * @param amount  The amount to withdraw from the account.
     *
     * @return  True if the operation is successful.
     */
    boolean withdraw(IAccount account, double amount);

    /**
     * Execute the transaction. Any methods called should not
     * perform any operations until {@code #execute} is called.
     *
     * <p>If the transaction fails, any operations that have been
     * performed should be undone.</p>
     */
    void execute() throws TransactionFailException;

    /**
     * Execute the transaction. Any methods called should not
     * perform any operations until {@code #execute} is called.
     *
     * <p>If the transaction fails, any operations that have been
     * performed should be undone.</p>
     *
     * @param force  If true, the transaction will attempt to take money
     *               from accounts with insufficient funds leaving them with
     *               a negative balance. This may still fail if the economy
     *               provider does not support or allow negative balances.
     */
    void execute(boolean force) throws TransactionFailException;

}
