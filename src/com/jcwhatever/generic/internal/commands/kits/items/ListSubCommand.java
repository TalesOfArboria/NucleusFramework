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


package com.jcwhatever.generic.internal.commands.kits.items;

import com.jcwhatever.generic.GenericsLib;
import com.jcwhatever.generic.commands.AbstractCommand;
import com.jcwhatever.generic.commands.CommandInfo;
import com.jcwhatever.generic.commands.arguments.CommandArguments;
import com.jcwhatever.generic.commands.exceptions.CommandException;
import com.jcwhatever.generic.internal.Lang;
import com.jcwhatever.generic.kits.Kit;
import com.jcwhatever.generic.kits.KitManager;
import com.jcwhatever.generic.utils.items.serializer.ItemStackSerializer.SerializerOutputType;
import com.jcwhatever.generic.language.Localizable;
import com.jcwhatever.generic.messaging.ChatPaginator;
import com.jcwhatever.generic.utils.items.ItemStackUtils;
import com.jcwhatever.generic.utils.text.TextUtils.FormatTemplate;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

@CommandInfo(
        parent="items",
        command="list",
        staticParams={ "kitName", "page=1" },
        description="List items in an chest kit.",

        paramDescriptions = {
                "kitName= The name of the kit items will be added to. {NAME16}",
                "page= {PAGE}"})

public final class ListSubCommand extends AbstractCommand {

    @Localizable
    static final String _PAGINATOR_TITLE = "Kit Items";
    @Localizable static final String _KIT_NOT_FOUND = "An chest kit named '{0}' was not found.";
    @Localizable static final String _LABEL_ARMOR = "ARMOR";
    @Localizable static final String _LABEL_ITEMS = "ITEMS";
    @Localizable static final String _LABEL_NONE = "<none>";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        String kitName = args.getName("kitName");
        int	page = args.getInteger("page");

        KitManager manager = GenericsLib.getKitManager();

        Kit kit = manager.getKitByName(kitName);
        if (kit == null) {
            tellError(sender, Lang.get(_KIT_NOT_FOUND, kitName));
            return; // finish
        }

        ChatPaginator pagin = new ChatPaginator(GenericsLib.getPlugin(), 5, Lang.get(_PAGINATOR_TITLE));

        // Armor
        pagin.addFormatted(FormatTemplate.SUB_HEADER, Lang.get(_LABEL_ARMOR));
        ItemStack[] armor = kit.getArmor();

        if (armor.length == 0) {
            pagin.addFormatted(FormatTemplate.LIST_ITEM, Lang.get(_LABEL_NONE));
        }
        else {

            for (ItemStack item : armor) {
                pagin.add(ItemStackUtils.serializeToString(item, SerializerOutputType.COLOR));
            }
        }

        // Items
        pagin.addFormatted(FormatTemplate.SUB_HEADER, Lang.get(_LABEL_ITEMS));
        ItemStack[] items = kit.getItems();

        if (items.length == 0) {
            pagin.addFormatted(FormatTemplate.LIST_ITEM, Lang.get(_LABEL_NONE));
        }
        else {

            for (ItemStack item : items) {
                pagin.add(ItemStackUtils.serializeToString(item, SerializerOutputType.COLOR));
            }
        }

        pagin.show(sender, page, FormatTemplate.RAW);
    }
}
