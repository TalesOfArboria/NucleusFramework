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

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="titles",
        command="add",
        staticParams={ "name", "title", "subtitle=" },
        floatingParams = { "in=-1", "out=-1", "stay=-1" },
        description="Add a new title.",

        paramDescriptions = {
                "name= The name of the title. {NAME}",
                "title= The title text.",
                "subtitlle= Optional. The sub-title text.",
                "in= Optional. The number of ticks spent fading in the title.",
                "out= Optional. The number of ticks spent fading out the title.",
                "stay= Optional. The number of ticks the title is displayed for."})

public final class AddSubCommand extends AbstractCommand {

    @Localizable static final String _ALREADY_EXISTS = "A title named '{0}' already exists.";
    @Localizable static final String _FAILED = "Failed to create title.";
    @Localizable static final String _SUCCESS = "Title named '{0}' created.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        String name = args.getName("name", 32);
        String title = args.getString("title");
        String subtitle = args.getString("subtitle");

        int fadein = args.getInteger("in");
        int fadeout = args.getInteger("out");
        int stay = args.getInteger("stay");

        INamedTitle currentTitle = Nucleus.getTitleManager().getTitle(name);
        if (currentTitle != null) {
            tellError(sender, Lang.get(_ALREADY_EXISTS, name));
            return; // finished
        }

        if (!Nucleus.getTitleManager().addTitle(name, title,
                subtitle.isEmpty() ? null : subtitle, fadein, stay, fadeout)) {

            tellError(sender, Lang.get(_FAILED));
            return; // finished
        }

        tellSuccess(sender, Lang.get(_SUCCESS, name));
    }
}
