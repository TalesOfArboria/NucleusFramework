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

import com.jcwhatever.nucleus.mixins.IWrapper;
import com.jcwhatever.nucleus.providers.IProvider;

import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Interface for an economy provider.
 *
 * <p>Should be implemented by a type that extends {@link com.jcwhatever.nucleus.providers.Provider}.</p>
 */
public interface IEconomyProvider extends IProvider, IWrapper<Object> {

    /**
     * Get the default currency of the provider.
     *
     * <p>The default currency conversion factor is 1.0</p>
     */
    ICurrency getCurrency();

    /**
     * Get a global economy account.
     *
     * @param playerId  The ID of the account owner.
     *
     * @return The account or null if failed to retrieve.
     */
    @Nullable
    IAccount getAccount(UUID playerId);

    /**
     * Get an object used to run a transaction.
     *
     * <p>A transaction is used to prevent or minimize the chance of account balances being
     * incorrect should 1 or more operations in the transaction fail.</p>
     */
    IEconomyTransaction createTransaction();

    /**
     * Get the underlying economy provider if the provider is wrapped. Otherwise, the handle is
     * the {@link IEconomyProvider} instance.
     */
    @Override
    Object getHandle();
}
