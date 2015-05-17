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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.providers.friends.Friends;
import com.jcwhatever.nucleus.providers.friends.IFriend;
import com.jcwhatever.nucleus.providers.friends.IFriendLevel;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.UUID;

@CommandInfo(
        command="add",
        staticParams = { "friendName", "level=casual" },
        description="Add a friend to your friends list or change friendship level.",
        paramDescriptions = {
                "friendName= The name of player to add as a friend.",
                "level= Optional. The level of friendship. Possible values " +
                        "are 'casual', 'good', or 'best'. Default is 'casual'. A number value can also be used."
        },
        permissionDefault = PermissionDefault.TRUE)

class AddSubCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _LEVEL_NOT_FOUND = "A friend level named '{0}' was not found.";
    @Localizable static final String _PLAYER_NOT_FOUND = "A player named '{0: friend name}' was not found.";
    @Localizable static final String _SUCCESS_ADD =  "Player '{0}' added to your friends list.";
    @Localizable static final String _SUCCESS_SET =  "Player '{0}' friendship level changed.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        CommandException.checkNotConsole(getPlugin(), this, sender);

        String name = args.getString("friendName");
        int rawLevel;

        if (args.hasInteger("level")) {
            rawLevel = args.getInteger("level");
        }
        else {
            
            String levelName = args.getString("level");

            IFriendLevel level = Nucleus.getProviders().getFriends().getLevel(levelName);
            if (level == null)
                throw new CommandException(NucLang.get(_LEVEL_NOT_FOUND, levelName));

            rawLevel = level.getRawLevel();
        }

        UUID friendId = PlayerUtils.getPlayerId(name);
        if (friendId == null)
            throw new CommandException(NucLang.get(_PLAYER_NOT_FOUND, name));

        Player player = (Player)sender;

        IFriend friend = Friends.get(player, friendId);
        if (friend != null) {
            friend.setRawLevel(rawLevel);
            tellSuccess(sender, NucLang.get(_SUCCESS_SET, name));
        }
        else {
            Friends.add(player, friendId, rawLevel);
            tellSuccess(sender, NucLang.get(_SUCCESS_ADD, name));
        }
    }
}

