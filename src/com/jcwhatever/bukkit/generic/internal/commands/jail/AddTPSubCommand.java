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

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.CommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException.CommandSenderType;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.jail.JailManager;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.mixins.INamedLocation;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
        parent="jail",
        command="addtp",
        staticParams = { "name" },
        usage="/{plugin-command} jail addtp <name>",
        description="Add a location where players are teleported within the jail using your current position.")

public class AddTPSubCommand extends AbstractCommand {

    @Localizable static final String _DUPLICATE_NAME = "There is already a location named '{0}'.";
    @Localizable static final String _FAILED = "Failed to add location.";
    @Localizable static final String _SUCCESS = "Your current location has been added to the default jail and is named '{0}'.";

    @Override
    public void execute(CommandSender sender, CommandArguments args)
            throws InvalidValueException, InvalidCommandSenderException {

        InvalidCommandSenderException.check(sender, CommandSenderType.PLAYER, "Console has no location");

        String name = args.getName("name");

        Player p = (Player)sender;

        Location loc = p.getLocation();

        JailManager jailManager = JailManager.getDefault();

        INamedLocation current = jailManager.getTeleport(name);
        if (current != null) {
            tellError(sender, Lang.get(_DUPLICATE_NAME, name));
            return; // finished
        }

        if (!jailManager.addTeleport(name, loc)) {
            tellError(sender, Lang.get(_FAILED));
            return; // finished
        }

        tellSuccess(sender, Lang.get(_SUCCESS, name));
    }

}
