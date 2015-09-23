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
import com.jcwhatever.nucleus.utils.text.dynamic.IDynamicText;

import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Interface for the global action bar manager.
 */
public interface IActionBarManager {

    /**
     * Create a new {@link IActionBar}.
     *
     * @param text  The text the action bar displays.
     */
    IActionBar create(CharSequence text);

    /**
     * Create a new {@link IPersistentActionBar}.
     *
     * @param text  The text the action bar displays.
     */
    IPersistentActionBar createPersistent(CharSequence text);

    /**
     * Create a new {@link IPersistentActionBar}.
     *
     * @param text         The text the action bar displays.
     * @param minDuration  The minimum display duration when displayed with other persistent action bars.
     * @param timeScale    The time scale of the specified duration.
     */
    IPersistentActionBar createPersistent(CharSequence text, int minDuration, TimeScale timeScale);

    /**
     * Create a new {@link IPersistentActionBar}.
     *
     * @param text  The text the action bar displays.
     */
    IPersistentActionBar createPersistent(IDynamicText text);

    /**
     * Create a new {@link IPersistentActionBar}.
     *
     * @param text         The text the action bar displays.
     * @param minDuration  The minimum display duration when displayed with other persistent action bars.
     * @param timeScale    The time scale of the specified duration.
     */
    IPersistentActionBar createPersistent(IDynamicText text, int minDuration, TimeScale timeScale);

    /**
     * Create a new {@link ITimedActionBar}.
     *
     * @param text  The text the action bar displays.
     */
    ITimedActionBar createTimed(CharSequence text);

    /**
     * Create a new {@link ITimedActionBar}.
     *
     * @param text       The text the action bar displays.
     * @param duration   The duration to display the action bar for.
     * @param timeScale  The time scale of the specified duration.
     */
    ITimedActionBar createTimed(CharSequence text, int duration, TimeScale timeScale);

    /**
     * Create a new {@link ITimedActionBar}.
     *
     * @param text  The text the action bar displays.
     */
    ITimedActionBar createTimed(IDynamicText text);

    /**
     * Create a new {@link ITimedActionBar}.
     *
     * @param text       The text the action bar displays.
     * @param duration   The duration to display the action bar for.
     * @param timeScale  The time scale of the specified duration.
     */
    ITimedActionBar createTimed(IDynamicText text, int duration, TimeScale timeScale);

    /**
     * Show an {@link IActionBar} to a player.
     *
     * <p>Displays using {@link ActionBarPriority#DEFAULT}.</p>
     *
     * @param player  The player to show the action bar to.
     * @param text    The text the action bar displays.
     */
    void showTo(Player player, CharSequence text);

    /**
     * Show an {@link IActionBar} to a player.
     *
     * @param player    The player to show the action bar to.
     * @param text      The text the action bar displays.
     * @param priority  The action bar priority.
     */
    void showTo(Player player, CharSequence text, ActionBarPriority priority);

    /**
     * Show an {@link IActionBar} to a player.
     *
     * <p>Displays using {@link ActionBarPriority#DEFAULT}.</p>
     *
     * @param player  The player to show the action bar to.
     * @param text    The text the action bar displays.
     */
    void showTo(Player player, IDynamicText text);

    /**
     * Show an {@link IActionBar} to a player.
     *
     * @param player    The player to show the action bar to.
     * @param text      The text the action bar displays.
     * @param priority  The action bar priority.
     */
    void showTo(Player player, IDynamicText text, ActionBarPriority priority);

    /**
     * Show an {@link IActionBar} to a collection of players.
     *
     * <p>Displays using {@link ActionBarPriority#DEFAULT}.</p>
     *
     * @param players  The players to show the action bar to.
     * @param text     The text the action bar displays.
     */
    void showTo(Collection<? extends Player> players, CharSequence text);

    /**
     * Show an {@link IActionBar} to a collection of players.
     *
     * @param players   The players to show the action bar to.
     * @param text      The text the action bar displays.
     * @param priority  The action bar priority.
     */
    void showTo(Collection<? extends Player> players, CharSequence text, ActionBarPriority priority);

    /**
     * Show an {@link IActionBar} to a collection of players.
     *
     * <p>Displays using {@link ActionBarPriority#DEFAULT}.</p>
     *
     * @param players  The players to show the action bar to.
     * @param text    The text the action bar displays.
     */
    void showTo(Collection<? extends Player> players, IDynamicText text);

