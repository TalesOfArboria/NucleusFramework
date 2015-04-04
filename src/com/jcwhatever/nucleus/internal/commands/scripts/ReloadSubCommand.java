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
import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.internal.scripting.InternalScriptManager;
import com.jcwhatever.nucleus.managed.scripting.IScript;
import com.jcwhatever.nucleus.managed.language.Localizable;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="scripts",
        command = "reload",
        staticParams = {"scriptName="},
        description = "Reload scripts.",

        paramDescriptions = {
                "scriptName= Optional. The name of the script to reload. Omit to reload all scripts."
        })

class ReloadSubCommand extends AbstractCommand {

    @Localizable static final String _RELOAD_ALL = "Scripts reloaded.";
    @Localizable static final String _RELOAD_ONE = "Script '{0: script name}' reloaded.";
    @Localizable static final String _SCRIPT_NOT_FOUND = "A script named '{0: script name}' was not found.";
    @Localizable static final String _FAILED = "Failed to reload script named '{0: script name}'.";

    @Override
    public void execute (CommandSender sender, CommandArguments args) throws InvalidArgumentException {


        if (args.isDefaultValue("scriptName")) {
            Nucleus.getScriptManager().reload();

            tellSuccess(sender, NucLang.get(_RELOAD_ALL));
        }
        else {

            String scriptName = args.getString("scriptName");

            InternalScriptManager manager = (InternalScriptManager)Nucleus.getScriptManager();

            IScript script = manager.getScript(scriptName);
            if (script == null) {
                tellError(sender, NucLang.get(_SCRIPT_NOT_FOUND, scriptName));
                return; // finish
            }

            if (!manager.reload(scriptName)) {
                tellError(sender, NucLang.get(_FAILED, scriptName));
                return; // finish
            }

            tellSuccess(sender, NucLang.get(_RELOAD_ONE, scriptName));
        }
    }
}
