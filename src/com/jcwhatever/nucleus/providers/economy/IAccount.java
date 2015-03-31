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
 *
 * <p>Depending on implementation, an account may support multiple balances of each currency or
 * a single balance convertible to different currencies.</p>
 */
public interface IAccount {

    /**
     * Get the account owners unique Minecraft ID.
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
     * returned is the economy providers default currency.
     */
    double getBalance();

    /**
     * Get the players balance in the specified currency.
     *
     * <p>The value returned depends on how the provider chooses to implement currency.
     * If the provider only stores the balance for a single currency, the returned value is
     * the balance of the default currency converted to the specified currency. The provider may
     * also store multiple currency balances in which case the returned value is the balance
     * for the specified currency.</p>
     *
     * <p>If the provider does not recognize the specified currency, it may choose to create
     * a new balance for the currency or simply convert the default balance to the currency.</p>
     *
     * @param currency  The currency to convert the result to.
     */
    double getBalance(ICurrency currency);

    /**
     * Deposit an amount of the providers default currency into the account.
     *
     * @param amount  The amount to deposit. The currency of the amount is assumed to be
     *                the economy providers default currency.
     *
     * @return  The amount deposited or null if failed.
     */
    Double deposit(double amount);

    /**
     * Deposit an amount of the specified currency into the account.
     *
     * <p>The amount deposited depends on how the provider chooses to implement currency.
     * If the provider only stores the balance for a single currency, the deposited amount is
     * converted to the default currency. The provider may also store multiple currency balances
     * in which case the amount is deposited into the balance for the specified currency.</p>
     *
     * <p>If the provider does not recognize the specified currency, it may choose to create a
     * new balance for it or simply convert the amount to the default currency.</p>.
     *
     * <p>If the provider does not recognize the specified currency, it may choose to create
     * a new balance for the currency or simply convert the default balance to the currency.</p>
     *
     * @param amount    The amount to deposit.
     * @param currency  The currency of the amount to deposit.
     *
     * @return  The amount deposited or null if failed.
     */
    Double deposit(double amount, ICurrency currency);

    /**
     * Withdraw an amount of the default currency from the account.
     *
     * <p>Allowing negative balances is implementation specific. Do not assume the operation
     * will fail if the account has insufficient funds. Check the balance first if allowing a
     * negative balance is not the intention.</p>
     *
     * @param amount  The amount to withdraw from the account. The
     *                currency of the amount is assumed to be the
     *                economy providers currency.
     *
     * @return  The amount withdrawn or null if failed.
     */
    Double withdraw(double amount);

    /**
     * Withdraw an amount of the default currency from the account.
     *
     * <p>Allowing negative balances is implementation specific. Do not assume the operation
     * will fail if the account has insufficient funds. Check the balance first if allowing a
     * negative balance is not the intention.</p>
     *
     * <p>The amount withdrawn depends on how the provider chooses to implement currency.
     * If the provider only stores the balance for a single currency, the withdrawn amount is
     * converted to the default currency. The provider may also store multiple currency balances
     * in which case the amount is withdrawn from the balance for the specified currency.</p>
     *
     * <p>If the provider does not recognize the specified currency, it may choose to create a
     * new balance for it, fail, or simply convert the amount to the default currency.</p>.
     *
     * @param amount   The amount to withdraw from the account.
     * @param currency The currency of the amount to withdraw.
     *
     * @return  The amount withdrawn or null if failed.
     */
    Double withdraw(double amount, ICurrency currency);

    /**
     * Get the underlying account object if the object is wrapped. Otherwise, the handle is
     * the {@link IAccount} instance.
     */
    Object getHandle();
}
