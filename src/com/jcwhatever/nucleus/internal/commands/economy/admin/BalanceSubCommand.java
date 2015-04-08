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

package com.jcwhatever.nucleus.internal.commands.economy.admin;

import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.providers.economy.Economy;
import com.jcwhatever.nucleus.providers.economy.IAccount;
import com.jcwhatever.nucleus.providers.economy.IBank;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;

import org.bukkit.command.CommandSender;

import java.util.UUID;

@CommandInfo(
        parent="admin",
        command="balance",
        staticParams = { "playerName", "bank=" },
        description="Get a players balance.",

        paramDescriptions = {
                "playerName= The name of the player to check.",
                "bank= Optional. The bank the account is in. Leave blank to check global account."})

class BalanceSubCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _PLAYER_NOT_FOUND =
            "A player by the name '{0: player name}' was not found.";

    @Localizable static final String _NO_BANK_SUPPORT =
            "The current economy provider does not support banks.";

    @Localizable static final String _BANK_NOT_FOUND =
            "A bank named '{0: bank name}' was not found.";

    @Localizable static final String _ACCOUNT_NOT_FOUND =
            "Player '{0: player name}' does not have an account at bank named '{1: bank name}'.";

    @Localizable static final String _GLOBAL_BALANCE =
            "The players balance is {0: currency balance}";

    @Localizable static final String _BANK_BALANCE =
            "The players balance is {0: currency balance} at bank '{1: bank name}'.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        String playerName = args.getName("playerName");
        String bankName = args.getString("bank");

        UUID playerId = PlayerUtils.getPlayerId(playerName);
        if (playerId == null) {
            tellError(sender, NucLang.get(_PLAYER_NOT_FOUND, playerName));
            return; // finish
        }

        if (bankName.isEmpty()) {
            double balance = Economy.getBalance(playerId);
            tellSuccess(sender, NucLang.get(_GLOBAL_BALANCE, Economy.getCurrency().format(balance)));
        }
        else {

            if (!Economy.hasBankSupport()) {
                tellError(sender, NucLang.get(_NO_BANK_SUPPORT));
                return; // finish
            }

            IBank bank = Economy.getBank(bankName);
            if (bank == null) {
                tellError(sender, NucLang.get(_BANK_NOT_FOUND, bankName));
                return; // finish
            }

            IAccount account = bank.getAccount(playerId);
            if (account == null) {
                tellError(sender, NucLang.get(_ACCOUNT_NOT_FOUND,
                        playerName, bank.getName()));
                return; // finish
            }

            tellSuccess(sender, NucLang.get(_BANK_BALANCE,
                    Economy.getCurrency().format(account.getBalance()), bank.getName()));
        }
    }
}
