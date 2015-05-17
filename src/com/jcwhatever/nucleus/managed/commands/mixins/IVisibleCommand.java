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

/**
 * A command mixin interface for a command that can dynamically change its
 * visibility to players.
 *
 * <p>If a command implements this mixin, the {@link #isVisible} method
 * is invoked to determine if the command is visible to a command sender
 * and to determine if the player can execute the command if it is
 * executable.</p>
 *
 * <p>A command that is not visible to a player cannot be run.</p>
 *
 * <p>Note that if a player does not have permission for a command, the
 * player will never be able to see or execute the command and the
 * {@link #isVisible} method will never be invoked for said player.</p>
 */
public interface IVisibleCommand extends ICommand {

    /**
     * Determine if the command is visible and usable by the
     * specified command sender.
     *
     * @param sender  The command sender.
     */
    boolean isVisible(CommandSender sender);
}
