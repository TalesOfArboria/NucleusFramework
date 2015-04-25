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


package com.jcwhatever.nucleus.internal.commands.scripts.locations;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.scripting.locations.IScriptLocationManager;
import com.jcwhatever.nucleus.utils.coords.NamedLocation;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="locations",
        command = "del",
        staticParams = { "locationName" },
        description = "Remove a script location.",
        paramDescriptions = {
                "locationName= The name of the location to delete."
        })

class DelSubCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _LOCATION_NOT_FOUND =
            "A script location named '{0}' was not found.";

    @Localizable static final String _FAILED = "Failed to remove script location.";
    @Localizable static final String _SUCCESS = "Script location '{0}' removed.";

    @Override
    public void execute (CommandSender sender, ICommandArguments args) throws CommandException {

        String locationName = args.getName("locationName", 48);

        IScriptLocationManager manager = Nucleus.getScriptManager().getLocations();

        NamedLocation scriptLocation = manager.get(locationName);
        if (scriptLocation == null)
            throw new CommandException(NucLang.get(_LOCATION_NOT_FOUND), locationName);

        if (!manager.remove(locationName))
            throw new CommandException(NucLang.get(_FAILED));

        tellSuccess(sender, NucLang.get(_SUCCESS), scriptLocation.getName());
    }
}

