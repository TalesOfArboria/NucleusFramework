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

package com.jcwhatever.nucleus.internal.commands.players;

import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.messaging.ChatPaginator;
import com.jcwhatever.nucleus.utils.DateUtils;
import com.jcwhatever.nucleus.utils.TimeScale;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

@CommandInfo(
        parent="players",
        command="info",
        staticParams={ "playerName", "page=1" },
        description="Display info about a player.",

        paramDescriptions = {
                "playerName= The name of the player.",
                "page= {PAGE}"})

class InfoSubCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _PAGINATOR_TITLE = "Player Info '{0}'";
    @Localizable static final String _PLAYER_NOT_FOUND = "A player named '{0}' was not found.";

    @Override
    public void execute(final CommandSender sender, ICommandArguments args) throws CommandException {

        final int page = args.getInteger("page");
        String playerName = args.getString("playerName");

        UUID playerId = PlayerUtils.getPlayerId(playerName);
        if (playerId == null)
            throw new CommandException(NucLang.get(_PLAYER_NOT_FOUND, playerName));

        // get actual name
        playerName = PlayerUtils.getPlayerName(playerId);

        final ChatPaginator pagin = createPagin(args, 8, NucLang.get(_PAGINATOR_TITLE, playerName));

        pagin.add("ID", playerId);

        Date firstLogin = PlayerUtils.getFirstLogin(playerId);
        Date lastLogin = PlayerUtils.getLastLogin(playerId);
        int loginCount = PlayerUtils.getLoginCount(playerId);

        Player player = PlayerUtils.getPlayer(playerId);
        if (player != null) {

            long sessionTime = PlayerUtils.getSessionTime(player);
            Location location = player.getLocation();

            pagin.add("Status", "Online");
            pagin.add("World", location.getWorld().getName());
            pagin.add("Coords", location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());

            if (sessionTime < 60 * 1000) {
                pagin.add("Session Duration", (sessionTime / TimeScale.SECONDS.getTimeFactor()) + " seconds");
            }
            else if (sessionTime < 60 * 60 * 1000) {
                pagin.add("Session Duration", (sessionTime / TimeScale.MINUTES.getTimeFactor()) + " minutes");
            }
            else if (sessionTime < 24 * 60 * 60 * 1000) {
                pagin.add("Session Duration", (sessionTime / TimeScale.HOURS.getTimeFactor()) + " hours");
            }
        }
        else {
            pagin.add("Status", "Offline");
        }

        pagin.add("First Login", DateUtils.format(firstLogin, "MMM-d-y H:m"));
        pagin.add("Last Login", DateUtils.format(lastLogin, "MMM-d-y H:m"));
        pagin.add("Login Count", loginCount);

        pagin.show(sender, page, TextUtils.FormatTemplate.LIST_ITEM_DESCRIPTION);
    }
}

