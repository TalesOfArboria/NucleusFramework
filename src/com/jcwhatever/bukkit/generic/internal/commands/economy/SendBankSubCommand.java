/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

package com.jcwhatever.bukkit.generic.internal.commands.economy;

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.CommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.CommandException;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.providers.economy.IAccount;
import com.jcwhatever.bukkit.generic.providers.economy.IBank;
import com.jcwhatever.bukkit.generic.utils.EconomyUtils;
import com.jcwhatever.bukkit.generic.utils.PlayerUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.UUID;

@CommandInfo(
        command="sendbank",
        staticParams = { "playerName", "amount" },
        floatingParams = { "mybank=", "bank=" },
        description="Send money from your account to another players account.",
        permissionDefault = PermissionDefault.TRUE,

        paramDescriptions = {
                "playerName= The name of the player to give money to.",
                "amount= The amount to give. Must be a positive number.",
                "mybank= The name of your bank to withdraw money from. " +
                       "Leave blank to use your global account.",
                "bank= The name of the bank of the player you are sending money to. " +
                       "Leave blank to send money to the players global account."})

public final class SendBankSubCommand extends AbstractCommand {

    @Localizable static final String _PLAYER_NOT_FOUND =
            "A player by the name '{0: player name}' was not found.";

    @Localizable static final String _BANK_NOT_FOUND =
            "A bank named '{0: bank name}' could not be found.";

    @Localizable static final String _NO_SEND_ACCOUNT_AT_BANK =
            "You do not have a bank account at bank '{0: bank name}'";

    @Localizable static final String _NO_SEND_ACCOUNT =
            "You don't have an account.";

    @Localizable static final String _NO_RECEIVE_ACCOUNT_AT_BANK =
            "'{0: receiver name}' does not have an account at bank '{1: bank name}'.";

    @Localizable static final String _NO_RECEIVE_ACCOUNT =
            "Player '{0: receiving players name}' does not have an account.";

    @Localizable static final String _NOT_ENOUGH_MONEY =
            "You don't have enough money in your account.";

    @Localizable static final String _FAILED =
            "Failed to transfer money.";

    @Localizable static final String _SUCCESS =
            "Sent {0: currency amount} to player '{1: player name}'. New Balance is {2: account balance}";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        CommandException.assertNotConsole(this, sender);

        Player player = (Player)sender;

        String receiverName = args.getName("playerName");
        double amount = args.getDouble("amount");
        String myBankName = args.getString("mybank");
        String bankName = args.getString("bank");

        UUID receiverId = PlayerUtils.getPlayerId(receiverName);
        if (receiverId == null) {
            tellError(sender, Lang.get(_PLAYER_NOT_FOUND, receiverName));
            return; // finish
        }

        // Get command senders account
        IAccount myAccount;
        if (myBankName.isEmpty()) {
            myAccount = EconomyUtils.getAccount(player.getUniqueId());
            if (myAccount == null) {
                tellError(sender, Lang.get(_NO_SEND_ACCOUNT));
                return; //finish
            }
        } else {
            IBank myBank = EconomyUtils.getBank(myBankName);
            if (myBank == null) {
                tellError(sender, Lang.get(_BANK_NOT_FOUND, myBankName));
                return; // finish
            }

            myAccount = myBank.getAccount(player.getUniqueId());

            if (myAccount == null) {
                tellError(sender, Lang.get(_NO_SEND_ACCOUNT_AT_BANK, myBank.getName()));
                return; // finish
            }
        }

        // Get receivers account
        IAccount receiverAccount;
        if (bankName.isEmpty()) {
            receiverAccount = EconomyUtils.getAccount(receiverId);
            if (receiverAccount == null) {
                tellError(sender, Lang.get(_NO_RECEIVE_ACCOUNT, receiverName));
                return; // finish
            }
        }
        else {
            IBank bank = EconomyUtils.getBank(bankName);
            if (bank == null) {
                tellError(sender, Lang.get(_BANK_NOT_FOUND, myBankName));
                return; // finish
            }

            receiverAccount = bank.getAccount(receiverId);
            if (receiverAccount == null) {
                tellError(sender, Lang.get(_NO_RECEIVE_ACCOUNT_AT_BANK, receiverName, bank.getName()));
                return; // finish
            }
        }

        // check command senders balance
        double balance = myAccount.getBalance();
        if (balance < amount) {
            tellError(sender, Lang.get(_NOT_ENOUGH_MONEY, balance));
            return; // finish
        }

        // transfer money
        if (!EconomyUtils.transfer(myAccount, receiverAccount, amount)) {
            tellError(sender, Lang.get(_FAILED));
            return; // finish
        }

        tellSuccess(sender, Lang.get(_SUCCESS, EconomyUtils.formatAmount(amount),
                receiverName, myAccount.getBalance()));
    }
}

