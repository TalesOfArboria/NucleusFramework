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


package com.jcwhatever.nucleus.commands;

import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.nucleus.internal.Lang;
import com.jcwhatever.nucleus.language.Localizable;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

@CommandInfo(
        command={"about"},
        description="Get information about the plugin.",
        permissionDefault=PermissionDefault.TRUE,
        isHelpVisible = false)
public class AboutCommand extends AbstractCommand {

    @Localizable static final String _HEADER = "----------------------------------------";
    @Localizable static final String _PLUGIN_NAME = "{BOLD}{GREEN}{plugin-name} {plugin-version}";
    @Localizable static final String _AUTHOR = "Plugin by {plugin-author}";
    @Localizable static final String _HELP = "{AQUA}For a list of commands, type '/{plugin-command} ?'";
    @Localizable static final String _HELP2 = "{DARK_AQUA}Add ? after any command to get help. " +
            "Add ?? after any command to get detailed help.";
    @Localizable static final String _FOOTER = "----------------------------------------";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidArgumentException {

        // show header
        tell(sender, Lang.get(getPlugin(), _HEADER));

        // show plugin name
        tell(sender, Lang.get(getPlugin(), _PLUGIN_NAME));

        List<String> authors = getPlugin().getDescription().getAuthors();

        // show authors, if any
        if (authors != null &&
                !authors.isEmpty()) {

            tell(sender, Lang.get(getPlugin(), _AUTHOR));
        }

        // show help text
        tell(sender, Lang.get(getPlugin(), _HELP));

        // show help text
        tell(sender, Lang.get(getPlugin(), _HELP2));

        // show footer
        tell(sender, Lang.get(getPlugin(), _FOOTER));
    }
}
