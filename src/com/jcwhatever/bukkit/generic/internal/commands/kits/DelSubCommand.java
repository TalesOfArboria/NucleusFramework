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


package com.jcwhatever.bukkit.generic.internal.commands.kits;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.CommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.inventory.Kit;
import com.jcwhatever.bukkit.generic.inventory.KitManager;
import com.jcwhatever.bukkit.generic.language.Localizable;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="kits",
        command="del",
        staticParams={ "kitName" },
        usage="/{plugin-command} {command} del <kitName>",
        description="Remove an inventory kit.")

public class DelSubCommand extends AbstractCommand {

    @Localizable static final String _KIT_NOT_FOUND = "An inventory kit named '{0}' was not found.";
    @Localizable static final String _FAILED = "Failed to remove inventory kit.";
    @Localizable static final String _SUCCESS = "Inventory kit '{0}' removed.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        String kitName = args.getName("kitName");

        KitManager manager = GenericsLib.getKitManager();

        Kit kit = manager.getKitByName(kitName);
        if (kit == null) {
            tellError(sender, Lang.get(_KIT_NOT_FOUND, kitName));
            return; // finish
        }

        if (!manager.deleteKit(kitName)) {
            tellError(sender, Lang.get(_FAILED));
            return; // finish
        }

        tellSuccess(sender, Lang.get(_SUCCESS, kit.getName()));
    }
}
