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

import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.providers.economy.Economy;
import com.jcwhatever.nucleus.providers.economy.IAccount;
import com.jcwhatever.nucleus.providers.economy.IEconomyTransaction;
import com.jcwhatever.nucleus.utils.observer.result.FutureSubscriber;
import com.jcwhatever.nucleus.utils.observer.result.Result;
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

class SendSubCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _PLAYER_NOT_FOUND =
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
    public void execute(final CommandSender sender, ICommandArguments args) throws CommandException {

        CommandException.checkNotConsole(getPlugin(), this, sender);

        Player player = (Player)sender;

        final String receiverName = args.getName("playerName");
        final double amount = args.getDouble("amount");

        UUID receiverId = PlayerUtils.getPlayerId(receiverName);
        if (receiverId == null)
            throw new CommandException(NucLang.get(_PLAYER_NOT_FOUND, receiverName));

        final IAccount account = Economy.getAccount(player.getUniqueId());
        if (account == null)
            throw new CommandException(NucLang.get(_NO_SEND_ACCOUNT));

        IAccount receiverAccount = Economy.getAccount(receiverId);
        if (receiverAccount == null)
            throw new CommandException(NucLang.get(_NO_RECEIVE_ACCOUNT, receiverName));

        double balance = account.getBalance();
        if (balance < amount)
            throw new CommandException(NucLang.get(_NOT_ENOUGH_MONEY, balance));

        Economy.transfer(account, receiverAccount, amount)
                .onError(new FutureSubscriber<IEconomyTransaction>() {
                    @Override
                    public void on(Result<IEconomyTransaction> result) {
                        tellError(sender, NucLang.get(_FAILED));
                    }
                })
                .onSuccess(new FutureSubscriber<IEconomyTransaction>() {
                    @Override
                    public void on(Result<IEconomyTransaction> result) {
                        tellSuccess(sender, NucLang.get(_SUCCESS,
                                Economy.getCurrency().format(amount), receiverName, account.getBalance()));
                    }
                });
    }
}
