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

import com.jcwhatever.nucleus.utils.Coords3Di;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Result object returned after an AStar search.
 *
 * @see AStar
 * @see AStar#search
 */
public class AStarResult {

    private final LinkedList<Coords3Di> _nodes = new LinkedList<>();
    private final AStarResultStatus _status;

    /**
     * Specifies the result status.
     */
    public enum AStarResultStatus {
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
     * Constructor.
     *
     * <p>Used for failed searches only.</p>
     *
     * @param status  The failed result status.
     */
    public AStarResult(AStarResultStatus status) {
        PreCon.notNull(status);
        PreCon.isValid(status != AStarResultStatus.RESOLVED, "Incorrect constructor for SUCCESS status.");

        _status = status;
    }

    /**
     * Constructor.
     *
     * @param context    The search context.
     * @param finalNode  The last node closed in the search, if any.
     */
    public AStarResult(AStarContext context,
                       @Nullable AStarNode finalNode) {
        PreCon.notNull(context);


        if (finalNode == null) {
            _status = AStarResultStatus.UNRESOLVABLE;
        }
        else {

            AStarNode current = finalNode;

            while (current != null) {

                Coords3Di coords = getCoords(current);

                _nodes.addFirst(coords);

                current = current.getParent();
            }

            if (_nodes.isEmpty()) {
                _status = AStarResultStatus.UNRESOLVABLE;
            }
            else {

                boolean hasStart = _nodes.peekFirst().equals(getCoords(context.getStart()));
                boolean hasEnd = context.getAstar().getExaminer().isDestination(finalNode);

                _status = hasStart && hasEnd
                        ? AStarResultStatus.RESOLVED
                        : AStarResultStatus.UNRESOLVABLE;

            }
        }
    }

    /**
     * Get the result status.
     */
    public AStarResultStatus getStatus() {
        return _status;
    }

    /**
     * Determine if there is a node that can be pulled from the result.
     */
    public boolean hasNext() {
        return !_nodes.isEmpty();
    }

    /**
     * Remove and return the next node coordinate in the result.
     */
    public Coords3Di remove() {
        return _nodes.removeFirst();
    }

    /**
     * Get a direct reference to path node coordinates in the result.
     *
     * <p>Coordinates are in order from start to destination.</p>
     *
     * <p>Removing a coordinate using {@link #remove} also removes the coordinates
     * from the returned values.</p>
     */
    public List<Coords3Di> values() {
        return _nodes;
    }

    /**
     * Get the path distance as the number of nodes currently
     * contained in the result.
     *
     * <p>Removing a coordinate using {@link #remove} also decrements the
     * value returned.</p>
     *
     * @return  The number of nodes or -1 if a path could not be resolved.
     */
    public int getPathDistance() {
        if (getStatus() != AStarResultStatus.RESOLVED)
            return -1;

        return _nodes.size();
    }

    /**
     * Invoked to get adjusted coordinate from a node.
     */
    protected Coords3Di getCoords(AStarNode node) {
        // add 1 to y coordinates to adjust for entity pathing.
        Coords3Di current = node.getCoords();
        return new Coords3Di(current, 0, 1, 0);
    }
}
