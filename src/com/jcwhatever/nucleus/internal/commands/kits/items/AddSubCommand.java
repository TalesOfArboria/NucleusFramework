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


package com.jcwhatever.nucleus.internal.commands.kits.items;

import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.internal.commands.kits.AbstractKitCommand;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.providers.kits.IKit;
import com.jcwhatever.nucleus.providers.kits.IModifiableKit;
import com.jcwhatever.nucleus.providers.kits.Kits;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

@CommandInfo(
        parent="items",
        command="add",
        staticParams={ "kitName", "items" },
        description="Add items to the specified NPC kit.",

        paramDescriptions = { "kitName= The name of the kit items will be added to. {NAME16}",
                              "items= The items to add. {ITEM_STACK}"})

class AddSubCommand extends AbstractKitCommand implements IExecutableCommand {

    @Localizable static final String _KIT_NOT_FOUND = "An chest kit named '{0}' was not found.";
    @Localizable static final String _SUCCESS = "Added items to chest kit '{0}'.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        String kitName = args.getName("kitName");
        ItemStack[] items = args.getItemStack(sender, "items");

        IKit kit = Kits.get(kitName);
        if (kit == null)
            throw new CommandException(NucLang.get(_KIT_NOT_FOUND, kitName));

        IModifiableKit modKit = Kits.modifyKit(kit);
        modKit.addAnyItems(items);
        modKit.save();

        tellSuccess(sender, NucLang.get(_SUCCESS, kit.getName()));
    }

    @Override
    public void onTabComplete(CommandSender sender, String[] arguments,
                              Collection<String> completions) {

        if (arguments.length == 2) {
            completions.add("inhand");
            completions.add("inventory");
            completions.add("hotbar");
        }
        else {
            super.onTabComplete(sender, arguments, completions);
        }
    }
}
