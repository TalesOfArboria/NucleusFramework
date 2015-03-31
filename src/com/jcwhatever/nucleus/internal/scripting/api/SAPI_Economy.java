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


package com.jcwhatever.nucleus.internal.scripting.api;

import com.jcwhatever.nucleus.internal.providers.economy.NucleusCurrency;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.providers.economy.IAccount;
import com.jcwhatever.nucleus.providers.economy.IBank;
import com.jcwhatever.nucleus.providers.economy.ICurrency;
import com.jcwhatever.nucleus.providers.economy.IEconomyTransaction;
import com.jcwhatever.nucleus.utils.Economy;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;

import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Provides script with Economy API
 */
public class SAPI_Economy implements IDisposable {

    private boolean _isDisposed;

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        _isDisposed = true;
    }

    /**
     * Get the balance of a players global account.
     *
     * @param player  The player.
     */
    public double getBalance(Object player) {
        PreCon.notNull(player);

        Player p = PlayerUtils.getPlayer(player);
        PreCon.notNull(p);

        return Economy.getBalance(p.getUniqueId());
    }

    /**
     * Get a players global account.
     *
     * @param player  The player.
     */
    public IAccount getAccount(Object player) {
        PreCon.notNull(player);

        Player p = PlayerUtils.getPlayer(player);
        PreCon.notNull(p);

        return Economy.getAccount(p.getUniqueId());
    }

    /**
     * Get the balance of a players global account.
     *
     * @param player    The player.
     * @param currency  The currency of the returned balance.
     */
    public double getBalanceCurrency(Object player, ICurrency currency) {
        PreCon.notNull(player);

        Player p = PlayerUtils.getPlayer(player);
        PreCon.notNull(p);

        return Economy.getBalance(p.getUniqueId(), currency);
    }

    /**
     * Deposit money into a players global account.
     *
     * @param player  The player.
     * @param amount  The amount to give.
     */
    public void deposit(Object player, double amount) {
        PreCon.notNull(player);

        Player p = PlayerUtils.getPlayer(player);
        PreCon.notNull(p);

        Economy.deposit(p.getUniqueId(), amount);
    }

    /**
     * Deposit money into a players global account.
     *
     * @param player    The player.
     * @param amount    The amount to give.
     * @param currency  The currency of the amount.
     */
    public void depositCurrency(Object player, double amount, ICurrency currency) {
        PreCon.notNull(player);

        Player p = PlayerUtils.getPlayer(player);
        PreCon.notNull(p);

        Economy.deposit(p.getUniqueId(), amount, currency);
    }

    /**
     * Withdraw money from a players global account.
     *
     * @param player  The player.
     * @param amount  The amount to take.
     */
    public void withdraw(Object player, double amount) {
        PreCon.notNull(player);

        Player p = PlayerUtils.getPlayer(player);
        PreCon.notNull(p);

        Economy.withdraw(p.getUniqueId(), amount);
    }

    /**
     * Withdraw money from a players global account.
     *
     * @param player  The player.
     * @param amount  The amount to take.
     */
    public void withdrawCurrency(Object player, double amount, ICurrency currency) {
        PreCon.notNull(player);

        Player p = PlayerUtils.getPlayer(player);
        PreCon.notNull(p);

        Economy.withdraw(p.getUniqueId(), amount, currency);
    }

    /**
     * Format an amount into a displayable String using
     * the economy providers currency.
     *
     * @param amount  The amount to format.
     */
    public String formatAmount(double amount) {

        return Economy.getCurrency().format(amount);
    }

    /**
     * Get the economy providers currency.
     */
    public ICurrency getCurrency() {
        return Economy.getCurrency();
    }

    /**
     * Create a new currency.
     *
     * @param singular       The singular name of the currency.
     * @param plural         The plural name of the currency.
     * @param factor         The currency conversion factor.
     * @param template       The format template.
     * @param decimalFormat  The decimal format.
     */
    public ICurrency createCurrency(String singular, String plural, double factor,
                                    String template, String decimalFormat) {
        PreCon.notNull(singular);
        PreCon.notNull(plural);
        PreCon.notNull(template);
        PreCon.notNull(decimalFormat);

        DecimalFormat format = new DecimalFormat(decimalFormat);
        return new NucleusCurrency(singular, plural, factor, template, format);
    }

    /**
     * Determine if the economy provider has bank support.
     */
    public boolean hasBankSupport() {
        return Economy.hasBankSupport();
    }

    /**
     * Get a bank.
     *
     * @param bankName  The name of the bank.
     */
    @Nullable
    public IBank getBank(String bankName) {
        PreCon.notNull(bankName);

        return Economy.getBank(bankName);
    }

    /**
     * Create a bank.
     *
     * @param bankName  The name of the bank.
     */
    @Nullable
    public IBank createBank(String bankName) {
        PreCon.notNull(bankName);

        return Economy.createBank(bankName);
    }

    /**
     * Create a bank.
     *
     * @param bankName     The name of the bank.
     * @param playerOwner  The owner of the bank.
     */
    @Nullable
    public IBank createOwnedBank(String bankName, Object playerOwner) {
        PreCon.notNull(bankName);

        Player p = PlayerUtils.getPlayer(playerOwner);
        PreCon.notNull(p);

        return Economy.createBank(bankName, p.getUniqueId());
    }

    /**
     * Delete a bank.
     *
     * @param bankName  The name of the bank.
     */
    @Nullable
    public boolean deleteBank(String bankName) {
        PreCon.notNull(bankName);

        return Economy.deleteBank(bankName);
    }

    /**
     * Get all banks.
     */
    public List<IBank> getBanks() {
        return Economy.getBanks();
    }

    /**
     * Create a transaction.
     */
    public IEconomyTransaction createTransaction() {
        return Economy.createTransaction();
    }
}

