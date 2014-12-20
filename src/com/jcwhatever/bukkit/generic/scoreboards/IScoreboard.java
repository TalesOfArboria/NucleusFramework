/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.scoreboards;

import com.jcwhatever.bukkit.generic.mixins.IDisposable;
import com.jcwhatever.bukkit.generic.mixins.IPluginOwned;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Represents a scoreboard wrapper.
 */
public interface IScoreboard extends IPluginOwned, IDisposable {

    /**
     * Get the scoreboards owning plugin.
     */
    @Override
    Plugin getPlugin();

    /**
     * Get the scoreboard type name.
     */
    String getType();

    /**
     * Get the encapsulated Bukkit scoreboard.
     */
    Scoreboard getScoreboard();

    /**
     * Apply the scoreboard to the specified player.
     *
     * @param p  The player.
     */
    void apply(Player p);

    /**
     * Remove the scoreboard from the specified player.
     *
     * @param p  The player.
     */
    void remove(Player p);

    /**
     * Dispose the scoreboard.
     */
    @Override
    void dispose();
}
