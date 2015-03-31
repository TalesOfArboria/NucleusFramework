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
     * Get the players balance. The currency of the amount returned is the economy
     * providers default currency.
     *
     * @return The balance. Providers that use databases may return a locally cached value.
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
     *
     * @return  The balance. Providers that use databases may return a locally cached value.
     */
    double getBalance(ICurrency currency);

    /**
     * Get the account balance. The currency of the amount returned is the economy
     * providers default currency.
     *
     * <p>For providers that use a database, this returns the balance as read from the database.
     * For providers that use disk based storage, this is effectively the same as invoking
     * {@link #getBalance}.</p>
     *
     * @return  A {@link Future} containing the balance result.
     */
    Future<Double> getLatestBalance();

    /**
     * Get the players balance in the specified currency.
     *
     * <p>For providers that use a database, this returns the balance as read from the database.
     * For providers that use disk based storage, this is effectively the same as invoking
     * {@link #getBalance}.</p>
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
     *
     * @return  A {@link Future} containing the balance result.
     */
    Future<Double> getLatestBalance(ICurrency currency);

    /**
     * Get the underlying account object if the object is wrapped. Otherwise, the handle is
     * the {@link IAccount} instance.
     */
    Object getHandle();
}
