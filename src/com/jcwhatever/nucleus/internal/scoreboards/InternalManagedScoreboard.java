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

package com.jcwhatever.nucleus.internal.scoreboards;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.managed.scoreboards.IManagedScoreboard;
import com.jcwhatever.nucleus.managed.scoreboards.IScoreboardExtension;
import com.jcwhatever.nucleus.managed.scoreboards.ScoreboardLifespan;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Set;
import javax.annotation.Nullable;

/**
 * Internal implementation of {@link IManagedScoreboard}
 */
public class InternalManagedScoreboard implements IManagedScoreboard {

    private final InternalScoreboardTracker _tracker;
    private final ScoreboardLifespan _lifespan;
    private final IScoreboardExtension _extension;
    private final Scoreboard _scoreboard;
    private boolean _isDisposed;

    /**
     * Constructor.
     *
     * @param tracker     The parent tracker.
     * @param scoreboard  The scoreboard to manage.
     * @param lifespan    The lifespan type
     */
    public InternalManagedScoreboard(InternalScoreboardTracker tracker,
                                     Scoreboard scoreboard, ScoreboardLifespan lifespan,
                                     @Nullable IScoreboardExtension extension) {
        PreCon.notNull(tracker);
        PreCon.notNull(lifespan);
        PreCon.notNull(scoreboard);

        _tracker = tracker;
        _lifespan = lifespan;
        _scoreboard = scoreboard;
        _extension = extension;
    }

    @Override
    public ScoreboardLifespan getLifespan() {
        return _lifespan;
    }

    @Override
    public final Scoreboard getScoreboard() {
        return _scoreboard;
    }

    @Override
    public boolean apply(Player player) {
        PreCon.notNull(player);

        if (_tracker.apply(player, this)) {

            if (_extension != null)
                _extension.onApply(player, this);

            return true;
        }

        return false;
    }

    @Override
    public boolean remove(Player player) {
        PreCon.notNull(player);

        if (_tracker.remove(player, this)) {

            if (_extension != null)
                _extension.onRemove(player, this);

            return true;
        }
        return false;
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        Set<Objective> objectives = _scoreboard.getObjectives();
        for (Objective objective : objectives) {
            objective.unregister();
        }
        _isDisposed = true;
    }
}

