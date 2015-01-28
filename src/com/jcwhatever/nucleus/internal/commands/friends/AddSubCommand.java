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

import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.providers.friends.FriendLevel;
import com.jcwhatever.nucleus.providers.friends.IFriend;
import com.jcwhatever.nucleus.utils.Friends;
import com.jcwhatever.nucleus.utils.language.Localizable;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.UUID;

@CommandInfo(
        command="add",
        staticParams = { "friendName", "level=casual" },
        description="Add a friend to your friends list.",
        paramDescriptions = {
                "friendName= The name of player to add as a friend.",
                "level= Optional. The level of friendship. Possible values " +
                        "are 'casual', 'good', or 'best'. Default is 'casual'."
        },
        permissionDefault = PermissionDefault.TRUE)

public final class AddSubCommand extends AbstractCommand {

    @Localizable static final String _PLAYER_NOT_FOUND = "A player named '{0: friend name}' was not found.";
    @Localizable static final String _ALREADY_FRIEND = "Player '{0}' is already in your friends list.";
    @Localizable static final String _SUCCESS =  "Player '{0}' added to your friends list.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        CommandException.checkNotConsole(this, sender);

        String name = args.getString("friendName");
        FriendLevel level = args.getEnum("level", FriendLevel.class);

        UUID friendId = PlayerUtils.getPlayerId(name);
        if (friendId == null) {
            tellError(sender, NucLang.get(_PLAYER_NOT_FOUND, name));
            return; // finish
        }

        Player player = (Player)sender;

        IFriend friend = Friends.getFriend(player, friendId);
        if (friend != null) {
            tell(sender, NucLang.get(_ALREADY_FRIEND, friend.getName()));
            return; // finish
        }

        Friends.addFriend(player, friendId, level);

        tellSuccess(sender, NucLang.get(_SUCCESS, name));
    }
}

