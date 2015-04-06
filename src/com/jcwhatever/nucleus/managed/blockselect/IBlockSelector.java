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

package com.jcwhatever.nucleus.managed.blockselect;

import org.bukkit.entity.Player;

/**
 * Interface for the global player block selector.
 */
public interface IBlockSelector {

    /**
     * Specifies the next action after the player selects a block.
     */
    enum BlockSelectResult {
        /**
         * The block selection is finished.
         */
        FINISHED,
        /**
         * Continue query for another block selection.
         */
        CONTINUE
    }

    /**
     * Determine if a player is currently being queried to select a block.
     *
     * @param player  The player to check.
     */
    boolean isSelecting(Player player);

    /**
     * Wait for the player to click a block and run the handler.
     *
     * <p>No message is displayed to the player that they are being queried to
     * select a block.</p>
     *
     * @param player   The player.
     * @param handler  The handler to run when the player selects a block.
     */
    void query(Player player, IBlockSelectHandler handler);

    /**
     * Cancel a players block selection query.
     *
     * <p>No message is displayed to the player that the query has been cancelled.</p>
     *
     * @param player  The player to cancel.
     */
    void cancel(Player player);
}
