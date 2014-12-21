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

package com.jcwhatever.bukkit.generic.internal.commands.scripts;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.CommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator;
import com.jcwhatever.bukkit.generic.scripting.IScript;
import com.jcwhatever.bukkit.generic.utils.text.TextUtils.FormatTemplate;

import org.bukkit.command.CommandSender;

import java.util.List;
import javax.script.ScriptEngine;

@CommandInfo(
        parent="scripts",
        command = "list",
        staticParams = { "page=1" },
        usage = "/{plugin-command} scripts list [page]",
        description = "List scripts.")

public final class ListSubCommand extends AbstractCommand {

    @Localizable static final String _PAGINATOR_TITLE = "Scripts";
    @Localizable static final String _LABEL_NO_ENGINE = "<!no engine!>";

    @Override
    public void execute (CommandSender sender, CommandArguments args) throws InvalidArgumentException {

        int page = args.getInteger("page");

        ChatPaginator pagin = new ChatPaginator(GenericsLib.getPlugin(), 7, Lang.get(_PAGINATOR_TITLE));

        List<IScript> scripts = GenericsLib.getScriptManager().getScripts();

        for (IScript script : scripts) {
            ScriptEngine engine = GenericsLib.getScriptEngineManager().getEngineByExtension(script.getType());

            pagin.add(script.getName(), engine != null
                    ? engine.getFactory().getEngineName() + ", " + engine.getFactory().getEngineVersion()
                    : Lang.get(_LABEL_NO_ENGINE));
        }

        pagin.show(sender, page, FormatTemplate.LIST_ITEM_DESCRIPTION);
    }
}
