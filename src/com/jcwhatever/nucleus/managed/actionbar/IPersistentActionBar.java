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
 * An {@link IActionBar} that persists on the players screen until the {@link #hide} method is invoked.
 */
public interface IPersistentActionBar extends IActionBar {

    /**
     * Get the default minimum time slice duration in ticks used when a player is shown more than 1
     * {@link IPersistentActionBar}.
     *
     * <p>This value is used when a value is not provided.</p>
     */
    int getMinDuration();

    /**
     * Show the action bar to a player.
     *
     * @param player       The player to show the action bar to.
     * @param minDuration  The min duration the player should see the bar if the player
     *                     is viewing more than 1 {@link IPersistentActionBar}.
     * @param timeScale    The time scale of the specified duration.
     */
    void showTo(Player player, int minDuration, TimeScale timeScale);

    /**
     * Show the action bar to a player.
     *
     * @param player       The player to show the action bar to.
     * @param minDuration  The min duration the player should see the bar if the player
     *                     is viewing more than 1 {@link IPersistentActionBar}.
     * @param timeScale    The time scale of the specified duration.
     */
    void showTo(Collection<? extends Player> player, int minDuration, TimeScale timeScale);

    /**
     * Hide the action bar from the player.
     *
     * @param player  The player to remove the action bar from.
     */
    void hide(Player player);

    /**
     * Hide the action bar from the player.
     *
     * @param players  The players to remove the action bar from.
     */
    void hide(Collection<? extends Player> players);

    /**
     * Hide the action bar from all players.
     */
    void hideAll();
}
