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

import com.jcwhatever.nucleus.mixins.INamed;
import com.jcwhatever.nucleus.utils.observer.result.FutureResultAgent.Future;

import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Interface for an economy bank.
 *
 * <p>Depending on implementation, a bank may support multiple balances of each currency or
 * a single balance convertible to different currencies.</p>
 */
public interface IBank extends INamed {

    /**
     * Get the ID of the bank owner.
     *
     * @return  Null if the bank has no owner.
     */
    @Nullable
    UUID getOwnerId();

    /**
     * Get the bank balance.
     *
     * <p>The bank balance is the sum of the balances of all accounts held by the bank.</p>
     *
     * <p>The currency of the amount returned is the economy providers default currency.</p>
     *
     * <p>If the provider implements multiple balances for multiple currencies, this only returns
     * the sum of the balances of the default currency.</p>
     *
     * @return The balance. Providers that use a database may return a locally cached value.</p>
     */
    double getBalance();

    /**
     * Get the bank balance.
     *
     * <p>The bank balance is the sum of the balances of all accounts held by the bank.</p>
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
     * @param currency  The currency of the amount to return.
     *
     * @return The balance. Providers that use a database may return a locally cached value.</p>
     */
    double getBalance(ICurrency currency);

    /**
     * Determine if the specified player has an account with the bank.
     *
     * @param playerId  The Minecraft ID of the player.
     *
     * @return True if the player has an account, otherwise false.
     */
    boolean hasAccount(UUID playerId);

    /**
     * Get a player account from the bank.
     *
     * @param playerId  The Minecraft ID of the account owner.
     *
     * @return  The account or null if not found.
     */
    @Nullable
    IAccount getAccount(UUID playerId);

    /**
     * Create a bank account.
     *
     * @param playerId  The Minecraft ID of the account owner.
     *
     * @return  The account or null if failed.
     */
    @Nullable
    IAccount createAccount(UUID playerId);

    /**
     * Delete a bank account.
     *
     * @param playerId  The Minecraft ID of the account owner.
     *
     * @return  A future indicating the result of the account deletion.
     */
    Future<Void> deleteAccount(UUID playerId);

    /**
     * Get the underlying bank object if the object is wrapped. Otherwise,
     * the handle is the {@link IBank} instance.
     */
    Object getHandle();
}
