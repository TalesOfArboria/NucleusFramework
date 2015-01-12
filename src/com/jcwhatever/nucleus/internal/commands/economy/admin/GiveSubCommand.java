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

import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.language.Localizable;
import com.jcwhatever.nucleus.utils.Economy;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;

import org.bukkit.command.CommandSender;

import java.util.UUID;

@CommandInfo(
        command="give",
        staticParams = { "playerName", "amount" },
        description="Give money to another player.",

        paramDescriptions = {
                "playerName= The name of the player to give money to.",
                "amount= The amount to give. Must be a positive number."})

public final class GiveSubCommand extends AbstractCommand {

    @Localizable
    static final String _PLAYER_NOT_FOUND =
            "A player by the name '{0}' was not found.";

    @Localizable static final String _FAILED =
            "Failed to deposit money.";

    @Localizable static final String _SUCCESS =
            "Gave {0: currency amount} to player '{1: player name}'.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        String playerName = args.getName("playerName");
        double amount = args.getDouble("amount");

        UUID playerId = PlayerUtils.getPlayerId(playerName);
        if (playerId == null) {
            tellError(sender, NucLang.get(_PLAYER_NOT_FOUND, playerName));
            return; // finish
        }

        if (!Economy.deposit(playerId, amount)) {
            tellError(sender, NucLang.get(_FAILED));
            return; // finish
        }

        tellSuccess(sender, NucLang.get(_SUCCESS, Economy.getCurrency().format(amount), playerName));
    }
}
