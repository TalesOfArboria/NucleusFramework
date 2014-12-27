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

package com.jcwhatever.nucleus.internal.commands.titles;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.internal.Lang;
import com.jcwhatever.nucleus.language.Localizable;
import com.jcwhatever.nucleus.titles.INamedTitle;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
        parent="titles",
        command="show",
        staticParams={ "titleName", "playerName=$self" },
        description="Show a title to yourself or the specified player.",

        paramDescriptions = {
                "titleName= The name of the title to show.",
                "playerName= Optional. If not set, the title is sent to the command sender. " +
                        "Otherwise this is the name of the player who will see the title."})

public final class ShowSubCommand extends AbstractCommand {

    @Localizable
    static final String _PLAYER_NOT_FOUND = "A player named '{0}' was not found.";
    @Localizable static final String _TITLE_NOT_FOUND = "A title named '{0}' was not found.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        String name = args.getName("titleName", 32);
        String playerName = args.getString("playerName");

        Player player;

        if (playerName.equals("$self")) {

            CommandException.assertNotConsole(this, sender);

            player = (Player)sender;
        }
        else {
            player = PlayerUtils.getPlayer(playerName);
            if (player == null) {
                tellError(sender,  Lang.get(_PLAYER_NOT_FOUND, playerName));
                return; // finished
            }
        }

        INamedTitle title = Nucleus.getTitleManager().getTitle(name);
        if (title == null) {
            tellError(sender, Lang.get(_TITLE_NOT_FOUND, name));
            return; // finished
        }

        title.showTo(player);
    }
}
