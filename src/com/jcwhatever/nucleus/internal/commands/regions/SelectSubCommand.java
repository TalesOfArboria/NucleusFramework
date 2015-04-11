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

package com.jcwhatever.nucleus.internal.commands.regions;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.providers.regionselect.RegionSelection;
import com.jcwhatever.nucleus.regions.IRegion;
import com.jcwhatever.nucleus.regions.SimpleRegionSelection;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        parent="regions",
        command = "select",
        staticParams = { "regionName=" },
        description = "Make a region selection using the region you are standing in.",

        paramDescriptions = {
                "regionName= Optional. Specifies which region to use when standing in more " +
                        "than one region."})

class SelectSubCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _NO_REGIONS =
            "No Nucleus based region was found where you are standing.";

    @Localizable static final String _FOUND_MORE_THAN_ONE =
            "More than one region was found. Please specify with " +
                    "one of the following region names:\n{WHITE}{0: region name list}";

    @Localizable static final String _REGION_NOT_FOUND =
            "Could not find a region.";

    @Localizable static final String _NAMED_REGION_NOT_FOUND =
            "Could not find a region named '{0: region name}' at your location.";

    @Override
    public void execute (CommandSender sender, ICommandArguments args) throws CommandException {

        CommandException.checkNotConsole(getPlugin(), this, sender);

        String regionName = args.getString("regionName");
        boolean hasRegionName = !args.isDefaultValue("regionName");

        Player p = (Player)sender;

        List<IRegion> regions = Nucleus.getRegionManager().getRegions(p.getLocation());


        if (regions.isEmpty())
            throw new CommandException(NucLang.get(_NO_REGIONS));

        if (regions.size() > 1 && !hasRegionName) {

            List<String> regionNames = new ArrayList<String>(regions.size());
            for (IRegion r : regions) {
                regionNames.add(r.getName() + '(' + r.getPlugin().getName() + ')');
            }

            throw new CommandException(
                    NucLang.get(_FOUND_MORE_THAN_ONE, TextUtils.concat(regionNames, ", ")));
        }

        IRegion region = null;

        for (IRegion r : regions) {

            if ((regions.size() == 1 && !hasRegionName ||
                    r.getSearchName().equalsIgnoreCase(regionName))) {

                region = r;
                break;
            }
        }

        if (region == null)
            throw new CommandException(hasRegionName
                    ? NucLang.get(getPlugin(), _NAMED_REGION_NOT_FOUND, regionName)
                    : NucLang.get(getPlugin(), _REGION_NOT_FOUND));

        RegionSelection.set(p, new SimpleRegionSelection(region.getP1(), region.getP2()));

        tellSuccess(sender,
                "Region selection set using the coordinates from region " +
                "'{0: region name}' from plugin '{1: plugin name}'.",
                region.getName(), region.getPlugin().getName());
    }
}
