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

package com.jcwhatever.nucleus.messaging;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Generates {@link IMessenger} implementation instances.
 */
public interface IMessengerFactory {

    /**
     * Gets or creates a singleton instance of a messenger
     * for the specified plugin.
     *
     * @param plugin  The plugin.
     */
    IMessenger get(Plugin plugin);

    /**
     * Get a singleton messenger that has no chat prefix.
     */
    IMessenger getAnon(Plugin plugin);

    /**
     * Create a new messenger instance.
     *
     * @param plugin  The owning plugin.
     */
    IMessenger create(Plugin plugin);

    /**
     * Create a new messenger instance.
     *
     * @param plugin        The owning plugin.
     * @param prefixObject  The object to create a prefix from.
     */
    IMessenger create(Plugin plugin, @Nullable Object prefixObject);

    /**
     * Display stored important messages for the specified player to the
     * specified player.
     *
     * @param player         The player.
     * @param clearMessages  True to clear messages after displaying, otherwise false.
     */
    void tellImportant(Player player, boolean clearMessages);
}
