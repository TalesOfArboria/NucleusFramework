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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.utils.TimeScale;
import com.jcwhatever.nucleus.utils.text.dynamic.IDynamicText;

import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Static convenience methods for accessing the global action bar manager.
 */
public final class ActionBars {

    private ActionBars() {}

    /**
     * Create a new {@link IActionBar}.
     *
     * @param text  The text the action bar displays.
     */
    public static IActionBar create(String text) {
        return manager().create(text);
    }

    /**
     * Create a new {@link IPersistentActionBar}.
     *
     * @param text  The text the action bar displays.
     */
    public static IPersistentActionBar createPersistent(String text) {
        return manager().createPersistent(text);
    }

    /**
     * Create a new {@link IPersistentActionBar}.
     *
     * @param text         The text the action bar displays.
     * @param minDuration  The minimum display duration when displayed with other persistent action bars.
     * @param timeScale    The time scale of the specified duration.
     */
    public static IPersistentActionBar createPersistent(String text, int minDuration, TimeScale timeScale) {
        return manager().createPersistent(text, minDuration, timeScale);
    }

    /**
     * Create a new {@link IPersistentActionBar}.
     *
     * @param text  The text the action bar displays.
     */
    public static IPersistentActionBar createPersistent(IDynamicText text) {
        return manager().createPersistent(text);
    }

    /**
     * Create a new {@link IPersistentActionBar}.
     *
     * @param text         The text the action bar displays.
     * @param minDuration  The minimum display duration when displayed with other persistent action bars.
     * @param timeScale    The time scale of the specified duration.
     */
    public static IPersistentActionBar createPersistent(IDynamicText text, int minDuration, TimeScale timeScale) {
        return manager().createPersistent(text, minDuration, timeScale);
    }

    /**
     * Create a new {@link ITimedActionBar}.
     *
     * @param text  The text the action bar displays.
     */
    public static ITimedActionBar createTimed(String text) {
        return manager().createTimed(text);
    }

    /**
     * Create a new {@link ITimedActionBar}.
     *
     * @param text       The text the action bar displays.
     * @param duration   The duration to display the action bar for.
     * @param timeScale  The time scale of the specified duration.
     */
    public static ITimedActionBar createTimed(String text, int duration, TimeScale timeScale) {
        return manager().createTimed(text, duration, timeScale);
    }

    /**
     * Create a new {@link ITimedActionBar}.
     *
     * @param text  The text the action bar displays.
     */
    public static ITimedActionBar createTimed(IDynamicText text) {
        return manager().createTimed(text);
    }

    /**
     * Create a new {@link ITimedActionBar}.
     *
     * @param text       The text the action bar displays.
     * @param duration   The duration to display the action bar for.
     * @param timeScale  The time scale of the specified duration.
     */
    public static ITimedActionBar createTimed(IDynamicText text, int duration, TimeScale timeScale) {
        return manager().createTimed(text, duration, timeScale);
    }

    /**
     * Show an {@link IActionBar} to a player.
     *
     * <p>Displays using {@link ActionBarPriority#DEFAULT}.</p>
     *
     * @param player  The player to show the action bar to.
     * @param text    The text the action bar displays.
     */
    public static void showTo(Player player, String text) {
        manager().showTo(player, text);
    }

    /**
     * Show an {@link IActionBar} to a player.
     *
     * @param player    The player to show the action bar to.
     * @param text      The text the action bar displays.
     * @param priority  The action bar priority.
     */
    public static void showTo(Player player, String text, ActionBarPriority priority) {
        manager().showTo(player, text, priority);
    }

    /**
     * Show an {@link IActionBar} to a player.
     *
     * <p>Displays using {@link ActionBarPriority#DEFAULT}.</p>
     *
     * @param player  The player to show the action bar to.
     * @param text    The text the action bar displays.
     */
    public static void showTo(Player player, IDynamicText text) {
        manager().showTo(player, text);
    }

    /**
     * Show an {@link IActionBar} to a player.
     *
     * @param player    The player to show the action bar to.
     * @param text      The text the action bar displays.
     * @param priority  The action bar priority.
     */
    public static void showTo(Player player, IDynamicText text, ActionBarPriority priority) {
        manager().showTo(player, text, priority);
    }

    /**
     * Show an {@link IActionBar} to a collection of players.
     *
     * <p>Displays using {@link ActionBarPriority#DEFAULT}.</p>
     *
     * @param players  The players to show the action bar to.
     * @param text     The text the action bar displays.
     */
    public static void showTo(Collection<? extends Player> players, String text) {
        manager().showTo(players, text);
    }

