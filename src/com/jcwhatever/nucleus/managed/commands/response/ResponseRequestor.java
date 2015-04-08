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

package com.jcwhatever.nucleus.managed.commands.response;

import com.jcwhatever.nucleus.Nucleus;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

/**
 * Static convenience methods for accessing the command response requestor.
 */
public final class ResponseRequestor {

    private ResponseRequestor() {}

    /**
     * Get a new {@link IRequestContextBuilder} used to create a new instance
     * of a {@link IRequestContext}.
     *
     * @param  plugin  The requesting plugin.
     */
    public static IRequestContextBuilder contextBuilder(Plugin plugin) {
        return Nucleus.getResponseRequestor().getContextBuilder(plugin);
    }

    /**
     * Get all response requests that are currently pending a response from
     * the specified command sender.
     *
     * @param sender  The command sender.
     */
    public static Collection<IRequestContext> getRequests(CommandSender sender) {
        return Nucleus.getResponseRequestor().getRequests(sender);
    }
}
