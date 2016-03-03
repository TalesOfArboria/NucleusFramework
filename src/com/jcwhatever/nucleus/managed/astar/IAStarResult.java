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

package com.jcwhatever.nucleus.managed.astar;

import com.jcwhatever.nucleus.managed.astar.nodes.IAStarNode;

import java.util.List;

/**
 * The result of an AStar search.
 */
public interface IAStarResult<N extends IAStarNode<N>> {

    enum ResultStatus {
        /**
         * Search failed due to iterations being exceeded.
         */
        ITERATIONS_EXCEEDED,
        /**
         * Search failed due to range being exceeded.
         */
        RANGE_EXCEEDED,
        /**
         * Search failed to resolve a suitable path.
         */
        UNRESOLVABLE,
        /**
         * Successfully resolved a path.
         */
        RESOLVED
    }

    /**
     * Get the result status.
     */
    ResultStatus getStatus();

    /**
     * Determine if there is a node that can be pulled from the result.
     */
    boolean hasNext();

    /**
     * Remove and return the next node coordinate in the result.
     */
    N remove();

    /**
     * Get a direct reference to path node coordinates in the result.
     *
     * <p>Coordinates are in order from start to destination.</p>
     *
     * <p>Removing a coordinate using {@link #remove} also removes the coordinates
     * from the returned values.</p>
     */
    List<N> values();

    /**
     * Get the path distance as the number of nodes currently
     * contained in the result.
     *
     * <p>Removing a coordinate using {@link #remove} also decrements the
     * value returned.</p>
     *
     * @return  The number of nodes or -1 if a path could not be resolved.
     */
    int getPathDistance();
}
