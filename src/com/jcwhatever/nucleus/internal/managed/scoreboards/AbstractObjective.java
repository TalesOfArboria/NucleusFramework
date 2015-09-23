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

import com.jcwhatever.nucleus.managed.scoreboards.IObjective;
import com.jcwhatever.nucleus.managed.scoreboards.IScoreboard;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

/**
 * Abstract scoreboard objective.
 */
public abstract class AbstractObjective implements IObjective {

    private final IScoreboard _scoreboard;
    private final Objective _objective;

    AbstractObjective(IScoreboard scoreboard, Objective objective) {
        _scoreboard = scoreboard;
        _objective = objective;
    }

    @Override
    public String getName() {
        return _objective.getName();
    }

    @Override
    public String getDisplayName() {
        return _objective.getDisplayName();
    }

    @Override
    public void setDisplayName(CharSequence displayName, Object... args) {
        _objective.setDisplayName(TextUtils.format(displayName, args).toString());
    }

    // for script compatibility
    public void setDisplayName(CharSequence displayName) {
        _objective.setDisplayName(TextUtils.format(displayName).toString());
    }

    @Override
    public String getCriteria() {
        return _objective.getCriteria();
    }

    @Override
    public boolean isModifiable() {
        return _objective.isModifiable();
    }

    @Override
    public IScoreboard getScoreboard() {
        return _scoreboard;
    }

    @Override
    public void unregister() {
        _objective.unregister();
    }

    @Override
    public void setDisplaySlot(DisplaySlot displaySlot) {
        PreCon.notNull(displaySlot);

        _objective.setDisplaySlot(displaySlot);
    }

    @Override
    public DisplaySlot getDisplaySlot() {
        return _objective.getDisplaySlot();
    }
}
