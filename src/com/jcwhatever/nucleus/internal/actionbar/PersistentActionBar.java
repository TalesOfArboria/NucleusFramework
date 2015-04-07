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
import com.jcwhatever.nucleus.managed.actionbar.IPersistentActionBar;
import com.jcwhatever.nucleus.utils.TimeScale;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.dynamic.DynamicTextBuilder;
import com.jcwhatever.nucleus.utils.text.dynamic.IDynamicText;

import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * An action bar that persists on the players screen until the
 * {@link #hide} method is invoked.
 */
class PersistentActionBar extends ActionBar implements IPersistentActionBar {

    /**
     * Determine if the player is currently viewing a persistent action bar.
     *
     * @param player  The player to check.
     */
    public static boolean isViewing(Player player) {
        return BarSender.isViewing(player);
    }

    private final int _defaultDuration;
    private final TimeScale _defaultTimeScale;

    /**
     * Constructor.
     *
     * <p>The default time slice duration for when a player is shown more
     * than 1 {@link PersistentActionBar} is 3 seconds.</p>
     *
     * @param text  The action bar text.
     */
    public PersistentActionBar(String text) {
        this(new DynamicTextBuilder().append(text).build(), 4, TimeScale.SECONDS);
    }

    /**
     * Constructor.
     *
     * <p>The default time slice duration for when a player is shown more
     * than 1 {@link PersistentActionBar} is 4 seconds.</p>
     *
     * @param dynamicText  The action bar dynamic text.
     */
    public PersistentActionBar(IDynamicText dynamicText) {
        this(dynamicText, 4, TimeScale.SECONDS);
    }

    /**
     * Constructor.
     *
     * @param text         The action bar text.
     * @param minDuration  The default time slice duration used when a player is shown
     *                     more than 1 {@link PersistentActionBar}.
     * @param timeScale    The default duration time scale.
     */
    public PersistentActionBar(String text, int minDuration, TimeScale timeScale) {
        this(new DynamicTextBuilder().append(text).build(), minDuration, timeScale);
    }

    /**
     * Constructor.
     *
     * @param dynamicText  The action bar dynamic text.
     * @param minDuration  The default time slice duration used when a player is shown
     *                     more than 1 {@link PersistentActionBar}.
     * @param timeScale    The default duration time scale.
     */
    public PersistentActionBar(IDynamicText dynamicText, int minDuration, TimeScale timeScale) {
        super(dynamicText);

        _defaultDuration = minDuration;
        _defaultTimeScale = timeScale;
    }

    /**
     * Get the default duration's {@link TimeScale}.
     */
    public TimeScale getTimeScale() {
        return _defaultTimeScale;
    }

    @Override
    public void showTo(Player player) {
        showTo(player, _defaultDuration, _defaultTimeScale);
    }

    @Override
    public void showTo(Player player, ActionBarPriority priority) {
        showTo(player, _defaultDuration, _defaultTimeScale, priority);
    }

    @Override
    public int getMinDuration() {
        return _defaultDuration * _defaultTimeScale.getTimeFactor();
    }

    @Override
    public void showTo(Player player, int minDuration, TimeScale timeScale) {
        showTo(player, minDuration, timeScale, ActionBarPriority.DEFAULT);
    }

    @Override
    public void showTo(Player player, int minDuration, TimeScale timeScale, ActionBarPriority priority) {
        PreCon.notNull(player);
        PreCon.greaterThanZero(minDuration);
        PreCon.notNull(timeScale);

        BarSender.addBar(player, this, minDuration, timeScale, priority);
    }

    @Override
    public void showTo(Collection<? extends Player> players, int minDuration, TimeScale timeScale) {
        showTo(players, minDuration, timeScale, ActionBarPriority.DEFAULT);
    }

    @Override
    public void showTo(Collection<? extends Player> players,
                       int minDuration, TimeScale timeScale, ActionBarPriority priority) {
        PreCon.notNull(players);
        PreCon.greaterThanZero(minDuration);
        PreCon.notNull(timeScale);

        for (Player player : players) {
            showTo(player, minDuration, timeScale, priority);
        }
    }

    @Override
    public void hide(Player player) {
        PreCon.notNull(player);

        BarSender.removeBar(player, this);
    }

    @Override
    public void hide(Collection<? extends Player> players) {
        PreCon.notNull(players);

        for (Player player : players) {
            hide(player);
        }
    }

    /**
     * Hide the {@link PersistentActionBar} from all players.
     */
    @Override
    public void hideAll() {
        BarSender.removeBar(this);
    }
}
