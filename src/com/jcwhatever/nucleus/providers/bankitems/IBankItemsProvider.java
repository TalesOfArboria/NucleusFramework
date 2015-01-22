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

package com.jcwhatever.nucleus.providers.bankitems;

import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Interface for a {@link IBankItem} provider.
 */
public interface IBankItemsProvider {

    /**
     * Get a players global bank item account. If the account
     * does not exist, it is created.
     *
     * @param playerId  The ID of the player.
     */
    IBankItemsAccount getAccount(UUID playerId);

    /**
     * Determine if a bank exists.
     *
     * @param bankName  The name of the bank to check.
     */
    boolean hasBank(String bankName);

    /**
     * Get a bank item bank.
     *
     * @param bankName The name of the bank.
     *
     * @return  The bank or null if it doesn't exist.
     */
    @Nullable
    IBankItemsBank getBank(String bankName);

    /**
     * Get or create a bank. If the bank already exists, the
     * existing bank is returned. Otherwise a new bank is created.
     *
     * <p>A newly created bank will not have a player owner.</p>
     *
     * @param bankName  The name of the bank.
     */
    IBankItemsBank createBank(String bankName);

    /**
     * Get or create a bank. If the bank already exists, the
     * existing bank is returned. Otherwise a new bank is created.
     *
     * @param bankName  The name of the bank.
     * @param ownerId   The id of the banks player owner. Only used if the bank is created.
     */
    IBankItemsBank createBank(String bankName, @Nullable UUID ownerId);

    /**
     * Delete a bank.
     *
     * @param bankName  The name of the bank to delete.
     *
     * @return  True if the bank was found and removed.
     */
    boolean deleteBank(String bankName);
}
