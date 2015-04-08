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

package com.jcwhatever.nucleus.internal.managed.commands;

import com.jcwhatever.nucleus.internal.managed.commands.CommandCollection.ICommandContainerFactory;
import com.jcwhatever.nucleus.managed.commands.ICommand;
import com.jcwhatever.nucleus.managed.commands.ICommandDispatcher;
import com.jcwhatever.nucleus.managed.commands.ICommandManager;
import com.jcwhatever.nucleus.managed.commands.utils.ICommandUsageGenerator;

import org.bukkit.plugin.Plugin;

/**
 * Internal implementation of {@link ICommandManager}.
 */
public class InternalCommandManager implements ICommandManager {

    private static final ICommandContainerFactory COMMAND_FACTORY = new ICommandContainerFactory() {
        @Override
        public RegisteredCommand create(Plugin plugin, ICommand command) {
            return new RegisteredCommand(plugin, command, this);
        }
    };

    @Override
    public ICommandDispatcher createDispatcher(Plugin plugin) {
        return new CommandDispatcher(plugin, COMMAND_FACTORY);
    }

    @Override
    public ICommandUsageGenerator getUsageGenerator() {
        return new UsageGenerator();
    }

    @Override
    public ICommandUsageGenerator getUsageGenerator(String defaultTemplate) {
        return new UsageGenerator(defaultTemplate);
    }
}
