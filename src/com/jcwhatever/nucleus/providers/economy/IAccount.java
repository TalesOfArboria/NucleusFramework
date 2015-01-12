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

import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Interface for an economy account.
 */
public interface IAccount {

    /**
     * Get the account owners unique id.
     */
    UUID getPlayerId();

    /**
     * Get the accounts owning bank.
     *
     * @return  Null if the account is a global account.
     */
    @Nullable
    IBank getBank();

    /**
     * Get the players balance. The currency of the amount
     * returned is the economy providers currency.
     */
    double getBalance();

    /**
     * Get the players balance converted to
     * the specified currency.
     *
     * @param currency  The currency to convert the result to.
     */
    double getBalance(ICurrency currency);

    /**
     * Deposit into the account.
     *
     * @param amount  The amount to deposit. The currency of the
     *                amount is assumed to be the economy providers
     *                currency.
     *
     * @return  True if the operation is successful.
     */
    boolean deposit(double amount);

    /**
     * Deposit into the account.
     *
     * @param amount    The amount to deposit.
     * @param currency  The currency of the amount to deposit.
     *
     * @return  True if the operation is successful.
     */
    boolean deposit(double amount, ICurrency currency);

    /**
     * Withdraw from the account.
     *
     * <p>Do not assume the operation will fail if the
     * account has insufficient funds. Check the balance first if
     * allowing a negative balance is not the intention.</p>
     *
     * @param amount  The amount to withdraw from the account. The
     *                currency of the amount is assumed to be the
     *                economy providers currency.
     *
     * @return  True if the operation is successful.
     */
    boolean withdraw(double amount);


    /**
     * Withdraw from the account.
     *
     * <p>Do not assume the operation will fail if the
     * account has insufficient funds. Check the balance first if
     * allowing a negative balance is not the intention.</p>
     *
     * @param amount   The amount to withdraw from the account.
     * @param currency The currency of the amount to withdraw.
     *
     * @return  True if the operation is successful.
     */
    boolean withdraw(double amount, ICurrency currency);

    /**
     * Get the underlying account object if the
     * object is wrapped. Otherwise, the handle is
     * the {@code IAccount} instance.
     */
    Object getHandle();
}
