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

package com.jcwhatever.nucleus.internal.commands.providers;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.messaging.ChatPaginator;
import com.jcwhatever.nucleus.providers.IProvider;
import com.jcwhatever.nucleus.providers.IProviderManager;
import com.jcwhatever.nucleus.providers.ProviderType;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.utils.text.TextUtils.FormatTemplate;

import org.bukkit.command.CommandSender;

import java.util.Collection;

@CommandInfo(
        parent="providers",
        command = "list",
        staticParams = { "page=1" },
        floatingParams = { "search=" },
        flags = { "all" },
        description = "List service providers.",

        paramDescriptions = {
                "page= {PAGE}",
                "search= Optional. Include to filter results by search term.",
                "all= Optional. Include flag to show all discovered providers."
        })

class ListSubCommand extends AbstractCommand {

    @Localizable static final String _PAGINATOR_TITLE = "Service Providers";
    @Localizable static final String _PAGINATOR_TITLE_ALL = "All Service Providers";
    @Localizable static final String _LABEL_UNKNOWN_TYPE = "<unknown type>";
    @Localizable static final String _LABEL_NONE = "<none>";

    @Override
    public void execute (CommandSender sender, CommandArguments args) throws CommandException {

        int page = args.getInteger("page");
        boolean showAll = args.getBoolean("all");

        ChatPaginator pagin = createPagin(7, NucLang.get(showAll ? _PAGINATOR_TITLE_ALL : _PAGINATOR_TITLE));


        if (showAll) {

            IProviderManager manager = Nucleus.getProviders();

            Collection<String> names = manager.getNames();

            for (String name : names) {

                ProviderType type = manager.getType(name);

                pagin.add(name, type == null
                        ? NucLang.get(_LABEL_UNKNOWN_TYPE)
                        : type.getName());
            }
        }
        else {

            for (ProviderType type : ProviderType.values()) {

                IProvider provider = Nucleus.getProviders().get(type);

                pagin.add(type.getName(), provider == null
                        ? NucLang.get(_LABEL_NONE)
                        : provider.getInfo().getName());
            }
        }

        if (!args.isDefaultValue("search"))
            pagin.setSearchTerm(args.getString("search"));

        pagin.show(sender, page, FormatTemplate.LIST_ITEM_DESCRIPTION);
    }
}
