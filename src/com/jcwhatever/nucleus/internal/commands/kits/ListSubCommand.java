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


package com.jcwhatever.nucleus.internal.commands.kits;

import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.messaging.ChatPaginator;
import com.jcwhatever.nucleus.providers.kits.IKit;
import com.jcwhatever.nucleus.providers.kits.Kits;
import com.jcwhatever.nucleus.utils.ArrayUtils;
import com.jcwhatever.nucleus.utils.text.components.IChatHoverable.HoverAction;
import com.jcwhatever.nucleus.utils.text.components.IChatMessage;
import com.jcwhatever.nucleus.utils.text.format.args.ClickGiveItemsArgModifier;
import com.jcwhatever.nucleus.utils.text.format.args.HoverableArgModifier;
import com.jcwhatever.nucleus.utils.text.format.args.TextArg;
import org.bukkit.command.CommandSender;

import java.util.Collection;

@CommandInfo(
        parent="kits",
        command="list",
        staticParams={ "page=1" },
        description="List inventory kits.",

        paramDescriptions = {
                "page= {PAGE}"})

class ListSubCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _PAGINATOR_TITLE = "Kits";
    @Localizable static final String _FORMAT = "{GOLD}{0} {GRAY}({1} Items, {2} Armor)";
    @Localizable static final String _CLICK_MESSAGE = "{YELLOW}Click to receive kit.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        int	page = args.getInteger("page");

        Collection<IKit> kits = Kits.getAll();

        ChatPaginator pagin = createPagin(args, 5, NucLang.get(_PAGINATOR_TITLE));

        IChatMessage clickMessage = NucLang.get(_CLICK_MESSAGE);

        for (IKit kit : kits) {

            TextArg name = new TextArg(kit.getName(),
                    new ClickGiveItemsArgModifier(ArrayUtils.addAll(kit.getItems(), kit.getArmor())),
                    new HoverableArgModifier(HoverAction.SHOW_TEXT, clickMessage));

            pagin.add(name, kit.getItems().length, kit.getArmor().length);
        }

        pagin.show(sender, page, NucLang.get(_FORMAT).toString());
    }
}
