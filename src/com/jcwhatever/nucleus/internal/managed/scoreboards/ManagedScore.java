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

package com.jcwhatever.nucleus.internal.managed.scoreboards;

import com.jcwhatever.nucleus.managed.scoreboards.IManagedScoreboard;
import com.jcwhatever.nucleus.managed.scoreboards.IObjective;
import com.jcwhatever.nucleus.managed.scoreboards.IScore;

import org.bukkit.scoreboard.Score;

/**
 * Managed scoreboard objective score.
 */
class ManagedScore implements IScore {

    private final IManagedScoreboard _scoreboard;
    private final IObjective _objective;
    private final Score _score;

    ManagedScore(IManagedScoreboard scoreboard, IObjective objective, Score score) {
        _scoreboard = scoreboard;
        _objective = objective;
        _score = score;
    }

    @Override
    public String getEntry() {
        return _score.getEntry();
    }

    @Override
    public IObjective getObjective() {
        return _objective;
    }

    @Override
    public int getScore() {
        return _score.getScore();
    }

    @Override
    public void setScore(int score) {
        _score.setScore(score);
    }

    @Override
    public int add(int amount) {
        int curr = _score.getScore();
        curr += amount;
        _score.setScore(curr);

        return curr;
    }

    @Override
    public boolean isScoreSet() {
        return _score.isScoreSet();
    }

    @Override
    public IManagedScoreboard getScoreboard() {
        return _scoreboard;
    }

    @Override
    public int hashCode() {
        return _score.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ManagedScore &&
                ((ManagedScore) obj)._score.equals(_score);
    }
}
