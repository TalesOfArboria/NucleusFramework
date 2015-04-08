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

package com.jcwhatever.nucleus.internal.commands.friends;

import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.providers.friends.Friends;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.UUID;

@CommandInfo(
        command="del",
        staticParams = { "friendName" },
        description="Remove a friend from your friends list.",
        paramDescriptions = { "friendName= The name of player to remove."},
        permissionDefault = PermissionDefault.TRUE)

class DelSubCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _PLAYER_NOT_FOUND = "A player named '{0: friend name}' was not found.";
    @Localizable static final String _NOT_FRIEND = "Player '{0}' is not in your friends list.";
    @Localizable static final String _SUCCESS =  "Player '{0}' removed from your friends list.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        CommandException.checkNotConsole(getPlugin(), this, sender);

        String name = args.getString("friendName");

        UUID friendId = PlayerUtils.getPlayerId(name);
        if (friendId == null)
            throw new CommandException(NucLang.get(_PLAYER_NOT_FOUND, name));

        Player player = (Player)sender;

        if (!Friends.remove(player, friendId))
            throw new CommandException(_NOT_FRIEND, name);

        tellSuccess(sender, NucLang.get(_SUCCESS, name));
    }
}

