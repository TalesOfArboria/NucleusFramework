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

import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Interface for the global title manager.
 */
public interface ITitleManager {

    /**
     * Create a new title instance.
     *
     * @param title  The title text.
     */
    ITitle create(CharSequence title);

    /**
     * Create a new title instance.
     *
     * @param title     The title text.
     * @param subTitle  The sub title text.
     */
    ITitle create(CharSequence title, @Nullable CharSequence subTitle);

    /**
     * Create a new title instance.
     *
     * @param title        The title text.
     * @param fadeInTime   The fade-in time in ticks.
     * @param stayTime     The stay visible time in ticks.
     * @param fadeOutTime  The fade-out time in ticks.
     */
    ITitle create(CharSequence title, int fadeInTime, int stayTime, int fadeOutTime);

    /**
     * Create a new title instance.
     *
     * @param title        The title text.
     * @param subTitle     The sub title text.
     * @param fadeInTime   The fade-in time in ticks
     * @param stayTime     The stay visible time in ticks.
     * @param fadeOutTime  The fade-out time in ticks.
     */
    ITitle create(CharSequence title, @Nullable CharSequence subTitle,
                  int fadeInTime, int stayTime, int fadeOutTime);

    /**
     * Show a title to the specified player.
     *
     * @param player  The player to show the title to.
     * @param title   The title text.
     */
    void showTo(Player player, CharSequence title);

    /**
     * Show a title to the specified player.
     *
     * @param player    The player to show the title to.
     * @param title     The title text.
     * @param subTitle  The sub title text.
     */
    void showTo(Player player, CharSequence title, @Nullable CharSequence subTitle);

    /**
     * Show a title to the specified player.
     *
     * @param player       The player to show the title to.
     * @param title        The title text.
     * @param fadeInTime   The fade-in time in ticks.
     * @param stayTime     The stay visible time in ticks.
     * @param fadeOutTime  The fade-out time in ticks.
     */
    void showTo(Player player, CharSequence title, int fadeInTime, int stayTime, int fadeOutTime);

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
    void showTo(Player player, CharSequence title, @Nullable CharSequence subTitle,
                int fadeInTime, int stayTime, int fadeOutTime);

    /**
     * Show a title to a collection of players.
     *
     * @param players      The players to show the title to.
     * @param title        The title text.
     */
    void showTo(Collection<? extends Player> players, CharSequence title);

    /**
     * Show a title to a collection of players.
     *
     * @param players      The players to show the title to.
     * @param title        The title text.
     * @param subTitle     The sub title text.
     */
    void showTo(Collection<? extends Player> players, CharSequence title, @Nullable CharSequence subTitle);

    /**
     * Show a title to a collection of players.
     *
     * @param players      The players to show the title to.
     * @param title        The title text.
     * @param fadeInTime   The fade-in time in ticks.
     * @param stayTime     The stay visible time in ticks.
     * @param fadeOutTime  The fade-out time in ticks.
     */
    void showTo(Collection<? extends Player> players, CharSequence title,
                int fadeInTime, int stayTime, int fadeOutTime);

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
    void showTo(Collection<? extends Player> players, CharSequence title, @Nullable CharSequence subTitle,
                int fadeInTime, int stayTime, int fadeOutTime);
}
