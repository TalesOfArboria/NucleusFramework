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
     * Get the players balance.
     */
    double getBalance();

    /**
     * Deposit into the account.
     *
     * @param amount  The amount to deposit.
     *
     * @return  True if the operation is successful.
     */
    boolean deposit(double amount);

    /**
     * Withdraw from the account.
     *
     * @param amount  The amount to withdraw from the account.
     *
     * @return  True if the operation is successful.
     */
    boolean withdraw(double amount);
}