    /**
     * Show an {@link IActionBar} to a collection of players.
     *
     * @param players   The players to show the action bar to.
     * @param text      The text the action bar displays.
     * @param priority  The action bar priority.
     */
    void showTo(Collection<? extends Player> players, IDynamicText text, ActionBarPriority priority);

    /**
     * Show an {@link ITimedActionBar} to a player.
     *
     * <p>Displays using {@link ActionBarPriority#DEFAULT}.</p>
     *
     * @param player     The player to show the action bar to.
     * @param text       The text the action bar displays.
     * @param duration   The duration to display the action bar for.
     * @param timeScale  The time scale of the specified duration.
     */
    void showTimed(Player player, CharSequence text, int duration, TimeScale timeScale);

    /**
     * Show an {@link ITimedActionBar} to a player.
     *
     * @param player     The player to show the action bar to.
     * @param text       The text the action bar displays.
     * @param duration   The duration to display the action bar for.
     * @param timeScale  The time scale of the specified duration.
     * @param priority   The action bar priority.
     */
    void showTimed(Player player, CharSequence text,
                   int duration, TimeScale timeScale, ActionBarPriority priority);

    /**
     * Show an {@link ITimedActionBar} to a player.
     *
     * <p>Displays using {@link ActionBarPriority#DEFAULT}.</p>
     *
     * @param player     The player to show the action bar to.
     * @param text       The text the action bar displays.
     * @param duration   The duration to display the action bar for.
     * @param timeScale  The time scale of the specified duration.
     */
    void showTimed(Player player, IDynamicText text, int duration, TimeScale timeScale);

    /**
     * Show an {@link ITimedActionBar} to a player.
     *
     * @param player     The player to show the action bar to.
     * @param text       The text the action bar displays.
     * @param duration   The duration to display the action bar for.
     * @param timeScale  The time scale of the specified duration.
     * @param priority   The action bar priority.
     */
    void showTimed(Player player, IDynamicText text,
                   int duration, TimeScale timeScale, ActionBarPriority priority);

    /**
     * Show an {@link ITimedActionBar} to a collection of players.
     *
     * <p>Displays using {@link ActionBarPriority#DEFAULT}.</p>
     *
     * @param players    The players to show the action bar to.
     * @param text       The text the action bar displays.
     * @param duration   The duration to display the action bar for.
     * @param timeScale  The time scale of the specified duration.
     */
    void showTimed(Collection<? extends Player> players, CharSequence text,
                   int duration, TimeScale timeScale);

    /**
     * Show an {@link ITimedActionBar} to a collection of players.
     *
     * @param players    The players to show the action bar to.
     * @param text       The text the action bar displays.
     * @param duration   The duration to display the action bar for.
     * @param timeScale  The time scale of the specified duration.
     * @param priority   The action bar priority.
     */
    void showTimed(Collection<? extends Player> players, CharSequence text,
                   int duration, TimeScale timeScale, ActionBarPriority priority);

    /**
     * Show an {@link ITimedActionBar} to a collection of players.
     *
     * <p>Displays using {@link ActionBarPriority#DEFAULT}.</p>
     *
     * @param players    The players to show the action bar to.
     * @param text       The text the action bar displays.
     * @param duration   The duration to display the action bar for.
     * @param timeScale  The time scale of the specified duration.
     */
    void showTimed(Collection<? extends Player> players, IDynamicText text,
                   int duration, TimeScale timeScale);

    /**
     * Show an {@link ITimedActionBar} to a collection of players.
     *
     * @param players    The players to show the action bar to.
     * @param text       The text the action bar displays.
     * @param duration   The duration to display the action bar for.
     * @param timeScale  The time scale of the specified duration.
     * @param priority   The action bar priority.
     */
    void showTimed(Collection<? extends Player> players, IDynamicText text,
                   int duration, TimeScale timeScale, ActionBarPriority priority);

    /**
     * Clear all persistent and timed action bars being displayed to a player.
     *
     * @param player  The player.
     */
    void clearAll(Player player);

    /**
     * Clear all persistent and timed action bars being displayed to a collection
     * of players.
     *
     * @param players  The players.
     */
    void clearAll(Collection<? extends Player> players);
}
