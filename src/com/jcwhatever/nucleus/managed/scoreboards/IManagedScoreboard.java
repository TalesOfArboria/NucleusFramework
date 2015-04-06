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

package com.jcwhatever.nucleus.managed.scoreboards;

import com.jcwhatever.nucleus.mixins.IDisposable;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Wraps a {@link org.bukkit.scoreboard.Scoreboard} so that it can be tracked
 * by an {@link IScoreboardTracker}.
 *
 * <p>Scoreboards applied to a player are tracked. If another scoreboard is applied
 * then removed, the previous scoreboard is applied. This does not apply to transient
 * scoreboards which are removed when another scoreboard is shown.</p>
 *
 * <p>When the scoreboard is no longer in use, invoke the {@link #dispose} method to
 * unregister objectives and flag the {@link IManagedScoreboard} as disposed.</p>
 *
 * @see IScoreboardTracker
 */
public interface IManagedScoreboard extends IDisposable {

    /**
     * Determine how the scoreboards lifespan is handled.
     */
    ScoreboardLifespan getLifespan();

    /**
     * Get the encapsulated scoreboard.
     */
    Scoreboard getScoreboard();

    /**
     * Apply the scoreboard to the specified player.
     *
     * <p>Use this method instead of directly setting the scoreboard on the player.</p>
     *
     * @param player  The player.
     *
     * @return  True if applied or transient, false if already applied.
     */
    boolean apply(Player player);

    /**
     * Remove the scoreboard from the player.
     *
     * <p>Removes the scoreboard from the players view and removes the last
     * occurrence from the tracked stack of scoreboards the player is viewing.
     * If the removed scoreboard is the one the player is currently viewing and if
     * there is a previous scoreboard in the stack, the previous scoreboard will be
     * applied to the player automatically.</p>
     *
     * <p>Use this method instead of directly setting the scoreboard on the player.</p>
     *
     * @param player  The player.
     *
     * @return  True if found and removed, otherwise false.
     */
    boolean remove(Player player);
}
