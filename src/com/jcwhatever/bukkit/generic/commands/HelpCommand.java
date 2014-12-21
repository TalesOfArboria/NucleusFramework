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


package com.jcwhatever.bukkit.generic.commands;

import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator;
import com.jcwhatever.bukkit.generic.permissions.Permissions;
import com.jcwhatever.bukkit.generic.utils.text.TextUtils.FormatTemplate;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        command={"help", "?"},
        staticParams={"page=1"},
        usage="/{plugin-command} help [page]",
        description="Show commands.",
        permissionDefault=PermissionDefault.TRUE,
        isHelpVisible=false)

public class HelpCommand extends AbstractCommand {

    @Localizable static final String _PAGINATOR_TITLE = "Commands";
    @Localizable static final String _USAGE = "{GOLD}/{plugin-command} {GREEN}{0} {GOLD}?";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidArgumentException {

        int page = args.getInteger("page");

        showHelp(sender, page);
    }

    @Override
    public void showHelp(final CommandSender sender, int page) {

        final ChatPaginator pagin = new ChatPaginator(getPlugin(), 6, Lang.get(_PAGINATOR_TITLE));

        final List<AbstractCommand> categories = new ArrayList<AbstractCommand>(getCommandHandler().getCommands().size());

        for (AbstractCommand cmd : getCommandHandler().getCommands()) {

            if (cmd.getCommands().size() > 0) {
                categories.add(cmd);
                continue;
            }

            CommandInfoContainer info = cmd.getInfo();

            if (!info.isHelpVisible())
                continue;

            if (sender instanceof Player && !Permissions.has(sender, cmd.getPermission().getName()))
                continue;

            pagin.add(info.getUsage(), info.getDescription());
        }

        for (AbstractCommand cmd : categories) {

            CommandInfoContainer info = cmd.getInfo();

            if (!info.isHelpVisible())
                continue;

            if (sender instanceof Player && !Permissions.has(sender, cmd.getPermission().getName()))
                continue;

            pagin.add(Lang.get(getPlugin(), _USAGE, info.getCommandName()), info.getDescription());
        }

        pagin.show(sender, page, FormatTemplate.CONSTANT_DEFINITION);
    }
}