    /**
     * Show an {@link IActionBar} to a collection of players.
     *
     * @param players   The players to show the action bar to.
     * @param text      The text the action bar displays.
     * @param priority  The action bar priority.
     */
    public static void showTo(Collection<? extends Player> players, String text, ActionBarPriority priority) {
        manager().showTo(players, text, priority);
    }

    /**
     * Show an {@link IActionBar} to a collection of players.
     *
     * <p>Displays using {@link ActionBarPriority#DEFAULT}.</p>
     *
     * @param players  The players to show the action bar to.
     * @param text    The text the action bar displays.
     */
    public static void showTo(Collection<? extends Player> players, IDynamicText text) {
        manager().showTo(players, text);
    }

    /**
     * Show an {@link IActionBar} to a collection of players.
     *
     * @param players   The players to show the action bar to.
     * @param text      The text the action bar displays.
     * @param priority  The action bar priority.
     */
    public static void showTo(Collection<? extends Player> players, IDynamicText text, ActionBarPriority priority) {
        manager().showTo(players, text, priority);
    }

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
    public static void showTimed(Player player, String text, int duration, TimeScale timeScale) {
        manager().showTimed(player, text, duration, timeScale);
    }

    /**
     * Show an {@link ITimedActionBar} to a player.
     *
     * @param player     The player to show the action bar to.
     * @param text       The text the action bar displays.
     * @param duration   The duration to display the action bar for.
     * @param timeScale  The time scale of the specified duration.
     * @param priority   The action bar priority.
     */
    public static void showTimed(Player player, String text,
                                 int duration, TimeScale timeScale, ActionBarPriority priority) {
        manager().showTimed(player, text, duration, timeScale, priority);
    }

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
    public static void showTimed(Player player, IDynamicText text, int duration, TimeScale timeScale) {
        manager().showTimed(player, text, duration, timeScale);
    }

    /**
     * Show an {@link ITimedActionBar} to a player.
     *
     * @param player     The player to show the action bar to.
     * @param text       The text the action bar displays.
     * @param duration   The duration to display the action bar for.
     * @param timeScale  The time scale of the specified duration.
     * @param priority   The action bar priority.
     */
    public static void showTimed(Player player, IDynamicText text,
                                 int duration, TimeScale timeScale, ActionBarPriority priority) {
        manager().showTimed(player, text, duration, timeScale, priority);
    }

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
    public static void showTimed(Collection<? extends Player> players,
                                 String text, int duration, TimeScale timeScale) {
        manager().showTimed(players, text, duration, timeScale);
    }

    /**
     * Show an {@link ITimedActionBar} to a collection of players.
     *
     * @param players    The players to show the action bar to.
     * @param text       The text the action bar displays.
     * @param duration   The duration to display the action bar for.
     * @param timeScale  The time scale of the specified duration.
     * @param priority   The action bar priority.
     */
    public static void showTimed(Collection<? extends Player> players, String text,
                                 int duration, TimeScale timeScale, ActionBarPriority priority) {
        manager().showTimed(players, text, duration, timeScale, priority);
    }

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
    public static void showTimed(Collection<? extends Player> players,
                                 IDynamicText text, int duration, TimeScale timeScale) {
        manager().showTimed(players, text, duration, timeScale);
    }

    /**
     * Show an {@link ITimedActionBar} to a collection of players.
     *
     * @param players    The players to show the action bar to.
     * @param text       The text the action bar displays.
     * @param duration   The duration to display the action bar for.
     * @param timeScale  The time scale of the specified duration.
     * @param priority   The action bar priority.
     */
    public static void showTimed(Collection<? extends Player> players, IDynamicText text,
                                 int duration, TimeScale timeScale, ActionBarPriority priority) {
        manager().showTimed(players, text, duration, timeScale, priority);
    }

    /**
     * Clear all persistent and timed action bars being displayed to a player.
     *
     * @param player  The player.
     */
    public static void clearAll(Player player) {
        manager().clearAll(player);
    }

    /**
     * Clear all persistent and timed action bars being displayed to a collection
     * of players.
     *
     * @param players  The players.
     */
    public static void clearAll(Collection<? extends Player> players) {
        manager().clearAll(players);
    }

    private static IActionBarManager manager() {
        return Nucleus.getActionBarManager();
    }
}
