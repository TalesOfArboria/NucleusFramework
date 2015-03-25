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
import com.jcwhatever.nucleus.commands.CommandDispatcher;
import com.jcwhatever.nucleus.internal.commands.economy.NEconomyCommand;
import com.jcwhatever.nucleus.internal.commands.friends.NFriendsCommand;
import com.jcwhatever.nucleus.internal.commands.jail.JailCommand;
import com.jcwhatever.nucleus.internal.commands.kits.KitsCommand;
import com.jcwhatever.nucleus.internal.commands.plugins.PluginsCommand;
import com.jcwhatever.nucleus.internal.commands.providers.ProvidersCommand;
import com.jcwhatever.nucleus.internal.commands.scripts.ScriptsCommand;
import com.jcwhatever.nucleus.internal.commands.storage.StorageCommand;

public final class NucleusCommandDispatcher extends CommandDispatcher {

    public NucleusCommandDispatcher() {
        super(Nucleus.getPlugin());
    }

    @Override
    protected void registerCommands () {
        registerCommand(NEconomyCommand.class);
        registerCommand(NFriendsCommand.class);
        registerCommand(JailCommand.class);
        registerCommand(KitsCommand.class);
        registerCommand(PluginsCommand.class);
        registerCommand(ProvidersCommand.class);
        registerCommand(ScriptsCommand.class);
        registerCommand(StorageCommand.class);
    }
}
