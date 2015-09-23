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

package com.jcwhatever.nucleus.managed.titles;

import com.jcwhatever.nucleus.Nucleus;

import org.bukkit.entity.Player;

import java.util.Collection;
import javax.annotation.Nullable;

/**
 * Static convenience methods for accessing the global title manager.
 */
public final class Titles {

    private Titles() {}

    /**
     * Create a new title instance.
     *
     * @param title  The title text.
     */
    public static ITitle create(String title) {
        return manager().create(title);
    }

    /**
     * Create a new title instance.
     *
     * @param title     The title text.
     * @param subTitle  The sub title text.
     */
    public static ITitle create(String title, @Nullable String subTitle) {
        return manager().create(title, subTitle);
    }

    /**
     * Create a new title instance.
     *
     * @param title        The title text.
     * @param fadeInTime   The fade-in time in ticks.
     * @param stayTime     The stay visible time in ticks.
     * @param fadeOutTime  The fade-out time in ticks.
     */
    public static ITitle create(String title, int fadeInTime, int stayTime, int fadeOutTime) {
        return manager().create(title, fadeInTime, stayTime, fadeOutTime);
    }

    /**
     * Create a new title instance.
     *
     * @param title        The title text.
     * @param subTitle     The sub title text.
     * @param fadeInTime   The fade-in time in ticks
     * @param stayTime     The stay visible time in ticks.
     * @param fadeOutTime  The fade-out time in ticks.
     */
    public static ITitle create(CharSequence title, @Nullable CharSequence subTitle,
                  int fadeInTime, int stayTime, int fadeOutTime) {
        return manager().create(title, subTitle, fadeInTime, stayTime, fadeOutTime);
    }

    /**
     * Show a title to the specified player.
     *
     * @param player  The player to show the title to.
     * @param title   The title text.
     */
    public static void showTo(Player player, CharSequence title) {
        manager().showTo(player, title);
    }

    /**
     * Show a title to the specified player.
     *
     * @param player    The player to show the title to.
     * @param title     The title text.
     * @param subTitle  The sub title text.
     */
    public static void showTo(Player player, CharSequence title, @Nullable CharSequence subTitle) {
        manager().showTo(player, title, subTitle);
    }

    /**
     * Show a title to the specified player.
     *
     * @param player       The player to show the title to.
     * @param title        The title text.
     * @param fadeInTime   The fade-in time in ticks.
     * @param stayTime     The stay visible time in ticks.
     * @param fadeOutTime  The fade-out time in ticks.
     */
    public static void showTo(Player player, CharSequence title,
                              int fadeInTime, int stayTime, int fadeOutTime) {
        manager().showTo(player, title, fadeInTime, stayTime, fadeOutTime);
    }

    /**
     * Show a title to the specified player.
     *
     * @param player       The player to show the title to.
     * @param title        The title text.
     * @param subTitle     The sub title text.
     * @param fadeInTime   The fade-in time in ticks.
     * @param stayTime     The stay visible time in ticks.
     * @param fadeOutTime  The fade-out time in ticks.
     */
    public static void showTo(Player player, CharSequence title, @Nullable CharSequence subTitle,
                int fadeInTime, int stayTime, int fadeOutTime) {
        manager().showTo(player, title, subTitle, fadeInTime, stayTime, fadeOutTime);
    }

    /**
     * Show a title to a collection of players.
     *
     * @param players      The players to show the title to.
     * @param title        The title text.
     */
    public static void showTo(Collection<? extends Player> players, CharSequence title) {
        manager().showTo(players, title);
    }

    /**
     * Show a title to a collection of players.
     *
     * @param players      The players to show the title to.
     * @param title        The title text.
     * @param subTitle     The sub title text.
     */
    public static void showTo(Collection<? extends Player> players,
                              CharSequence title, @Nullable CharSequence subTitle) {
        manager().showTo(players, title, subTitle);
    }

    /**
     * Show a title to a collection of players.
     *
     * @param players      The players to show the title to.
     * @param title        The title text.
     * @param fadeInTime   The fade-in time in ticks.
     * @param stayTime     The stay visible time in ticks.
     * @param fadeOutTime  The fade-out time in ticks.
     */
    public static void showTo(Collection<? extends Player> players, CharSequence title,
                int fadeInTime, int stayTime, int fadeOutTime) {
        manager().showTo(players, title, fadeInTime, stayTime, fadeOutTime);
    }

    /**
     * Show a title to a collection of players.
     *
     * @param players      The players to show the title to.
     * @param title        The title text.
     * @param subTitle     The sub title text.
     * @param fadeInTime   The fade-in time in ticks.
     * @param stayTime     The stay visible time in ticks.
     * @param fadeOutTime  The fade-out time in ticks.
     */
    public static void showTo(Collection<? extends Player> players,
                              CharSequence title, @Nullable CharSequence subTitle,
                              int fadeInTime, int stayTime, int fadeOutTime) {
        manager().showTo(players, title, subTitle, fadeInTime, stayTime, fadeOutTime);
    }

    private static ITitleManager manager() {
        return Nucleus.getTitleManager();
    }
}
