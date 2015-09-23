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
import com.jcwhatever.nucleus.managed.commands.arguments.ILocationHandler;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.scripting.locations.IScriptLocationManager;
import com.jcwhatever.nucleus.utils.coords.NamedLocation;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
        parent="locations",
        command = "add",
        staticParams = { "locationName", "location" },
        description = "Add a new script location.",
        paramDescriptions = {
                "locationName= The name of the location. {NAME}",
                "location= The location. {LOCATION}"
        })

class AddSubCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _LOCATION_ALREADY_EXISTS =
            "There is already a location with the name '{0}'.";

    @Localizable static final String _FAILED = "Failed to add location.";
    @Localizable static final String _SUCCESS = "Script location '{0}' created.";

    @Override
    public void execute (CommandSender sender, ICommandArguments args) throws CommandException {

        CommandException.checkNotConsole(getPlugin(), this, sender);

        final String locationName = args.getName("locationName", 48);

        final IScriptLocationManager manager = Nucleus.getScriptManager().getLocations();

        NamedLocation scriptLocation = manager.get(locationName);
        if (scriptLocation != null)
            throw new CommandException(NucLang.get(_LOCATION_ALREADY_EXISTS, locationName));

        args.getLocation(sender, "location", new ILocationHandler() {

            @Override
            public void onLocationRetrieved(Player player, Location result) {

                NamedLocation scriptLocation = manager.add(locationName, result);
                if (scriptLocation == null) {
                    tellError(player, NucLang.get(_FAILED));
                }
                else {
                    tellSuccess(player, NucLang.get(_SUCCESS), locationName);
                }
            }
        });
    }
}
