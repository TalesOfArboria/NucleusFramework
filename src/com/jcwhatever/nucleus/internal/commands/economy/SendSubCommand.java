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

package com.jcwhatever.nucleus.internal.commands.economy;

import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.internal.Lang;
import com.jcwhatever.nucleus.language.Localizable;
import com.jcwhatever.nucleus.providers.economy.IAccount;
import com.jcwhatever.nucleus.utils.Economy;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.UUID;

@CommandInfo(
        command="send",
        staticParams = { "playerName", "amount" },
        description="Send money from your global account to another players global account.",
        permissionDefault = PermissionDefault.TRUE,

        paramDescriptions = {
                "playerName= The name of the player to give money to.",
                "amount= The amount to give. Must be a positive number."})

public final class SendSubCommand extends AbstractCommand {

    @Localizable
    static final String _PLAYER_NOT_FOUND =
            "A player by the name '{0}' was not found.";

    @Localizable static final String _NO_SEND_ACCOUNT =
            "You don't have an account.";

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

        CommandException.checkNotConsole(this, sender);

        Player player = (Player)sender;

        String receiverName = args.getName("playerName");
        double amount = args.getDouble("amount");

        UUID receiverId = PlayerUtils.getPlayerId(receiverName);
        if (receiverId == null) {
            tellError(sender, Lang.get(_PLAYER_NOT_FOUND, receiverName));
            return; // finish
        }

        IAccount account = Economy.getAccount(player.getUniqueId());
        if (account == null) {
            tellError(sender, Lang.get(_NO_SEND_ACCOUNT));
            return; //finish
        }

        IAccount receiverAccount = Economy.getAccount(receiverId);
        if (receiverAccount == null) {
            tellError(sender, Lang.get(_NO_RECEIVE_ACCOUNT, receiverName));
            return; // finish
        }

        double balance = account.getBalance();
        if (balance < amount) {
            tellError(sender, Lang.get(_NOT_ENOUGH_MONEY, balance));
            return; // finish
        }

        if (!Economy.transfer(account, receiverAccount, amount)) {
            tellError(sender, Lang.get(_FAILED));
            return; // finish
        }

        tellSuccess(sender, Lang.get(_SUCCESS, Economy.formatAmount(amount), receiverName, account.getBalance()));
    }
}
