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

package com.jcwhatever.nucleus.internal.commands;

import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.teleport.Teleporter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
        command="tp",
        staticParams = { "x", "y", "z", "yaw", "pitch", "world" },
        description="Teleport to the specified coordinates.",
        paramDescriptions = {
                "x= The X coordinate.",
                "y= The Y coordinate.",
                "z= The Z coordinate.",
                "yaw= The Yaw angle.",
                "pitch= The Pitch angle.",
                "world= The world name.",
        })

public final class TPCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _WORLD_NOT_FOUND = "Failed to find world named '{0: world name}'.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        CommandException.checkNotConsole(getRegistered(), sender);

        double x = args.getDouble("x");
        double y = args.getDouble("y");
        double z = args.getDouble("z");
        float yaw = args.getFloat("yaw");
        float pitch = args.getFloat("pitch");
        String worldName = args.getString("world");

        World world = Bukkit.getWorld(worldName);
        if (world == null)
            throw new CommandException(NucLang.get(_WORLD_NOT_FOUND, worldName));

        Player player = (Player)sender;

        Teleporter.teleport(player, new Location(world, x, y, z, yaw, pitch));
    }
}

