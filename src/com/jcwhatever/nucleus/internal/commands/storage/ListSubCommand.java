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
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.messaging.ChatPaginator;
import com.jcwhatever.nucleus.providers.storage.IStorageProvider;
import com.jcwhatever.nucleus.utils.text.TextUtils.FormatTemplate;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;

@CommandInfo(
        parent="storage",
        command = "list",
        staticParams = { "page=1" },
        floatingParams = { "plugin=" },
        description = "List available data storage providers or display the provider for a specific plugin.",

        paramDescriptions = {
                "plugin= Optional name of the the plugin to show the plugins data provider.",
                "page= {PAGE}"})

class ListSubCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _PAGINATOR_TITLE = "Storage Providers";
    @Localizable static final String _PLUGIN_NOT_FOUND = "A plugin named '{0: plugin name}' was not found.";
    @Localizable static final String _PLUGIN_PROVIDER = "The storage provider for plugin '{0}' is named '{1}'.";
    @Localizable static final String _LABEL_DEFAULT = "[Default]";

    @Override
    public void execute (CommandSender sender, ICommandArguments args) throws CommandException {

        int page = args.getInteger("page");

        if (!args.isDefaultValue("plugin")) {
            String pluginName = args.getString("plugin");

            Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
            if (plugin == null) {
                tellError(sender, NucLang.get(_PLUGIN_NOT_FOUND, pluginName));
                return; // finish
            }

            IStorageProvider storageProvider = Nucleus.getProviders().getStorage(plugin);

            tell(sender, _PLUGIN_PROVIDER, plugin.getName(), storageProvider.getInfo().getName());
            return;
        }

        ChatPaginator pagin = new ChatPaginator(Nucleus.getPlugin(), 7, NucLang.get(_PAGINATOR_TITLE));

        List<IStorageProvider> providers = Nucleus.getProviders().getStorageProviders();
        IStorageProvider defaultProvider = Nucleus.getProviders().getStorage();

        pagin.addFormatted(FormatTemplate.LIST_ITEM_DESCRIPTION, defaultProvider.getInfo().getName(), _LABEL_DEFAULT);

        for (IStorageProvider provider : providers) {
            if (provider == defaultProvider)
                continue;

            pagin.add(provider.getInfo().getName());
        }

        pagin.show(sender, page, FormatTemplate.LIST_ITEM);
    }
}
