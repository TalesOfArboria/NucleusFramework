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

package com.jcwhatever.bukkit.generic.internal.commands.plugins;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.GenericsPlugin;
import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.CommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.CommandException;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator;
import com.jcwhatever.bukkit.generic.utils.text.TextUtils.FormatTemplate;

import org.bukkit.command.CommandSender;

import java.util.List;

@CommandInfo(
        parent="plugins",
        command = "list",
        staticParams = { "page=1" },
        description = "List GenericsLib based plugins.",

        paramDescriptions = {
                "page= {PAGE}"})

public final class ListSubCommand extends AbstractCommand {

    @Localizable static final String _PAGINATOR_TITLE = "Plugins";

    @Override
    public void execute (CommandSender sender, CommandArguments args) throws CommandException {

        int page = args.getInteger("page");

        ChatPaginator pagin = new ChatPaginator(GenericsLib.getPlugin(), 7, Lang.get(_PAGINATOR_TITLE));

        List<GenericsPlugin> plugins = GenericsLib.getGenericsPlugins();

        for (GenericsPlugin plugin : plugins) {
            pagin.add(plugin.getName(), plugin.getChatPrefix());
        }

        pagin.show(sender, page, FormatTemplate.LIST_ITEM_DESCRIPTION);
    }
}
