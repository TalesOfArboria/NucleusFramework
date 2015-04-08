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
import com.jcwhatever.nucleus.providers.kits.IKit;
import com.jcwhatever.nucleus.providers.kits.Kits;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="kits",
        command="add",
        staticParams={ "kitName" },
        description="Add a new chest kit.",

        paramDescriptions = {
                "kitName= The name of the kit items will be added to. {NAME16}"})

class AddSubCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _KIT_ALREADY_EXISTS = "An chest kit named '{0}' already exists.";
    @Localizable static final String _FAILED = "Failed to create chest kit.";
    @Localizable static final String _SUCCESS = "Inventory kit '{0}' created.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        String kitName = args.getName("kitName");

        IKit kit = Kits.get(kitName);
        if (kit != null) {
            tellError(sender, NucLang.get(_KIT_ALREADY_EXISTS, kitName));
            return; // finish
        }

        kit = Kits.add(kitName);
        if (kit == null) {
            tellError(sender, NucLang.get(_FAILED));
            return; // finish
        }

        tellSuccess(sender, NucLang.get(_SUCCESS, kit.getName()));
    }
}
