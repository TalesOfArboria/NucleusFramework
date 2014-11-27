/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

package com.jcwhatever.bukkit.generic.mixins;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * Mixin defines an implementation that determines
 * which players can see whatever object it represents
 * in the world.
 */
public interface IViewable {

    /**
     * Get the view mode.
     */
    ViewPolicy getViewPolicy();

    /**
     * Set the view mode.
     *
     * @param viewMode  The view mode.
     */
    void setViewMode(ViewPolicy viewMode);

    /**
     * Determine if the player can see the
     * object.
     *
     * @param player The player to check.
     */
    boolean canSee(Player player);

    /**
     * Determine if the specified player
     * is in the collection of viewers.
     *
     * @param player  The player to check.
     */
    boolean hasViewer(Player player);

    /**
     * Add a player to the collection
     * of viewers.
     *
     * @param player  The player to add.
     *
     * @return True if the player was added.
     */
    boolean addViewer(Player player);

    /**
     * Remove a player from the collection
     * of viewers.
     *
     * @param player  The player to remove.
     *
     * @return True if the player was removed.
     */
    boolean removeViewer(Player player);

    /**
     * Clear all players from the collection of
     * viewers.
     */
    void clearViewers();

    /**
     * Get all players in the view collection.
     */
    List<Player> getViewers();

    /**
     * Define how the viewer collection is treated.
     */
    public enum ViewPolicy {
        /**
         * All viewers in the collection cannot
         * see the object in the world.
         */
        BLACKLIST,

        /**
         * Only viewers in the collection can
         * see the object in the world.
         */
        WHITELIST
    }
}
