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

package com.jcwhatever.nucleus.internal.commands.respacks;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.messaging.ChatPaginator;
import com.jcwhatever.nucleus.managed.resourcepacks.IResourcePack;
import com.jcwhatever.nucleus.managed.resourcepacks.ResourcePacks;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import org.bukkit.command.CommandSender;

import java.util.Collection;

@CommandInfo(
        parent="respacks",
        command="list",
        staticParams={ "page=1" },
        floatingParams={ "search=" },
        description="List resource packs.",
        paramDescriptions = {
                "page= {PAGE}",
                "search= Optional search parameter."
        })

class ListSubCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _PAGINATOR_TITLE = "Resource Packs";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        int	page = args.getInteger("page");

        Collection<IResourcePack> packs = ResourcePacks.getAll();

        ChatPaginator pagin = new ChatPaginator(Nucleus.getPlugin(), 3, NucLang.get(_PAGINATOR_TITLE));

        for (IResourcePack pack : packs) {
            pagin.add(pack.getName(), pack.getUrl());
        }

        if (!args.isDefaultValue("search")) {
            pagin.setSearchTerm(args.getString("search"));
        }

        pagin.show(sender, page, TextUtils.FormatTemplate.LIST_ITEM_DESCRIPTION);
    }
}
