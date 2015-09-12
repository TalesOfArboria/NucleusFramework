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

import org.bukkit.command.CommandSender;

import java.util.List;

@CommandInfo(
        command="debugall",
        staticParams = { "isEnabled" },
        description="Turn debugging on or off for all Nucleus based plugins.",
        paramDescriptions = {
                "isEnabled= True to enable debugging, otherwise false."})

public final class DebugAllCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _SUCCESS =
            "Debug mode set to {0} for all Nucleus based plugins.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        boolean isDebugging = args.getBoolean("isEnabled");

        Nucleus.getPlugin().setDebugging(isDebugging);

        List<NucleusPlugin> plugins = Nucleus.getNucleusPlugins();
        for (NucleusPlugin plugin : plugins) {
            plugin.setDebugging(isDebugging);
        }

        tell(sender, NucLang.get(_SUCCESS, isDebugging));
    }
}
