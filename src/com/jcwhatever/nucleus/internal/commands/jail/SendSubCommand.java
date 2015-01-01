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


package com.jcwhatever.nucleus.internal.commands.jail;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.jail.Jail;
import com.jcwhatever.nucleus.jail.JailSession;
import com.jcwhatever.nucleus.language.Localizable;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@CommandInfo(
        parent="jail",
        command="send",
        staticParams={"playerName", "minutes"},
        description="Imprison player at the default jail.",

        paramDescriptions = {"playerName= The name of the player to imprison.",
                             "minutes= The number of minutes the player will be imprisoned for."})

public final class SendSubCommand extends AbstractCommand {

    @Localizable
    static final String _PLAYER_NOT_FOUND = "Could not find player '{0}'.";
    @Localizable static final String _FAILED = "Failed to send player to Default Jail. Make sure it is setup.";
    @Localizable static final String _SUCCESS = "Player '{0}' sent to Default Jail for {0} minutes.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {
        
        String playerName = args.getName("playerName");
        int minutes = args.getInteger("minutes");
        
        Player player = PlayerUtils.getPlayer(playerName);
        if (player == null) {
            tellError(sender, NucLang.get(_PLAYER_NOT_FOUND, playerName));
            return; // finish
        }
        
        Jail jail = Nucleus.getDefaultJail();
        JailSession jailSession = jail.imprison(player, minutes);
        
        if (jailSession == null) {
            tellError(sender, NucLang.get(_FAILED));
            return; // finish
        }
        
        tellSuccess(sender, NucLang.get(_SUCCESS, playerName, minutes));
    }

}