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

package com.jcwhatever.nucleus.internal.commands.storage;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.utils.language.Localizable;
import com.jcwhatever.nucleus.providers.IStorageProvider;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

@CommandInfo(
        parent="storage",
        command="set",
        staticParams={ "pluginName", "storageName" },
        description="Set a plugins data storage provider.",

        paramDescriptions = {
                "pluginName= The name of the plugin that will have its storage provider changed.",
                "storageName= The name of the storage provider."})

public final class SetSubCommand extends AbstractCommand {

    @Localizable
    static final String _PLUGIN_NOT_FOUND = "A plugin named '{0: plugin name}' was not found.";
    @Localizable static final String _PROVIDER_NOT_FOUND = "A data storage provider named '{0: provider name}' was not found.";
    @Localizable static final String _SUCCESS = "Data storage provider for plugin '{0: plugin name}' set to provider named '{1 : provider name}'. Server restart required to take effect.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        String pluginName = args.getString("pluginName");
        String storageName = args.getString("storageName");

        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (plugin == null) {
            tellError(sender, NucLang.get(_PLUGIN_NOT_FOUND, pluginName));
            return; // finish
        }

        IStorageProvider provider = Nucleus.getProviderManager().getStorageProvider(storageName);
        if (provider == null) {
            tellError(sender, NucLang.get(_PROVIDER_NOT_FOUND, storageName));
            return; // finish
        }

        Nucleus.getProviderManager().setStorageProvider(plugin, provider);

        tellSuccess(sender, NucLang.get(_SUCCESS, plugin.getName(), provider.getName()));
    }
}

