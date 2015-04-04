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

package com.jcwhatever.nucleus.utils.astar;

import javax.annotation.Nullable;

/**
 * Used to examine the environment and nodes for an A-Star search.
 *
 * @see AStar
 */
public interface IAStarExaminer {

    /**
     * Determine if a node can be considered the destination node in
     * its current search.
     *
     * @param node  The node to check.
     */
    boolean isDestination(AStarNode node);

    /**
     * Determine if conditions allow for a continued search for a path.
     *
     * @param context  The search context.
     */
    boolean canSearch(AStarContext context);

    /**
     * Determine if a node can be pathed to from an adjacent node.
     *
     * @param from  The node being path from.
     * @param to    The node being path to.
     */
    PathableResult isPathable(AStarNode from, AStarNode to);

    /**
     * Get score for a node with the specified parent.
     *
     * @param parent  The parent node.
     * @param node    The node to get a score for.
     */
    IAStarScore getScore(@Nullable AStarNode parent, AStarNode node);

    /**
     * Specifies the result of {@link #isPathable}.
     */
    enum PathableResult {
        /**
         * The node is not a pathable surface or there are obstructions
         * that disqualify it.
         */
        INVALID_POINT,
        /**
         * The entire Y axis column is not valid.
         */
        INVALID_COLUMN,
        /**
         * The node is a pathable surface.
         */
        VALID
    }
}
