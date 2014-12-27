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

package com.jcwhatever.nucleus.titles;

import org.bukkit.entity.Player;

/**
 * Interface for an object that represents a
 * Minecraft title.
 */
public interface ITitle {

    /**
     * Get the time spent fading in.
     *
     * @return -1 if the default is used.
     */
    int getFadeInTime();

    /**
     * Get the time spent being displayed.
     *
     * @return -1 if the default is used.
     */
    int getStayTime();

    /**
     * Get the time spent fading out.
     *
     * @return  -1 if the default is used.
     */
    int getFadeOutTime();

    /**
     * Get the title components.
     */
    String getTitle();

    /**
     * Get the sub-title components.
     */
    String getSubTitle();

    /**
     * Show the title to the specified player.
     *
     * @param p  The player to show the title to.
     */
    void showTo(Player p);
}
