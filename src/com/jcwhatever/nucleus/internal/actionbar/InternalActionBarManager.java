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

package com.jcwhatever.nucleus.internal.actionbar;

import com.jcwhatever.nucleus.managed.actionbar.ActionBarPriority;
import com.jcwhatever.nucleus.managed.actionbar.IActionBar;
import com.jcwhatever.nucleus.managed.actionbar.IActionBarManager;
import com.jcwhatever.nucleus.managed.actionbar.IPersistentActionBar;
import com.jcwhatever.nucleus.managed.actionbar.ITimedActionBar;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.TimeScale;
import com.jcwhatever.nucleus.utils.text.dynamic.IDynamicText;

import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Internal implementation of {@link IActionBarManager}.
 */
public final class InternalActionBarManager implements IActionBarManager {

    @Override
    public IActionBar create(CharSequence text) {
        return new ActionBar(text);
    }

    @Override
    public IPersistentActionBar createPersistent(CharSequence text) {
        return new PersistentActionBar(text);
    }

    @Override
    public IPersistentActionBar createPersistent(CharSequence text, int minDuration, TimeScale timeScale) {
        return new PersistentActionBar(text, minDuration, timeScale);
    }

    @Override
    public IPersistentActionBar createPersistent(IDynamicText text) {
        return new PersistentActionBar(text);
    }

    @Override
    public IPersistentActionBar createPersistent(IDynamicText text, int minDuration, TimeScale timeScale) {
        return new PersistentActionBar(text, minDuration, timeScale);
    }

    @Override
    public ITimedActionBar createTimed(CharSequence text) {
        return new TimedActionBar(text);
    }

    @Override
    public ITimedActionBar createTimed(CharSequence text, int duration, TimeScale timeScale) {
        return new TimedActionBar(text, duration, timeScale);
    }

    @Override
    public ITimedActionBar createTimed(IDynamicText text) {
        return new TimedActionBar(text);
    }

    @Override
    public ITimedActionBar createTimed(IDynamicText text, int duration, TimeScale timeScale) {
        return new TimedActionBar(text, duration, timeScale);
    }

    @Override
    public void showTo(Player player, CharSequence text) {
        new ActionBar(text).showTo(player);
    }

    @Override
    public void showTo(Player player, CharSequence text, ActionBarPriority priority) {
        new ActionBar(text).showTo(player, priority);
    }

    @Override
    public void showTo(Player player, IDynamicText text) {
        new ActionBar(text).showTo(player);
    }

    @Override
    public void showTo(Player player, IDynamicText text, ActionBarPriority priority) {
        new ActionBar(text).showTo(player, priority);
    }

    @Override
    public void showTo(Collection<? extends Player> players, CharSequence text) {
        new ActionBar(text).showTo(players);
    }

    @Override
    public void showTo(Collection<? extends Player> players, CharSequence text, ActionBarPriority priority) {
        new ActionBar(text).showTo(players, priority);
    }

    @Override
    public void showTo(Collection<? extends Player> players, IDynamicText text) {
        new ActionBar(text).showTo(players);
    }

    @Override
    public void showTo(Collection<? extends Player> players, IDynamicText text, ActionBarPriority priority) {
        new ActionBar(text).showTo(players, priority);
    }

    @Override
    public void showTimed(Player player, CharSequence text, int duration, TimeScale timeScale) {
        new TimedActionBar(text, duration, timeScale).showTo(player);
    }

    @Override
    public void showTimed(Player player, CharSequence text,
                          int duration, TimeScale timeScale, ActionBarPriority priority) {
        new TimedActionBar(text, duration, timeScale).showTo(player, priority);
    }

    @Override
    public void showTimed(Player player, IDynamicText text, int duration, TimeScale timeScale) {
        new TimedActionBar(text, duration, timeScale).showTo(player);
    }

    @Override
    public void showTimed(Player player, IDynamicText text,
                          int duration, TimeScale timeScale, ActionBarPriority priority) {
        new TimedActionBar(text, duration, timeScale).showTo(player, priority);
    }

    @Override
    public void showTimed(Collection<? extends Player> players, CharSequence text,
                          int duration, TimeScale timeScale) {
        new TimedActionBar(text, duration, timeScale).showTo(players);
    }

    @Override
    public void showTimed(Collection<? extends Player> players, CharSequence text,
                          int duration, TimeScale timeScale, ActionBarPriority priority) {
        new TimedActionBar(text, duration, timeScale).showTo(players, priority);
    }

    @Override
    public void showTimed(Collection<? extends Player> players, IDynamicText text,
                          int duration, TimeScale timeScale) {
        new TimedActionBar(text, duration, timeScale).showTo(players);
    }

    @Override
    public void showTimed(Collection<? extends Player> players, IDynamicText text,
                          int duration, TimeScale timeScale, ActionBarPriority priority) {
        new TimedActionBar(text, duration, timeScale).showTo(players, priority);
    }

    @Override
    public void clearAll(Player player) {
        PreCon.notNull(player);

        BarSender.removePlayer(player);
    }

    @Override
    public void clearAll(Collection<? extends Player> players) {
        PreCon.notNull(players);

        for (Player player : players) {
            BarSender.removePlayer(player);
        }
    }
}
