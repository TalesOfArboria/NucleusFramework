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

package com.jcwhatever.generic.internal.commands.economy;

import com.jcwhatever.generic.commands.AbstractCommand;
import com.jcwhatever.generic.commands.CommandInfo;
import com.jcwhatever.generic.commands.arguments.CommandArguments;
import com.jcwhatever.generic.commands.exceptions.CommandException;
import com.jcwhatever.generic.internal.Lang;
import com.jcwhatever.generic.language.Localizable;
import com.jcwhatever.generic.providers.economy.IAccount;
import com.jcwhatever.generic.providers.economy.IBank;
import com.jcwhatever.generic.utils.EconomyUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

@CommandInfo(
        parent="geconomy",
        command="balance",
        staticParams = { "bank=" },
        description="Get your current economy account balance.",
        permissionDefault = PermissionDefault.TRUE,

        paramDescriptions = {
                "bank= Optional. The bank the account is in. Leave blank to check your global account."})

public final class BalanceSubCommand extends AbstractCommand {

    @Localizable static final String _NO_BANK_SUPPORT =
            "The current economy provider does not support banks.";

    @Localizable static final String _BANK_NOT_FOUND =
            "A bank named '{0: bank name}' was not found.";

    @Localizable static final String _ACCOUNT_NOT_FOUND =
            "You don't have an account at bank named '{0: bank name}'.";

    @Localizable static final String _GLOBAL_BALANCE =
            "Your account balance is {0: currency balance}";

    @Localizable static final String _BANK_BALANCE =
            "Your account balance is {0: currency balance} at bank '{1: bank name}'.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        CommandException.assertNotConsole(this, sender);

        Player player = (Player)sender;

        String bankName = args.getString("bank");

        if (bankName.isEmpty()) {
            double balance = EconomyUtils.getBalance(player.getUniqueId());
            tellSuccess(sender, Lang.get(_GLOBAL_BALANCE, EconomyUtils.formatAmount(balance)));
        }
        else {

            if (!EconomyUtils.hasBankSupport()) {
                tellError(sender, Lang.get(_NO_BANK_SUPPORT));
                return; // finish
            }

            IBank bank = EconomyUtils.getBank(bankName);
            if (bank == null) {
                tellError(sender, Lang.get(_BANK_NOT_FOUND, bankName));
                return; // finish
            }

            IAccount account = bank.getAccount(player.getUniqueId());
            if (account == null) {
                tellError(sender, Lang.get(_ACCOUNT_NOT_FOUND, bank.getName()));
                return; // finish
            }

            if (account.getBalance() >= 0) {
                tellSuccess(sender, Lang.get(_BANK_BALANCE,
                        EconomyUtils.formatAmount(account.getBalance()), bank.getName()));
            }
            else {
                tellError(sender, Lang.get(_BANK_BALANCE,
                        EconomyUtils.formatAmount(account.getBalance()), bank.getName()));
            }
        }
    }
}
