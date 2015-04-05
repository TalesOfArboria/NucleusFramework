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

package com.jcwhatever.nucleus.managed.actionbar;

import com.jcwhatever.nucleus.utils.TimeScale;

import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * A {@link IPersistentActionBar} that is automatically removed when the specified
 * duration ends.
 */
public interface ITimedActionBar extends IPersistentActionBar {

    /**
     * Get default amount of time in ticks the action bar is displayed for.
     * {@link IPersistentActionBar}.
     *
     * <p>This value is used when a value is not provided.</p>
     */
    int getDuration();

    /**
     * Show the action bar to a player.
     *
     * @param player     The player to show the action bar to.
     * @param duration   The duration the player should see the bar for.
     * @param timeScale  The time scale of the specified duration.
     */
    @Override
    void showTo(Player player, int duration, TimeScale timeScale);

    /**
     * Show the action bar to a player.
     *
     * @param player     The player to show the action bar to.
     * @param duration   The duration the player should see the bar for.
     * @param timeScale  The time scale of the specified duration.
     */
    @Override
    void showTo(Collection<? extends Player> player, int duration, TimeScale timeScale);
}
