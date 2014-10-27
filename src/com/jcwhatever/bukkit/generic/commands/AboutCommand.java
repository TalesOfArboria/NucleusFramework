/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.messaging.Messenger;
import com.jcwhatever.bukkit.generic.utils.TextUtils;

@ICommandInfo(
        command={"about"},
        usage="/{command} about",
        description="Get information about the plugin.",
        permissionDefault=PermissionDefault.TRUE,
        isHelpVisible=false)
public class AboutCommand extends AbstractCommand {

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Messenger.tell(_plugin, sender, "----------------------------------------");

        String text = TextUtils.formatPluginInfo(_plugin, "{BOLD}{GREEN}{plugin-name} v{plugin-version}");
        Messenger.tell(_plugin, sender, text);

        String author = TextUtils.formatPluginInfo(_plugin, "Plugin by {plugin-author}");
        Messenger.tell(_plugin, sender, author);

        String list = TextUtils.formatPluginInfo(_plugin, "{AQUA}For a list of commands, type '/{plugin-command} help'\r");
        Messenger.tell(_plugin, sender, list);

        Messenger.tell(_plugin, sender, "----------------------------------------");
    }
}
