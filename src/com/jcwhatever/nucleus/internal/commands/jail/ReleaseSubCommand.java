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

import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.providers.jail.IJailSession;
import com.jcwhatever.nucleus.providers.jail.Jails;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;

import org.bukkit.command.CommandSender;

import java.util.UUID;


@CommandInfo(
        parent="jail",
        command="release",
        staticParams={"playerName"},
        description="Release prisoner from default jail.",

        paramDescriptions = { "playerName= The name of the player to release." })

class ReleaseSubCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _PLAYER_NOT_FOUND = "Could not find a player with the name '{0}'.";
    @Localizable static final String _PLAYER_NOT_IMPRISONED = "Player '{0}' is not imprisoned in the Default Jail.";
    @Localizable static final String _SUCCESS = "Player '{0}' released from the Default Jail.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {
        String playerName = args.getName("playerName");
        
        UUID playerId = PlayerUtils.getPlayerId(playerName);
        if (playerId == null)
            throw new CommandException(NucLang.get(_PLAYER_NOT_FOUND, playerName));

        IJailSession jailSession = Jails.getSession(playerId);
        
        if (jailSession == null)
            throw new CommandException(NucLang.get(_PLAYER_NOT_IMPRISONED, playerName));

        jailSession.release();
        
        tellSuccess(sender, NucLang.get(_SUCCESS, playerName));
    }
}

