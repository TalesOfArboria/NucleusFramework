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

import com.jcwhatever.nucleus.mixins.INamedInsensitive;

import java.util.Date;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Interface for a {@link IBankItemsAccount} bank.
 */
public interface IBankItemsBank extends INamedInsensitive {

    /**
     * Get the ID of the player owner of the bank.
     *
     * @return  The ID or null if the bank has no player owner.
     */
    @Nullable
    UUID getOwnerId();

    /**
     * Get the date/time the bank was created.
     */
    Date getCreatedDate();

    /**
     * Get the last access date/time of the bank. If the bank has
     * never been accessed, the created date is returned.
     */
    Date getLastAccess();

    /**
     * Determine if a player has an account with the bank.
     *
     * @param playerId  The ID of the player.
     */
    boolean hasAccount(UUID playerId);

    /**
     * Get a players bank account.
     *
     * @param playerId  The ID of the player account owner.
     *
     * @return  The account or null if the player does not have an account.
     */
    @Nullable
    IBankItemsAccount getAccount(UUID playerId);

    /**
     * Get or create a player bank account. If the account already exists,
     * the existing account is returned. Otherwise a new one is created.
     *
     * @param playerId  The ID of the player account owner.
     */
    IBankItemsAccount createAccount(UUID playerId);

    /**
     * Delete a players account from the bank.
     *
     * @param playerId  The ID of the player.
     * @return
     */
    boolean deleteAccount(UUID playerId);
}