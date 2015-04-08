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

package com.jcwhatever.nucleus.managed.commands.mixins;

import com.jcwhatever.nucleus.managed.commands.ICommand;

import org.bukkit.command.CommandSender;

import java.util.Collection;

/**
 * A command mixin interface for a command that can handle tab completion.
 */
public interface ITabCompletable extends ICommand {

    /**
     * Invoked to get a list of possible tab complete values from the command based
     * on the current text.
     *
     * <p>Intended to be overridden by implementation if needed.</p>
     *
     * @param sender      The command sender.
     * @param arguments   This command arguments currently entered by the command sender.
     *                    not including the command and command path.
     * @param completions The list of completions.
     */
    void onTabComplete(CommandSender sender, String[] arguments, Collection<String> completions);
}
