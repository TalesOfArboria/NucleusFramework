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


package com.jcwhatever.bukkit.generic.internal.commands.jail;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.CommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.CommandException;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.jail.Jail;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.mixins.INamedLocation;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="jail",
        command="deltp",
        staticParams = { "name" },
        description="Remove a location where players are teleported within the jail.",

        paramDescriptions = { "name= The name of the teleport location. {NAME16}"})

public final class DelTPSubCommand extends AbstractCommand {

    @Localizable static final String _NOT_FOUND = "A location named '{0}' was not found.";
    @Localizable static final String _FAILED = "Failed to remove location.";
    @Localizable static final String _SUCCESS = "Location '{0}' removed.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        String name = args.getName("name");

        Jail jail = GenericsLib.getDefaultJail();

        INamedLocation current = jail.getTeleport(name);
        if (current == null) {
            tellError(sender, Lang.get(_NOT_FOUND, name));
            return; // finished
        }

        if (!jail.removeTeleport(name)) {
            tellError(sender, Lang.get(_FAILED));
            return; // finished
        }

        tellSuccess(sender, Lang.get(_SUCCESS, name));
    }

}

