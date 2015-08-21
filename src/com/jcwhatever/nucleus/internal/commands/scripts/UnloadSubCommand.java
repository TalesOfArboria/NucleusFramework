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

package com.jcwhatever.nucleus.internal.commands.scripts;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.internal.managed.scripting.InternalScriptManager;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.scripting.IScript;
import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="scripts",
        command = "unload",
        staticParams = {"scriptName"},
        description = "Unload a script.",

        paramDescriptions = {
                "scriptName= The name of the script to unload."
        })

class UnloadSubCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _SCRIPT_NOT_FOUND = "A script named '{0: script name}' was not found.";
    @Localizable static final String _FAILED = "Failed to reload script named '{0: script name}'.";
    @Localizable static final String _SUCCESS = "Script '{0: script name}' unloaded.";

    @Override
    public void execute (CommandSender sender, ICommandArguments args) throws CommandException {

        String scriptName = args.getString("scriptName");

        InternalScriptManager manager = (InternalScriptManager)Nucleus.getScriptManager();

        IScript script = manager.getScript(scriptName);
        if (script == null)
            throw new CommandException(NucLang.get(_SCRIPT_NOT_FOUND, scriptName));

        if (!manager.unload(scriptName))
            throw new CommandException(NucLang.get(_FAILED, scriptName));

        tellSuccess(sender, NucLang.get(_SUCCESS, scriptName));
    }
}

