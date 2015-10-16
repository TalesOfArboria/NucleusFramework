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

package com.jcwhatever.nucleus.internal.commands.respacks;

import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.internal.commands.kits.AbstractKitCommand;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.resourcepacks.IResourcePack;
import com.jcwhatever.nucleus.managed.resourcepacks.ResourcePacks;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="respacks",
        command="require",
        staticParams={ "worldName", "isRequired=" },
        description="Set or view the the resource pack used in a world.",

        paramDescriptions = {
                "worldName= The name of the world.",
                "isRequired= 'true' to require pack, 'false' for not required. "
                        + "Leave empty to see current setting."
        })

class RequireSubCommand extends AbstractKitCommand implements IExecutableCommand {

    @Localizable static final String _WORLD_NOT_FOUND =
            "A world named '{0: world name}' was not found.";
    @Localizable static final String _NO_RESOURCE_PACK =
            "World '{0: world name}' does not have a resource pack.";
    @Localizable static final String _CURRENT_REQUIRED =
            "Resource pack named '{0: pack name}' in world '{1: world name}' is REQUIRED.";
    @Localizable static final String _CURRENT_NOT_REQUIRED =
            "Resource pack named '{0: pack name}' in world '{1: world name}' is not required.";
    @Localizable static final String _SUCCESS_REQUIRED =
            "Resource pack named '{0: pack name}' in world '{1: world name}' set to REQUIRED.";
    @Localizable static final String _SUCCESS_NOT_REQUIRED =
            "Resource pack named '{0: pack name}' in world '{1: world name}' set to 'not required'.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        String worldName = args.getString("worldName");
        World world = Bukkit.getWorld(worldName);
        if (world == null)
            throw new CommandException(NucLang.get(_WORLD_NOT_FOUND, worldName));

        IResourcePack pack = ResourcePacks.getWorld(world);
        if (pack == null)
            throw new CommandException(NucLang.get(_NO_RESOURCE_PACK, worldName));

        if (args.isDefaultValue("isRequired")) {
            boolean isRequired = ResourcePacks.isRequired(world);
            if (isRequired) {
                tell(sender, NucLang.get(_CURRENT_REQUIRED, pack.getName(), world.getName()));
            }
            else {
                tell(sender, NucLang.get(_CURRENT_NOT_REQUIRED, pack.getName(), world.getName()));
            }
        }
        else {

            boolean isRequired = args.getBoolean("isRequired");
            ResourcePacks.setRequired(world, isRequired);

            if (isRequired) {
                tellSuccess(sender, NucLang.get(_SUCCESS_REQUIRED, pack.getName(), world.getName()));
            }
            else {
                tellSuccess(sender, NucLang.get(_SUCCESS_NOT_REQUIRED, pack.getName(), world.getName()));
            }
        }
    }
}
