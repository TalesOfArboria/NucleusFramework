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

package com.jcwhatever.nucleus.internal.commands.providers;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.providers.ProviderType;
import com.jcwhatever.nucleus.managed.language.Localizable;

import org.bukkit.command.CommandSender;

import java.util.Collection;

@CommandInfo(
        parent="providers",
        command = "prefer",
        staticParams = { "providerType", "providerName=" },
        flags = { "clear" },
        description = "Show or set the preferred service provider for a provider type.",

        paramDescriptions = {
                "providerType= The service provider type.",
                "providerName= The name of the service provider to prefer for the type. " +
                        "Leave empty to see current setting.",
                "clear= Optional. Include flag to clear preferred provider for the type."
        })

class PreferSubCommand extends AbstractCommand {

    @Localizable static final String _CLEARED =
            "Cleared preferred service provider for type {0: provider type}.";

    @Localizable static final String _NO_PREFERRED =
            "There is no preferred provider for {0: provider type}.";

    @Localizable static final String _SHOW_PREFERRED =
            "'{0: provider name}' is the preferred {1: provider type} provider.";

    @Localizable static final String _SET_FAILED =
            "Failed to set preferred provider.";

    @Localizable static final String _SET_SUCCESS =
            "Preferred {0: provider type} provider set to '{1: provider name}'";

    @Localizable static final String _PROVIDER_NOT_FOUND_WARNING =
            "{YELLOW}Warning: Provider '{0: provider name}' was not found for type {1: provider type}.";

    @Localizable static final String _RESTART_REQUIRED =
            "A server restart is required for provider changes to take effect.";

    @Override
    public void execute (CommandSender sender, CommandArguments args) throws CommandException {

        ProviderType type = args.getEnum("providerType", ProviderType.class);

        if (args.getBoolean("clear")) {

            Nucleus.getProviders().setPreferred(type, null);

            tellSuccess(sender, NucLang.get(_CLEARED, type.name()));
            return; // finish
        }

        if (args.isDefaultValue("providerName")) {

            String preferred = Nucleus.getProviders().getPreferred(type);
            if (preferred == null) {
                tell(sender, NucLang.get(_NO_PREFERRED, type.name()));
            }
            else {
                tell(sender, NucLang.get(_SHOW_PREFERRED, preferred, type.name()));
            }
            return; // finish
        }

        String name = args.getString("providerName");
        if (!Nucleus.getProviders().setPreferred(type, name)) {

            tellError(sender, NucLang.get(_SET_FAILED));
            return;// finish
        }

        // see if the provider name is valid
        Collection<String> names = Nucleus.getProviders().getNames(type);
        boolean isProviderFound = false;

        for (String providerName : names) {
            if (providerName.equalsIgnoreCase(name)) {
                isProviderFound = true;
                break;
            }
        }

        if (!isProviderFound)
            tell(sender, NucLang.get(_PROVIDER_NOT_FOUND_WARNING, name, type.getName()));

        tellSuccess(sender, NucLang.get(_SET_SUCCESS, type.name(), name));
        tell(sender, NucLang.get(_RESTART_REQUIRED));
    }
}
