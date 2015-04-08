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


package com.jcwhatever.nucleus.internal.commands.signs;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.messaging.ChatPaginator;
import com.jcwhatever.nucleus.managed.signs.SignHandler;
import com.jcwhatever.nucleus.utils.text.TextUtils.FormatTemplate;

import org.bukkit.command.CommandSender;

import java.util.Collection;

@CommandInfo(
        parent="signs",
        command="types",
        staticParams={"page=1"},
        floatingParams={"search="},
        description="Show a list of sign types.",

        paramDescriptions = {
                "page= {PAGE}",
                "search= Optional. Specify a search filter."
        })

class TypesSubCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _PAGINATOR_TITLE =
            "Sign Types";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        ChatPaginator pagin = createPagin(NucLang.get(_PAGINATOR_TITLE));

        Collection<SignHandler> signHandlers = Nucleus.getSignManager().getSignHandlers();
        for (SignHandler handler : signHandlers) {
            pagin.add(handler.getName(), handler.getDescription());
        }

        if (!args.isDefaultValue("search"))
            pagin.setSearchTerm(args.getString("search"));

        pagin.show(sender, args.getInteger("page"), FormatTemplate.LIST_ITEM_DESCRIPTION);
    }
}
