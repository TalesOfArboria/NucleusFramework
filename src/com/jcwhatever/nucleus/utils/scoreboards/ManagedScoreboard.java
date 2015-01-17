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


package com.jcwhatever.nucleus.utils.scoreboards;

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Set;

/**
 * Base implementation of a scoreboard container.
 *
 * <p>
 *     Scoreboards applied to a player are tracked. If another scoreboard is
 *     applied then removed, the previous scoreboard is applied.
 * </p>
 */
public class ManagedScoreboard implements IManagedScoreboard, IDisposable {

    private final ScoreboardLifespan _lifespan;
    private final Scoreboard _scoreboard;
    private boolean _isDisposed;

    /**
     * Constructor.
     *
     * @param lifespan    The lifespan type
     * @param scoreboard  The scoreboard to manage.
     */
    public ManagedScoreboard(ScoreboardLifespan lifespan, Scoreboard scoreboard) {
        PreCon.notNull(lifespan);
        PreCon.notNull(scoreboard);

        _lifespan = lifespan;
        _scoreboard = scoreboard;
    }

    /**
     * Get the scoreboard type.
     */
    @Override
    public ScoreboardLifespan getLifespan() {
        return _lifespan;
    }

    /**
     * Get the encapsulated scoreboard.
     */
    @Override
    public final Scoreboard getScoreboard() {
        return _scoreboard;
    }

    /**
     * Apply the scoreboard to the specified player.
     *
     * @param p  The player.
     */
    public void apply(Player p) {
        PreCon.notNull(p);

        ScoreboardTracker.apply(p, this);
    }

    /**
     * Remove the scoreboard from the player.
     *
     * @param p  The player.
     */
    public void remove(Player p) {
        PreCon.notNull(p);

        ScoreboardTracker.remove(p, this);
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    /**
     * Dispose the scoreboard.
     */
    @Override
    public void dispose() {
        Set<Objective> objectives = _scoreboard.getObjectives();
        for (Objective objective : objectives) {
            objective.unregister();
        }

        _isDisposed = true;
    }
}
