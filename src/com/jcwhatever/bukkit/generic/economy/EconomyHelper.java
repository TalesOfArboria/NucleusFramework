/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.economy;

import com.jcwhatever.bukkit.generic.events.bukkit.economy.EconGiveEvent;
import com.jcwhatever.bukkit.generic.events.bukkit.economy.EconWithdrawEvent;
import com.jcwhatever.bukkit.generic.player.PlayerHelper;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.UUID;

/**
 * Provides static functions to interface with the installed economy
 * via Vault plugin.
 */
public class EconomyHelper {
    private static boolean _hasEconomy = false;
    private static Object _econ;

    static {
        init();
    }

    private EconomyHelper() {}

    /**
     * Specifies how a currency name is used.
     */
    public enum CurrencyNoun {
        SINGULAR,
        PLURAL
    }

    /**
     * Determine if the an economy plugin is uninstalled.
     */
    public static boolean hasEconomy() {
        return _hasEconomy;
    }

    /**
     * Get the vault Economy wrapper.
     */
    public static Economy getEconomy() {
        return hasEconomy() ? (Economy)_econ : null;
    }

    /**
     * Get a players balance.
     *
     * @param p  The player.
     */
    public static double getBalance(Player p) {
        PreCon.notNull(p);

        if (!_hasEconomy) return 0;

        if (p == null)
            return 0;

        return getEconomy().getBalance(p.getName());
    }

    /**
     * Get a players balance.
     *
     * @param playerId  The id of the player.
     */
    public static double getBalance(UUID playerId) {
        PreCon.notNull(playerId);

        if (!_hasEconomy) return 0;

        String playerName = PlayerHelper.getPlayerName(playerId);
        if (playerName == null || playerName.equals("[unknown]"))
            return 0;

        return getEconomy().getBalance(playerName);
    }


    /**
     * Get a players balance as a formatted string.
     *
     * @param p  The player.
     */
    public static String getBalanceText(Player p) {
        if (!_hasEconomy) return "0";
        return getEconomy().format(getBalance(p));
    }

    /**
     * Get a players balance as a formatted string.
     *
     * @param playerId  The id of the player.
     * @return
     */
    public static String getBalanceText(UUID playerId) {
        if (!_hasEconomy) return "0";

        return getEconomy().format(getBalance(playerId));
    }

    /**
     * Format an amount into a string using the economy settings.
     *
     * @param amount  The amount to format.
     */
    public static String formatAmount(double amount) {
        if (!_hasEconomy) return "";
        return getEconomy().format(amount);
    }

    /**
     * Get the currency name.
     *
     * @param noun  The type of noun to return.
     */
    public static String getCurrencyName(CurrencyNoun noun) {
        if (!_hasEconomy) return "";

        switch (noun) {
            case SINGULAR:
                return getEconomy().currencyNameSingular();
            case PLURAL:
                return getEconomy().currencyNamePlural();
        }

        return "";
    }

    /**
     * Transfer money between two players.
     *
     * @param giverPlayerId     The id of the player who is giving money
     * @param receiverPlayerId  The id of the player who is receiving money
     * @param amount            The amount of money to transfer.
     *
     * @return  True if the operation completed successfully.
     */
    public static boolean transferMoney(UUID giverPlayerId, UUID receiverPlayerId, double amount) {
        String giverName = PlayerHelper.getPlayerName(giverPlayerId);
        if (giverName == null || giverName.equals("[unknown]")) {
            return false;
        }

        String receiverName = PlayerHelper.getPlayerName(receiverPlayerId);
        if (receiverName == null || receiverName.equals("[unknown]")) {
            return false;
        }

        // check givers balance
        if (getBalance(giverPlayerId) < amount) {
            return false;
        }

        if (!giveMoney(giverName, giverPlayerId, -amount)) {
            return false;
        }

        if (!giveMoney(receiverName, receiverPlayerId, amount)) {
            // give money back
            giveMoney(giverName, giverPlayerId, amount);
            return false;
        }

        return true;
    }

    /**
     * Give money to a player. Take money by providing a negative number.
     *
     * @param p           The player to give money to.
     * @param amount      The amount to give the player.
     *
     * @return  True if the operation is successful.
     */
    public static boolean giveMoney(Player p, double amount) {
        return giveMoney(p.getName(), p.getUniqueId(), amount);
    }

    /**
     * Give money to a player. Take money by providing a negative number.
     *
     * @param playerId  The id of the player to give money to.
     * @param amount    The amount to give the player.
     *
     * @return  True if the operation is successful.
     */
    public static boolean giveMoney(UUID playerId, double amount) {
        String playerName = PlayerHelper.getPlayerName(playerId);
        return !(playerName == null || playerName.equals("[unknown]")) && giveMoney(playerName, playerId, amount);
    }

    private static boolean giveMoney(String playerName, UUID playerId, double amount) {
        PreCon.notNullOrEmpty(playerName);
        PreCon.notNull(playerId);

        if (!hasEconomy())
            return false;

        Economy econ = getEconomy();
        EconomyResponse r;

        if (amount > 0) {
            EconGiveEvent event = EconGiveEvent.callEvent(playerId, amount);

            r = econ.depositPlayer(playerName, event.getAmount());
        }
        else {
            EconWithdrawEvent event = EconWithdrawEvent.callEvent(playerId, amount);

            r = econ.withdrawPlayer(playerName, Math.abs(event.getAmount()));
        }


        if (!r.transactionSuccess()) {
            return false;
        }

        return true;
    }

    private static void init() {
        Plugin _vault = Bukkit.getPluginManager().getPlugin("Vault");
        if (!(_vault instanceof Vault)) {
            _hasEconomy = false;
            return;
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            _hasEconomy = false;
            return;
        }
        Economy econ = rsp.getProvider();
        if (econ == null) {
            _hasEconomy = false;
            return;
        }
        _econ = econ;
        _hasEconomy = true;
    }


}
