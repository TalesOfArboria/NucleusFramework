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


package com.jcwhatever.nucleus.utils;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.providers.economy.IAccount;
import com.jcwhatever.nucleus.providers.economy.IBank;
import com.jcwhatever.nucleus.providers.economy.IEconomyProvider;
import com.jcwhatever.nucleus.providers.economy.IEconomyProvider.CurrencyNoun;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Provides static functions to interface with the installed economy
 * via Vault plugin.
 */
public final class EconomyUtils {

    private EconomyUtils() {}

    /**
     * Get a players global account.
     *
     * @param playerId  The id of the player.
     */
    public static IAccount getAccount(UUID playerId) {
        PreCon.notNull(playerId);

        return provider().getAccount(playerId);
    }

    /**
     * Get a players global account balance.
     *
     * @param playerId The id of the player.
     */
    public static double getBalance(UUID playerId) {
        PreCon.notNull(playerId);

        IAccount account = provider().getAccount(playerId);
        if (account == null)
            return 0;

        return account.getBalance();
    }

    /**
     * Get a players global account balance as a formatted string.
     *
     * @param playerId The id of the player.
     * @return
     */
    public static String getBalanceText(UUID playerId) {
        PreCon.notNull(playerId);

        double balance = getBalance(playerId);

        return provider().formatAmount(balance);
    }

    /**
     * Format an amount into a string using the economy provider.
     *
     * @param amount The amount to format.
     */
    public static String formatAmount(double amount) {

        return provider().formatAmount(amount);
    }

    /**
     * Get the currency name.
     *
     * @param noun The type of noun to return.
     */
    public static String getCurrencyName(CurrencyNoun noun) {
        PreCon.notNull(noun);

        return provider().getCurrencyName(noun);
    }

    /**
     * Transfer money between two players global accounts.
     *
     * @param giverPlayerId     The id of the player who is giving money
     * @param receiverPlayerId  The id of the player who is receiving money
     * @param amount            The amount of money to transfer.
     *
     * @return  True if the operation completed successfully.
     */
    public static boolean transfer(UUID giverPlayerId, UUID receiverPlayerId, double amount) {
        PreCon.notNull(giverPlayerId);
        PreCon.notNull(receiverPlayerId);
        PreCon.positiveNumber(amount);

        IAccount fromAccount = provider().getAccount(giverPlayerId);
        if (fromAccount == null)
            return false;

        IAccount toAccount = provider().getAccount(receiverPlayerId);
        return toAccount != null && transfer(fromAccount, toAccount, amount);
    }

    /**
     * Transfer money between two accounts.
     *
     * @param fromAccount  The account to transfer money out of.
     * @param toAccount    The account to transfer money into.
     * @param amount       The amount to transfer.
     *
     * @return  True if the operation is successful.
     */
    public static boolean transfer(IAccount fromAccount, IAccount toAccount, double amount) {
        PreCon.notNull(fromAccount);
        PreCon.notNull(toAccount);
        PreCon.positiveNumber(amount);

        if (Double.compare(amount, 0.0D) == 0)
            return true;


        if (fromAccount.getBalance() < amount)
            return false;

        if (!fromAccount.withdraw(amount))
            return false;

        if (!toAccount.deposit(amount)) {
            // give money back
            fromAccount.deposit(amount);
            return false;
        }

        return true;
    }

    /**
     * Deposit money into a players global account.
     *
     * @param playerId  The id of the player to give money to.
     * @param amount    The amount to give the player.
     *
     * @return  True if the operation is successful.
     */
    public static boolean deposit(UUID playerId, double amount) {
        PreCon.notNull(playerId);

        IAccount account = provider().getAccount(playerId);
        return account != null && account.deposit(amount);
    }

    /**
     * Withdraw money from a players global account.
     *
     * @param playerId  The id of the player to take money from.
     * @param amount    The amount to take.
     *
     * @return  True if the operation is successful.
     */
    public static boolean withdraw(UUID playerId, double amount) {
        PreCon.notNull(playerId);

        IAccount account = provider().getAccount(playerId);
        return account != null && account.withdraw(amount);
    }

    /**
     * Deposit or withdraw money from a players global account
     * depending on the value provided.
     *
     * <p>Positive values are deposited, negative values are withdrawn.</p>
     *
     * @param playerId  The id of the player.
     * @param amount    The amount to deposit or withdraw.
     *
     * @return  True if the operation is successful.
     */
    public static boolean depositOrWithdraw(UUID playerId, double amount) {
        PreCon.notNull(playerId);
        return Double.compare(amount, 0.0D) == 0 ||
                (amount < 0 ? withdraw(playerId, amount) : deposit(playerId, amount));
    }

    /**
     * Deposit or withdraw money from an account depending on the
     * value provided.
     *
     * <p>Positive values are deposited, negative values are withdrawn.</p>
     *
     * @param account  The account.
     * @param amount   The amount to deposit or withdraw.
     *
     * @return  True if the operation is successful.
     */
    public static boolean depositOrWithdraw(IAccount account, double amount) {
        PreCon.notNull(account);
        return Double.compare(amount, 0.0D) == 0 ||
                (amount < 0 ? account.withdraw(amount) : account.deposit(amount));
    }

    /**
     * Determine if the economy provider has bank support.
     */
    public static boolean hasBankSupport() {
        return provider().hasBankSupport();
    }

    /**
     * Get a bank by name.
     *
     * @param bankName  The name of the bank.
     *
     * @return  Null if the bank was not found.
     *
     * @throws java.lang.UnsupportedOperationException if {@code hasBankSupport} returns false.
     */
    @Nullable
    public static IBank getBank(String bankName) {
        return provider().getBank(bankName);
    }

    /**
     * Get a list of banks.
     *
     * @throws java.lang.UnsupportedOperationException if {@code hasBankSupport} returns false.
     */
    public static List<IBank> getBanks() {
        return provider().getBanks();
    }

    /**
     * Create a new bank account.
     *
     * @param bankName  The name of the bank.
     *
     * @return  Null if the bank was not created.
     *
     * @throws java.lang.UnsupportedOperationException if {@code hasBankSupport} returns false.
     */
    @Nullable
    public static IBank createBank(String bankName) {
        return provider().createBank(bankName);
    }

    /**
     * Create a new bank account with the specified player as the owner.
     *
     * @param bankName  The name of the bank.
     * @param ownerId   The ID of the bank owner.
     *
     * @return  Null if the bank was not created.
     *
     * @throws java.lang.UnsupportedOperationException if {@code hasBankSupport} returns false.
     */
    @Nullable
    public static IBank createBank(String bankName, UUID ownerId) {
        return provider().createBank(bankName, ownerId);
    }

    /**
     * Delete a bank.
     *
     * @param bankName  The name of the bank.
     *
     * @return  True if the bank was found and deleted.
     *
     * @throws java.lang.UnsupportedOperationException if {@code hasBankSupport} returns false.
     */
    public static boolean deleteBank(String bankName) {
        return provider().deleteBank(bankName);
    }

    private static IEconomyProvider provider() {
        return Nucleus.getProviderManager().getEconomyProvider();
    }
}
