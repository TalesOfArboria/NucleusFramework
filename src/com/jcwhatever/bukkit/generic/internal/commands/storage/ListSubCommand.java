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

package com.jcwhatever.bukkit.generic.internal.commands.storage;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.CommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator;
import com.jcwhatever.bukkit.generic.providers.IStorageProvider;
import com.jcwhatever.bukkit.generic.utils.text.TextUtils.FormatTemplate;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;

@CommandInfo(
        parent="storage",
        command = "list",
        staticParams = { "page=1" },
        floatingParams = { "plugin" },
        usage = "/{plugin-command} storage list [page] [--plugin <pluginName>]",
        description = "List available data storage providers or display the provider for a specific plugin.")

public final class ListSubCommand extends AbstractCommand {

    @Localizable static final String _PAGINATOR_TITLE = "Storage Providers";
    @Localizable static final String _PLUGIN_NOT_FOUND = "A plugin named '{0: plugin name}' was not found.";
    @Localizable static final String _PLUGIN_PROVIDER = "The storage provider for plugin '{0}' is named '{1}'.";
    @Localizable static final String _LABEL_DEFAULT = "[Default]";

    @Override
    public void execute (CommandSender sender, CommandArguments args) throws InvalidArgumentException {

        int page = args.getInteger("page");

        if (args.hasString("plugin")) {
            String pluginName = args.getString("plugin");

            Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
            if (plugin == null) {
                tellError(sender, Lang.get(_PLUGIN_NOT_FOUND, pluginName));
                return; // finish
            }

            IStorageProvider storageProvider = GenericsLib.getProviderManager().getStorageProvider(plugin);

            tell(sender, _PLUGIN_PROVIDER, plugin.getName(), storageProvider.getName());
            return;
        }

        ChatPaginator pagin = new ChatPaginator(GenericsLib.getPlugin(), 7, Lang.get(_PAGINATOR_TITLE));

        List<IStorageProvider> providers = GenericsLib.getProviderManager().getStorageProviders();
        IStorageProvider defaultProvider = GenericsLib.getProviderManager().getStorageProvider();

        pagin.addFormatted(FormatTemplate.LIST_ITEM_DESCRIPTION, defaultProvider.getName(), _LABEL_DEFAULT);

        for (IStorageProvider provider : providers) {
            if (provider == defaultProvider)
                continue;

            pagin.add(provider.getName());
        }

        pagin.show(sender, page, FormatTemplate.LIST_ITEM);
    }
}
