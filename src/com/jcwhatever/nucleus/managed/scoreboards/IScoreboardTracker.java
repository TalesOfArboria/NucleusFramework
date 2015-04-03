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

import org.bukkit.scoreboard.Scoreboard;

import javax.annotation.Nullable;

/**
 * Interface for the global scoreboard tracker.
 *
 * <p>Tracks the scoreboards that players have viewed so they can be re-shown.</p>
 *
 * <p>Helps reduce plugin scoreboard conflicts by acting as the central
 * scoreboard manager. Each plugin can simply apply their scoreboards via
 * {@link IScoreboardTracker} to ensure that when a plugin is done showing a
 * scoreboard to a player, the previous scoreboard is re-shown.</p>
 *
 * @see IManagedScoreboard
 */
public interface IScoreboardTracker {

    /**
     * Create an {@link IManagedScoreboard} instance for a {@link Scoreboard} so
     * it can be tracked.
     *
     * @param scoreboard  The scoreboard to manage.
     * @param lifespan    The intended life span of the scoreboard.
     *
     * @return  The managed scoreboard.
     */
    IManagedScoreboard manage(Scoreboard scoreboard, ScoreboardLifespan lifespan);

    /**
     * Create an {@link IManagedScoreboard} instance for a {@link Scoreboard} so
     * it can be tracked.
     *
     * @param scoreboard  The scoreboard to manage.
     * @param lifespan    The intended life span of the scoreboard.
     * @param extension   The extension to use with the managed scoreboard.
     *
     * @return  The managed scoreboard.
     */
    IManagedScoreboard manage(Scoreboard scoreboard, ScoreboardLifespan lifespan,
                              @Nullable IScoreboardExtension extension);
}
