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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.NucleusPlugin;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

@CommandInfo(
        command="debug",
        staticParams = { "pluginName" },
        description="Toggle plugin debugging.",
        paramDescriptions = {
                "pluginName= The name of the plugin to toggle. Must be a Nucleus based plugin."})

public final class DebugCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _NOT_NUCLEUS_PLUGIN =
            "'{0}' is not a Nucleus based plugin.";

    @Localizable static final String _PLUGIN_NOT_FOUND =
            "A plugin named '{0}' was not found.";

    @Localizable static final String _SUCCESS =
            "Plugin '{0}' debug mode set to {1}.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        String pluginName = args.getString("pluginName");

        NucleusPlugin plugin = Nucleus.getNucleusPlugin(pluginName);
        if (plugin == null) {

            Plugin bukkitPlugin = Bukkit.getPluginManager().getPlugin(pluginName);
            if (bukkitPlugin != null)
                throw new CommandException(NucLang.get(_NOT_NUCLEUS_PLUGIN, pluginName));

            throw new CommandException(NucLang.get(_PLUGIN_NOT_FOUND, pluginName));
        }

        plugin.setDebugging(!plugin.isDebugging());

        tell(sender, NucLang.get(_SUCCESS, plugin.getName(), plugin.isDebugging()));
    }
}
